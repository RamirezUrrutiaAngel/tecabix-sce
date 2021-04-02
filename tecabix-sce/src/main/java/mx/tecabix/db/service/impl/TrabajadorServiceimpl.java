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
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.repository.TrabajadorRepository;
import mx.tecabix.db.service.TrabajadorService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class TrabajadorServiceimpl extends GenericSeviceImpl<Trabajador, Long> implements TrabajadorService {

	@Autowired
	private TrabajadorRepository trabajadorRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(trabajadorRepository);
		
	}
	
	@Override
	public Optional<Trabajador> findByClave(UUID uuid) {
		return trabajadorRepository.findByClave(uuid);
	}

	@Override
	public Page<Trabajador> findByLikeCURP(Long idEmpresa, String CURP, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return trabajadorRepository.findByLikeCURP(idEmpresa, CURP, pageable);
	}

	@Override
	public Page<Trabajador> findByLikeNombre(Long idEmpresa, String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return trabajadorRepository.findByLikeNombre(idEmpresa, nombre, pageable);
	}

	@Override
	public Page<Trabajador> findByLikeApellidoPaterno(Long idEmpresa, String apellidoPaterno, int elements, int page,
			Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return trabajadorRepository.findByLikeApellidoPaterno(idEmpresa, apellidoPaterno, pageable);
	}

	@Override
	public Page<Trabajador> findByLikeApellidoMaterno(Long idEmpresa, String apellidoMaterno, int elements, int page,
			Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return trabajadorRepository.findByLikeApellidoMaterno(idEmpresa, apellidoMaterno, pageable);
	}

	@Override
	public Page<Trabajador> findByIdEmpresa(Long idEmpresa, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return trabajadorRepository.findByIdEmpresa(idEmpresa, pageable);
	}

	@Override
	public Page<Trabajador> findByLikePuesto(Long idEmpresa, String puesto, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return trabajadorRepository.findByLikePuesto(idEmpresa, puesto, pageable);
	}

	@Override
	public Page<Trabajador> findByLikePlantel(Long idEmpresa, String plantel, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return trabajadorRepository.findByLikePlantel(idEmpresa, plantel, pageable);
	}
	
	@Override
	public Boolean canInsert(Long idEmpresa) {
		return trabajadorRepository.canInsert(idEmpresa);
	}
}
