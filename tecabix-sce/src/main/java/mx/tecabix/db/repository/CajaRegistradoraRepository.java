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
package mx.tecabix.db.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import mx.tecabix.db.entity.CajaRegistradora;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface CajaRegistradoraRepository  extends JpaRepository<CajaRegistradora, Long>{

	Page<CajaRegistradora> findByIdEmpresa(Long idEmpresa, Pageable pageable);
	Page<CajaRegistradora> findLikeNombre(Long idEmpresa, String nombre, Pageable pageable);
	Page<CajaRegistradora> findLikeDescripcion(Long idEmpresa, String descripcion, Pageable pageable);
	Page<CajaRegistradora> findLikeMarca(Long idEmpresa, String marca, Pageable pageable);
	Page<CajaRegistradora> findLikeModelo(Long idEmpresa, String modelo, Pageable pageable);
	
	Optional<CajaRegistradora> findByIdLicencia(Long idLicencia);
	Optional<CajaRegistradora> findByClave(UUID uuid);

}