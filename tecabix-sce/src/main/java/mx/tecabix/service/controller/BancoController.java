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

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.Auth;
import mx.tecabix.db.entity.Banco;
import mx.tecabix.db.service.BancoService;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("banco")
public class BancoController extends Auth{
	
	private String BANCO ="BANCO";
	private String ROOT_BANCO = "ROOT_BANCO";
	
	@Autowired
	private BancoService bancoService;
	
	@GetMapping("findAll")
	public ResponseEntity<Page<Banco>> findAll(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		if(isNotAuthorized(token, BANCO, ROOT_BANCO)) {
			return new ResponseEntity<Page<Banco>>(HttpStatus.UNAUTHORIZED);
		}
		Page<Banco> result =  bancoService.findAll(elements, page);
		return new ResponseEntity<Page<Banco>>(result, HttpStatus.OK);
	}
	
	@GetMapping("findById")
	public ResponseEntity<Banco> findById(@RequestParam(value="token") String token,@RequestParam(value="id") Integer id) {
		if(isNotAuthorized(token, BANCO, ROOT_BANCO)) {
			return new ResponseEntity<Banco>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Banco> result =  bancoService.findById(id);
		if(!result.isPresent()) {
			return new ResponseEntity<Banco>(HttpStatus.NOT_FOUND);
		}
		Banco body = result.get();
		return new ResponseEntity<Banco>(body, HttpStatus.OK);
	}
}
