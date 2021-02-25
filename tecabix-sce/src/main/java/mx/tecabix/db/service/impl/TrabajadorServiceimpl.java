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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.generic.GenericSeviceImpl;
import mx.tecabix.db.repository.TrabajadorRepository;
import mx.tecabix.db.service.TrabajadorService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class TrabajadorServiceimpl extends GenericSeviceImpl<Trabajador, Long> implements TrabajadorService {

	@Autowired
	private TrabajadorRepository trabajadorRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(trabajadorRepository);
		
	}
	
	@Override
	public Trabajador findByKey(Long id) {
		Trabajador trabajador = trabajadorRepository.findByKey(id);
		return trabajador;
	}
	
	@Override
	public Trabajador findByUsuario(String usuario) {
		Trabajador trabajador = trabajadorRepository.findByUsuario(usuario);
		return trabajador;
	}

	@Override
	public Page<Trabajador> findAll(Long idEscuela,int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Trabajador> entitys = trabajadorRepository.findAll(idEscuela, pageable);
		return entitys;
	}

	@Override
	public Page<Trabajador> findAllByNombre(Long idEscuela, String nombre, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Trabajador> entitys = trabajadorRepository.findAllByNombre(idEscuela,nombre, pageable);
		return entitys;
	}

	@Override
	public Trabajador findByIdAndPendiente(Long id) {
		Trabajador trabajador = trabajadorRepository.findByKeyAndPendiente(id);
		return trabajador;
	}

}
