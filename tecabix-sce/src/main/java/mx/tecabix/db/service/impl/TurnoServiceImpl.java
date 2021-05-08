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
import mx.tecabix.db.entity.Turno;
import mx.tecabix.db.repository.TurnoRepository;
import mx.tecabix.db.service.TurnoService;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class TurnoServiceImpl extends GenericSeviceImpl<Turno, Long> implements TurnoService{

	@Autowired
	private TurnoRepository turnoRepository;
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		setJpaRepository(turnoRepository);
	}
	
	@Override
	public Optional<Turno> findByClave(UUID uuid) {
		return turnoRepository.findByClave(uuid);
	}

	@Override
	public Boolean canInsert(Long idEmpresa) {
		return turnoRepository.canInsert(idEmpresa);
	}

	@Override
	public Page<Turno> findByLikeNombre(Long idEmpresa, String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return turnoRepository.findByLikeNombre(idEmpresa, nombre, pageable);
	}

	@Override
	public Page<Turno> findByLikeDescripcion(Long idEmpresa, String descripcion, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return turnoRepository.findByLikeDescripcion(idEmpresa, descripcion, pageable);
	}

	@Override
	public Page<Turno> findByIdEmpresa(Long idEmpresa, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return turnoRepository.findByIdEmpresa(idEmpresa, pageable);
	}

	@Override
	public Optional<Turno> findByNombre(Long idEmpresa, String nombre) {
		return turnoRepository.findByNombre(idEmpresa, nombre);
	}

}
