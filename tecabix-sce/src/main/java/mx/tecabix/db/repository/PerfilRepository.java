package mx.tecabix.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import mx.tecabix.db.entity.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long>{
	
	Perfil findByKey(Long id);
	Page<Perfil> findAll(Long idEscuela, Pageable pageable);
	Page<Perfil> findAllByNombre(Long idEscuela, String nombre, Pageable pageable);
	Perfil findByNombre(Long idEscuela, String nombre);
	
}
