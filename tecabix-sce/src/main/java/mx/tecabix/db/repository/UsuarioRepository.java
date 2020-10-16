package mx.tecabix.db.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.Usuario;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Usuario findByNombre(String nombre);
	Usuario findByNameRegardlessOfStatus(String nombre);
	Page<Usuario> findByPerfil(Long idPerfil, Pageable pageable);
}
