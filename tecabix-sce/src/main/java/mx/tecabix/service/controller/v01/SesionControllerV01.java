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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Licencia;
import mx.tecabix.db.entity.Servicio;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Suscripcion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.LicenciaService;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.SuscripcionService;
import mx.tecabix.db.service.UsuarioService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.page.SesionPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("sesion/v1")
public class SesionControllerV01 extends Auth {
	private static final Logger LOG = LoggerFactory.getLogger(SesionControllerV01.class);

	
	@Autowired
	private LicenciaService licenciaService;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired 
	private UsuarioService usuarioService;
	@Autowired
	private SesionService sesionService;
	@Autowired
	private SuscripcionService suscripcionService;
	
	private final String ACTIVO = "ACTIVO";
	private final String ELIMINADO = "ELIMINADO";
	private final String ESTATUS = "ESTATUS";
	private final String ROOT_SESION = "ROOT_SESION";
	private final String ROOT_SESION_ELIMINAR = "ROOT_SESION_ELIMINAR";
	
	private final String SESION_ELIMINAR = "SESION_ELIMINAR";

	private final String TIPO_DE_LICENCIA = "TIPO_DE_LICENCIA";
	private final String WEB = "WEB";
	
	@GetMapping("validateUsrPasw")
	public ResponseEntity<Boolean> validateUsrPasw() {
		return new ResponseEntity<Boolean>(true,HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Sesion> post(@RequestParam(value="key") String key){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuarioName = auth.getName();
		Licencia licencia = licenciaService.findByToken(key);
		if(licencia == null) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND); 
		}
		Usuario usuario = usuarioService.findByNombre(usuarioName);
		if(usuario == null) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Suscripcion> optionalSuscripcion = suscripcionService.findByIdEscuelaAndValid(licencia.getPlantel().getIdEscuela());
		if(!optionalSuscripcion.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.LOCKED);
		}
		
		Optional<Catalogo> optionalCatalogoTipoLicencia = catalogoService.findByTipoAndNombre(TIPO_DE_LICENCIA, WEB);
		if(!optionalCatalogoTipoLicencia.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo catalogoTipoLicenciaWeb = optionalCatalogoTipoLicencia.get();
		
		Optional<Catalogo> optionalCatalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		if(!optionalCatalogoEliminado.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo catalogoEliminado = optionalCatalogoEliminado.get();
		Integer peticionesRestantes = 0;
		LocalDateTime vencimiento = LocalDateTime.now();
		if(licencia.getTipo().getId().intValue() == catalogoTipoLicenciaWeb.getId().intValue()) {
			vencimiento = LocalDateTime.of(vencimiento.toLocalDate(),LocalTime.of(23, 59));
			
			List<Sesion> sesionesAviertas = sesionService.findByUsuarioAndActive(licencia.getId(), usuario.getId(),Integer.MAX_VALUE,0).getContent();
			if(sesionesAviertas != null) {
				LOG.info("SE ENCONTRARON "+sesionesAviertas.size()+" PARA LA KEY: "+ key);
				for (Sesion sesion : sesionesAviertas) {
					sesion.setFechaDeModificacion(LocalDateTime.now());
					sesion.setIdUsuarioModificado(sesion.getUsuario().getId());
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
			Page<Sesion> sesionesAviertas = sesionService.findByLicenciaAndActive(licencia.getId(), Integer.MAX_VALUE, 0);
			if(sesionesAviertas != null) {
				LOG.info("SE ENCONTRARON "+sesionesAviertas.getSize()+" PARA LA KEY: "+ key);
				for (Sesion sesion : sesionesAviertas) {
					sesion.setFechaDeModificacion(LocalDateTime.now());
					sesion.setIdUsuarioModificado(sesion.getUsuario().getId());
					sesion.setEstatus(catalogoEliminado);
					sesionService.update(sesion);
				}
			}
			Page<Sesion> sesionesDeHoy = sesionService.findByNow(licencia.getId(), Integer.MAX_VALUE, 0);
			if(sesionesDeHoy != null && !sesionesDeHoy.isEmpty()) {
				LOG.info("SE ENCONTRARON "+sesionesAviertas.getSize()+" SESIONES DE HOY PARA LA ID_LICENCIA: "+ licencia.getId());
				peticionesRestantes = sesionesDeHoy.getContent().get(0).getPeticionesRestantes();
				if(peticionesRestantes < 1) {
					return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);
				}
			}else {
				Servicio servicio = licencia.getServicio();
				peticionesRestantes = servicio.getPeticiones();
			}
			
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo catalogoActivo = optionalCatalogoActivo.get();
		
		Sesion sesion = new Sesion();
		sesion.setUsuario(usuario);
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

		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		sesion.setLicencia(null);
		return new ResponseEntity<Sesion>(sesion,HttpStatus.OK);
	}
	
	@DeleteMapping
	public ResponseEntity<Sesion> delete(@RequestParam(value="token") String token){
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		Optional<Catalogo> optionalCatalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		if(!optionalCatalogoEliminado.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Catalogo catalogoEliminado = optionalCatalogoEliminado.get();
		sesion.setFechaDeModificacion(LocalDateTime.now());
		sesion.setIdUsuarioModificado(sesion.getUsuario().getId());
		sesion.setEstatus(catalogoEliminado);
		sesion = sesionService.update(sesion);
		sesion.setLicencia(null);
		return new ResponseEntity<Sesion>(sesion,HttpStatus.OK);
	}
	
	@DeleteMapping("deleteByClave")
	public ResponseEntity<Sesion> deleteById(@RequestParam(value="token") String token, @RequestParam(value="clave") UUID uuid){
		Sesion sesion = getSessionIfIsAuthorized(token,SESION_ELIMINAR);
		if(sesion == null) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Catalogo> optionalCatalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		if(!optionalCatalogoEliminado.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Catalogo catalogoEliminado = optionalCatalogoEliminado.get();
		Optional<Sesion> sesionOptional = sesionService.findByClave(uuid);
		if(!sesionOptional.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		Sesion sesionAux = sesionOptional.get();
		if(!sesionAux.getLicencia().getPlantel().getIdEscuela().equals(sesion.getLicencia().getPlantel().getIdEscuela())) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		sesionAux.setFechaDeModificacion(LocalDateTime.now());
		sesionAux.setIdUsuarioModificado(sesion.getUsuario().getId());
		sesionAux.setEstatus(catalogoEliminado);
		sesionAux = sesionService.update(sesion);
		return new ResponseEntity<Sesion>(sesion,HttpStatus.NOT_FOUND);
	}
	
	@DeleteMapping("deleteRoot")
	public ResponseEntity<Sesion> deleteRoot(@RequestParam(value="token") String token, @RequestParam(value="clave") UUID uuid){
		Sesion sesion = getSessionIfIsAuthorized(token,ROOT_SESION_ELIMINAR);
		if(sesion == null) {
			return new ResponseEntity<Sesion>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Catalogo> optionalCatalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		if(!optionalCatalogoEliminado.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Catalogo catalogoEliminado = optionalCatalogoEliminado.get();
		
		Optional<Sesion> sesionOptional = sesionService.findByClave(uuid);
		if(!sesionOptional.isPresent()) {
			return new ResponseEntity<Sesion>(HttpStatus.NOT_FOUND);
		}
		Sesion sesionAux = sesionOptional.get();
		
		sesionAux.setFechaDeModificacion(LocalDateTime.now());
		sesionAux.setIdUsuarioModificado(sesion.getUsuario().getId());
		sesionAux.setEstatus(catalogoEliminado);
		sesionAux = sesionService.update(sesion);
		return new ResponseEntity<Sesion>(sesion,HttpStatus.NOT_FOUND);
	}
	
	
	@GetMapping("findAll")
	public ResponseEntity<SesionPage> findAll(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {

		Sesion sesion = getSessionIfIsAuthorized(token,ROOT_SESION);
		if(sesion == null) {
			return new ResponseEntity<SesionPage>(HttpStatus.NOT_FOUND);
		}
		Page<Sesion> response = sesionService.findAll(elements, page);
		SesionPage body = new SesionPage(response);
		return new ResponseEntity<SesionPage>(body,HttpStatus.OK);
	}
	
	@GetMapping("findByActive")
	public ResponseEntity<SesionPage> findByActive(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {

		Sesion sesion = getSessionIfIsAuthorized(token,ROOT_SESION);
		if(sesion == null) {
			return new ResponseEntity<SesionPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Sesion> response = sesionService.findByActive(sesion.getLicencia().getPlantel().getIdEscuela(), elements, page);
		SesionPage body = new SesionPage(response);
		return new ResponseEntity<SesionPage>(body,HttpStatus.OK);
	}
}
