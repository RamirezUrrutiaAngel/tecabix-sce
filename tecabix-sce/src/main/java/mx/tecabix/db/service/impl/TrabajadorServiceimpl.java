package mx.tecabix.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.repository.TrabajadorRepository;
import mx.tecabix.db.service.TrabajadorService;
@Service
public class TrabajadorServiceimpl implements TrabajadorService {

	@Autowired
	private TrabajadorRepository trabajadorRepository;
	
	@Override
	public Trabajador findById(Long id) {
		Trabajador trabajador = trabajadorRepository.findByKey(id);
		return trabajador;
	}

	@Override
	public Trabajador save(Trabajador save) {
		Trabajador trabajador = trabajadorRepository.save(save);
		return trabajador;
	}

	@Override
	public Trabajador update(Trabajador update) {
		Trabajador trabajador = trabajadorRepository.save(update);
		return trabajador;
	}

	@Override
	public Trabajador findByUsuario(String usuario) {
		Trabajador trabajador = trabajadorRepository.findByUsuario(usuario);
		return trabajador;
	}

	@Override
	public Page<Trabajador> findAll(Long idEscuela,int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Trabajador> entitys = trabajadorRepository.findAll(idEscuela, pageable);
		return entitys;
	}

	@Override
	public Page<Trabajador> findAllByNombre(Long idEscuela, String nombre, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Trabajador> entitys = trabajadorRepository.findAllByNombre(idEscuela,nombre, pageable);
		return entitys;
	}

	@Override
	public Trabajador findByIdAndPendiente(Long id) {
		Trabajador trabajador = trabajadorRepository.findByKeyAndPendiente(id);
		return trabajador;
	}



}
