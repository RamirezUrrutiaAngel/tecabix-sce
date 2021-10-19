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
package mx.tecabix.service.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public class HttpService<REQUEST, RESPONS> {

	@SuppressWarnings("rawtypes")
	private final Class clase;

	private boolean https;
	private String protocolo;
	private String ip;
	private String puerto;

	private String usuario;
	private String password;

	public HttpService(Class<?> claseResponse) {
		this.clase = claseResponse;
		this.protocolo = "http";
		this.ip = "localhost";
		this.puerto = "80";
	}

	public HttpService(String ip, Class<?> claseResponse) {
		this(claseResponse);
		this.ip = ip;
	}

	public HttpService(String ip, String puerto, Class<?> claseResponse) {
		this(ip, claseResponse);
		this.puerto = puerto;
	}

	public HttpService(boolean https, String ip, String puerto, Class<?> claseRespons) {
		this(ip, puerto, claseRespons);
		this.https = https;
		this.protocolo = (https) ? "https" : "http";
	}

	public HttpService(boolean https, String ip, String puerto, String usuario, String password,
			Class<?> claseRespons) {
		this(https, ip, puerto, claseRespons);
		this.usuario = usuario;
		this.password = password;
	}

	@SuppressWarnings("unchecked")
	public RESPONS getPeticion(HttpMethod method, String url, HashMap<String, Object> arg, REQUEST body)
			throws Exception {
		StringBuilder dir = new StringBuilder();
		dir.append(protocolo).append("://").append(ip).append(":").append(puerto).append("/").append(url);
		arg = (HashMap<String, Object>) arg.clone();

		if (!arg.isEmpty()) {
			dir.append("?");
			Iterator<Map.Entry<String, Object>> iterador = arg.entrySet().iterator();
			while (iterador.hasNext()) {
				Map.Entry<String, Object> i = iterador.next();
				dir.append(i.getKey()).append("=").append(i.getValue());
				if (iterador.hasNext()) {
					dir.append("&");
				}
			}
		}

		HttpHeaders httpHeaders = new HttpHeaders();
		if (usuario != null && password != null) {
			if (!usuario.isBlank() && !password.isBlank()) {
				httpHeaders.setBasicAuth(usuario, password);
			}
		}

		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		List<MediaType> listMediaType = new ArrayList<>();
		listMediaType.add(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(listMediaType);

		HttpEntity<REQUEST> requestHttpEntity = new HttpEntity<REQUEST>(body, httpHeaders);
		ResponseEntity<RESPONS> responseEntity = new RestTemplate().exchange(dir.toString(), method, requestHttpEntity,
				clase);
		RESPONS respons = responseEntity.getBody();
		return respons;
	}

	public boolean isHttps() {
		return https;
	}

	public void setHttps(boolean https) {
		this.https = https;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
