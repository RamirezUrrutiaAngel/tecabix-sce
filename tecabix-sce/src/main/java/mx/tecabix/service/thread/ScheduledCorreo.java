/*
 *   This file is part of Foobar.
 *
 *   Foobar is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package mx.tecabix.service.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.entity.CorreoMsj;
import mx.tecabix.db.entity.CorreoMsjItem;
import mx.tecabix.db.service.CorreoMsjItemService;
import mx.tecabix.db.service.CorreoMsjService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Configuration
@EnableScheduling
public class ScheduledCorreo extends Auth{
	
	@Value("${configuracion.email}")
	private String configuracionEmailFile;
	
	private static final Logger LOG = LoggerFactory.getLogger(ScheduledCorreo.class);

	private static final String SMTP						= "smtp";
	private static final String TRUE						= "true";
	private static final String MAIL_SMTP_HOST				= "mail.smtp.host";
	private static final String MAIL_SMTP_USER				= "mail.smtp.user";
	private static final String MAIL_SMTP_PORT				= "mail.smtp.port";
	private static final String MAIL_SMTP_AUTH				= "mail.smtp.auth";
	private static final String MAIL_SMTP_CLAVE				= "mail.smtp.clave";
	private static final String MAIL_SMTP_STARTTLS_ENABLE	= "mail.smtp.starttls.enable";
	
	private static String SEED;
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private CorreoMsjService correoMsjService;
	@Autowired
	private CorreoMsjItemService correoMsjItemService;
	
	@PostConstruct
	private void postConstruct() {
		if(SEED == null) {
			try {
				Properties properties = new Properties();
				FileReader fileReader;
				fileReader = new FileReader(new File(configuracionEmailFile).getAbsoluteFile());
				properties.load(fileReader);
				SEED = properties.getProperty("SEED");
				fileReader.close();
			} catch (FileNotFoundException e) {
				LOG.error("se produjo un FileNotFoundException en el postConstruct de ScheduledCorreo");
				e.printStackTrace();
				
			} catch (IOException e) {
				LOG.error("se produjo un IOException en el postConstruct de ScheduledCorreo");
				e.printStackTrace();
			}
		}
	}
	
	@Scheduled(cron = "00 */5 * * * *")
	public void enviarCorreos() {
		LOG.info("Enviando correos");
		Sort sort = Sort.by(Sort.Direction.DESC, "programado");
		Page<CorreoMsj> correosMsj = correoMsjService.findLast(100, 0, sort);
		if(correosMsj.getTotalElements() > 0) {
			final Catalogo CAT_ELIMINADO = singletonUtil.getEliminado();
			correosMsj.stream().forEach(msj ->{
				msj.setEstatus(CAT_ELIMINADO);
				correoMsjService.update(msj);
				msj.getCorreoMsjItems().stream().forEach(item->{
					item.setEstatus(CAT_ELIMINADO);
					correoMsjItemService.update(item);
				});
			});
			correosMsj.stream().forEach(msj ->{
				StringBuilder text;
				File file = new File(msj.getMensaje());
				if(!file.exists()) {
					text = new StringBuilder(msj.getMensaje());
				}else {
					text = new StringBuilder(1800);
					FileReader fileReader = null;
					try {
						msj.getCorreo().setPassword(super.desencriptar(msj.getCorreo().getPassword(), SEED));
						fileReader = new FileReader(file);
						BufferedReader br = new BufferedReader(fileReader);
						
						List<CorreoMsjItem> items = msj.getCorreoMsjItems().stream()
								.filter(x -> !x.getTipo().getNombre().equals("CC")
										&& !x.getTipo().getNombre().equals("ADJUNTO"))
								.collect(Collectors.toList());
						String linea;
						while ((linea = br.readLine())!= null) {
							for(CorreoMsjItem item: items) {
								if(linea.contains(item.getTipo().getNombre())) {
									linea = linea.replace(item.getTipo().getNombre(), item.getDato());
								}
							}
							text.append(linea);
						}
						br.close();
					} catch (IOException e) {
						LOG.error("se produjo un IOException en el enviarLogs de ScheduledCorreo");
						e.printStackTrace();
					} catch (Exception e) {
						LOG.error("se produjo un Exception en el enviarLogs de ScheduledCorreo");
						e.printStackTrace();
					}finally {
						try {
							if(fileReader != null) {
								fileReader.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				List<String> cc = new LinkedList<>();
				msj.getCorreoMsjItems().stream().filter(x->x.getTipo().getNombre().equals("CC")).forEach(x->{
					cc.add(x.getDato());
				});
				List<File> adjuntos = new LinkedList<>();
				msj.getCorreoMsjItems().stream().filter(x->x.getTipo().getNombre().equals("ADJUNTO")).forEach(x->{
					adjuntos.add(new File(x.getDato()));
				});
				sendMailAttached(msj.getCorreo(), text.toString(), msj.getDestinatario(), msj.getAsunto(), cc, adjuntos);
				adjuntos.stream().forEach(x->x.delete());
			});
		}
	}
	
	private void sendMailAttached(Correo correo, String msj, String to, String subject, List<String> cc , List<File> adjunto) {
		try {
			Properties props = System.getProperties();
		    props.put(MAIL_SMTP_HOST, correo.getSmtpServidor());
		    props.put(MAIL_SMTP_USER, correo.getRemitente());
		    props.put(MAIL_SMTP_CLAVE,correo.getPassword());
		    props.put(MAIL_SMTP_AUTH, TRUE);
		    props.put(MAIL_SMTP_STARTTLS_ENABLE, TRUE);
		    props.put(MAIL_SMTP_PORT, correo.getSmtpPort()); 
		    Session session = Session.getDefaultInstance(props);
		    MimeMessage message = new MimeMessage(session);
		    MimeMultipart multiParte = new MimeMultipart();
	    	BodyPart texto = new MimeBodyPart();
	    	texto.setContent(msj,"text/html");
	        multiParte.addBodyPart(texto);
	        
	        if(adjunto != null) {
	        	for (int i = 0; i < adjunto.size(); i++) {
					File file = adjunto.get(i);
					if(file != null) {
						if(file.isFile() && file.canRead()) {
							BodyPart BodyPartAdjunto = new MimeBodyPart();
							BodyPartAdjunto.setDataHandler(new DataHandler(new FileDataSource(file)));
							BodyPartAdjunto.setFileName(file.getName());
					        multiParte.addBodyPart(BodyPartAdjunto);
						}
					}
				}
	        }
	        message.setFrom(new InternetAddress(correo.getRemitente()));
	        message.addRecipients(Message.RecipientType.TO, to);
	        
	        if(cc != null) {
	        	for (String copy : cc) {
	        		if(copy != null) {
	        			message.addRecipients(Message.RecipientType.CC, copy);
	        		}
				}
	        }
	        message.setSubject(subject);
	        message.setContent(multiParte);
	        Transport transport = session.getTransport(SMTP);
	        transport.connect(correo.getSmtpServidor(), correo.getRemitente(), correo.getPassword());
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
		    
		} catch (Exception e) {
			LOG.error("se produjo un Exception en el sendMailAttached de ScheduledCorreo");
			e.printStackTrace();
		}
	}
}
