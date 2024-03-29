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
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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
@Table(name = "producto_departamento")
public final class ProductoDepartamento implements Serializable{

	private static final long serialVersionUID = -4699275654323360668L;
	
	public static final short SIZE_NOMBRE = 40;
	public static final short SIZE_DESCRIPCION = 250;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_producto_departamento", unique = true, nullable = false)
	@SequenceGenerator(name = "producto_departamento_id_producto_departamento_gen", sequenceName = "tecabix_sce.producto_departamento_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_departamento_id_producto_departamento_gen")
	private Long id;
	@JsonProperty(access = Access.WRITE_ONLY)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_pre_producto_departamento")
	private ProductoDepartamento preProductoDepartamento;
	@Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "id_usuario_modificado")
    private Long idUsuarioModificado;
    @Column(name = "fecha_modificado")
    private LocalDateTime fechaDeModificacion;
    @ManyToOne
    @JoinColumn(name = "id_estatus")
    private Catalogo estatus;
    @Column(name = "clave")
    @Type(type="pg-uuid")
    private UUID clave;
    @JsonProperty(access = Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "preProductoDepartamento", cascade = CascadeType.REMOVE)
	private List<ProductoDepartamento> subProductoDepartamentos;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ProductoDepartamento getPreProductoDepartamento() {
		return preProductoDepartamento;
	}
	public void setPreProductoDepartamento(ProductoDepartamento preProductoDepartamento) {
		this.preProductoDepartamento = preProductoDepartamento;
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
	public List<ProductoDepartamento> getSubProductoDepartamentos() {
		return subProductoDepartamentos;
	}
	public void setSubProductoDepartamentos(List<ProductoDepartamento> subProductoDepartamentos) {
		this.subProductoDepartamentos = subProductoDepartamentos;
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
		ProductoDepartamento other = (ProductoDepartamento) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
