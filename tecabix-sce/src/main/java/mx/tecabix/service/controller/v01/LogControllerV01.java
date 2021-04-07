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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.PostConstruct;
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

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Contacto;
import mx.tecabix.db.entity.Empresa;
import mx.tecabix.db.entity.Sesion;
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
	private static final String LOG_URL = "/log/v1";
	
	private String LOG_DIR			= null;
	
	@Autowired
	private EmpresaService empresaService;
	
	
	@PostConstruct
	private void postConstruct() {
		try {
			File file = this.getConfiguracionEmailFile();
			if( file != null) {
				if(file.exists()) {
					String LOG_DIR = "LOG_DIR";
					try {
						Properties properties = new Properties();
						FileReader fileReader = new FileReader(file);
						properties.load(fileReader);
						this.LOG_DIR		= properties.getProperty(LOG_DIR);
						
						fileReader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@ApiOperation(value = "Envio de log de excepciones o errores.")
	@PostMapping
	public ResponseEntity<?> post(@RequestParam(value="token") UUID token, @RequestParam(value="cuerpo") String cuerpo) {
		
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Optional<Empresa> empresaOptional = empresaService.findById(sesion.getLicencia().getPlantel().getIdEmpresa());
		if(!empresaOptional.isPresent()) {
			LOG.info("{}No se encuentra la empresa.",headerLog);
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
}

