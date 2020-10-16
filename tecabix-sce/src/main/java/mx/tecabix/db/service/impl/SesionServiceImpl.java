package mx.tecabix.db.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.repository.SesionRepository;
import mx.tecabix.db.service.SesionService;

@Service
public class SesionServiceImpl implements SesionService{

	private static final Logger LOG = LoggerFactory.getLogger(SesionService.class);
	
	@Autowired
	private SesionRepository sesionRepository;

	@Override
	public Sesion save(Sesion save) {
		String time = LocalDateTime.now().toString();
		String key = sesionRepository.getMD5(time);
		save.setToken(key);
		save = sesionRepository.save(save);
		return save;
	}

	@Override
	public List<Sesion> findByActive(Long idLicencia) {
		List<Sesion> response = sesionRepository.findByActive(idLicencia);
		return response;
	}

	@Override
	public List<Sesion> findByNow(Long idLicencia, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Sesion> entitys = sesionRepository.findByNow(idLicencia, pageable);
		List<Sesion> list = entitys.getContent();
		return list;
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
	public List<Sesion> findByUsuarioAndActive(Long idLicencia, Long idUsuario) {
		List<Sesion> response = sesionRepository.findByUsuarioAndActive(idLicencia,idUsuario);
		return response;
	}

	@Override
	public List<Sesion> findByUsuarioAndNow(Long idLicencia, Long idUsuario, int elements, int page) {
		Pageable pageable = PageRequest.of(page, elements);
		Page<Sesion> entitys = sesionRepository.findByUsuarioAndNow(idLicencia, idUsuario, pageable);
		List<Sesion> list = entitys.getContent();
		return list;
	}

	@Override
	public Sesion update(Sesion update) {
		update = sesionRepository.save(update);
		return update;
	}
	
	
}
