package mx.tecabix.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.UsuarioPersona;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface UsuarioPersonaRepository extends JpaRepository<UsuarioPersona, Long>{

	UsuarioPersona findByUsuario(String nombre);
}
