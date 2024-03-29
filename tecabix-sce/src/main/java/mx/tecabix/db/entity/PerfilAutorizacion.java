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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Entity()
@Table(name = "perfil_autorizacion")
@NamedQueries({
	@NamedQuery(name = "PerfilAutorizacion.findByPerfil",query = "SELECT p FROM PerfilAutorizacion p WHERE p.perfil.id = ?1 "),
	@NamedQuery(name = "PerfilAutorizacion.findByAutorizacion",query = "SELECT p FROM PerfilAutorizacion p WHERE p.autorizacion.id = ?1 ")
})
public final class PerfilAutorizacion implements Serializable{

	private static final long serialVersionUID = -4911315157724285906L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_perfil_autorizacion", unique = true, nullable = false)
	@SequenceGenerator(name = "perfil_autorizacion_id_perfil_autorizacion_gen", sequenceName = "tecabix_sce.perfil_autorizacion_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perfil_autorizacion_id_perfil_autorizacion_gen")
    private Long id;
	@ManyToOne
    @JoinColumn(name = "id_perfil")
	private Perfil perfil;
	@ManyToOne
    @JoinColumn(name = "id_autorizacion")
	private Autorizacion autorizacion;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Perfil getPerfil() {
		return perfil;
	}
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	public Autorizacion getAutorizacion() {
		return autorizacion;
	}
	public void setAutorizacion(Autorizacion autorizacion) {
		this.autorizacion = autorizacion;
	}
}
