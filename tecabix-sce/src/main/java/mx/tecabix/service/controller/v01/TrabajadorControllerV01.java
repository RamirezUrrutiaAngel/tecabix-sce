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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import mx.tecabix.db.entity.Configuracion;
import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.entity.CorreoMsj;
import mx.tecabix.db.entity.CorreoMsjItem;
import mx.tecabix.db.entity.Direccion;
import mx.tecabix.db.entity.Empresa;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.entity.Perfil;
import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.entity.PersonaFisica;
import mx.tecabix.db.entity.Plantel;
import mx.tecabix.db.entity.Puesto;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.entity.UsuarioPersona;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.CorreoMsjItemService;
import mx.tecabix.db.service.CorreoMsjService;
import mx.tecabix.db.service.CorreoService;
import mx.tecabix.db.service.DireccionService;
import mx.tecabix.db.service.EmpresaService;
import mx.tecabix.db.service.MunicipioService;
import mx.tecabix.db.service.PerfilService;
import mx.tecabix.db.service.PersonaFisicaService;
import mx.tecabix.db.service.PersonaService;
import mx.tecabix.db.service.PlantelService;
import mx.tecabix.db.service.PuestoService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.db.service.UsuarioPersonaService;
import mx.tecabix.db.service.UsuarioService;
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
public final class TrabajadorControllerV01 extends Auth{
	private static final Logger LOG = LoggerFactory.getLogger(TrabajadorControllerV01.class);
	private static final String LOG_URL = "/trabajador/v1";

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
	@Autowired
	private PuestoService puestoService;
	@Autowired
	private PerfilService perfilService;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private UsuarioPersonaService usuarioPersonaService;
	@Autowired
	private EmpresaService empresaService;
	@Autowired
	private CorreoService correoService;
	@Autowired
	private CorreoMsjService correoMsjService;
	@Autowired
	private CorreoMsjItemService correoMsjItemService;
	
	private final String TIPO_DE_PERSONA = "TIPO_DE_PERSONA";
	private final String FISICA = "FISICA";
	
	private final String ALTA_USR_CORREO	= "ALTA_USR_CORREO";
	private final String ALTA_USR_ASUNTO	= "ALTA_USR_ASUNTO";
	private final String ALTA_USR_PLANTILLA	= "ALTA_USR_PLANTILLA";
	
	private final String TIPO_DE_CORREO		= "TIPO_DE_CORREO";
	private final String CONFIRMAR_CORREO	= "CONFIRMAR_CORREO";
	
	
	private final String NOMBRE					= "[NOMBRE]";
	private final String USUARIO				= "[USUARIO]";
	private final String PASSWORD				= "[PASSWORD]";
	private final String RAZON_SOCIAL			= "[RAZON_SOCIAL]";
	private final String ID_TRABAJADOR			= "[ID_TRABAJADOR]";
	private final String ID_EMPRESA				= "[ID_EMPRESA]";
	private final String TIPO_ELEMENTO_CORREO	= "TIPO_ELEMENTO_CORREO";
	
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
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		final boolean canInsert = trabajadorService.canInsert(idEmpresa);
		if(!canInsert) {
			LOG.info("{}Se a superado el numero máximo de trabajadores.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.LOCKED);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC, Trabajador.SIZE_CURP, trabajador.getCURP())) {
			LOG.info("{}El formato de la curp es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(trabajador.getJefe())) {
			LOG.info("{}No se mando el jefe.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(trabajador.getJefe().getClave())) {
			LOG.info("{}No se mando la clave del jefe.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isValid(trabajador.getPlantel())) {
			if(isNotValid(trabajador.getPlantel().getClave())) {
				LOG.info("{}No se mando la clave del plantel.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
		}
		if(isNotValid(trabajador.getPuesto())) {
			LOG.info("{}No se mando el puesto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(trabajador.getPuesto().getClave())) {
			LOG.info("{}No se mando la clave del puesto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		PersonaFisica persona = trabajador.getPersonaFisica();
		if(isNotValid(persona) ) {
			LOG.info("{}No se mando la persona fisica.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, PersonaFisica.SIZE_NOMBRE, persona.getNombre())) {
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			persona.setNombre(persona.getNombre().strip());
		}
		if(isNotValid(persona.getSexo())) {
			LOG.info("{}No se mando el sexo.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(persona.getSexo().getNombre())) {
			LOG.info("{}No se mando el nombre del sexo.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(persona.getFechaNacimiento())) {
			LOG.info("{}No se mando la fecha de nacimiento.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA, PersonaFisica.SIZE_APELLIDO_MATERNO, persona.getApellidoMaterno())) {
			LOG.info("{}El formato del apellido materno es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA, PersonaFisica.SIZE_APELLIDO_PATERNO, persona.getApellidoPaterno())) {
			LOG.info("{}El formato del apellido paterno es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		Direccion direccion = persona.getDireccion();
		if(isNotValid(direccion)) {
			LOG.info("{}No se mando la direccion.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_CALLE, direccion.getCalle())) {
			LOG.info("{}El formato de la calle es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC, Direccion.SIZE_CODIGO_POSTAL, direccion.getCodigoPostal())) {
			LOG.info("{}El formato del codigo postal es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Direccion.SIZE_ASENTAMIENTO, direccion.getAsentamiento())) {
			LOG.info("{}El formato del asentamiento es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setAsentamiento(direccion.getAsentamiento().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Direccion.SIZE_NUM_EXT, direccion.getNumExt())) {
			LOG.info("{}El formato del numero exterior es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setNumExt(direccion.getNumExt().strip());
		}
		if(isValid(direccion.getNumInt())) {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Direccion.SIZE_NUM_INT, direccion.getNumInt())) {
				LOG.info("{}El formato del numero interior es incorrecto.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}else {
				direccion.setNumInt(direccion.getNumInt().strip());
			}
		}
		if(isNotValid(direccion.getMunicipio())) {
			LOG.info("{}No se mando el municipio.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(direccion.getMunicipio().getClave())) {
			LOG.info("{}No se mando la clave del municipio.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		
		Optional<Catalogo> optionalCatalogoSexo = catalogoService.findByTipoAndNombre(SEXO, persona.getSexo().getNombre());
		Optional<Catalogo> optionalCatalogoTipoPersona = catalogoService.findByTipoAndNombre(TIPO_DE_PERSONA, FISICA);
		
		if(optionalCatalogoSexo.isEmpty()) {
			LOG.info("{}No se encontro el sexo.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		
		if(optionalCatalogoTipoPersona.isEmpty()) {
			LOG.info("{}No se encontro el tipo de persona.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_SEXO = optionalCatalogoSexo.get();
		final Catalogo CAT_ACTIVO = singletonUtil.getActivo();
		final Catalogo CAT_TIPO_PERSONA = optionalCatalogoTipoPersona.get();
		
		Optional<Municipio> municipioOptional = municipioService.findByClave(direccion.getMunicipio().getClave());
		if(municipioOptional.isEmpty()) {
			LOG.info("{}No se encontro el municipio.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipio = municipioOptional.get();
		
		Optional<Trabajador> opcionalTrabajador =  trabajadorService.findByClave(trabajador.getJefe().getClave());
		if(opcionalTrabajador.isEmpty()) {
			LOG.info("{}No se encontro el trabajador (jefe).",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		}
		Trabajador jefe = opcionalTrabajador.get();
		if(!jefe.getPlantel().getIdEmpresa().equals(idEmpresa)) {
			LOG.info("{}El trabajador (jefe) no pertenece a la empresa.",headerLog);
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
		Optional<Puesto> optionalPuesto = puestoService.findByClave(trabajador.getPuesto().getClave());
		if(optionalPuesto.isEmpty()) {
			LOG.info("{}No encontro el puesto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		Puesto puesto = optionalPuesto.get();
		if(!puesto.getEstatus().equals(CAT_ACTIVO)) {
			LOG.info("{}El puesto no esta activo.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		if(!puesto.getDepartamento().getIdEmpresa().equals(idEmpresa)) {
			LOG.info("{}El puesto no pertenece a la empresa.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		
		Usuario usuario = null;
		CorreoMsj correoMsj = null;
		if (persona.getPersona() != null && persona.getPersona().getUsuarioPersona() != null
				&& persona.getPersona().getUsuarioPersona().getUsuario() != null) {
			Optional<Empresa> optionalEmpresa = empresaService.findById(idEmpresa);
			if(optionalEmpresa.isEmpty()) {
				LOG.info("{}No encontro el id de la empresa.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			Empresa empresa = optionalEmpresa.get();
			Configuracion configuracion = empresa.getConfiguraciones().stream()
					.filter(x -> x.getTipo().getNombre().equals(ALTA_USR_CORREO)).findFirst().orElse(null);
			if(configuracion == null || configuracion.getValor().isBlank()) {
				LOG.info("{}No encontro la configuracion del remitente.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			Optional<Correo> optionalCorreo = correoService.findByRemitente(configuracion.getValor());
			if(optionalCorreo.isEmpty()) {
				LOG.info("{}No encontro el correo del remitente.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			usuario = persona.getPersona().getUsuarioPersona().getUsuario();
			usuario.setId(null);
			correoMsj = new CorreoMsj();
			correoMsj.setCorreo(optionalCorreo.get());
			correoMsj.setCorreoMsjItems(new ArrayList<>());
			correoMsj.setProgramado(LocalDateTime.now());
			correoMsj.setFechaDeModificacion(LocalDateTime.now());
			correoMsj.setIdUsuarioModificado(sesion.getUsuario().getId());
			correoMsj.setEstatus(CAT_ACTIVO);
			correoMsj.setClave(UUID.randomUUID());
			
			configuracion = empresa.getConfiguraciones().stream()
					.filter(x -> x.getTipo().getNombre().equals(ALTA_USR_ASUNTO)).findFirst().orElse(null);
			if(configuracion == null || configuracion.getValor().isBlank()) {
				LOG.info("{}No encontro el asunto del correo que envia la contraseña al nuevo usuario.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			correoMsj.setAsunto(configuracion.getValor());
			configuracion = empresa.getConfiguraciones().stream()
					.filter(x -> x.getTipo().getNombre().equals(ALTA_USR_PLANTILLA)).findFirst().orElse(null);
			if(configuracion == null || configuracion.getValor().isBlank()) {
				LOG.info("{}No encontro el path de la plantilla del correo que envia la contraseña al nuevo usuario.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			correoMsj.setMensaje(configuracion.getValor());
			if(isNotValid(TIPO_EMAIL, Usuario.SIZE_CORREO, usuario.getCorreo())) {
				LOG.info("{}El correo no cumple con el formato.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(TIPO_VARIABLE, Usuario.SIZE_NOMBRE, usuario.getNombre()) &&
					isNotValid(TIPO_EMAIL, Usuario.SIZE_NOMBRE, usuario.getNombre())) {
				LOG.info("{}El username no cumple con el formato.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(usuario.getPerfil())) {
				LOG.info("{}No se mando el perfil.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(usuario.getPerfil().getClave())) {
				LOG.info("{}No se mando la clave del perfil.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
			
			if(usuario.getNombre().length()<8) {
				LOG.info("{}El username debe tener almenos 8 caracteres.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
			
			if(usuarioService.findByNameRegardlessOfStatus(usuario.getNombre()).isPresent()) {
				LOG.info("{}El username ya existe.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.CONFLICT);
			}
			Optional<Perfil> optionalPerfil = perfilService.findByClave(usuario.getPerfil().getClave());
			if(optionalPerfil.isEmpty()) {
				LOG.info("{}No encontro el perfil.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			Perfil perfil = optionalPerfil.get();
			if(!perfil.getEstatus().equals(CAT_ACTIVO)) {
				LOG.info("{}El perfil no esta activo.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			if(!perfil.getIdEmpresa().equals(idEmpresa)) {
				LOG.info("{}El perfil no pertenece a la empresa.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			
			Optional<Catalogo> optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_DE_CORREO, CONFIRMAR_CORREO);
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No encontro el catalogo TIPO_DE_CORREO CONFIRMAR_CORREO.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			correoMsj.setTipo(optionalCatalogo.get());
			
			optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_ELEMENTO_CORREO, NOMBRE);
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No encontro el catalogo nombre TIPO_ELEMENTO_CORREO NOMBRE.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			CorreoMsjItem correoMsjItem = new CorreoMsjItem();
			correoMsjItem.setTipo(optionalCatalogo.get());
			
			correoMsjItem.setFechaDeModificacion(LocalDateTime.now());
			correoMsjItem.setIdUsuarioModificado(sesion.getUsuario().getId());
			correoMsjItem.setEstatus(CAT_ACTIVO);
			correoMsjItem.setClave(UUID.randomUUID());
			correoMsjItem.setCorreoMsj(correoMsj);
			correoMsjItem.setDato(persona.getNombre()
					.concat(" ").concat(persona.getApellidoPaterno())
					.concat(" ").concat(persona.getApellidoMaterno()));
			correoMsj.getCorreoMsjItems().add(correoMsjItem);
			
			optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_ELEMENTO_CORREO, RAZON_SOCIAL);
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No encontro el catalogo TIPO_ELEMENTO_CORREO RAZON_SOCIAL.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			correoMsjItem = new CorreoMsjItem();
			correoMsjItem.setTipo(optionalCatalogo.get());
			correoMsjItem.setFechaDeModificacion(LocalDateTime.now());
			correoMsjItem.setIdUsuarioModificado(sesion.getUsuario().getId());
			correoMsjItem.setEstatus(CAT_ACTIVO);
			correoMsjItem.setClave(UUID.randomUUID());
			correoMsjItem.setCorreoMsj(correoMsj);
			correoMsjItem.setDato(empresa.getNombre());
			correoMsj.getCorreoMsjItems().add(correoMsjItem);
			
			optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_ELEMENTO_CORREO, USUARIO);
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No encontro el catalogo TIPO_ELEMENTO_CORREO USUARIO.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			correoMsjItem = new CorreoMsjItem();
			correoMsjItem.setTipo(optionalCatalogo.get());
			correoMsjItem.setFechaDeModificacion(LocalDateTime.now());
			correoMsjItem.setIdUsuarioModificado(sesion.getUsuario().getId());
			correoMsjItem.setEstatus(CAT_ACTIVO);
			correoMsjItem.setClave(UUID.randomUUID());
			correoMsjItem.setCorreoMsj(correoMsj);
			correoMsjItem.setDato(usuario.getNombre());
			correoMsj.getCorreoMsjItems().add(correoMsjItem);
			
			optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_ELEMENTO_CORREO, ID_TRABAJADOR);
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No encontro el catalogo TIPO_ELEMENTO_CORREO ID_TRABAJADOR.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			correoMsjItem = new CorreoMsjItem();
			correoMsjItem.setTipo(optionalCatalogo.get());
			correoMsjItem.setFechaDeModificacion(LocalDateTime.now());
			correoMsjItem.setIdUsuarioModificado(sesion.getUsuario().getId());
			correoMsjItem.setEstatus(CAT_ACTIVO);
			correoMsjItem.setClave(UUID.randomUUID());
			correoMsjItem.setCorreoMsj(correoMsj);
			correoMsj.getCorreoMsjItems().add(correoMsjItem);
			
			optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_ELEMENTO_CORREO, ID_EMPRESA);
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No encontro el catalogo TIPO_ELEMENTO_CORREO ID_EMPRESA.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			correoMsjItem = new CorreoMsjItem();
			correoMsjItem.setTipo(optionalCatalogo.get());
			correoMsjItem.setFechaDeModificacion(LocalDateTime.now());
			correoMsjItem.setIdUsuarioModificado(sesion.getUsuario().getId());
			correoMsjItem.setEstatus(CAT_ACTIVO);
			correoMsjItem.setClave(UUID.randomUUID());
			correoMsjItem.setCorreoMsj(correoMsj);
			correoMsjItem.setDato(String.valueOf(idEmpresa));
			correoMsj.getCorreoMsjItems().add(correoMsjItem);
			
			optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_ELEMENTO_CORREO, PASSWORD);
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No encontro el catalogo TIPO_ELEMENTO_CORREO PASSWORD.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			correoMsjItem = new CorreoMsjItem();
			correoMsjItem.setTipo(optionalCatalogo.get());
			correoMsjItem.setFechaDeModificacion(LocalDateTime.now());
			correoMsjItem.setIdUsuarioModificado(sesion.getUsuario().getId());
			correoMsjItem.setEstatus(CAT_ACTIVO);
			correoMsjItem.setClave(UUID.randomUUID());
			correoMsjItem.setCorreoMsj(correoMsj);
			correoMsjItem.setDato("TCBX-".concat(String.valueOf((int)(1000+Math.random() * 8000))));
			usuario.setPassword(correoMsjItem.getDato());
			correoMsj.getCorreoMsjItems().add(correoMsjItem);
			
			correoMsj.setDestinatario(usuario.getCorreo());
			
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			usuario.setClave(UUID.randomUUID());
			usuario.setPerfil(perfil);
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
			usuario.setFechaDeModificacion(LocalDateTime.now());
			usuario.setIdUsuarioModificado(sesion.getUsuario().getId());
			usuario.setEstatus(CAT_ACTIVO);
		}
		direccion.setId(null);
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
		prs.setIdEmpresa(idEmpresa);
		prs = personaService.save(prs);
		persona.setId(null);
		persona.setClave(UUID.randomUUID());
		persona.setPersona(prs);
		persona.setDireccion(direccion);
		persona.setSexo(CAT_SEXO);
		persona.setFechaDeModificacion(LocalDateTime.now());
		persona.setIdUsuarioModificado(sesion.getUsuario().getId());
		persona.setEstatus(CAT_ACTIVO);
		
		persona = personaFisicaService.save(persona);
		trabajador.setId(null);
		trabajador.setClave(UUID.randomUUID());
		trabajador.setEstatus(CAT_ACTIVO);
		trabajador.setFechaDeModificacion(LocalDateTime.now());
		trabajador.setIdUsuarioModificado(sesion.getUsuario().getId());
		trabajador.setIdEmpresa(sesion.getLicencia().getPlantel().getIdEmpresa());
		trabajador.setJefe(jefe);
		trabajador.setPersonaFisica(persona);
		trabajador.setPlantel(plantel);
		trabajador.setPuesto(puesto);
		trabajador = trabajadorService.save(trabajador);
		
		if(usuario != null) {
			usuario = usuarioService.save(usuario);
			UsuarioPersona usuarioPersona = new UsuarioPersona();
			usuarioPersona.setUsuario(usuario);
			usuarioPersona.setPersona(prs);
			usuarioPersona.setFechaDeModificacion(LocalDateTime.now());
			usuarioPersona.setIdUsuarioModificado(sesion.getUsuario().getId());
			usuarioPersona.setEstatus(CAT_ACTIVO);
			usuarioPersona.setClave(UUID.randomUUID());
			usuarioPersona = usuarioPersonaService.save(usuarioPersona);
			prs.setUsuarioPersona(usuarioPersona);
			correoMsjService.save(correoMsj);
			List<CorreoMsjItem> correoMsjItems = correoMsj.getCorreoMsjItems();
			for(CorreoMsjItem item:correoMsjItems) {
				if(item.getTipo().getNombre().equals(ID_TRABAJADOR)) {
					item.setDato(trabajador.getId().toString());
				}
				correoMsjItemService.save(item);
			}
		}
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
		Persona persona = personaFisica.getPersona();
		persona.setEstatus(CAT_ELIMINADO);
		persona = personaService.update(persona);
		personaFisica.setPersona(persona);
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
