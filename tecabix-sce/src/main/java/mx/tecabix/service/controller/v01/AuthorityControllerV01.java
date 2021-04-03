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
import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.entity.PerfilAuthority;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.AuthorityService;
import mx.tecabix.db.service.PerfilAuthorityService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.AuthorityPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("authority/v1")
public final class AuthorityControllerV01 extends Auth{
	private static final Logger LOG = LoggerFactory.getLogger(AuthorityControllerV01.class);
	private static final String LOG_URL = "/authority/v1";
	
	private static final String AUTHORITY = "AUTHORITY";
	private static final String PERFIL = "PERFIL";
	private static final String AUTENTIFICADOS = "AUTENTIFICADOS";
	
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private AuthorityService authorityService;
	@Autowired
	private PerfilAuthorityService perfilAuthorityService;
	
	
	@ApiOperation(value = "Persiste la entidad del Authority con sus correspondientes sub Authority. ")
	@PostMapping()
	public ResponseEntity<Authority> save(@RequestParam(value="token") UUID token, @RequestBody Authority authority){
		
		Sesion sesion = getSessionIfIsAuthorized(token, AUTHORITY);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Authority>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		if(isNotValid(TIPO_VARIABLE, Authority.SIZE_NOMBRE, authority.getNombre())){
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Authority.SIZE_DESCRIPCION, authority.getDescripcion())) {
			LOG.info("{}El formato de la descripcioon es incorrecto.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}else {
			authority.setDescripcion(authority.getDescripcion().strip());
		}
		List<Authority> list = authority.getSubAuthority();
		if(list != null) {
			for (int i = 0; i < list.size(); i++) {
				Authority aux = list.get(i);
				if(aux == null) {
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_VARIABLE, Authority.SIZE_NOMBRE, aux.getNombre())){
					LOG.info("{}El formato del nombre  de un sub authority es incorrecto.",headerLog);
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Authority.SIZE_DESCRIPCION, aux.getDescripcion())) {
					LOG.info("{}El formato de la descripcion de un sub authority es incorrecto.",headerLog);
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}else {
					aux.setDescripcion(aux.getDescripcion().strip());
				}
				if(authority.getNombre().equalsIgnoreCase(aux.getNombre())) {
					LOG.info("{}Hay nombres repetidos.",headerLog);
					return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
				}
			}
			for (int i = 0; i < list.size() - 1; i++) {
				Authority auxA = list.get(i);
				for (int j = i + 1; j < list.size(); j++) {
					Authority auxB = list.get(j);
					if(auxA.getNombre().equalsIgnoreCase(auxB.getNombre())) {
						LOG.info("{}Hay nombres repetidos.",headerLog);
						return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
					}
				}
			}
		}
		Optional<Authority> authorities = authorityService.findByNombre(authority.getNombre());
		
		if(authorities.isPresent()) {
			return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
		}
		if(list != null) {
			for (int i = 0; i < list.size(); i++) {
				Authority aux = list.get(i);
				Optional<Authority> authoritiesAux = authorityService.findByNombre(aux.getNombre());
				if(authoritiesAux.isPresent()) {
					LOG.info("{}Uno de los nombre ya existe.",headerLog);
					return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
				}
			}
		}
		Optional<Authority> authorityPadreOptional = authorityService.findByNombre(AUTENTIFICADOS);
		if(authorityPadreOptional.isEmpty()) {
			LOG.info("{}No se encontro el autority AUTENTIFICADO.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Authority authorityPadre = authorityPadreOptional.get(); 
		authority.setId(null);
		authority.setPerfiles(null);
		authority.setPreAuthority(authorityPadre);
		authority.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		authority.setFechaDeModificacion(LocalDateTime.now());
		authority.setEstatus(singletonUtil.getActivo());
		authority.setClave(UUID.randomUUID());
		authority = authorityService.save(authority);
		if(list != null) {
			for (int i = 0; i < list.size(); i++) {
				Authority aux = list.get(i);
				aux.setId(null);
				aux.setPerfiles(null);
				aux.setSubAuthority(null);
				aux.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
				aux.setFechaDeModificacion(LocalDateTime.now());
				aux.setEstatus(singletonUtil.getActivo());
				aux.setClave(UUID.randomUUID());
				aux.setPreAuthority(authority);
				aux = authorityService.save(aux);
				authority = aux.getPreAuthority();
			}
		}
		Optional<Authority>authorityOptional = authorityService.findById(authority.getId());
		if(authorityOptional.isEmpty()) {
			LOG.info("{}No pudo recuperar el id del authority guardado.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		authority = authorityOptional.get();
		return new ResponseEntity<Authority>(authority,HttpStatus.OK);
	}
	/**
	 * 
	 * @param by:		NOMBRE, DESCRIPCION
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los authority paginado.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION<br/><b>order:</b> ASC, DESC")
	@GetMapping
	public ResponseEntity<AuthorityPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		if(isNotAuthorized(token, AUTHORITY)) {
			return new ResponseEntity<AuthorityPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Authority> authorities = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<AuthorityPage>(HttpStatus.BAD_REQUEST);
		}
		if(search == null || search.isEmpty()) {
			authorities = authorityService.findAll(elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				authorities = authorityService.findByLikeNombre(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DESCRIPCION")) {
				authorities = authorityService.findByLikeDescripcion(text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<AuthorityPage>(HttpStatus.BAD_REQUEST);
			}
		}
		for (Authority authority : authorities) {
			authority.setPreAuthority(null);
			authority.setSubAuthority(null);
		}
		AuthorityPage body = new AuthorityPage(authorities); 
		ResponseEntity<AuthorityPage> response = new ResponseEntity<AuthorityPage>(body, HttpStatus.OK);
		return response;
	}
	
	@ApiOperation(value = "Obtiene el authority con el ID proporcionado. ")
	@GetMapping("findByClave")
	public ResponseEntity<Authority> findByClave(@RequestParam(value="token") UUID token, @RequestParam(value = "clave") UUID clave){
		
		if(isNotAuthorized(token, AUTHORITY)) {
			return new ResponseEntity<Authority>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Authority> result = authorityService.findByClave(clave);
		if(result.isEmpty()) {
			return new ResponseEntity<Authority>(HttpStatus.NOT_FOUND);
		}
		
		Authority body = result.get();
		return new ResponseEntity<Authority>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Obtiene los authority autentificados.")
	@GetMapping("findAutentificados")
	public ResponseEntity<Authority> findAutentificados(@RequestParam(value="token") UUID token){
		
		if(isNotAuthorized(token, PERFIL)) {
			return new ResponseEntity<Authority>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Authority> result = authorityService.findByNombre(AUTENTIFICADOS);
		if(result.isEmpty()) {
			return new ResponseEntity<Authority>(HttpStatus.NOT_FOUND);
		}
		Authority body = result.get();
		return new ResponseEntity<Authority>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del Authority con sus correspondientes sub Authority" ,
			notes  = "La clave del Authority principal es obligatorio, en el sub Authority al proporcionar"
			+ " la clave se indica que se va actualizar dicho Authority, "
			+ "si no se le proporciona se le considera un nuevo sub Authority.\n"
			+ "Los Authority ya guardado que no se especifiquen en la petición serán eliminados.")
	@PutMapping()
	public ResponseEntity<Authority> update(@RequestParam(value="token") UUID token, @RequestBody Authority authority){
		Sesion sesion = getSessionIfIsAuthorized(token, AUTHORITY);
		if(sesion == null) {
			return new ResponseEntity<Authority>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPut(idEmpresa, LOG_URL);
		if(authority.getClave() == null) {
			LOG.info("{}No se mando la clave.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_VARIABLE, Authority.SIZE_NOMBRE, authority.getNombre())) {
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Authority.SIZE_DESCRIPCION, authority.getDescripcion())) {
			LOG.info("{}El formato de la descripcion es incorrecto.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}else {
			authority.setDescripcion(authority.getDescripcion().strip());
		}
		Optional<Authority> optionalAuthorityViejo =  authorityService.findByClave(authority.getClave());
		if(optionalAuthorityViejo.isEmpty()) {
			LOG.info("{}No se encontro el authority con la clave {}.",headerLog, authority.getClave());
			return new ResponseEntity<Authority>(HttpStatus.NOT_FOUND);
		}
		
		Authority authorityViejo = optionalAuthorityViejo.get();
		if(!authorityViejo.getEstatus().equals(singletonUtil.getActivo())) {
			LOG.info("{}El authority no esta activo.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.NOT_FOUND);
		}
		authority.setId(authorityViejo.getId());
		authority.setPreAuthority(authorityViejo.getPreAuthority());
		authority.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		authority.setFechaDeModificacion(LocalDateTime.now());
		authority.setEstatus(authorityViejo.getEstatus());
		
		Optional<Authority> authorityPadreOptional = authorityService.findByNombre(AUTENTIFICADOS);
		if(authorityPadreOptional.isEmpty()) {
			LOG.info("{}No se encontro el authority AUTENTIFICADOS.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Authority authorityPadre = authorityPadreOptional.get(); 
		if(!authorityPadre.equals(authorityViejo.getPreAuthority())) {
			LOG.info("{}El auhority que se esta intentando modificar no ereda de AUTENTIFICADO.",headerLog);
			return new ResponseEntity<Authority>(HttpStatus.UNAUTHORIZED);
		}
		
		List<Authority> listaDeSubAuthoritysActualizados = authority.getSubAuthority();
		if(listaDeSubAuthoritysActualizados != null) {
			for (int i = 0; i < listaDeSubAuthoritysActualizados.size(); i++) {
				Authority aux = listaDeSubAuthoritysActualizados.get(i);
				if(aux == null) {
					LOG.info("{}Un sub Authority es nulo.",headerLog);
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_VARIABLE, Authority.SIZE_NOMBRE, aux.getNombre())){
					LOG.info("{}El formato del nombre de un sub authority es incorrecto.",headerLog);
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Authority.SIZE_DESCRIPCION, aux.getDescripcion())) {
					LOG.info("{}El formato de la descripcion de un sub authority es incorrecto.",headerLog);
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}else {
					aux.setDescripcion(aux.getDescripcion().strip());
				}
				if(authority.getNombre().equalsIgnoreCase(aux.getNombre())) {
					LOG.info("{}El nombre de un sub authority esta repetido.",headerLog);
					return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
				}
			}
			for (int i = 0; i < listaDeSubAuthoritysActualizados.size() - 1; i++) {
				Authority auxA = listaDeSubAuthoritysActualizados.get(i);
				for (int j = i + 1; j < listaDeSubAuthoritysActualizados.size(); j++) {
					Authority auxB = listaDeSubAuthoritysActualizados.get(j);
					if(auxA.getNombre().equalsIgnoreCase(auxB.getNombre())) {
						LOG.info("{}El nombre de un sub authority esta repetido.",headerLog);
						return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
					}
				}
			}
		}
		
		if( !authority.getNombre().equals(authorityViejo.getNombre())) {
			Optional<Authority> optionalAuthority = authorityService.findByNombre(authority.getNombre());
			if(optionalAuthority.isPresent()) {
				LOG.info("{}El nombre de un authority esta repetido.",headerLog);
				return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
			}
		}
		
		List<Authority> listaDeSubAuthorityValidado = new ArrayList<Authority>();
		List<Authority> listaDeAuthoritiesPorBorrar = authorityViejo.getSubAuthority();
		if(listaDeSubAuthoritysActualizados != null) {
			for (int i = 0; i < listaDeSubAuthoritysActualizados.size(); i++) {
				Authority subAuthorityActualizado = listaDeSubAuthoritysActualizados.get(i);
				if(subAuthorityActualizado.getClave() != null) {
					Optional<Authority> optionalSubAuthorityViejo =  authorityService.findByClave(subAuthorityActualizado.getClave());
					if(optionalSubAuthorityViejo.isPresent()) {
						Authority subAuthorityViejo = optionalSubAuthorityViejo.get();
						if(!subAuthorityViejo.getPreAuthority().equals(authority)) {
							LOG.info("{}Un authority hijo no pertenece al padre.",headerLog);
							return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
						}
						listaDeAuthoritiesPorBorrar.remove(subAuthorityViejo);
						subAuthorityViejo.setNombre(subAuthorityActualizado.getNombre());
						subAuthorityViejo.setDescripcion(subAuthorityActualizado.getNombre());
						listaDeSubAuthorityValidado.add(subAuthorityViejo);
						continue;
					}else {
						subAuthorityActualizado.setId(null);
						subAuthorityActualizado.setClave(null);
					}
				}
				
				Optional<Authority> optionalSubAuthorityAux = authorityService.findByNombre(subAuthorityActualizado.getNombre());
				if(optionalSubAuthorityAux.isPresent()) {
					Authority authority2 = optionalSubAuthorityAux.get();
					if(subAuthorityActualizado.getClave() == null ) {
						LOG.info("{}Falta la clave para uno de los sub Authority.",headerLog);
						return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
					}else if(!authority2.getClave().equals(subAuthorityActualizado.getClave())) {
						LOG.info("{}El nombre de un authority esta repetido.",headerLog);
						return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
					}
				}
				listaDeSubAuthorityValidado.add(subAuthorityActualizado);
			}
		}
		
		for (Authority authorityItem : listaDeAuthoritiesPorBorrar) {
			authorityItem.setPreAuthority(null);
			authorityItem = authorityService.update(authorityItem);
			authorityService.deleteById(authorityItem.getId());
		}
	
		if(listaDeSubAuthorityValidado != null) {
			for (int i = 0; i < listaDeSubAuthorityValidado.size(); i++) {
				Authority aux = listaDeSubAuthorityValidado.get(i);
				if(aux.getId() == null) {
					aux.setClave(UUID.randomUUID());
					aux.setEstatus(singletonUtil.getActivo());
				}
				aux.setPreAuthority(authority);
				aux.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
				aux.setFechaDeModificacion(LocalDateTime.now());
				aux = authorityService.save(aux);
				aux.getPreAuthority();
			}
		}

		authority.setSubAuthority(listaDeSubAuthorityValidado);
		authorityService.update(authority);
		return new ResponseEntity<Authority>(authority,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entity con sus correspondientes sub Authority.")
	@DeleteMapping()
	public ResponseEntity<?> delete(@RequestParam(value="token") UUID token, @RequestParam(value="clave") UUID uuid){
		Sesion sesion = getSessionIfIsAuthorized(token, AUTHORITY);
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Authority> optionalAuthorityViejo =  authorityService.findByClave(uuid);
		if(optionalAuthorityViejo.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Authority authorityViejo = optionalAuthorityViejo.get();
		Optional<Authority> authorityPadreOptional = authorityService.findByNombre(AUTENTIFICADOS);
		if(authorityPadreOptional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Authority authorityPadre = authorityPadreOptional.get(); 
		if(!authorityPadre.equals(authorityViejo.getPreAuthority())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		List<Authority> listaDeAuthoritiesPorBorrar = authorityViejo.getSubAuthority();
		for (Authority authorityItem : listaDeAuthoritiesPorBorrar) {
			authorityItem.setPreAuthority(null);
			authorityItem.setEstatus(singletonUtil.getEliminado());
			authorityItem.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
			authorityItem = authorityService.update(authorityItem);
			Page<PerfilAuthority> pagePerfilAuthority = perfilAuthorityService.findByAuthority(authorityItem.getId());
			for (PerfilAuthority perfilAuthority : pagePerfilAuthority) {
				perfilAuthorityService.delete(perfilAuthority);
			}
			authorityService.deleteById(authorityItem.getId());
		}
		authorityViejo.setSubAuthority(null);
		authorityViejo.setPreAuthority(null);
		authorityService.update(authorityViejo);
		Page<PerfilAuthority> pagePerfilAuthority = perfilAuthorityService.findByAuthority(authorityViejo.getId());
		for (PerfilAuthority perfilAuthority : pagePerfilAuthority) {
			perfilAuthorityService.deleteById(perfilAuthority.getId());
		}
		authorityService.deleteById(authorityViejo.getId());
		return new ResponseEntity<Boolean>(HttpStatus.OK);
	}
}
