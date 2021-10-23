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

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Cuenta;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CuentaService;
import mx.tecabix.service.Auth;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("cuenta/v1")
public class CuentaControllerV01 extends Auth{
	private static final Logger LOG = LoggerFactory.getLogger(CuentaControllerV01.class);
	private static final String LOG_URL = "/cuenta/v1";
	
	@Autowired
	private CuentaService cuentaService;
	
	@GetMapping
	public ResponseEntity<Cuenta> findCuenta(@RequestParam(value = "token") UUID token){
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<Cuenta>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogGet(idEmpresa, LOG_URL);
		
		if(sesion.getUsuario() == null) {
			LOG.error("{}No se encontro el usuario de la sesion {}.",headerLog, sesion.getId());
			return new ResponseEntity<Cuenta>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(sesion.getUsuario().getUsuarioPersona() == null) {
			LOG.error("{}No se encontro el usuario persona del usuario {}.",headerLog, sesion.getUsuario().getId());
			return new ResponseEntity<Cuenta>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(sesion.getUsuario().getUsuarioPersona().getPersona() == null) {
			LOG.error("{}No se encontro la persona del usuario {}.",headerLog, sesion.getUsuario().getUsuarioPersona().getId());
			return new ResponseEntity<Cuenta>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Optional<Cuenta> optionalCuenta = cuentaService.findByPersona(sesion.getUsuario().getUsuarioPersona().getPersona().getId());
		if(optionalCuenta.isEmpty()) {
			LOG.info("{}No se encontro la cuenta de la persona {}.",headerLog, sesion.getUsuario().getUsuarioPersona().getPersona().getId());
			return new ResponseEntity<Cuenta>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Cuenta>(optionalCuenta.get(),HttpStatus.OK);
	}
}
