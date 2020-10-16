package mx.tecabix.db.service;

import org.springframework.data.domain.Page;
import mx.tecabix.db.entity.Perfil;

public interface PerfilService {

	Perfil save(Perfil save);
	Perfil update(Perfil update);
	void delete(Long idPerfil);
	Perfil findById(Long id);
	Perfil findByNombre(Long idEscuela, String nombre);
	Page<Perfil> findAll(Long idEscuela, int elements, int page);
	Page<Perfil> findAllbyNombre(Long idEscuela, String nombre, int elements, int page);
}
