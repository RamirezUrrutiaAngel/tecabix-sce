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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
/**
 * 
 * @author Ramirez Urrutia Angel Abinad
 */
@Entity
@Table(name = "usuario")
@NamedQueries({
	@NamedQuery(name = "Usuario.findByPerfil",query = "SELECT u FROM Usuario u WHERE u.perfil.id = ?1"),
    @NamedQuery(name = "Usuario.findByNombre",query = "SELECT u FROM Usuario u WHERE u.nombre = ?1 AND u.estatus.nombre = 'ACTIVO'"),
    @NamedQuery(name = "Usuario.findByNameRegardlessOfStatus",query = "SELECT u FROM Usuario u WHERE upper(u.nombre) =  upper(?1)")
})
public class Usuario implements Serializable{

	private static final long serialVersionUID = 8367658930410205355L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_usuario", unique = true, nullable = false)
	@SequenceGenerator(name = "usuario_id_usuario_gen", sequenceName = "tecabix_sce.usuario_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_id_usuario_gen")
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "psw")
    private String password;
    @Column(name = "correo")
    private String correo;
    @ManyToOne
    @JoinColumn(name = "id_perfil")
    private Perfil perfil;
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
    @JsonProperty(access = Access.WRITE_ONLY)
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "usuario", cascade=CascadeType.REMOVE)
	private UsuarioPersona usuarioPersona;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public Perfil getPerfil() {
		return perfil;
	}
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
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
	public UsuarioPersona getUsuarioPersona() {
		return usuarioPersona;
	}
	public void setUsuarioPersona(UsuarioPersona usuarioPersona) {
		this.usuarioPersona = usuarioPersona;
	}
}
