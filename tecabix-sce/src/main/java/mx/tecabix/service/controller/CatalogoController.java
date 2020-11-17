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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.CatalogoTipo;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.CatalogoTipoService;
import mx.tecabix.db.service.SesionService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("catalogo")
public class CatalogoController {
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private CatalogoTipoService catalogoTipoService;
	@Autowired
	private SesionService sesionService;
	
	@GetMapping("findByTipoNombre")
	public ResponseEntity<CatalogoTipo> findByTipoNombre(
			@RequestParam(value="catalogoTipoNombre") String catalogoTipoNombre,
			@RequestParam(value="token") String token) {
		
		Sesion sesion = sesionService.findByToken(token);
		if(sesion == null) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.UNAUTHORIZED);
		}
		CatalogoTipo tipo = catalogoTipoService.findByNombre(catalogoTipoNombre);
		if(tipo == null || tipo.getCatalogos() == null)new ResponseEntity<Catalogo>(HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<CatalogoTipo>(tipo,HttpStatus.OK);
	}
	
	@GetMapping("findByTipoAndNombre")
	public ResponseEntity<Catalogo> findByTipoAndNombre(
			@RequestParam(value="catalogoTipoNombre") String catalogoTipoNombre,
			@RequestParam(value="nombre") String nombre,
			@RequestParam(value="token") String token) {
		
		Sesion sesion = sesionService.findByToken(token);
		if(sesion == null) {
			return new ResponseEntity<Catalogo>(HttpStatus.UNAUTHORIZED);
		}
		Catalogo result = catalogoService.findByTipoAndNombre(catalogoTipoNombre, nombre);
		return new ResponseEntity<Catalogo>(result,HttpStatus.OK);
	}

}
