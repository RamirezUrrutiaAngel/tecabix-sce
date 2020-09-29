package mx.tecabix.db.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.repository.AuthorityRepository;
import mx.tecabix.db.service.AuthorityService;

@Service
public class AuthorityServiceImpl implements AuthorityService{

	@Autowired
	private AuthorityRepository authorityRepository;
	
	public List<Authority> findAll(){
		List<Authority> list = authorityRepository.findAll();
		return list;
	}
}
