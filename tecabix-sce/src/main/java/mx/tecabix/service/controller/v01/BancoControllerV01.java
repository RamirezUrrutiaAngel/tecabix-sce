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
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Banco;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.BancoService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.BancoPage;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("banco/v1")
public final class BancoControllerV01 extends Auth{
	
	private String BANCO ="BANCO";
	private String ROOT_BANCO = "ROOT_BANCO";
	private String ROOT_BANCO_CREAR = "ROOT_BANCO_CREAR";
	private String ROOT_BANCO_EDITAR = "ROOT_BANCO_EDITAR";
	private String ROOT_BANCO_ELIMINAR = "ROOT_BANCO_ELIMINAR";
	private final String TRABAJADOR = "TRABAJADOR";
	
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private BancoService bancoService;
	/**
	 * 
	 * @param by:		NOMBRE, RAZON_SOCIAL, CLAVE_BANCO
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Obtiene todo los bancos paginado.",
			notes = "<b>by:</b> NOMBRE, RAZON_SOCIAL, CLAVE_BANCO<br/><b>order:</b> ASC, DESC")
	@GetMapping()
	public ResponseEntity<BancoPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		final String PERMISOS[] = { BANCO, ROOT_BANCO, TRABAJADOR };
		if(isNotAuthorized(token,PERMISOS)) {
			return new ResponseEntity<BancoPage>(HttpStatus.UNAUTHORIZED);
		}
		Page<Banco> result = null;
		String columna = null;
		if(by.equalsIgnoreCase("NOMBRE")) {
			columna = "nombre";
		}else if(by.equalsIgnoreCase("RAZON_SOCIAL")) {
			columna = "razonSocial";
		}else if(by.equalsIgnoreCase("CLAVE_BANCO")) {
			columna = "claveBanco";
		}else {
			return new ResponseEntity<BancoPage>(HttpStatus.BAD_REQUEST);
		}
		Sort sort;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, columna);
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, columna);
		}else {
			return new ResponseEntity<BancoPage>(HttpStatus.BAD_REQUEST);
		}
		if(search == null || search.isEmpty()) {
			result = bancoService.findAll(elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				result = bancoService.findByLikeNombre(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("RAZON_SOCIAL")) {
				result = bancoService.findByLikeRazonSocial(text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("CLAVE_BANCO")) {
				result = bancoService.findByLikeClaveBanco(text.toString(), elements, page, sort);
			}
		}
		BancoPage body = new BancoPage(result);
		return new ResponseEntity<BancoPage>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad del Banco. ")
	@PostMapping
	public ResponseEntity<Banco> save(@RequestParam(value="token") UUID token, @RequestBody Banco banco){
		Sesion sesion = getSessionIfIsAuthorized(token, ROOT_BANCO_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Banco>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(TIPO_NUMERIC, Banco.SIZE_CLAVE_BANCO, banco.getClaveBanco())) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, Banco.SIZE_NOMBRE, banco.getNombre())) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}else {
			banco.setNombre(banco.getNombre().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Banco.SIZE_RAZON_SOCIAL, banco.getRazonSocial())) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}else {
			banco.setRazonSocial(banco.getRazonSocial().strip());
		}
		banco.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		banco.setFechaDeModificacion(LocalDateTime.now());
		banco.setEstatus(singletonUtil.getActivo());
		banco.setClave(UUID.randomUUID());
		banco = bancoService.save(banco);
		return new ResponseEntity<Banco>(banco,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del Banco. ")
	@PutMapping
	public ResponseEntity<Banco> update(@RequestParam(value="token") UUID token, @RequestBody Banco banco){
		Sesion sesion = getSessionIfIsAuthorized(token, ROOT_BANCO_EDITAR);
		if(sesion == null) {
			return new ResponseEntity<Banco>(HttpStatus.UNAUTHORIZED);
		}
		if(isNotValid(banco.getClave())) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC, Banco.SIZE_CLAVE_BANCO, banco.getClaveBanco())) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE,Banco.SIZE_NOMBRE,banco.getNombre())) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}else {
			banco.setNombre(banco.getNombre().strip());
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Banco.SIZE_RAZON_SOCIAL, banco.getRazonSocial())) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}else {
			banco.setRazonSocial(banco.getRazonSocial().strip());
		}
		Optional<Banco> bancoAux =  bancoService.findByClave(banco.getClave());
		if(bancoAux.isEmpty()) {
			return new ResponseEntity<Banco>(HttpStatus.NOT_FOUND);
		}
		Banco bancoUpdate = bancoAux.get();
		bancoUpdate.setClaveBanco(banco.getClaveBanco());
		bancoUpdate.setNombre(banco.getNombre());
		bancoUpdate.setRazonSocial(banco.getRazonSocial());
		bancoUpdate.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		bancoUpdate.setFechaDeModificacion(LocalDateTime.now());
		banco = bancoService.update(bancoUpdate);
		return new ResponseEntity<Banco>(banco,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad del Banco por clave. ")
	@DeleteMapping
	public ResponseEntity<?> delete(@RequestParam(value="token") UUID token,@RequestParam(value="clave") UUID clave) {
		if(isNotAuthorized(token,ROOT_BANCO_ELIMINAR)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Banco> optional = bancoService.findByClave(clave);
		if(optional.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		bancoService.deleteById(optional.get().getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
