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

import mx.tecabix.db.GenericSevice;
import mx.tecabix.db.entity.Sesion;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
public interface SesionService extends GenericSevice<Sesion, Long>{
	
	Page<Sesion> findByActive(Long idEmpresa, int elements, int page);
	
	Page<Sesion> findByActiveAndLikeUsuario(Long idEmpresa, String usuario, int elements, int page);
	
	Page<Sesion> findByActiveAndLikeLicencia(Long idEmpresa, String licencia, int elements, int page);
	
	Page<Sesion> findByActiveAndLikeServicio(Long idEmpresa, String servicio, int elements, int page);
	
	Page<Sesion> findByLicenciaAndActive(Long idLicencia, int elements, int page);
	
	Page<Sesion> findByUsuarioAndActive(Long idLicencia,Long idUsuario, int elements, int page);
	
	Page<Sesion> findByNow(Long idLicencia,int elements,int page) ;
	
	Page<Sesion> findByUsuarioAndNow(Long idLicencia, Long idUsuario, int elements,int page) ;
	
	Optional<Sesion> findByToken(UUID keyToken);
	
	Optional<Sesion> findByClave(UUID uuid);
	
}
