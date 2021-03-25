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
import mx.tecabix.db.entity.Puesto;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.DepartamentoService;
import mx.tecabix.db.service.PuestoService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.PuestoPage;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("puesto/v1")
public class PuestoControllerV01 extends Auth{

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private PuestoService puestoService;
	@Autowired
	private DepartamentoService departamentoService;
	
	private final String PUESTO = "PUESTO";
	private final String PUESTO_CREAR = "PUESTO_CREAR";
	private final String PUESTOS_EDITAR = "PUESTOS_EDITAR";
	private final String PUESTOS_ELIMINAR = "PUESTOS_ELIMINAR";
	private final String TRABAJADOR_CREAR = "TRABAJADOR_CREAR";
	private final String TRABAJADOR_EDITAR = "TRABAJADOR_EDITAR";
	/**
	 * 
	 * @param by:		NOMBRE, DESCRIPCION, DEPARTAMENTO
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los puestos paginado.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION, DEPARTAMENTO<br/><b>order:</b> ASC, DESC")
	@GetMapping()
	public ResponseEntity<PuestoPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		final String PERMISOS[] = { PUESTO, TRABAJADOR_CREAR, TRABAJADOR_EDITAR };
		Sesion sesion = getSessionIfIsAuthorized(token, PERMISOS);
		if(sesion == null) {
			return new ResponseEntity<PuestoPage>(HttpStatus.UNAUTHORIZED);
		}
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		Page<Puesto> response = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<PuestoPage>(HttpStatus.BAD_REQUEST);
		}
		if(search == null || search.isEmpty()) {
			response = puestoService.findByIdEmpresa(idEmpresa, elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				response = puestoService.findByLikeNombre(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DESCRIPCION")) {
				response = puestoService.findByLikeDescripcion(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DEPARTAMENTO")) {
				response = puestoService.findByLikeDepartamento(text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<PuestoPage>(HttpStatus.BAD_REQUEST);
			}
		}
		PuestoPage body = new PuestoPage(response);
		return new ResponseEntity<PuestoPage>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad del Puesto. ")
	@PostMapping
	public ResponseEntity<Puesto> save(@RequestParam(value="token") UUID token,@RequestBody Puesto puesto){
		Sesion sesion = getSessionIfIsAuthorized(token, PUESTO_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Puesto>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Puesto.SIZE_NOMBRE, puesto.getNombre())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}else {
			puesto.setNombre(puesto.getNombre().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Puesto.SIZE_DESCRIPCION, puesto.getDescripcion())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}else {
			puesto.setDescripcion(puesto.getDescripcion().strip());
		}
		if(isNotValid(puesto.getDepartamento())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(puesto.getDepartamento().getClave())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		Optional<Departamento> optionalDepartamento = departamentoService.findByClave(puesto.getDepartamento().getClave());
		if(optionalDepartamento.isEmpty()) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		Departamento departamento = optionalDepartamento.get();
		final Catalogo CAT_ACTIVO = singletonUtil.getActivo();
		if(!departamento.getIdEmpresa().equals(sesion.getLicencia().getPlantel().getIdEmpresa())) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		if(!departamento.getEstatus().equals(CAT_ACTIVO)) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		puesto.setClave(UUID.randomUUID());
		puesto.setDepartamento(departamento);
		puesto.setEstatus(CAT_ACTIVO);
		puesto.setFechaDeModificacion(LocalDateTime.now());
		puesto.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		puesto = puestoService.save(puesto);
		return new ResponseEntity<Puesto>(puesto, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del Puesto. ")
	@PutMapping
	public ResponseEntity<Puesto> update(@RequestParam(value="token") UUID token,@RequestBody Puesto puesto){
		Sesion sesion = getSessionIfIsAuthorized(token, PUESTOS_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<Puesto>(HttpStatus.UNAUTHORIZED);
		}
		if (isNotValid(puesto.getClave())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Puesto.SIZE_NOMBRE, puesto.getNombre())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}else {
			puesto.setNombre(puesto.getNombre().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Puesto.SIZE_DESCRIPCION, puesto.getDescripcion())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}else {
			puesto.setDescripcion(puesto.getDescripcion().strip());
		}
		if(isNotValid(puesto.getDepartamento())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(puesto.getDepartamento().getClave())) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		Optional<Puesto> optionalPuesto = puestoService.findByClave(puesto.getClave());
		if(optionalPuesto.isEmpty()) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_FOUND);
		}
		Puesto puestoEdit = optionalPuesto.get();
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		if(!puestoEdit.getDepartamento().getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Optional<Departamento> optionalDepartamento = departamentoService.findByClave(puesto.getDepartamento().getClave());
		if(optionalDepartamento.isEmpty()) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		Departamento departamento = optionalDepartamento.get();
		final Catalogo CAT_ACTIVO = singletonUtil.getActivo();
		if(!departamento.getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		if(!departamento.getEstatus().equals(CAT_ACTIVO)) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		puestoEdit.setNombre(puesto.getNombre());
		puestoEdit.setDescripcion(puesto.getDescripcion());
		puestoEdit.setDepartamento(departamento);
		puestoEdit.setFechaDeModificacion(LocalDateTime.now());
		puestoEdit.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		puestoEdit = puestoService.update(puestoEdit);
		return new ResponseEntity<Puesto>(puestoEdit, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad del Puesto por clave. ")
	@DeleteMapping
	public ResponseEntity<?> delete(@RequestParam(value="token") UUID token, @RequestParam(value="clave") UUID uuid){
		Sesion sesion = getSessionIfIsAuthorized(token, PUESTOS_ELIMINAR);
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Puesto> optionalPuesto =  puestoService.findByClave(uuid);
		if(optionalPuesto.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Puesto puesto = optionalPuesto.get();
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		if(!puesto.getDepartamento().getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		puesto.setEstatus(singletonUtil.getEliminado());
		puesto.setFechaDeModificacion(LocalDateTime.now());
		puesto.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		puesto = puestoService.update(puesto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
