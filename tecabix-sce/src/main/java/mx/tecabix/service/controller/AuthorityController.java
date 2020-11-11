package mx.tecabix.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.Auth;
import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.service.AuthorityService;
import mx.tecabix.service.response.AuthorityListResponse;

@RestController
@RequestMapping("authority")
public class AuthorityController {
	
	private static final String AUTHORITY = "AUTHORITY";
	private static final String PERFIL = "PERFIL";
	
	@Autowired
	private AuthorityService authorityService;
	
	@GetMapping("findAll")
	public ResponseEntity<AuthorityListResponse> findAll(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!Auth.hash(auth, AUTHORITY,PERFIL)) {
			return new ResponseEntity<AuthorityListResponse>(HttpStatus.UNAUTHORIZED);
		}
		
		List<Authority> authorities = authorityService.findAll();
		if(authorities != null) {
			for (Authority authority : authorities) {
				authority.setPerfiles(null);
				authority.setSubAuthority(null);
			}
		}
		List<GrantedAuthority> list = new ArrayList<GrantedAuthority>(auth.getAuthorities());
		for (GrantedAuthority grantedAuthority : list) {
			System.out.println(grantedAuthority.getAuthority());
		}
		AuthorityListResponse responseList = new AuthorityListResponse();
		responseList.setAuthorities(authorities);
		ResponseEntity<AuthorityListResponse> response = new ResponseEntity<AuthorityListResponse>(responseList, HttpStatus.OK);
		return response;
	}
}
