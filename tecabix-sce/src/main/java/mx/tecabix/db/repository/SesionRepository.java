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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import mx.tecabix.db.entity.Sesion;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface SesionRepository extends JpaRepository<Sesion, Long>{

	@Query(value = "SELECT MD5(?1)", nativeQuery = true)
	String getMD5(String text);
	
	Page<Sesion> findByActive(Long idLicencia,Pageable pageable);
	Page<Sesion> findByUsuarioAndActive(Long idLicencia,Long idUsuario,Pageable pageable);
	
	Page<Sesion> findByNow(Long idLicencia,Pageable pageable);
	Page<Sesion> findByUsuarioAndNow(Long idLicencia,Long idUsuari, Pageable pageable);
	
	Sesion findByToken(String keyToken);
	
}
