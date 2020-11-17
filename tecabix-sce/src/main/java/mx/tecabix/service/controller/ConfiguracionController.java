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
import mx.tecabix.db.entity.Configuracion;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.ConfiguracionService;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.UsuarioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("configuracion")
public class ConfiguracionController {
	
	@Autowired 
	private UsuarioService usuarioService;
	@Autowired
	private SesionService sesionService;
	@Autowired
	private ConfiguracionService configuracionService;
	private static final String CONFIGURACION = "CONFIGURACION";
	private static final String CONFIGURACION_EDITAR = "CONFIGURACION_EDITAR";
	private static final String ROOT_CONFIGURACION = "ROOT_CONFIGURACION";
	private static final String ROOT_CONFIGURACION_CREAR = "ROOT_CONFIGURACION_CREAR";
	private static final String ROOT_CONFIGURACION_EDITAR = "ROOT_CONFIGURACION_EDITAR";
	private static final String ROOT_CONFIGURACION_ELIMINAR = "ROOT_CONFIGURACION_ELIMINAR";
	
	
	@GetMapping
	public ResponseEntity<Page<Configuracion>> findByIdEscuela(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, CONFIGURACION, ROOT_CONFIGURACION)) {
			return new ResponseEntity<Page<Configuracion>>(HttpStatus.UNAUTHORIZED);
		}
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null) {
			return new ResponseEntity<Page<Configuracion>>(HttpStatus.UNAUTHORIZED);
		}
		if(usr == null) {
			return new ResponseEntity<Page<Configuracion>>(HttpStatus.UNAUTHORIZED);
		}
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Page<Configuracion>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Configuracion> configuraciones = configuracionService.findByIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela(), elements, page);
		return new ResponseEntity<Page<Configuracion>>(configuraciones,HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Configuracion> save(@RequestBody Configuracion configuracion, @RequestParam(value="token") String token ){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, ROOT_CONFIGURACION_CREAR)) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(usr == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		return null;
	}
	
	@PutMapping
	public ResponseEntity<Configuracion> updateRoot(@RequestBody Configuracion configuracion, @RequestParam(value="token") String token ){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, ROOT_CONFIGURACION_EDITAR)) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(usr == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		return null;
	}
	
	@PutMapping
	public ResponseEntity<Configuracion> update(@RequestBody Configuracion configuracion, @RequestParam(value="token") String token ){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, CONFIGURACION_EDITAR)) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(usr == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		return null;
	}
	
	
	@DeleteMapping
	public ResponseEntity<Configuracion> deleteRoot(@RequestBody Configuracion configuracion, @RequestParam(value="token") String token ){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, ROOT_CONFIGURACION_ELIMINAR)) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(usr == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		return null;
	}
	
}
