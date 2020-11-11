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

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Entity
@Table(name = "trabajador")
@NamedQueries({
	@NamedQuery(name = "Trabajador.findByKey",query = "SELECT t FROM Trabajador t WHERE t.id = ?1 AND t.estatus.nombre = 'ACTIVO' "),
	@NamedQuery(name = "Trabajador.findByKeyAndPendiente",query = "SELECT t FROM Trabajador t WHERE t.id = ?1 AND t.estatus.nombre = 'PENDIENTE' "),
	
	@NamedQuery(name = "Trabajador.findAll",query = "SELECT t FROM Trabajador t WHERE t.idEscuela = ?1 AND t.estatus.nombre = 'ACTIVO' ORDER BY t.personaFisica.apellidoPaterno"),
	@NamedQuery(name = "Trabajador.findAllByNombre",query = "SELECT t FROM Trabajador t WHERE t.idEscuela = ?1 AND t.estatus.nombre = 'ACTIVO' AND t.personaFisica.nombre LIKE ?2 ORDER BY t.personaFisica.nombre"),

	@NamedQuery(name = "Trabajador.findByUsuario",query = "SELECT t FROM Trabajador t WHERE t.personaFisica.presona.usuarioPersona.usuario.nombre = ?1 AND t.estatus.nombre = 'ACTIVO' ")

})
public class Trabajador implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7407043110565368553L;
	@Id
    @Column(name = "id_trabajador", unique = true, nullable = false)
	@SequenceGenerator(name = "trabajador_id_trabajador_gen", sequenceName = "tecabix_spv.trabajador_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trabajador_id_trabajador_gen")
    private Long id;
	@Column(name = "curp")
	private String CURP;
	@ManyToOne
    @JoinColumn(name = "id_persona_fisica")
	private PersonaFisica personaFisica;
	@ManyToOne
    @JoinColumn(name = "id_puesto")
	private Puesto puesto;
	@ManyToOne
    @JoinColumn(name = "id_jefe")
	private Trabajador jefe;
	@Column(name = "url_imagen")
	private String urlImagen;
	@Column(name = "id_escuela")
	private Long idEscuela;
	@Column(name = "id_usuario_modificado")
    private Long idUsuarioModificado;
    @Column(name = "fecha_modificado")
    private LocalDateTime fechaDeModificacion;
    @ManyToOne
    @JoinColumn(name = "id_estatus")
    private Catalogo estatus;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCURP() {
		return CURP;
	}
	public void setCURP(String cURP) {
		CURP = cURP;
	}
	public PersonaFisica getPersonaFisica() {
		return personaFisica;
	}
	public void setPersonaFisica(PersonaFisica personaFisica) {
		this.personaFisica = personaFisica;
	}
	public Puesto getPuesto() {
		return puesto;
	}
	public void setPuesto(Puesto puesto) {
		this.puesto = puesto;
	}
	public Trabajador getJefe() {
		return jefe;
	}
	public void setJefe(Trabajador jefe) {
		this.jefe = jefe;
	}
	public String getUrlImagen() {
		return urlImagen;
	}
	public void setUrlImagen(String urlImagen) {
		this.urlImagen = urlImagen;
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
    
}
