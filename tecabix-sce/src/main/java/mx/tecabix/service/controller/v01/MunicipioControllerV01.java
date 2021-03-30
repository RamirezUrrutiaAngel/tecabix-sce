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

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.service.MunicipioService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.page.MunicipioPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */

@RestController
@RequestMapping("municipio/v1")
public final class MunicipioControllerV01 extends Auth{

	@Autowired
	private MunicipioService municipioService;
	
	/**
	 * 
	 * @param by:		NOMBRE
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los municipios paginado.", 
			notes = "<b>by:</b> NOMBRE<br/><b>order:</b> ASC, DESC")
	@GetMapping
	public ResponseEntity<MunicipioPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		if(isNotAuthorized(token)) {
			return new ResponseEntity<MunicipioPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Municipio> municipios = null;
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			return new ResponseEntity<MunicipioPage>(HttpStatus.BAD_REQUEST);
		}
		if(search == null || search.isEmpty()) {
			municipios = municipioService.findByActivo(elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				municipios = municipioService.findByLikeNombre(text.toString(), elements, page, sort);
			}else {
				return new ResponseEntity<MunicipioPage>(HttpStatus.BAD_REQUEST);
			}
		}
		MunicipioPage body = new MunicipioPage(municipios);
		return new ResponseEntity<MunicipioPage>(body,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Obtiene todo los municipios de la clave del estado.")
	@GetMapping("find-by-estado-clave")
	public ResponseEntity<MunicipioPage> findByEstadoClave(
			@RequestParam(value="token") UUID token,@RequestParam(value="clave") UUID clave){
		if(isNotAuthorized(token)) {
			return new ResponseEntity<MunicipioPage>(HttpStatus.UNAUTHORIZED);
		}
		Sort sort = Sort.by(Sort.Direction.ASC,"nombre");
		Page<Municipio> municipios = municipioService.findByEstadoClave(clave, Integer.MAX_VALUE, 0, sort);
		MunicipioPage body = new MunicipioPage(municipios);
		return new ResponseEntity<MunicipioPage>(body,HttpStatus.OK);
	}
}
