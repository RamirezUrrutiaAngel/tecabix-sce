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
import mx.tecabix.db.entity.PerfilAuthority;
import mx.tecabix.db.repository.PerfilAuthorityRepository;
import mx.tecabix.db.service.PerfilAuthorityService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class PerfilAuthorityServiceImpl extends GenericSeviceImpl<PerfilAuthority, Long> implements PerfilAuthorityService{

	@Autowired
	private PerfilAuthorityRepository perfilAuthorityRepository;
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(perfilAuthorityRepository);
		
	}
	@Override
	public Page<PerfilAuthority> findByPerfil(Long idPerfil) {
		byte ZERO = 0; 
		Page<PerfilAuthority> result = findByPerfil(idPerfil, Integer.MAX_VALUE, ZERO);
		return result;
	}
	@Override
	public Page<PerfilAuthority> findByAuthority(Integer idAuthority) {
		byte ZERO = 0; 
		Page<PerfilAuthority> result = findByAuthority(idAuthority, Integer.MAX_VALUE, ZERO);
		return result;
	}
	@Override
	public Page<PerfilAuthority> findByPerfil(Long idPerfil, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<PerfilAuthority> result = perfilAuthorityRepository.findByPerfil(idPerfil, pageable);
		return result;
	}
	@Override
	public Page<PerfilAuthority> findByAuthority(Integer idAAuthority, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<PerfilAuthority> result = perfilAuthorityRepository.findByAuthority(idAAuthority, pageable);
		return result;
	}

}
