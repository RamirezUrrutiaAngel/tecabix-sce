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
@Table(name = "puesto")
@NamedQueries({
	@NamedQuery(name = "Puesto.findByLikeNombre",query = "SELECT p FROM Puesto p WHERE UPPER(p.nombre) LIKE UPPER(?1) AND p.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Puesto.findByLikeDescripcion",query = "SELECT p FROM Puesto p WHERE UPPER(p.descripcion) LIKE UPPER(?1) AND p.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Puesto.findByLikeDepartamento",query = "SELECT p FROM Puesto p WHERE UPPER(p.departamento.nombre) LIKE UPPER(?1) AND p.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Puesto.findByIdEmpresa",query = "SELECT p FROM Puesto p WHERE p.estatus.nombre = 'ACTIVO' AND p.departamento.idEmpresa = ?1 ")
})
public class Puesto implements Serializable{

	private static final long serialVersionUID = -1105824443217371322L;

	public static final short SIZE_NOMBRE = 35;
	public static final short SIZE_DESCRIPCION = 300;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_puesto", unique = true, nullable = false)
	@SequenceGenerator(name = "puesto_id_puesto_gen", sequenceName = "tecabix_sce.puesto_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "puesto_id_puesto_gen")
    private Long id;
	@Column(name = "nombre")
	private String nombre;
	@Column(name = "descripcion")
	private String descripcion;
	@ManyToOne
    @JoinColumn(name = "id_departamento")
	private Departamento departamento;
	@Column(name = "id_usuario_modificado")
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
	public Departamento getDepartamento() {
		return departamento;
	}
	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
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
}
