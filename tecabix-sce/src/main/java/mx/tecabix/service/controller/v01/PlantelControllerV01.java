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
import mx.tecabix.db.entity.Direccion;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.entity.Plantel;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.service.DireccionService;
import mx.tecabix.db.service.MunicipioService;
import mx.tecabix.db.service.PlantelService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.PlantelPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("plantel/v1")
public class PlantelControllerV01 extends Auth{

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private PlantelService plantelService;
	@Autowired
	private MunicipioService municipioService;
	@Autowired
	private TrabajadorService trabajadorService;
	@Autowired
	private DireccionService direccionService;
	
	private final String PLANTEL = "PLANTEL";
	private final String PLANTEL_CREAR = "PLANTEL_CREAR";
	private final String PLANTEL_EDITAR = "PLANTEL_EDITAR";
	private final String PLANTEL_ELIMINAR = "PLANTEL_ELIMINAR";
	
	/**
	 * 
	 * @param by:		NOMBRE, MUNICIPIO, ESTADO
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los Planteles paginado.", 
			notes = "<b>by:</b> NOMBRE, MUNICIPIO, ESTADO<br/><b>order:</b> ASC, DESC")
	@GetMapping
	private ResponseEntity<PlantelPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {

		Sesion sesion = getSessionIfIsAuthorized(token, PLANTEL);
		if(sesion == null){
			return new ResponseEntity<PlantelPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Plantel> response = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<PlantelPage>(HttpStatus.BAD_REQUEST);
		}
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		if(search == null || search.isEmpty()) {
			response = plantelService.findByIdEmpresa(idEmpresa, elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				response = plantelService.findByLikeNombre(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("MUNICIPIO")) {
				response = plantelService.findByLikeMunicipio(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("ESTADO")) {
				response = plantelService.findByLikeEstado(idEmpresa, text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<PlantelPage>( HttpStatus.BAD_REQUEST);
			}
		}
		PlantelPage body = new PlantelPage(response);
		return new ResponseEntity<PlantelPage>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad del Plantel con sus correspondiente direccion. ")
	@PostMapping
	private ResponseEntity<Plantel> save(@RequestParam(value="token") UUID token, @RequestBody Plantel plantel){
		Sesion sesion = getSessionIfIsAuthorized(token, PLANTEL_CREAR);
		if(sesion == null){
			return new ResponseEntity<Plantel>(HttpStatus.UNAUTHORIZED);
		}
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Plantel.SIZE_NOMBRE, plantel.getNombre())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			plantel.setNombre(plantel.getNombre().strip());
		}
		if(isNotValid(plantel.getGerente())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(plantel.getGerente().getClave())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(plantel.getDireccion())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Direccion direccion = plantel.getDireccion();
		if(isNotValid(direccion)) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_CALLE, direccion.getCalle())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setCalle(direccion.getCalle().strip());
		}
		if(isNotValid(TIPO_NUMERIC, Direccion.SIZE_CODIGO_POSTAL, direccion.getCodigoPostal())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Direccion.SIZE_ASENTAMIENTO, direccion.getAsentamiento())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setAsentamiento(direccion.getAsentamiento().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_NUM_EXT, direccion.getNumExt())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setNumExt(direccion.getNumExt().strip());
		}
		if(isValid(direccion.getNumInt())) {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_NUM_INT, direccion.getNumInt())) {
				return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
			}else {
				direccion.setNumInt(direccion.getNumInt().strip());
			}
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_REFERENCIA, direccion.getReferencia())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setReferencia(direccion.getReferencia().strip());
		}
		if(isNotValid(direccion.getMunicipio())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(direccion.getMunicipio().getClave())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Optional<Plantel> optionalPlantel = plantelService.findByNombre(idEmpresa, plantel.getNombre());
		if(optionalPlantel.isPresent()) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_GATEWAY);
		}
		Optional<Municipio> municipioOptional = municipioService.findByClave(direccion.getMunicipio().getClave());
		if(municipioOptional.isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipio = municipioOptional.get();
		direccion.setMunicipio(municipio);
		
		Optional<Trabajador> optionalTrabajador = trabajadorService.findByClave(plantel.getGerente().getClave());
		if(optionalTrabajador.isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Trabajador gerente = optionalTrabajador.get();
		if(!gerente.getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		direccion.setClave(UUID.randomUUID());
		direccion.setEstatus(singletonUtil.getActivo());
		direccion.setFechaDeModificacion(LocalDateTime.now());
		direccion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		direccion = direccionService.save(direccion);
		plantel.setIdEmpresa(idEmpresa);
		plantel.setDireccion(direccion);
		plantel.setGerente(gerente);
		plantel.setClave(UUID.randomUUID());
		plantel.setEstatus(singletonUtil.getActivo());
		plantel.setFechaDeModificacion(LocalDateTime.now());
		plantel.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		plantel = plantelService.save(plantel);
		return new ResponseEntity<Plantel>(plantel,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del Plantel con sus correspondiente direccion. ")
	@PutMapping
	private ResponseEntity<Plantel> update(@RequestParam(value="token") UUID token, @RequestBody Plantel plantel){
		Sesion sesion = getSessionIfIsAuthorized(token, PLANTEL_EDITAR);
		if(sesion == null){
			return new ResponseEntity<Plantel>(HttpStatus.UNAUTHORIZED);
		}
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		if(isNotValid(plantel.getClave())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Plantel.SIZE_NOMBRE, plantel.getNombre())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			plantel.setNombre(plantel.getNombre().strip());
		}
		if(isNotValid(plantel.getGerente())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(plantel.getGerente().getClave())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(plantel.getDireccion())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Direccion direccion = plantel.getDireccion();
		if(isNotValid(direccion)) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_CALLE, direccion.getCalle())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setCalle(direccion.getCalle().strip());
		}
		if(isNotValid(TIPO_NUMERIC, Direccion.SIZE_CODIGO_POSTAL, direccion.getCodigoPostal())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Direccion.SIZE_ASENTAMIENTO, direccion.getAsentamiento())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setAsentamiento(direccion.getAsentamiento().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_NUM_EXT, direccion.getNumExt())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setNumExt(direccion.getNumExt().strip());
		}
		if(isValid(direccion.getNumInt())) {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_NUM_INT, direccion.getNumInt())) {
				return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
			}else {
				direccion.setNumInt(direccion.getNumInt().strip());
			}
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_REFERENCIA, direccion.getReferencia())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setReferencia(direccion.getReferencia().strip());
		}
		if(isNotValid(direccion.getMunicipio())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(direccion.getMunicipio().getClave())) {
			return new ResponseEntity<Plantel>(HttpStatus.BAD_REQUEST);
		}
		Optional<Municipio> municipioOptional = municipioService.findByClave(direccion.getMunicipio().getClave());
		if(municipioOptional.isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipio = municipioOptional.get();
		direccion.setMunicipio(municipio);
		
		Optional<Trabajador> optionalTrabajador = trabajadorService.findByClave(plantel.getGerente().getClave());
		if(optionalTrabajador.isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Trabajador gerente = optionalTrabajador.get();
		if(!gerente.getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_ACCEPTABLE);
		}
		Optional<Plantel> optionalPlantel = plantelService.findByNombre(idEmpresa, plantel.getNombre());
		if(optionalPlantel.isPresent()) {
			if(!optionalPlantel.get().getClave().equals(plantel.getClave())) {
				return new ResponseEntity<Plantel>(HttpStatus.BAD_GATEWAY);
			}
		}
		optionalPlantel = plantelService.findByClave(plantel.getClave());
		if(optionalPlantel.isEmpty()) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_FOUND);
		}
		Plantel plantelEdit = optionalPlantel.get();
		if(!plantelEdit.getEstatus().equals(singletonUtil.getActivo())) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_FOUND);
		}
		if(!plantelEdit.getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<Plantel>(HttpStatus.NOT_FOUND);
		}
		direccion.setId(plantelEdit.getDireccion().getId());
		direccion.setClave(plantelEdit.getDireccion().getClave());
		direccion.setFechaDeModificacion(LocalDateTime.now());
		direccion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		direccion.setEstatus(plantelEdit.getDireccion().getEstatus());
		direccion = direccionService.update(direccion);
		plantelEdit.setDireccion(direccion);
		plantelEdit.setNombre(plantel.getNombre());
		plantelEdit.setGerente(gerente);
		plantelEdit.setFechaDeModificacion(LocalDateTime.now());
		plantelEdit.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		plantelEdit = plantelService.update(plantelEdit);
		return new ResponseEntity<Plantel>(plantelEdit,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad del Plantel con sus correspondiente direccion. ")
	@DeleteMapping
	public ResponseEntity<?> delete(@RequestParam(value="clave") UUID clave, @RequestParam(value="token") UUID token) {
		Sesion sesion = getSessionIfIsAuthorized(token, PLANTEL_ELIMINAR);
		if(sesion == null){
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Plantel> optionalPlantel = plantelService.findByClave(clave);
		if(optionalPlantel.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Plantel plantel =  optionalPlantel.get();
		Direccion direccion = plantel.getDireccion();
		direccion.setEstatus(singletonUtil.getEliminado());
		direccion.setFechaDeModificacion(LocalDateTime.now());
		direccion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		direccionService.update(direccion);
		plantel.setEstatus(singletonUtil.getEliminado());
		plantel.setFechaDeModificacion(LocalDateTime.now());
		plantel.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		plantelService.update(plantel);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
