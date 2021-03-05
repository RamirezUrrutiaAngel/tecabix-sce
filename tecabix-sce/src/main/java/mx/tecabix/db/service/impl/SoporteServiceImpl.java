package mx.tecabix.db.service.impl;

import java.util.Optional;
import java.util.UUID;

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

	@Override
	public Optional<Soporte> findByClave(UUID uuid) {
		return soporteRepository.findByClave(uuid);
	}

}
