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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOG = LoggerFactory.getLogger(LogControllerV01.class);
	
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
	private boolean enviar			= false;
	
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
						if (aux != null && !aux.trim().isEmpty()) {
							this.CC = Arrays.asList(aux.split(" "))
									.stream().filter(x -> !x.isBlank())
									.collect(Collectors.toList());
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
							enviar = !isNull;
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
			contactos.stream().filter(x->x.getTipo() != null && x.getValor()!=null && x.getValor() != null).forEach(x->{
				mensaje.append(x.getTipo().getNombre()).append(":").append(x.getValor()).append("\n");
			});
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
	
	@Scheduled(cron = "00 */3 * * *")
	public void enviarLogs() {
		StringBuilder log = new StringBuilder();
		log.append(((enviar)?"El envio de logs esta activo.\n":"El envio de logs esta desactivo.\n"));
		if(enviar) {
			List<File> logs = Arrays.asList(new File(LOG_DIR).listFiles());
			if(logs == null || logs.isEmpty()) {
				log.append("No se encontro log que enviar.\n");
				return;
			}
			logs.stream().filter(file->file.getName().startsWith(".")).forEach(file->file.delete());
			logs = Arrays.asList(new File(LOG_DIR).listFiles());
			if(logs == null || logs.isEmpty()) {
				log.append("No se encontro log que enviar.\n");
				return;
			}
			log.append("Se encontraron ").append(logs.size()).append(" archivos\n");
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
		    	logs = logs.stream().map(file ->{
		    		File aux = new File(file.getParent(),file.getName().concat(".log"));
		    		file.renameTo(aux);
		    		file.deleteOnExit();
		    		return aux;
		    	}).collect(Collectors.toList());
	    		sendMailAttached(correo, cuerpo, TO, SUBJECT,CC,logs);
	    		logs.stream().forEach(x->x.delete());
		        log.append("Se enviaron y borraron los logs\n");
		    }catch (FileNotFoundException e) {
				e.printStackTrace();
				log.append("Se produjo un FileNotFoundException\n");
			} catch (IOException e) {
				log.append("Se produjo un IOException\n");
				e.printStackTrace();
			} 
		    LOG.info(log.toString());
		}
	}
}

