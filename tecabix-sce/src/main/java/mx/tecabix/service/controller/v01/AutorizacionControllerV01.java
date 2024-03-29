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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Autorizacion;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.AutorizacionService;
import mx.tecabix.db.service.PerfilAutorizacionService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.AutorizacionPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("autorizacion/v1")
public final class AutorizacionControllerV01 extends Auth{
	private static final Logger LOG = LoggerFactory.getLogger(AutorizacionControllerV01.class);
	private static final String LOG_URL = "/autorizacion/v1";
	
	private static final String AUTORIZACION = "AUTORIZACION";
	private static final String PERFIL = "PERFIL";
	private static final String AUTENTIFICADOS = "AUTENTIFICADOS";
	
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private AutorizacionService autorizacionService;
	@Autowired
	private PerfilAutorizacionService perfilAutorizacionService;
	
	
	@ApiOperation(value = "Persiste la entidad de la autorizacion con sus correspondientes sub autorizacion. ")
	@PostMapping()
	public ResponseEntity<Autorizacion> save(@RequestParam(value="token") UUID token, @RequestBody Autorizacion autorizacion){
		
		Sesion sesion = getSessionIfIsAuthorized(token, AUTORIZACION);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Autorizacion>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		if(isNotValid(TIPO_VARIABLE, Autorizacion.SIZE_NOMBRE, autorizacion.getNombre())){
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Autorizacion.SIZE_DESCRIPCION, autorizacion.getDescripcion())) {
			LOG.info("{}El formato de la descripcioon es incorrecto.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
		}else {
			autorizacion.setDescripcion(autorizacion.getDescripcion().strip());
		}
		List<Autorizacion> subAutorizaciones = autorizacion.getSubAutorizacion();
		if(subAutorizaciones != null) {
			for (int i = 0; i < subAutorizaciones.size(); i++) {
				Autorizacion aux = subAutorizaciones.get(i);
				if(aux == null) {
					return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_VARIABLE, Autorizacion.SIZE_NOMBRE, aux.getNombre())){
					LOG.info("{}El formato del nombre  de un sub autorizacion es incorrecto.",headerLog);
					return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Autorizacion.SIZE_DESCRIPCION, aux.getDescripcion())) {
					LOG.info("{}El formato de la descripcion de un sub autorizacion es incorrecto.",headerLog);
					return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
				}else {
					aux.setDescripcion(aux.getDescripcion().strip());
				}
				if(autorizacion.getNombre().equalsIgnoreCase(aux.getNombre())) {
					LOG.info("{}Hay nombres repetidos.",headerLog);
					return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
				}
			}
			for (int i = 0; i < subAutorizaciones.size() - 1; i++) {
				Autorizacion auxA = subAutorizaciones.get(i);
				for (int j = i + 1; j < subAutorizaciones.size(); j++) {
					Autorizacion auxB = subAutorizaciones.get(j);
					if(auxA.getNombre().equalsIgnoreCase(auxB.getNombre())) {
						LOG.info("{}Hay nombres repetidos.",headerLog);
						return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
					}
				}
			}
		}
		Optional<Autorizacion> authorities = autorizacionService.findByNombre(autorizacion.getNombre());
		
		if(authorities.isPresent()) {
			return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
		}
		if(subAutorizaciones != null) {
			for (int i = 0; i < subAutorizaciones.size(); i++) {
				Autorizacion aux = subAutorizaciones.get(i);
				Optional<Autorizacion> authoritiesAux = autorizacionService.findByNombre(aux.getNombre());
				if(authoritiesAux.isPresent()) {
					LOG.info("{}Uno de los nombre ya existe.",headerLog);
					return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
				}
			}
		}
		Optional<Autorizacion> autorizacionPadreOptional = autorizacionService.findByNombre(AUTENTIFICADOS);
		if(autorizacionPadreOptional.isEmpty()) {
			LOG.info("{}No se encontro el autority AUTENTIFICADO.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Catalogo ACTIVO = singletonUtil.getActivo();
		Autorizacion autorizacionPadre = autorizacionPadreOptional.get(); 
		autorizacion.setId(null);
		autorizacion.setPerfiles(null);
		autorizacion.setPreAutorizacion(autorizacionPadre);
		autorizacion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		autorizacion.setFechaDeModificacion(LocalDateTime.now());
		autorizacion.setEstatus(ACTIVO);
		autorizacion.setClave(UUID.randomUUID());
		autorizacion = autorizacionService.save(autorizacion);
		if(subAutorizaciones != null) {
			final Autorizacion aux = autorizacion;
			subAutorizaciones.stream().forEach(x ->{
				x.setId(null);
				x.setPerfiles(null);
				x.setSubAutorizacion(null);
				x.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
				x.setFechaDeModificacion(LocalDateTime.now());
				x.setEstatus(ACTIVO);
				x.setClave(UUID.randomUUID());
				x.setPreAutorizacion(aux);
				autorizacionService.save(x);
			});
		}
		Optional<Autorizacion>autorizacionOptional = autorizacionService.findById(autorizacion.getId());
		if(autorizacionOptional.isEmpty()) {
			LOG.info("{}No pudo recuperar el id del autorizacion guardado.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		autorizacion = autorizacionOptional.get();
		return new ResponseEntity<Autorizacion>(autorizacion,HttpStatus.OK);
	}
	/**
	 * 
	 * @param by:		NOMBRE, DESCRIPCION
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los autorizacion paginado.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION<br/><b>order:</b> ASC, DESC")
	@GetMapping
	public ResponseEntity<AutorizacionPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		if(isNotAuthorized(token, AUTORIZACION)) {
			return new ResponseEntity<AutorizacionPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Autorizacion> authorities = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<AutorizacionPage>(HttpStatus.BAD_REQUEST);
		}
		if(search == null || search.isEmpty()) {
			authorities = autorizacionService.findAll(elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				authorities = autorizacionService.findByLikeNombre(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DESCRIPCION")) {
				authorities = autorizacionService.findByLikeDescripcion(text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<AutorizacionPage>(HttpStatus.BAD_REQUEST);
			}
		}
		for (Autorizacion autorizacion : authorities) {
			autorizacion.setPreAutorizacion(null);
			autorizacion.setSubAutorizacion(null);
		}
		AutorizacionPage body = new AutorizacionPage(authorities); 
		ResponseEntity<AutorizacionPage> response = new ResponseEntity<AutorizacionPage>(body, HttpStatus.OK);
		return response;
	}
	
	@ApiOperation(value = "Obtiene la autorizacion con el ID proporcionado. ")
	@GetMapping("findByClave")
	public ResponseEntity<Autorizacion> findByClave(@RequestParam(value="token") UUID token, @RequestParam(value = "clave") UUID clave){
		
		if(isNotAuthorized(token, AUTORIZACION)) {
			return new ResponseEntity<Autorizacion>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Autorizacion> result = autorizacionService.findByClave(clave);
		if(result.isEmpty()) {
			return new ResponseEntity<Autorizacion>(HttpStatus.NOT_FOUND);
		}
		
		Autorizacion body = result.get();
		return new ResponseEntity<Autorizacion>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Obtiene las autorizaciones autentificados.")
	@GetMapping("findAutentificados")
	public ResponseEntity<Autorizacion> findAutentificados(@RequestParam(value="token") UUID token){
		
		if(isNotAuthorized(token, PERFIL)) {
			return new ResponseEntity<Autorizacion>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Autorizacion> result = autorizacionService.findByNombre(AUTENTIFICADOS);
		if(result.isEmpty()) {
			return new ResponseEntity<Autorizacion>(HttpStatus.NOT_FOUND);
		}
		Autorizacion body = result.get();
		return new ResponseEntity<Autorizacion>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del autorizacion con sus correspondientes sub autorizacion" ,
			notes  = "La clave del autorizacion principal es obligatorio, en el sub autorizacion al proporcionar"
			+ " la clave se indica que se va actualizar dicho autorizacion, "
			+ "si no se le proporciona se le considera un nuevo sub autorizacion.\n"
			+ "Los autorizacion ya guardado que no se especifiquen en la petición serán eliminados.")
	@PutMapping()
	public ResponseEntity<Autorizacion> update(@RequestParam(value="token") UUID token, @RequestBody Autorizacion autorizacion){
		Sesion sesion = getSessionIfIsAuthorized(token, AUTORIZACION);
		if(sesion == null) {
			return new ResponseEntity<Autorizacion>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPut(idEmpresa, LOG_URL);
		if(autorizacion.getClave() == null) {
			LOG.info("{}No se mando la clave.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_VARIABLE, Autorizacion.SIZE_NOMBRE, autorizacion.getNombre())) {
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Autorizacion.SIZE_DESCRIPCION, autorizacion.getDescripcion())) {
			LOG.info("{}El formato de la descripcion es incorrecto.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
		}else {
			autorizacion.setDescripcion(autorizacion.getDescripcion().strip());
		}
		Optional<Autorizacion> optionalautorizacionViejo =  autorizacionService.findByClave(autorizacion.getClave());
		if(optionalautorizacionViejo.isEmpty()) {
			LOG.info("{}No se encontro el autorizacion con la clave {}.",headerLog, autorizacion.getClave());
			return new ResponseEntity<Autorizacion>(HttpStatus.NOT_FOUND);
		}
		Catalogo ACTIVO = singletonUtil.getActivo();
		Autorizacion autorizacionViejo = optionalautorizacionViejo.get();
		if(!autorizacionViejo.getEstatus().equals(ACTIVO)) {
			LOG.info("{}El autorizacion no esta activo.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.NOT_FOUND);
		}
		autorizacion.setId(autorizacionViejo.getId());
		autorizacion.setPreAutorizacion(autorizacionViejo.getPreAutorizacion());
		autorizacion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		autorizacion.setFechaDeModificacion(LocalDateTime.now());
		autorizacion.setEstatus(autorizacionViejo.getEstatus());
		
		Optional<Autorizacion> autorizacionPadreOptional = autorizacionService.findByNombre(AUTENTIFICADOS);
		if(autorizacionPadreOptional.isEmpty()) {
			LOG.info("{}No se encontro el autorizacion AUTENTIFICADOS.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Autorizacion autorizacionPadre = autorizacionPadreOptional.get(); 
		if(!autorizacionPadre.equals(autorizacionViejo.getPreAutorizacion())) {
			LOG.info("{}El auhority que se esta intentando modificar no ereda de AUTENTIFICADO.",headerLog);
			return new ResponseEntity<Autorizacion>(HttpStatus.UNAUTHORIZED);
		}
		
		List<Autorizacion> listaDeSubautorizacionsActualizados = autorizacion.getSubAutorizacion();
		if(listaDeSubautorizacionsActualizados != null) {
			for (int i = 0; i < listaDeSubautorizacionsActualizados.size(); i++) {
				Autorizacion aux = listaDeSubautorizacionsActualizados.get(i);
				if(aux == null) {
					LOG.info("{}Un sub autorizacion es nulo.",headerLog);
					return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_VARIABLE, Autorizacion.SIZE_NOMBRE, aux.getNombre())){
					LOG.info("{}El formato del nombre de un sub autorizacion es incorrecto.",headerLog);
					return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Autorizacion.SIZE_DESCRIPCION, aux.getDescripcion())) {
					LOG.info("{}El formato de la descripcion de un sub autorizacion es incorrecto.",headerLog);
					return new ResponseEntity<Autorizacion>(HttpStatus.BAD_REQUEST);
				}else {
					aux.setDescripcion(aux.getDescripcion().strip());
				}
				if(autorizacion.getNombre().equalsIgnoreCase(aux.getNombre())) {
					LOG.info("{}El nombre de un sub autorizacion esta repetido.",headerLog);
					return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
				}
			}
			for (int i = 0; i < listaDeSubautorizacionsActualizados.size() - 1; i++) {
				Autorizacion auxA = listaDeSubautorizacionsActualizados.get(i);
				for (int j = i + 1; j < listaDeSubautorizacionsActualizados.size(); j++) {
					Autorizacion auxB = listaDeSubautorizacionsActualizados.get(j);
					if(auxA.getNombre().equalsIgnoreCase(auxB.getNombre())) {
						LOG.info("{}El nombre de un sub autorizacion esta repetido.",headerLog);
						return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
					}
				}
			}
		}
		
		if( !autorizacion.getNombre().equals(autorizacionViejo.getNombre())) {
			Optional<Autorizacion> optionalautorizacion = autorizacionService.findByNombre(autorizacion.getNombre());
			if(optionalautorizacion.isPresent()) {
				LOG.info("{}El nombre de un autorizacion esta repetido.",headerLog);
				return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
			}
		}
		
		List<Autorizacion> listaDeSubautorizacionValidado = new ArrayList<Autorizacion>();
		List<Autorizacion> listaDeAuthoritiesPorBorrar = autorizacionViejo.getSubAutorizacion();
		if(listaDeSubautorizacionsActualizados != null) {
			for (int i = 0; i < listaDeSubautorizacionsActualizados.size(); i++) {
				Autorizacion subautorizacionActualizado = listaDeSubautorizacionsActualizados.get(i);
				if(subautorizacionActualizado.getClave() != null) {
					Optional<Autorizacion> optionalSubautorizacionViejo =  autorizacionService.findByClave(subautorizacionActualizado.getClave());
					if(optionalSubautorizacionViejo.isPresent()) {
						Autorizacion subautorizacionViejo = optionalSubautorizacionViejo.get();
						if(!subautorizacionViejo.getPreAutorizacion().equals(autorizacion)) {
							LOG.info("{}Un autorizacion hijo no pertenece al padre.",headerLog);
							return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
						}
						listaDeAuthoritiesPorBorrar.remove(subautorizacionViejo);
						subautorizacionViejo.setNombre(subautorizacionActualizado.getNombre());
						subautorizacionViejo.setDescripcion(subautorizacionActualizado.getNombre());
						listaDeSubautorizacionValidado.add(subautorizacionViejo);
						continue;
					}else {
						subautorizacionActualizado.setId(null);
						subautorizacionActualizado.setClave(null);
					}
				}
				Optional<Autorizacion> optionalSubautorizacionAux = autorizacionService.findByNombre(subautorizacionActualizado.getNombre());
				if(optionalSubautorizacionAux.isPresent()) {
					Autorizacion autorizacion2 = optionalSubautorizacionAux.get();
					if(subautorizacionActualizado.getClave() == null ) {
						LOG.info("{}Falta la clave para uno de los sub autorizacion.",headerLog);
						return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
					}else if(!autorizacion2.getClave().equals(subautorizacionActualizado.getClave())) {
						LOG.info("{}El nombre de un autorizacion esta repetido.",headerLog);
						return new ResponseEntity<Autorizacion>(HttpStatus.CONFLICT);
					}
				}
				listaDeSubautorizacionValidado.add(subautorizacionActualizado);
			}
		}
		listaDeAuthoritiesPorBorrar.stream().forEach(x ->{
			x.setPreAutorizacion(null);
			autorizacionService.update(x);
			autorizacionService.deleteById(x.getId());
		});
		if(listaDeSubautorizacionValidado != null) {
			listaDeSubautorizacionValidado.stream().forEach(x->{
				if(x.getId() == null) {
					x.setClave(UUID.randomUUID());
					x.setEstatus(ACTIVO);
				}
				x.setPreAutorizacion(autorizacion);
				x.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
				x.setFechaDeModificacion(LocalDateTime.now());
				autorizacionService.save(x);
			});
		}
		autorizacion.setSubAutorizacion(listaDeSubautorizacionValidado);
		autorizacionService.update(autorizacion);
		return new ResponseEntity<Autorizacion>(autorizacion,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entity con sus correspondientes sub autorizacion.")
	@DeleteMapping()
	public ResponseEntity<?> delete(@RequestParam(value="token") UUID token, @RequestParam(value="clave") UUID uuid){
		Sesion sesion = getSessionIfIsAuthorized(token, AUTORIZACION);
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Autorizacion> optionalautorizacionViejo =  autorizacionService.findByClave(uuid);
		if(optionalautorizacionViejo.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Autorizacion autorizacionViejo = optionalautorizacionViejo.get();
		Optional<Autorizacion> autorizacionPadreOptional = autorizacionService.findByNombre(AUTENTIFICADOS);
		if(autorizacionPadreOptional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Autorizacion autorizacionPadre = autorizacionPadreOptional.get(); 
		if(!autorizacionPadre.equals(autorizacionViejo.getPreAutorizacion())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Catalogo ELIMINADO = singletonUtil.getEliminado();
		autorizacionViejo.getSubAutorizacion().stream().forEach(autorizacion->{
			autorizacion.setPreAutorizacion(null);
			autorizacion.setEstatus(ELIMINADO);
			autorizacion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
			autorizacionService.update(autorizacion);
			perfilAutorizacionService.findByAutorizacion(autorizacion.getId()).stream().forEach(perfilautorizacion->{
				perfilAutorizacionService.delete(perfilautorizacion);
			});
			autorizacionService.deleteById(autorizacion.getId());
		});
		autorizacionViejo.setSubAutorizacion(null);
		autorizacionViejo.setPreAutorizacion(null);
		autorizacionService.update(autorizacionViejo);
		perfilAutorizacionService.findByAutorizacion(autorizacionViejo.getId()).stream().forEach(perfilautorizacion->{
			perfilAutorizacionService.deleteById(perfilautorizacion.getId());
		});
		autorizacionService.deleteById(autorizacionViejo.getId());
		return new ResponseEntity<Boolean>(HttpStatus.OK);
	}
}
