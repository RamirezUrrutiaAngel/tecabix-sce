package mx.tecabix.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Licencia;
import mx.tecabix.db.repository.LicenciaRepository;
import mx.tecabix.db.service.LicenciaService;
@Service
public class LicenciaServiceImpl implements LicenciaService {

	@Autowired
	private LicenciaRepository licenciaRepository;

	@Override
	public Licencia findByToken(String key) {
		Licencia response = licenciaRepository.findByToken(key);
		return response;
	}
	
	
}
