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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.repository.AuthorityRepository;
import mx.tecabix.db.service.AuthorityService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class AuthorityServiceImpl implements AuthorityService{

	@Autowired
	private AuthorityRepository authorityRepository;
	
	public Page<Authority> findAll(){
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		Page<Authority> list = authorityRepository.findAll(pageable);
		return list;
	}

	@Override
	public Optional<Authority> findById(Integer id) {
		Optional<Authority> response = authorityRepository.findById(id);
		return response;
	}
}
