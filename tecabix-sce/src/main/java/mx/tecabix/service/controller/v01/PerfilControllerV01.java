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
package mx.tecabix.service.controller.v01;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Authority;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Perfil;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.AuthorityService;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.PerfilService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.page.PerfilPage;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("perfil/v1")
public class PerfilControllerV01 extends Auth{

	@Autowired
	private PerfilService perfilService;
	@Autowired 
	private AuthorityService authorityService;
	@Autowired
	private CatalogoService catalogoService;
	
	private final String PERFIL = "PERFIL";
	private final String PERFIL_CREAR = "PERFIL_CREAR";
	private final String PERFIL_EDITAR = "PERFIL_EDITAR";
	private final String PERFIL_ELIMINAR = "PERFIL_ELIMINAR";
	
	private final String ESTATUS = "ESTATUS";
	private final String ACTIVO = "ACTIVO";
	private final String ELIMINADO = "ELIMINADO";
	
	@GetMapping
	public ResponseEntity<Perfil> get(@RequestParam(value="token") UUID token) {
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null){
			return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		}
		Usuario usuario = sesion.getUsuario();
		if(usuario == null) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_FOUND);
		}
		Perfil perfil = usuario.getPerfil();
		if(perfil == null) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Perfil>(perfil,HttpStatus.OK);
	}
	
	@GetMapping("findAll")
	public ResponseEntity<PerfilPage> findAll(@RequestParam(value="token") UUID token, byte elements, short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL);
		if(sesion == null){
			return new ResponseEntity<PerfilPage>(HttpStatus.UNAUTHORIZED);
		}
		
		Page<Perfil> response = perfilService.findAll(sesion.getLicencia().getPlantel().getIdEscuela(), elements, page);
		PerfilPage body = new PerfilPage(response);
		return new ResponseEntity<PerfilPage>(body, HttpStatus.OK);
	}
	
	@GetMapping("findAllByNombre")
	public ResponseEntity<PerfilPage> findAllByNombre(@RequestParam(value="token") UUID token, @RequestParam(value="nombre") String nombre, byte elements, short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL);
		if(sesion == null){
			return new ResponseEntity<PerfilPage>(HttpStatus.UNAUTHORIZED);
		}
		
		Page<Perfil> response = perfilService.findAllbyNombre(sesion.getLicencia().getPlantel().getIdEscuela(), nombre, elements, page);
		PerfilPage body = new PerfilPage(response);
		return new ResponseEntity<PerfilPage>(body, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Perfil> save(@RequestParam(value="token") UUID token,@RequestBody Perfil perfil){
		
		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL_CREAR);
		if(sesion == null){
			return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		}
		
		if(perfil.getDescripcion() == null || perfil.getDescripcion().isEmpty()) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		if(perfil.getNombre() == null || perfil.getNombre().isEmpty()) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		if((perfilService.findByNombre(sesion.getLicencia().getPlantel().getIdEscuela(), perfil.getNombre()))!=null) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_ACCEPTABLE);
		}
		perfil.setIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela());
		List<Authority> list = perfil.getAuthorities();
		List<Authority> listAux = new ArrayList<Authority>();
		if(list != null) {
			for (Authority authority : list) {
				Optional<Authority> authOptional = authorityService.findById(authority.getId());
				if(!authOptional.isPresent()) {
					return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
				}else {
					listAux.add(authOptional.get());
				}
			}
			perfil.setAuthorities(listAux);
		}
		Optional<Catalogo> optionalCatalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		if(!optionalCatalogoActivo.isPresent()) {
			return new ResponseEntity<Perfil>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ACTIVO = optionalCatalogoActivo.get();
		
		perfil.setIdEscuela(sesion.getLicencia().getPlantel().getIdEscuela());
		perfil.setEstatus(CAT_ACTIVO);
		perfil.setIdUsuarioModificado(sesion.getUsuario().getId());
		perfil.setFechaDeModificacion(LocalDateTime.now());
		perfilService.save(perfil);
		return new ResponseEntity<Perfil>(perfil,HttpStatus.OK);
	}
	
	@PutMapping
	public ResponseEntity<Perfil> update(@RequestParam(value="token") UUID token,@RequestBody Perfil perfil){
		
		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL_EDITAR);
		if(sesion == null){
			return new ResponseEntity<Perfil>(HttpStatus.UNAUTHORIZED);
		}
		
		if(perfil.getId() == null) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		if(perfil.getDescripcion() == null || perfil.getDescripcion().isEmpty()) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		if(perfil.getNombre() == null || perfil.getNombre().isEmpty()) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		Optional<Perfil> perfilAuxOptional = perfilService.findById(perfil.getId());
		if(!perfilAuxOptional.isPresent()) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		Perfil perfilAux = perfilAuxOptional.get();
		if(perfilAux.getIdEscuela().longValue() != sesion.getLicencia().getPlantel().getIdEscuela().longValue()) {
			return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
		}
		
		Perfil perfilExistente = perfilService.findByNombre(sesion.getLicencia().getPlantel().getIdEscuela(), perfil.getNombre());
		if(perfilExistente != null && perfilExistente.getId().longValue() != perfil.getId()) {
			return new ResponseEntity<Perfil>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		List<Authority> list = perfil.getAuthorities();
		if(list != null) {
			for (Authority authority : list) {
				if(!authorityService.findById(authority.getId()).isPresent())return new ResponseEntity<Perfil>(HttpStatus.BAD_REQUEST);
			}
		}
		perfilAux.setNombre(perfil.getNombre());
		perfilAux.setDescripcion(perfil.getDescripcion());
		perfilAux.setAuthorities(list);
		perfilAux.setIdUsuarioModificado(sesion.getUsuario().getId());
		perfilAux.setFechaDeModificacion(LocalDateTime.now());
		perfilService.update(perfilAux);
		return new ResponseEntity<Perfil>(perfilAux,HttpStatus.OK);
	}
	@DeleteMapping
	public ResponseEntity<?> delete(@RequestParam(value="token") UUID token,@RequestParam Long idPerfil){
		
		Sesion sesion = getSessionIfIsAuthorized(token, PERFIL_ELIMINAR);
		if(sesion == null){
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		Optional<Perfil> perfilOptional = perfilService.findById(idPerfil);
		
		if(!perfilOptional.isPresent() ) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Perfil perfil = perfilOptional.get();
		if(perfil.getIdEscuela().longValue() != sesion.getLicencia().getPlantel().getIdEscuela().longValue()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Optional<Catalogo> optionalCatalogoEliminado = catalogoService.findByTipoAndNombre(ESTATUS, ELIMINADO);
		if(!optionalCatalogoEliminado.isPresent()) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		final Catalogo CAT_ELIMINADO = optionalCatalogoEliminado.get();
		
		perfil.setEstatus(CAT_ELIMINADO);
		perfil.setIdUsuarioModificado(sesion.getUsuario().getId());
		perfil.setFechaDeModificacion(LocalDateTime.now());
		perfil = perfilService.update(perfil);
		perfilService.deleteById(perfil.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
