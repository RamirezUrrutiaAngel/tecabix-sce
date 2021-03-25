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

import mx.tecabix.db.entity.Estado;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface EstadoRepository extends JpaRepository<Estado, Integer>{

	Page<Estado> findByActivo(Pageable pageable);
	Page<Estado> findByLikeNombre(String nombre, Pageable pageable);
	Page<Estado> findByLikeAbreviatura(String descripcion, Pageable pageable);
	Optional<Estado> findByClave(UUID uuid);
}
