package mx.tecabix.db.service;

import mx.tecabix.db.entity.Usuario;


public interface UsuarioService {
	Usuario findByNameRegardlessOfStatus(String nombre);
	Usuario findByNombre(String nombre);
	
	Usuario save(Usuario save);
	Usuario update(Usuario update);
	
}
