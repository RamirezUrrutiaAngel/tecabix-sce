/*
 *   This file is part of Foobar.
 *
 *   Foobar is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package mx.tecabix.db.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "correo")
@NamedQueries({
	@NamedQuery(name = "correo.findByRemitente",query = "SELECT c FROM Correo c WHERE c.remitente = ?1 AND c.estatus.nombre = 'ACTIVO' ")
})
public final class Correo implements Serializable{

	private static final long serialVersionUID = 3322361249577115212L;

	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_correo", unique = true, nullable = false)
	@SequenceGenerator(name = "correo_id_correo_gen", sequenceName = "tecabix_sce.correo_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "correo_id_correo_gen")
    private Long id;
	@Column(name="remitente")
	private String remitente;
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "psw")
    private String password;
	@Column(name="smtp_servidor")
	private String smtpServidor;
	@Column(name="smtp_port")
	private String smtpPort;
	@ManyToOne
    @JoinColumn(name = "id_tipo")
    private Catalogo tipo;
	@Column(name = "vencimiento")
	private LocalDate vencimiento;
	@Column(name="peticiones")
	private Integer peticiones;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getRemitente() {
		return remitente;
	}
	public void setRemitente(String remitente) {
		this.remitente = remitente;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSmtpServidor() {
		return smtpServidor;
	}
	public void setSmtpServidor(String smtpServidor) {
		this.smtpServidor = smtpServidor;
	}
	public String getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	public Catalogo getTipo() {
		return tipo;
	}
	public void setTipo(Catalogo tipo) {
		this.tipo = tipo;
	}
	public LocalDate getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(LocalDate vencimiento) {
		this.vencimiento = vencimiento;
	}
	public Integer getPeticiones() {
		return peticiones;
	}
	public void setPeticiones(Integer peticiones) {
		this.peticiones = peticiones;
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
		Correo other = (Correo) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
