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
@Table(name = "caja_registradora")
@NamedQueries({
	@NamedQuery(name = "CajaRegistradora.findByIdEmpresa",query = "SELECT c FROM CajaRegistradora c WHERE c.plantel.idEmpresa = ?1 AND c.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "CajaRegistradora.findByIdLicencia",query = "SELECT c FROM CajaRegistradora c WHERE c.licencia.id = ?1 AND c.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "CajaRegistradora.findLikeNombre",query = "SELECT c FROM CajaRegistradora c WHERE c.plantel.idEmpresa = ?1 AND UPPER(c.nombre) LIKE UPPER(?2) AND c.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "CajaRegistradora.findLikeDescripcion",query = "SELECT c FROM CajaRegistradora c WHERE c.plantel.idEmpresa = ?1 AND UPPER(c.descripcion) LIKE UPPER(?2) AND c.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "CajaRegistradora.findLikeMarca",query = "SELECT c FROM CajaRegistradora c WHERE c.plantel.idEmpresa = ?1 AND UPPER(c.marca) LIKE UPPER(?2) AND c.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "CajaRegistradora.findLikeModelo",query = "SELECT c FROM CajaRegistradora c WHERE c.plantel.idEmpresa = ?1 AND UPPER(c.modelo) LIKE UPPER(?2) AND c.estatus.nombre = 'ACTIVO' ")
})
public final class CajaRegistradora implements Serializable{

	private static final long serialVersionUID = 2054851860569450098L;
	public static final short SIZE_NOMBRE = 50;
	public static final short SIZE_DESCRIPCION = 300;
	public static final short SIZE_MARCA = 50;
	public static final short SIZE_MODELO = 50;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_caja_registradora", unique = true, nullable = false)
	@SequenceGenerator(name = "caja_registradora_id_caja_registradora_gen", sequenceName = "tecabix_sce.caja_registradora_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "caja_registradora_id_caja_registradora_gen")
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "marca")
    private String marca;
    @Column(name = "modelo")
    private String modelo;
    @ManyToOne
    @JoinColumn(name = "id_licencia")
	private Licencia licencia;
    @ManyToOne
    @JoinColumn(name = "id_plantel")
	private Plantel plantel;
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
    @OneToMany(fetch = FetchType.LAZY, mappedBy="cajaRegistradora", cascade=CascadeType.REMOVE)
    private List<CajaRegistro> registros;
    
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public String getModelo() {
		return modelo;
	}
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	public Licencia getLicencia() {
		return licencia;
	}
	public void setLicencia(Licencia licencia) {
		this.licencia = licencia;
	}
	public Plantel getPlantel() {
		return plantel;
	}
	public void setPlantel(Plantel plantel) {
		this.plantel = plantel;
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
	public List<CajaRegistro> getRegistros() {
		return registros;
	}
	public void setRegistros(List<CajaRegistro> registros) {
		this.registros = registros;
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
		CajaRegistradora other = (CajaRegistradora) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
