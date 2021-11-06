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
@Table(name = "caja_registro_transaccion_item")
public final class CajaRegistroTransaccionItem implements Serializable{

	private static final long serialVersionUID = -1426920736756690562L;
	
	public static final short SIZE_DESCRIPCION = 20;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "id_caja_registro_transaccion_item", unique = true, nullable = false)
	@SequenceGenerator(name = "caja_registro_transaccion_id_caja_registro_transaccion_item_gen", sequenceName = "tecabix_sce.caja_registro_transaccion_item_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "caja_registro_transaccion_id_caja_registro_transaccion_item_gen")
	private Long id;
	@ManyToOne
	@JsonProperty(access = Access.WRITE_ONLY)
	@JoinColumn(name = "id_caja_registro_transaccion")
	private CajaRegistroTransaccion cajaRegistroTransaccion;
	@Column(name = "descripcion")
	private String descripcion;
	@Column(name = "cantidad")
	private Integer cantidad;
	@ManyToOne
	@JoinColumn(name = "id_unidad")
	private Catalogo unidad;
	@Column(name = "precio_unitario")
	private Integer precioUnitario;
	@Column(name = "precio_grupal")
	private Integer precioGrupal;
	@ManyToOne
	@JoinColumn(name = "id_entidad")
	private Catalogo entidad;
	@Column(name = "identificador")
	@Type(type = "pg-uuid")
	private UUID identificador;
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
	@Type(type = "pg-uuid")
	private UUID clave;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CajaRegistroTransaccion getCajaRegistroTransaccion() {
		return cajaRegistroTransaccion;
	}
	public void setCajaRegistroTransaccion(CajaRegistroTransaccion cajaRegistroTransaccion) {
		this.cajaRegistroTransaccion = cajaRegistroTransaccion;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Catalogo getUnidad() {
		return unidad;
	}
	public void setUnidad(Catalogo unidad) {
		this.unidad = unidad;
	}
	public Integer getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(Integer precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public Integer getPrecioGrupal() {
		return precioGrupal;
	}
	public void setPrecioGrupal(Integer precioGrupal) {
		this.precioGrupal = precioGrupal;
	}
	public Catalogo getEntidad() {
		return entidad;
	}
	public void setEntidad(Catalogo entidad) {
		this.entidad = entidad;
	}
	public UUID getIdentificador() {
		return identificador;
	}
	public void setIdentificador(UUID identificador) {
		this.identificador = identificador;
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
		CajaRegistroTransaccionItem other = (CajaRegistroTransaccionItem) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
