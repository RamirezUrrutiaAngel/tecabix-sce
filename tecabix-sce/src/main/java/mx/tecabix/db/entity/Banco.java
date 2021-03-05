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
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "banco")
public class Banco implements Serializable{

	private static final long serialVersionUID = -7280453670571498466L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_banco", unique = true, nullable = false)
	@SequenceGenerator(name = "banco_id_banco_gen", sequenceName = "tecabix_sce.banco_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "banco_id_banco_gen")
	private Integer id;
    @Column(name = "clave_banco")
    private String claveBanco;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "razon_social")
    private String razonSocial;
    @Column(name = "clave")
    @Type(type="pg-uuid")
    private UUID clave;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getClaveBanco() {
		return claveBanco;
	}
	public void setClaveBanco(String claveBanco) {
		this.claveBanco = claveBanco;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public UUID getClave() {
		return clave;
	}
	public void setClave(UUID clave) {
		this.clave = clave;
	}
}
