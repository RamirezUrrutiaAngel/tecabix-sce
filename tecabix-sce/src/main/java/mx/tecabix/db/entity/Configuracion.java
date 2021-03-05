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
@Entity()
@Table(name = "configuracion")
@NamedQueries({
    @NamedQuery(name = "Configuracion.findByIdEscuela",query = "SELECT c FROM Configuracion c WHERE c.estatus.nombre = 'ACTIVO' AND c.idEscuela = ?1 "),
    @NamedQuery(name = "Configuracion.findByNombre",query = "SELECT c FROM Configuracion c WHERE c.estatus.nombre = 'ACTIVO' AND c.tipo.nombre = ?1 "),
    @NamedQuery(name = "Configuracion.findByIdEscuelaAndNombre",query = "SELECT c FROM Configuracion c WHERE c.estatus.nombre = 'ACTIVO' AND c.idEscuela = ?1 AND c.tipo.nombre = ?2 ")
})
public class Configuracion implements Serializable{

	private static final long serialVersionUID = -3454681497916100291L;
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_configuracion", unique = true, nullable = false)
	@SequenceGenerator(name = "configuracion_id_configuracion_gen", sequenceName = "tecabix_sce.configuracion_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "configuracion_id_configuracion_gen")
	private Long id;
	@Column(name = "id_escuela")
    private Long idEscuela;
	@ManyToOne
    @JoinColumn(name = "id_tipo")
    private Catalogo tipo;
    @Column(name = "valor")
    private String valor;
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
	public Long getIdEscuela() {
		return idEscuela;
	}
	public void setIdEscuela(Long idEscuela) {
		this.idEscuela = idEscuela;
	}
	public Catalogo getTipo() {
		return tipo;
	}
	public void setTipo(Catalogo tipo) {
		this.tipo = tipo;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
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
