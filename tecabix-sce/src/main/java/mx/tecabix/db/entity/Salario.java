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
@Entity
@Table(name = "salario")
public class Salario implements Serializable{

	private static final long serialVersionUID = -3921169535438059734L;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_salario", unique = true, nullable = false)
	@SequenceGenerator(name = "salario_id_salario_gen", sequenceName = "tecabix_sce.salario_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salario_id_salario_gen")
    private Long id;
	@Column(name = "periodo")
	private Integer periodo;
	@Column(name = "dia")
	private Integer dia;
	@Column(name = "hora")
	private Integer hora;
	@Column(name = "hora_x_dia")
	private Integer horaPorDia;
	@Column(name = "dia_x_periodo")
	private Integer diaPorPeriodo;
	@ManyToOne
    @JoinColumn(name = "id_tipo_periodo")
    private Catalogo tipoPeriodo;
	@ManyToOne
    @JoinColumn(name = "id_tipo_pago")
    private Catalogo tipoPago;
	@ManyToOne
    @JoinColumn(name = "id_banco")
    private Banco banco;
	@Column(name = "numero_cuenta")
	private String numeroCuenta;
	@Column(name = "sucursal")
	private String sucursal;
	@Column(name = "clave_interbancaria")
	private String claveInterbancaria;
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
	public Integer getPeriodo() {
		return periodo;
	}
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}
	public Integer getDia() {
		return dia;
	}
	public void setDia(Integer dia) {
		this.dia = dia;
	}
	public Integer getHora() {
		return hora;
	}
	public void setHora(Integer hora) {
		this.hora = hora;
	}
	public Integer getHoraPorDia() {
		return horaPorDia;
	}
	public void setHoraPorDia(Integer horaPorDia) {
		this.horaPorDia = horaPorDia;
	}
	public Integer getDiaPorPeriodo() {
		return diaPorPeriodo;
	}
	public void setDiaPorPeriodo(Integer diaPorPeriodo) {
		this.diaPorPeriodo = diaPorPeriodo;
	}
	public Catalogo getTipoPeriodo() {
		return tipoPeriodo;
	}
	public void setTipoPeriodo(Catalogo tipoPeriodo) {
		this.tipoPeriodo = tipoPeriodo;
	}
	public Catalogo getTipoPago() {
		return tipoPago;
	}
	public void setTipoPago(Catalogo tipoPago) {
		this.tipoPago = tipoPago;
	}
	public Banco getBanco() {
		return banco;
	}
	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	public String getNumeroCuenta() {
		return numeroCuenta;
	}
	public void setNumeroCuenta(String numeroCuenta) {
		this.numeroCuenta = numeroCuenta;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public String getClaveInterbancaria() {
		return claveInterbancaria;
	}
	public void setClaveInterbancaria(String claveInterbancaria) {
		this.claveInterbancaria = claveInterbancaria;
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
		Salario other = (Salario) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
