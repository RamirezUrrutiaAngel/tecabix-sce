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
import mx.tecabix.db.entity.Autorizacion;
import mx.tecabix.db.entity.Salario;
import mx.tecabix.db.repository.SalarioRepository;
import mx.tecabix.db.service.SalarioService;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class SalarioServiceImpl  extends GenericSeviceImpl<Salario, Long> implements SalarioService{

	@Autowired
	private SalarioRepository salarioRepository;
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		setJpaRepository(salarioRepository);
	}
	
	@Override
	public Optional<Autorizacion> findByClave(UUID uuid) {
		return salarioRepository.findByClave(uuid);
	}
}
