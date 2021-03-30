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
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Entity()
@Table(name = "servicio")
@NamedQueries({
	@NamedQuery(name = "Servicio.findByNombre", query = "SELECT s FROM Servicio s WHERE s.nombre = ?1 ")
})
public final class Servicio  implements Serializable{
	
	private static final long serialVersionUID = -34582993453527056L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_servicio", unique = true, nullable = false)
	@SequenceGenerator(name = "servicio_id_servicio_gen", sequenceName = "tecabix_sce.servicio_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "servicio_id_servicio_gen")
    private Integer id;
	@ManyToOne
    @JoinColumn(name = "id_plan")
	private Plan plan;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @ManyToOne
    @JoinColumn(name = "id_tipo")
    private Catalogo tipo;
    @Column(name = "peticiones")
    private Integer peticiones;
    @Column(name ="numero_licencias")
	private Integer numeroLicencias;
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
	public Plan getPlan() {
		return plan;
	}
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Catalogo getTipo() {
		return tipo;
	}
	public void setTipo(Catalogo tipo) {
		this.tipo = tipo;
	}
	public Integer getPeticiones() {
		return peticiones;
	}
	public void setPeticiones(Integer peticiones) {
		this.peticiones = peticiones;
	}
	public Integer getNumeroLicencias() {
		return numeroLicencias;
	}
	public void setNumeroLicencias(Integer numeroLicencias) {
		this.numeroLicencias = numeroLicencias;
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
		Servicio other = (Servicio) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
