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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Suscripcion;
import mx.tecabix.db.generic.GenericSeviceImpl;
import mx.tecabix.db.repository.SesionRepository;
import mx.tecabix.db.service.SesionService;
import mx.tecabix.db.service.SuscripcionService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class SesionServiceImpl extends GenericSeviceImpl<Sesion, Long> implements SesionService{

	private static final Logger LOG = LoggerFactory.getLogger(SesionService.class);
	
	@Autowired
	private SesionRepository sesionRepository;
	@Autowired SuscripcionService suscripcionService;

	@PostConstruct
	@Override
	protected void postConstruct() {
		setJpaRepository(sesionRepository);
		
	}

	@Override
	public Sesion save(Sesion save) {
		String time = LocalDateTime.now().toString();
		String key = sesionRepository.getMD5(time);
		save.setToken(key);
		save = sesionRepository.save(save);
		return save;
	}

	@Override
	public Page<Sesion> findByActive(Long idLicencia, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Sesion> response = sesionRepository.findByActive(idLicencia,pageable);
		return response;
	}

	@Override
	public Page<Sesion> findByNow(Long idLicencia, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Sesion> entitys = sesionRepository.findByNow(idLicencia, pageable);
		return entitys;
	}

	@Override
	public Sesion findByToken(String keyToken) {
		synchronized (LOG) {
			try {
				Sesion response = sesionRepository.findByToken(keyToken);
				if(response != null) {
					LocalDateTime hoy = LocalDateTime.now();
					if(response.getLicencia().getTipo().getNombre().equals("WEB")) {
						if(response.getVencimiento().isBefore(hoy)) {
							Long idEscuela = response.getLicencia().getPlantel().getIdEscuela();
							Optional<Suscripcion> optionalSuscripsion = suscripcionService.findByIdEscuelaAndValid(idEscuela);
							if(!optionalSuscripsion.isPresent()) {
								LOG.warn("SUSCRIPCION CADUCADA PARA EL ID "+idEscuela);
								return null;
							}
							LocalDateTime vencimiento = LocalDateTime.of(hoy.toLocalDate(),LocalTime.of(23, 59));
							response.setVencimiento(vencimiento);
							response.setPeticionesRestantes(response.getLicencia().getServicio().getPeticiones());
						}
					}
					Integer numeroDePeticionesRestante = response.getPeticionesRestantes();
					numeroDePeticionesRestante -= 1;
					response.setPeticionesRestantes(numeroDePeticionesRestante);
					response = sesionRepository.save(response);
					return response;
				}
				return response;
			} catch (Exception e) {
				LOG.error("Excepcion en findByToken", e.getCause());
				return null;
			}	
		}
	}

	@Override
	public Page<Sesion> findByUsuarioAndActive(Long idLicencia, Long idUsuario, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Sesion> response = sesionRepository.findByUsuarioAndActive(idLicencia,idUsuario,pageable);
		return response;
	}

	@Override
	public Page<Sesion> findByUsuarioAndNow(Long idLicencia, Long idUsuario, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Sesion> entitys = sesionRepository.findByUsuarioAndNow(idLicencia, idUsuario, pageable);
		return entitys;
	}

}
