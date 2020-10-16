package mx.tecabix.db.service;

import org.springframework.data.domain.Page;

import mx.tecabix.db.entity.Usuario;


public interface UsuarioService {
	Usuario findByNameRegardlessOfStatus(String nombre);
	Usuario findByNombre(String nombre);
	
	Usuario save(Usuario save);
	Usuario update(Usuario update);
	
	Page<Usuario> findByPerfil(Long idPerfil,int elements, int page);
	
}
