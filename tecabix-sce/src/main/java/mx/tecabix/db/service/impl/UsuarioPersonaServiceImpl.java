/*
 *   This file is part of Foobar.
 *
 *   Foobar is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package mx.tecabix.db.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.tecabix.db.entity.UsuarioPersona;
import mx.tecabix.db.repository.UsuarioPersonaRepository;
import mx.tecabix.db.service.UsuarioPersonaService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
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
