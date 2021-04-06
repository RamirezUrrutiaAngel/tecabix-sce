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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.CorreoService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@RestController
@RequestMapping("correo/v1")
public final class CorreoControllerV01 extends Auth{
	
	private static final Logger LOG = LoggerFactory.getLogger(CorreoControllerV01.class);
	private static final String LOG_URL = "/correo/v1";
	
	private static final String CORREO_CREAR = "CORREO_CREAR";
	private static final String TIPO_DE_CORREO = "TIPO_DE_CORREO";
	
	
	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private CorreoService correoService;
	@Autowired
	private CatalogoService catalogoService;

	@ApiOperation(value = "Persiste la entidad del correo. ")
	@PostMapping()
	public ResponseEntity<Correo> save(@RequestParam(value="token") UUID token, @RequestBody Correo correo){
		Sesion sesion = getSessionIfIsAuthorized(token, CORREO_CREAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Correo>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPost(idEmpresa, LOG_URL);
		if(isNotValid(TIPO_EMAIL, Correo.SIZE_REMITENTE, correo.getRemitente())){
			LOG.info("{}El formato del remitente es incorrecto.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Correo.SIZE_PSW, correo.getPassword())){
			LOG.info("{}El formato del psw es incorrecto.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Correo.SIZE_SMTP_SERVIDOR, correo.getSmtpServidor())){
			LOG.info("{}El formato del smtp del servidor es incorrecto.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC, Correo.SIZE_SMTP_PORT, correo.getSmtpPort())){
			LOG.info("{}El formato del smtp del port es incorrecto.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(correo.getTipo())){
			LOG.info("{}No se mando el tipo de correo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(correo.getTipo().getNombre())){
			LOG.info("{}No se mando el nombre del tipo de correo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(correo.getPeticiones())){
			LOG.info("{}No se mando el numero de peticioines que puede hacer el correo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(sesion.getUsuario().getUsuarioPersona()) || 
				isNotValid(sesion.getUsuario().getUsuarioPersona().getPersona())) {
			LOG.info("{}El usuario no esta sujeto a una persona.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.PRECONDITION_FAILED);
		}
		correo.setPersona(sesion.getUsuario().getUsuarioPersona().getPersona());
		try {
			correo.setPassword(encriptar(correo.getPassword(), getSEED()));
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return new ResponseEntity<Correo>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		Optional<Catalogo> optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_DE_CORREO,correo.getTipo().getNombre());
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No encontro el tipo en el catalogo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.NOT_FOUND);
		}
		Catalogo tipo = optionalCatalogo.get();
		if(!tipo.getEstatus().equals(singletonUtil.getActivo())) {
			LOG.info("{}El tipo en catalogo no esta activo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.NOT_FOUND);
		}
		correo.setId(null);
		correo.setTipo(tipo);
		correo.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		correo.setFechaDeModificacion(LocalDateTime.now());
		correo.setEstatus(singletonUtil.getActivo());
		correo.setClave(UUID.randomUUID());
		correo =  correoService.save(correo);
		return new ResponseEntity<Correo>(correo, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Actualiza la entidad del correo. ")
	@PutMapping
	public ResponseEntity<Correo> update(@RequestParam(value="token") UUID token, @RequestBody Correo correo){
		Sesion sesion = getSessionIfIsAuthorized(token, CORREO_CREAR);
		if(isNotValid(sesion)) {
			return new ResponseEntity<Correo>(HttpStatus.UNAUTHORIZED);
		}
		final long idEmpresa = sesion.getLicencia().getPlantel().getIdEmpresa();
		final String headerLog = formatLogPut(idEmpresa, LOG_URL);
		if(isNotValid(correo.getClave())){
			LOG.info("{}No se mando la clave.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_EMAIL, Correo.SIZE_REMITENTE, correo.getRemitente())){
			LOG.info("{}El formato del remitente es incorrecto.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(correo.getPassword() != null && !correo.getPassword().isBlank()) {
			if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Correo.SIZE_PSW, correo.getPassword())){
				LOG.info("{}El formato del psw es incorrecto.",headerLog);
				return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
			}
		}
		if(isNotValid(TIPO_ALFA_NUMERIC_SPACE_WITH_SPECIAL_SYMBOLS, Correo.SIZE_SMTP_SERVIDOR, correo.getSmtpServidor())){
			LOG.info("{}El formato del smtp del servidor es incorrecto.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(TIPO_NUMERIC, Correo.SIZE_SMTP_PORT, correo.getSmtpPort())){
			LOG.info("{}El formato del smtp del port es incorrecto.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(correo.getTipo())){
			LOG.info("{}No se mando el tipo de correo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(correo.getTipo().getNombre())){
			LOG.info("{}No se mando la clave del tipo de correo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(correo.getPeticiones())){
			LOG.info("{}No se mando el numero de peticioines que puede hacer el correo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(correo.getPersona())){
			LOG.info("{}No se mando la persona.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(isNotValid(correo.getPersona().getClave())){
			LOG.info("{}No se mando la clave de la persona.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.BAD_REQUEST);
		}
		if(correo.getPassword() != null && !correo.getPassword().isBlank()) {
			try {
				correo.setPassword(encriptar(correo.getPassword(), getSEED()));
			} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
					| IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
				return new ResponseEntity<Correo>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		Optional<Correo> optionalCorreo = correoService.findByClave(correo.getClave());
		if(optionalCorreo.isEmpty()) {
			LOG.info("{}No se encontro el correo con la clave {}.",headerLog, correo.getClave());
			return new ResponseEntity<Correo>(HttpStatus.NOT_FOUND);
		}
		Optional<Catalogo> optionalCatalogo = catalogoService.findByTipoAndNombre(TIPO_DE_CORREO,correo.getTipo().getNombre());
		if(optionalCatalogo.isEmpty()) {
			LOG.info("{}No encontro el tipo en el catalogo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.NOT_FOUND);
		}
		Catalogo tipo = optionalCatalogo.get();
		if(!tipo.getEstatus().equals(singletonUtil.getActivo())) {
			LOG.info("{}El tipo en catalogo no esta activo.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.NOT_FOUND);
		}
		Correo correoUpdate = optionalCorreo.get();
		if(sesion.getUsuario().getUsuarioPersona() != null) {
			if(!correoUpdate.getPersona().equals(sesion.getUsuario().getUsuarioPersona().getPersona())) {
				LOG.info("{}El correo no le pertenece.",headerLog);
				return new ResponseEntity<Correo>(HttpStatus.NOT_FOUND);
			}
		}else {
			LOG.info("{}El usuario no esta sujeto a una persona.",headerLog);
			return new ResponseEntity<Correo>(HttpStatus.PRECONDITION_FAILED);
		}
		if(!correoUpdate.getEstatus().equals(singletonUtil.getActivo())) {
			return new ResponseEntity<Correo>(HttpStatus.NOT_FOUND);
		}
		if(correo.getPassword() != null && !correo.getPassword().isBlank()) {
			try {
				correoUpdate.setPassword(encriptar(correo.getPassword(), getSEED()));
			} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
					| IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
				return new ResponseEntity<Correo>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		correoUpdate.setTipo(tipo);
		correoUpdate.setRemitente(correo.getRemitente());
		correoUpdate.setSmtpPort(correo.getSmtpPort());
		correoUpdate.setSmtpServidor(correo.getSmtpServidor());
		correoUpdate.setTipo(correo.getTipo());
		correoUpdate.setIdUsuarioModificado(sesion.getIdUsuarioModificado());
		correoUpdate.setFechaDeModificacion(LocalDateTime.now());
		correo =  correoService.update(correoUpdate);
		return new ResponseEntity<Correo>(correo, HttpStatus.OK);
	}
	
}
