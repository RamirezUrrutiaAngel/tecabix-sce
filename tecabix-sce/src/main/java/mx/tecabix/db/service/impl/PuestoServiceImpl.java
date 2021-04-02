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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.Puesto;
import mx.tecabix.db.repository.PuestoRepository;
import mx.tecabix.db.service.PuestoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class PuestoServiceImpl extends GenericSeviceImpl<Puesto, Long> implements PuestoService{

	@Autowired
	private PuestoRepository puestoRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(puestoRepository);
	}
	@Override
	public Page<Puesto> findByIdEmpresa(Long idEmpresa, int elements, int page, Sort sort){
		Pageable pageable = PageRequest.of(page, elements, sort);
		return puestoRepository.findByIdEmpresa(idEmpresa, pageable);
	}
	@Override
	public Optional<Puesto> findByClave(UUID uuid) {
		return puestoRepository.findByClave(uuid);
	}
	@Override
	public Page<Puesto> findByLikeNombre(String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		return puestoRepository.findByLikeNombre(nombre, pageable);
	}
	@Override
	public Page<Puesto> findByLikeDescripcion(String descripcion, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		return puestoRepository.findByLikeDescripcion(descripcion, pageable);
	}
	@Override
	public Page<Puesto> findByLikeDepartamento(String departamento, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort);
		return puestoRepository.findByLikeDepartamento(departamento, pageable);
	}
	@Override
	public Boolean canInsert(Long idEmpresa) {
		return puestoRepository.canInsert(idEmpresa);
	}

}
