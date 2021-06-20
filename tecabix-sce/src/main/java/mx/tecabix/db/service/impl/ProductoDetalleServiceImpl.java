package mx.tecabix.db.service.impl;

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
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.ProductoDetalle;
import mx.tecabix.db.repository.ProductoDetalleRepository;
import mx.tecabix.db.service.ProductoDetalleService;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class ProductoDetalleServiceImpl extends GenericSeviceImpl<ProductoDetalle, Long>
		implements ProductoDetalleService {

	@Autowired
	private ProductoDetalleRepository productoDetalleRepository;

	@Override
	@PostConstruct
	protected void postConstruct() {
		setJpaRepository(productoDetalleRepository);
	}

	@Override
	public Optional<ProductoDetalle> findByClave(UUID uuid) {
		return productoDetalleRepository.findByClave(uuid);
	}

}
