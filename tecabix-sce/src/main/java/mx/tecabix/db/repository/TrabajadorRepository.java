package mx.tecabix.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.Trabajador;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Long>{

	Trabajador findByKey(Long id);
	Trabajador findByKeyAndPendiente(Long id);
	Trabajador findByUsuario(String usuario);
	Page<Trabajador> findAll(Long idEscuela, Pageable pageable);
	Page<Trabajador> findAllByNombre(Long idEscuela,String nombre, Pageable pageable);
}
