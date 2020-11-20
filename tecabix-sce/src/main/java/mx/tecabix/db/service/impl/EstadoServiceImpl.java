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
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Estado;
import mx.tecabix.db.generic.GenericSeviceImpl;
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

	
}
