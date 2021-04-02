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
import mx.tecabix.db.entity.Perfil;
import mx.tecabix.db.repository.PerfilRepository;
import mx.tecabix.db.service.PerfilService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class PerfilServiceImpl extends GenericSeviceImpl<Perfil, Long> implements PerfilService{

	@Autowired
	private PerfilRepository perfilRepository;
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		setJpaRepository(perfilRepository);
	}

	@Override
	public Page<Perfil> findAll(Long idEmpresa, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		Page<Perfil> perfiles = perfilRepository.findAll(idEmpresa, pageable);
		return perfiles;
	}

	@Override
	public Page<Perfil> findByLikeNombre(Long idEmpresa, String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		Page<Perfil> perfiles = perfilRepository.findByLikeNombre(idEmpresa, nombre, pageable);
		return perfiles;
	}

	@Override
	public Page<Perfil> findByNombre(Long idEmpresa, String nombre, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Perfil> perfil = perfilRepository.findByNombre(idEmpresa, nombre, pageable);
		return perfil;
	}

	@Override
	public Optional<Perfil> findByClave(UUID uuid) {
		Optional<Perfil> result = perfilRepository.findByClave(uuid);
		return result;
	}

	@Override
	public Page<Perfil> findByLikeDescripcion(Long idEmpresa, String descripcion, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		Page<Perfil> perfiles = perfilRepository.findByLikeDescripcion(idEmpresa, descripcion, pageable);
		return perfiles;
	}	
	
	@Override
	public Boolean canInsert(Long idEmpresa) {
		return perfilRepository.canInsert(idEmpresa);
	}
}
