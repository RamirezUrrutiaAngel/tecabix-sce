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
import mx.tecabix.db.entity.Perfil;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.AuthorityService;
import mx.tecabix.db.service.PerfilService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.PerfilPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("perfil/v1")
public class PerfilControllerV01 extends Auth{

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private PerfilService perfilService;
	@Autowired 
	private AuthorityService authorityService;
	
	private final String PERFIL = "PERFIL";
	private final String PERFIL_CREAR = "PERFIL_CREAR";
	private final String PERFIL_EDITAR = "PERFIL_EDITAR";
	private final String PERFIL_ELIMINAR = "PERFIL_ELIMINAR";
	
	/**
	 * 
	 * @param by:		NOMBRE, DESCRIPCION
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los perfiles paginado.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION<br/><b>order:</b> ASC, DESC")
	@GetMapping()
	public ResponseEntity<PerfilPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {

		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL);
		if(sesion == null){
			return new ResponseEntity<PerfilPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Perfil> response = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<PerfilPage>(HttpStatus.BAD_REQUEST);
		}
		long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		if(search == null || search.isEmpty()) {
			response = perfilService.findAll(idEscuela, elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				response = perfilService.findByLikeNombre(idEscuela, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DESCRIPCION")) {
				response = perfilService.findByLikeDescripcion(idEscuela, text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<PerfilPage>(HttpStatus.BAD_REQUEST);
			}
		}
		PerfilPage body = new PerfilPage(response);
		return new ResponseEntity<PerfilPage>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad del Perfil con sus correspondientes Authority. ")
	@PostMapping
	public ResponseEntity<Perfil> save(@RequestParam(value="token") UUID token,@RequestBody Perfil perfil){
		
		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL_CREAR);
		if(isValid(sesion)){
			return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Perfil.SIZE_DESCRIPCION, perfil.getDescripcion())) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Perfil.SIZE_NOMBRE, perfil.getNombre())) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		if((perfilService.findByNombre(sesion.getLicencia().getPlantel().getIdEscuela(), perfil.getNombre()))!=null) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_ACCEPTABLE);
		}
		perfil.setIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela());
		List<Authority> list = perfil.getAuthorities();
		List<Authority> listAux = new ArrayList<Authority>();
		if(list != null) {
			for (Authority authority : list) {
				Optional<Authority> authOptional = authorityService.findByClave(authority.getClave());
				if(!authOptional.isPresent()) {
					return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
				}else {
					listAux.add(authOptional.get());
				}
			}
			perfil.setAuthorities(listAux);
		}
		
		perfil.setIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela());
		perfil.setEstatus(singletonUtil.getActivo());
		perfil.setIdUsuarioModificado(sesion.getUsuario().getId());
		perfil.setFechaDeModificacion(LocalDateTime.now());
		perfilService.save(perfil);
		return new ResponseEntity<Perfil>(perfil,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del perfil.")
	@PutMapping
	public ResponseEntity<Perfil> update(@RequestParam(value="token") UUID token,@RequestBody Perfil perfil){
		
		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL_EDITAR);
		if(isNotValid(sesion)){
			return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(perfil.getClave())) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Perfil.SIZE_DESCRIPCION, perfil.getDescripcion())) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Perfil.SIZE_NOMBRE, perfil.getNombre())) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		Optional<Perfil> perfilAuxOptional = perfilService.findByClave(perfil.getClave());
		if(!perfilAuxOptional.isPresent()) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_FOUND);
		}
		Perfil perfilAux = perfilAuxOptional.get();
		if(perfilAux.getIdEscuela().longValue() != sesion.getLicencia().getPlantel().getIdEscuela().longValue()) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_FOUND);
		}
		
		Perfil perfilExistente = perfilService.findByNombre(sesion.getLicencia().getPlantel().getIdEscuela(), perfil.getNombre());
		if(perfilExistente != null && perfilExistente.getId().longValue() != perfil.getId()) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		List<Authority> list = perfil.getAuthorities();
		if(list != null) {
			for (Authority authority : list) {
				if(!authorityService.findById(authority.getId()).isPresent())return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
			}
		}
		perfilAux.setNombre(perfil.getNombre());
		perfilAux.setDescripcion(perfil.getDescripcion());
		perfilAux.setAuthorities(list);
		perfilAux.setIdUsuarioModificado(sesion.getUsuario().getId());
		perfilAux.setFechaDeModificacion(LocalDateTime.now());
		perfilService.update(perfilAux);
		return new ResponseEntity<Perfil>(perfilAux,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad del perfil.")
	@DeleteMapping
	public ResponseEntity<?> delete(@RequestParam(value="token") UUID token,@RequestParam UUID clave){
		
		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL_ELIMINAR);
		if(sesion == null){
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Optional<Perfil> perfilOptional = perfilService.findByClave(clave);
		
		if(!perfilOptional.isPresent() ) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Perfil perfil = perfilOptional.get();
		if(perfil.getIdEscuela().longValue() != sesion.getLicencia().getPlantel().getIdEscuela().longValue()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		perfil.setEstatus(singletonUtil.getEliminado());
		perfil.setIdUsuarioModificado(sesion.getUsuario().getId());
		perfil.setFechaDeModificacion(LocalDateTime.now());
		perfil = perfilService.update(perfil);
		perfilService.deleteById(perfil.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
