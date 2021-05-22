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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.PerfilAutorizacion;
import mx.tecabix.db.repository.PerfilAutorizacionRepository;
import mx.tecabix.db.service.PerfilAutorizacionService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class PerfilAutorizacionServiceImpl extends GenericSeviceImpl<PerfilAutorizacion, Long> implements PerfilAutorizacionService{

	@Autowired
	private PerfilAutorizacionRepository perfilAutorizacionRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(perfilAutorizacionRepository);
		
	}
	@Override
	public Page<PerfilAutorizacion> findByPerfil(Long idPerfil) {
		byte ZERO = 0; 
		Page<PerfilAutorizacion> result = findByPerfil(idPerfil, Integer.MAX_VALUE, ZERO);
		return result;
	}
	@Override
	public Page<PerfilAutorizacion> findByAutorizacion(Integer idAutorizacion) {
		byte ZERO = 0; 
		Page<PerfilAutorizacion> result = findByAutorizacion(idAutorizacion, Integer.MAX_VALUE, ZERO);
		return result;
	}
	@Override
	public Page<PerfilAutorizacion> findByPerfil(Long idPerfil, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<PerfilAutorizacion> result = perfilAutorizacionRepository.findByPerfil(idPerfil, pageable);
		return result;
	}
	@Override
	public Page<PerfilAutorizacion> findByAutorizacion(Integer idAutorizacion, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<PerfilAutorizacion> result = perfilAutorizacionRepository.findByAutorizacion(idAutorizacion, pageable);
		return result;
	}

}
