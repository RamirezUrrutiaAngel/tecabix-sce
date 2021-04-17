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
package mx.tecabix.service.thread;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import mx.tecabix.db.entity.Catalogo;
import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.entity.CorreoMsj;
import mx.tecabix.db.entity.CorreoMsjItem;
import mx.tecabix.db.entity.Usuario;
import mx.tecabix.db.service.CatalogoService;
import mx.tecabix.db.service.CorreoMsjItemService;
import mx.tecabix.db.service.CorreoMsjService;
import mx.tecabix.db.service.CorreoService;
import mx.tecabix.db.service.UsuarioService;
import mx.tecabix.service.Auth;
import mx.tecabix.service.SingletonUtil;

/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Configuration
@EnableScheduling
public class ScheduledLog extends Auth {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledLog.class);

	@Value("${configuracion.email}")
	private String configuracionEmailFile;

	@Autowired
	private SingletonUtil singletonUtil;
	@Autowired
	private CorreoService correoService;
	@Autowired
	private CorreoMsjService correoMsjService;
	@Autowired
	private CorreoMsjItemService correoMsjItemService;
	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private CatalogoService catalogoService;
	

	private Correo correo = null;
	private String SUBJECT = null;
	private String TO = null;
	private List<String> copiados = null;
	private String FILE_MESSAGE = null;
	private String LOG_DIR = null;
	private boolean enviar = false;
	private Usuario root;
	private Catalogo CAT_ACTIVO;
	private Catalogo CAT_TIPO_CORREO;
	private Catalogo CAT_CC;
	private Catalogo CAT_ADJUNTO;

	@PostConstruct
	private void postConstruct() {
		try {
			File file = new File(configuracionEmailFile).getAbsoluteFile();
			if (file != null) {
				if (file.exists()) {
					
					final String TIPO_DE_CORREO = "TIPO_DE_CORREO";
					final String INFORMATIVO = "INFORMATIVO";
					final String TIPO_ELEMENTO_CORREO = "TIPO_ELEMENTO_CORREO";
					final String ADJUNTO = "ADJUNTO";
					final String CC = "CC";
					
					String REMITENTE = "LOG_REMITENTE";
					String SUBJECT = "LOG_SUBJECT";
					String LOG_TO = "LOG_TO";
					String LOG_CC = "LOG_CC";
					String FILE_MESSAGE = "LOG_FILE_MESSAGE";
					String LOG_DIR = "LOG_DIR";
					

					try {
						Properties properties = new Properties();
						FileReader fileReader = new FileReader(file);
						properties.load(fileReader);
						REMITENTE = properties.getProperty(REMITENTE);
						this.SUBJECT = properties.getProperty(SUBJECT);
						this.TO = properties.getProperty(LOG_TO);
						this.FILE_MESSAGE = properties.getProperty(FILE_MESSAGE);
						this.LOG_DIR = properties.getProperty(LOG_DIR);
						String aux = properties.getProperty(LOG_CC);
						if (aux != null && !aux.trim().isEmpty()) {
							this.copiados = Arrays.asList(aux.split(" ")).stream().filter(x -> !x.isBlank())
									.collect(Collectors.toList());
						}
						fileReader.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					boolean isNull = REMITENTE == null;
					isNull = isNull || this.TO == null || this.SUBJECT == null;
					isNull = isNull || this.FILE_MESSAGE == null || this.LOG_DIR == null;
					if (!isNull) {
						Optional<Correo> optionalCorreo = correoService.findByRemitente(REMITENTE);
						if (optionalCorreo.isPresent()) {
							correo = optionalCorreo.get();
							
							isNull = isNull || this.SUBJECT.isEmpty();
							isNull = isNull || this.TO.isEmpty();
							isNull = isNull || this.FILE_MESSAGE.isEmpty();
							isNull = isNull || this.LOG_DIR.isEmpty();
							enviar = !isNull;
							if (enviar) {
								Optional<Usuario> optionalUsuario = usuarioService.findByNombre("root");
								if (optionalUsuario.isEmpty()) {
									LOG.error("No existe el usuario con el nombre root.");
									enviar = false;
									return;
								} 
								root = optionalUsuario.get();
								CAT_ACTIVO = singletonUtil.getActivo();
								Optional<Catalogo> optionalCatalogo = catalogoService
										.findByTipoAndNombre(TIPO_DE_CORREO, INFORMATIVO);
								if (optionalCatalogo.isEmpty()) {
									LOG.error("No existe el catalogo TIPO_DE_CORREO INFORMATIVO.");
									enviar = false;
									return;
								} 
								CAT_TIPO_CORREO = optionalCatalogo.get();
								optionalCatalogo = catalogoService
										.findByTipoAndNombre(TIPO_ELEMENTO_CORREO, CC);
								if (optionalCatalogo.isEmpty()) {
									LOG.error("No existe el catalogo TIPO_ELEMENTO_CORREO CC.");
									enviar = false;
									return;
								}
								CAT_CC = optionalCatalogo.get();
								optionalCatalogo = catalogoService
										.findByTipoAndNombre(TIPO_ELEMENTO_CORREO, ADJUNTO);
								if (optionalCatalogo.isEmpty()) {
									LOG.error("No existe el catalogo TIPO_ELEMENTO_CORREO ADJUNTO.");
									enviar = false;
									return;
								}
								CAT_ADJUNTO = optionalCatalogo.get();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			correo = null;
		}
	}

	@Scheduled(cron = "33 33 */3 * * *")
	public void enviarLogs() {
		StringBuilder log = new StringBuilder();
		log.append(((enviar) ? "El envio de logs esta activo.\n" : "El envio de logs esta desactivo.\n"));
		if (enviar) {
			List<File> logs = Arrays.asList(new File(LOG_DIR).listFiles());
			if (logs == null || logs.isEmpty()) {
				log.append("No se encontro log que enviar.\n");
				return;
			}
			logs.stream().filter(file -> file.getName().startsWith(".")).forEach(file -> file.delete());
			logs = Arrays.asList(new File(LOG_DIR).listFiles());
			if (logs == null || logs.isEmpty()) {
				log.append("No se encontro log que enviar.\n");
				return;
			}
			log.append("Se encontraron ").append(logs.size()).append(" archivos\n");

			CorreoMsj correoMsj = null;
			correoMsj = new CorreoMsj();
			correoMsj.setCorreo(correo);
			correoMsj.setCorreoMsjItems(new ArrayList<>());
			correoMsj.setProgramado(LocalDateTime.now());
			correoMsj.setFechaDeModificacion(LocalDateTime.now());
			correoMsj.setIdUsuarioModificado(root.getId());
			correoMsj.setEstatus(CAT_ACTIVO);
			correoMsj.setClave(UUID.randomUUID());
			correoMsj.setAsunto(SUBJECT);
			correoMsj.setMensaje(FILE_MESSAGE);
			correoMsj.setTipo(CAT_TIPO_CORREO);
			correoMsj.setDestinatario(TO);
			correoMsj = correoMsjService.save(correoMsj);
			for(File file: logs) {
				File aux = new File(file.getParent(), file.getName().concat(".log"));
				file.renameTo(aux);
				file.deleteOnExit();
				CorreoMsjItem correoMsjItem = new CorreoMsjItem();
				correoMsjItem.setTipo(CAT_ADJUNTO);
				correoMsjItem.setFechaDeModificacion(LocalDateTime.now());
				correoMsjItem.setIdUsuarioModificado(root.getId());
				correoMsjItem.setEstatus(CAT_ACTIVO);
				correoMsjItem.setClave(UUID.randomUUID());
				correoMsjItem.setDato(aux.getAbsolutePath());
				correoMsjItem.setCorreoMsj(correoMsj);
				correoMsjItemService.save(correoMsjItem);
			}
			for(String cc:copiados) {
				CorreoMsjItem correoMsjItem = new CorreoMsjItem();
				correoMsjItem.setTipo(CAT_CC);
				correoMsjItem.setFechaDeModificacion(LocalDateTime.now());
				correoMsjItem.setIdUsuarioModificado(root.getId());
				correoMsjItem.setEstatus(CAT_ACTIVO);
				correoMsjItem.setClave(UUID.randomUUID());
				correoMsjItem.setDato(cc);
				correoMsjItem.setCorreoMsj(correoMsj);
				correoMsjItemService.save(correoMsjItem);
			}
		}
		LOG.info(log.toString());
	}
}
