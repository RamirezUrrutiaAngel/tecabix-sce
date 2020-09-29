package mx.tecabix.service.response;

import java.util.List;

import mx.tecabix.db.entity.CatalogoTipo;

public class CatalogoTipoListResponse {

	private List<CatalogoTipo> cataGrupos;

	public List<CatalogoTipo> getCataGrupos() {
		return cataGrupos;
	}

	public void setCataGrupos(List<CatalogoTipo> cataGrupos) {
		this.cataGrupos = cataGrupos;
	}
	
	
}
