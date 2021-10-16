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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mx.tecabix.db.entity.CajaRegistradora;
import mx.tecabix.db.entity.CajaRegistro;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Plantel;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.service.CajaRegistradoraService;
import mx.tecabix.db.service.PlantelService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.CajaRegistradoraPage;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("caja-registradora/v1")
public final class CajaRegistradoraControllerV01 extends Auth{

	private static final Logger LOG = LoggerFactory.getLogger(CajaRegistradoraControllerV01.class);
	private static final String LOG_URL = "/caja-registradora/v1";
	private static final String LOG_URL_REGISTRO = "/caja-registradora/v1/registro";
	
	private static final String CAJA_REGISTRADORA = "CAJA_REGISTRADORA";
	private static final String CAJA_REGISTRADORA_ABRIR = "CAJA_REGISTRADORA_ABRIR";
	private static final String CAJA_REGISTRADORA_CREAR = "CAJA_REGISTRADORA_CREAR";
	private static final String CAJA_REGISTRADORA_EDITAR = "CAJA_REGISTRADORA_EDITAR";
	private static final String CAJA_REGISTRADORA_ELIMINAR = "CAJA_REGISTRADORA_ELIMINAR";
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private TrabajadorService trabajadorService;
	@Autowired
	private PlantelService plantelService;
	@Autowired
	private CajaRegistradoraService cajaRegistradoraService;
	
	/**
	 * 
	 * @param by:		NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, CURP, PUESTO, PLANTEL
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Trae todos las cajas registradoras paginados con estatus ACTIVO.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION, MARCA, MODELO <br/><b>order:</b> ASC, DESC")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping()
	public ResponseEntity<CajaRegistradoraPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA);
		if(sesion == null) {
			return new ResponseEntity<CajaRegistradoraPage>(HttpStatus.UNAUTHORIZED);
		}
		Sort sort = null;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			new ResponseEntity<CajaRegistradoraPage>(HttpStatus.BAD_REQUEST);
		}
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		Page<CajaRegistradora> response = null;
		if(search == null || search.isEmpty()) {
			response = cajaRegistradoraService.findByIdEmpresa(idEmpresa,elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				response = cajaRegistradoraService.findLikeNombre(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DESCRIPCION")) {
				response = cajaRegistradoraService.findLikeDescripcion(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("MARCA")) {
				response = cajaRegistradoraService.findLikeMarca(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("MODELO")) {
				response = cajaRegistradoraService.findLikeModelo(idEmpresa, text.toString(), elements, page, sort);
			}else {
				new ResponseEntity<CajaRegistradoraPage>(HttpStatus.BAD_REQUEST);
			}
		}
		CajaRegistradoraPage body = new CajaRegistradoraPage(response);
		return new ResponseEntity<CajaRegistradoraPage>(body, HttpStatus.OK);
	}

	
	@ApiOperation(value = "Persiste la entidad del tipo de caja registradora. ")
	@PostMapping
	public ResponseEntity<CajaRegistradora> save(@RequestParam(value="token") UUID token, @RequestBody CajaRegistradora cajaRegistradora){

		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA_CREAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<CajaRegistradora>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_NOMBRE, cajaRegistradora.getNombre())){
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_DESCRIPCION , cajaRegistradora.getDescripcion())){
			LOG.info("{}El formato de la descripcion es incorrecto.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(cajaRegistradora.getMarca() != null && cajaRegistradora.getMarca().isBlank()) {
			cajaRegistradora.setMarca(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_MARCA , cajaRegistradora.getMarca())){
				LOG.info("{}El formato de la marca es incorrecto.",headerLog);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
			}
		}
		if(cajaRegistradora.getModelo() != null && cajaRegistradora.getModelo().isBlank()) {
			cajaRegistradora.setModelo(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_MODELO , cajaRegistradora.getModelo())){
				LOG.info("{}El formato del modelo es incorrecto.",headerLog);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
			}
		}
		if(cajaRegistradora.getPlantel() == null || cajaRegistradora.getPlantel().getClave() == null) {
			Optional<Trabajador> optionalTrabajador = trabajadorService
					.findByClaveUsuario(sesion.getUsuario().getClave());
			if(optionalTrabajador.isEmpty()) {
				LOG.error("{}No se encontro el trabajador para el usuario {}.",headerLog, sesion.getUsuario().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			cajaRegistradora.setPlantel(optionalTrabajador.get().getPlantel());
		}else {
			Optional<Plantel> optionalPlantel = plantelService.findByClave(cajaRegistradora.getPlantel().getClave());
			if(optionalPlantel.isEmpty()) {
				LOG.info("{}No se encontro el plantel {}.",headerLog, cajaRegistradora.getPlantel().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			Plantel plantel = optionalPlantel.get();
			if(!plantel.getIdEmpresa().equals(idEmpresa)) {
				LOG.info("{}No se encontro el plantel {} para la empresa {}.",headerLog, cajaRegistradora.getPlantel().getClave(), idEmpresa);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			if(!plantel.getEstatus().equals(singletonUtil.getActivo())) {
				LOG.info("{}No se encuentra activado el plantel {}.",headerLog, cajaRegistradora.getPlantel().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			cajaRegistradora.setPlantel(plantel);
		}
		Catalogo ACTIVO = singletonUtil.getActivo();
		cajaRegistradora.setId(null);
		cajaRegistradora.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegistradora.setFechaDeModificacion(LocalDateTime.now());
		cajaRegistradora.setEstatus(ACTIVO);
		cajaRegistradora.setClave(UUID.randomUUID());
		cajaRegistradora = cajaRegistradoraService.save(cajaRegistradora);
		return new ResponseEntity<CajaRegistradora>(cajaRegistradora,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad de la caja registradora. ")
	@PutMapping
	public ResponseEntity<CajaRegistradora> update(@RequestParam(value="token") UUID token, @RequestBody CajaRegistradora cajaRegistradora){

		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA_EDITAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<CajaRegistradora>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPut(idEmpresa, LOG_URL);
		if(isNotValid(cajaRegistradora.getClave())) {
			LOG.info("{}No se mando la clave de la caja registradora.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_NOMBRE, cajaRegistradora.getNombre())){
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_DESCRIPCION , cajaRegistradora.getDescripcion())){
			LOG.info("{}El formato de la descripcion es incorrecto.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(cajaRegistradora.getMarca() != null && cajaRegistradora.getMarca().isBlank()) {
			cajaRegistradora.setMarca(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_MARCA , cajaRegistradora.getMarca())){
				LOG.info("{}El formato de la marca es incorrecto.",headerLog);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
			}
		}
		if(cajaRegistradora.getModelo() != null && cajaRegistradora.getModelo().isBlank()) {
			cajaRegistradora.setModelo(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_MODELO , cajaRegistradora.getModelo())){
				LOG.info("{}El formato del modelo es incorrecto.",headerLog);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
			}
		}
		if(cajaRegistradora.getPlantel() == null || cajaRegistradora.getPlantel().getClave() == null) {
			Optional<Trabajador> optionalTrabajador = trabajadorService
					.findByClaveUsuario(sesion.getUsuario().getClave());
			if(optionalTrabajador.isEmpty()) {
				LOG.error("{}No se encontro el trabajador para el usuario {}.",headerLog, sesion.getUsuario().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			cajaRegistradora.setPlantel(optionalTrabajador.get().getPlantel());
		}else {
			Optional<Plantel> optionalPlantel = plantelService.findByClave(cajaRegistradora.getPlantel().getClave());
			if(optionalPlantel.isEmpty()) {
				LOG.info("{}No se encontro el plantel {}.",headerLog, cajaRegistradora.getPlantel().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			Plantel plantel = optionalPlantel.get();
			if(!plantel.getIdEmpresa().equals(idEmpresa)) {
				LOG.info("{}No se encontro el plantel {} para la empresa {}.",headerLog, cajaRegistradora.getPlantel().getClave(), idEmpresa);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			if(!plantel.getEstatus().equals(singletonUtil.getActivo())) {
				LOG.info("{}No se encuentra activado el plantel {}.",headerLog, cajaRegistradora.getPlantel().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			cajaRegistradora.setPlantel(plantel);
		}
		
		Optional<CajaRegistradora> optionalCajaRegistradora = cajaRegistradoraService.findByClave(cajaRegistradora.getClave());
		if(optionalCajaRegistradora.isEmpty()) {
			LOG.info("{}No se encontro la caja registradora {}.",headerLog, cajaRegistradora.getClave());
			return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
		}
		CajaRegistradora cajaRegistradoraEdit = optionalCajaRegistradora.get();
		cajaRegistradoraEdit.setDescripcion(cajaRegistradora.getDescripcion());
		cajaRegistradoraEdit.setMarca(cajaRegistradora.getMarca());
		cajaRegistradoraEdit.setModelo(cajaRegistradora.getModelo());
		cajaRegistradoraEdit.setNombre(cajaRegistradora.getNombre());
		cajaRegistradoraEdit.setPlantel(cajaRegistradora.getPlantel());
		cajaRegistradoraEdit.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegistradoraEdit.setFechaDeModificacion(LocalDateTime.now());
		cajaRegistradoraEdit = cajaRegistradoraService.save(cajaRegistradora);
		return new ResponseEntity<CajaRegistradora>(cajaRegistradoraEdit,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad de la caja registradora por clave. ")
	@DeleteMapping
	public ResponseEntity<?> delete(@RequestParam(value="token") UUID token,@RequestParam(value="clave") UUID clave) {
		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA_ELIMINAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<CajaRegistradora>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogDelete(idEmpresa, LOG_URL);
		Optional<CajaRegistradora> optional = cajaRegistradoraService.findByClave(clave);
		if(optional.isEmpty()) {
			LOG.info("{}No se encontro la caja registradora {}.",headerLog, clave);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Catalogo ELIMINADO = singletonUtil.getEliminado();
		CajaRegistradora cajaRegistradora = optional.get();
		cajaRegistradora.setEstatus(ELIMINADO);
		cajaRegistradora.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegistradora.setFechaDeModificacion(LocalDateTime.now());
		cajaRegistradoraService.update(cajaRegistradora);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
