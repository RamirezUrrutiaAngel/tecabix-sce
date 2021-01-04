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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import mx.tecabix.Auth;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.CatalogoTipo;
import mx.tecabix.db.entity.Configuracion;
import mx.tecabix.db.entity.Escuela;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.CatalogoTipoService;
import mx.tecabix.db.service.ConfiguracionService;
import mx.tecabix.db.service.EscuelaService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("configuracion")
public class ConfiguracionController extends Auth{
	
	@Autowired
	private ConfiguracionService configuracionService;
	@Autowired
	private EscuelaService escuelaService;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private CatalogoTipoService catalogoTipoService;
	
	private static final String ACTIVO = "ACTIVO";
	private static final String ESTATUS = "ESTATUS";
	private static final String CONFIGURACION = "CONFIGURACION";
	private static final String CONFIGURACION_EDITAR = "CONFIGURACION_EDITAR";
	private static final String ROOT_CONFIGURACION = "ROOT_CONFIGURACION";
	private static final String ROOT_CONFIGURACION_CREAR = "ROOT_CONFIGURACION_CREAR";
	private static final String ROOT_CONFIGURACION_EDITAR = "ROOT_CONFIGURACION_EDITAR";
	
	
	@ApiOperation(value = "Obtiene todas la configuración de la persona moral sujeta a la sesión.")
	@GetMapping("findAll")
	public ResponseEntity<Page<Configuracion>> findAll(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, CONFIGURACION) ;
		if(sesion == null) {
			return new ResponseEntity<Page<Configuracion>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Configuracion> configuraciones = configuracionService.findByIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela(), elements, page);
		return new ResponseEntity<Page<Configuracion>>(configuraciones,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Obtiene mediante el nombre la configuración de la persona moral sujeta a la sesión.")
	@GetMapping("findByNombre")
	public ResponseEntity<Configuracion> findByNombre(@RequestParam(value="token") String token,@RequestParam(value="nombre") String nombre) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, CONFIGURACION) ;
		if(sesion == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Configuracion> optionalConfiguracion = configuracionService.findByIdEscuelaAndNombre(sesion.getLicencia().getPlantel().getIdEscuela(), nombre);
		if(!optionalConfiguracion.isPresent()) {
			return new ResponseEntity<Configuracion>(HttpStatus.NOT_FOUND);
		}
		Configuracion body = optionalConfiguracion.get();
		return new ResponseEntity<Configuracion>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Obtiene todas las configuraciones con respecto a el nombre proporcionado.")
	@GetMapping("findWithRootByNombre")
	public ResponseEntity<Page<Configuracion>> findWithRootByNombre(
			@RequestParam(value="token") String token,
			@RequestParam(value="nombre") String nombre,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, ROOT_CONFIGURACION) ;
		if(sesion == null) {
			return new ResponseEntity<Page<Configuracion>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Configuracion> configuraciones = configuracionService.findByNombre(nombre, elements, page);
		
		return new ResponseEntity<Page<Configuracion>>(configuraciones,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad Configuración con sus correspondientes valor para cada una de la persona social. ", 
			notes = "Se crea un nuevo catalogo de tipo configuración, todas los registros que estén en persona moral se le creara "
			+ "un nueva configuración con el valor por defecto proporcionado.")
	@PostMapping
	public ResponseEntity<List<Configuracion>> save(@RequestBody Catalogo catalogo,@RequestParam(value="valorDefault") String valorDefault, @RequestParam(value="token") String token ){
		
		Sesion sesion = getSessionIfIsAuthorized(token, ROOT_CONFIGURACION_CREAR);
		if(sesion == null) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.UNAUTHORIZED);
		}
		if(catalogo.getNombre() == null || catalogo.getNombre().isEmpty()) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.BAD_REQUEST);
		}
		if(catalogo.getNombreCompleto() == null || catalogo.getNombreCompleto().isEmpty()) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.BAD_REQUEST);
		}
		if(catalogo.getDescripcion() == null || catalogo.getDescripcion().isEmpty()) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.BAD_REQUEST);
		}
		Optional<Catalogo> optionalTipoConfiguacion = catalogoService.findByTipoAndNombre(CONFIGURACION, catalogo.getNombre());
		if(optionalTipoConfiguacion.isPresent()) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.CONFLICT);
		}
		Optional<CatalogoTipo> optionalCatalogoTipo = catalogoTipoService.findByNombre(CONFIGURACION);
		if(!optionalCatalogoTipo.isPresent()) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ACTIVO = optionalCatalogoActivo.get();
		
		catalogo.setCatalogoTipo(optionalCatalogoTipo.get());
		catalogo = catalogoService.save(catalogo);
		List<Escuela> escuelas = escuelaService.findAll();
		List<Configuracion> configuracions = new ArrayList<Configuracion>();
		if(escuelas != null) {
			for (int i = 0; i < escuelas.size(); i++) {
				Escuela escuela = escuelas.get(i);
				Configuracion configuracion = new Configuracion();
				configuracion.setEstatus(CAT_ACTIVO);
				configuracion.setFechaDeModificacion(LocalDateTime.now());
				configuracion.setIdUsuarioModificado(sesion.getUsuario().getId());
				configuracion.setIdEscuela(escuela.getId());
				configuracion.setTipo(catalogo);
				configuracion.setValor(valorDefault);
				configuracion = configuracionService.save(configuracion);
				configuracions.add(configuracion);
			}
		}
		return new ResponseEntity<List<Configuracion>>(configuracions, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza todas las configuraciones con respecto a el nombre proporcionado con un valor por defecto.")
	@PutMapping("updateAllWithRoot")
	public ResponseEntity<List<Configuracion>> updateRoot(@RequestParam(value="nombre") String nombre, @RequestParam(value="valor") String valor, @RequestParam(value="token") String token ){
		
		Sesion sesion = getSessionIfIsAuthorized(token, ROOT_CONFIGURACION_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Catalogo> optionalTipoConfiguacion = catalogoService.findByTipoAndNombre(CONFIGURACION, nombre);
		if(!optionalTipoConfiguacion.isPresent()) {
			return new ResponseEntity<List<Configuracion>>(HttpStatus.NOT_FOUND);
		}
		final byte ZERO = 0;
		List<Configuracion> body = new ArrayList<Configuracion>();
		Page<Configuracion> configuraciones = configuracionService.findByNombre(nombre, Integer.MAX_VALUE, ZERO);
		for (Configuracion configuracion : configuraciones) {
			configuracion.setValor(valor);
			configuracion.setFechaDeModificacion(LocalDateTime.now());
			configuracion.setIdUsuarioModificado(sesion.getUsuario().getId());
			configuracion = configuracionService.update(configuracion);
			body.add(configuracion);
		}
		
		return new ResponseEntity<List<Configuracion>>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza una configuracion con respecto a el nombre proporcionado con un valor por defecto.")
	@PutMapping("updateUnityWithRoot")
	public ResponseEntity<Configuracion> updateUnityWithRoot(@RequestBody Configuracion configuracion, @RequestParam(value="token") String token ){
		
		Sesion sesion = getSessionIfIsAuthorized(token, ROOT_CONFIGURACION_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		if(configuracion.getId() == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		if(configuracion.getTipo() == null || configuracion.getTipo().getNombre() == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		if(configuracion.getIdEscuela() == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		if(configuracion.getValor() == null || configuracion.getValor().isEmpty()){
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		
		Optional<Catalogo> optionalTipoConfiguacion = catalogoService.findByTipoAndNombre(CONFIGURACION, configuracion.getTipo().getNombre());
		if(!optionalTipoConfiguacion.isPresent()) {
			return new ResponseEntity<Configuracion>(HttpStatus.NOT_FOUND);
		}
		
		Optional<Configuracion> optionalConfiguracion = configuracionService.findById(configuracion.getId());
		if(!optionalConfiguracion.isPresent()) {
			return new ResponseEntity<Configuracion>(HttpStatus.NOT_FOUND);
		}
		Configuracion body = optionalConfiguracion.get();
		body.setValor(configuracion.getValor());
		body.setFechaDeModificacion(LocalDateTime.now());
		body.setIdUsuarioModificado(sesion.getUsuario().getId());
		body = configuracionService.update(body);
		
		return new ResponseEntity<Configuracion>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza mediante la configuración de la persona moral sujeta a la sesión.")
	@PutMapping
	public ResponseEntity<Configuracion> update(
			@RequestParam(value="nombre") String nombre, 
			@RequestParam(value="valor") String valor, 
			@RequestParam(value="token") String token ){
		
		Sesion sesion = getSessionIfIsAuthorized(token, CONFIGURACION_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		Optional<Configuracion> optionalConfiguracion = configuracionService.findByIdEscuelaAndNombre(idEscuela, nombre);
		if(!optionalConfiguracion.isPresent()) {
			return new ResponseEntity<Configuracion>(HttpStatus.NOT_FOUND);
		}
		Configuracion configuracion = optionalConfiguracion.get();
		configuracion.setValor(valor);
		configuracion.setFechaDeModificacion(LocalDateTime.now());
		configuracion.setIdUsuarioModificado(sesion.getUsuario().getId());
		configuracion = configuracionService.update(configuracion);
		
		return new ResponseEntity<Configuracion>(configuracion,HttpStatus.OK);
	}
	
}
