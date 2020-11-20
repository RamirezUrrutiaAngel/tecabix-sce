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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.Auth;
import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.AuthorityService;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.UsuarioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("authority")
public class AuthorityController {
	
	private static final String AUTHORITY = "AUTHORITY";
	private static final String PERFIL = "PERFIL";
	
	@Autowired
	private AuthorityService authorityService;
	@Autowired 
	private UsuarioService usuarioService;
	@Autowired
	private SesionService sesionService;
	
	@GetMapping("findAll")
	public ResponseEntity<List<Authority>> findAll(@RequestParam(value="token") String token) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, AUTHORITY,PERFIL)) {
			return new ResponseEntity<List<Authority>>(HttpStatus.UNAUTHORIZED);
		}
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null) {
			return new ResponseEntity<List<Authority>>(HttpStatus.UNAUTHORIZED);
		}
		if(usr == null) {
			return new ResponseEntity<List<Authority>>(HttpStatus.UNAUTHORIZED);
		}
		if(sesion.getIdUsuarioModificado().longValue() != usr.getId().longValue()) {
			return new ResponseEntity<List<Authority>>(HttpStatus.UNAUTHORIZED);
		}
		
		List<Authority> authorities = authorityService.findAll();
		if(authorities != null) {
			for (Authority authority : authorities) {
				authority.setPerfiles(null);
				authority.setSubAuthority(null);
			}
		}
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>(auth.getAuthorities());
		for (GrantedAuthority grantedAuthority : list) {
			System.out.println(grantedAuthority.getAuthority());
		}
		ResponseEntity<List<Authority>> response = new ResponseEntity<List<Authority>>(authorities, HttpStatus.OK);
		return response;
	}
}
