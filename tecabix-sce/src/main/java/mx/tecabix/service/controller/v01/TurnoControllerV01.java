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
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.entity.Turno;
import mx.tecabix.db.entity.TurnoDia;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.TurnoDiaService;
import mx.tecabix.db.service.TurnoService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.TurnoPage;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("turno/v1")
public final class TurnoControllerV01 extends Auth{

	private static final Logger LOG = LoggerFactory.getLogger(TurnoControllerV01.class);
	private static final String LOG_URL = "/turno/v1";
	
	
	private final String DIA_DE_LA_SEMANA = "DIA_DE_LA_SEMANA";
	
	private final String TURNO = "TURNO";
	private final String TURNO_CREAR = "TURNO_CREAR";
	private final String TURNO_EDITAR = "TURNO_EDITAR";
	private final String TURNO_ELIMINAR = "TURNO_ELIMINAR";

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private TurnoService turnoService;
	@Autowired
	private TurnoDiaService turnoDiaService;
	@Autowired
	private CatalogoService catalogoService;
	
	
	/**
	 * 
	 * @param by:		NOMBRE, DESCRIPCION
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los Turnos paginado.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION<br/><b>order:</b> ASC, DESC")
	@GetMapping
	public ResponseEntity<TurnoPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token,TURNO);
		if(sesion == null) {
			return new ResponseEntity<TurnoPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Turno> pageTurno = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<TurnoPage>(HttpStatus.BAD_REQUEST);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		if(search == null || search.isEmpty()) {
			pageTurno = turnoService.findByIdEmpresa(idEmpresa, elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				pageTurno = turnoService.findByLikeNombre(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DESCRIPCION")) {
				pageTurno = turnoService.findByLikeDescripcion(idEmpresa, text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<TurnoPage>(HttpStatus.BAD_REQUEST);
			}
		}
		TurnoPage body = new TurnoPage(pageTurno);
		ResponseEntity<TurnoPage> response = new ResponseEntity<TurnoPage>(body, HttpStatus.OK);
		return response;
	}
	
	@ApiOperation(value = "Dar de alta un nuevo turno")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 400, message = "Faltan datos para poder procesar la petición o no son validos."),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso."),
			@ApiResponse(code = 406, message = "Uno o varios datos ingresados no son validos para procesar la petición."),
			@ApiResponse(code = 423, message = "La petición no pudo realizarse por que se supero el numero de turnos creados.") })
	@PostMapping
	public ResponseEntity<Turno>  save(@RequestBody Turno turno, @RequestParam(value="token") UUID token) {
		Sesion sesion = getSessionIfIsAuthorized(token, TURNO_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Turno>(HttpStatus.UNAUTHORIZED); 
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		final boolean canInsert = turnoService.canInsert(idEmpresa);
		if(!canInsert) {
			LOG.info("{}Se a superado el numero máximo de turnos.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.LOCKED);
		}
		
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Turno.SIZE_NOMBRE, turno.getNombre())) {
			LOG.info("{}El valor del nombre del turno no es valido.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}else {
			turno.setNombre(turno.getNombre().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Turno.SIZE_DESCRIPCION, turno.getDescripcion())) {
			LOG.info("{}El valor de la descripcion del turno no es valido.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}else {
			turno.setDescripcion(turno.getDescripcion().strip());
		}
		if(isNotValid(turno.getTipo())) {
			LOG.info("{}No se mando el tipo del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(turno.getTipo().getNombre())) {
			LOG.info("{}No se mando el nombre del tipo del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(turno.getTurnoDias())) {
			LOG.info("{}No se mando los dias del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		Optional<Catalogo> optionalCatalogo = null;
		Catalogo CAT_ACTIVO = singletonUtil.getActivo();
		List<TurnoDia> turnoDias = turno.getTurnoDias();
		for (TurnoDia turnoDia : turnoDias) {
			if(isNotValid(turnoDia.getInicio())) {
				LOG.info("{}No se mando el inicio del turno del dia.",headerLog);
				return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(turnoDia.getFin())) {
				LOG.info("{}No se mando el fin del turno del dia.",headerLog);
				return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(turnoDia.getDia())) {
				LOG.info("{}No se mando el dia para el turno del dia.",headerLog);
				return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(turnoDia.getDia().getNombre())) {
				LOG.info("{}No se mando el nombre del dia para el turno del dia.",headerLog);
				return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
			}
			optionalCatalogo = catalogoService.findByTipoAndNombre(DIA_DE_LA_SEMANA, turnoDia.getDia().getNombre());
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No se encontro el nombre del dia para el turno del dia.",headerLog);
				return new ResponseEntity<Turno>(HttpStatus.NOT_FOUND);
			}
			turnoDia.setDia(optionalCatalogo.get());
			turnoDia.setId(null);
			turnoDia.setClave(UUID.randomUUID());
			turnoDia.setEstatus(CAT_ACTIVO);
			turnoDia.setFechaDeModificacion(LocalDateTime.now());
			turnoDia.setIdUsuarioModificado(sesion.getUsuario().getId());
		}
		
		optionalCatalogo = catalogoService.findByTipoAndNombre(TURNO, turno.getTipo().getNombre());
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No se encontro el tipo del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		
		turno.setId(null);
		turno.setTipo(optionalCatalogo.get());
		turno.setIdEmpresa(idEmpresa);
		turno.setClave(UUID.randomUUID());
		turno.setEstatus(CAT_ACTIVO);
		turno.setFechaDeModificacion(LocalDateTime.now());
		turno.setIdUsuarioModificado(sesion.getUsuario().getId());
		turno = turnoService.save(turno);
		
		for (TurnoDia turnoDia : turnoDias) {
			turnoDia.setTurno(turno);
			turnoDia = turnoDiaService.save(turnoDia);
		}
		turno.setTurnoDias(turnoDias);
		return new ResponseEntity<Turno>(turno,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Actualizar un turno")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 400, message = "Faltan datos para poder procesar la petición o no son validos."),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso."),
			@ApiResponse(code = 406, message = "Uno o varios datos ingresados no son validos para procesar la petición."),
			@ApiResponse(code = 423, message = "La petición no pudo realizarse por que se supero el numero de turnos creados.") })
	@PutMapping
	public ResponseEntity<Turno>  update(@RequestBody Turno turno, @RequestParam(value="token") UUID token) {
		Sesion sesion = getSessionIfIsAuthorized(token, TURNO_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<Turno>(HttpStatus.UNAUTHORIZED); 
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPut(idEmpresa, LOG_URL);
		
		if(isNotValid(turno.getClave())) {
			LOG.info("{}No se mando la clave.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Turno.SIZE_NOMBRE, turno.getNombre())) {
			LOG.info("{}El valor del nombre del turno no es valido.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}else {
			turno.setNombre(turno.getNombre().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Turno.SIZE_DESCRIPCION, turno.getDescripcion())) {
			LOG.info("{}El valor de la descripcion del turno no es valido.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}else {
			turno.setDescripcion(turno.getDescripcion().strip());
		}
		if(isNotValid(turno.getTipo())) {
			LOG.info("{}No se mando el tipo del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(turno.getTipo().getNombre())) {
			LOG.info("{}No se mando el nombre del tipo del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(turno.getTurnoDias())) {
			LOG.info("{}No se mando los dias del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		Optional<Catalogo> optionalCatalogo = null;
		Catalogo CAT_ACTIVO = singletonUtil.getActivo();
		List<TurnoDia> turnoDias = turno.getTurnoDias();
		{
			List<TurnoDia> turnoDiasUpdate = new ArrayList<TurnoDia>(turnoDias.size());
			for (TurnoDia turnoDia : turnoDias) {
				if(isNotValid(turnoDia.getInicio())) {
					LOG.info("{}No se mando el inicio del turno del dia.",headerLog);
					return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(turnoDia.getFin())) {
					LOG.info("{}No se mando el fin del turno del dia.",headerLog);
					return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(turnoDia.getDia())) {
					LOG.info("{}No se mando el dia para el turno del dia.",headerLog);
					return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(turnoDia.getDia().getNombre())) {
					LOG.info("{}No se mando el nombre del dia para el turno del dia.",headerLog);
					return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
				}
				optionalCatalogo = catalogoService.findByTipoAndNombre(DIA_DE_LA_SEMANA, turnoDia.getDia().getNombre());
				if(optionalCatalogo.isEmpty()) {
					LOG.info("{}No se encontro el nombre del dia para el turno del dia.",headerLog);
					return new ResponseEntity<Turno>(HttpStatus.NOT_FOUND);
				}
				turnoDia.setDia(optionalCatalogo.get());
				
				if(turnoDia.getClave() != null) {
					Optional<TurnoDia> optionalTurnoDia = turnoDiaService.findByClave(turnoDia.getClave());
					if(optionalTurnoDia.isEmpty()) {
						LOG.info("{}No se encontro el turno dia.",headerLog);
						return new ResponseEntity<Turno>(HttpStatus.NOT_FOUND);
					}
					TurnoDia turnoDiaUpdate = optionalTurnoDia.get();
					if(!turnoDiaUpdate.getEstatus().equals(CAT_ACTIVO)) {
						LOG.info("{}No se encontro el turno dia no esta activo.",headerLog);
						return new ResponseEntity<Turno>(HttpStatus.NOT_FOUND);
					}
					turnoDiaUpdate.setInicio(turnoDia.getInicio());
					turnoDiaUpdate.setFin(turnoDia.getFin());
					turnoDiaUpdate.setDia(turnoDia.getDia());
					turnoDia = turnoDiaUpdate;
				}else {
					turnoDia.setId(null);
					turnoDia.setEstatus(CAT_ACTIVO);
					turnoDia.setClave(UUID.randomUUID());
				}
				turnoDia.setFechaDeModificacion(LocalDateTime.now());
				turnoDia.setIdUsuarioModificado(sesion.getUsuario().getId());
				turnoDiasUpdate.add(turnoDia);
			}
			turnoDias = turnoDiasUpdate;
		}
		optionalCatalogo = catalogoService.findByTipoAndNombre(TURNO, turno.getTipo().getNombre());
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No se encontro el tipo del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.NOT_FOUND);
		}
		Optional<Turno> optionalTurno = turnoService.findByClave(turno.getClave());
		if(optionalTurno.isEmpty()) {
			LOG.info("{}No se encontro el turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.NOT_FOUND);
		}
		Turno turnoUpdate = optionalTurno.get();
		if(!turnoUpdate.getEstatus().equals(CAT_ACTIVO)) {
			LOG.info("{}El turno no esta activo.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.NOT_FOUND);
		}
		if(!turnoUpdate.getIdEmpresa().equals(idEmpresa)) {
			LOG.info("{}El turno no pertenece a la empresa.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.NOT_FOUND);
		}
		turnoUpdate.setNombre(turno.getNombre());
		turnoUpdate.setDescripcion(turno.getDescripcion());
		turnoUpdate.setTipo(optionalCatalogo.get());
		turnoUpdate.setFechaDeModificacion(LocalDateTime.now());
		turnoUpdate.setIdUsuarioModificado(sesion.getUsuario().getId());
		
		turnoUpdate = turnoService.save(turnoUpdate);
		
		for (TurnoDia turnoDia : turnoDias) {
			turnoDia.setTurno(turnoUpdate);
			if(turno.getClave()!=null) {
				turnoDia = turnoDiaService.save(turnoDia);
			}else {
				turnoDia = turnoDiaService.update(turnoDia);
			}
		}
		turnoUpdate.setTurnoDias(turnoDias);
		return new ResponseEntity<Turno>(turnoUpdate,HttpStatus.OK);
	}
	
}
