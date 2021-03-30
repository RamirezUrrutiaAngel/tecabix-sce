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
package mx.tecabix.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.CatalogoTipo;
import mx.tecabix.db.service.CatalogoService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public final class SingletonUtil {
	private static boolean instanciado;
	
	public static final String ESTATUS = "ESTATUS";
	public static final String ACTIVO = "ACTIVO";
	public static final String ELIMINADO = "ELIMINADO";
	public static final String PENDIENTE = "PENDIENTE";
	
	@Autowired 
	private CatalogoService catalogoService;
	
	private static Catalogo activo;
	private static Catalogo eliminado;
	private static Catalogo pendiente;
	
	@PostConstruct
	private void postConstruct() {
		if(!instanciado) {
			instanciado = true;
			activo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO).get();
			eliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO).get();
			pendiente = catalogoService.findByTipoAndNombre(ESTATUS, PENDIENTE).get();
		}
	}
	private CatalogoTipo cloneA(CatalogoTipo clone) {
		CatalogoTipo aux = new CatalogoTipo();
		aux.setId(clone.getId());
		aux.setClave(clone.getClave());
		aux.setDescripcion(aux.getDescripcion());
		aux.setNombre(clone.getNombre());
		return aux;
	}
	private Catalogo cloneA(Catalogo clone) {
		Catalogo aux = new Catalogo();
		aux.setId(clone.getId());
		aux.setCatalogoTipo(cloneA(clone.getCatalogoTipo()));
		aux.setClave(clone.getClave());
		aux.setDescripcion(clone.getDescripcion());
		aux.setNombre(clone.getNombre());
		aux.setNombreCompleto(clone.getNombreCompleto());
		aux.setOrden(clone.getOrden());
		return aux;
	}

	public Catalogo getActivo() {
		return cloneA(activo);
	}

	public Catalogo getEliminado() {
		return cloneA(eliminado);
	}

	public Catalogo getPendiente() {
		return cloneA(pendiente);
	}
}
