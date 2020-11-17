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
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Entity()
@Table(name = "plan_servicio")
public class PlanServicio implements Serializable{

	private static final long serialVersionUID = -6577049550950356830L;
	@Id
    @Column(name = "id_plan_servicio", unique = true, nullable = false)
	@SequenceGenerator(name = "plan_servicio_id_plan_servicio_gen", sequenceName = "tecabix_sce.plan_servicio_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plan_servicio_id_plan_servicio_gen")
    private Integer id;
	@ManyToOne
    @JoinColumn(name = "id_plan")
	private Plan plan;
	@ManyToOne
    @JoinColumn(name = "id_servicio")
	private Servicio servicio;
	@Column(name ="numero_licencias")
	private Integer numeroLicencias;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Plan getPlan() {
		return plan;
	}
	public void setPlan(Plan plan) {
		this.plan = plan;
	}
	public Servicio getServicio() {
		return servicio;
	}
	public void setServicio(Servicio servicio) {
		this.servicio = servicio;
	}
	public Integer getNumeroLicencias() {
		return numeroLicencias;
	}
	public void setNumeroLicencias(Integer numeroLicencias) {
		this.numeroLicencias = numeroLicencias;
	}
	
	
}
