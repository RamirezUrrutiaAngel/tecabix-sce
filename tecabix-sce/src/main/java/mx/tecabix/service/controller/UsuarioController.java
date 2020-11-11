package mx.tecabix.service.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.Auth;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.entity.UsuarioPersona;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.PersonaService;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.UsuarioPersonaService;
import mx.tecabix.db.service.UsuarioService;

@RestController
@RequestMapping("usuario")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private CatalogoService catalogoService;
	
	@Autowired
	private SesionService sesionService;
	
	@Autowired 
	private PersonaService personaService;
	
	@Autowired
	private UsuarioPersonaService usuarioPersonaService;
	
	
	private final String ACTIVO = "ACTIVO";
	private final String ESTATUS = "ESTATUS";
	private final String USUARIO_CREAR ="USUARIO_CREAR";
	private final String USUARIO_EDITAR ="USUARIO_EDITAR";
		
	@GetMapping
	public ResponseEntity<Usuario> get(@RequestParam(value="key") String token) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<Usuario>(usr, HttpStatus.OK);
	}
	
	@GetMapping("findIsExist")
	public ResponseEntity<Boolean> findByNameRegardlessOfStatus(@RequestParam(value="nombre") String nombre){
		
		Usuario usr = usuarioService.findByNameRegardlessOfStatus(nombre);
		if(usr == null)return new ResponseEntity<Boolean>(false,HttpStatus.ACCEPTED);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	
	@PostMapping()
	public ResponseEntity<Usuario> save(@RequestBody Usuario usuario, @RequestParam(value="token") String token) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, USUARIO_CREAR)) return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED); 
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if(usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		if(usuario.getNombre() == null || usuario.getNombre().isEmpty()) return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		if(usuario.getPassword() == null || usuario.getPassword().isEmpty()) return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		if(usuario.getNombre().length()>8) return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		if(usuario.getUsuarioPersona() == null || usuario.getUsuarioPersona().getPersona() == null || usuario.getUsuarioPersona().getPersona().getId() == null) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		Persona persona = personaService.findById(usuario.getUsuarioPersona().getPersona().getId());
		if(persona == null || !persona.getEstatus().getNombre().equals(ACTIVO) || persona.getUsuarioPersona()!=null) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_ACCEPTABLE);
		}
		if(usuarioService.findByNameRegardlessOfStatus(usuario.getNombre())!= null)return new ResponseEntity<Usuario>(HttpStatus.CONFLICT);
		final Catalogo catalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuario.setFechaDeModificacion(LocalDateTime.now());
		usuario.setIdUsuarioModificado(usr.getId());
		usuario.setEstatus(catalogoActivo);
		usuario = usuarioService.save(usuario);
		UsuarioPersona usuarioPersona = new UsuarioPersona();
		usuarioPersona.setUsuario(usuario);
		usuarioPersona.setPersona(persona);
		usuarioPersona.setFechaDeModificacion(LocalDateTime.now());
		usuarioPersona.setIdUsuarioModificado(usr.getId());
		usuarioPersona.setEstatus(catalogoActivo);
		usuarioPersona = usuarioPersonaService.save(usuarioPersona);
		return new ResponseEntity<Usuario>(usuario,HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario, @RequestParam(value="token") String token) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		if(usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		usr.setFechaDeModificacion(LocalDateTime.now());
		usr.setCorreo(usuario.getCorreo());
		if(usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			usr.setPassword(passwordEncoder.encode(usuario.getPassword()));
		}
		
		usr = usuarioService.save(usr);
		return new ResponseEntity<Usuario>(usr,HttpStatus.OK);
	}
	
	@PutMapping("update-usuario")
	public ResponseEntity<Usuario> updateUsuario(@RequestBody Usuario usuario, @RequestParam(value="token") String token) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, USUARIO_EDITAR)) return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED); 
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		if(usuario.getId() == null || usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		
		
		Usuario usuarioUpdate= usuarioService.findById(usuario.getId());
		if(usuarioUpdate == null || usuarioUpdate.getUsuarioPersona() == null) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
		}
		if(!sesion.getLicencia().getPlantel().getIdEscuela().equals(usuarioUpdate.getUsuarioPersona().getPersona().getIdEscuela())) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
		}
		usuarioUpdate.setFechaDeModificacion(LocalDateTime.now());
		usuarioUpdate.setCorreo(usuario.getCorreo());
		if(usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			usr.setPassword(passwordEncoder.encode(usuario.getPassword()));
		}
		
		usr = usuarioService.save(usr);
		return new ResponseEntity<Usuario>(usr,HttpStatus.OK);
	}
}
