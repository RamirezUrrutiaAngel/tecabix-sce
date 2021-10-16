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
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
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
@Entity
@Table(name = "trabajador")
@NamedQueries({
	@NamedQuery(name = "Trabajador.findBoss",query = "SELECT t.jefe FROM Trabajador t WHERE t.clave = ?1 AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByJefe",query = "SELECT t FROM Trabajador t WHERE t.idEmpresa = ?1 AND t.jefe.id = ?2 AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByClaveUsuario",query = "SELECT t FROM Trabajador t WHERE t.personaFisica.persona.usuarioPersona.usuario.clave = ?1 AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByLikePuesto",query = "SELECT t FROM Trabajador t WHERE t.idEmpresa = ?1 AND UPPER(t.puesto.nombre) LIKE UPPER(?2) AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByLikePlantel",query = "SELECT t FROM Trabajador t WHERE t.idEmpresa = ?1 AND UPPER(t.plantel.nombre) LIKE UPPER(?2) AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByLikeNombre",query = "SELECT t FROM Trabajador t WHERE t.idEmpresa = ?1 AND UPPER(t.personaFisica.nombre) LIKE UPPER(?2) AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByLikeApellidoPaterno",query = "SELECT t FROM Trabajador t WHERE t.idEmpresa = ?1 AND UPPER(t.personaFisica.apellidoPaterno) LIKE UPPER(?2) AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByLikeApellidoMaterno",query = "SELECT t FROM Trabajador t WHERE t.idEmpresa = ?1 AND UPPER(t.personaFisica.apellidoMaterno) LIKE UPPER(?2) AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByLikeCURP",query = "SELECT t FROM Trabajador t WHERE t.idEmpresa = ?1 AND UPPER(t.seguroSocial.CURP) LIKE UPPER(?2) AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByIdEmpresa",query = "SELECT t FROM Trabajador t WHERE t.idEmpresa = ?1 AND t.estatus.nombre = 'ACTIVO' ")

})
@NamedNativeQueries({
	@NamedNativeQuery(name = "Trabajador.canInsert", query = "SELECT tecabix_sce.trabajador_can_insert(?1)")
})
public final class Trabajador implements Serializable{

	private static final long serialVersionUID = -7407043110565368553L;

	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_trabajador", unique = true, nullable = false)
	@SequenceGenerator(name = "trabajador_id_trabajador_gen", sequenceName = "tecabix_sce.trabajador_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trabajador_id_trabajador_gen")
    private Long id;
	@ManyToOne
    @JoinColumn(name = "id_persona_fisica")
	private PersonaFisica personaFisica;
	@ManyToOne
    @JoinColumn(name = "id_seguro_social")
	private SeguroSocial seguroSocial;
	@ManyToOne
    @JoinColumn(name = "id_puesto")
	private Puesto puesto;
	@ManyToOne
    @JoinColumn(name = "id_plantel")
	private Plantel plantel;
	@ManyToOne
    @JoinColumn(name = "id_turno")
	private Turno turno;
	@ManyToOne
    @JoinColumn(name = "id_salario")
	private Salario salario;
	@ManyToOne
    @JoinColumn(name = "id_jefe")
	@JsonProperty(access = Access.WRITE_ONLY)
	private Trabajador jefe;
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name = "id_empresa")
	private Long idEmpresa;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public PersonaFisica getPersonaFisica() {
		return personaFisica;
	}
	public void setPersonaFisica(PersonaFisica personaFisica) {
		this.personaFisica = personaFisica;
	}
	public SeguroSocial getSeguroSocial() {
		return seguroSocial;
	}
	public void setSeguroSocial(SeguroSocial seguroSocial) {
		this.seguroSocial = seguroSocial;
	}
	public Puesto getPuesto() {
		return puesto;
	}
	public void setPuesto(Puesto puesto) {
		this.puesto = puesto;
	}
	public Plantel getPlantel() {
		return plantel;
	}
	public void setPlantel(Plantel plantel) {
		this.plantel = plantel;
	}
	public Turno getTurno() {
		return turno;
	}
	public void setTurno(Turno turno) {
		this.turno = turno;
	}
	public Salario getSalario() {
		return salario;
	}
	public void setSalario(Salario salario) {
		this.salario = salario;
	}
	public Trabajador getJefe() {
		return jefe;
	}
	public void setJefe(Trabajador jefe) {
		this.jefe = jefe;
	}
	public Long getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
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
		Trabajador other = (Trabajador) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}
