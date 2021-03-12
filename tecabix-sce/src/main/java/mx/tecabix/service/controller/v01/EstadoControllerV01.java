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

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Estado;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.EstadoService;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.service.Auth;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("estado/v1")
public class EstadoControllerV01 extends Auth{
	
	@Autowired
	private EstadoService estadoService;

	@Autowired
	private SesionService sesionService;
	
	@GetMapping("all")
	public ResponseEntity<List<Estado>> all(@RequestParam(value="token") UUID token) {
		Sesion sesion = sesionService.findByToken(token);
		if(sesion == null) {
			return new ResponseEntity<List<Estado>>(HttpStatus.UNAUTHORIZED);
		}
		List<Estado> estados = estadoService.findAll();
		for (Estado estado : estados) {
			estado.setMunicipios(null);
		}
		return new ResponseEntity<List<Estado>>(estados, HttpStatus.OK);
	}
	
	// INICIO DE SERVICIO NO PROTEGIDO CON AUTENTIFICACION
	@GetMapping("all-join-municipio")
	public ResponseEntity<List<Estado>> allJoinMunicipio(@RequestParam(value="token") UUID token) {
		Sesion sesion = sesionService.findByToken(token);
		if(sesion == null) {
			return new ResponseEntity<List<Estado>>(HttpStatus.UNAUTHORIZED);
		}
		List<Estado> estados = estadoService.findAll();
		return new ResponseEntity<List<Estado>>(estados, HttpStatus.OK);
	}
	// FIN DE SERVICIO NO PROTEGIDO CON AUTENTIFICACION
	
	
}
