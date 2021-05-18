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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mx.tecabix.db.entity.Banco;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Configuracion;
import mx.tecabix.db.entity.Contacto;
import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.entity.CorreoMsj;
import mx.tecabix.db.entity.CorreoMsjItem;
import mx.tecabix.db.entity.Direccion;
import mx.tecabix.db.entity.Empresa;
import mx.tecabix.db.entity.Estado;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.entity.Perfil;
import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.entity.PersonaFisica;
import mx.tecabix.db.entity.Plantel;
import mx.tecabix.db.entity.Puesto;
import mx.tecabix.db.entity.Salario;
import mx.tecabix.db.entity.SeguroSocial;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.entity.Turno;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.entity.UsuarioPersona;
import mx.tecabix.db.service.BancoService;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.ContactoService;
import mx.tecabix.db.service.CorreoMsjItemService;
import mx.tecabix.db.service.CorreoMsjService;
import mx.tecabix.db.service.CorreoService;
import mx.tecabix.db.service.DireccionService;
import mx.tecabix.db.service.EmpresaService;
import mx.tecabix.db.service.EstadoService;
import mx.tecabix.db.service.MunicipioService;
import mx.tecabix.db.service.PerfilService;
import mx.tecabix.db.service.PersonaFisicaService;
import mx.tecabix.db.service.PersonaService;
import mx.tecabix.db.service.PlantelService;
import mx.tecabix.db.service.PuestoService;
import mx.tecabix.db.service.SalarioService;
import mx.tecabix.db.service.SeguroSocialService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.db.service.TurnoService;
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
	
	@Value("${configuracion.resource}")
	private String configuracionResourcelFile;
	private String IMG_TRABAJADOR_DIR;
	
	private static final Logger LOG = LoggerFactory.getLogger(TrabajadorControllerV01.class);
	private static final String LOG_URL = "/trabajador/v1";
	private static final String LOG_URL_IMAGE = "/trabajador/v1/image";

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private TrabajadorService trabajadorService;
	@Autowired
	private PersonaService personaService;
	@Autowired
	private ContactoService contactoService;
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
	@Autowired
	private BancoService bancoService;
	@Autowired
	private EstadoService estadoService;
	@Autowired
	private TurnoService turnoService;
	@Autowired
	private SalarioService salarioService;
	@Autowired
	private SeguroSocialService seguroSocialService;
	
	private final String TIPO_DE_PERSONA = "TIPO_DE_PERSONA";
	private final String FISICA = "FISICA";
	
	private final String ALTA_USR_CORREO	= "ALTA_USR_CORREO";
	private final String ALTA_USR_ASUNTO	= "ALTA_USR_ASUNTO";
	private final String ALTA_USR_PLANTILLA	= "ALTA_USR_PLANTILLA";
	
	
	private final String PAGO_SALARIO		= "PAGO_SALARIO";
	private final String PERIODO_SALARIO	= "PERIODO_SALARIO";
	private final String TIPO_DE_CORREO		= "TIPO_DE_CORREO";
	private final String CONFIRMAR_CORREO	= "CONFIRMAR_CORREO";
	
	private final String TIPO_CONTACTO		= "TIPO_CONTACTO";
	private final String SEXO				= "SEXO";
	
	private final String NOMBRE					= "[NOMBRE]";
	private final String USUARIO				= "[USUARIO]";
	private final String PASSWORD				= "[PASSWORD]";
	private final String RAZON_SOCIAL			= "[RAZON_SOCIAL]";
	private final String ID_TRABAJADOR			= "[ID_TRABAJADOR]";
	private final String ID_EMPRESA				= "[ID_EMPRESA]";
	private final String TIPO_ELEMENTO_CORREO	= "TIPO_ELEMENTO_CORREO";
	
	
	private final String TRABAJADOR = "TRABAJADOR";
	private final String TRABAJADOR_CREAR = "TRABAJADOR_CREAR";
	private final String TRABAJADOR_ELIMINAR = "TRABAJADOR_ELIMINAR";
	
	
	@PostConstruct
	private void postConstruct() {
		try {
			Properties properties = new Properties();
			FileReader fileReader;
			fileReader = new FileReader(new File(configuracionResourcelFile).getAbsoluteFile());
			properties.load(fileReader);
			IMG_TRABAJADOR_DIR = properties.getProperty("IMG_TRABAJADOR_DIR");
			fileReader.close();
		} catch (FileNotFoundException e) {
			LOG.error("se produjo un FileNotFoundException en el postConstruct de TrabajadorControllerV01");
			e.printStackTrace();
			
		} catch (IOException e) {
			LOG.error("se produjo un IOException en el postConstruct de TrabajadorControllerV01");
			e.printStackTrace();
		}
	}
	
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
	
	
	@ApiOperation(value = "Trae el jefe del empleado por su clave.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping("find-boss")
	public ResponseEntity<Trabajador> findBoss(
			@RequestParam(value="token") UUID token, @RequestParam(value="clave") UUID clave) {
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR);
		if(sesion == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		
		Optional<Trabajador> optionalTrabajador = trabajadorService.findBoss(clave);
		if(optionalTrabajador.isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		Trabajador trabajador = optionalTrabajador.get();
		if(!trabajador.getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Trabajador>(trabajador,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Trae el empleado por su clave.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping("find-by-clave")
	public ResponseEntity<Trabajador> findByClave(
			@RequestParam(value="token") UUID token, @RequestParam(value="clave") UUID clave) {
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR);
		if(sesion == null) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		
		Optional<Trabajador> optionalTrabajador = trabajadorService.findByClave(clave);
		if(optionalTrabajador.isEmpty()) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		Trabajador trabajador = optionalTrabajador.get();
		if(!trabajador.getEstatus().equals(singletonUtil.getActivo())) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		if(!trabajador.getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Trabajador>(trabajador,HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Trae todo los empleados del jefe.")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping("find-by-jefe")
	public ResponseEntity<TrabajadorPage> findByJefe(
			@RequestParam(value = "token") UUID token,
			@RequestParam(value = "clave") UUID clave, 
			@RequestParam(value = "by", defaultValue = "ID") String by,
			@RequestParam(value = "order", defaultValue = "ASC") String order,
			@RequestParam(value = "elements") byte elements, 
			@RequestParam(value = "page") short page) {
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR);
		if (sesion == null) {
			return new ResponseEntity<TrabajadorPage>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();

		Optional<Trabajador> optionalTrabajador = trabajadorService.findByClave(clave);
		if (optionalTrabajador.isEmpty()) {
			return new ResponseEntity<TrabajadorPage>(HttpStatus.NOT_FOUND);
		}
		Trabajador trabajador = optionalTrabajador.get();
		if (!trabajador.getEstatus().equals(singletonUtil.getActivo())) {
			return new ResponseEntity<TrabajadorPage>(HttpStatus.NOT_FOUND);
		}
		if (!trabajador.getIdEmpresa().equals(idEmpresa)) {
			return new ResponseEntity<TrabajadorPage>(HttpStatus.NOT_FOUND);
		}
		Sort sort = null;
		if (order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		} else if (order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		} else {
			new ResponseEntity<TrabajadorPage>(HttpStatus.BAD_REQUEST);
		}
		Page<Trabajador> pageTrabajador = trabajadorService.findByJefe(idEmpresa, trabajador.getId(), elements, page,
				sort);
		TrabajadorPage body = new TrabajadorPage(pageTrabajador);
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
		PersonaFisica personaFisica = trabajador.getPersonaFisica();
		if(isNotValid(personaFisica) ) {
			LOG.info("{}No se mando la persona fisica.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, PersonaFisica.SIZE_NOMBRE, personaFisica.getNombre())) {
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			personaFisica.setNombre(personaFisica.getNombre().strip());
		}
		if(isNotValid(personaFisica.getSexo())) {
			LOG.info("{}No se mando el sexo.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(personaFisica.getSexo().getNombre())) {
			LOG.info("{}No se mando el nombre del sexo.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(personaFisica.getFechaNacimiento())) {
			LOG.info("{}No se mando la fecha de nacimiento.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA, PersonaFisica.SIZE_APELLIDO_MATERNO, personaFisica.getApellidoMaterno())) {
			LOG.info("{}El formato del apellido materno es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA, PersonaFisica.SIZE_APELLIDO_PATERNO, personaFisica.getApellidoPaterno())) {
			LOG.info("{}El formato del apellido paterno es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		Persona persona = personaFisica.getPersona();
		if(persona == null) {
			persona = new Persona();
		}
		if(persona.getContactos() == null) {
			persona.setContactos(new ArrayList<>());
		}
		for(Contacto contacto: persona.getContactos()) {
			if(isNotValid(contacto.getTipo())) {
				LOG.info("{}No se esta mandando tipo para el contacto.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(contacto.getTipo().getNombre())) {
				LOG.info("{}No se esta mandando el nombre del tipo de contacto.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Contacto.SIZE_VALOR , contacto.getValor() )) {
				LOG.info("{}El formato del valor del contacto no es valido.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}else {
				contacto.setValor(contacto.getValor().strip());
			}
		}
		
		Direccion direccion = personaFisica.getDireccion();
		if(isNotValid(direccion)) {
			LOG.info("{}No se mando la direccion.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Direccion.SIZE_CALLE, direccion.getCalle())) {
			LOG.info("{}El formato de la calle es incorrecto.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			direccion.setCalle(direccion.getCalle().strip());
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
		
		Optional<Catalogo> optionalCatalogo = catalogoService.findByTipoAndNombre(SEXO, personaFisica.getSexo().getNombre());
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No se encontro el sexo.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		final Catalogo CAT_SEXO = optionalCatalogo.get();
		
		optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_DE_PERSONA, FISICA);
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No se encontro el tipo de persona.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_TIPO_PERSONA = optionalCatalogo.get();		
		final Catalogo CAT_ACTIVO = singletonUtil.getActivo();
		
		{
			Optional<Municipio> municipioOptional = municipioService.findByClave(direccion.getMunicipio().getClave());
			if(municipioOptional.isEmpty()) {
				LOG.info("{}No se encontro el municipio.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
			}
			direccion.setMunicipio(municipioOptional.get());
		}
		{
			Optional<Trabajador> opcionalTrabajador =  trabajadorService.findByClave(trabajador.getJefe().getClave());
			if(opcionalTrabajador.isEmpty()) {
				LOG.info("{}No se encontro el trabajador (jefe).",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
			}
			trabajador.setJefe(opcionalTrabajador.get());
			if(!trabajador.getJefe().getPlantel().getIdEmpresa().equals(idEmpresa)) {
				LOG.info("{}El trabajador (jefe) no pertenece a la empresa.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
			}
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
		{
			Optional<Puesto> optionalPuesto = puestoService.findByClave(trabajador.getPuesto().getClave());
			if(optionalPuesto.isEmpty()) {
				LOG.info("{}No encontro el puesto.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			trabajador.setPuesto(optionalPuesto.get());
			if(!trabajador.getPuesto().getEstatus().equals(CAT_ACTIVO)) {
				LOG.info("{}El puesto no esta activo.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			if(!trabajador.getPuesto().getDepartamento().getIdEmpresa().equals(idEmpresa)) {
				LOG.info("{}El puesto no pertenece a la empresa.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
		}
		for(Contacto contacto: persona.getContactos()) {
			optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_CONTACTO, contacto.getTipo().getNombre());
			if(optionalCatalogo.isEmpty()) {
				LOG.info("{}No encontro el tipo de contacto.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			contacto.setTipo(optionalCatalogo.get());
			if(!contacto.getTipo().getEstatus().equals(CAT_ACTIVO)) {
				LOG.info("{}El tipo de contacto no esta activo.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
		}
		Salario salario = trabajador.getSalario();
		if(salario == null) {
			LOG.info("{}No se mando el salario.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC_POSITIVO, Integer.MAX_VALUE, salario.getPeriodo())) {
			LOG.info("{}El valor del periodo no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC_POSITIVO, Integer.MAX_VALUE, salario.getDia())) {
			LOG.info("{}El valor del dia no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC_POSITIVO, Integer.MAX_VALUE, salario.getHora())) {
			LOG.info("{}El valor de la hora no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC_POSITIVO, Integer.MAX_VALUE, salario.getHoraPorDia())) {
			LOG.info("{}El valor de la hora por dia no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC_POSITIVO, Integer.MAX_VALUE, salario.getDiaPorPeriodo())) {
			LOG.info("{}El valor del dia por periodo no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isValid(salario.getBanco())) {
			if(isNotValid(salario.getBanco().getClave())) {
				LOG.info("{}No se mando la clave del banco para el salario.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
			Optional<Banco> optionalBanco = bancoService.findByClave(salario.getBanco().getClave());
			if(optionalBanco.isEmpty()) {
				LOG.info("{}No se encontro el banco para el salario.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			salario.setBanco(optionalBanco.get());
			if(!salario.getBanco().getEstatus().equals(CAT_ACTIVO)) {
				LOG.info("{}El banco no esta activo para el salario.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
		}
		if(salario.getNumeroCuenta()!= null && !salario.getNumeroCuenta().isBlank()) {
			if(isNotValid(TIPO_ALFA_NUMERIC, Salario.SIZE_NUMERO_CUENTA, salario.getNumeroCuenta() )) {
				LOG.info("{}No se mando el numero de cuenta para el salario.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
		}else{
			salario.setNumeroCuenta(null);
		}
		if(salario.getSucursal() != null && !salario.getSucursal().isBlank()) {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Salario.SIZE_SUCURSAL, salario.getSucursal() )) {
				LOG.info("{}El valor de la sucursal del salario no es valido.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}else {
				salario.setSucursal(salario.getSucursal().strip());
			}
		}else {
			salario.setSucursal(null);
		}
		if(salario.getClaveInterBancaria() != null && !salario.getClaveInterBancaria().isBlank()) {
			if(isNotValid(TIPO_NUMERIC, Salario.SIZE_CLAVE_INTERBANCARIA, salario.getClaveInterBancaria() )) {
				LOG.info("{}El valor de la sucursal del salario no es valido.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}
		}else {
			salario.setClaveInterBancaria(null);
		}
		if(isNotValid(salario.getTipoPago())) {
			LOG.info("{}No se mando el tipo de pago del salario.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(salario.getTipoPago().getNombre())) {
			LOG.info("{}No se mando el nombre del tipo de pago del salario.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(salario.getTipoPeriodo())) {
			LOG.info("{}No se mando el tipo de periodo del salario.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(salario.getTipoPeriodo().getNombre())) {
			LOG.info("{}No se mando el nombre del tipo de periodo del salario.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		optionalCatalogo = catalogoService.findByTipoAndNombre(PAGO_SALARIO, salario.getTipoPago().getNombre());
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No se encontro el catalogo para PAGO_SALARIO.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		salario.setTipoPago(optionalCatalogo.get());
		optionalCatalogo = catalogoService.findByTipoAndNombre(PERIODO_SALARIO, salario.getTipoPeriodo().getNombre());
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No se encontro el catalogo para PERIODO_SALARIO.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
		}
		salario.setTipoPeriodo(optionalCatalogo.get());
		
		SeguroSocial seguroSocial = trabajador.getSeguroSocial();
		if(seguroSocial == null) {
			LOG.info("{}No se mando el seguro social.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC, SeguroSocial.SIZE_NUMERO, seguroSocial.getNumero())){
			LOG.info("{}El valor del numero del seguro social no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, SeguroSocial.SIZE_CIUDAD, seguroSocial.getCiudad())){
			LOG.info("{}El valor de la ciudad del seguro social no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}else {
			seguroSocial.setCiudad(seguroSocial.getCiudad().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC, SeguroSocial.SIZE_RFC, seguroSocial.getRFC())){
			LOG.info("{}El valor del RFC del seguro social no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC, SeguroSocial.SIZE_CURP, seguroSocial.getCURP())){
			LOG.info("{}El valor del CURP del seguro social no es valido.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(seguroSocial.getAlta()==null) {
			seguroSocial.setAlta(LocalDate.now());
		}
		if(seguroSocial.getBaja()==null) {
			seguroSocial.setObservacionesBaja(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, SeguroSocial.SIZE_OBSERVACIONES_BAJA, seguroSocial.getObservacionesBaja())){
				LOG.info("{}El valor de la observacion del seguro social no es valido.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			}else {
				seguroSocial.setObservacionesBaja(seguroSocial.getObservacionesBaja().strip());
			}
		}
		if(isNotValid(seguroSocial.getEntidadFederativa())) {
			LOG.info("{}No se mando la entidad federativa del seguro social.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(seguroSocial.getEntidadFederativa().getClave())) {
			LOG.info("{}No se mando la clave de la entidad federativa del seguro social.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		{
			Optional<Estado> optionalEstado = estadoService.findByClave(seguroSocial.getEntidadFederativa().getClave());
			if(optionalEstado.isEmpty()) {
				LOG.info("{}No se encontro la entidad federativa del seguro social.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			seguroSocial.setEntidadFederativa(optionalEstado.get());
			if(!seguroSocial.getEntidadFederativa().getEstatus().equals(CAT_ACTIVO)) {
				LOG.info("{}La entidad federativa del seguro social no esta activa.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
		}
		if(isNotValid(trabajador.getTurno())){
			LOG.info("{}No se mando el turno.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(trabajador.getTurno().getClave())){
			LOG.info("{}No se mando la clave del turno.",headerLog);
			return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		}
		{
			Optional<Turno> optionalTurno = turnoService.findByClave(trabajador.getTurno().getClave());
			if(optionalTurno.isEmpty()) {
				LOG.info("{}No se encontro el tueno.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			trabajador.setTurno(optionalTurno.get());
			if(!trabajador.getTurno().getIdEmpresa().equals(idEmpresa)) {
				LOG.info("{}El turno no pertenece a la empresa.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
			if(!trabajador.getTurno().getEstatus().equals(CAT_ACTIVO)) {
				LOG.info("{}El turno no esta activo.",headerLog);
				return new ResponseEntity<Trabajador>(HttpStatus.NOT_FOUND);
			}
		}
		Usuario usuario = null;
		CorreoMsj correoMsj = null;
		if (personaFisica.getPersona() != null && personaFisica.getPersona().getUsuarioPersona() != null
				&& personaFisica.getPersona().getUsuarioPersona().getUsuario() != null) {
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
			usuario = personaFisica.getPersona().getUsuarioPersona().getUsuario();
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
			optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_DE_CORREO, CONFIRMAR_CORREO);
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
			correoMsjItem.setDato(personaFisica.getNombre()
					.concat(" ").concat(personaFisica.getApellidoPaterno())
					.concat(" ").concat(personaFisica.getApellidoMaterno()));
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
		
		salario.setId(null);
		salario.setClave(UUID.randomUUID());
		salario.setEstatus(CAT_ACTIVO);
		salario.setFechaDeModificacion(LocalDateTime.now());
		salario.setIdUsuarioModificado(sesion.getUsuario().getId());
		salario = salarioService.save(salario);
		
		seguroSocial.setId(null);
		seguroSocial.setClave(UUID.randomUUID());
		seguroSocial.setEstatus(CAT_ACTIVO);
		seguroSocial.setFechaDeModificacion(LocalDateTime.now());
		seguroSocial.setIdUsuarioModificado(sesion.getUsuario().getId());
		seguroSocial = seguroSocialService.save(seguroSocial);
		
		direccion.setId(null);
		direccion.setClave(UUID.randomUUID());
		direccion.setEstatus(CAT_ACTIVO);
		direccion.setFechaDeModificacion(LocalDateTime.now());
		direccion.setIdUsuarioModificado(sesion.getUsuario().getId());
		direccion = direccionService.save(direccion);
		
		persona.setId(null);
		persona.setUsuarioPersona(null);
		persona.setClave(UUID.randomUUID());
		persona.setTipo(CAT_TIPO_PERSONA);
		persona.setIdUsuarioModificado(sesion.getUsuario().getId());
		persona.setFechaDeModificacion(LocalDateTime.now());
		persona.setEstatus(CAT_ACTIVO);
		persona.setIdEmpresa(idEmpresa);
		List<Contacto> contactos = persona.getContactos();
		persona = personaService.save(persona);
		
		personaFisica.setId(null);
		personaFisica.setClave(UUID.randomUUID());
		personaFisica.setPersona(persona);
		personaFisica.setDireccion(direccion);
		personaFisica.setSexo(CAT_SEXO);
		personaFisica.setFechaDeModificacion(LocalDateTime.now());
		personaFisica.setIdUsuarioModificado(sesion.getUsuario().getId());
		personaFisica.setEstatus(CAT_ACTIVO);
		
		personaFisica = personaFisicaService.save(personaFisica);
		trabajador.setId(null);
		trabajador.setClave(UUID.randomUUID());
		trabajador.setEstatus(CAT_ACTIVO);
		trabajador.setFechaDeModificacion(LocalDateTime.now());
		trabajador.setIdUsuarioModificado(sesion.getUsuario().getId());
		trabajador.setIdEmpresa(sesion.getLicencia().getPlantel().getIdEmpresa());
		trabajador.setPersonaFisica(personaFisica);
		trabajador.setPlantel(plantel);
		trabajador.setSalario(salario);
		trabajador.setSeguroSocial(seguroSocial);
		trabajador = trabajadorService.save(trabajador);
		
		if(usuario != null) {
			usuario = usuarioService.save(usuario);
			UsuarioPersona usuarioPersona = new UsuarioPersona();
			usuarioPersona.setUsuario(usuario);
			usuarioPersona.setPersona(persona);
			usuarioPersona.setFechaDeModificacion(LocalDateTime.now());
			usuarioPersona.setIdUsuarioModificado(sesion.getUsuario().getId());
			usuarioPersona.setEstatus(CAT_ACTIVO);
			usuarioPersona.setClave(UUID.randomUUID());
			usuarioPersona = usuarioPersonaService.save(usuarioPersona);
			persona.setUsuarioPersona(usuarioPersona);
			correoMsjService.save(correoMsj);
			List<CorreoMsjItem> correoMsjItems = correoMsj.getCorreoMsjItems();
			for(CorreoMsjItem item:correoMsjItems) {
				if(item.getTipo().getNombre().equals(ID_TRABAJADOR)) {
					item.setDato(trabajador.getId().toString());
				}
				correoMsjItemService.save(item);
			}
		}
		for(Contacto contacto: contactos) {
			contacto.setId(null);
			contacto.setPersona(persona);
			contactoService.save(contacto);
		}
		return new ResponseEntity<Trabajador>(trabajador, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la imagen del trabajador")
	@PutMapping("image")
	public ResponseEntity<?> updateImage(@RequestParam(value="clave") UUID clave, @RequestParam(value="token") UUID token,@RequestParam(value="imag") MultipartFile imag){
		
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR_CREAR);
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); 
		}
		
		final Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPut(idEmpresa, LOG_URL_IMAGE);
		
		
		if(!imag.getContentType().equals("image/jpeg")) {
			LOG.info("{}El formato del archivo no es el esperado.",headerLog);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		}
		File file = null;
		{
			Optional<Trabajador> optionalTrabajador = trabajadorService.findByClave(clave);
			if(optionalTrabajador.isEmpty()) {
				LOG.info("{}No se encontro la clave del trabajador.",headerLog);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
			}
			Trabajador trabajador = optionalTrabajador.get();
			if(!trabajador.getEstatus().equals(singletonUtil.getActivo())) {
				LOG.info("{}No se encontro al trabajador.",headerLog);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
			}
			file = new File(this.IMG_TRABAJADOR_DIR.replace(":ID_EMPRESA:", idEmpresa.toString()),trabajador.getId().toString().concat(".jpg"));
			if(file.exists()) {
				file.delete();
			}
		}
		try {
			OutputStream outputStream = new FileOutputStream(file);
			if(imag.getSize() > 1000_000) {
				float compresion = 1000_000f/imag.getSize();
			    if(compresion > 0.8) {
			    	compresion = 0.8f;
			    }else if(compresion < 0.3) {
			    	compresion = 0.3f;
			    }
			    BufferedImage bufferedImage = ImageIO.read(imag.getInputStream());

			    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			    ImageWriter writer = (ImageWriter) writers.next();

			    ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
			    writer.setOutput(ios);

			    ImageWriteParam param = writer.getDefaultWriteParam();

			    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			    
			    param.setCompressionQuality(compresion);
			    writer.write(null, new IIOImage(bufferedImage, null, null), param);

			    ios.close();
			    writer.dispose();
			}else {
				outputStream.write(imag.getBytes());
			}
			outputStream.close();
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (FileNotFoundException e) {
			LOG.error("{}Se produjo un FileNotFoundException.",headerLog);
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (IOException e) {
			LOG.error("{}Se produjo un IOException.",headerLog);
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@ApiOperation(value = "Muestra la imagen del trabajador")
	@GetMapping("image")
	public ResponseEntity<byte[]> perfilImage(@RequestParam(value="clave") UUID clave, @RequestParam(value="token") UUID token){
		
		Sesion sesion = getSessionIfIsAuthorized(token, TRABAJADOR);
		if(sesion == null) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); 
		}
		Optional<Trabajador> optionalTrabajador = trabajadorService.findByClave(clave);
		if(optionalTrabajador.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
		}
		File file = null;
		{
			Trabajador trabajador = optionalTrabajador.get();
			if(!trabajador.getEstatus().equals(singletonUtil.getActivo())) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
			}
			file = new File(this.IMG_TRABAJADOR_DIR,trabajador.getId().toString().concat(".jpg"));
			if(!file.exists()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			if(!file.canRead()) {
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		}
		byte[] bytes = null;
		try {
			InputStream inputStream = new FileInputStream(file);
			bytes = inputStream.readAllBytes();
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/jpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "tecabix.jpg" + "\"")
                .body(bytes);
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
