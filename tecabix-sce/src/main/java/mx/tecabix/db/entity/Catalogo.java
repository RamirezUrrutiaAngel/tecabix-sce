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
@Table(name = "catalogo")
@NamedQueries({
    @NamedQuery(name = "Catalogo.findByTipoAndNombre",query = "SELECT c FROM Catalogo c WHERE c.catalogoTipo.nombre = ?1 AND c.nombre = ?2 ")
})
public class Catalogo implements Serializable{

	private static final long serialVersionUID = 8898558749708373148L;
	
	public static final short SIZE_NOMBRE = 45;
	public static final short SIZE_DESCRIPCION = 500;
	public static final short SIZE_NOMBRE_COMPLETO = 250;
	
	@Id
	@JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_catalogo", unique = true, nullable = false)
	@SequenceGenerator(name = "catalogo_id_catalogo_gen", sequenceName = "tecabix_sce.catalogo_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalogo_id_catalogo_gen")
	private Integer id;
    @Column(name = "nombre")
    private String nombre;
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "nombre_completo")
    private String nombreCompleto;
    @Column(name = "descripcion")
    private String descripcion;
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "orden")
    private Integer orden;
    @JsonProperty(access = Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "id_catalogo_tipo")
    private CatalogoTipo catalogoTipo;
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "id_usuario_modificado")
    private Long idUsuarioModificado;
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "fecha_modificado")
    private LocalDateTime fechaDeModificacion;
    @ManyToOne
    @JoinColumn(name = "id_estatus")
    @JsonProperty(access = Access.WRITE_ONLY)
    private Catalogo estatus;
    @Column(name = "clave")
    @Type(type="pg-uuid")
    private UUID clave;
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
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Integer getOrden() {
		return orden;
	}
	public void setOrden(Integer orden) {
		this.orden = orden;
	}
	public CatalogoTipo getCatalogoTipo() {
		return catalogoTipo;
	}
	public void setCatalogoTipo(CatalogoTipo catalogoTipo) {
		this.catalogoTipo = catalogoTipo;
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
