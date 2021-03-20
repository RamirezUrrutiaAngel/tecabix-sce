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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.Departamento;
import mx.tecabix.db.repository.DepartamentoRepository;
import mx.tecabix.db.service.DepartamentoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class DepartamentoServiceImpl extends GenericSeviceImpl<Departamento, Long> implements DepartamentoService{

	@Autowired
	private DepartamentoRepository departamentoRepository;

	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(departamentoRepository);
		
	}

	@Override
	public Page<Departamento> findByIdEmpresa(Long idEmpresa, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Departamento> response = departamentoRepository.findByIdEmpresa(idEmpresa, pageable);
		return response;
	}

	@Override
	public Optional<Departamento> findByClave(UUID uuid) {
		Optional<Departamento> result = departamentoRepository.findByClave(uuid);
		return result;
	}
	
}
