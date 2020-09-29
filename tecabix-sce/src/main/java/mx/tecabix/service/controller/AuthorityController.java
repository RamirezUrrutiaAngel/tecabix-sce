package mx.tecabix.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.service.AuthorityService;
import mx.tecabix.service.response.AuthorityListResponse;

@RestController
@RequestMapping("authority")
public class AuthorityController {

	@Autowired
	private AuthorityService authorityService;
	
	@GetMapping("findAll")
	public ResponseEntity<AuthorityListResponse> findAll(){
		List<Authority> authorities = authorityService.findAll();
		if(authorities != null) {
			for (Authority authority : authorities) {
				authority.setPerfiles(null);
				authority.setSubAuthority(null);
			}
		}
		AuthorityListResponse responseList = new AuthorityListResponse();
		responseList.setAuthorities(authorities);
		ResponseEntity<AuthorityListResponse> response = new ResponseEntity<AuthorityListResponse>(responseList, HttpStatus.OK);
		return response;
	}
}
