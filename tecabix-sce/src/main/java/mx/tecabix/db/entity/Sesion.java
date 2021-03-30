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
@Entity()
@Table(name = "sesion")
@NamedQueries({
    @NamedQuery(name = "Sesion.findByActive",query = "SELECT s FROM Sesion s WHERE s.licencia.plantel.idEmpresa = ?1 AND vencimiento > NOW() AND peticionesRestantes > 0 AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByActiveAndLikeUsuario",query = "SELECT s FROM Sesion s WHERE s.licencia.plantel.idEmpresa = ?1 AND vencimiento > NOW() AND peticionesRestantes > 0 AND UPPER(s.usuario.nombre) LIKE UPPER(?2) AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByActiveAndLikeLicencia",query = "SELECT s FROM Sesion s WHERE s.licencia.plantel.idEmpresa = ?1 AND vencimiento > NOW() AND peticionesRestantes > 0 AND UPPER(s.licencia.nombre) LIKE UPPER(?2) AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByActiveAndLikeServicio",query = "SELECT s FROM Sesion s WHERE s.licencia.plantel.idEmpresa = ?1 AND vencimiento > NOW() AND peticionesRestantes > 0 AND UPPER(s.licencia.servicio.nombre) LIKE UPPER(?2) AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByLicenciaAndActive",query = "SELECT s FROM Sesion s WHERE s.licencia.id = ?1 AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByUsuarioAndActive",query = "SELECT s FROM Sesion s WHERE s.licencia.id = ?1 AND s.usuario.id = ?2 AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByNow",query = "SELECT s FROM Sesion s WHERE s.licencia.id = ?1 AND DATE(s.vencimiento) = DATE(NOW()) ORDER BY s.peticionesRestantes"),
    @NamedQuery(name = "Sesion.findByUsuarioAndNow",query = "SELECT s FROM Sesion s WHERE s.licencia.id = ?1 AND s.usuario.id = ?2 AND DATE(s.fechaDeModificacion) = DATE(NOW()) ORDER BY s.peticionesRestantes")
})
@NamedNativeQueries({
	@NamedNativeQuery(name = "Sesion.findByToken", query = "SELECT * FROM tecabix_sce.sesion_find_by_token(?1)", resultClass = Sesion.class)
})
public final class Sesion implements Serializable{

	private static final long serialVersionUID = -1073408998327677969L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_sesion", unique = true, nullable = false)
	@SequenceGenerator(name = "sesion_id_sesion_gen", sequenceName = "tecabix_sce.sesion_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sesion_id_sesion_gen")
    private Long id;
	@ManyToOne
    @JoinColumn(name = "id_usuario")
	private Usuario usuario;
    @Column(name = "vencimiento")
    private LocalDateTime vencimiento;
    @ManyToOne
    @JoinColumn(name = "id_licencia")
    private Licencia licencia;
    @Column(name = "peticiones_restantes")
    private Integer peticionesRestantes;
    @Column(name = "id_usuario_modificado")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Long idUsuarioModificado;
    @Column(name = "fecha_modificado")
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
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public LocalDateTime getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(LocalDateTime vencimiento) {
		this.vencimiento = vencimiento;
	}
	public Licencia getLicencia() {
		return licencia;
	}
	public void setLicencia(Licencia licencia) {
		this.licencia = licencia;
	}
	public Integer getPeticionesRestantes() {
		return peticionesRestantes;
	}
	public void setPeticionesRestantes(Integer peticionesRestantes) {
		this.peticionesRestantes = peticionesRestantes;
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
		Sesion other = (Sesion) obj;
		if (clave == null) {
			if (other.clave != null)
				return false;
		} else if (!clave.equals(other.clave))
			return false;
		return true;
	}
}