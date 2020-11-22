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
import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.service.AuthorityService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("authority")
public class AuthorityController extends Auth{
	
	private static final String AUTHORITY = "AUTHORITY";
	private static final String PERFIL = "PERFIL";
	
	@Autowired
	private AuthorityService authorityService;
	
	@GetMapping("findAll")
	public ResponseEntity<Page<Authority>> findAll(@RequestParam(value="token") String token,@RequestParam(value="elements") byte elements,@RequestParam(value="page") short page) {
		if(isNotAuthorized(token, AUTHORITY, PERFIL)) {
			return new ResponseEntity<Page<Authority>>(HttpStatus.UNAUTHORIZED);
		}
		
		Page<Authority> authorities = authorityService.findAll(elements, page);
		if(authorities != null) {
			for (Authority authority : authorities) {
				authority.setPerfiles(null);
				authority.setSubAuthority(null);
			}
		}
		ResponseEntity<Page<Authority>> response = new ResponseEntity<Page<Authority>>(authorities, HttpStatus.OK);
		return response;
	}
	
	@GetMapping("findById")
	public ResponseEntity<Authority> findById(@RequestParam(value="token") String token, @RequestParam(value = "id") Integer id){
		
		if(isNotAuthorized(token, AUTHORITY, PERFIL)) {
			return new ResponseEntity<Authority>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Authority> result = authorityService.findById(id);
		if(!result.isPresent()) {
			return new ResponseEntity<Authority>(HttpStatus.NOT_FOUND);
		}
		Authority body = result.get();
		return new ResponseEntity<Authority>(body, HttpStatus.OK);
	}
}
