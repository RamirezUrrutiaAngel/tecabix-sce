package mx.tecabix.db.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "correo_msj")
@NamedQueries({
	@NamedQuery(name = "CorreoMsj.findLast",query = "SELECT c FROM CorreoMsj c WHERE c.programado < NOW() AND c.estatus.nombre = 'ACTIVO' ")
})
public final class CorreoMsj implements Serializable{

	private static final long serialVersionUID = -2393758369081519039L;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_correo_msj", unique = true, nullable = false)
	@SequenceGenerator(name = "correo_msj_id_correo_msj_gen", sequenceName = "tecabix_sce.correo_msj_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "correo_msj_id_correo_msj_gen")
    private Long id;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_correo")
    private Correo correo;
	@Column(name = "destinatario")
    private String destinatario;
	@Column(name = "asunto")
    private String asunto;
	@Column(name = "mensaje")
    private String mensaje;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo")
    private Catalogo tipo;
	@Column(name="programado")
	private LocalDateTime programado;
	@Column(name="id_usuario_modificado")
	private Long idUsuarioModificado;
	@Column(name="fecha_modificado")
	private LocalDateTime fechaDeModificacion;
	@ManyToOne
    @JoinColumn(name = "id_estatus")
    private Catalogo estatus;
	@Column(name = "clave")
    @Type(type="pg-uuid")
    private UUID clave;
	@JsonProperty(access = Access.WRITE_ONLY)
    @OneToMany( mappedBy="correoMsj", cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<CorreoMsjItem> correoMsjItems;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Correo getCorreo() {
		return correo;
	}
	public void setCorreo(Correo correo) {
		this.correo = correo;
	}
	public String getDestinatario() {
		return destinatario;
	}
	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public Catalogo getTipo() {
		return tipo;
	}
	public void setTipo(Catalogo tipo) {
		this.tipo = tipo;
	}
	public LocalDateTime getProgramado() {
		return programado;
	}
	public void setProgramado(LocalDateTime programado) {
		this.programado = programado;
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
	public List<CorreoMsjItem> getCorreoMsjItems() {
		return correoMsjItems;
	}
	public void setCorreoMsjItems(List<CorreoMsjItem> correoMsjItems) {
		this.correoMsjItems = correoMsjItems;
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
		CorreoMsj other = (CorreoMsj) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
