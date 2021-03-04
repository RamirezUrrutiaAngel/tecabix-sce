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

import mx.tecabix.db.entity.Plan;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Suscripcion;
import mx.tecabix.db.service.PlanService;
import mx.tecabix.db.service.SuscripcionService;
import mx.tecabix.service.Auth;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("plan/v1")
public class PlanControllerV01 extends Auth {

	@Autowired
	private PlanService planService;
	@Autowired
	private SuscripcionService suscripcionService;
	
	private final String PLAN = "PLAN";
	private final String ROOT_PLAN = "ROOT_PLAN";
	private final String ROOT_PLAN_CREAR = "ROOT_PLAN_CREAR";
	private final String ROOT_PLAN_EDITAR = "ROOT_PLAN_EDITAR";
	
	
	@GetMapping("findAll")
	public ResponseEntity<Page<Plan>> findAll(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		Sesion sesion = getSessionIfIsAuthorized(token,ROOT_PLAN);
		if(sesion == null){
			return new ResponseEntity<Page<Plan>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Plan> pagePlan = planService.findAll(elements, page);
		return new ResponseEntity<Page<Plan>>(pagePlan,HttpStatus.OK);
	}	
	
	@GetMapping
	public ResponseEntity<Plan> get(@RequestParam(value="token") String token) {
		Sesion sesion = getSessionIfIsAuthorized(token,PLAN);
		if(sesion == null){
			return new ResponseEntity<Plan>(HttpStatus.UNAUTHORIZED);
		}
		long idEscuela = sesion.getLicencia().getPlantel().getIdEscuela();
		Optional<Suscripcion> optionalSuscripcion = suscripcionService.findByIdEscuela(idEscuela);
		if(!optionalSuscripcion.isPresent()) {
			return new ResponseEntity<Plan>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Plan body =  optionalSuscripcion.get().getPlan();
		return new ResponseEntity<Plan>(body, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Plan> post(@RequestParam(value="token") String token, @RequestBody Plan plan) {
		if(isNotAuthorized(token, ROOT_PLAN_CREAR)) {
			return new ResponseEntity<Plan>(HttpStatus.UNAUTHORIZED);
		}
		if(plan.getDescripcion() == null || plan.getDescripcion().isEmpty()) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		if(plan.getNombre() == null || plan.getNombre().isEmpty()) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		if(plan.getPrecio() == null || plan.getPrecio() < 0) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		plan = planService.save(plan);
		
		return new ResponseEntity<Plan>(plan, HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<Plan> put(@RequestParam(value="token") String token, @RequestBody Plan plan) {
		if(isNotAuthorized(token, ROOT_PLAN_EDITAR)) {
			return new ResponseEntity<Plan>(HttpStatus.UNAUTHORIZED);
		}
		if(plan.getId() == null) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		if(plan.getDescripcion() == null || plan.getDescripcion().isEmpty()) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		if(plan.getNombre() == null || plan.getNombre().isEmpty()) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		if(plan.getPrecio() == null || plan.getPrecio() < 0) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		Optional<Plan> optionalPlan = planService.findById(plan.getId());
		if(!optionalPlan.isPresent()) {
			return new ResponseEntity<Plan>(HttpStatus.NOT_FOUND);
		}
		plan = planService.save(plan);
		
		return new ResponseEntity<Plan>(plan, HttpStatus.OK);
	}
	
}
