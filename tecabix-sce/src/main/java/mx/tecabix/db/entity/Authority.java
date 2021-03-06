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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "authority")
@NamedQueries({
		@NamedQuery(name = "Authority.findByLikeNombre",query = "SELECT a FROM Authority a WHERE a.nombre LIKE ?1 "),
		@NamedQuery(name = "Authority.findByNombre",query = "SELECT a FROM Authority a WHERE a.nombre = ?1 ")
})
public class Authority implements Serializable{

	private static final long serialVersionUID = 4643106103106362573L;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_authority", unique = true, nullable = false)
	@SequenceGenerator(name = "authority_id_authority_gen", sequenceName = "tecabix_sce.authority_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authority_id_authority_gen")
    private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "descripcion")
    private String descripcion;
    @JsonProperty(access = Access.WRITE_ONLY)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "id_pre_authority")
    private Authority preAuthority;
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_usuario_modificado")
    private Long idUsuarioModificado;
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "fecha_modificado")
    private LocalDateTime fechaDeModificacion;
    @ManyToOne
    @JoinColumn(name = "id_estatus")
    private Catalogo estatus;
    @Column(name = "clave")
    @Type(type="pg-uuid")
    private UUID clave;
    @OneToMany(fetch = FetchType.LAZY, mappedBy="preAuthority", cascade=CascadeType.REMOVE)
    private List<Authority> subAuthority;
    @JsonProperty(access = Access.WRITE_ONLY)
    @ManyToMany(mappedBy = "authorities", cascade = CascadeType.REMOVE)
	private List<Perfil> perfiles;
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
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Authority getPreAuthority() {
		return preAuthority;
	}
	public void setPreAuthority(Authority preAuthority) {
		this.preAuthority = preAuthority;
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
	public List<Authority> getSubAuthority() {
		return subAuthority;
	}
	public void setSubAuthority(List<Authority> subAuthority) {
		this.subAuthority = subAuthority;
	}
	public List<Perfil> getPerfiles() {
		return perfiles;
	}
	public void setPerfiles(List<Perfil> perfiles) {
		this.perfiles = perfiles;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Authority other = (Authority) obj;
		if (this.id != null && other.id != null) {
			if(this.id.equals(other.id)) {
				if(this.clave != null && other.clave != null) {
					if(this.clave.equals(this.clave)) {
						return true;
					}
				}else {
					return true;
				}
			}
		}else if(this.clave != null && other.clave != null) {
			if(this.clave.equals(other.clave)) {
				return true;
			}
		}
		return false;
	}
}
