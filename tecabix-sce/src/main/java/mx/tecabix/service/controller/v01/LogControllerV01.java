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
package mx.tecabix.service.controller.v01;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.annotation.PostConstruct;

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
import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.entity.Empresa;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CorreoService;
import mx.tecabix.db.service.EmpresaService;
import mx.tecabix.service.Auth;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("log/v1")
public final class LogControllerV01 extends Auth{
	
	@Autowired
	private EmpresaService empresaService;
	@Autowired
	private CorreoService correoService;
	
		
	
	private Correo correo			= null;
	private String SUBJECT			= null;
	private String TO				= null;
	private List<String> CC			= null;
	private String FILE_MESSAGE		= null;
	private String LOG_DIR			= null;
	
	
	@PostConstruct
	private void postConstruct() {
		try {
			if(this.getConfiguracionEmailFile() != null) {
				if(!this.getConfiguracionEmailFile().exists()) {
					String REMITENTE 		= "LOG_REMITENTE";
					String SUBJECT			= "LOG_SUBJECT";
					String TO				= "LOG_TO";
					String CC				= "LOG_CC";
					String FILE_MESSAGE		= "LOG_FILE_MESSAGE";
					String LOG_DIR			= "LOG_DIR";
					String SEED				= "SEED";
					
					try {
						Properties properties = new Properties();
						FileReader fileReader = new FileReader(this.getConfiguracionEmailFile());
						properties.load(fileReader);
						SEED				= properties.getProperty(SEED);
						REMITENTE			= properties.getProperty(REMITENTE);
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
					boolean isNull = REMITENTE == null;
					isNull = isNull || this.TO == null || this.SUBJECT == null;
					isNull = isNull || this.FILE_MESSAGE == null || this.LOG_DIR == null;
					if(!isNull) {
						Optional<Correo> optionalCorreo = correoService.findByRemitente(REMITENTE);
						if(optionalCorreo.isPresent()) {
							correo = optionalCorreo.get();
							String psw = correo.getPassword();
							psw = desencriptar(psw, SEED);
							correo.setPassword(psw);
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
			correo = null;
		}
	}
	
	@PostMapping
	public ResponseEntity<?> post(@RequestParam(value="token") UUID token, @RequestParam(value="cuerpo") String cuerpo) {
		
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<Empresa> empresaOptional = empresaService.findById(sesion.getLicencia().getPlantel().getIdEmpresa());
		if(!empresaOptional.isPresent()) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<Contacto> contactos = empresaOptional.get().getPresona().getContactos();
		StringBuilder mensaje = new StringBuilder(LocalDateTime.now()+"\n")
		.append("EMPRESARIAL:    ").append(empresaOptional.get().getNombre()).append("\n")
		.append("EMPRESARIAL ID: ").append(sesion.getLicencia().getPlantel().getIdEmpresa()).append("\n");
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
			File file = new File(LOG_DIR,sesion.getLicencia().getPlantel().getIdEmpresa().toString());
			
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
    		for (int i = 0; i < logs.length; i++) {
				File aux = new File(logs[i].getParent(),logs[i].getName()+".log");
				logs[i].renameTo(aux);
				logs[i].deleteOnExit();
				logs[i] = aux;			
			}
    		sendMailAttached(correo, cuerpo, TO, SUBJECT,CC,logs);
	        for (int i = 0; i < logs.length; i++) {
				logs[i].delete();
			}
	    }catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}

