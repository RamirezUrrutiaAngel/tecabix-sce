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
import mx.tecabix.db.entity.Licencia;
import mx.tecabix.db.repository.LicenciaRepository;
import mx.tecabix.db.service.LicenciaService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class LicenciaServiceImpl extends GenericSeviceImpl<Licencia, Long> implements LicenciaService {

	@Autowired
	private LicenciaRepository licenciaRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(licenciaRepository);
		
	}
	
	@Override
	public Licencia findByToken(UUID key) {
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
	public Page<Licencia> findByIdEmpresa(Long idEmpresa, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Licencia> result = licenciaRepository.findByIdEmpresa(idEmpresa, pageable);
		return result;
	}

	@Override
	public Licencia save(Licencia entity) {
		return super.save(entity);
	}

	@Override
	public Page<Licencia> findByIdEmpresaAndServicio(Long idEmpresa, Integer idServicio, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Licencia> result = licenciaRepository.findByIdEmpresaAndServicio(idEmpresa, idServicio, pageable);
		return result;
	}

	@Override
	public Optional<Licencia> findByClave(UUID uuid) {
		Optional<Licencia> result = licenciaRepository.findByClave(uuid);
		return result;
	}

	
	
	
}
