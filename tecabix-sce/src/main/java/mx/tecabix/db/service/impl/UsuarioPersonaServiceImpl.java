package mx.tecabix.db.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.tecabix.db.entity.UsuarioPersona;
import mx.tecabix.db.repository.UsuarioPersonaRepository;
import mx.tecabix.db.service.UsuarioPersonaService;

@Service
public class UsuarioPersonaServiceImpl implements UsuarioPersonaService{

	
	@Autowired
	private UsuarioPersonaRepository usuarioPersonaRepository;
	
	@Override
	public UsuarioPersona findById(long id) {
		UsuarioPersona usuarioPersona = null;
		Optional<UsuarioPersona> o = usuarioPersonaRepository.findById(id);
		if(o.isPresent()) {
			usuarioPersona =  o.get();
		}
		return usuarioPersona;
	}

	@Override
	public UsuarioPersona findByUsuario(String nombre) {
		UsuarioPersona usuarioPersona = usuarioPersonaRepository.findByUsuario(nombre);
		return usuarioPersona;
	}

	@Override
	public UsuarioPersona save(UsuarioPersona save) {
		save = usuarioPersonaRepository.save(save);
		return save;
	}

	@Override
	public UsuarioPersona update(UsuarioPersona update) {
		update = usuarioPersonaRepository.save(update);
		return update;
	}

}
