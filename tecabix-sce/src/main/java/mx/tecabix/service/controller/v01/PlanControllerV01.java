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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.CatalogoTipo;
import mx.tecabix.db.entity.Configuracion;
import mx.tecabix.db.entity.Plan;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CatalogoTipoService;
import mx.tecabix.db.service.ConfiguracionService;
import mx.tecabix.db.service.PlanService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("plan/v1")
public final class PlanControllerV01 extends Auth {

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private PlanService planService;
	@Autowired
	private CatalogoTipoService catalogoTipoService;
	@Autowired
	private ConfiguracionService configuracionService;
	
	private final String PLAN_CREAR = "PLAN_CREAR";
	
	private final String CONFIGURACION_PLAN = "CONFIGURACION_PLAN";
	
	
	@PostMapping
	public ResponseEntity<Plan> save(
			@RequestParam(value="token") UUID token,
			@RequestBody Plan plan) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, PLAN_CREAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Plan>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(TIPO_VARIABLE, Plan.SIZE_NOMBRE, plan.getNombre())){
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Plan.SIZE_DESCRIPCION, plan.getDescripcion())) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}else {
			plan.setDescripcion(plan.getDescripcion().strip());
		}
		if(isNotValid(TIPO_NUMERIC_NATURAL, Float.MAX_VALUE, plan.getPrecio())) {
			return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
		}
		Catalogo ACTIVO = singletonUtil.getActivo();
		List<Configuracion> configuraciones = plan.getConfiguraciones();
		Optional<CatalogoTipo> optionalCatalogoTipo = catalogoTipoService.findByNombre(CONFIGURACION_PLAN);
		if(optionalCatalogoTipo.isPresent()) {
			List<Catalogo> catalogos = optionalCatalogoTipo.get().getCatalogos();
			if(catalogos != null) {
				final byte NUM_DIGITOS = 9;
				
				if(configuraciones == null) {
					configuraciones = new ArrayList<Configuracion>();
				}
				for (Configuracion configuracion : configuraciones) {
					if(isNotValid(TIPO_NUMERIC, NUM_DIGITOS, configuracion.getValor())) {
						return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
					}
					int select = catalogos.indexOf(configuracion.getTipo());
					if(select < 0) {
						return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
					}
					Catalogo tipo = catalogos.get(select);
					catalogos.remove(tipo);
					if(!tipo.equals(ACTIVO)) {
						return new ResponseEntity<Plan>(HttpStatus.BAD_REQUEST);
					}
					configuracion.setId(null);
					configuracion.setTipo(tipo);
					configuracion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
					configuracion.setFechaDeModificacion(LocalDateTime.now());
					configuracion.setEstatus(ACTIVO);
					configuracion.setClave(UUID.randomUUID());
				}
				for(Catalogo catalogo: catalogos) {
					Configuracion configuracion = new Configuracion();
					configuracion.setId(null);
					configuracion.setValor("0");
					configuracion.setTipo(catalogo);
					configuracion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
					configuracion.setFechaDeModificacion(LocalDateTime.now());
					configuracion.setEstatus(ACTIVO);
					configuracion.setClave(UUID.randomUUID());
					configuraciones.add(configuracion);
				}
			}else {
				return new ResponseEntity<Plan>(HttpStatus.NOT_FOUND);
			}
		}else {
			return new ResponseEntity<Plan>(HttpStatus.NOT_FOUND);
		}
		
		plan.setConfiguraciones(
				configuraciones.stream()
				.map(x -> configuracionService.save(x))
				.collect(Collectors.toList()));
		plan.setId(null);
		plan.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		plan.setFechaDeModificacion(LocalDateTime.now());
		plan.setEstatus(ACTIVO);
		plan.setClave(UUID.randomUUID());
		plan = planService.save(plan);
		return new ResponseEntity<Plan>(plan, HttpStatus.OK);
	}
	
}
