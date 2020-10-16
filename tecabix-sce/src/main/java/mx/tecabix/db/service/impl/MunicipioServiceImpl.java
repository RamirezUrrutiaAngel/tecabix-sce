package mx.tecabix.db.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.repository.MunicipioRepository;
import mx.tecabix.db.service.MunicipioService;

@Service
public class MunicipioServiceImpl implements MunicipioService{

	@Autowired
	private MunicipioRepository municipioRepository;
	@Override
	public Municipio findById(Integer id) {
		Optional<Municipio> municipio = municipioRepository.findById(id);
		Municipio municipio2 = null; 
		if(municipio.isPresent()) {
			municipio2 =  municipio.get();
		}
		return municipio2;
	}

}
