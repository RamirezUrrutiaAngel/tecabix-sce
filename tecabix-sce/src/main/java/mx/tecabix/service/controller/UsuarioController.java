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
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.SesionService;
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
	
	private final String ACTIVO = "ACTIVO";
	private final String ESTATUS = "ESTATUS";
	private final String USUARIO_ANONIMOS ="USUARIO_ANONIMOS";
		
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
		if(!Auth.hash(auth, USUARIO_ANONIMOS)) return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED); 
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
		if(usuarioService.findByNameRegardlessOfStatus(usuario.getNombre())!= null)return new ResponseEntity<Usuario>(HttpStatus.CONFLICT);
		final Catalogo catalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuario.setFechaDeModificacion(LocalDateTime.now());
		usuario.setIdUsuarioModificado(usr.getId());
		usuario.setEstatus(catalogoActivo);
		usuarioService.save(usuario);
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
		if(usuarioService.findByNameRegardlessOfStatus(usuario.getNombre())!= null)return new ResponseEntity<Usuario>(HttpStatus.CONFLICT);
		usr.setFechaDeModificacion(LocalDateTime.now());
		usr.setCorreo(usuario.getCorreo());
		if(usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			usr.setPassword(passwordEncoder.encode(usuario.getPassword()));
		}
		
		usuarioService.save(usr);
		return new ResponseEntity<Usuario>(usr,HttpStatus.OK);
	}
}
