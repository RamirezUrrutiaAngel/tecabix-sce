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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * 
 * @author Ramirez Urrutia Angel Abinad
 */
@Entity
@Table(name = "municipio")
public class Municipio implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9218053756023446113L;
	@Id
    @Column(name = "id_municipio", unique = true, nullable = false)
	@SequenceGenerator(name = "municipio_id_municipio_gen", sequenceName = "tecabix.municipio_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municipio_id_municipio_gen")
    private Integer id;
	@Column(name = "nombre")
    private String nombre;
	@JsonProperty(access = Access.WRITE_ONLY)
	@ManyToOne
    @JoinColumn(name = "id_estado")
	private Estado entidadFederativa;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Estado getEntidadFederativa() {
		return entidadFederativa;
	}
	public void setEntidadFederativa(Estado entidadFederativa) {
		this.entidadFederativa = entidadFederativa;
	}
	
	
}
