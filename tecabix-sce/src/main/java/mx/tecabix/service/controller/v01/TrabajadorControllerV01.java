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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Direccion;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.entity.PersonaFisica;
import mx.tecabix.db.entity.Plantel;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.DireccionService;
import mx.tecabix.db.service.MunicipioService;
import mx.tecabix.db.service.PersonaFisicaService;
import mx.tecabix.db.service.PersonaService;
import mx.tecabix.db.service.PlantelService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.TrabajadorPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("trabajador/v1")
public class TrabajadorControllerV01 extends Auth{

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private TrabajadorService trabajadorService;
	@Autowired
	private PersonaService personaService;
	@Autowired
	private PersonaFisicaService personaFisicaService;
	@Autowired
	private MunicipioService municipioService;
	@Autowired
	private DireccionService direccionService;
	@Autowired
	private PlantelService plantelService;
	
	private final String TIPO_DE_PERSONA = "TIPO_DE_PERSONA";
	private final String FISICA = "FISICA";
	
	private final String SEXO = "SEXO";
	
	private final String TRABAJADOR = "TRABAJADOR";
	private final String TRABAJADOR_CREAR = "TRABAJADOR_CREAR";
	private final String TRABAJADOR_ELIMINAR = "TRABAJADOR_ELIMINAR";
	
	/**
	 * 
	 * @param by:		NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, CURP, PUESTO, PLANTEL
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Trae todos los trabajadores paginados con estatus ACTIVO.", 
			notes = "<b>by:</b> NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, CURP, PUESTO, PLANTEL<br/><b>order:</b> ASC, DESC")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping()
	public ResponseEntity<TrabajadorPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR);
		if(sesion == null) {
			return new ResponseEntity<TrabajadorPage>(HttpStatus.UNAUTHORIZED);
		}
		Sort sort = null;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			new ResponseEntity<TrabajadorPage>(HttpStatus.BAD_REQUEST);
		}
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		Page<Trabajador> response = null;
		if(search == null || search.isEmpty()) {
			response = trabajadorService.findByIdEmpresa(idEmpresa,elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				response = trabajadorService.findByLikeNombre(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("APELLIDO_PATERNO")) {
				response = trabajadorService.findByLikeApellidoPaterno(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("APELLIDO_MATERNO")) {
				response = trabajadorService.findByLikeApellidoMaterno(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("CURP")) {
				response = trabajadorService.findByLikeCURP(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("PUESTO")) {
				response = trabajadorService.findByLikePuesto(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("PLANTEL")) {
				response = trabajadorService.findByLikePlantel(idEmpresa, text.toString(), elements, page, sort);
			}else {
				new ResponseEntity<TrabajadorPage>(HttpStatus.BAD_REQUEST);
			}
		}
		TrabajadorPage body = new TrabajadorPage(response);
		return new ResponseEntity<TrabajadorPage>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Dar de alta un nuevo trabajador", notes = "Dar de alta un trabajador nuevo en una empresa ya existente.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 400, message = "Faltan datos para poder procesar la petición o no son validos."),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso."),
			@ApiResponse(code = 406, message = "Uno o varios datos ingresados no son validos para procesar la petición."),
			@ApiResponse(code = 409, message = "La petición no pudo realizarse por que el usuario que se intenta guardar ya existe.") })
	@PostMapping
	public ResponseEntity<Trabajador> save(@RequestBody Trabajador trabajador, @RequestParam(value="token") UUID token) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED); 
		}
		if(trabajador == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC, Trabajador.SIZE_CURP, trabajador.getCURP())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(trabajador.getJefe())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(trabajador.getJefe().getClave())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isValid(trabajador.getPlantel())) {
			if(isNotValid(trabajador.getPlantel().getClave())) {
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
		}
		if(isNotValid(trabajador.getPuesto())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(trabajador.getPuesto().getClave())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		PersonaFisica persona = trabajador.getPersonaFisica();
		if(isNotValid(persona) ) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, PersonaFisica.SIZE_NOMBRE, persona.getNombre())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			persona.setNombre(persona.getNombre().strip());
		}
		if(isNotValid(persona.getSexo())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(persona.getSexo().getNombre())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(persona.getFechaNacimiento())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA, PersonaFisica.SIZE_APELLIDO_MATERNO, persona.getApellidoMaterno())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA, PersonaFisica.SIZE_APELLIDO_PATERNO, persona.getApellidoPaterno())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		Direccion direccion = persona.getDireccion();
		if(isNotValid(direccion)) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC, Direccion.SIZE_CALLE, direccion.getCalle())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC, Direccion.SIZE_CODIGO_POSTAL, direccion.getCodigoPostal())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Direccion.SIZE_ASENTAMIENTO, direccion.getAsentamiento())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setAsentamiento(direccion.getAsentamiento().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Direccion.SIZE_NUM_EXT, direccion.getNumExt())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setNumExt(direccion.getNumExt().strip());
		}
		if(isValid(direccion.getNumInt())) {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Direccion.SIZE_NUM_INT, direccion.getNumInt())) {
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}else {
				direccion.setNumInt(direccion.getNumInt().strip());
			}
		}
		if(isNotValid(direccion.getMunicipio())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(direccion.getMunicipio().getClave())) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		
		Optional<Catalogo> optionalCatalogoSexo = catalogoService.findByTipoAndNombre(SEXO, persona.getSexo().getNombre());
		Optional<Catalogo> optionalCatalogoTipoPersona = catalogoService.findByTipoAndNombre(TIPO_DE_PERSONA, FISICA);
		
		if(optionalCatalogoSexo.isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		
		if(optionalCatalogoTipoPersona.isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_SEXO = optionalCatalogoSexo.get();
		final Catalogo CAT_ACTIVO = singletonUtil.getActivo();
		final Catalogo CAT_TIPO_PERSONA = optionalCatalogoTipoPersona.get();
		
		Optional<Municipio> municipioOptional = municipioService.findByClave(direccion.getMunicipio().getClave());
		if(municipioOptional.isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipio = municipioOptional.get();
		
		Optional<Trabajador> opcionalTrabajador =  trabajadorService.findByClave(trabajador.getJefe().getClave());
		if(opcionalTrabajador.isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		Trabajador jefe = opcionalTrabajador.get();
		if(jefe.getPlantel().getIdEmpresa().equals(sesion.getLicencia().getPlantel().getIdEmpresa())) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		Plantel plantel = trabajador.getPlantel();
		if(isValid(plantel)) {
			Optional<Plantel> optionalPlantel = plantelService.findByClave(plantel.getClave());
			if(optionalPlantel.isPresent()) {
				plantel = optionalPlantel.get();
				if(!plantel.getIdEmpresa().equals(sesion.getLicencia().getPlantel().getIdEmpresa())) {
					return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
				}
			}else {
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
			}
		}
		direccion.setClave(UUID.randomUUID());
		direccion.setEstatus(CAT_ACTIVO);
		direccion.setMunicipio(municipio);
		direccion.setFechaDeModificacion(LocalDateTime.now());
		direccion.setIdUsuarioModificado(sesion.getUsuario().getId());
		direccion = direccionService.save(direccion);
		
		Persona prs=new Persona();
		prs.setClave(UUID.randomUUID());
		prs.setTipo(CAT_TIPO_PERSONA);
		prs.setIdUsuarioModificado(sesion.getUsuario().getId());
		prs.setFechaDeModificacion(LocalDateTime.now());
		prs.setEstatus(CAT_ACTIVO);
		prs = personaService.save(prs);
		persona.setClave(UUID.randomUUID());
		persona.setPresona(prs);
		persona.setDireccion(direccion);
		persona.setSexo(CAT_SEXO);
		persona.setFechaDeModificacion(LocalDateTime.now());
		persona.setIdUsuarioModificado(sesion.getUsuario().getId());
		persona.setEstatus(CAT_ACTIVO);
		
		persona = personaFisicaService.save(persona);
		trabajador.setClave(UUID.randomUUID());
		trabajador.setEstatus(CAT_ACTIVO);
		trabajador.setFechaDeModificacion(LocalDateTime.now());
		trabajador.setIdUsuarioModificado(sesion.getUsuario().getId());
		trabajador.setIdEmpresa(sesion.getLicencia().getPlantel().getIdEmpresa());
		trabajador.setJefe(jefe);
		trabajador.setPersonaFisica(persona);
		trabajador.setPlantel(plantel);
		trabajador = trabajadorService.save(trabajador);
		return new ResponseEntity<Trabajador>(trabajador, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Eliminar trabajador y dependencias")
	@DeleteMapping("delete")
	public ResponseEntity<Trabajador> delete(@RequestParam(value="clave") UUID clave, @RequestParam(value="token") UUID token) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR_ELIMINAR);
		if(sesion == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED); 
		}
		final Catalogo CAT_ELIMINADO = singletonUtil.getEliminado();

		Optional<Trabajador> opcionalTrabajador = trabajadorService.findByClave(clave);
		if(opcionalTrabajador.isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		Trabajador trabajador = opcionalTrabajador.get();
		
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		if(trabajador.getIdEmpresa().longValue() != idEmpresa.longValue()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		PersonaFisica personaFisica = trabajador.getPersonaFisica();
		personaFisica.setEstatus(CAT_ELIMINADO);
		Persona persona = personaFisica.getPresona();
		persona.setEstatus(CAT_ELIMINADO);
		persona = personaService.update(persona);
		personaFisica.setPresona(persona);
		Direccion direccion = personaFisica.getDireccion();
		direccion.setEstatus(CAT_ELIMINADO);
		direccion = direccionService.update(direccion);
		personaFisica.setDireccion(direccion);
		personaFisica = personaFisicaService.update(personaFisica);
		trabajador.setPersonaFisica(personaFisica);
		trabajador = trabajadorService.update(trabajador);
		return new ResponseEntity<Trabajador>(trabajador, HttpStatus.OK);
	}
}
