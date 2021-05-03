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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.TurnoService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;

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
	
	private final String TURNO = "TURNO";
	private final String TURNO_CREAR = "TURNO_CREAR";
	private final String TURNO_EDITAR = "TURNO_EDITAR";
	private final String TURNO_ELIMINAR = "TURNO_ELIMINAR";

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private TurnoService turnoService;
	@Autowired
	private CatalogoService catalogoService;
	
	@ApiOperation(value = "Dar de alta un nuevo trabajador", notes = "Dar de alta un trabajador nuevo en una empresa ya existente.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 400, message = "Faltan datos para poder procesar la petición o no son validos."),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso."),
			@ApiResponse(code = 406, message = "Uno o varios datos ingresados no son validos para procesar la petición."),
			@ApiResponse(code = 409, message = "La petición no pudo realizarse por que el usuario que se intenta guardar ya existe.") })
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
		Optional<Catalogo> optionalCatalogo = catalogoService.findByTipoAndNombre(TURNO, turno.getTipo().getNombre());
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No se encontro el tipo del turno.",headerLog);
			return new ResponseEntity<Turno>(HttpStatus.BAD_REQUEST);
		}
		turno.setId(null);
		turno.setTipo(optionalCatalogo.get());
		turno.setIdEmpresa(idEmpresa);
		turno.setClave(UUID.randomUUID());
		turno.setEstatus(singletonUtil.getActivo());
		turno.setFechaDeModificacion(LocalDateTime.now());
		turno.setIdUsuarioModificado(sesion.getUsuario().getId());
		turno = turnoService.save(turno);
		return new ResponseEntity<Turno>(turno,HttpStatus.OK);
	}
}
