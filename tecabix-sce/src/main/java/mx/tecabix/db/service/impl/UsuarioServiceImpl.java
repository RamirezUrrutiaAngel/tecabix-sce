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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.repository.UsuarioRepository;
import mx.tecabix.db.service.UsuarioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
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

	@Override
	public Usuario findById(long id) {
		Usuario usuario = null;
		Optional<Usuario> o = usuarioRepository.findById(id);
		if(o.isPresent()) {
			usuario = o.get();
		}
		return usuario;
	}
}
