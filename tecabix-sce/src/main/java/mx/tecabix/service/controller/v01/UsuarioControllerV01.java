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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.entity.UsuarioPersona;
import mx.tecabix.db.service.PersonaService;
import mx.tecabix.db.service.UsuarioPersonaService;
import mx.tecabix.db.service.UsuarioService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.UsuarioPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("usuario/v1")
public class UsuarioControllerV01 extends Auth {
	
	@Autowired
	private SingletonUtil singletonUtil;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired 
	private PersonaService personaService;
	
	@Autowired
	private UsuarioPersonaService usuarioPersonaService;
	
	private final String USUARIO ="USUARIO";
	private final String USUARIO_CREAR ="USUARIO_CREAR";
	private final String USUARIO_EDITAR ="USUARIO_EDITAR";
	/**
	 * 
	 * @param by:		NOMBRE, CORREO, PERFIL
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los authority paginado.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION<br/><b>order:</b> ASC, DESC")
	@GetMapping
	public ResponseEntity<UsuarioPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token,USUARIO);
		if(sesion == null) {
			return new ResponseEntity<UsuarioPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Usuario> usuarios = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<UsuarioPage>(HttpStatus.BAD_REQUEST);
		}
		if(search == null || search.isEmpty()) {
			usuarios = usuarioService.findAll(elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				usuarios = usuarioService.findByLikeNombre(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("CORREO")) {
				usuarios = usuarioService.findByLikeCorreo(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("PERFIL")) {
				usuarios = usuarioService.findByLikePerfil(text.toString(), elements, page, sort);
			}else {
				new ResponseEntity<UsuarioPage>(HttpStatus.BAD_REQUEST);
			}
		}
		UsuarioPage usuarioPage = new UsuarioPage(usuarios);
		return new ResponseEntity<UsuarioPage>(usuarioPage,HttpStatus.OK);
	}
	
	@GetMapping("is-username-accepted")
	public ResponseEntity<?> exists(@RequestParam(value="token") UUID token,@RequestParam(value="username") String usuario){
		Sesion sesion = getSessionIfIsAuthorized(token,USUARIO);
		if(sesion == null) {
			return new ResponseEntity<UsuarioPage>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(TIPO_VARIABLE, Usuario.SIZE_NOMBRE, usuario)) {
			return new ResponseEntity<Boolean>(HttpStatus.NOT_ACCEPTABLE);
		}
		Optional<Usuario> optionalUsuario = usuarioService.findByNameRegardlessOfStatus(usuario);
		if(optionalUsuario.isEmpty()) {
			return new ResponseEntity<Boolean>(HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<Boolean>(HttpStatus.NOT_ACCEPTABLE);
	}
	
	@PostMapping()
	public ResponseEntity<Usuario> save(@RequestBody Usuario usuario, @RequestParam(value="token") UUID token) {
		Sesion sesion = getSessionIfIsAuthorized(token,USUARIO_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		if(isNotValid(TIPO_EMAIL, Usuario.SIZE_CORREO, usuario.getCorreo())) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_VARIABLE, Usuario.SIZE_NOMBRE, usuario.getNombre())) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, TIPO_ALFA, usuario.getPassword())) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(usuario.getNombre().length()>8) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(usuario.getUsuarioPersona())) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(usuario.getUsuarioPersona().getClave())) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		Optional<Persona> personaOptional = personaService.findByClave(usuario.getUsuarioPersona().getPersona().getClave());
		if(personaOptional.isEmpty()) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_ACCEPTABLE);
		}
		Persona persona = personaOptional.get();
		Catalogo activo = singletonUtil.getActivo();
		if( !persona.getEstatus().getNombre().equals(activo) || persona.getUsuarioPersona()!=null) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_ACCEPTABLE);
		}
		if(usuarioService.findByNameRegardlessOfStatus(usuario.getNombre())!= null) {
			return new ResponseEntity<Usuario>(HttpStatus.CONFLICT);
		}
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuario.setFechaDeModificacion(LocalDateTime.now());
		usuario.setIdUsuarioModificado(sesion.getUsuario().getId());
		usuario.setEstatus(activo);
		usuario = usuarioService.save(usuario);
		UsuarioPersona usuarioPersona = new UsuarioPersona();
		usuarioPersona.setUsuario(usuario);
		usuarioPersona.setPersona(persona);
		usuarioPersona.setFechaDeModificacion(LocalDateTime.now());
		usuarioPersona.setIdUsuarioModificado(sesion.getUsuario().getId());
		usuarioPersona.setEstatus(activo);
		usuarioPersona = usuarioPersonaService.save(usuarioPersona);
		return new ResponseEntity<Usuario>(usuario,HttpStatus.OK);
	}
	
	@PutMapping("this")
	public ResponseEntity<Usuario> updateThis(@RequestBody Usuario usuario, @RequestParam(value="token") UUID token) {
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
	
	@PutMapping()
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario, @RequestParam(value="token") UUID token) {
		Sesion sesion = getSessionIfIsAuthorized(token,USUARIO_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<Usuario>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(usuario.getClave())) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(usuario.getCorreo())) {
			return new ResponseEntity<Usuario>(HttpStatus.BAD_REQUEST);
		}
		Optional<Usuario> usuarioUpdateOptional = usuarioService.findByClave(usuario.getClave());
		
		if(usuarioUpdateOptional.isEmpty()) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
		}
		Usuario usuarioUpdate = usuarioUpdateOptional.get();
		if(isNotValid(usuarioUpdate.getUsuarioPersona())) {
			return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
		}
		if(!sesion.getLicencia().getPlantel().getIdEmpresa().equals(usuarioUpdate.getUsuarioPersona().getPersona().getIdEmpresa())) {
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
