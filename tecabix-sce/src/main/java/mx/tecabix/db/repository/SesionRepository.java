package mx.tecabix.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import mx.tecabix.db.entity.Sesion;

public interface SesionRepository extends JpaRepository<Sesion, Long>{

	@Query(value = "SELECT MD5(?1)", nativeQuery = true)
	String getMD5(String text);
	
	List<Sesion> findByActive(Long idLicencia);
	List<Sesion> findByUsuarioAndActive(Long idLicencia,Long idUsuario);
	
	Page<Sesion> findByNow(Long idLicencia,Pageable pageable);
	Page<Sesion> findByUsuarioAndNow(Long idLicencia,Long idUsuari, Pageable pageable);
	
	Sesion findByToken(String keyToken);
	
}
