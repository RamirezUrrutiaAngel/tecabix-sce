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
import mx.tecabix.db.entity.Configuracion;
import mx.tecabix.db.repository.ConfiguracionRepository;
import mx.tecabix.db.service.ConfiguracionService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class ConfiguracionServiceImpl extends GenericSeviceImpl<Configuracion, Long> implements ConfiguracionService{

	@Autowired
	private ConfiguracionRepository configuracionRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(configuracionRepository);
		
	}

	@Override
	public Optional<Configuracion> findByClave(UUID uuid) {
		Optional<Configuracion> result = configuracionRepository.findByClave(uuid);
		return result;
	}
	

}
