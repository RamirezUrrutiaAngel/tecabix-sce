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
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.repository.CatalogoRepository;
import mx.tecabix.db.service.CatalogoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class CatalogoServiceImpl extends GenericSeviceImpl<Catalogo, Integer> implements CatalogoService {

	@Autowired
	private CatalogoRepository cataRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(cataRepository);
	}

	@Override
	public Optional<Catalogo> findByTipoAndNombre(String tipo, String nombre) {
		Optional<Catalogo> entity = cataRepository.findByTipoAndNombre(tipo, nombre);
		return entity;
	}

	@Override
	public Optional<Catalogo> findByClave(UUID uuid) {
		Optional<Catalogo> entity = cataRepository.findByClave(uuid);
		return entity;
	}
}
