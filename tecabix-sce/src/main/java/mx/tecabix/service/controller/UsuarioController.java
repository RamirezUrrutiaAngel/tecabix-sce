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
package mx.tecabix.service.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import mx.tecabix.db.service.UsuarioPersonaService;
import mx.tecabix.db.service.UsuarioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("usuario")
public class UsuarioController extends Auth {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private CatalogoService catalogoService;
	
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
		
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		
		return new ResponseEntity<Usuario>(sesion.getUsuario(), HttpStatus.OK);
	}
	
	@GetMapping("findIfIsExist")
	public ResponseEntity<Boolean> findByNameRegardlessOfStatus(@RequestParam(value="nombre") String nombre){
		
		Usuario usr = usuarioService.findByNameRegardlessOfStatus(nombre);
		if(usr == null) {
			return new ResponseEntity<Boolean>(false,HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	
	@PostMapping()
	public ResponseEntity<Usuario> save(@RequestBody Usuario usuario, @RequestParam(value="token") String token) {
		Sesion sesion = getSessionIfIsAuthorized(token,USUARIO_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if(usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(usuario.getNombre().length()>8) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(usuario.getUsuarioPersona() == null || usuario.getUsuarioPersona().getPersona() == null || usuario.getUsuarioPersona().getPersona().getId() == null) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		Optional<Persona> personaOptional = personaService.findById(usuario.getUsuarioPersona().getPersona().getId());
		if(!personaOptional.isPresent()) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_ACCEPTABLE);
		}
		Persona persona = personaOptional.get();
		if( !persona.getEstatus().getNombre().equals(ACTIVO) || persona.getUsuarioPersona()!=null) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		if(usuarioService.findByNameRegardlessOfStatus(usuario.getNombre())!= null) {
			return new ResponseEntity<Usuario>(HttpStatus.CONFLICT);
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<Usuario>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo catalogoActivo = optionalCatalogoActivo.get();
		
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuario.setFechaDeModificacion(LocalDateTime.now());
		usuario.setIdUsuarioModificado(sesion.getUsuario().getId());
		usuario.setEstatus(catalogoActivo);
		usuario = usuarioService.save(usuario);
		UsuarioPersona usuarioPersona = new UsuarioPersona();
		usuarioPersona.setUsuario(usuario);
		usuarioPersona.setPersona(persona);
		usuarioPersona.setFechaDeModificacion(LocalDateTime.now());
		usuarioPersona.setIdUsuarioModificado(sesion.getUsuario().getId());
		usuarioPersona.setEstatus(catalogoActivo);
		usuarioPersona = usuarioPersonaService.save(usuarioPersona);
		return new ResponseEntity<Usuario>(usuario,HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario, @RequestParam(value="token") String token) {
		Sesion sesion = getSessionIfIsAuthorized(token,USUARIO_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		Usuario usr = sesion.getUsuario();
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
		Sesion sesion = getSessionIfIsAuthorized(token,USUARIO_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		if(usuario.getId() == null || usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		Optional<Usuario> usuarioUpdateOptional = usuarioService.findById(usuario.getId());
		
		if(!usuarioUpdateOptional.isPresent()) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
		}
		Usuario usuarioUpdate= usuarioUpdateOptional.get();
		if(usuarioUpdate == null || usuarioUpdate.getUsuarioPersona() == null) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
		}
		
		if(usuarioUpdate.getUsuarioPersona() == null || usuarioUpdate.getUsuarioPersona().getPersona().getIdEscuela() == null) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
		}
		if(!sesion.getLicencia().getPlantel().getIdEscuela().equals(usuarioUpdate.getUsuarioPersona().getPersona().getIdEscuela())) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
		}
		usuarioUpdate.setFechaDeModificacion(LocalDateTime.now());
		usuarioUpdate.setCorreo(usuario.getCorreo());
		if(usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			usuarioUpdate.setPassword(passwordEncoder.encode(usuario.getPassword()));
		}
		
		usuarioUpdate = usuarioService.save(usuarioUpdate);
		return new ResponseEntity<Usuario>(usuarioUpdate,HttpStatus.OK);
	}
}
