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
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import mx.tecabix.db.service.BancoService;
import mx.tecabix.service.Auth;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("banco/v1")
public class BancoControllerV01 extends Auth{
	
	private String BANCO ="BANCO";
	private String ROOT_BANCO = "ROOT_BANCO";
	private String ROOT_BANCO_CREAR = "ROOT_BANCO_CREAR";
	private String ROOT_BANCO_EDITAR = "ROOT_BANCO_EDITAR";
	private String ROOT_BANCO_ELIMINAR = "ROOT_BANCO_ELIMINAR";
	
	@Autowired
	private BancoService bancoService;
	
	@ApiOperation(value = "Obtiene todo los bancos paginado.")
	@GetMapping("findAll")
	public ResponseEntity<Page<Banco>> findAll(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		if(isNotAuthorized(token, BANCO, ROOT_BANCO)) {
			return new ResponseEntity<Page<Banco>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Banco> result =  bancoService.findAll(elements, page);
		return new ResponseEntity<Page<Banco>>(result, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Obtiene el bancos por Clave.")
	@GetMapping("findByClave")
	public ResponseEntity<Banco> findById(@RequestParam(value="token") String token,@RequestParam(value="clave") UUID uuid) {
		if(isNotAuthorized(token, BANCO, ROOT_BANCO)) {
			return new ResponseEntity<Banco>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Banco> result =  bancoService.findByClave(uuid);
		if(!result.isPresent()) {
			return new ResponseEntity<Banco>(HttpStatus.NOT_FOUND);
		}
		Banco body = result.get();
		return new ResponseEntity<Banco>(body, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Persiste la entidad del Banco. ")
	@PostMapping
	public ResponseEntity<Banco> save(@RequestParam(value="token") String token, @RequestBody Banco banco){
		if(isNotAuthorized(token, ROOT_BANCO_CREAR)) {
			return new ResponseEntity<Banco>(HttpStatus.UNAUTHORIZED);
		}
		if(banco.getClaveBanco() == null || banco.getClaveBanco().isEmpty()) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		if(banco.getNombre() == null || banco.getNombre().isEmpty()) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		if(banco.getRazonSocial() == null || banco.getRazonSocial().isEmpty()) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		banco = bancoService.save(banco);
		return new ResponseEntity<Banco>(banco,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del Banco. ")
	@PutMapping
	public ResponseEntity<Banco> update(@RequestParam(value="token") String token, @RequestBody Banco banco){
		if(isNotAuthorized(token, ROOT_BANCO_EDITAR)) {
			return new ResponseEntity<Banco>(HttpStatus.UNAUTHORIZED);
		}
		if(banco.getClave() == null) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		if(banco.getClaveBanco() == null || banco.getClaveBanco().isEmpty()) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		if(banco.getNombre() == null || banco.getNombre().isEmpty()) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		if(banco.getRazonSocial() == null || banco.getRazonSocial().isEmpty()) {
			return new ResponseEntity<Banco>(HttpStatus.BAD_REQUEST);
		}
		Optional<Banco> bancoAux =  bancoService.findByClave(banco.getClave());
		if(!bancoAux.isPresent()) {
			return new ResponseEntity<Banco>(HttpStatus.NOT_FOUND);
		}
		banco = bancoService.save(banco);
		return new ResponseEntity<Banco>(banco,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad del Banco por clave. ")
	@DeleteMapping
	public ResponseEntity<Boolean> delete(@RequestParam(value="token") String token,@RequestParam(value="clave") UUID clave) {
		if(isNotAuthorized(token,ROOT_BANCO_ELIMINAR)) {
			return new ResponseEntity<Boolean>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Banco> optional = bancoService.findByClave(clave);
		if(!optional.isPresent()) {
			return new ResponseEntity<Boolean>(HttpStatus.NOT_FOUND);
		}
		bancoService.deleteById(optional.get().getId());
		return new ResponseEntity<Boolean>(true,HttpStatus.OK);
	}
}
