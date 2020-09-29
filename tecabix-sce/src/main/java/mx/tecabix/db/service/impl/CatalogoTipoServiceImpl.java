package mx.tecabix.db.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import mx.tecabix.db.entity.CatalogoTipo;
import mx.tecabix.db.repository.CatalogoTipoRepository;
import mx.tecabix.db.service.CatalogoTipoService;
@Service
public class CatalogoTipoServiceImpl implements CatalogoTipoService{

	@Autowired
	private CatalogoTipoRepository cataGrupoRepository;;
	
	@Override
	public CatalogoTipo findByNombre(String String) {
		return cataGrupoRepository.findByNombre(String);
	}

}
