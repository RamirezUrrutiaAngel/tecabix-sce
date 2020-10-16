package mx.tecabix.service.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.Auth;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Direccion;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.entity.PersonaFisica;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.DireccionService;
import mx.tecabix.db.service.MunicipioService;
import mx.tecabix.db.service.PersonaFisicaService;
import mx.tecabix.db.service.PersonaService;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.db.service.UsuarioService;

@RestController
@RequestMapping("trabajador")
public class TrabajadorController {

	@Autowired
	private CatalogoService catalogoService;
	@Autowired 
	private UsuarioService usuarioService;
	@Autowired
	private SesionService sesionService;
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
	
	private final String USUARIO_TRABAJADOR_CREAR = "USUARIO_TRABAJADOR_CREAR";
	
	
	
	@GetMapping
	public ResponseEntity<Trabajador> get(@RequestParam(value="token") String token) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		}
		Trabajador trabajador =  trabajadorService.findByUsuario(usr.getNombre());
		return new ResponseEntity<Trabajador>(trabajador,HttpStatus.OK);
	}
	
	@GetMapping("findAll")
	public ResponseEntity<Page<Trabajador>> findAll(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, TRABAJADOR)) return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Trabajador> trabajador =  trabajadorService.findAll(sesion.getLicencia().getPlantel().getIdEscuela(),elements, page);
		return new ResponseEntity<Page<Trabajador>>(trabajador,HttpStatus.OK);
	}
	
	@GetMapping("findAllByNombre")
	public ResponseEntity<Page<Trabajador>> findAllByNombre(@RequestParam(value="token") String token, @RequestParam(value="nombre") String nombre,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, TRABAJADOR)) return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Page<Trabajador>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Trabajador> trabajador =  trabajadorService.findAllByNombre(sesion.getLicencia().getPlantel().getIdEscuela(),nombre,elements, page);
		return new ResponseEntity<Page<Trabajador>>(trabajador,HttpStatus.OK);
	}
	
	
	@PostMapping
	public ResponseEntity<Trabajador> save(@RequestBody Trabajador trabajador, @RequestParam(value="token") String token) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, TRABAJADOR_CREAR)) return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED); 
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		}
		if(trabajador == null)return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(trabajador.getCURP() == null || trabajador.getCURP().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(trabajador.getJefe() == null || trabajador.getJefe().getId() == null)return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(trabajador.getPuesto() == null || trabajador.getPuesto().getId() == null)return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		
		if(trabajador.getUsuario() == null)return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		PersonaFisica persona = trabajador.getPersonaFisica();
		if(persona == null ) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(persona.getNombre() == null || persona.getNombre().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(persona.getSexo() == null || persona.getSexo().getNombre() == null || persona.getSexo().getNombre().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(persona.getFechaNacimiento() == null) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(persona.getApellidoMaterno() == null || persona.getApellidoMaterno().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(persona.getApellidoPaterno() == null || persona.getApellidoPaterno().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		Direccion direccion = persona.getDireccion();
		if(direccion == null ) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(direccion.getCalle() == null || direccion.getCalle().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(direccion.getCodigoPostal() == null || direccion.getCodigoPostal().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(direccion.getAsentamiento() == null || direccion.getAsentamiento().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(direccion.getNumExt() == null || direccion.getAsentamiento().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(direccion.getMunicipio() == null || direccion.getMunicipio().getId() == null ) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		final Catalogo CAT_SEXO = catalogoService.findByTipoAndNombre(SEXO, persona.getSexo().getNombre());
		
		if(CAT_SEXO == null ) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		final Catalogo CAT_PENDIENTE = catalogoService.findByTipoAndNombre(ESTATUS, PENDIENTE);
		final Catalogo CAT_TIPO_PERSONA = catalogoService.findByTipoAndNombre(TIPO_DE_PERSONA, FISICA);
		Municipio municipio = municipioService.findById(direccion.getMunicipio().getId());
		if(municipio == null)return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		Trabajador jefe = trabajadorService.findById(trabajador.getJefe().getId());
		if(jefe == null)return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		Usuario usuario = trabajador.getUsuario();
		if(usuario != null && Auth.hash(auth, USUARIO_TRABAJADOR_CREAR)) {
			
			if(usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			if(usuario.getNombre() == null || usuario.getNombre().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			if(usuario.getPassword() == null || usuario.getPassword().isEmpty()) return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
			if(usuario.getNombre().length()<8) return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
			if(usuarioService.findByNameRegardlessOfStatus(usuario.getNombre())!= null)return new ResponseEntity<Trabajador>(HttpStatus.CONFLICT);
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
			usuario.setFechaDeModificacion(LocalDateTime.now());
			usuario.setIdUsuarioModificado(usr.getId());
			usuario.setEstatus(CAT_PENDIENTE);
			usuario = usuarioService.save(usuario);
			trabajador.setUsuario(usuario);
		}
		
		direccion.setEstatus(CAT_PENDIENTE);
		direccion.setMunicipio(municipio);
		direccion.setFechaDeModificacion(LocalDateTime.now());
		direccion.setIdUsuarioModificado(usr.getId());
		direccion = direccionService.save(direccion);
		
		Persona prs=new Persona();
		prs.setTipo(CAT_TIPO_PERSONA);
		prs.setIdUsuarioModificado(usr.getId());
		prs.setFechaDeModificacion(LocalDateTime.now());
		prs.setEstatus(CAT_PENDIENTE);
		prs = personaService.save(prs);
		persona.setPresona(prs);
		persona.setDireccion(direccion);
		persona.setSexo(CAT_SEXO);
		persona.setFechaDeModificacion(LocalDateTime.now());
		persona.setIdUsuarioModificado(usr.getId());
		persona.setEstatus(CAT_PENDIENTE);
		
		persona = personaFisicaService.save(persona);
		trabajador.setEstatus(CAT_PENDIENTE);
		trabajador.setFechaDeModificacion(LocalDateTime.now());
		trabajador.setIdUsuarioModificado(usr.getId());
		trabajador.setIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela());
		trabajador.setJefe(jefe);
		trabajador.setPersonaFisica(persona);
		trabajadorService.save(trabajador);
		trabajador.getUsuario().setPassword(null);
		return new ResponseEntity<Trabajador>(trabajador, HttpStatus.OK);
	}
	
	@PutMapping("activar")
	public ResponseEntity<Trabajador> activar(@RequestParam(value="id") Long id, @RequestParam(value="token") String token) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, TRABAJADOR_ACTIVAR)) return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED); 
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		}
		final Catalogo CAT_ACTIVO = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		Trabajador trabajador = trabajadorService.findByIdAndPendiente(id);
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		if(trabajador == null)return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		if(trabajador.getIdEscuela().longValue() != idEscuela.longValue())return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		Usuario usuario = trabajador.getUsuario();
		usuario.setEstatus(CAT_ACTIVO);
		usuarioService.update(usuario);
		trabajador.setUsuario(usuario);
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
	
	@DeleteMapping("eliminar")
	public ResponseEntity<Trabajador> eliminar(@RequestParam(value="id") Long id, @RequestParam(value="token") String token) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, TRABAJADOR_ELIMINAR)) return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED); 
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Trabajador>(HttpStatus.UNAUTHORIZED);
		}
		final Catalogo CAT_ELIMINADO = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);

		Trabajador trabajador = trabajadorService.findById(id);
		if(trabajador == null)return new ResponseEntity<Trabajador>(HttpStatus.BAD_REQUEST);
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		if(trabajador.getIdEscuela().longValue() != idEscuela.longValue())return new ResponseEntity<Trabajador>(HttpStatus.NOT_ACCEPTABLE);
		Usuario usuario = trabajador.getUsuario();
		usuario.setEstatus(CAT_ELIMINADO);
		usuarioService.update(usuario);
		trabajador.setUsuario(usuario);
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
