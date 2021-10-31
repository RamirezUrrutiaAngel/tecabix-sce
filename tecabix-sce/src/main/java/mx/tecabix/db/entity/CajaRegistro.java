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
@Table(name = "caja_registro")
public final class CajaRegistro implements Serializable{

	private static final long serialVersionUID = 7650984265089275683L;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "id_caja_registro", unique = true, nullable = false)
	@SequenceGenerator(name = "caja_registro_id_caja_registro_gen", sequenceName = "tecabix_sce.caja_registro_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "caja_registro_id_caja_registro_gen")
	private Long id;
	@ManyToOne
	@JsonProperty(access = Access.WRITE_ONLY)
	@JoinColumn(name = "id_caja_registradora")
	private CajaRegistradora cajaRegistradora;
	@Column(name = "saldo_inicial")
	private Integer saldoInicial;
	@Column(name = "saldo")
	private Integer saldo;
	@Column(name = "saldo_final")
	private Integer saldoFinal;
	@Column(name = "id_usuario_corte")
	private Long idUsuarioCorte;
	@Column(name = "fecha_corte")
	@JsonProperty(access = Access.WRITE_ONLY)
	private LocalDateTime fechaDeCorte;
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
	@OneToMany(fetch = FetchType.LAZY, mappedBy="cajaRegistro", cascade=CascadeType.REMOVE)
    private List<CajaRegistroTransaccion> transacciones;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public CajaRegistradora getCajaRegistradora() {
		return cajaRegistradora;
	}
	public void setCajaRegistradora(CajaRegistradora cajaRegistradora) {
		this.cajaRegistradora = cajaRegistradora;
	}
	public Integer getSaldoInicial() {
		return saldoInicial;
	}
	public void setSaldoInicial(Integer saldoInicial) {
		this.saldoInicial = saldoInicial;
	}
	public Integer getSaldo() {
		return saldo;
	}
	public void setSaldo(Integer saldo) {
		this.saldo = saldo;
	}
	public Integer getSaldoFinal() {
		return saldoFinal;
	}
	public void setSaldoFinal(Integer saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	public Long getIdUsuarioCorte() {
		return idUsuarioCorte;
	}
	public void setIdUsuarioCorte(Long idUsuarioCorte) {
		this.idUsuarioCorte = idUsuarioCorte;
	}
	public LocalDateTime getFechaDeCorte() {
		return fechaDeCorte;
	}
	public void setFechaDeCorte(LocalDateTime fechaDeCorte) {
		this.fechaDeCorte = fechaDeCorte;
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
	public List<CajaRegistroTransaccion> getTransacciones() {
		return transacciones;
	}
	public void setTransacciones(List<CajaRegistroTransaccion> transacciones) {
		this.transacciones = transacciones;
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
		CajaRegistro other = (CajaRegistro) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
