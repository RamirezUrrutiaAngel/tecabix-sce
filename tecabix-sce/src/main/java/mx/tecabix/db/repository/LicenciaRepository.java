package mx.tecabix.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.Licencia;

public interface LicenciaRepository extends JpaRepository<Licencia, Long>{

	Licencia findByToken(String key);
}
