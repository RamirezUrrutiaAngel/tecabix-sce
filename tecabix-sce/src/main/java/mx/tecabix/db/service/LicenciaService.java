package mx.tecabix.db.service;

import mx.tecabix.db.entity.Licencia;

public interface LicenciaService {
	
	Licencia findByToken(String key);

}
