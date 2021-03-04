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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Departamento;
import mx.tecabix.db.entity.Puesto;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.DepartamentoService;
import mx.tecabix.db.service.PuestoService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.service.Auth;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("puesto/v1")
public class PuestoControllerV01 extends Auth{

	@Autowired
	private TrabajadorService trabajadorService;
	@Autowired
	private PuestoService puestoService;
	@Autowired
	private DepartamentoService departamentoService;
	@Autowired
	private CatalogoService catalogoService;
	
	private final String PUESTO = "PUESTO";
	private final String PUESTO_CREAR = "PUESTO_CREAR";
	private final String PUESTOS_EDITAR = "PUESTOS_EDITAR";
	
	private final String ESTATUS = "ESTATUS";
	private final String ACTIVO = "ACTIVO";
	
	@GetMapping
	public ResponseEntity<Puesto> get(@RequestParam(value="token") String token){
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<Puesto>(HttpStatus.UNAUTHORIZED);
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuarioName = auth.getName();
		Optional<Trabajador> optionalTrabajador = trabajadorService.findByUsuario(usuarioName);
		if(!optionalTrabajador.isPresent()) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_FOUND);
		}
		Puesto body = optionalTrabajador.get().getPuesto();
		return new ResponseEntity<Puesto>(body,HttpStatus.OK);
	}
	@GetMapping("findAll")
	public ResponseEntity<Page<Puesto>> findAll(@RequestParam(value="token") String token, byte elements, short page) {
		Sesion sesion = getSessionIfIsAuthorized(token, PUESTO);
		if(sesion == null) {
			return new ResponseEntity<Page<Puesto>>(HttpStatus.UNAUTHORIZED);
		}
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		Page<Puesto> puestos = puestoService.findByIdEscuela(idEscuela, elements, page);
		return new ResponseEntity<Page<Puesto>>(puestos,HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Puesto> save(@RequestParam(value="token") String token,@RequestBody Puesto puesto){
		Sesion sesion = getSessionIfIsAuthorized(token, PUESTO_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Puesto>(HttpStatus.UNAUTHORIZED);
		}
		if(puesto.getNombre() == null || puesto.getNombre().isEmpty()) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(puesto.getDescripcion() ==  null || puesto.getDescripcion().isEmpty()) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(puesto.getDepartamento() == null) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(puesto.getDepartamento().getId() == null) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		Optional<Departamento> optionalDepartamento = departamentoService.findById(puesto.getDepartamento().getId());
		if(!optionalDepartamento.isPresent()) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		Departamento departamento = optionalDepartamento.get();
		if(!departamento.getIdEscuela().equals(sesion.getLicencia().getPlantel().getIdEscuela())) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<Puesto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ACTIVO = optionalCatalogoActivo.get();
		puesto.setDepartamento(departamento);
		puesto.setEstatus(CAT_ACTIVO);
		puesto.setFechaDeModificacion(LocalDateTime.now());
		puesto.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		puesto = puestoService.save(puesto);
		return new ResponseEntity<Puesto>(puesto, HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<Puesto> update(@RequestParam(value="token") String token,@RequestBody Puesto puesto){
		Sesion sesion = getSessionIfIsAuthorized(token, PUESTOS_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<Puesto>(HttpStatus.UNAUTHORIZED);
		}
		if(puesto.getId() == null) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(puesto.getNombre() == null || puesto.getNombre().isEmpty()) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(puesto.getDescripcion() ==  null || puesto.getDescripcion().isEmpty()) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(puesto.getDepartamento() == null) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		if(puesto.getDepartamento().getId() == null) {
			return new ResponseEntity<Puesto>(HttpStatus.BAD_REQUEST);
		}
		Optional<Departamento> optionalDepartamento = departamentoService.findById(puesto.getDepartamento().getId());
		if(!optionalDepartamento.isPresent()) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		Optional<Puesto> optionalPuesto = puestoService.findById(puesto.getId());
		if(!optionalPuesto.isPresent()) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_FOUND);
		}
		if(!optionalPuesto.get().getDepartamento().getIdEscuela().equals(sesion.getLicencia().getPlantel().getIdEscuela())) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_FOUND);
		}
		Departamento departamento = optionalDepartamento.get();
		if(!departamento.getIdEscuela().equals(sesion.getLicencia().getPlantel().getIdEscuela())) {
			return new ResponseEntity<Puesto>(HttpStatus.NOT_ACCEPTABLE);
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<Puesto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ACTIVO = optionalCatalogoActivo.get();
		puesto.setDepartamento(departamento);
		puesto.setEstatus(CAT_ACTIVO);
		puesto.setFechaDeModificacion(LocalDateTime.now());
		puesto.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		puesto = puestoService.update(puesto);
		return new ResponseEntity<Puesto>(puesto, HttpStatus.OK);
	}
}
