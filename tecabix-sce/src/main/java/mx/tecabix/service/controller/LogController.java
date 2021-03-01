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
package mx.tecabix.service.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Contacto;
import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.entity.Escuela;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CorreoService;
import mx.tecabix.db.service.EscuelaService;
import mx.tecabix.service.Auth;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("log")
public class LogController extends Auth{
	
	@Value("${configuracion.email}")
	private String configuracionEmailFile;
	
	@Autowired
	private EscuelaService escuelaService;
	@Autowired
	private CorreoService correoService;
	
	
	
	private final String SMTP						= "smtp";
	private final String TRUE						= "true";
	private final String MAIL_SMTP_HOST				= "mail.smtp.host";
	private final String MAIL_SMTP_USER				= "mail.smtp.user";
	private final String MAIL_SMTP_PORT				= "mail.smtp.port";
	private final String MAIL_SMTP_AUTH				= "mail.smtp.auth";
	private final String MAIL_SMTP_CLAVE			= "mail.smtp.clave";
	private final String MAIL_SMTP_STARTTLS_ENABLE	= "mail.smtp.starttls.enable";
	
	private String REMITENTE 		= null;
	private String PASSWORD			= null;
	private String SMTP_SERVIDOR	= null;
	private String SMTP_PORT		= null;
	private String SUBJECT			= null;
	private String TO				= null;
	private List<String> CC			= null;
	private String FILE_MESSAGE		= null;
	private String LOG_DIR			= null;
	
	
	@PostConstruct
	private void postConstruct() {
		try {
			if(configuracionEmailFile != null) {
				if(!configuracionEmailFile.isEmpty()) {
					String REMITENTE 		= "LOG_REMITENTE";
					String SUBJECT			= "LOG_SUBJECT";
					String TO				= "LOG_TO";
					String CC				= "LOG_CC";
					String FILE_MESSAGE		= "LOG_FILE_MESSAGE";
					String LOG_DIR			= "LOG_DIR";
					String SEED				= "SEED";
					
					try {
						Properties properties = new Properties();
						FileReader fileReader = new FileReader(configuracionEmailFile);
						properties.load(fileReader);
						SEED				= properties.getProperty(SEED);
						this.REMITENTE		= properties.getProperty(REMITENTE);
						this.SUBJECT		= properties.getProperty(SUBJECT);
						this.TO				= properties.getProperty(TO);
						this.FILE_MESSAGE	= properties.getProperty(FILE_MESSAGE);
						this.LOG_DIR		= properties.getProperty(LOG_DIR);
						String aux 			= properties.getProperty(CC);
						if(aux != null && !aux.trim().isEmpty()) {
							String[] array = aux.split(" ");
							this.CC = new ArrayList<String>();
							for (String correo : array) {
								if(!correo.isEmpty()) {
									this.CC.add(correo);
								}
							}
						}
						fileReader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					boolean isNull = this.REMITENTE == null;
					isNull = isNull || this.TO == null || this.SUBJECT == null;
					isNull = isNull || this.FILE_MESSAGE == null || this.LOG_DIR == null;
					if(!isNull) {
						Optional<Correo> optionalCorreo = correoService.findByRemitente(this.REMITENTE);
						if(optionalCorreo.isPresent()) {
							Correo correo = optionalCorreo.get();
							this.SMTP_SERVIDOR = correo.getSmtpServidor();
							this.SMTP_PORT = correo.getSmtpPort();
							this.PASSWORD = this.desencriptar(correo.getPassword(), SEED);
							isNull = this.SMTP_SERVIDOR.isEmpty();
							isNull = isNull || this.SMTP_PORT.isEmpty();
							isNull = isNull || this.SUBJECT.isEmpty();
							isNull = isNull || this.TO.isEmpty();
							isNull = isNull || this.FILE_MESSAGE.isEmpty();
							isNull = isNull || this.LOG_DIR.isEmpty();
							if(!isNull) {
								TimerTask timerTask = new TimerTask() {
									@Override
									public void run() {
										enviarLogs();
									}
								};
								int minutos = 60000;
								int hora = minutos * 60;
								Timer timer = new Timer();
								timer.schedule(timerTask, 0 * minutos, 3 * hora);
								
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PostMapping
	public ResponseEntity<?> post(@RequestParam(value="token") String token, @RequestParam(value="cuerpo") String cuerpo) {
		
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<Escuela> optionalEscuela = escuelaService.findById(sesion.getLicencia().getPlantel().getIdEscuela());
		if(!optionalEscuela.isPresent()) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<Contacto> contactos = optionalEscuela.get().getPresona().getContactos();
		StringBuilder mensaje = new StringBuilder(LocalDateTime.now()+"\n")
		.append("EMPRESARIAL:    ").append(optionalEscuela.get().getNombre()).append("\n")
		.append("EMPRESARIAL ID: ").append(sesion.getLicencia().getPlantel().getIdEscuela()).append("\n");
		if(contactos != null) {
			for (Contacto contacto : contactos) {
				if(contacto == null || contacto.getTipo() == null || contacto.getValor() == null) {
					continue;
				}
				mensaje.append(contacto.getTipo().getNombre())
				.append(": ").append(contacto.getValor())
				.append("\n");
			}
		}
		
		mensaje.append("\n")
		.append("USUARIO:    ").append(auth.getName()).append('\n')
		.append("NOMBRE:     ").append(sesion.getUsuario().getNombre()).append('\n')
		.append("CORREO:     ").append(sesion.getUsuario().getCorreo()).append("\n\n")
		.append("LICENCIA:   ").append(sesion.getLicencia().getNombre()).append("\n\n")
		.append("SESION ID:  ").append(sesion.getId()).append('\n')
		.append("USUARIO ID: ").append(sesion.getUsuario().getId()).append('\n')
		.append("LICENCIA ID:").append(sesion.getLicencia().getId()).append("\n\n\n")
		
		.append(cuerpo)
		.append("\n\n=====================================================================\n\n\n\n");
		cuerpo = mensaje.toString();
		
		try {
			File file = new File(LOG_DIR,sesion.getLicencia().getPlantel().getIdEscuela().toString());
			
			FileWriter fw = new FileWriter(file, true);
			fw.write(cuerpo);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	public void enviarLogs() {
		File[] logs = new File(LOG_DIR).listFiles();
		if(logs == null || logs.length == 0) {
			return;
		}
		for (int i = 0; i < logs.length; i++) {
			File file = logs[i];
			if(file.getName().startsWith(".")) {
				file.delete();
			}
		}
		logs = new File(LOG_DIR).listFiles();
		if(logs == null || logs.length == 0) {
			return;
		}
		String cuerpo = new String();
		
	    Properties props = System.getProperties();
	    props.put(MAIL_SMTP_HOST, SMTP_SERVIDOR);
	    props.put(MAIL_SMTP_USER, REMITENTE);
	    props.put(MAIL_SMTP_CLAVE, PASSWORD);
	    props.put(MAIL_SMTP_AUTH, TRUE);
	    props.put(MAIL_SMTP_STARTTLS_ENABLE, TRUE);
	    props.put(MAIL_SMTP_PORT, SMTP_PORT); 
	    
	    Session session = Session.getDefaultInstance(props);
	    MimeMessage message = new MimeMessage(session);
	    
	    try {
	    	File fileMsg = new File(FILE_MESSAGE);
	    	if(fileMsg.exists() && fileMsg.canRead()) {
	    		FileReader fr = new FileReader(fileMsg);
	    		BufferedReader br = new BufferedReader(fr);
	    		StringBuilder sb = new StringBuilder();
	    		String linea;
	    		while((linea = br.readLine())!=null) {
	    			sb.append(linea);
	    		}
	    		br.close();
	    		fr.close();
	    		sb.append("\n\n");
	    		cuerpo = sb.toString();
	    	}
	    	MimeMultipart multiParte = new MimeMultipart();
	    	BodyPart texto = new MimeBodyPart();
	    	texto.setContent(cuerpo,"text/html");
	        multiParte.addBodyPart(texto);
	        
    		for (int i = 0; i < logs.length; i++) {
				File aux = new File(logs[i].getParent(),logs[i].getName()+".log");
				logs[i].renameTo(aux);
				logs[i].deleteOnExit();
				logs[i] = aux;
				BodyPart adjunto = new MimeBodyPart();
		        adjunto.setDataHandler(new DataHandler(new FileDataSource(logs[i])));
		        adjunto.setFileName(logs[i].getName());
		        multiParte.addBodyPart(adjunto);					
			}
    		
	        message.setFrom(new InternetAddress(REMITENTE));
	        message.addRecipients(Message.RecipientType.TO, TO);
	        if(CC != null) {
	        	for (String correo : CC) {
	        		message.addRecipients(Message.RecipientType.CC, correo);
				}
	        }
	        
	        message.setSubject(SUBJECT);
	        message.setContent(multiParte);
	        Transport transport = session.getTransport(SMTP);
	        transport.connect(SMTP_SERVIDOR, REMITENTE, PASSWORD);
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
	        for (int i = 0; i < logs.length; i++) {
				logs[i].delete();
			}
	    }
	    catch (MessagingException me) {
	        me.printStackTrace();
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}

