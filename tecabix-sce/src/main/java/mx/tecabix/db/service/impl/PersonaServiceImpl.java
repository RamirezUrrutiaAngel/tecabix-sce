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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.repository.PersonaRepository;
import mx.tecabix.db.service.PersonaService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class PersonaServiceImpl implements PersonaService{

	@Autowired
	private PersonaRepository personaRepository;
	
	@Override
	public Persona save(Persona save) {
		save = personaRepository.save(save);
		return save;
	}

	@Override
	public Persona update(Persona update) {
		update = personaRepository.save(update);
		return update;
	}

	@Override
	public Persona findById(long id) {
		Optional<Persona> o = personaRepository.findById(id);
		Persona result = null;
		if(o.isPresent()) {
			result = o.get();
		}
		return result;
	}
}