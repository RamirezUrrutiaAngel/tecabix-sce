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

import org.springframework.data.domain.Page;

import mx.tecabix.db.GenericSevice;
import mx.tecabix.db.entity.PerfilAuthority;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface PerfilAuthorityService extends GenericSevice<PerfilAuthority, Long>{

	Page<PerfilAuthority> findByPerfil(Long idPerfil);
	Page<PerfilAuthority> findByAuthority(Integer idAAuthority);
	Page<PerfilAuthority> findByPerfil(Long idPerfil, int elements, int page);
	Page<PerfilAuthority> findByAuthority(Integer idAAuthority, int elements, int page);
}
