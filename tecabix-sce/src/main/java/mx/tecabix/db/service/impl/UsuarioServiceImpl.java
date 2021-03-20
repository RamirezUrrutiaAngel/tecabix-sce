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
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.repository.UsuarioRepository;
import mx.tecabix.db.service.UsuarioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class UsuarioServiceImpl extends GenericSeviceImpl<Usuario, Long> implements UsuarioService{

	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(usuarioRepository);
	}
	
	@Override
	public Optional<Usuario> findByNombre(String nombre) {
		return usuarioRepository.findByNombre(nombre);
	}

	@Override
	public Optional<Usuario> findByNameRegardlessOfStatus(String nombre) {
		return usuarioRepository.findByNameRegardlessOfStatus(nombre);
	}

	@Override
	public Optional<Usuario> findByClave(UUID uuid) {
		return usuarioRepository.findByClave(uuid);
	}

	@Override
	public Page<Usuario> findByLikeNombre(String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		return usuarioRepository.findByLikeNombre(nombre, pageable);
	}

	@Override
	public Page<Usuario> findByLikeCorreo(String correo, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		return usuarioRepository.findByLikeNombre(correo, pageable);
	}

	@Override
	public Page<Usuario> findByLikePerfil(String perfil, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		return usuarioRepository.findByLikeNombre(perfil, pageable);
	}
}
