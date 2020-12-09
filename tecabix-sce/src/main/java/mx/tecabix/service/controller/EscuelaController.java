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
package mx.tecabix.service.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.Auth;
import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Direccion;
import mx.tecabix.db.entity.Escuela;
import mx.tecabix.db.entity.Municipio;
import mx.tecabix.db.entity.Persona;
import mx.tecabix.db.entity.PersonaFisica;
import mx.tecabix.db.entity.Sesion;
import mx.tecabix.db.entity.Trabajador;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.entity.UsuarioPersona;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.DireccionService;
/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
import mx.tecabix.db.service.EscuelaService;
import mx.tecabix.db.service.MunicipioService;
import mx.tecabix.db.service.PersonaFisicaService;
import mx.tecabix.db.service.PersonaService;
import mx.tecabix.db.service.TrabajadorService;
import mx.tecabix.db.service.UsuarioPersonaService;
import mx.tecabix.db.service.UsuarioService;
import mx.tecabix.service.request.EmpresaRequest;
@RestController
@RequestMapping("escuela")
public class EscuelaController extends Auth{
	
	private String ROOT_ESCUELA_CREAR = "ROOT_ESCUELA_CREAR";
	
	private final String ESTATUS = "ESTATUS";
	private final String PENDIENTE = "PENDIENTE";
	private final String ACTIVO = "ACTIVO";
	
	private final String TIPO_DE_PERSONA = "TIPO_DE_PERSONA";
	private final String MORAL = "MORAL";
	
	private final String SEXO = "SEXO";

	@Autowired
	private EscuelaService escuelaService;
	
	@Autowired
	private CatalogoService catalogoService;
	@Autowired
	private TrabajadorService trabajadorService;
	@Autowired
	private PersonaService personaService;
	@Autowired
	private PersonaFisicaService personaFisicaService;
	@Autowired
	private MunicipioService municipioService;
	@Autowired
	private DireccionService direccionService;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private UsuarioPersonaService usuarioPersonaService;
	
	
	@GetMapping
	public ResponseEntity<Escuela> get(@RequestParam(value="token") String token){
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null){
			return new ResponseEntity<Escuela>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Escuela> escuelaOptional = escuelaService.findById(sesion.getLicencia().getPlantel().getIdEscuela());
		if(!escuelaOptional.isPresent()){
			return new ResponseEntity<Escuela>(HttpStatus.NOT_FOUND);
		}
		Escuela body = escuelaOptional.get();
		return new ResponseEntity<Escuela>(body, HttpStatus.OK);
	}
	
	@GetMapping("findByNameRegardlessOfStatus")
	public ResponseEntity<Escuela> findByNameRegardlessOfStatus(@RequestParam(value="token") String token, String nombre){
		Sesion sesion = getSessionIfIsAuthorized(token);
		if(sesion == null){
			return new ResponseEntity<Escuela>(HttpStatus.UNAUTHORIZED);
		}
		Optional<Escuela> escuelaOptional = escuelaService.findByNameRegardlessOfStatus(nombre);
		if(!escuelaOptional.isPresent()){
			return new ResponseEntity<Escuela>(HttpStatus.NOT_FOUND);
		}
		Escuela body = escuelaOptional.get();
		return new ResponseEntity<Escuela>(body, HttpStatus.OK);
	}
	
	@PostMapping("save")
	private ResponseEntity<Escuela> save(@RequestParam(value="token") String token, @RequestBody EmpresaRequest empresaRequest){
		Sesion sesion = getSessionIfIsAuthorized(token, ROOT_ESCUELA_CREAR);
		if(sesion == null) {
			return new ResponseEntity<Escuela>(HttpStatus.UNAUTHORIZED);
		}
		Trabajador trabajador = empresaRequest.getTrabajador();
		Escuela escuela = empresaRequest.getEscuela();
				
		if(trabajador == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getCURP() == null || trabajador.getCURP().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getJefe() == null || trabajador.getJefe().getId() == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getPuesto() == null || trabajador.getPuesto().getId() == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getPersonaFisica() == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getPersonaFisica().getPresona() == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getPersonaFisica().getPresona().getUsuarioPersona() == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(trabajador.getPersonaFisica().getPresona().getUsuarioPersona().getUsuario() == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		Usuario usuario = trabajador.getPersonaFisica().getPresona().getUsuarioPersona().getUsuario();
		
		if(escuela == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		
		if(escuela.getRfc() == null || escuela.getRfc().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(escuela.getNombre() == null || escuela.getNombre().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(escuela.getFundada() == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		
		PersonaFisica personaFisicaCEO = trabajador.getPersonaFisica();
		if(personaFisicaCEO == null ) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(personaFisicaCEO.getNombre() == null || personaFisicaCEO.getNombre().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(personaFisicaCEO.getSexo() == null || personaFisicaCEO.getSexo().getNombre() == null || personaFisicaCEO.getSexo().getNombre().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(personaFisicaCEO.getFechaNacimiento() == null) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(personaFisicaCEO.getApellidoMaterno() == null || personaFisicaCEO.getApellidoMaterno().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(personaFisicaCEO.getApellidoPaterno() == null || personaFisicaCEO.getApellidoPaterno().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		Direccion direccionCEO = personaFisicaCEO.getDireccion();
		if(direccionCEO == null ) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionCEO.getCalle() == null || direccionCEO.getCalle().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionCEO.getCodigoPostal() == null || direccionCEO.getCodigoPostal().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionCEO.getAsentamiento() == null || direccionCEO.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionCEO.getNumExt() == null || direccionCEO.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionCEO.getMunicipio() == null || direccionCEO.getMunicipio().getId() == null ) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		
		Direccion direccionInstitucional = escuela.getDireccion();
		if(direccionInstitucional == null ) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionInstitucional.getCalle() == null || direccionInstitucional.getCalle().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionInstitucional.getCodigoPostal() == null || direccionInstitucional.getCodigoPostal().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionInstitucional.getAsentamiento() == null || direccionInstitucional.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionInstitucional.getNumExt() == null || direccionInstitucional.getAsentamiento().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(direccionInstitucional.getMunicipio() == null || direccionInstitucional.getMunicipio().getId() == null ) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		
		final Catalogo CAT_SEXO = catalogoService.findByTipoAndNombre(SEXO, personaFisicaCEO.getSexo().getNombre());
		
		if(CAT_SEXO == null ) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		final Catalogo CAT_PENDIENTE = catalogoService.findByTipoAndNombre(ESTATUS, PENDIENTE);
		final Catalogo CAT_TIPO_PERSONA = catalogoService.findByTipoAndNombre(TIPO_DE_PERSONA, MORAL);
		Optional<Municipio> municipioOptional = municipioService.findById(direccionCEO.getMunicipio().getId());
		if(!municipioOptional.isPresent()) {
			return new ResponseEntity<Escuela>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipioCEO = municipioOptional.get();
		municipioOptional = municipioService.findById(direccionInstitucional.getMunicipio().getId());
		if(!municipioOptional.isPresent()) {
			return new ResponseEntity<Escuela>(HttpStatus.NOT_ACCEPTABLE);
		}
		Municipio municipioInstitucional = municipioOptional.get();
		Trabajador jefe = trabajadorService.findByKey(trabajador.getJefe().getId());
		if(jefe == null) {
			return new ResponseEntity<Escuela>(HttpStatus.NOT_ACCEPTABLE);
		}
		
		if(usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(usuario.getNombre() == null || usuario.getNombre().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		if(usuario.getNombre().length()>8) {
			return new ResponseEntity<Escuela>(HttpStatus.BAD_REQUEST);
		}
		
		if(usuarioService.findByNameRegardlessOfStatus(usuario.getNombre())!= null) {
			return new ResponseEntity<Escuela>(HttpStatus.CONFLICT);
		}
		
		if(escuelaService.findByNameRegardlessOfStatus(escuela.getNombre()).isPresent()) {
			return new ResponseEntity<Escuela>(HttpStatus.CONFLICT);
		}
		final Catalogo catalogoActivo = catalogoService.findByTipoAndNombre(ESTATUS, ACTIVO);
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuario.setFechaDeModificacion(LocalDateTime.now());
		usuario.setIdUsuarioModificado(sesion.getUsuario().getId());
		usuario.setEstatus(catalogoActivo);
		usuario = usuarioService.save(usuario);
		UsuarioPersona usuarioPersona = new UsuarioPersona();
		
		direccionCEO.setEstatus(CAT_PENDIENTE);
		direccionCEO.setMunicipio(municipioCEO);
		direccionCEO.setFechaDeModificacion(LocalDateTime.now());
		direccionCEO.setIdUsuarioModificado(sesion.getUsuario().getId());
		direccionCEO = direccionService.save(direccionCEO);
		
		direccionInstitucional.setEstatus(CAT_PENDIENTE);
		direccionInstitucional.setMunicipio(municipioInstitucional);
		direccionInstitucional.setFechaDeModificacion(LocalDateTime.now());
		direccionInstitucional.setIdUsuarioModificado(sesion.getUsuario().getId());
		direccionInstitucional = direccionService.save(direccionInstitucional);
		
		Persona persona=new Persona();
		persona.setTipo(CAT_TIPO_PERSONA);
		persona.setIdUsuarioModificado(sesion.getUsuario().getId());
		persona.setFechaDeModificacion(LocalDateTime.now());
		persona.setEstatus(CAT_PENDIENTE);
		persona = personaService.save(persona);
		
		usuarioPersona.setUsuario(usuario);
		usuarioPersona.setPersona(persona);
		usuarioPersona.setFechaDeModificacion(LocalDateTime.now());
		usuarioPersona.setIdUsuarioModificado(sesion.getUsuario().getId());
		usuarioPersona.setEstatus(catalogoActivo);
		usuarioPersona = usuarioPersonaService.save(usuarioPersona);
		
		escuela.setPresona(persona);
		escuela.setIdUsuarioModificado(sesion.getUsuario().getId());
		escuela.setFechaDeModificacion(LocalDateTime.now());
		escuela.setEstatus(CAT_PENDIENTE);
		escuela = escuelaService.save(escuela);
		
		persona.setIdEscuela(escuela.getId());
		escuelaService.update(escuela);

		personaFisicaCEO.setPresona(persona);
		personaFisicaCEO.setDireccion(direccionCEO);
		personaFisicaCEO.setSexo(CAT_SEXO);
		personaFisicaCEO.setFechaDeModificacion(LocalDateTime.now());
		personaFisicaCEO.setIdUsuarioModificado(sesion.getUsuario().getId());
		personaFisicaCEO.setEstatus(CAT_PENDIENTE);
		
		personaFisicaCEO = personaFisicaService.save(personaFisicaCEO);
		trabajador.setEstatus(CAT_PENDIENTE);
		trabajador.setFechaDeModificacion(LocalDateTime.now());
		trabajador.setIdUsuarioModificado(sesion.getUsuario().getId());
		trabajador.setIdEscuela(escuela.getId());
		trabajador.setJefe(null);
		trabajador.setPersonaFisica(personaFisicaCEO);
		trabajador = trabajadorService.save(trabajador);
		return new ResponseEntity<Escuela>(escuela, HttpStatus.OK);
	}
}
