package mx.tecabix.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.Persona;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface PersonaRepository extends JpaRepository<Persona, Long>{

}
