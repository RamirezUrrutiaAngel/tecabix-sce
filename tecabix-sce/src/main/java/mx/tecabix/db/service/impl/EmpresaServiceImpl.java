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
import mx.tecabix.db.entity.Empresa;
import mx.tecabix.db.repository.EmpresaRepository;
import mx.tecabix.db.service.EmpresaService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class EmpresaServiceImpl extends GenericSeviceImpl<Empresa, Long>implements EmpresaService{

	@Autowired
	private EmpresaRepository empresaRespository;

	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(empresaRespository);
		
	}

	@Override
	public Optional<Empresa> findByNameRegardlessOfStatus(String nombre) {
		Optional<Empresa> result = empresaRespository.findByNameRegardlessOfStatus(nombre);
		return result;
	}

	@Override
	public Optional<Empresa> findByClave(UUID uuid) {
		Optional<Empresa> result = empresaRespository.findByClave(uuid);
		return result;
	}


}
