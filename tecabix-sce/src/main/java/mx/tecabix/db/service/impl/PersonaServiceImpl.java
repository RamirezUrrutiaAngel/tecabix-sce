package mx.tecabix.db.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.repository.PersonaRepository;
import mx.tecabix.db.service.PersonaService;

@Service
public class PersonaServiceImpl implements PersonaService{

	@Autowired
	private PersonaRepository personaRepository;
	
	@Override
	public Persona save(Persona save) {
		save = personaRepository.save(save);
		return save;
	}

	@Override
	public Persona update(Persona update) {
		update = personaRepository.save(update);
		return update;
	}

	@Override
	public Persona findById(long id) {
		Optional<Persona> o = personaRepository.findById(id);
		Persona result = null;
		if(o.isPresent()) {
			result = o.get();
		}
		return result;
	}
}
