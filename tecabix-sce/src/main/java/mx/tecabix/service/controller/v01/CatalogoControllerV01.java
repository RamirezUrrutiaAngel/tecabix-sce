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

import org.springframework.beans.factory.annotation.Autowired;
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
import mx.tecabix.db.entity.CatalogoTipo;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.CatalogoTipoService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("catalogo/v1")
public final class CatalogoControllerV01 extends Auth{
	
	private static final String CATALOGO = "CATALOGO";
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private CatalogoTipoService catalogoTipoService;
	
	@ApiOperation(value = "Persiste la entidad del tipo del catalogo con sus correspondientes catalogos. ")
	@PostMapping("saveCatalogoTipo")
	public ResponseEntity<CatalogoTipo> saveCatalogoTipo(@RequestParam(value = "token") UUID token,
			@RequestBody CatalogoTipo catalogoTipo) {
		Sesion sesion = getSessionIfIsAuthorized(token, CATALOGO);
		if (isNotValid(sesion)) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.UNAUTHORIZED);
		}
		if (isNotValid(TIPO_VARIABLE, CatalogoTipo.SIZE_NOMBRE, catalogoTipo.getNombre())) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
		}

		if (isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, CatalogoTipo.SIZE_DESCRIPCION,
				catalogoTipo.getDescripcion())) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
		}else {
			catalogoTipo.setDescripcion(catalogoTipo.getDescripcion().strip());
		}
		Optional<CatalogoTipo> optionalCatalogoTipo = catalogoTipoService.findByNombre(catalogoTipo.getNombre());
		if (optionalCatalogoTipo.isPresent()) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.CONFLICT);
		}
		List<Catalogo> catalogos = catalogoTipo.getCatalogos();
		if (catalogos != null) {
			for (int i = 0; i < catalogos.size(); i++) {
				Catalogo catalogo = catalogos.get(i);
				if (isNotValid(catalogo.getOrden())) {
					return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
				}
				if (isNotValid(TIPO_VARIABLE, Catalogo.SIZE_NOMBRE, catalogo.getNombre())) {
					return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
				}
				if (isNotValid(TIPO_ALFA_NUMERIC_SPACE, Catalogo.SIZE_DESCRIPCION, catalogo.getDescripcion())) {
					return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
				} else {
					catalogo.setDescripcion(catalogo.getDescripcion().strip());
				}
				if (isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Catalogo.SIZE_NOMBRE_COMPLETO, catalogo.getNombreCompleto())) {
					return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
				} else {
					catalogo.setNombreCompleto(catalogo.getNombreCompleto().strip());
				}
			}
			for (int i = 0; i < catalogos.size(); i++) {
				Catalogo catalogoA = catalogos.get(i);
				for (int j = i + 1; j < catalogos.size(); j++) {
					Catalogo catalogoB = catalogos.get(j);
					if (catalogoA.getNombre().equalsIgnoreCase(catalogoB.getNombre())) {
						return new ResponseEntity<CatalogoTipo>(HttpStatus.CONFLICT);
					}
				}
			}
		}
		Catalogo ACTIVO = singletonUtil.getActivo();
		catalogoTipo.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		catalogoTipo.setFechaDeModificacion(LocalDateTime.now());
		catalogoTipo.setEstatus(ACTIVO);
		catalogoTipo.setClave(UUID.randomUUID());
		catalogoTipo = catalogoTipoService.save(catalogoTipo);
		if (catalogos != null) {
			final CatalogoTipo aux = catalogoTipo;
			catalogos.stream().forEach(catalogo -> {
				catalogo.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
				catalogo.setFechaDeModificacion(LocalDateTime.now());
				catalogo.setEstatus(ACTIVO);
				catalogo.setClave(UUID.randomUUID());
				catalogo.setCatalogoTipo(aux);
				catalogoService.save(catalogo);
			});
		}
		catalogoTipo.setCatalogos(catalogos);
		return new ResponseEntity<CatalogoTipo>(catalogoTipo, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad del catalogo a un tipo de catalogo. ")
	@PostMapping("saveCatalogo")
	public ResponseEntity<Catalogo> saveCatalogo(@RequestParam(value = "token") UUID token, @RequestBody Catalogo catalogo){
		
		Sesion sesion = getSessionIfIsAuthorized(token, CATALOGO);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Catalogo>(HttpStatus.UNAUTHORIZED);
		}
		if(catalogo.getId() != null ) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}
		if(catalogo.getOrden() == null) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_VARIABLE, Catalogo.SIZE_NOMBRE, catalogo.getNombre())) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Catalogo.SIZE_DESCRIPCION, catalogo.getDescripcion())) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		} else {
			catalogo.setDescripcion(catalogo.getDescripcion().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Catalogo.SIZE_NOMBRE_COMPLETO, catalogo.getNombreCompleto())) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}else {
			catalogo.setNombreCompleto(catalogo.getNombreCompleto().strip());
		}
		if(catalogo.getCatalogoTipo() == null || catalogo.getCatalogoTipo().getId() == null) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}
		Optional<CatalogoTipo> optionalCatalogo = catalogoTipoService.findById(catalogo.getCatalogoTipo().getId());
		if(optionalCatalogo.isEmpty()) {
			return new ResponseEntity<Catalogo>(HttpStatus.NOT_FOUND);
		}
		catalogo.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		catalogo.setFechaDeModificacion(LocalDateTime.now());
		catalogo.setEstatus(singletonUtil.getActivo());
		catalogo.setClave(UUID.randomUUID());
		catalogo.setCatalogoTipo(optionalCatalogo.get());
		catalogo = catalogoService.save(catalogo);
		return new ResponseEntity<Catalogo>(catalogo,HttpStatus.OK);
	}

	@ApiOperation(value = "Actualiza la entidad del catalogo de un tipo de catalogo. ")
	@PutMapping("updateCatalogo")
	public ResponseEntity<Catalogo> updateCatalogo(@RequestParam(value = "token") UUID token, @RequestBody Catalogo catalogo){
		
		Sesion sesion = getSessionIfIsAuthorized(token, CATALOGO);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Catalogo>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(catalogo.getId())) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(catalogo.getOrden())) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_VARIABLE, Catalogo.SIZE_NOMBRE, catalogo.getNombre())) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Catalogo.SIZE_DESCRIPCION, catalogo.getDescripcion())) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}else {
			catalogo.setDescripcion(catalogo.getDescripcion().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Catalogo.SIZE_NOMBRE_COMPLETO, catalogo.getNombreCompleto())) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}else {
			catalogo.setNombreCompleto(catalogo.getNombreCompleto().strip());
		}
		if(catalogo.getCatalogoTipo() == null || catalogo.getCatalogoTipo().getId() == null) {
			return new ResponseEntity<Catalogo>(HttpStatus.BAD_REQUEST);
		}
		Optional<Catalogo> optionalCatalogo = catalogoService.findById(catalogo.getId());
		if(optionalCatalogo.isEmpty()) {
			return new ResponseEntity<Catalogo>(HttpStatus.NOT_FOUND);
		}
		Catalogo catalogoAux = optionalCatalogo.get();
		catalogoAux.setNombre(catalogo.getNombre());
		catalogoAux.setDescripcion(catalogo.getDescripcion());
		catalogo = catalogoAux;
		catalogo.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		catalogo.setFechaDeModificacion(LocalDateTime.now());
		catalogo = catalogoService.update(catalogo);
		return new ResponseEntity<Catalogo>(catalogo,HttpStatus.OK);
	}
	

	@ApiOperation(value = "Actualiza la entidad tipo catalogo. ")
	@PutMapping("updateCatalogoTipo")
	public ResponseEntity<CatalogoTipo> updateCatalogoTipo(@RequestParam(value = "token") UUID token, @RequestBody CatalogoTipo catalogoTipo){
		Sesion sesion = getSessionIfIsAuthorized(token, CATALOGO);
		if(isNotValid(sesion)) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(catalogoTipo.getClave())) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
		}if(isNotValid(TIPO_VARIABLE, CatalogoTipo.SIZE_NOMBRE, catalogoTipo.getNombre())) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
		}if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, CatalogoTipo.SIZE_DESCRIPCION, catalogoTipo.getDescripcion())) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.BAD_REQUEST);
		}else {
			catalogoTipo.setDescripcion(catalogoTipo.getDescripcion().strip());
		}
		Optional<CatalogoTipo> optionalCatalogoTipo = catalogoTipoService.findByClave(catalogoTipo.getClave());
		if(optionalCatalogoTipo.isEmpty()) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.NOT_FOUND);
		}
		CatalogoTipo catalogoTipoAux = optionalCatalogoTipo.get();
		catalogoTipoAux.setNombre(catalogoTipo.getNombre());
		catalogoTipoAux.setDescripcion(catalogoTipo.getDescripcion());
		catalogoTipo = catalogoTipoAux;
		catalogoTipo.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		catalogoTipo.setFechaDeModificacion(LocalDateTime.now());
		catalogoTipo = catalogoTipoService.update(catalogoTipo);
		return new ResponseEntity<CatalogoTipo>(catalogoTipo,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Obtiene el tipo de catalogos pero que coincidan con el nombre proporcionado.")
	@GetMapping("findByTipoNombre")
	public ResponseEntity<CatalogoTipo> findByTipoNombre(
			@RequestParam(value="catalogoTipoNombre") String catalogoTipoNombre,
			@RequestParam(value="token") UUID token) {
		
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.UNAUTHORIZED);
		}
		Optional<CatalogoTipo> optionalCatalogoTipo = catalogoTipoService.findByNombre(catalogoTipoNombre);
		if(optionalCatalogoTipo.isEmpty()) {
			return new ResponseEntity<CatalogoTipo>(HttpStatus.NOT_FOUND);
		}
		CatalogoTipo tipo = optionalCatalogoTipo.get();
		
		return new ResponseEntity<CatalogoTipo>(tipo,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Obtiene el catalogo pero que coincidan con el nombre del catalogo y el nombre del tipo de catalogo.")
	@GetMapping("findByTipoAndNombre")
	public ResponseEntity<Catalogo> findByTipoAndNombre(
			@RequestParam(value="catalogoTipoNombre") String catalogoTipoNombre,
			@RequestParam(value="nombre") String nombre,
			@RequestParam(value="token") UUID token) {
		
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null) {
			return new ResponseEntity<Catalogo>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Catalogo> optionalCatalogo = catalogoService.findByTipoAndNombre(catalogoTipoNombre, nombre);
		if(optionalCatalogo.isEmpty()) {
			return new ResponseEntity<Catalogo>(HttpStatus.NOT_FOUND);
		}
		Catalogo body = optionalCatalogo.get();
		return new ResponseEntity<Catalogo>(body,HttpStatus.OK);
	}
}
