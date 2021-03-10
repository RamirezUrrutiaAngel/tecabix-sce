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
import mx.tecabix.db.entity.Banco;
import mx.tecabix.db.repository.BancoRepository;
import mx.tecabix.db.service.BancoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class BancoServiceImpl extends GenericSeviceImpl<Banco, Integer> implements BancoService{

	@Autowired
	private BancoRepository bancoRepository;
	
	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(bancoRepository);
	}

	@Override
	public Optional<Banco> findByClave(UUID uuid) {
		Optional<Banco> result = bancoRepository.findByClave(uuid);
		return result;
	}

	@Override
	public Page<Banco> findByLikeNombre(String nombre, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return bancoRepository.findByLikeNombre(nombre, pageable);
	}

	@Override
	public Page<Banco> findByLikeClaveBanco(String claveBanco, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return bancoRepository.findByLikeClaveBanco(claveBanco, pageable);
	}

	@Override
	public Page<Banco> findByLikeRazonSocial(String razonSocial, int elements, int page, Sort sort) {
		Pageable pageable = PageRequest.of(page, elements, sort );
		return bancoRepository.findByLikeRazonSocial(razonSocial, pageable);
	}

}
