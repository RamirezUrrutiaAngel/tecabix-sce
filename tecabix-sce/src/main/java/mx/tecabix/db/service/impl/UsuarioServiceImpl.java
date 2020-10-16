package mx.tecabix.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.repository.UsuarioRepository;
import mx.tecabix.db.service.UsuarioService;
@Service
public class UsuarioServiceImpl implements UsuarioService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Override
	public Usuario findByNombre(String nombre) {
		Usuario usr = usuarioRepository.findByNombre(nombre);
		return usr;
	}

	@Override
	public Usuario save(Usuario save) {
		save = usuarioRepository.save(save);
		return save;
	}

	@Override
	public Usuario update(Usuario update) {
		update = usuarioRepository.save(update);
		return update;
	}

	@Override
	public Usuario findByNameRegardlessOfStatus(String nombre) {
		Usuario usr = usuarioRepository.findByNameRegardlessOfStatus(nombre);
		return usr;
	}

	@Override
	public Page<Usuario> findByPerfil(Long idPerfil, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Usuario> entitys = usuarioRepository.findByPerfil(idPerfil, pageable);
		return entitys;
	}
}
