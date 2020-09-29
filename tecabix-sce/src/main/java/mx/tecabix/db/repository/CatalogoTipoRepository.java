package mx.tecabix.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import mx.tecabix.db.entity.CatalogoTipo;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface CatalogoTipoRepository extends JpaRepository<CatalogoTipo, Integer>{
	
	CatalogoTipo findByNombre(String String);
	
}
