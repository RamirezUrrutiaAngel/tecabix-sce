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
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

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
public class Auth extends Encrypt{

	private static final String LOGMS = "\nEMPRESA: :EMPRESA \n:PET : :PATH \n";
	
	private static final String GET = "GET";
	private static final String PUT = "PUT";
	private static final String POST = "POST";
	private static final String DELETE = "DELETE";
	
	@Autowired 
	protected UsuarioService usuarioService;
	@Autowired
	protected SesionService sesionService;
	
	private String formatHeaderOfLogger(long idEmpresa, String peticion, String path) {
		return LOGMS.replaceFirst(":EMPRESA", String.valueOf(idEmpresa)).replaceFirst(":PET", peticion)
				.replaceFirst(":PATH", path);
	}
	
	protected String formatLogGet(long idEmpresa, String path){
		return formatHeaderOfLogger(idEmpresa, GET, path);
	}
	protected String formatLogPost(long idEmpresa, String path){
		return formatHeaderOfLogger(idEmpresa, POST, path);
	}
	protected String formatLogPut(long idEmpresa, String path){
		return formatHeaderOfLogger(idEmpresa, PUT, path);
	}
	protected String formatLogDelete(long idEmpresa, String path){
		return formatHeaderOfLogger(idEmpresa, DELETE, path);
	}

	protected final boolean hash(Authentication authentication, String... authorities) {
		Collection<? extends GrantedAuthority> collectionAuthorities = authentication.getAuthorities();
		List<String> authoritiesList = Arrays.asList(authorities);
		for (GrantedAuthority grantedAuthority : collectionAuthorities) {
			if(authoritiesList.contains(grantedAuthority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
	
	protected final boolean isAuthorized(UUID token, String... authorities) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(authorities != null && authorities.length > 0) {
			if(!hash(auth, authorities)) {
				return false;
			}
		}
		Optional<Sesion> optioonalSesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Optional<Usuario> OptionalUsuario = usuarioService.findByNombre(usuarioName);
		if(optioonalSesion.isEmpty()) {
			return false;
		}
		if(OptionalUsuario.isEmpty()) {
			return false;
		}
		Sesion sesion = optioonalSesion.get();
		Usuario usuario = OptionalUsuario.get();
		if(sesion.getUsuario().getId().longValue() != usuario.getId().longValue()) {
			return false;
		}
		return true;
	}
	
	protected final boolean isNotAuthorized(UUID token, String... authorities) {
		return !isAuthorized(token, authorities);
	}
	
	protected final Sesion getSessionIfIsAuthorized(UUID token, String... authorities) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(authorities != null && authorities.length > 0) {
			if(!hash(auth, authorities)) {
				return null;
			}
		}
		Optional<Sesion> optioonalSesion = sesionService.findByToken(token);
		String usuarioName = auth.getName();
		Optional<Usuario> OptionalUsuario = usuarioService.findByNombre(usuarioName);
		if(optioonalSesion.isEmpty()) {
			return null;
		}
		if(OptionalUsuario.isEmpty()) {
			return null;
		}
		Sesion sesion = optioonalSesion.get();
		Usuario usuario = OptionalUsuario.get();
		if(sesion.getUsuario().getId().longValue() != usuario.getId().longValue()) {
			return null;
		}
		return sesion;
	}
	
	public static final byte TIPO_OBJECT = -1;
	public static final byte TIPO_ALFA = 0;
	public static final byte TIPO_ALFA_NUMERIC = 1;
	public static final byte TIPO_ALFA_NUMERIC_SPACE = 2;
	public static final byte TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS = 3;
	public static final byte TIPO_TEL = 4;
	public static final byte TIPO_EMAIL = 5;
	public static final byte TIPO_NUMERIC = 6;
	public static final byte TIPO_VARIABLE = 7;
	public static final byte TIPO_NUMERIC_SPACE = 8;
	/**
	 * x = { x ∈ N | x < maxZise }  
	 * x = { 0, 1, 2, 3, ... ,maxZise }
	 */
	public static final byte TIPO_NUMERIC_NATURAL = 9;
	/**
	 * x = { x ∈ N | 0 < x < maxZise }  
	 * x = { 1, 2, 3, 4, ... ,maxZise }
	 */
	public static final byte TIPO_NUMERIC_POSITIVO = 10;
	/**
	 * x = { x ∈ N | 0 > x > maxZise }  
	 * x = { -1, -2, -3, -4, ... ,maxZise }
	 */
	public static final byte TIPO_NUMERIC_NEGATIVO = 11;
	public static final byte TIPO_URL = 12;
	
	
	private static final String ALFA = "[a-zA-Z[áéíóúÁÉÍÓÚñÑ]]+";
	private static final String ALFA_NUMERIC = "[a-zA-Z0-9[áéíóúÁÉÍÓÚñÑ]]+";
	private static final String ALFA_NUMERIC_SPACE = "[a-zA-Z0-9[áéíóúÁÉÍÓÚñÑ\\s]]+";
	private static final String ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS = "[a-zA-Z0-9[.,():¿?!¡_&%$#@|áéíóúÁÉÍÓÚñÑ\\s]]+";
	private static final String TEL = "([(]{1}[0-9]{2,3}[)]{1}[\\s]{1}){0,1}([0-9]{2,4}[\\s])*[0-9]{2,8}";
	private static final String EMAIL = "[a-zA-Z0-9]{1}[a-zA-Z0-9[._]]*[a-zA-Z0-9]{1}[@]{1}[a-zA-Z0-9]+[a-zA-Z]{1}[a-zA-Z0-9]+([.]{1}[a-zA-Z]{2,4}){1,2}";
	private static final String VARIABLE = "[a-zA-Z]+([_]{1}[a-zA-Z0-9]+)*[a-zA-Z0-9]+";
	private static final String NUMERIC = "[0-9]+";
	private static final String NUMERIC_SPACE = "[0-9]+[0-9[\\s]]*[0-9]+";
	private static final String URL = "^((((https?|ftps?|gopher|telnet|nntp)://)|(mailto:|news:))(%{2}|[-()_.!~*’;/?:@&=+$, A-Za-z0-9])+)([).!’;/?:, ][[:blank:]])?$\n";
	
	
	protected boolean isNotValid(Object arg) {
		return isNotValid(TIPO_OBJECT, Integer.MAX_VALUE, arg);
	}
	protected boolean isValid(Object arg) {
		return isValid(TIPO_OBJECT,Integer.MAX_VALUE, arg);
	}
	protected boolean isNotValid(double size,Object arg) {
		return isNotValid(TIPO_OBJECT,size, arg);
	}
	protected boolean isValid(double size,Object arg) {
		return isValid(TIPO_OBJECT,size, arg);
	}
	protected boolean isNotValid(byte tipo,double size, Object arg) {
		return !isValid(tipo,size, arg);
	}
	protected final boolean isValid(byte tipo,double size, Object arg) {
		if(arg == null) {
			return false;
		}
		if(arg instanceof String || arg instanceof StringBuilder) {
			String text = arg.toString();
			if(text.isBlank()|| text.length() > size) {
				return false;
			}
			if(tipo == TIPO_ALFA) {
				return Pattern.matches(ALFA, text);
			}else if(tipo == TIPO_ALFA_NUMERIC) {
				return Pattern.matches(ALFA_NUMERIC, text);
			}else if(tipo == TIPO_ALFA_NUMERIC_SPACE) {
				return Pattern.matches(ALFA_NUMERIC_SPACE, text);
			}else if(tipo == TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS) {
				return Pattern.matches(ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, text);
			}else if(tipo == TIPO_TEL) {
				return Pattern.matches(TEL, text);
			}else if(tipo == TIPO_EMAIL) {
				return Pattern.matches(EMAIL, text);
			}else if(tipo == TIPO_NUMERIC) {
				return Pattern.matches(NUMERIC, text);
			}else if(tipo == TIPO_NUMERIC_SPACE) {
				return Pattern.matches(NUMERIC_SPACE, text);
			}else if(tipo == TIPO_VARIABLE){
				return Pattern.matches(VARIABLE, text);
			}else if(tipo == TIPO_URL){
				return Pattern.matches(URL, text);
			}
		} else if (arg instanceof Integer || arg instanceof Float || arg instanceof Long || arg instanceof Double) {
			Double num = Double.parseDouble(arg.toString());
			if (tipo == TIPO_NUMERIC_NATURAL) {
				return num >= 0 && num <= size;
			} else if (tipo == TIPO_NUMERIC_POSITIVO) {
				return num > 0 && num <= size;
			} else if (tipo == TIPO_NUMERIC_NEGATIVO) {
				return num < 0 && num >= ((size < 0) ? size : -1 * size);
			} else if (tipo == TIPO_NUMERIC) {
				return num <= size;
			}
		}
		return true;
	}
}
