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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.tecabix.db.GenericSeviceImpl;
import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.repository.AuthorityRepository;
import mx.tecabix.db.service.AuthorityService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class AuthorityServiceImpl extends GenericSeviceImpl<Authority, Integer> implements AuthorityService{

	@Autowired
	private AuthorityRepository authorityRepository;

	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(authorityRepository);
	}

	@Override
	public Page<Authority> findByLikeNombre(String nombre, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Authority> result = authorityRepository.findByLikeNombre(nombre, pageable);
		return result;
	}

	@Override
	public Optional<Authority> findByNombre(String nombre) {
		Optional<Authority> optional = authorityRepository.findByNombre(nombre);
		return optional;
	}
}
