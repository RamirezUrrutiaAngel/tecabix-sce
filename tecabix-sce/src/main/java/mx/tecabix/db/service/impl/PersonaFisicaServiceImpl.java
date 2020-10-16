package mx.tecabix.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.PersonaFisica;
import mx.tecabix.db.repository.PersonaFisicaRepository;
import mx.tecabix.db.service.PersonaFisicaService;
@Service
public class PersonaFisicaServiceImpl implements PersonaFisicaService {

	@Autowired
	private PersonaFisicaRepository personaFisicaRepository;

	@Override
	public PersonaFisica save(PersonaFisica save) {
		save = personaFisicaRepository.save(save);
		return save;
	}

	@Override
	public PersonaFisica update(PersonaFisica update) {
		update = personaFisicaRepository.save(update);
		return update;
	}
	
}
