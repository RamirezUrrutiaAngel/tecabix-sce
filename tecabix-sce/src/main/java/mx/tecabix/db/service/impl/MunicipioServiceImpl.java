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
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.repository.MunicipioRepository;
import mx.tecabix.db.service.MunicipioService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class MunicipioServiceImpl extends GenericSeviceImpl<Municipio, Integer> implements MunicipioService{

	@Autowired
	private MunicipioRepository municipioRepository;

	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(municipioRepository);
	}

	@Override
	public Optional<Municipio> findByClave(UUID uuid) {
		return municipioRepository.findByClave(uuid);
	}

	@Override
	public Page<Municipio> findByLikeNombre(String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return municipioRepository.findByLikeNombre(nombre, pageable);
	}

	@Override
	public Page<Municipio> findByEstadoClave(UUID estadoClave, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return municipioRepository.findByEstadoClave(estadoClave, pageable);
	}

	@Override
	public Page<Municipio> findByActivo(int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return municipioRepository.findByActivo(pageable);
	}
	

}
