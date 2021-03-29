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
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Departamento;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.DepartamentoService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.DepartamentoPage;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("departamento/v1")
public class DepartamentoControllerV01 extends Auth{
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private DepartamentoService departamentoService;
	
	private final String TRABAJADOR_CREAR = "TRABAJADOR_CREAR";
	private final String TRABAJADOR_EDITAR = "TRABAJADOR_EDITAR";
	private final String PUESTO_CREAR = "PUESTO_CREAR";
	private final String PUESTOS_EDITAR = "PUESTOS_EDITAR";
	private final String DEPARTAMENTO = "DEPARTAMENTO";
	private final String DEPARTAMENTO_CREAR = "DEPARTAMENTO_CREAR";
	private final String DEPARTAMENTO_EDITAR = "DEPARTAMENTO_EDITAR";
	private final String DEPARTAMENTO_ELIMINAR = "DEPARTAMENTO_ELIMINAR";

	/**
	 * 
	 * @param by:		NOMBRE, DESCRIPCION
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los Departamentos paginado.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION<br/><b>order:</b> ASC, DESC")
	@GetMapping()
	public ResponseEntity<DepartamentoPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		final String PERMISOS[] = { DEPARTAMENTO, PUESTO_CREAR, PUESTOS_EDITAR, TRABAJADOR_CREAR, TRABAJADOR_EDITAR };
		Sesion sesion = getSessionIfIsAuthorized(token, PERMISOS);
		if(sesion == null) {
			return new ResponseEntity<DepartamentoPage>(HttpStatus.UNAUTHORIZED);
		}
		long IdEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		Page<Departamento> response = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<DepartamentoPage>(HttpStatus.BAD_REQUEST);
		}
		if(search == null || search.isEmpty()) {
			response = departamentoService.findByIdEmpresa(IdEmpresa,elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				response = departamentoService.findByLikeNombre(IdEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DESCRIPCION")) {
				response = departamentoService.findByLikeDescripcion(IdEmpresa, text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<DepartamentoPage>(HttpStatus.BAD_REQUEST);
			}
		}
		DepartamentoPage body = new DepartamentoPage(response);
		return new ResponseEntity<DepartamentoPage>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad del Departamento. ")
	@PostMapping()
	public ResponseEntity<Departamento> save(
			@RequestBody Departamento departamento,
			@RequestParam(value="token") UUID token){
		
		Sesion sesion = getSessionIfIsAuthorized(token, DEPARTAMENTO_CREAR) ;
		if(sesion == null) {
			return new ResponseEntity<Departamento>(HttpStatus.UNAUTHORIZED);
		}
		
		long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		boolean canInsert = departamentoService.canInsert(idEmpresa);
		if(!canInsert) {
			return new ResponseEntity<Departamento>(HttpStatus.LOCKED);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Departamento.SIZE_NOMBRE, departamento.getNombre())) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}else {
			departamento.setNombre(departamento.getNombre().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Departamento.SIZE_DESCRIPCION, departamento.getDescripcion())) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}else {
			departamento.setDescripcion(departamento.getDescripcion().strip());
		}
		
		final Catalogo CAT_ACTIVO = singletonUtil.getActivo();
		
		departamento.setClave(UUID.randomUUID());
		departamento.setEstatus(CAT_ACTIVO);
		departamento.setFechaDeModificacion(LocalDateTime.now());
		departamento.setIdUsuarioModificado(sesion.getUsuario().getId());
		departamento.setIdEmpresa(idEmpresa);
		departamento = departamentoService.save(departamento);
		return new ResponseEntity<Departamento>(departamento,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del Departamento. ")
	@PutMapping()
	public ResponseEntity<Departamento> update(
			@RequestBody Departamento departamento,
			@RequestParam(value="token") UUID token){
		
		Sesion sesion = getSessionIfIsAuthorized(token, DEPARTAMENTO_EDITAR) ;
		if(sesion == null) {
			return new ResponseEntity<Departamento>(HttpStatus.UNAUTHORIZED);
		}
		long IdEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		if(isNotValid(sesion.getClave())) {
			return new ResponseEntity<Departamento>(HttpStatus.NOT_FOUND);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Departamento.SIZE_NOMBRE, departamento.getNombre())) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}else {
			departamento.setNombre(departamento.getNombre().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Departamento.SIZE_DESCRIPCION, departamento.getDescripcion())) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}else {
			departamento.setDescripcion(departamento.getDescripcion().strip());
		}
		Optional<Departamento> optionalDepartamento = departamentoService.findByClave(departamento.getClave());
		if(optionalDepartamento.isEmpty()) {
			return new ResponseEntity<Departamento>(HttpStatus.NOT_FOUND);
		}
		Departamento body = optionalDepartamento.get();
		if(!body.getIdEmpresa().equals(IdEmpresa)) {
			return new ResponseEntity<Departamento>(HttpStatus.NOT_FOUND);
		}
		if(!body.getEstatus().equals(singletonUtil.getActivo())) {
			return new ResponseEntity<Departamento>(HttpStatus.NOT_FOUND);
		}
		body.setNombre(departamento.getNombre());
		body.setDescripcion(departamento.getDescripcion());
		body.setFechaDeModificacion(LocalDateTime.now());
		body.setIdUsuarioModificado(sesion.getUsuario().getId());
		body = departamentoService.save(body);
		return new ResponseEntity<Departamento>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad del departamento por clave. ")
	@DeleteMapping
	public ResponseEntity<?> delete(
			@RequestParam(value="clave") UUID uuid,
			@RequestParam(value="token") UUID token){
		
		Sesion sesion = getSessionIfIsAuthorized(token, DEPARTAMENTO_ELIMINAR) ;
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Departamento> optionalDepartamento = departamentoService.findByClave(uuid);
		if(optionalDepartamento.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		Departamento response = optionalDepartamento.get();
		if(!response.getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		departamentoService.deleteById(optionalDepartamento.get().getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
