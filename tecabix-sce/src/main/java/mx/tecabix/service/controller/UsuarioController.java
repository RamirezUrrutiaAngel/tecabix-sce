package mx.tecabix.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.UsuarioService;

@RestController
@RequestMapping("Usuario")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	
	
		
	@GetMapping("findByNombre")
	public ResponseEntity<Usuario> findByNombre(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String usuario = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuario);
		if(usr == null)return new ResponseEntity<Usuario>(HttpStatus.CONFLICT);
		usr.setPassword(new String());
		return new ResponseEntity<Usuario>(usr, HttpStatus.OK);
	}
	
	@GetMapping("findByNameRegardlessOfStatus")
	public ResponseEntity<Usuario> findByNameRegardlessOfStatus(@RequestParam(value="nombre") String nombre){
		
		Usuario usr = usuarioService.findByNameRegardlessOfStatus(nombre);
		if(usr == null)return new ResponseEntity<Usuario>(HttpStatus.CONFLICT);
		usr.setPassword(new String());
		return new ResponseEntity<Usuario>(usr, HttpStatus.OK);
	}
	

}
