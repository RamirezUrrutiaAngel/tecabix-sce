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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Estado;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.EstadoService;
import mx.tecabix.db.service.MunicipioService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.EstadoPage;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */

@RestController
@RequestMapping("estado/v1")
public final class EstadoControllerV01 extends Auth{
	
	private static final Logger LOG = LoggerFactory.getLogger(EstadoControllerV01.class);
	private static final String LOG_URL = "/estado/v1";
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private EstadoService estadoService;
	@Autowired
	private MunicipioService municipioService;
	
	private static final String ESTADO = "ESTADO";
	
	/**
	 * 
	 * @param by:		NOMBRE, ABREVIATURA
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los estados paginado.", 
			notes = "<b>by:</b> NOMBRE, ABREVIATURA<br/><b>order:</b> ASC, DESC")
	@GetMapping
	public ResponseEntity<EstadoPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		if(isNotAuthorized(token)) {
			return new ResponseEntity<EstadoPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Estado> estados = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<EstadoPage>(HttpStatus.BAD_REQUEST);
		}
		if(search == null || search.isEmpty()) {
			estados = estadoService.findByActivo(elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				estados = estadoService.findByLikeNombre(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("ABREVIATURA")) {
				estados = estadoService.findByLikeAbreviatura(text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<EstadoPage>(HttpStatus.BAD_REQUEST);
			}
		}
		EstadoPage body = new EstadoPage(estados);
		return new ResponseEntity<EstadoPage>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Guarda la entidad federativa")
	@PostMapping
	public ResponseEntity<Estado> save(
			@RequestParam(value="token") UUID token, @RequestBody Estado estado) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, ESTADO);
		if(sesion == null) {
			return new ResponseEntity<Estado>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Estado.SIZE_NOMBRE, estado.getNombre())) {
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<Estado>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA, Estado.SIZE_ABREVIATURA, estado.getAbreviatura())) {
			LOG.info("{}El formato de la abreviatura es incorrecto.",headerLog);
			return new ResponseEntity<Estado>(HttpStatus.BAD_REQUEST);
		}else {
			estado.setAbreviatura(estado.getAbreviatura().strip());
		}
		if(isValid(estado.getMunicipios())) {
			for(Municipio municipio: estado.getMunicipios()) {
				if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Municipio.SIZE_NOMBRE, municipio.getNombre())) {
					LOG.info("{}El formato del nombre del municipio es incorrecto.",headerLog);
					return new ResponseEntity<Estado>(HttpStatus.BAD_REQUEST);
				}
			}
		}
		Catalogo ACTIVO =  singletonUtil.getActivo();
		estado.setId(null);
		estado.setEstatus(ACTIVO);
		estado.setFechaDeModificacion(LocalDateTime.now());
		estado.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		estado.setClave(UUID.randomUUID());
		List<Municipio> municipios = estado.getMunicipios();
		Estado entidadFederativa = estadoService.save(estado);
		if(isValid(estado.getMunicipios())) {
			entidadFederativa.setMunicipios(
				municipios.stream().map(x -> {
					x.setId(null);
					x.setEntidadFederativa(entidadFederativa);
					x.setEstatus(ACTIVO);
					x.setFechaDeModificacion(LocalDateTime.now());
					x.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
					x.setClave(UUID.randomUUID());
					return municipioService.save(x);
				}).collect(Collectors.toList())
			);
		}
		return new ResponseEntity<Estado>(entidadFederativa,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualizar la entidad federativa")
	@PutMapping
	public ResponseEntity<Estado> update(
			@RequestParam(value="token") UUID token, @RequestBody Estado estado) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, ESTADO);
		if(sesion == null) {
			return new ResponseEntity<Estado>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPut(idEmpresa, LOG_URL);
		if(isNotValid(estado.getClave())) {
			LOG.info("{}No se a mandado la clave.",headerLog);
			return new ResponseEntity<Estado>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Estado.SIZE_NOMBRE, estado.getNombre())) {
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<Estado>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA, Estado.SIZE_ABREVIATURA, estado.getAbreviatura())) {
			LOG.info("{}El formato de la abreviatura es incorrecto.",headerLog);
			return new ResponseEntity<Estado>(HttpStatus.BAD_REQUEST);
		}else {
			estado.setAbreviatura(estado.getAbreviatura().strip());
		}
		Optional<Estado> optionalEstado = estadoService.findByClave(estado.getClave());
		if(optionalEstado.isEmpty()) {
			LOG.info("{}No se encontro la clave.",headerLog);
			return new ResponseEntity<Estado>(HttpStatus.NOT_FOUND);
		}
		
		Estado entidadFederativa = optionalEstado.get();
		if(!entidadFederativa.getEstatus().equals(singletonUtil.getActivo())) {
			LOG.info("{}El estado no se encuentra activo.",headerLog);
			return new ResponseEntity<Estado>(HttpStatus.NOT_FOUND);
		}
		entidadFederativa.setFechaDeModificacion(LocalDateTime.now());
		entidadFederativa.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		entidadFederativa.setNombre(estado.getNombre());
		entidadFederativa.setAbreviatura(estado.getAbreviatura());
		
		estado = estadoService.save(entidadFederativa);
		
		return new ResponseEntity<Estado>(estado,HttpStatus.OK);
	}
}
