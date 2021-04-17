package mx.tecabix.db.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Entity()
@Table(name = "correo_msj_item")
public class CorreoMsjItem implements Serializable{

	private static final long serialVersionUID = -7319433469516856859L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_correo_msj_item", unique = true, nullable = false)
	@SequenceGenerator(name = "correo_msj_item_id_correo_msj_gen", sequenceName = "tecabix_sce.correo_msj_item_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "correo_msj_item_id_correo_msj_gen")
    private Long id;
	@ManyToOne
    @JoinColumn(name = "id_correo_msj")
    private CorreoMsj correoMsj;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo")
    private Catalogo tipo;
	@Column(name = "dato")
    private String dato;
	@Column(name="id_usuario_modificado")
	private Long idUsuarioModificado;
	@Column(name="fecha_modificado")
	private LocalDateTime fechaDeModificacion;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estatus")
    private Catalogo estatus;
	@Column(name = "clave")
    @Type(type="pg-uuid")
    private UUID clave;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CorreoMsj getCorreoMsj() {
		return correoMsj;
	}
	public void setCorreoMsj(CorreoMsj correoMsj) {
		this.correoMsj = correoMsj;
	}
	public Catalogo getTipo() {
		return tipo;
	}
	public void setTipo(Catalogo tipo) {
		this.tipo = tipo;
	}
	public String getDato() {
		return dato;
	}
	public void setDato(String dato) {
		this.dato = dato;
	}
	public Long getIdUsuarioModificado() {
		return idUsuarioModificado;
	}
	public void setIdUsuarioModificado(Long idUsuarioModificado) {
		this.idUsuarioModificado = idUsuarioModificado;
	}
	public LocalDateTime getFechaDeModificacion() {
		return fechaDeModificacion;
	}
	public void setFechaDeModificacion(LocalDateTime fechaDeModificacion) {
		this.fechaDeModificacion = fechaDeModificacion;
	}
	public Catalogo getEstatus() {
		return estatus;
	}
	public void setEstatus(Catalogo estatus) {
		this.estatus = estatus;
	}
	public UUID getClave() {
		return clave;
	}
	public void setClave(UUID clave) {
		this.clave = clave;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clave == null) ? 0 : clave.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CorreoMsjItem other = (CorreoMsjItem) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
