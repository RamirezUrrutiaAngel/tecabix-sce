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
package mx.tecabix.service;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.service.CorreoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public class Notificacion extends Encrypt {

	@Value("${configuracion.email}")
	private String configuracionEmailFile;
	

	@Autowired
	private CorreoService correoService;
	
	protected static final String SMTP							= "smtp";
	protected static final String TRUE							= "true";
	protected static final String MAIL_SMTP_HOST				= "mail.smtp.host";
	protected static final String MAIL_SMTP_USER				= "mail.smtp.user";
	protected static final String MAIL_SMTP_PORT				= "mail.smtp.port";
	protected static final String MAIL_SMTP_AUTH				= "mail.smtp.auth";
	protected static final String MAIL_SMTP_CLAVE				= "mail.smtp.clave";
	protected static final String MAIL_SMTP_STARTTLS_ENABLE	= "mail.smtp.starttls.enable";
	
	
	private static Correo correoInfo = null;
	private String INFO_SUBJECT = null;
	private String INFO_TO = null;
	private List<String> INFO_CC = null;
	private String INF_FILE_MESSAGE = null;
	
	private static Correo correoConfirmacion = null;
	private String CONF_SUBJECT = null;
	private String CONF_TO = null;
	private List<String> CONF_CC = null;
	private String CONF_FILE_MESSAGE = null;
	
	@PostConstruct
	private void postConstruct() {
		if(correoInfo == null) {
			initCorreoINF();
		}
		if(correoConfirmacion == null) {
			initCorreoCON();
		}
	}
	
	private void initCorreoINF() {
		try {
			String REMITENTE 		= "INF_REMITENTE";
			String SUBJECT			= "INF_SUBJECT";
			String TO				= "INF_TO";
			String CC				= "INF_CC";
			String FILE_MESSAGE		= "INF_FILE_MESSAGE";
			String SEED				= "SEED";
			Properties properties = new Properties();
			FileReader fileReader = new FileReader(getConfiguracionEmailFile());
			properties.load(fileReader);
			SEED				= properties.getProperty(SEED);
			REMITENTE			= properties.getProperty(REMITENTE);
			this.INFO_SUBJECT	= properties.getProperty(SUBJECT);
			this.INFO_TO		= properties.getProperty(TO);
			this.INFO_SUBJECT	= properties.getProperty(FILE_MESSAGE);
			String aux 			= properties.getProperty(CC);
			if(aux != null && !aux.trim().isEmpty()) {
				String[] array = aux.split(" ");
				this.INFO_CC = new ArrayList<String>();
				for (String correo : array) {
					if(!correo.isEmpty()) {
						this.INFO_CC.add(correo);
					}
				}
			}
			fileReader.close();
			boolean isNull = REMITENTE == null;
			isNull = isNull || this.INFO_TO == null || this.INFO_SUBJECT == null;
			isNull = isNull || this.INF_FILE_MESSAGE == null;
			if(!isNull) {
				isNull = this.INFO_SUBJECT.isEmpty();
				isNull = isNull || this.INFO_TO.isEmpty();
				isNull = isNull || this.INF_FILE_MESSAGE.isEmpty();
				if(!isNull) {
					Optional<Correo> optionalCorreo = correoService.findByRemitente(REMITENTE);
					if(optionalCorreo.isPresent()) {
						correoInfo = optionalCorreo.get();
						String psw = correoInfo.getPassword();
						psw = desencriptar(psw, SEED);
						correoInfo.setPassword(psw);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			correoInfo = null;
			INFO_SUBJECT = null;
			INFO_TO = null;
			INFO_CC = null;
			INF_FILE_MESSAGE = null;
		}
	}


	private void initCorreoCON() {
		try {
			String REMITENTE 		= "CON_REMITENTE";
			String SUBJECT			= "CON_SUBJECT";
			String TO				= "CON_TO";
			String CC				= "CON_CC";
			String FILE_MESSAGE		= "CON_FILE_MESSAGE";
			String SEED				= "SEED";
			Properties properties = new Properties();
			FileReader fileReader = new FileReader(this.getConfiguracionEmailFile());
			properties.load(fileReader);
			SEED				= properties.getProperty(SEED);
			REMITENTE			= properties.getProperty(REMITENTE);
			this.CONF_SUBJECT	= properties.getProperty(SUBJECT);
			this.CONF_TO		= properties.getProperty(TO);
			this.CONF_SUBJECT	= properties.getProperty(FILE_MESSAGE);
			String aux 			= properties.getProperty(CC);
			if(aux != null && !aux.trim().isEmpty()) {
				String[] array = aux.split(" ");
				this.CONF_CC = new ArrayList<String>();
				for (String correo : array) {
					if(!correo.isEmpty()) {
						this.CONF_CC.add(correo);
					}
				}
			}
			fileReader.close();
			boolean isNull = REMITENTE == null;
			isNull = isNull || this.CONF_TO == null || this.CONF_SUBJECT == null;
			isNull = isNull || this.CONF_FILE_MESSAGE == null;
			if(!isNull) {
				isNull = this.CONF_SUBJECT.isEmpty();
				isNull = isNull || this.CONF_TO.isEmpty();
				isNull = isNull || this.CONF_FILE_MESSAGE.isEmpty();
				if(!isNull) {
					Optional<Correo> optionalCorreo = correoService.findByRemitente(REMITENTE);
					if(optionalCorreo.isPresent()) {
						correoInfo = optionalCorreo.get();
						String psw = correoInfo.getPassword();
						psw = desencriptar(psw, SEED);
						correoInfo.setPassword(psw);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			correoConfirmacion = null;
			CONF_SUBJECT = null;
			CONF_TO = null;
			CONF_CC = null;
			CONF_FILE_MESSAGE = null;
		}
	}
	
	protected void sendMail(Correo correo, String msj, String to, String subject, List<String> cc) {
		String[] auxCC = null;
		int size = 0;
		if(cc != null) {
			size = cc.size();
		}
		auxCC = new String[size];
		sendMailAttached(correo, msj, to, subject, auxCC, null);
	}
	
	protected void sendMail(Correo correo, String msj, String to, String subject, String... cc) {
		sendMailAttached(correo, msj, to, subject, cc, null);
	}
	
	protected void sendMailAttached(Correo correo, String msj, String to, String subject, File... adjunto) {
		String[] auxCC = null;
		sendMailAttached(correo, msj, to, subject, auxCC, adjunto);
	}
	
	protected void sendMailAttached(Correo correo, String msj, String to, String subject, String cc , File adjunto) {
		sendMailAttached(correo, msj, to, subject, cc, adjunto);
	}
	
	protected void sendMailAttached(Correo correo, String msj, String to, String subject, List<String> cc , File[] adjunto) {
		String[] auxCC = null;
		int size = 0;
		if(cc != null) {
			size = cc.size();
		}
		auxCC = new String[size];
		if(cc != null) {
			for(int i = 0 ; i < cc.size(); i++) {
				String item = cc.get(i);
				auxCC[i] = item;
			}
		}
		sendMailAttached(correo, msj, to, subject, auxCC, adjunto);
	}
	
	protected void sendMailAttached(Correo correo, String msj, String to, String subject, String[] cc , File[] adjunto) {
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
	        	for (int i = 0; i < adjunto.length; i++) {
					File file = adjunto[i];
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
			e.printStackTrace();
		}
	}

	protected final File getConfiguracionEmailFile() {
		File file = new File(this.configuracionEmailFile);
		return file.getAbsoluteFile();
	}
	
}
