package mx.tecabix.db.service;

import org.springframework.data.domain.Page;
import mx.tecabix.db.entity.Trabajador;

public interface TrabajadorService  {
	Trabajador findById(Long id);
	Trabajador findByIdAndPendiente(Long id);
	Trabajador findByUsuario(String usuario);
	Trabajador save(Trabajador save);
	Trabajador update(Trabajador update);
	Page<Trabajador> findAll(Long idEscuelam, int elements, int page);
	Page<Trabajador> findAllByNombre(Long idEscuela,String nombre, int elements, int page);
}
