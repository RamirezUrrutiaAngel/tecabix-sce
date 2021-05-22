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
package mx.tecabix.db.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import mx.tecabix.db.GenericSevice;
import mx.tecabix.db.entity.Autorizacion;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface AutorizacionService extends GenericSevice<Autorizacion, Integer>{

	Page<Autorizacion> findByLikeNombre(String nombre, int elements, int page, Sort sort);
	
	Page<Autorizacion> findByLikeDescripcion(String descripcion, int elements, int page, Sort sort);
	
	Optional<Autorizacion> findByNombre(String nombre);
	
	Optional<Autorizacion> findByClave(UUID uuid);
}
