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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import mx.tecabix.db.entity.CajaRegistradora;
import mx.tecabix.db.entity.CajaRegistro;
import mx.tecabix.db.entity.CajaRegistroTransaccion;
import mx.tecabix.db.entity.CajaRegistroTransaccionItem;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Cuenta;
import mx.tecabix.db.entity.Plantel;
import mx.tecabix.db.entity.Producto;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.CajaRegistradoraService;
import mx.tecabix.db.service.CajaRegistroService;
import mx.tecabix.db.service.CajaRegistroTransaccionItemService;
import mx.tecabix.db.service.CajaRegistroTransaccionService;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.CuentaService;
import mx.tecabix.db.service.PlantelService;
import mx.tecabix.db.service.ProductoService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;
import mx.tecabix.service.page.CajaRegistradoraPage;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("caja-registradora/v1")
public final class CajaRegistradoraControllerV01 extends Auth{

	private static final Logger LOG = LoggerFactory.getLogger(CajaRegistradoraControllerV01.class);
	private static final String LOG_URL = "/caja-registradora/v1";
	private static final String LOG_URL_REGISTRO = "/caja-registradora/v1/registro";
	private static final String LOG_URL_TRANSACCION = "/caja-registradora/v1/registro/transaccion";
	
	private static final String CAJA_REGISTRADORA = "CAJA_REGISTRADORA";
	private static final String CAJA_REGISTRADORA_ABRIR = "CAJA_REGISTRADORA_ABRIR";
	private static final String CAJA_REGISTRADORA_CERRAR = "CAJA_REGISTRADORA_CERRAR";
	private static final String CAJA_REGISTRADORA_CREAR = "CAJA_REGISTRADORA_CREAR";
	private static final String CAJA_REGISTRADORA_EDITAR = "CAJA_REGISTRADORA_EDITAR";
	private static final String CAJA_REGISTRADORA_ELIMINAR = "CAJA_REGISTRADORA_ELIMINAR";
	private static final String CAJA_REGISTRADORA_VENTA = "CAJA_REGISTRADORA_VENTA";
	private static final String CAJA_REGISTRADORA_DEPOSITO = "CAJA_REGISTRADORA_DEPOSITO";
	
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private TrabajadorService trabajadorService;
	@Autowired
	private PlantelService plantelService;
	@Autowired
	private CajaRegistradoraService cajaRegistradoraService;
	@Autowired
	private CajaRegistroService cajaRegistroService;
	@Autowired
	private CajaRegistroTransaccionService cajaRegistroTransaccionService;
	@Autowired
	private CajaRegistroTransaccionItemService cajaRegistroTransaccionItemService;
	
	@Autowired
	private ProductoService productoService;
	@Autowired
	private CuentaService cuentaService;
	
	private static Catalogo CATALOGO_PRODUCTO, CATALOGO_CUENTA;
	
	@PostConstruct
	private void postConstruct() {
		if(CATALOGO_CUENTA == null && CATALOGO_PRODUCTO == null){
			Optional<Catalogo> optional = catalogoService.findByTipoAndNombre("ID_TABLA_TRANSACCION_CAJA", "PRODUCTO");
			if(optional.isEmpty()) {
				LOG.error("No se encontro el catalogo PRODUCTO de ID_TABLA_TRANSACCION_CAJA.");
				System.exit(-1);
			}
			CATALOGO_PRODUCTO = optional.get();
			optional = catalogoService.findByTipoAndNombre("ID_TABLA_TRANSACCION_CAJA", "CUENTA");
			if(optional.isEmpty()) {
				LOG.error("No se encontro el catalogo CUENTA de ID_TABLA_TRANSACCION_CAJA.");
				System.exit(-1);
			}
			CATALOGO_CUENTA = optional.get();
		}
	}
	
	/**
	 * 
	 * @param by:		NOMBRE, APELLIDO_PATERNO, APELLIDO_MATERNO, CURP, PUESTO, PLANTEL
	 * @param order:	ASC, DESC
	 * 
	 */
	@ApiOperation(value = "Trae todos las cajas registradoras paginados con estatus ACTIVO.", 
			notes = "<b>by:</b> NOMBRE, DESCRIPCION, MARCA, MODELO <br/><b>order:</b> ASC, DESC")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Se realizo la petición correctamente.", response = Trabajador.class),
			@ApiResponse(code = 401, message = "El cliente no tiene permitido acceder a los recursos del servidor, ya sea por que el nombre y contraseña no es valida, o el token no es valido para el usuario, o el usuario no tiene autorizado consumir el recurso.") })
	@GetMapping()
	public ResponseEntity<CajaRegistradoraPage> find(
			@RequestParam(value="token") UUID token,
			@RequestParam(value="search", required = false) String search,
			@RequestParam(value="by", defaultValue = "NOMBRE") String by,
			@RequestParam(value="order", defaultValue = "ASC") String order,
			@RequestParam(value="elements") byte elements,
			@RequestParam(value="page") short page) {
		
		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA);
		if(sesion == null) {
			return new ResponseEntity<CajaRegistradoraPage>(HttpStatus.UNAUTHORIZED);
		}
		Sort sort = null;
		if(order.equalsIgnoreCase("ASC")) {
			sort = Sort.by(Sort.Direction.ASC, by.toLowerCase());
		}else if(order.equalsIgnoreCase("DESC")) {
			sort = Sort.by(Sort.Direction.DESC, by.toLowerCase());
		}else {
			new ResponseEntity<CajaRegistradoraPage>(HttpStatus.BAD_REQUEST);
		}
		Long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		Page<CajaRegistradora> response = null;
		if(search == null || search.isEmpty()) {
			response = cajaRegistradoraService.findByIdEmpresa(idEmpresa,elements, page, sort);
		}else {
			StringBuilder text = new StringBuilder("%").append(search).append("%");
			if(by.equalsIgnoreCase("NOMBRE")) {
				response = cajaRegistradoraService.findLikeNombre(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("DESCRIPCION")) {
				response = cajaRegistradoraService.findLikeDescripcion(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("MARCA")) {
				response = cajaRegistradoraService.findLikeMarca(idEmpresa, text.toString(), elements, page, sort);
			}else if(by.equalsIgnoreCase("MODELO")) {
				response = cajaRegistradoraService.findLikeModelo(idEmpresa, text.toString(), elements, page, sort);
			}else {
				new ResponseEntity<CajaRegistradoraPage>(HttpStatus.BAD_REQUEST);
			}
		}
		CajaRegistradoraPage body = new CajaRegistradoraPage(response);
		return new ResponseEntity<CajaRegistradoraPage>(body, HttpStatus.OK);
	}

	
	@ApiOperation(value = "Persiste la entidad del tipo de caja registradora. ")
	@PostMapping
	public ResponseEntity<CajaRegistradora> save(@RequestParam(value="token") UUID token, @RequestBody CajaRegistradora cajaRegistradora){

		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA_CREAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<CajaRegistradora>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_NOMBRE, cajaRegistradora.getNombre())){
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_DESCRIPCION , cajaRegistradora.getDescripcion())){
			LOG.info("{}El formato de la descripcion es incorrecto.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(cajaRegistradora.getMarca() != null && cajaRegistradora.getMarca().isBlank()) {
			cajaRegistradora.setMarca(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_MARCA , cajaRegistradora.getMarca())){
				LOG.info("{}El formato de la marca es incorrecto.",headerLog);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
			}
		}
		if(cajaRegistradora.getModelo() != null && cajaRegistradora.getModelo().isBlank()) {
			cajaRegistradora.setModelo(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_MODELO , cajaRegistradora.getModelo())){
				LOG.info("{}El formato del modelo es incorrecto.",headerLog);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
			}
		}
		if(cajaRegistradora.getPlantel() == null || cajaRegistradora.getPlantel().getClave() == null) {
			Optional<Trabajador> optionalTrabajador = trabajadorService
					.findByClaveUsuario(sesion.getUsuario().getClave());
			if(optionalTrabajador.isEmpty()) {
				LOG.error("{}No se encontro el trabajador para el usuario {}.",headerLog, sesion.getUsuario().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			cajaRegistradora.setPlantel(optionalTrabajador.get().getPlantel());
		}else {
			Optional<Plantel> optionalPlantel = plantelService.findByClave(cajaRegistradora.getPlantel().getClave());
			if(optionalPlantel.isEmpty()) {
				LOG.info("{}No se encontro el plantel {}.",headerLog, cajaRegistradora.getPlantel().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			Plantel plantel = optionalPlantel.get();
			if(!plantel.getIdEmpresa().equals(idEmpresa)) {
				LOG.info("{}No se encontro el plantel {} para la empresa {}.",headerLog, cajaRegistradora.getPlantel().getClave(), idEmpresa);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			if(!plantel.getEstatus().equals(singletonUtil.getActivo())) {
				LOG.info("{}No se encuentra activado el plantel {}.",headerLog, cajaRegistradora.getPlantel().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			cajaRegistradora.setPlantel(plantel);
		}
		Catalogo ACTIVO = singletonUtil.getActivo();
		cajaRegistradora.setId(null);
		cajaRegistradora.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegistradora.setFechaDeModificacion(LocalDateTime.now());
		cajaRegistradora.setEstatus(ACTIVO);
		cajaRegistradora.setClave(UUID.randomUUID());
		cajaRegistradora = cajaRegistradoraService.save(cajaRegistradora);
		return new ResponseEntity<CajaRegistradora>(cajaRegistradora,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad de la caja registradora. ")
	@PutMapping
	public ResponseEntity<CajaRegistradora> update(@RequestParam(value="token") UUID token, @RequestBody CajaRegistradora cajaRegistradora){

		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA_EDITAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<CajaRegistradora>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPut(idEmpresa, LOG_URL);
		if(isNotValid(cajaRegistradora.getClave())) {
			LOG.info("{}No se mando la clave de la caja registradora.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_NOMBRE, cajaRegistradora.getNombre())){
			LOG.info("{}El formato del nombre es incorrecto.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_DESCRIPCION , cajaRegistradora.getDescripcion())){
			LOG.info("{}El formato de la descripcion es incorrecto.",headerLog);
			return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
		}
		if(cajaRegistradora.getMarca() != null && cajaRegistradora.getMarca().isBlank()) {
			cajaRegistradora.setMarca(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_MARCA , cajaRegistradora.getMarca())){
				LOG.info("{}El formato de la marca es incorrecto.",headerLog);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
			}
		}
		if(cajaRegistradora.getModelo() != null && cajaRegistradora.getModelo().isBlank()) {
			cajaRegistradora.setModelo(null);
		}else {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE, CajaRegistradora.SIZE_MODELO , cajaRegistradora.getModelo())){
				LOG.info("{}El formato del modelo es incorrecto.",headerLog);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.BAD_REQUEST);
			}
		}
		if(cajaRegistradora.getPlantel() == null || cajaRegistradora.getPlantel().getClave() == null) {
			Optional<Trabajador> optionalTrabajador = trabajadorService
					.findByClaveUsuario(sesion.getUsuario().getClave());
			if(optionalTrabajador.isEmpty()) {
				LOG.error("{}No se encontro el trabajador para el usuario {}.",headerLog, sesion.getUsuario().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			cajaRegistradora.setPlantel(optionalTrabajador.get().getPlantel());
		}else {
			Optional<Plantel> optionalPlantel = plantelService.findByClave(cajaRegistradora.getPlantel().getClave());
			if(optionalPlantel.isEmpty()) {
				LOG.info("{}No se encontro el plantel {}.",headerLog, cajaRegistradora.getPlantel().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			Plantel plantel = optionalPlantel.get();
			if(!plantel.getIdEmpresa().equals(idEmpresa)) {
				LOG.info("{}No se encontro el plantel {} para la empresa {}.",headerLog, cajaRegistradora.getPlantel().getClave(), idEmpresa);
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			if(!plantel.getEstatus().equals(singletonUtil.getActivo())) {
				LOG.info("{}No se encuentra activado el plantel {}.",headerLog, cajaRegistradora.getPlantel().getClave());
				return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
			}
			cajaRegistradora.setPlantel(plantel);
		}
		
		Optional<CajaRegistradora> optionalCajaRegistradora = cajaRegistradoraService.findByClave(cajaRegistradora.getClave());
		if(optionalCajaRegistradora.isEmpty()) {
			LOG.info("{}No se encontro la caja registradora {}.",headerLog, cajaRegistradora.getClave());
			return new ResponseEntity<CajaRegistradora>(HttpStatus.NOT_FOUND);
		}
		CajaRegistradora cajaRegistradoraEdit = optionalCajaRegistradora.get();
		cajaRegistradoraEdit.setDescripcion(cajaRegistradora.getDescripcion());
		cajaRegistradoraEdit.setMarca(cajaRegistradora.getMarca());
		cajaRegistradoraEdit.setModelo(cajaRegistradora.getModelo());
		cajaRegistradoraEdit.setNombre(cajaRegistradora.getNombre());
		cajaRegistradoraEdit.setPlantel(cajaRegistradora.getPlantel());
		cajaRegistradoraEdit.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegistradoraEdit.setFechaDeModificacion(LocalDateTime.now());
		cajaRegistradoraEdit = cajaRegistradoraService.save(cajaRegistradora);
		return new ResponseEntity<CajaRegistradora>(cajaRegistradoraEdit,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Elimina la entidad de la caja registradora por clave. ")
	@DeleteMapping
	public ResponseEntity<?> delete(@RequestParam(value="token") UUID token,@RequestParam(value="clave") UUID clave) {
		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA_ELIMINAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<CajaRegistradora>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogDelete(idEmpresa, LOG_URL);
		Optional<CajaRegistradora> optional = cajaRegistradoraService.findByClave(clave);
		if(optional.isEmpty()) {
			LOG.info("{}No se encontro la caja registradora {}.",headerLog, clave);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		Catalogo ELIMINADO = singletonUtil.getEliminado();
		CajaRegistradora cajaRegistradora = optional.get();
		cajaRegistradora.setEstatus(ELIMINADO);
		cajaRegistradora.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegistradora.setFechaDeModificacion(LocalDateTime.now());
		cajaRegistradoraService.update(cajaRegistradora);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@ApiOperation(value = "Abrir Caja. ")
	@PostMapping("registro")
	public ResponseEntity<CajaRegistro> open(@RequestParam(value="token") UUID token, @RequestBody CajaRegistro cajaRegistro){

		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA_ABRIR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL_REGISTRO);
		if(isNotValid(TIPO_NUMERIC_NATURAL, Integer.MAX_VALUE, cajaRegistro.getSaldoInicial())){
			LOG.info("{}El formato del saldo inicial de '{}' es incorrecto.",headerLog, cajaRegistro.getSaldoInicial());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Optional<CajaRegistradora> optionalCajaRegistradora = cajaRegistradoraService.findByIdLicencia(sesion.getLicencia().getId());
		if(optionalCajaRegistradora.isEmpty()) {
			LOG.info("{}No se encontro la caja registradora {}.",headerLog, sesion.getLicencia().getId());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		CajaRegistro cajaRegistroNuevo = new CajaRegistro();
		Catalogo ACTIVO = singletonUtil.getActivo();
		cajaRegistroNuevo.setCajaRegistradora(optionalCajaRegistradora.get());
		cajaRegistroNuevo.setSaldoInicial(cajaRegistro.getSaldoInicial());
		cajaRegistroNuevo.setSaldo(cajaRegistro.getSaldoInicial());
		cajaRegistroNuevo.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegistroNuevo.setFechaDeModificacion(LocalDateTime.now());
		cajaRegistroNuevo.setEstatus(ACTIVO);
		cajaRegistroNuevo.setClave(UUID.randomUUID());
		
		if(cajaRegistroNuevo.getCajaRegistradora().getRegistros().stream().filter(x->x.getFechaDeCorte() == null).count()>0) {
			LOG.info("{}La caja registradora esta abierta {}.",headerLog, cajaRegistroNuevo.getCajaRegistradora().getId());
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		
		cajaRegistroNuevo = cajaRegistroService.save(cajaRegistroNuevo);
		return new ResponseEntity<CajaRegistro>(cajaRegistroNuevo,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Cerrar Caja. ")
	@DeleteMapping("registro")
	public ResponseEntity<CajaRegistro> close(@RequestParam(value="token") UUID token,@RequestParam(value="usr-close") String usrNameClose, @RequestParam(value="pin-close", required = false) String usrPinClose, @RequestBody CajaRegistro cajaRegistro){

		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA);
		if(isNotValid(sesion)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogDelete(idEmpresa, LOG_URL_REGISTRO);
		if(isNotValid(TIPO_NUMERIC_NATURAL, Integer.MAX_VALUE, cajaRegistro.getSaldoFinal())){
			LOG.info("{}El formato del saldo inicial de '{}' es incorrecto.",headerLog, cajaRegistro.getSaldoFinal());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Optional<CajaRegistradora> optionalCajaRegistradora = cajaRegistradoraService.findByIdLicencia(sesion.getLicencia().getId());
		if(optionalCajaRegistradora.isEmpty()) {
			LOG.info("{}No se encontro la caja registradora {}.",headerLog, sesion.getLicencia().getId());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		CajaRegistro cajaRegitroCerrar = new CajaRegistro();
		
		cajaRegitroCerrar.setCajaRegistradora(optionalCajaRegistradora.get());
		
		Optional<CajaRegistro> optionalCajaRejistro = cajaRegitroCerrar.getCajaRegistradora().getRegistros().stream().filter(x->x.getFechaDeCorte() == null).findFirst();
		
		if(optionalCajaRejistro.isEmpty()) {
			LOG.info("{}No se encontro un registro abierto para la licencia {}.",headerLog, sesion.getLicencia().getId());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		cajaRegitroCerrar = optionalCajaRejistro.get();
		Usuario usuarioCorte = null;
		if(sesion.getUsuario().getNombre().equals(usrNameClose)) {
			usuarioCorte = sesion.getUsuario();
		}else {
			Optional<Usuario> optionalUsuario = usuarioService.findByNombre(usrNameClose);
			if(optionalUsuario.isEmpty()) {
				LOG.info("{}No se encontro el usuario {}.",headerLog, usrNameClose);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			usuarioCorte = optionalUsuario.get();
			if(!usuarioCorte.getPin().equals(usrPinClose)) {
				LOG.info("{}El pin del usuario {} no es correcto.",headerLog, usrNameClose);
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
		
		if(usuarioCorte.getPerfil().getAutorizaciones().stream().filter(x->x.getNombre().equals(CAJA_REGISTRADORA_CERRAR)).count()<1) {
			LOG.info("{}El usuario {} no tiene permiso para cerrar la caja.",headerLog, usrNameClose);
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
		cajaRegitroCerrar.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegitroCerrar.setFechaDeModificacion(LocalDateTime.now());
		cajaRegitroCerrar.setSaldoFinal(cajaRegistro.getSaldoFinal());
		cajaRegitroCerrar.setFechaDeCorte(LocalDateTime.now());
		cajaRegitroCerrar.setIdUsuarioCorte(usuarioCorte.getId());
		cajaRegitroCerrar = cajaRegistroService.update(cajaRegitroCerrar);
		
		return new ResponseEntity<CajaRegistro>(cajaRegitroCerrar,HttpStatus.OK);
	}
	
	@ApiOperation(value = "Get Registro. ")
	@GetMapping("registro")
	public ResponseEntity<CajaRegistro> getRegistro(@RequestParam(value="token") UUID token){

		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA);
		if(isNotValid(sesion)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogGet(idEmpresa, LOG_URL_REGISTRO);

		Optional<CajaRegistro> optional = cajaRegistroService.findByIdLicenciaAndFechaCorteIsNull(sesion.getLicencia().getId());
		if(optional.isEmpty()) {
			LOG.info("{}No se encontro la caja registradora {}.",headerLog, sesion.getLicencia().getId());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<CajaRegistro>(optional.get(),HttpStatus.OK);
	}
	
	
	@ApiOperation(value = "Persiste la entidad del tipo de registro de la transaccion. ")
	@PostMapping("registro/transaccion")
	public ResponseEntity<CajaRegistroTransaccion> registroTransaccionSave(@RequestParam(value="token") UUID token, @RequestBody CajaRegistroTransaccion cajaRegistroTransaccion){

		Sesion sesion = getSessionIfIsAuthorized(token, CAJA_REGISTRADORA_VENTA, CAJA_REGISTRADORA_DEPOSITO);
		if(isNotValid(sesion)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL_TRANSACCION);
		if(isNotValid(TIPO_NUMERIC_POSITIVO,Integer.MAX_VALUE,cajaRegistroTransaccion.getTransaccion())) {
			LOG.info("{}El formato de la transaccion no es correcta.",headerLog);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(cajaRegistroTransaccion.getRegistroTransaccionItems())) {
			LOG.info("{}No se mando la lista de las transacciones.",headerLog);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		List<Catalogo> catalogos = new ArrayList<Catalogo>();
		HashMap<UUID, Producto> productos = new HashMap<>();
		HashMap<UUID, Cuenta> cuentas = new HashMap<>();
		int total = 0;
		Catalogo ACTIVO = singletonUtil.getActivo();
		cajaRegistroTransaccion.setId(null);
		cajaRegistroTransaccion.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		cajaRegistroTransaccion.setFechaDeModificacion(LocalDateTime.now());
		cajaRegistroTransaccion.setEstatus(ACTIVO);
		cajaRegistroTransaccion.setClave(UUID.randomUUID());
		for(CajaRegistroTransaccionItem item :cajaRegistroTransaccion.getRegistroTransaccionItems()) {
			if(isNotValid(item)) {
				LOG.info("{}Se mando un registro nulo en la lista de las transacciones.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(item.getIdentificador())) {
				LOG.info("{}El formato del identtificador de una de las listas de las transacciones, es incorrecta.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS,CajaRegistroTransaccionItem.SIZE_DESCRIPCION,item.getDescripcion())) {
				LOG.info("{}El formato de la descripcion de una de las listas de las transacciones, es incorrecta.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(TIPO_NUMERIC_POSITIVO, Integer.MAX_VALUE, item.getCantidad())) {
				LOG.info("{}El formato de la cantidad de una de las listas de las transacciones, es incorrecta.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(item.getUnidad())) {
				LOG.info("{}No se mando la unidad en una de las listas.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(item.getUnidad().getNombre())) {
				LOG.info("{}No se mando el nombre de la unidad en una de las listas.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(TIPO_NUMERIC_NATURAL,Integer.MAX_VALUE,item.getPrecioUnitario())) {
				LOG.info("{}El formato del precio unitario de una de las listas de las transacciones, es incorrecta.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(TIPO_NUMERIC_NATURAL,Integer.MAX_VALUE,item.getPrecioGrupal())) {
				LOG.info("{}El formato del precio unitario de una de las listas de las transacciones, es incorrecta.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(item.getEntidad())) {
				LOG.info("{}No se mando la tabla en una de las listas.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			if(isNotValid(item.getEntidad().getNombre())) {
				LOG.info("{}No se mando el nombre de la tabla en una de las listas.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			{
				Catalogo catalogo = catalogos.stream().filter(x -> x.getNombre().equals(item.getUnidad().getNombre())
						&& x.getCatalogoTipo().getNombre().equals("UNIDAD_DE_MEDIDA")).findAny().orElse(null);
				if (catalogo == null) {
					Optional<Catalogo> optional = catalogoService.findByTipoAndNombre("UNIDAD_DE_MEDIDA", item.getUnidad().getNombre());
					if(optional.isEmpty()) {
						LOG.info("{}No se encontro la unidad de una de las listas de las transacciones TIPO = {},  NOMBRE = ¨{}.",headerLog,"UNIDAD_DE_MEDIDA",item.getUnidad().getNombre());
						return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
					}
					item.setUnidad(optional.get());
					catalogos.add(item.getUnidad());
				}else {
					item.setUnidad(catalogo);
				}
				catalogo = catalogos.stream().filter(x -> x.getNombre().equals(item.getEntidad().getNombre())
						&& x.getCatalogoTipo().getNombre().equals("ID_TABLA_TRANSACCION_CAJA")).findAny().orElse(null);
				if (catalogo == null) {
					Optional<Catalogo> optional = catalogoService.findByTipoAndNombre("ID_TABLA_TRANSACCION_CAJA", item.getUnidad().getNombre());
					if(optional.isEmpty()) {
						LOG.info("{}No se encontro la tabla de una de las listas de las transacciones TIPO = {},  NOMBRE = ¨{}.",headerLog,"ID_TABLA_TRANSACCION_CAJA",item.getEntidad().getNombre());
						return new ResponseEntity<>(HttpStatus.NOT_FOUND);
					}
					item.setEntidad(optional.get());
					catalogos.add(item.getUnidad());
				}else {
					item.setEntidad(catalogo);
				}
			}
			
			if(item.getEntidad().getNombre().equals("PRODUCTO")) {
				Optional<Producto> optional = productoService.findByClave(item.getIdentificador());
				if(optional.isEmpty()) {
					LOG.info("{}No se encontro el producto de una de las listas de las transacciones IDENTIFICADOR = {}.",headerLog,item.getIdentificador());
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
				if(productos.containsKey(item.getIdentificador())) {
					LOG.info("{}Hay duplicidad de registros en la listas de las transacciones.",headerLog);
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
				Producto producto =  optional.get();
				productos.put(item.getIdentificador(), producto);
				if(producto.getVenta() == null) {
					LOG.info("{}Hay un producto de los registros en la listas de las transacciones que no esta a la venta {}.",headerLog, producto.getId());
					return new ResponseEntity<>(HttpStatus.CONFLICT);
				}
				if(item.getPrecioUnitario().intValue() < producto.getVenta().getPrecio().intValue()) {
					LOG.info("{}Hay un producto de los registros en la listas de las transacciones que el precio es menor de lo debido.",headerLog);
					return new ResponseEntity<>(HttpStatus.CONFLICT);
				}
				if(item.getPrecioGrupal() < producto.getVenta().getPrecio() * item.getCantidad()) {
					LOG.info("{}Hay un producto de los registros en la listas de las transacciones que el precio grupal es menor de lo debido.",headerLog);
					return new ResponseEntity<>(HttpStatus.CONFLICT);
				}
				total+=item.getPrecioGrupal();
				item.setEntidad(CATALOGO_PRODUCTO);
			}else  if(item.getEntidad().getNombre().equals("CUENTA")) {
				Optional<Cuenta> optional = cuentaService.findByClave(item.getIdentificador());
				if(optional.isEmpty()) {
					LOG.info("{}No se encontro la cuenta {}.",headerLog, item.getIdentificador());
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
				if(cuentas.containsKey(item.getIdentificador())) {
					LOG.info("{}Hay duplicidad de registros en la listas de las cuentas.",headerLog);
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
				Cuenta cuenta = optional.get();
				cuentas.put(item.getIdentificador(), cuenta );
				total+=item.getPrecioUnitario();
				item.setEntidad(CATALOGO_CUENTA);
			}else {
				LOG.info("{}Hay una identidad desconocida en los registros en la listas de las transacciones.",headerLog);
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			item.setId(null);
			item.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
			item.setFechaDeModificacion(LocalDateTime.now());
			item.setEstatus(ACTIVO);
			item.setClave(UUID.randomUUID());
		}
		if(total < cajaRegistroTransaccion.getTransaccion()) {
			LOG.info("{}No coencide el total con las transacciones.",headerLog);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Optional<Catalogo> optional = catalogoService.findByTipoAndNombre("TIPO_CAJA_REGISTRO", "VENTA");
		if(optional.isEmpty()) {
			LOG.error("{}No se encontro el catalogo de TIPO_CAJA_REGISTRO.",headerLog);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Optional<CajaRegistro> optionalCajaRegistro = cajaRegistroService.findByIdLicenciaAndFechaCorteIsNull(sesion.getLicencia().getId());
		if(optionalCajaRegistro.isEmpty()) {
			LOG.info("{}No se encontro la caja registradora {}.",headerLog, sesion.getLicencia().getId());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		cajaRegistroTransaccion.setCajaRegistro(optionalCajaRegistro.get());
		cajaRegistroTransaccion.setTipo(optional.get());
		cajaRegistroTransaccion = cajaRegistroTransaccionService.save(cajaRegistroTransaccion);
		
		for(CajaRegistroTransaccionItem item :cajaRegistroTransaccion.getRegistroTransaccionItems()) {
			item.setCajaRegistroTransaccion(cajaRegistroTransaccion);
			item = cajaRegistroTransaccionItemService.save(item);
			//SE DEVE MEJORAR ESTA PARTE PARA CONCURRENCIA
			if(item.getEntidad().getNombre().equals("CUENTA")) {
				Cuenta cuenta = cuentas.get(item.getIdentificador());
				cuenta.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
				cuenta.setFechaDeModificacion(LocalDateTime.now());
				cuenta.setSaldo(cuenta.getSaldo() + item.getPrecioUnitario());
				cuentaService.update(cuenta);
			}
		}
		return new ResponseEntity<CajaRegistroTransaccion>(cajaRegistroTransaccion,HttpStatus.OK);
	}
}
