package mx.tecabix.service.request;

import java.util.List;

import mx.tecabix.db.entity.Contacto;
import mx.tecabix.db.entity.Escuela;
import mx.tecabix.db.entity.Trabajador;

public class EmpresaRequest {

	private Escuela escuela;
	private Trabajador trabajador;
	List<Contacto> contactos;
	
	public Escuela getEscuela() {
		return escuela;
	}
	public void setEscuela(Escuela escuela) {
		this.escuela = escuela;
	}
	public Trabajador getTrabajador() {
		return trabajador;
	}
	public void setTrabajador(Trabajador trabajador) {
		this.trabajador = trabajador;
	}
	public List<Contacto> getContactos() {
		return contactos;
	}
	public void setContactos(List<Contacto> contactos) {
		this.contactos = contactos;
	}
	
	
}
