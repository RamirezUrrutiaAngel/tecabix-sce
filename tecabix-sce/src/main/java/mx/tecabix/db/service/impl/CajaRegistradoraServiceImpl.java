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
import mx.tecabix.db.entity.CajaRegistradora;
import mx.tecabix.db.repository.CajaRegistradoraRepository;
import mx.tecabix.db.service.CajaRegistradoraService;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class CajaRegistradoraServiceImpl extends GenericSeviceImpl<CajaRegistradora, Long>
	implements CajaRegistradoraService{
	
	@Autowired
	private CajaRegistradoraRepository cajaRegistradoraRepository;
	
	@Override
	@PostConstruct
	protected void postConstruct() {
		setJpaRepository(cajaRegistradoraRepository);
	}

	@Override
	public Optional<CajaRegistradora> findByClave(UUID uuid) {
		return cajaRegistradoraRepository.findByClave(uuid);
	}

	@Override
	public Page<CajaRegistradora> findByIdEmpresa(Long idEmpresa, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return cajaRegistradoraRepository.findByIdEmpresa(idEmpresa, pageable);
	}

	@Override
	public Page<CajaRegistradora> findLikeNombre(Long idEmpresa, String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return cajaRegistradoraRepository.findLikeNombre(idEmpresa, nombre, pageable);
	}

	@Override
	public Page<CajaRegistradora> findLikeDescripcion(Long idEmpresa, String descripcion, int elements, int page,
			Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return cajaRegistradoraRepository.findLikeDescripcion(idEmpresa, descripcion, pageable);
	}

	@Override
	public Page<CajaRegistradora> findLikeMarca(Long idEmpresa, String marca, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return cajaRegistradoraRepository.findLikeMarca(idEmpresa, marca, pageable);
	}

	@Override
	public Page<CajaRegistradora> findLikeModelo(Long idEmpresa, String modelo, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return cajaRegistradoraRepository.findLikeModelo(idEmpresa, modelo, pageable);
	}

	@Override
	public Optional<CajaRegistradora> findByIdLicencia(Long idLicencia) {
		return cajaRegistradoraRepository.findByIdLicencia(idLicencia);
	}

}
