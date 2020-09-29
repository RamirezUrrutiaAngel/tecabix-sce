package mx.tecabix.service.response;

import java.util.List;

import mx.tecabix.db.entity.Estado;

public class EstadoListResponse {

	private List<Estado> estados;

	public List<Estado> getEstados() {
		return estados;
	}

	public void setEstados(List<Estado> estados) {
		this.estados = estados;
	}
	
	
}
