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

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Contacto;
import mx.tecabix.db.entity.Escuela;
import mx.tecabix.db.entity.Sesion;
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
	
	@Autowired
	private EscuelaService escuelaService;
	
	private final String SMTP						= "smtp";
	private final String TRUE						= "true";
	private final String MAIL_SMTP_HOST				= "mail.smtp.host";
	private final String MAIL_SMTP_USER				= "mail.smtp.user";
	private final String MAIL_SMTP_PORT				= "mail.smtp.port";
	private final String MAIL_SMTP_AUTH				= "mail.smtp.auth";
	private final String MAIL_SMTP_CLAVE			= "mail.smtp.clave";
	private final String MAIL_SMTP_STARTTLS_ENABLE	= "mail.smtp.starttls.enable";
	
	private final String REMITENTE 					= "joyeria.el.diamate.azul@gmail.com";
	private final String PASSWORD					= "becVyb-secke9-sucnar";
	private final String SMTP_SERVIDOR				= "smtp.gmail.com";
	private final String SMTP_PORT					= "587";
	
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
		StringBuilder mensaje = new StringBuilder()
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
		
		.append(cuerpo);
		cuerpo = mensaje.toString();
		
		String destinatario = "info@tecabix.com";
		String asunto = "LOG " + optionalEscuela.get().getNombre();
		
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
	        message.setFrom(new InternetAddress(REMITENTE));
	        message.addRecipients(Message.RecipientType.TO, destinatario);
	        message.setSubject(asunto);
	        message.setText(cuerpo);
	        Transport transport = session.getTransport(SMTP);
	        transport.connect(SMTP_SERVIDOR, REMITENTE, PASSWORD);
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
	    }
	    catch (MessagingException me) {
	        me.printStackTrace();
	    }
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
