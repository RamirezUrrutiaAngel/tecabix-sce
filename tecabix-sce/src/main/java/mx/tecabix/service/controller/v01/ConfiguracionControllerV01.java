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
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Configuracion;
import mx.tecabix.db.entity.Empresa;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.CatalogoTipo;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.CatalogoTipoService;
import mx.tecabix.db.service.ConfiguracionService;
import mx.tecabix.db.service.EmpresaService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("configuracion/v1")
public final class ConfiguracionControllerV01 extends Auth{
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfiguracionControllerV01.class);
	private static final String LOG_URL = "/configuracion/v1";
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private CatalogoTipoService catalogoTipoService;
	@Autowired
	private EmpresaService empresaService;
	@Autowired
	private ConfiguracionService configuracionService;
	
	
	private static final String CONFIGURACION = "CONFIGURACION";
	
	@ApiOperation(value = "Persiste la entidad de la Configuracion")
	@PostMapping()
	public ResponseEntity<Configuracion> save(
			@RequestParam(value="token") UUID token, 
			@RequestBody Configuracion configuracion){
		
		Sesion sesion = getSessionIfIsAuthorized(token, CONFIGURACION);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Configuracion>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Configuracion.SIZE_VALOR, configuracion.getValor())) {
			LOG.info("{}El formato del valor es incorrecto.",headerLog);
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		
		if(isNotValid(configuracion.getTipo())) {
			LOG.info("{}No se mando el tipo.",headerLog);
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		
		if(isNotValid(configuracion.getTipo().getCatalogoTipo())) {
			LOG.info("{}No se mando el nombre del catalogo tipo.",headerLog);
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		if(isValid(configuracion.getTipo().getCatalogoTipo().getClave())) {
			Optional<CatalogoTipo> optionalCatalogoTipo = catalogoTipoService
					.findByClave(configuracion.getTipo().getCatalogoTipo().getClave());
			if(optionalCatalogoTipo.isEmpty()) {
				LOG.info("{}No se encontro la clave del catalogo tipo.",headerLog);
				return new ResponseEntity<Configuracion>(HttpStatus.NOT_FOUND);
			}
			configuracion.getTipo().setCatalogoTipo(optionalCatalogoTipo.get());
			for(Catalogo catalogo : configuracion.getTipo().getCatalogoTipo().getCatalogos()) {
				if(configuracion.getTipo().getNombre().equalsIgnoreCase(catalogo.getNombre())) {
					LOG.info("{}Conflicto de nombre repetido en los catalogos existente.",headerLog);
					return new ResponseEntity<Configuracion>(HttpStatus.CONFLICT);
				}
			}
		}else if(isValid(configuracion.getTipo().getCatalogoTipo().getCatalogos())) {
			for(Catalogo catalogo:configuracion.getTipo().getCatalogoTipo().getCatalogos()) {
				if(isNotValid(catalogo.getNombre())) {
					LOG.info("{}No se mando el nombre del catalogo.",headerLog);
					return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(catalogo.getNombreCompleto())) {
					LOG.info("{}No se mando el nombre completo del catalogo.",headerLog);
					return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
				}
				if(isNotValid(catalogo.getDescripcion())) {
					LOG.info("{}No se mando la descripcion del catalogo.",headerLog);
					return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
				}
			}
			for (int i = 0; i < configuracion.getTipo().getCatalogoTipo().getCatalogos().size(); i++) {
				Catalogo catalogoA = configuracion.getTipo().getCatalogoTipo().getCatalogos().get(i);
				for (int j = i + 1; j < configuracion.getTipo().getCatalogoTipo().getCatalogos().size(); j++) {
					Catalogo catalogoB = configuracion.getTipo().getCatalogoTipo().getCatalogos().get(j);
					if (catalogoA.getNombre().equalsIgnoreCase(catalogoB.getNombre())) {
						LOG.info("{}Conflicto de nombre repetidos en los catalogos.",headerLog);
						return new ResponseEntity<Configuracion>(HttpStatus.CONFLICT);
					}
				}
			}
		}
		if(isNotValid(configuracion.getTipo().getNombre())) {
			LOG.info("{}No se mando el nombre del tipo.",headerLog);
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(configuracion.getTipo().getNombreCompleto())) {
			LOG.info("{}No se mando el nombre completo del tipo.",headerLog);
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(configuracion.getTipo().getDescripcion())) {
			LOG.info("{}No se mando la descripcion del tipo.",headerLog);
			return new ResponseEntity<Configuracion>(HttpStatus.BAD_REQUEST);
		}
		Catalogo ACTIVO = singletonUtil.getActivo();
		
		if(isNotValid(configuracion.getTipo().getCatalogoTipo().getClave())) {
			configuracion.getTipo().getCatalogoTipo().setId(null);
			configuracion.getTipo().getCatalogoTipo().setIdUsuarioModificado(sesion.getIdUsuarioModificado());
			configuracion.getTipo().getCatalogoTipo().setFechaDeModificacion(LocalDateTime.now());
			configuracion.getTipo().getCatalogoTipo().setEstatus(ACTIVO);
			configuracion.getTipo().getCatalogoTipo().setClave(UUID.randomUUID());
			List<Catalogo> catalogos = configuracion.getTipo().getCatalogoTipo().getCatalogos();
			CatalogoTipo catalogoTipo = catalogoTipoService.save(configuracion.getTipo().getCatalogoTipo());
			configuracion.getTipo().setCatalogoTipo(catalogoTipo);
			if(isValid(catalogos)) {
				catalogos = catalogos.stream().map(x->{
					x.setId(null);
					x.setCatalogoTipo(catalogoTipo);
					x.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
					x.setFechaDeModificacion(LocalDateTime.now());
					x.setEstatus(ACTIVO);
					x.setClave(UUID.randomUUID());
					return catalogoService.save(x);
				}).collect(Collectors.toList());
				catalogoTipo.setCatalogos(catalogos);
			}
		}
		List<Empresa> empresas = empresaService.findAll();
		empresas.stream().forEach(x->{
			Configuracion aux = new Configuracion();
			aux.setTipo(configuracion.getTipo());
			aux.setValor(configuracion.getValor());
			aux.getTipo().setIdUsuarioModificado(sesion.getIdUsuarioModificado());
			aux.getTipo().setFechaDeModificacion(LocalDateTime.now());
			aux.getTipo().setEstatus(ACTIVO);
			aux.getTipo().setClave(UUID.randomUUID());
			aux.setTipo(catalogoService.save(aux.getTipo()));
			aux.setId(null);
			aux.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
			aux.setFechaDeModificacion(LocalDateTime.now());
			aux.setEstatus(ACTIVO);
			aux.setClave(UUID.randomUUID());
			if(isNotValid(x.getConfiguraciones())) {
				x.setConfiguraciones(new ArrayList<>());
			}
			x.getConfiguraciones().add(configuracionService.save(aux));
			empresaService.update(x);
		});
		return new ResponseEntity<Configuracion>(configuracion,HttpStatus.OK);
	}
}
