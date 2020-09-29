package mx.tecabix.service.response;

import java.util.List;

import mx.tecabix.db.entity.Authority;

public class AuthorityListResponse {

	private List<Authority> authorities;

	public List<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}
	
	
}
