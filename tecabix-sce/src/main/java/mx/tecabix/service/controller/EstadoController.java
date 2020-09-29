package mx.tecabix.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.tecabix.db.entity.Estado;
import mx.tecabix.db.service.EstadoService;
import mx.tecabix.service.response.EstadoListResponse;

@RestController
@RequestMapping("estado")
public class EstadoController {
	
	@Autowired
	private EstadoService estadoService;
	
	@GetMapping("all")
	public ResponseEntity<EstadoListResponse> all(){
		List<Estado> estados = estadoService.findByAll();
		for (Estado estado : estados) {
			estado.setMunicipios(null);
		}
		EstadoListResponse response = new EstadoListResponse();
		response.setEstados(estados);
		return new ResponseEntity<EstadoListResponse>(response, HttpStatus.OK);
	}
	
	// INICIO DE SERVICIO NO PROTEGIDO CON AUTENTIFICACION
	@GetMapping("all-join-municipio")
	public ResponseEntity<EstadoListResponse> allJoinMunicipio(){
		List<Estado> estados = estadoService.findByAll();
		EstadoListResponse response = new EstadoListResponse();
		response.setEstados(estados);
		return new ResponseEntity<EstadoListResponse>(response, HttpStatus.OK);
	}
	// FIN DE SERVICIO NO PROTEGIDO CON AUTENTIFICACION
	
	
}
