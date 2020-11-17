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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.Auth;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Licencia;
import mx.tecabix.db.entity.Servicio;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.LicenciaService;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.UsuarioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("sesion")
public class SesionController {
	private static final Logger LOG = LoggerFactory.getLogger(SesionController.class);

	
	@Autowired
	private LicenciaService licenciaService;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired 
	private UsuarioService usuarioService;
	@Autowired
	private SesionService sesionService;
	
	private final String ACTIVO = "ACTIVO";
	private final String ELIMINADO = "ELIMINADO";
	private final String ESTATUS = "ESTATUS";
	private final String SESION = "SESION";
	
	private final String TIPO_DE_LICENCIA = "TIPO_DE_LICENCIA";
	private final String WEB = "WEB";
	
	@GetMapping("validateUsrPasw")
	public ResponseEntity<Object> validateUsrPasw() {
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Sesion> post(@RequestParam(value="key") String key){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, SESION)) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED); 
		}
		String usuarioName = auth.getName();
		Licencia licencia = licenciaService.findByToken(key);
		if(licencia == null) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED); 
		}
		Usuario usuario = usuarioService.findByNombre(usuarioName);
		if(usuario == null) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);
		}
		final Catalogo catalogoTipoLicenciaWeb = catalogoService.findByTipoAndNombre(TIPO_DE_LICENCIA, WEB);
		final Catalogo catalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		Integer peticionesRestantes = 0;
		LocalDateTime vencimiento = LocalDateTime.now();
		if(licencia.getTipo().getId().intValue() == catalogoTipoLicenciaWeb.getId().intValue()) {
			vencimiento = LocalDateTime.of(vencimiento.toLocalDate(),LocalTime.of(23, 59));
			
			List<Sesion> sesionesAviertas = sesionService.findByUsuarioAndActive(licencia.getId(), usuario.getId(),Integer.MAX_VALUE,0).getContent();
			if(sesionesAviertas != null) {
				LOG.info("SE ENCONTRARON "+sesionesAviertas.size()+" PARA LA KEY: "+ key);
				for (Sesion sesion : sesionesAviertas) {
					sesion.setEstatus(catalogoEliminado);
					sesionService.update(sesion);
				}
			}
			List<Sesion> sesionesDeHoy = sesionService.findByUsuarioAndNow(licencia.getId(), usuario.getId(), Integer.MAX_VALUE, 0).getContent();
			if(sesionesDeHoy != null && !sesionesDeHoy.isEmpty()) {
				LOG.info("SE ENCONTRARON "+sesionesAviertas.size()+" SESIONES DE HOY PARA LA ID_LICENCIA: "+ licencia.getId());
				peticionesRestantes = sesionesDeHoy.get(0).getPeticionesRestantes();
				if(peticionesRestantes < 1) {
					return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);
				}
			}else {
				Servicio servicio = licencia.getServicio();
				peticionesRestantes = servicio.getPeticiones();
			}
		}else {
			vencimiento = vencimiento.plusHours(8);
			List<Sesion> sesionesAviertas = sesionService.findByActive(licencia.getId(),Integer.MAX_VALUE,0).getContent();
			if(sesionesAviertas != null) {
				LOG.info("SE ENCONTRARON "+sesionesAviertas.size()+" PARA LA KEY: "+ key);
				for (Sesion sesion : sesionesAviertas) {
					sesion.setEstatus(catalogoEliminado);
					sesionService.update(sesion);
				}
			}
			List<Sesion> sesionesDeHoy = sesionService.findByNow(licencia.getId(), Integer.MAX_VALUE, 0).getContent();
			if(sesionesDeHoy != null && !sesionesDeHoy.isEmpty()) {
				LOG.info("SE ENCONTRARON "+sesionesAviertas.size()+" SESIONES DE HOY PARA LA ID_LICENCIA: "+ licencia.getId());
				peticionesRestantes = sesionesDeHoy.get(0).getPeticionesRestantes();
				if(peticionesRestantes < 1) {
					return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);
				}
			}else {
				Servicio servicio = licencia.getServicio();
				peticionesRestantes = servicio.getPeticiones();
			}
			
		}
		final Catalogo catalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		
		Sesion sesion = new Sesion();
		sesion.setEstatus(catalogoActivo);
		sesion.setFechaDeModificacion(LocalDateTime.now());
		sesion.setIdUsuarioModificado(usuario.getId());
		sesion.setLicencia(licencia);
		sesion.setVencimiento(vencimiento);
		sesion.setPeticionesRestantes(peticionesRestantes);
		sesion = sesionService.save(sesion);
		sesion.setLicencia(null);
		return new ResponseEntity<Sesion>(sesion, HttpStatus.OK);
	}
	
	
	@GetMapping
	public ResponseEntity<Sesion> get(@RequestParam(value="token") String token){
		LOG.info("token: "+token);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, SESION)) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED); 
		}
		String usuarioName = auth.getName();
		Usuario usuario = usuarioService.findByNombre(usuarioName);
		if(usuario == null) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);		
		}
		Sesion sesion = sesionService.findByToken(token);
		if(sesion == null) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		if(sesion.getIdUsuarioModificado().longValue() != usuario.getId().longValue()) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		sesion.setLicencia(null);
		return new ResponseEntity<Sesion>(sesion,HttpStatus.OK);
	}
	
	@DeleteMapping
	public ResponseEntity<Sesion> delete(@RequestParam(value="token") String token){
		LOG.info("token: "+token);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, SESION)) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED); 
		}
		String usuarioName = auth.getName();
		Usuario usuario = usuarioService.findByNombre(usuarioName);
		if(usuario == null) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);		
		}
		Sesion sesion = sesionService.findByToken(token);
		if(sesion == null) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		if(sesion.getIdUsuarioModificado().longValue() != usuario.getId().longValue()) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		Catalogo catalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		sesion.setEstatus(catalogoEliminado);
		sesion = sesionService.save(sesion);
		sesion.setLicencia(null);
		return new ResponseEntity<Sesion>(sesion,HttpStatus.NOT_FOUND);
	}
}