package mx.tecabix.db.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.Soporte;
import mx.tecabix.db.repository.SoporteRepository;
import mx.tecabix.db.service.SoporteService;

public class SoporteServiceImpl extends GenericSeviceImpl<Soporte, Long> implements SoporteService{

	@Autowired
	private SoporteRepository soporteRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(soporteRepository);
	}

}
