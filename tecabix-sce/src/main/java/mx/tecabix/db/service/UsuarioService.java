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
import mx.tecabix.db.entity.Usuario;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface UsuarioService extends GenericSevice<Usuario, Long>{
	
	Page<Usuario> findByLikeNombre(String nombre, int elements, int page, Sort sort);
	Page<Usuario> findByLikeCorreo(String correo, int elements, int page, Sort sort);
	Page<Usuario> findByLikePerfil(String perfil, int elements, int page, Sort sort);
	Optional<Usuario> findByNameRegardlessOfStatus(String nombre);
	Optional<Usuario> findByNombre(String nombre);
	Optional<Usuario> findByClave(UUID uuid);
}
