package mx.tecabix.db.service;

import java.util.List;


import mx.tecabix.db.entity.Sesion;

public interface SesionService {

	Sesion save(Sesion save);
	
	Sesion update(Sesion update);
	
	List<Sesion> findByActive(Long idLicencia);
	
	List<Sesion> findByUsuarioAndActive(Long idLicencia,Long idUsuario);
	
	List<Sesion> findByNow(Long idLicencia,int elements,int page) ;
	
	List<Sesion> findByUsuarioAndNow(Long idLicencia, Long idUsuario, int elements,int page) ;
	
	Sesion findByToken(String keyToken);
	
	
}
