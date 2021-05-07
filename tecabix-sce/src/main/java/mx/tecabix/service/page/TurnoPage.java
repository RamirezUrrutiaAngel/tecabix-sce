package mx.tecabix.service.page;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;

import mx.tecabix.db.entity.Turno;
import mx.tecabix.service.PageGeneric;

public class TurnoPage extends PageGeneric implements Serializable{

	private static final long serialVersionUID = 1765750623995837258L;
	
	private List<Turno> data;

	public TurnoPage() {
		super();
	}

	public TurnoPage(Page<Turno> data) {
		super(data);
		this.data = data.getContent();
	}

	public List<Turno> getData() {
		return data;
	}

	public void setData(List<Turno> data) {
		this.data = data;
	}
}
