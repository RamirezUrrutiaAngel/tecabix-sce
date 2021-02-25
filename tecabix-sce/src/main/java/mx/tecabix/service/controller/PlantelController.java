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

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Direccion;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.entity.Plantel;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.service.MunicipioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
import mx.tecabix.db.service.PlantelService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.service.Auth;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("plantel")
public class PlantelController extends Auth{

	@Autowired
	private PlantelService plantelService;
	@Autowired
	private MunicipioService municipioService;
	@Autowired
	private TrabajadorService trabajadorService;
	
	private final String PLANTEL = "PLANTEL";
	private final String PLANTEL_CREAR = "PLANTEL_CREAR";
	private final String PLANTEL_EDITAR = "PLANTEL_EDITAR";
	
	@GetMapping
	private ResponseEntity<Page<Plantel>> get(
			@RequestParam(value="token") String token, 
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {

		Sesion sesion = getSessionIfIsAuthorized(token, PLANTEL);
		if(sesion == null){
			return new ResponseEntity<Page<Plantel>>(HttpStatus.UNAUTHORIZED);
		}
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		Page<Plantel> body = plantelService.findByIdEscuela(idEscuela, elements, page);
		return new ResponseEntity<Page<Plantel>>(body, HttpStatus.OK);
	}
	
	@PostMapping
	private ResponseEntity<Plantel> post(@RequestParam(value="token") String token, @RequestBody Plantel plantel){
		Sesion sesion = getSessionIfIsAuthorized(token, PLANTEL_CREAR);
		if(sesion == null){
			return new ResponseEntity<Plantel>(HttpStatus.UNAUTHORIZED);
		}
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		if(plantel.getNombre() == null || plantel.getNombre().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(plantel.getDireccion() == null) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Direccion direccion = plantel.getDireccion();
		if(direccion == null ) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getCalle() == null || direccion.getCalle().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getCodigoPostal() == null || direccion.getCodigoPostal().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getAsentamiento() == null || direccion.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getNumExt() == null || direccion.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getMunicipio() == null || direccion.getMunicipio().getId() == null ) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Optional<Municipio> municipioOptional = municipioService.findById(direccion.getMunicipio().getId());
		if(!municipioOptional.isPresent()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipio = municipioOptional.get();
		direccion.setMunicipio(municipio);
		
		if(plantel.getGerente() == null || plantel.getGerente().getId() == null) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Optional<Trabajador> optionalTrabajador = trabajadorService.findById(plantel.getGerente().getId());
		if(!optionalTrabajador.isPresent()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Trabajador gerente = optionalTrabajador.get();
		if(!gerente.getIdEscuela().equals(idEscuela)) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		plantel.setGerente(gerente);
		Optional<Plantel> optionalPlantel = plantelService.findByIdEscuelaAndNombre(idEscuela, plantel.getNombre());
		if(optionalPlantel.isPresent()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_GATEWAY);
		}
		plantel.setFechaDeModificacion(LocalDateTime.now());
		plantel.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		plantel = plantelService.save(plantel);
		return new ResponseEntity<Plantel>(plantel,HttpStatus.OK);
	}
	
	@PutMapping
	private ResponseEntity<Plantel> put(@RequestParam(value="token") String token, @RequestBody Plantel plantel){
		Sesion sesion = getSessionIfIsAuthorized(token, PLANTEL_EDITAR);
		if(sesion == null){
			return new ResponseEntity<Plantel>(HttpStatus.UNAUTHORIZED);
		}
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		if(plantel.getId() == null) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Optional<Plantel> optionalPlantel = plantelService.findById(plantel.getId());
		if(!optionalPlantel.isPresent()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_FOUND);
		}
		if(!optionalPlantel.get().getIdEscuela().equals(idEscuela)) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_FOUND);
		}
		if(plantel.getNombre() == null || plantel.getNombre().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(plantel.getDireccion() == null) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Direccion direccion = plantel.getDireccion();
		if(direccion == null ) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getCalle() == null || direccion.getCalle().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getCodigoPostal() == null || direccion.getCodigoPostal().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getAsentamiento() == null || direccion.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getNumExt() == null || direccion.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getMunicipio() == null || direccion.getMunicipio().getId() == null ) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Optional<Municipio> municipioOptional = municipioService.findById(direccion.getMunicipio().getId());
		if(!municipioOptional.isPresent()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipio = municipioOptional.get();
		direccion.setMunicipio(municipio);
		
		if(plantel.getGerente() == null || plantel.getGerente().getId() == null) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Optional<Trabajador> optionalTrabajador = trabajadorService.findById(plantel.getGerente().getId());
		if(!optionalTrabajador.isPresent()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Trabajador gerente = optionalTrabajador.get();
		if(!gerente.getIdEscuela().equals(idEscuela)) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		plantel.setGerente(gerente);
		Optional<Plantel> optionalPlantel2 = plantelService.findByIdEscuelaAndNombre(idEscuela, plantel.getNombre());
		if(optionalPlantel2.isPresent()) {
			if(!optionalPlantel2.get().getId().equals(plantel.getId())) {
				return new ResponseEntity<Plantel>(HttpStatus.BAD_GATEWAY);
			}
		}
		plantel.setFechaDeModificacion(LocalDateTime.now());
		plantel.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		plantel = plantelService.update(plantel);
		return new ResponseEntity<Plantel>(plantel,HttpStatus.OK);
	}
	
	
	
}
