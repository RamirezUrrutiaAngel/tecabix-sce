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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
import mx.tecabix.db.entity.Departamento;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.DepartamentoService;
import mx.tecabix.service.Auth;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("departamento/v1")
public class DepartamentoControllerV01 extends Auth{
	
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private DepartamentoService departamentoService;
	
	private final String ACTIVO = "ACTIVO";
	private final String ESTATUS = "ESTATUS";
	private final String DEPARTAMENTO = "DEPARTAMENTO";
	private final String DEPARTAMENTO_CREAR = "DEPARTAMENTO_CREAR";
	private final String DEPARTAMENTO_EDITAR = "DEPARTAMENTO_EDITAR";
	private final String DEPARTAMENTO_ELIMINAR = "DEPARTAMENTO_ELIMINAR";

	@ApiOperation(value = "Obtiene todo los Departamentos paginado.")
	@GetMapping("findAll")
	public ResponseEntity<Page<Departamento>> findAll(
			@RequestParam(value="token") String token,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, DEPARTAMENTO) ;
		if(sesion == null) {
			return new ResponseEntity<Page<Departamento>>(HttpStatus.UNAUTHORIZED);
		}
		long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		Page<Departamento> response = departamentoService.findByIdEscuela(idEscuela, elements, page);
		return new ResponseEntity<Page<Departamento>>(response,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad del Departamento. ")
	@PostMapping("save")
	public ResponseEntity<Departamento> save(
			@RequestBody Departamento departamento,
			@RequestParam(value="token") String token){
		
		Sesion sesion = getSessionIfIsAuthorized(token, DEPARTAMENTO_CREAR) ;
		if(sesion == null) {
			return new ResponseEntity<Departamento>(HttpStatus.UNAUTHORIZED);
		}
		long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		if(departamento.getNombre() == null || departamento.getNombre().isEmpty()) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}
		if(departamento.getDescripcion() == null || departamento.getDescripcion().isEmpty()) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<Departamento>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ACTIVO = optionalCatalogoActivo.get();
		
		departamento.setEstatus(CAT_ACTIVO);
		departamento.setFechaDeModificacion(LocalDateTime.now());
		departamento.setIdUsuarioModificado(sesion.getUsuario().getId());
		departamento.setIdEscuela(idEscuela);
		departamento = departamentoService.save(departamento);
		return new ResponseEntity<Departamento>(departamento,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del Departamento. ")
	@PutMapping("update")
	public ResponseEntity<Departamento> update(
			@RequestBody Departamento departamento,
			@RequestParam(value="token") String token){
		
		Sesion sesion = getSessionIfIsAuthorized(token, DEPARTAMENTO_EDITAR) ;
		if(sesion == null) {
			return new ResponseEntity<Departamento>(HttpStatus.UNAUTHORIZED);
		}
		long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		if(departamento.getId() == null ) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}
		if(departamento.getNombre() == null || departamento.getNombre().isEmpty()) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}
		if(departamento.getDescripcion() == null || departamento.getDescripcion().isEmpty()) {
			return new ResponseEntity<Departamento>(HttpStatus.BAD_REQUEST);
		}
		Optional<Departamento> optionalDepartamento = departamentoService.findById(departamento.getId());
		if(!optionalDepartamento.isPresent()) {
			return new ResponseEntity<Departamento>(HttpStatus.NOT_FOUND);
		}
		Departamento body = optionalDepartamento.get();
		if(!body.getIdEscuela().equals(idEscuela)) {
			return new ResponseEntity<Departamento>(HttpStatus.NOT_FOUND);
		}
		body.setNombre(departamento.getNombre());
		body.setDescripcion(departamento.getDescripcion());
		body.setFechaDeModificacion(LocalDateTime.now());
		body.setIdUsuarioModificado(sesion.getUsuario().getId());
		body = departamentoService.save(body);
		return new ResponseEntity<Departamento>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad del Departamento por ID. ")
	@DeleteMapping
	public ResponseEntity<Boolean> delete(
			@RequestParam(value="id") Long id,
			@RequestParam(value="token") String token){
		
		Sesion sesion = getSessionIfIsAuthorized(token, DEPARTAMENTO_ELIMINAR) ;
		if(sesion == null) {
			return new ResponseEntity<Boolean>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Departamento> optionalDepartamento = departamentoService.findById(id);
		if(!optionalDepartamento.isPresent()) {
			return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
		}
		long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		Departamento response = optionalDepartamento.get();
		if(!response.getIdEscuela().equals(idEscuela)) {
			return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
		}
		departamentoService.deleteById(id);
		return new ResponseEntity<Boolean>(true,HttpStatus.OK);
	}
}
