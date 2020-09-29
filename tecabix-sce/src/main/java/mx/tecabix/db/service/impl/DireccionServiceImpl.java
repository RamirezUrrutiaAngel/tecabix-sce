package mx.tecabix.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Direccion;
import mx.tecabix.db.repository.DireccionRepository;
import mx.tecabix.db.service.DireccionService;


@Service
public class DireccionServiceImpl implements DireccionService {
	
	@Autowired
	private DireccionRepository direccionRepository;
	
	
	@Override
	public Direccion save(Direccion save) {
		save = direccionRepository.save(save);
		return save;
	}
	
}
