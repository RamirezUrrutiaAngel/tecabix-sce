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
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("trabajador/v1")
public class TrabajadorControllerV01 extends Auth{

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
	
	private final String ESTATUS = "ESTATUS";
	private final String PENDIENTE = "PENDIENTE";
	private final String ACTIVO = "ACTIVO";
	private final String ELIMINADO = "ELIMINADO";
	
	private final String TIPO_DE_PERSONA = "TIPO_DE_PERSONA";
	private final String FISICA = "FISICA";
	
	private final String SEXO = "SEXO";
	
	private final String TRABAJADOR = "TRABAJADOR";
	private final String TRABAJADOR_CREAR = "TRABAJADOR_CREAR";
	private final String TRABAJADOR_ACTIVAR = "TRABAJADOR_ACTIVAR";
	private final String TRABAJADOR_ELIMINAR = "TRABAJADOR_ELIMINAR";
	
	
	
	@ApiOperation(value = "Devuelve los datos del trabajador que realiza la petición.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping
	public ResponseEntity<Trabajador> findByUsuario(@RequestParam(value="token") String token) {
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Trabajador> opcionalTrabajador =  trabajadorService.findByUsuario(sesion.getUsuario().getNombre());
		if(!opcionalTrabajador.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		Trabajador trabajador = opcionalTrabajador.get();
		return new ResponseEntity<Trabajador>(trabajador,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Trae todos los trabajadores paginados con estatus ACTIVO.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping("findAll")
	public ResponseEntity<Page<Trabajador>> findAll(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {

		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR);
		if(sesion == null) {
			return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		}
		
		Page<Trabajador> trabajador =  trabajadorService.findAll(sesion.getLicencia().getPlantel().getIdEscuela(),elements, page);
		return new ResponseEntity<Page<Trabajador>>(trabajador,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Trae los trabajadores por coincidencia de nombre")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping("findAllByNombre")
	public ResponseEntity<Page<Trabajador>> findAllByNombre(@RequestParam(value="token") String token, @RequestParam(value="nombre") String nombre,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR);
		if(sesion == null) {
			return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Trabajador> trabajador =  trabajadorService.findAllByNombre(sesion.getLicencia().getPlantel().getIdEscuela(),nombre,elements, page);
		return new ResponseEntity<Page<Trabajador>>(trabajador,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Dar de alta un nuevo trabajador", notes = "Dar de alta un trabajador nuevo en una empresa ya existente, pero no se encontrara habilitado hasta que se active con el serivicio trabajador/activar.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 400, message = "Faltan datos para poder procesar la petición o no son validos."),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso."),
			@ApiResponse(code = 406, message = "Uno o varios datos ingresados no son validos para procesar la petición."),
			@ApiResponse(code = 409, message = "La petición no pudo realizarse por que el usuario que se intenta guardar ya existe.") })
	@PostMapping
	public ResponseEntity<Trabajador> save(@RequestBody Trabajador trabajador, @RequestParam(value="token") String token) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED); 
		}
		if(trabajador == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getCURP() == null || trabajador.getCURP().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getJefe() == null || trabajador.getJefe().getId() == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getPuesto() == null || trabajador.getPuesto().getId() == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		
		PersonaFisica persona = trabajador.getPersonaFisica();
		if(persona == null ) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(persona.getNombre() == null || persona.getNombre().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(persona.getSexo() == null || persona.getSexo().getNombre() == null || persona.getSexo().getNombre().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(persona.getFechaNacimiento() == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(persona.getApellidoMaterno() == null || persona.getApellidoMaterno().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(persona.getApellidoPaterno() == null || persona.getApellidoPaterno().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		Direccion direccion = persona.getDireccion();
		if(direccion == null ) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getCalle() == null || direccion.getCalle().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getCodigoPostal() == null || direccion.getCodigoPostal().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getAsentamiento() == null || direccion.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getNumExt() == null || direccion.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(direccion.getMunicipio() == null || direccion.getMunicipio().getId() == null ) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		
		
		Optional<Catalogo> optionalCatalogoSexo = catalogoService.findByTipoAndNombre(SEXO, persona.getSexo().getNombre());
		Optional<Catalogo> optionalCatalogoPendinte = catalogoService.findByTipoAndNombre(ESTATUS, PENDIENTE);
		Optional<Catalogo> optionalCatalogoTipoPersona = catalogoService.findByTipoAndNombre(TIPO_DE_PERSONA, FISICA);
		
		if(!optionalCatalogoSexo.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(!optionalCatalogoPendinte.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(!optionalCatalogoTipoPersona.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_SEXO = optionalCatalogoSexo.get();
		final Catalogo CAT_PENDIENTE = optionalCatalogoPendinte.get();
		final Catalogo CAT_TIPO_PERSONA = optionalCatalogoTipoPersona.get();
		
		Optional<Municipio> municipioOptional = municipioService.findById(direccion.getMunicipio().getId());
		if(!municipioOptional.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipio = municipioOptional.get();
		
		Optional<Trabajador> opcionalTrabajador =  trabajadorService.findByKey(trabajador.getJefe().getId());
		if(!opcionalTrabajador.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		Trabajador jefe = opcionalTrabajador.get();
		
		Plantel plantel = trabajador.getPlantel();
		if(plantel != null) {
			Optional<Plantel> optionalPlantel = plantelService.findById(plantel.getId());
			if(optionalPlantel.isPresent()) {
				plantel = optionalPlantel.get();
				if(!plantel.getIdEscuela().equals(sesion.getLicencia().getPlantel().getIdEscuela())) {
					plantel = null;
				}
			}else {
				plantel = null;
			}
		}
		
		direccion.setEstatus(CAT_PENDIENTE);
		direccion.setMunicipio(municipio);
		direccion.setFechaDeModificacion(LocalDateTime.now());
		direccion.setIdUsuarioModificado(sesion.getUsuario().getId());
		direccion = direccionService.save(direccion);
		
		Persona prs=new Persona();
		prs.setTipo(CAT_TIPO_PERSONA);
		prs.setIdUsuarioModificado(sesion.getUsuario().getId());
		prs.setFechaDeModificacion(LocalDateTime.now());
		prs.setEstatus(CAT_PENDIENTE);
		prs = personaService.save(prs);
		persona.setPresona(prs);
		persona.setDireccion(direccion);
		persona.setSexo(CAT_SEXO);
		persona.setFechaDeModificacion(LocalDateTime.now());
		persona.setIdUsuarioModificado(sesion.getUsuario().getId());
		persona.setEstatus(CAT_PENDIENTE);
		
		persona = personaFisicaService.save(persona);
		trabajador.setEstatus(CAT_PENDIENTE);
		trabajador.setFechaDeModificacion(LocalDateTime.now());
		trabajador.setIdUsuarioModificado(sesion.getUsuario().getId());
		trabajador.setIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela());
		trabajador.setJefe(jefe);
		trabajador.setPersonaFisica(persona);
		trabajador.setPlantel(plantel);
		trabajadorService.save(trabajador);
		return new ResponseEntity<Trabajador>(trabajador, HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Activar trabajador dado de alta",
			notes = "Activa el trabajador con sus corespondientes usuario y direccion")
	@ApiResponses(value = {
				@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
				@ApiResponse(code = 400, message = "Faltan datos para poder procesar la petición o no son validos."),
				@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso."),
				@ApiResponse(code = 406, message = "Uno o varios datos ingresados no son validos para procesar la petición."),
				@ApiResponse(code = 409, message = "La petición no pudo realizarse por que el usuario que se intenta guardar ya existe.")
			})
	@PutMapping("activar")
	public ResponseEntity<Trabajador> activar(@RequestParam(value="id") Long id, @RequestParam(value="token") String token) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR_ACTIVAR);
		if(sesion == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED); 
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ACTIVO = optionalCatalogoActivo.get();
		
		
		Optional<Trabajador> opcionalTrabajador =  trabajadorService.findByIdAndPendiente(id);
		if(!opcionalTrabajador.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}

		Trabajador trabajador = opcionalTrabajador.get();
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		
		if(trabajador.getIdEscuela().longValue() != idEscuela.longValue()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		PersonaFisica personaFisica = trabajador.getPersonaFisica();
		personaFisica.setEstatus(CAT_ACTIVO);
		Persona persona = personaFisica.getPresona();
		persona.setEstatus(CAT_ACTIVO);
		persona = personaService.update(persona);
		personaFisica.setPresona(persona);
		Direccion direccion = personaFisica.getDireccion();
		direccion.setEstatus(CAT_ACTIVO);
		direccionService.update(direccion);
		personaFisica.setDireccion(direccion);
		personaFisica = personaFisicaService.update(personaFisica);
		trabajador.setPersonaFisica(personaFisica);
		trabajadorService.update(trabajador);
		return new ResponseEntity<Trabajador>(trabajador, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Eliminar trabajador y dependencias")
	@DeleteMapping("delete")
	public ResponseEntity<Trabajador> delete(@RequestParam(value="id") Long id, @RequestParam(value="token") String token) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR_ELIMINAR);
		if(sesion == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED); 
		}
		
		Optional<Catalogo> optionalCatalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		if(!optionalCatalogoEliminado.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ELIMINADO = optionalCatalogoEliminado.get();
		

		Optional<Trabajador> opcionalTrabajador = trabajadorService.findByKey(id);
		if(!opcionalTrabajador.isPresent()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		Trabajador trabajador = opcionalTrabajador.get();
		
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		if(trabajador.getIdEscuela().longValue() != idEscuela.longValue()) {
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
		direccionService.update(direccion);
		personaFisica.setDireccion(direccion);
		personaFisica = personaFisicaService.update(personaFisica);
		trabajador.setPersonaFisica(personaFisica);
		trabajadorService.update(trabajador);
		return new ResponseEntity<Trabajador>(trabajador, HttpStatus.OK);
	}
	
}
