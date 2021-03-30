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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 */
@Entity
@Table(name = "persona_moral")
@NamedQueries({
    @NamedQuery(name = "Empresa.findByNameRegardlessOfStatus",query = "SELECT e FROM Empresa e WHERE upper(e.nombre) =  upper(?1)")
})
public final class Empresa implements Serializable {

	private static final long serialVersionUID = 4047413230691680424L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_persona_moral", unique = true, nullable = false)
	@SequenceGenerator(name = "persona_moral_id_persona_moral_gen", sequenceName = "tecabix_sce.persona_moral_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persona_moral_id_persona_moral_gen")
    private Long id;
	@OneToOne
    @JoinColumn(name = "id_persona")
    private Persona presona;
	@Column(name = "razon_social")
	private String nombre;
	@Column(name= "rfc")
	private String rfc;
	@Column(name="fundada")
	private LocalDate fundada;
	@OneToOne
	@JoinColumn(name="id_direccion")
	private Direccion direccion;
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
	public Persona getPresona() {
		return presona;
	}
	public void setPresona(Persona presona) {
		this.presona = presona;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getRfc() {
		return rfc;
	}
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}
	public LocalDate getFundada() {
		return fundada;
	}
	public void setFundada(LocalDate fundada) {
		this.fundada = fundada;
	}
	public Direccion getDireccion() {
		return direccion;
	}
	public void setDireccion(Direccion direccion) {
		this.direccion = direccion;
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
		Empresa other = (Empresa) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
