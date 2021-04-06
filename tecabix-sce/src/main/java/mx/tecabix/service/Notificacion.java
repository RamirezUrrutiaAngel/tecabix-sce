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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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

import org.springframework.beans.factory.annotation.Value;

import mx.tecabix.db.entity.Correo;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public class Notificacion extends Encrypt {
	
	@Value("${configuracion.email}")
	private String configuracionEmailFile;
	
	private static String SEED;
	
	private static final String LOGMS = "\nEMPRESA: :EMPRESA \n:PET : :PATH \n";
	
	private static final String GET = "GET";
	private static final String PUT = "PUT";
	private static final String POST = "POST";
	private static final String DELETE = "DELETE";
	
	protected static final String SMTP							= "smtp";
	protected static final String TRUE							= "true";
	protected static final String MAIL_SMTP_HOST				= "mail.smtp.host";
	protected static final String MAIL_SMTP_USER				= "mail.smtp.user";
	protected static final String MAIL_SMTP_PORT				= "mail.smtp.port";
	protected static final String MAIL_SMTP_AUTH				= "mail.smtp.auth";
	protected static final String MAIL_SMTP_CLAVE				= "mail.smtp.clave";
	protected static final String MAIL_SMTP_STARTTLS_ENABLE		= "mail.smtp.starttls.enable";
	
	@PostConstruct
	private void postConstruct() {
		if(SEED == null) {
			try {
				Properties properties = new Properties();
				FileReader fileReader;
				fileReader = new FileReader(getConfiguracionEmailFile());
				properties.load(fileReader);
				SEED = properties.getProperty("SEED");
				fileReader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String formatHeaderOfLogger(long idEmpresa, String peticion, String path) {
		return LOGMS.replaceFirst(":EMPRESA", String.valueOf(idEmpresa)).replaceFirst(":PET", peticion)
				.replaceFirst(":PATH", path);
	}
	protected String formatLogGet(long idEmpresa, String path){
		return formatHeaderOfLogger(idEmpresa, GET, path);
	}
	protected String formatLogPost(long idEmpresa, String path){
		return formatHeaderOfLogger(idEmpresa, POST, path);
	}
	protected String formatLogPut(long idEmpresa, String path){
		return formatHeaderOfLogger(idEmpresa, PUT, path);
	}
	protected String formatLogDelete(long idEmpresa, String path){
		return formatHeaderOfLogger(idEmpresa, DELETE, path);
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
		sendMailAttached(correo, msj, to, subject, auxCC, Arrays.asList(adjunto));
	}
	
	protected void sendMailAttached(Correo correo, String msj, String to, String subject, String cc , File adjunto) {
		sendMailAttached(correo, msj, to, subject, cc, adjunto);
	}
	
	protected void sendMailAttached(Correo correo, String msj, String to, String subject, List<String> cc , List<File> adjunto) {
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
	
	protected void sendMailAttached(Correo correo, String msj, String to, String subject, String[] cc , List<File> adjunto) {
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
			e.printStackTrace();
		}
	}

	protected final File getConfiguracionEmailFile() {
		File file = new File(configuracionEmailFile);
		return file.getAbsoluteFile();
	}

	protected String getSEED() {
		return Notificacion.SEED;
	}
	
}
