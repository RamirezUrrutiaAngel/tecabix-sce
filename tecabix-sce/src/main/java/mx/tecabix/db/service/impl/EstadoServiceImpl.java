package mx.tecabix.db.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Estado;
import mx.tecabix.db.repository.EstadoRepository;
import mx.tecabix.db.service.EstadoService;

@Service
public class EstadoServiceImpl implements EstadoService{

	@Autowired
	private EstadoRepository estadoRepository;

	@Override
	public List<Estado> findByAll() {
		List<Estado> estados = estadoRepository.findAll();
		return estados;
	}
	
	
}
