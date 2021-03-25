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
import mx.tecabix.db.entity.Estado;
import mx.tecabix.db.repository.EstadoRepository;
import mx.tecabix.db.service.EstadoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class EstadoServiceImpl extends GenericSeviceImpl<Estado, Integer> implements EstadoService{

	@Autowired
	private EstadoRepository estadoRepository;

	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(estadoRepository);
		
	}

	@Override
	public Optional<Estado> findByClave(UUID uuid) {
		return estadoRepository.findByClave(uuid);
	}

	@Override
	public Page<Estado> findByActivo(int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return estadoRepository.findByActivo(pageable);
	}

	@Override
	public Page<Estado> findByLikeNombre(String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return estadoRepository.findByLikeNombre(nombre, pageable);
	}

	@Override
	public Page<Estado> findByLikeAbreviatura(String descripcion, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return estadoRepository.findByLikeAbreviatura(descripcion, pageable);
	}

	
}
