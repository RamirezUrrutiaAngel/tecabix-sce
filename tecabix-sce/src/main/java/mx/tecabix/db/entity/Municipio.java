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
 * @author Ramirez Urrutia Angel Abinad
 */
@Entity
@Table(name = "municipio")
@NamedQueries({
	@NamedQuery(name = "Municipio.findByLikeNombre",query = "SELECT m FROM Municipio m WHERE UPPER(m.nombre) LIKE UPPER(?1) AND m.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Municipio.findByEstadoClave",query = "SELECT m FROM Municipio m WHERE m.entidadFederativa.clave = ?1 AND m.entidadFederativa.estatus.nombre = 'ACTIVO' AND m.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Municipio.findByActivo",query = "SELECT m FROM Municipio m WHERE m.estatus.nombre = 'ACTIVO' ")
})
public final class Municipio implements Serializable {

	private static final long serialVersionUID = -9218053756023446113L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_municipio", unique = true, nullable = false)
	@SequenceGenerator(name = "municipio_id_municipio_gen", sequenceName = "tecabix_sce.municipio_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municipio_id_municipio_gen")
    private Integer id;
	@Column(name = "nombre")
    private String nombre;
	@ManyToOne
    @JoinColumn(name = "id_estado")
	private Estado entidadFederativa;
    @Column(name = "id_usuario_modificado")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Long idUsuarioModificado;
    @Column(name = "fecha_modificado")
    @JsonProperty(access = Access.WRITE_ONLY)
    private LocalDateTime fechaDeModificacion;
    @ManyToOne
    @JoinColumn(name = "id_estatus")
    private Catalogo estatus;
    @Column(name = "clave")
    @Type(type="pg-uuid")
    private UUID clave;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Estado getEntidadFederativa() {
		return entidadFederativa;
	}
	public void setEntidadFederativa(Estado entidadFederativa) {
		this.entidadFederativa = entidadFederativa;
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
		Municipio other = (Municipio) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
