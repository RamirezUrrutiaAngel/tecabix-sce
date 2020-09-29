package mx.tecabix.db.service;

import java.util.List;

import mx.tecabix.db.entity.Catalogo;

public interface CatalogoService {

	public List<Catalogo>findAll();
	
	public Catalogo findByTipoAndNombre(String tipo,String nombre);
}
