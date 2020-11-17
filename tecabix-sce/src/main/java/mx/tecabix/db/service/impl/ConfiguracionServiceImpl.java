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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import mx.tecabix.db.entity.Configuracion;
import mx.tecabix.db.repository.ConfiguracionRepository;
import mx.tecabix.db.service.ConfiguracionService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public class ConfiguracionServiceImpl implements ConfiguracionService{

	@Autowired
	private ConfiguracionRepository configuracionRepository;
	@Override
	public Page<Configuracion> findByIdEscuela(long id, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Configuracion> entitys = configuracionRepository.findByIdEscuela(id, pageable);
		return entitys;
	}
	@Override
	public Configuracion save(Configuracion save) {
		save = configuracionRepository.save(save);
		return save;
	}
	@Override
	public Configuracion update(Configuracion update) {
		update = configuracionRepository.save(update);
		return update;
	}

}
