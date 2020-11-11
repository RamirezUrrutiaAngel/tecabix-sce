package mx.tecabix.db.service;

import mx.tecabix.db.entity.UsuarioPersona;

public interface UsuarioPersonaService {
	
	UsuarioPersona save(UsuarioPersona save);
	UsuarioPersona update(UsuarioPersona update);
	
	UsuarioPersona findById(long id);
	UsuarioPersona findByUsuario(String nombre);
	

}
