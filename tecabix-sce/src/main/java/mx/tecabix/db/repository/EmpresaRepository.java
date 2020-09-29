package mx.tecabix.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long>{

	
}
