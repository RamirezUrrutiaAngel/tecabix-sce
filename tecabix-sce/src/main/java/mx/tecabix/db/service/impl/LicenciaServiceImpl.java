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
import mx.tecabix.db.entity.Licencia;
import mx.tecabix.db.repository.LicenciaRepository;
import mx.tecabix.db.service.LicenciaService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class LicenciaServiceImpl extends GenericSeviceImpl<Licencia, Long> implements LicenciaService {

	@Autowired
	private LicenciaRepository licenciaRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(licenciaRepository);
		
	}
	
	@Override
	public Licencia findByToken(String key) {
		Licencia response = licenciaRepository.findByToken(key);
		return response;
	}

	@Override
	public Page<Licencia> findAll(int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Licencia> result = licenciaRepository.findAll(pageable);
		return result;
	}

	@Override
	public Page<Licencia> findByIdEscuela(Long idEscuela, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Licencia> result = licenciaRepository.findByIdEscuela(idEscuela, pageable);
		return result;
	}

	@Override
	public Licencia save(Licencia entity) {
		String key = licenciaRepository.getMD5(String.valueOf(System.currentTimeMillis()));
		entity.setToken(key);
		return super.save(entity);
	}

	@Override
	public Page<Licencia> findByIdEscuelaAndServicio(Long idEscuela, Integer idServicio, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Licencia> result = licenciaRepository.findByIdEscuelaAndServicio(idEscuela, idServicio, pageable);
		return result;
	}

	
	
	
}
