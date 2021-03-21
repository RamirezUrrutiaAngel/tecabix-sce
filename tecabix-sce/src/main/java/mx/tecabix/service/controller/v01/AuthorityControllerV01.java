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
public class AuthorityControllerV01 extends Auth{
	
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
		if(isNotValid(TIPO_VARIABLE, Authority.SIZE_NOMBRE, authority.getNombre())){
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Authority.SIZE_DESCRIPCION, authority.getDescripcion())) {
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}
		List<Authority> list = authority.getSubAuthority();
		if(list != null) {
			for (int i = 0; i < list.size(); i++) {
				Authority aux = list.get(i);
				if(aux == null) {
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_VARIABLE, Authority.SIZE_NOMBRE, aux.getNombre())){
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Authority.SIZE_DESCRIPCION, aux.getDescripcion())) {
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(authority.getNombre().equalsIgnoreCase(aux.getNombre())) {
					return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
				}
			}
			for (int i = 0; i < list.size() - 1; i++) {
				Authority auxA = list.get(i);
				for (int j = i + 1; j < list.size(); j++) {
					Authority auxB = list.get(j);
					if(auxA.getNombre().equalsIgnoreCase(auxB.getNombre())) {
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
					return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
				}
			}
		}
		Optional<Authority> authorityPadreOptional = authorityService.findByNombre(AUTENTIFICADOS);
		if(!authorityPadreOptional.isPresent()) {
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
		if(!authorityOptional.isPresent()) {
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
		if(!result.isPresent()) {
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
		if(!result.isPresent()) {
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
		if(authority.getClave() == null) {
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_VARIABLE, Authority.SIZE_NOMBRE, authority.getNombre())) {
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Authority.SIZE_DESCRIPCION, authority.getDescripcion())) {
			return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
		}
		Optional<Authority> optionalAuthorityViejo =  authorityService.findByClave(authority.getClave());
		if(!optionalAuthorityViejo.isPresent()) {
			return new ResponseEntity<Authority>(HttpStatus.NOT_FOUND);
		}
		
		Authority authorityViejo = optionalAuthorityViejo.get();
		if(!authorityViejo.getEstatus().equals(singletonUtil.getActivo())) {
			return new ResponseEntity<Authority>(HttpStatus.NOT_FOUND);
		}
		authority.setId(authorityViejo.getId());
		authority.setPreAuthority(authorityViejo.getPreAuthority());
		authority.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		authority.setFechaDeModificacion(LocalDateTime.now());
		authority.setEstatus(authorityViejo.getEstatus());
		
		Optional<Authority> authorityPadreOptional = authorityService.findByNombre(AUTENTIFICADOS);
		if(!authorityPadreOptional.isPresent()) {
			return new ResponseEntity<Authority>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Authority authorityPadre = authorityPadreOptional.get(); 
		if(!authorityPadre.equals(authorityViejo.getPreAuthority())) {
			return new ResponseEntity<Authority>(HttpStatus.UNAUTHORIZED);
		}
		
		List<Authority> listaDeSubAuthoritysActualizados = authority.getSubAuthority();
		if(listaDeSubAuthoritysActualizados != null) {
			for (int i = 0; i < listaDeSubAuthoritysActualizados.size(); i++) {
				Authority aux = listaDeSubAuthoritysActualizados.get(i);
				if(aux == null) {
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_VARIABLE, Authority.SIZE_NOMBRE, aux.getNombre())){
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Authority.SIZE_DESCRIPCION, aux.getDescripcion())) {
					return new ResponseEntity<Authority>(HttpStatus.BAD_REQUEST);
				}
				if(authority.getNombre().equalsIgnoreCase(aux.getNombre())) {
					return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
				}
			}
			for (int i = 0; i < listaDeSubAuthoritysActualizados.size() - 1; i++) {
				Authority auxA = listaDeSubAuthoritysActualizados.get(i);
				for (int j = i + 1; j < listaDeSubAuthoritysActualizados.size(); j++) {
					Authority auxB = listaDeSubAuthoritysActualizados.get(j);
					if(auxA.getNombre().equalsIgnoreCase(auxB.getNombre())) {
						return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
					}
				}
			}
		}
		
		if( !authority.getNombre().equals(authorityViejo.getNombre())) {
			Optional<Authority> optionalAuthority = authorityService.findByNombre(authority.getNombre());
			if(optionalAuthority.isPresent()) {
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
						return new ResponseEntity<Authority>(HttpStatus.CONFLICT);
					}else if(!authority2.getClave().equals(subAuthorityActualizado.getClave())) {
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
		if(!optionalAuthorityViejo.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Authority authorityViejo = optionalAuthorityViejo.get();
		Optional<Authority> authorityPadreOptional = authorityService.findByNombre(AUTENTIFICADOS);
		if(!authorityPadreOptional.isPresent()) {
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
