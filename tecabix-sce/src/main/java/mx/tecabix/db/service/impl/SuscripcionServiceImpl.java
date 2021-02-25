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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Suscripcion;
import mx.tecabix.db.generic.GenericSeviceImpl;
import mx.tecabix.db.repository.SuscripcionRepository;
import mx.tecabix.db.service.SuscripcionService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class SuscripcionServiceImpl extends GenericSeviceImpl<Suscripcion, Long> implements SuscripcionService{

	@Autowired
	private SuscripcionRepository suscripcionRepository;

	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(suscripcionRepository);
		
	}

	@Override
	public Optional<Suscripcion> findByIdEscuela(Long idEscuela) {
		Optional<Suscripcion> result = suscripcionRepository.findByIdEscuela(idEscuela);
		return result;
	}
	
	@Override
	public Optional<Suscripcion> findByIdEscuelaAndValid(Long idEscuela) {
		Optional<Suscripcion> result = suscripcionRepository.findByIdEscuelaAndValid(idEscuela);
		return result;
	}

}
