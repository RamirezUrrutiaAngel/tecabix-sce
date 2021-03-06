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
import org.springframework.stereotype.Service;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.Escuela;
import mx.tecabix.db.repository.EscuelaRepository;
import mx.tecabix.db.service.EscuelaService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class EscuelaServiceImpl extends GenericSeviceImpl<Escuela, Long>implements EscuelaService{

	@Autowired
	private EscuelaRepository empresaRespository;

	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(empresaRespository);
		
	}

	@Override
	public Optional<Escuela> findByNameRegardlessOfStatus(String nombre) {
		Optional<Escuela> result = empresaRespository.findByNameRegardlessOfStatus(nombre);
		return result;
	}

	@Override
	public Optional<Escuela> findByClave(UUID uuid) {
		Optional<Escuela> result = empresaRespository.findByClave(uuid);
		return result;
	}


}
