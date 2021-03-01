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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

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
public class Auth {
	
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
	

    /**
     * Crea la clave de encriptacion usada internamente
     * @param key Clave o llave que se usara para encriptar
     * @return Clave de encriptacion
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException 
     */
    protected SecretKeySpec crearClave(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] claveEncriptacion = key.getBytes("UTF-8");
        
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        
        claveEncriptacion = sha.digest(claveEncriptacion);
        claveEncriptacion = Arrays.copyOf(claveEncriptacion, 16);
        
        SecretKeySpec secretKey = new SecretKeySpec(claveEncriptacion, "AES");

        return secretKey;
    }
    

    /**
     * Aplica la encriptacion AES a la cadena de texto usando la clave indicada
     * @param texto a encriptar
     * @param key Clave para encriptar
     * @return Información encriptada
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException 
     */
    protected String encriptar(String texto, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = this.crearClave(key);
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] datosEncriptar = texto.getBytes("UTF-8");
        byte[] bytesEncriptados = cipher.doFinal(datosEncriptar);
        String encriptado = Base64.getEncoder().encodeToString(bytesEncriptados);

        return encriptado;
    }

    /**
     * Desencripta la cadena de texto indicada usando la clave de encriptacion
     * @param textoEncriptado Datos encriptados
     * @param key Clave de encriptacion
     * @return Informacion desencriptada
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException 
     */
    protected String desencriptar(String textoEncriptado, String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec secretKey = this.crearClave(key);

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] bytesEncriptados = Base64.getDecoder().decode(textoEncriptado);
        byte[] datosDesencriptados = cipher.doFinal(bytesEncriptados);
        String datos = new String(datosDesencriptados);
        
        return datos;
    }

}
