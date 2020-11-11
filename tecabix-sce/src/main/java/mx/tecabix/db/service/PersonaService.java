package mx.tecabix.db.service;

import mx.tecabix.db.entity.Persona;


public interface PersonaService {
	
	Persona findById(long id);
	Persona save(Persona save);
	Persona update(Persona update);
}
