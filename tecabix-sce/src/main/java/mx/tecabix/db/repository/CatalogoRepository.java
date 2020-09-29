package mx.tecabix.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.Catalogo;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface CatalogoRepository extends JpaRepository<Catalogo, Integer>{

	Catalogo findByTipoAndNombre(String tipo,String nombre);
}
