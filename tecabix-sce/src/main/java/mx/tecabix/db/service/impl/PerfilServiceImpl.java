package mx.tecabix.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Perfil;
import mx.tecabix.db.repository.PerfilRepository;
import mx.tecabix.db.service.PerfilService;

@Service
public class PerfilServiceImpl implements PerfilService{

	@Autowired
	private PerfilRepository perfilRepository;

	@Override
	public Perfil findById(Long id) {
		Perfil perfil = perfilRepository.findByKey(id);
		return perfil;
	}

	@Override
	public Page<Perfil> findAll(Long idEscuela, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Perfil> perfiles = perfilRepository.findAll(idEscuela, pageable);
		return perfiles;
	}

	@Override
	public Page<Perfil> findAllbyNombre(Long idEscuela, String nombre, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Perfil> perfiles = perfilRepository.findAllByNombre(idEscuela,nombre, pageable);
		return perfiles;
	}

	@Override
	public Perfil save(Perfil save) {
		save = perfilRepository.save(save);
		return save;
	}

	@Override
	public Perfil update(Perfil update) {
		update = perfilRepository.save(update);
		return update;
	}

	@Override
	public Perfil findByNombre(Long idEscuela, String nombre) {
		Perfil perfil = perfilRepository.findByNombre(idEscuela, nombre);
		return perfil;
	}

	@Override
	public void delete(Long idPerfil) {
		perfilRepository.deleteById(idPerfil);
	}
	
}
