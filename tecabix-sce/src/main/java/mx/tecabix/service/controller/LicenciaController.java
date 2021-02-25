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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Licencia;
import mx.tecabix.db.entity.PlanServicio;
import mx.tecabix.db.entity.Plantel;
import mx.tecabix.db.entity.Servicio;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Suscripcion;
import mx.tecabix.db.service.CatalogoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
import mx.tecabix.db.service.LicenciaService;
import mx.tecabix.db.service.PlanServicioService;
import mx.tecabix.db.service.ServicioService;
import mx.tecabix.db.service.SuscripcionService;
import mx.tecabix.service.Auth;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("licencia")
public class LicenciaController extends Auth{
	
	private final String LICENCIA = "LICENCIA";
	private final String LICENCIA_CREAR = "LICENCIA_CREAR";
	private final String LICENCIA_ELIMINAR = "LICENCIA_ELIMINAR";
	private final String ROOT_LICENCIA = "ROOT_LICENCIA";
	private final String ESTATUS = "ESTATUS";
	private final String ACTIVO = "ACTIVO";
	private final String ELIMINADO = "ELIMINADO";
	
	@Autowired
	private LicenciaService licenciaService;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private ServicioService servicioService;
	@Autowired
	private PlanServicioService planServicioService;
	@Autowired
	private SuscripcionService suscripcionService;
	
	@GetMapping
	public ResponseEntity<Page<Licencia>> get(
			@RequestParam(value="token") String token,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, LICENCIA);
		if(sesion == null) {
			return new ResponseEntity<Page<Licencia>>(HttpStatus.UNAUTHORIZED);
		}
		Long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		Page<Licencia> response = licenciaService.findByIdEscuela(idEscuela, elements, page);
		return new ResponseEntity<Page<Licencia>>(response, HttpStatus.OK);
	}
	
	@GetMapping("findAll")
	public ResponseEntity<Page<Licencia>> findAll(
			@RequestParam(value="token") String token,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		if(isNotAuthorized(token, ROOT_LICENCIA)) {
			return new ResponseEntity<Page<Licencia>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Licencia> response = licenciaService.findAll(elements, page);
		return new ResponseEntity<Page<Licencia>>(response, HttpStatus.OK);
	}
	
	@GetMapping("findByIdEscuela")
	public ResponseEntity<Page<Licencia>> findByIdEscuela(
			@RequestParam(value="token") String token,
			@RequestParam(value="idEscuela") Long idEscuela,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		if(isNotAuthorized(token, ROOT_LICENCIA)) {
			return new ResponseEntity<Page<Licencia>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Licencia> response = licenciaService.findByIdEscuela(idEscuela, elements, page);
		return new ResponseEntity<Page<Licencia>>(response, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Licencia> save(
			@RequestParam(value="token") String token,
			@RequestParam(value="tipo") String tipo,
			@RequestParam(value="nombre") String nombre,
			@RequestParam(value="servicio") String servicio){
		
		byte ZERO = 0;
		
		Sesion sesion = getSessionIfIsAuthorized(token, LICENCIA_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Licencia>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Catalogo> optionalCatalogoTipo = catalogoService.findByTipoAndNombre("TIPO_DE_LICENCIA", tipo);
		if(!optionalCatalogoTipo.isPresent()) {
			return new ResponseEntity<Licencia>(HttpStatus.BAD_REQUEST);
		}
		Optional<Servicio> optionalServicio = servicioService.findByNombre(servicio);
		if (!optionalServicio.isPresent()) {
			return new ResponseEntity<Licencia>(HttpStatus.BAD_REQUEST);
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<Licencia>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ACTIVO = optionalCatalogoActivo.get();
		
		Catalogo catalogoTipo = optionalCatalogoTipo.get();
		Servicio servicioTipo = optionalServicio.get();
		Plantel plantel = sesion.getLicencia().getPlantel();
		
		Long idEscuela = plantel.getIdEscuela();
		
		Page<Licencia> licencias = licenciaService.findByIdEscuelaAndServicio(idEscuela, servicioTipo.getId(), Integer.MAX_VALUE, ZERO);
		Optional<Suscripcion> optionalSuscripcion =  suscripcionService.findByIdEscuelaAndValid(idEscuela);
		if(!optionalSuscripcion.isPresent()) {
			return new ResponseEntity<Licencia>(HttpStatus.LOCKED);
		}
		Suscripcion suscripcion = optionalSuscripcion.get();
		Optional<PlanServicio> optionalPlanServicios = planServicioService.fromByIdPlanAndIdService(suscripcion.getPlan().getId(), servicioTipo.getId());
		if(!optionalPlanServicios.isPresent()) {
			return new ResponseEntity<Licencia>(HttpStatus.NOT_FOUND);
		}
		PlanServicio planServicio = optionalPlanServicios.get();
		if (licencias.getSize() >=  planServicio.getNumeroLicencias().intValue()) {
			return new ResponseEntity<Licencia>(HttpStatus.GONE);
		}
		Licencia licencia = new Licencia();
		licencia.setNombre(nombre);
		licencia.setTipo(catalogoTipo);
		licencia.setServicio(servicioTipo);
		licencia.setPlantel(plantel);
		licencia.setEstatus(CAT_ACTIVO);
		licencia.setFechaDeModificacion(LocalDateTime.now());
		licencia.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		licencia = licenciaService.save(licencia);
		return new ResponseEntity<Licencia>(licencia,HttpStatus.OK);
	}
	
	@DeleteMapping("deleteById")
	public ResponseEntity<Boolean> deleteById(@RequestParam(value="token") String token, @RequestParam(value="id") Long id){
		Sesion sesion = getSessionIfIsAuthorized(token,LICENCIA_ELIMINAR);
		if(sesion == null) {
			return new ResponseEntity<Boolean>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Catalogo> optionalCatalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		if(!optionalCatalogoEliminado.isPresent()) {
			return new ResponseEntity<Boolean>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Catalogo catalogoEliminado = optionalCatalogoEliminado.get();
		Optional<Licencia> optionalLicencia = licenciaService.findById(id);
		if(!optionalLicencia.isPresent()) {
			return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
		}
		Licencia licencia =  optionalLicencia.get();
		licencia.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		licencia.setFechaDeModificacion(LocalDateTime.now());
		licencia.setEstatus(catalogoEliminado);
		licencia = licenciaService.update(licencia);
		return new ResponseEntity<Boolean>(true,HttpStatus.OK);
	}
	
	
}
