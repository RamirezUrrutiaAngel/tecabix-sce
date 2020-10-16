package mx.tecabix.service.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import mx.tecabix.Auth;
import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Perfil;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.AuthorityService;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.PerfilService;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.UsuarioService;

@RestController
@RequestMapping("perfil")
public class PerfilController {

	@Autowired
	private PerfilService perfilService;
	@Autowired 
	private UsuarioService usuarioService;
	@Autowired
	private SesionService sesionService;
	@Autowired 
	private AuthorityService authorityService;
	@Autowired
	private CatalogoService catalogoService;
	
	private final String PERFIL = "PERFIL";
	private final String PERFIL_CREAR = "PERFIL_CREAR";
	private final String PERFIL_EDITAR = "PERFIL_EDITAR";
	private final String PERFIL_ELIMINAR = "PERFIL_ELIMINAR";
	
	private final String ESTATUS = "ESTATUS";
	private final String ACTIVO = "ACTIVO";
	private final String ELIMINADO = "ELIMINADO";
	
	@GetMapping
	public ResponseEntity<Perfil> get(@RequestParam(value="token") String token) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		}
		Perfil perfil = usr.getPerfil();
		if(perfil == null) return new ResponseEntity<Perfil>(HttpStatus.NOT_FOUND);
		return new ResponseEntity<Perfil>(perfil,HttpStatus.OK);
	}
	
	@GetMapping("findAll")
	public ResponseEntity<Page<Perfil>> findAll(@RequestParam(value="token") String token, byte elements, short page) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, PERFIL)) return new ResponseEntity<Page<Perfil>>(HttpStatus.UNAUTHORIZED);
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Page<Perfil>>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Page<Perfil>>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Page<Perfil>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Perfil> response = perfilService.findAll(sesion.getLicencia().getPlantel().getIdEscuela(), elements, page);
		return new ResponseEntity<Page<Perfil>>(response, HttpStatus.OK);
	}
	
	@GetMapping("findAllByNombre")
	public ResponseEntity<Page<Perfil>> findAllByNombre(@RequestParam(value="token") String token, @RequestParam(value="nombre") String nombre, byte elements, short page) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, PERFIL)) return new ResponseEntity<Page<Perfil>>(HttpStatus.UNAUTHORIZED);
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Page<Perfil>>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Page<Perfil>>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Page<Perfil>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Perfil> response = perfilService.findAllbyNombre(sesion.getLicencia().getPlantel().getIdEscuela(), nombre, elements, page);
		return new ResponseEntity<Page<Perfil>>(response, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Perfil> post(@RequestParam(value="token") String token,@RequestBody Perfil perfil){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, PERFIL_CREAR)) return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		}
		if(perfil.getDescripcion() == null || perfil.getDescripcion().isEmpty())return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		if(perfil.getNombre() == null || perfil.getNombre().isEmpty())return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		if((perfilService.findByNombre(sesion.getLicencia().getPlantel().getIdEscuela(), perfil.getNombre()))!=null) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_ACCEPTABLE);
		}
		perfil.setIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela());
		List<Authority> list = perfil.getAuthorities();
		if(list != null) {
			for (Authority authority : list) {
				if(!authorityService.findById(authority.getId()).isPresent())return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
			}
		}
		final Catalogo CAT_ACTIVO = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		
		perfil.setIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela());
		perfil.setEstatus(CAT_ACTIVO);
		perfil.setIdUsuarioModificado(usr.getId());
		perfil.setFechaDeModificacion(LocalDateTime.now());
		perfilService.save(perfil);
		return new ResponseEntity<Perfil>(perfil,HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<Perfil> put(@RequestParam(value="token") String token,@RequestBody Perfil perfil){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, PERFIL_EDITAR)) return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		}
		if(perfil.getId() == null)return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		if(perfil.getDescripcion() == null || perfil.getDescripcion().isEmpty())return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		if(perfil.getNombre() == null || perfil.getNombre().isEmpty())return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		Perfil perfilAux = perfilService.findById(perfil.getId());
		if(perfilAux == null )return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		if(perfilAux.getIdEscuela().longValue() != sesion.getLicencia().getPlantel().getIdEscuela().longValue()) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		
		Perfil perfilExistente = perfilService.findByNombre(sesion.getLicencia().getPlantel().getIdEscuela(), perfil.getNombre());
		if(perfilExistente != null && perfilExistente.getId().longValue() != perfil.getId())return new ResponseEntity<Perfil>(HttpStatus.NOT_ACCEPTABLE);
		
		List<Authority> list = perfil.getAuthorities();
		if(list != null) {
			for (Authority authority : list) {
				if(!authorityService.findById(authority.getId()).isPresent())return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
			}
		}
		perfilAux.setNombre(perfil.getNombre());
		perfilAux.setDescripcion(perfil.getDescripcion());
		perfilAux.setAuthorities(list);
		perfilAux.setIdUsuarioModificado(usr.getId());
		perfilAux.setFechaDeModificacion(LocalDateTime.now());
		perfilService.update(perfilAux);
		return new ResponseEntity<Perfil>(perfilAux,HttpStatus.OK);
	}
	@DeleteMapping
	public ResponseEntity<Boolean> delete(@RequestParam(value="token") String token,@RequestParam Long idPerfil){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, PERFIL_ELIMINAR)) return new ResponseEntity<Boolean>(HttpStatus.UNAUTHORIZED);
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null)return new ResponseEntity<Boolean>(HttpStatus.UNAUTHORIZED);
		if(usr == null)return new ResponseEntity<Boolean>(HttpStatus.UNAUTHORIZED);
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Boolean>(HttpStatus.UNAUTHORIZED);
		}
		Perfil perfil = perfilService.findById(idPerfil);
		if(perfil == null )return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
		if(perfil.getIdEscuela().longValue() != sesion.getLicencia().getPlantel().getIdEscuela().longValue()) {
			return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
		}
		final Catalogo CAT_ELIMINADO = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		
		perfil.setEstatus(CAT_ELIMINADO);
		perfil.setIdUsuarioModificado(usr.getId());
		perfil.setFechaDeModificacion(LocalDateTime.now());
		perfil = perfilService.update(perfil);
		perfilService.delete(perfil.getId());
		return new ResponseEntity<Boolean>(true,HttpStatus.OK);
	}
	
}
