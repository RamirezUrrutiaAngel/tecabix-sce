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
package mx.tecabix.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.UsuarioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public class Auth extends Notificacion{
	
	
	@Autowired 
	private UsuarioService usuarioService;
	@Autowired
	private SesionService sesionService;
	
	
	protected boolean hash(Authentication authentication, String... authorities) {
		Collection<? extends GrantedAuthority> collectionAuthorities = authentication.getAuthorities();
		List<String> authoritiesList = Arrays.asList(authorities);
		for (GrantedAuthority grantedAuthority : collectionAuthorities) {
			if(authoritiesList.contains(grantedAuthority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean isAuthorized(String token, String... authorities) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(authorities != null && authorities.length > 0) {
			if(!hash(auth, authorities)) {
				return false;
			}
		}
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null) {
			return false;
		}
		if(usr == null) {
			return false;
		}
		if(sesion.getUsuario().getId().longValue() != usr.getId().longValue()) {
			return false;
		}
		return true;
	}
	
	protected boolean isNotAuthorized(String token, String... authorities) {
		return !isAuthorized(token, authorities);
	}
	
	protected Sesion getSessionIfIsAuthorized(String token, String... authorities) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(authorities != null && authorities.length > 0) {
			if(!hash(auth, authorities)) {
				return null;
			}
		}
		Sesion sesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Usuario usr = usuarioService.findByNombre(usuarioName);
		if(sesion == null) {
			return null;
		}
		if(usr == null) {
			return null;
		}
		if(sesion.getUsuario().getId().longValue() != usr.getId().longValue()) {
			return null;
		}
		return sesion;
	}
	
	protected boolean validateArg(Object... args) {
		for (Object arg : args) {
			if(arg == null) {
				return false;
			}
			if(arg.getClass().equals(String.class)) {
				if(arg.toString().isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
}
