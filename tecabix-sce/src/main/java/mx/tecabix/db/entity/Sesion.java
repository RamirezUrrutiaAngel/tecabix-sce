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
@Entity()
@Table(name = "sesion")
@NamedQueries({
    @NamedQuery(name = "Sesion.findByActive",query = "SELECT s FROM Sesion s WHERE s.licencia.plantel.idEscuela = ?1 AND vencimiento > NOW() AND peticionesRestantes > 0 AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByLicenciaAndActive",query = "SELECT s FROM Sesion s WHERE s.licencia.id = ?1 AND vencimiento > NOW() AND peticionesRestantes > 0 AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByUsuarioAndActive",query = "SELECT s FROM Sesion s WHERE s.licencia.id = ?1 AND s.usuario.id = ?2 AND vencimiento > NOW() AND peticionesRestantes > 0 AND s.estatus.nombre = 'ACTIVO' "),
    @NamedQuery(name = "Sesion.findByToken",query = "SELECT s FROM Sesion s WHERE s.token = ?1 AND peticionesRestantes > 0 AND s.estatus.nombre = 'ACTIVO' AND ( s.licencia.tipo.nombre = 'WEB' OR s.vencimiento > NOW() )"),
    @NamedQuery(name = "Sesion.findByNow",query = "SELECT s FROM Sesion s WHERE s.licencia.id = ?1 AND DATE(s.vencimiento) = DATE(NOW()) ORDER BY s.peticionesRestantes"),
    @NamedQuery(name = "Sesion.findByUsuarioAndNow",query = "SELECT s FROM Sesion s WHERE s.licencia.id = ?1 AND s.usuario.id = ?2 AND DATE(s.fechaDeModificacion) = DATE(NOW()) ORDER BY s.peticionesRestantes")
})
public class Sesion implements Serializable{

	private static final long serialVersionUID = -1073408998327677969L;
	@Id
    @Column(name = "id_sesion", unique = true, nullable = false)
	@SequenceGenerator(name = "sesion_id_sesion_gen", sequenceName = "tecabix_sce.sesion_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sesion_id_sesion_gen")
    private Long id;
	@ManyToOne
    @JoinColumn(name = "id_usuario")
	private Usuario usuario;
    @Column(name = "key_token")
    private String token;
    @Column(name = "vencimiento")
    private LocalDateTime vencimiento;
    @ManyToOne
    @JoinColumn(name = "id_licencia")
    private Licencia licencia;
    @Column(name = "peticiones_restantes")
    private Integer peticionesRestantes;
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
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public LocalDateTime getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(LocalDateTime vencimiento) {
		this.vencimiento = vencimiento;
	}
	public Licencia getLicencia() {
		return licencia;
	}
	public void setLicencia(Licencia licencia) {
		this.licencia = licencia;
	}
	public Integer getPeticionesRestantes() {
		return peticionesRestantes;
	}
	public void setPeticionesRestantes(Integer peticionesRestantes) {
		this.peticionesRestantes = peticionesRestantes;
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