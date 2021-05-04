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
@Entity
@Table(name = "seguro_social")
public class SeguroSocial implements Serializable{

	private static final long serialVersionUID = 5434474820126276090L;
	
	public static final short SIZE_NUMERO = 12;
	public static final short SIZE_CIUDAD = 100;
	public static final short SIZE_CURP = 19;
	public static final short SIZE_RFC= 16;
	public static final short SIZE_OBSERVACIONES_BAJA = 200;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_seguro_social", unique = true, nullable = false)
	@SequenceGenerator(name = "turno_id_turno_gen", sequenceName = "tecabix_sce.seguro_social_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "turno_id_turno_gen")
    private Long id;
	@Column(name = "numero")
	private String numero;
	@ManyToOne
    @JoinColumn(name = "id_estado")
	private Estado entidadFederativa;
	@Column(name = "ciudad")
	private String ciudad;
	@Column(name = "curp")
	private String CURP;
	@Column(name = "rfc")
	private String RFC;
	@Column(name = "alta")
	private LocalDate alta;
	@Column(name = "baja")
	private LocalDate baja;
	@Column(name = "observaciones_baja")
	private String observacionesBaja;
	@Column(name = "id_usuario_modificado")
	@JsonProperty(access = Access.WRITE_ONLY)
    private Long idUsuarioModificado;
    @Column(name = "fecha_modificado")
    @JsonProperty(access = Access.WRITE_ONLY)
    private LocalDateTime fechaDeModificacion;
    @ManyToOne
    @JoinColumn(name = "id_estatus")
    @JsonProperty(access = Access.WRITE_ONLY)
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
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Estado getEntidadFederativa() {
		return entidadFederativa;
	}
	public void setEntidadFederativa(Estado entidadFederativa) {
		this.entidadFederativa = entidadFederativa;
	}
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	public String getCURP() {
		return CURP;
	}
	public void setCURP(String curp) {
		this.CURP = curp;
	}
	public String getRFC() {
		return RFC;
	}
	public void setRfc(String rfc) {
		this.RFC = rfc;
	}
	public LocalDate getAlta() {
		return alta;
	}
	public void setAlta(LocalDate alta) {
		this.alta = alta;
	}
	public LocalDate getBaja() {
		return baja;
	}
	public void setBaja(LocalDate baja) {
		this.baja = baja;
	}
	public String getObservacionesBaja() {
		return observacionesBaja;
	}
	public void setObservacionesBaja(String observaciones_baja) {
		this.observacionesBaja = observaciones_baja;
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
		SeguroSocial other = (SeguroSocial) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
