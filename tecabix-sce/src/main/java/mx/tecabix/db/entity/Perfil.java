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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Entity()
@Table(name = "perfil")
@NamedQueries({
	@NamedQuery(name = "Perfil.findByKey",query = "SELECT p FROM Perfil p WHERE p.id = ?1 AND p.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Perfil.findAll",query = "SELECT p FROM Perfil p WHERE p.idEscuela = ?1 AND p.estatus.nombre = 'ACTIVO' ORDER BY p.nombre"),
	@NamedQuery(name = "Perfil.findAllByNombre",query = "SELECT p FROM Perfil p WHERE p.estatus.nombre = 'ACTIVO' AND p.idEscuela = ?1 AND p.nombre LIKE ?2 ORDER BY p.nombre"),
	@NamedQuery(name = "Perfil.findByNombre",query = "SELECT p FROM Perfil p WHERE p.estatus.nombre = 'ACTIVO' AND p.idEscuela = ?1 AND p.nombre = ?2 ")

})
public class Perfil implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1945352087628007583L;
	@Id
    @Column(name = "id_perfil", unique = true, nullable = false)
	@SequenceGenerator(name = "perfil_id_perfil_gen", sequenceName = "tecabix_spv.tecabix.perfil_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perfil_id_perfil_gen")
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "id_escuela")
    private Long idEscuela;
    @Column(name = "id_usuario_modificado")
    private Long idUsuarioModificado;
    @Column(name = "fecha_modificado")
    private LocalDateTime fechaDeModificacion;
    @ManyToOne
    @JoinColumn(name = "id_estatus")
    private Catalogo estatus;
	@ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
        name = "perfil_authority", 
        joinColumns = { @JoinColumn(name = "id_perfil") }, 
        inverseJoinColumns = { @JoinColumn(name = "id_authority") }
    )
    private List<Authority> authorities;
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
	public Long getIdEscuela() {
		return idEscuela;
	}
	public void setIdEscuela(Long idEscuela) {
		this.idEscuela = idEscuela;
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
	public List<Authority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}
	
	
}
