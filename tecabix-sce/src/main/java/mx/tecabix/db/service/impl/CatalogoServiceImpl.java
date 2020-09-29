package mx.tecabix.db.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.repository.CatalogoRepository;
import mx.tecabix.db.service.CatalogoService;
@Service
public class CatalogoServiceImpl implements CatalogoService {

	@Autowired
	private CatalogoRepository cataRepository;
	
	public List<Catalogo> findAll() {
		List<Catalogo> request = cataRepository.findAll();
		
		return request;
	}

	@Override
	public Catalogo findByTipoAndNombre(String tipo, String nombre) {
		try {
		
			Catalogo entity = cataRepository.findByTipoAndNombre(tipo, nombre);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
