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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.Puesto;
import mx.tecabix.db.repository.PuestoRepository;
import mx.tecabix.db.service.PuestoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class PuestoServiceImpl extends GenericSeviceImpl<Puesto, Long> implements PuestoService{

	@Autowired
	private PuestoRepository puestoRepository;
	@Override
	protected void postConstruct() {
		setJpaRepository(puestoRepository);
		
	}
	@Override
	public Page<Puesto> findByIdEscuela(Long idEscuela, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Puesto> puestos = puestoRepository.findByIdEscuela(idEscuela, pageable);
		return puestos;
	}
	@Override
	public Optional<Puesto> findByClave(UUID uuid) {
		return puestoRepository.findByClave(uuid);
	}

}
