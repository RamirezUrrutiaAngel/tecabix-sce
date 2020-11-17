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
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.repository.CatalogoRepository;
import mx.tecabix.db.service.CatalogoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class CatalogoServiceImpl implements CatalogoService {

	@Autowired
	private CatalogoRepository cataRepository;
	
	public Page<Catalogo> findAll() {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		Page<Catalogo> request = cataRepository.findAll(pageable);
		
		return request;
	}

	@Override
	public Catalogo findByTipoAndNombre(String tipo, String nombre) {
		try {
		
			Catalogo entity = cataRepository.findByTipoAndNombre(tipo, nombre);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
