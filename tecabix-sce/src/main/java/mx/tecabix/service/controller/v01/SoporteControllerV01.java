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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Soporte;
import mx.tecabix.db.entity.SoporteMsj;
import mx.tecabix.db.service.SoporteMsjService;
import mx.tecabix.db.service.SoporteService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("soporte/v1")
public final class SoporteControllerV01 extends Auth{

	private static final Logger LOG = LoggerFactory.getLogger(SoporteControllerV01.class);
	private static final String LOG_URL = "/soporte/v1";
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private SoporteService soporteService;
	@Autowired
	private SoporteMsjService soporteMsjService;
	
	@ApiOperation(value = "Envia un ticket a soporte. ")
	@PostMapping()
	public ResponseEntity<Soporte> save(@RequestParam(value="token") UUID token, @RequestBody Soporte soporte){
		
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Soporte>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Soporte.SIZE_ASUNTO, soporte.getAsunto())){
			LOG.info("{}El formato del ausnto es incorrecto.",headerLog);
			return new ResponseEntity<Soporte>(HttpStatus.BAD_REQUEST);
		}else {
			soporte.setAsunto(soporte.getAsunto().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Soporte.SIZE_DESCRIPCION, soporte.getDescripcion())){
			LOG.info("{}El formato del ausnto es incorrecto.",headerLog);
			return new ResponseEntity<Soporte>(HttpStatus.BAD_REQUEST);
		}else {
			soporte.setDescripcion(soporte.getDescripcion().strip());
		}
		List<SoporteMsj> soporteMensajes = soporte.getSoporteMsjs();
		if(isValid(soporteMensajes)) {
			for(SoporteMsj msj: soporteMensajes) {
				if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, SoporteMsj.SIZE_CONTENIDO, msj.getContenido())) {
					LOG.info("{}El formato del contenido es incorrecto.",headerLog);
					return new ResponseEntity<Soporte>(HttpStatus.BAD_REQUEST);
				}else {
					msj.setContenido(msj.getContenido().strip());
				}
			}
		}
		
		Catalogo ACTIVO = singletonUtil.getActivo();
		soporte.setId(null);
		soporte.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		soporte.setFechaDeModificacion(LocalDateTime.now());
		soporte.setEstatus(ACTIVO);
		soporte.setClave(UUID.randomUUID());
		Soporte aux = soporteService.save(soporte);
		if(isValid(soporteMensajes)) {
			aux.setSoporteMsjs(
				soporteMensajes.stream().map(x->{
					x.setId(null);
					x.setSoporte(aux);
					x.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
					x.setFechaDeModificacion(LocalDateTime.now());
					x.setEstatus(ACTIVO);
					x.setClave(UUID.randomUUID());
					return soporteMsjService.save(x);
				}).collect(Collectors.toList())
			);
		}
		return new ResponseEntity<Soporte>(aux,HttpStatus.OK);
	}
}
