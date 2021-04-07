package mx.tecabix.service.thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import mx.tecabix.db.entity.Correo;
import mx.tecabix.db.service.CorreoService;
import mx.tecabix.service.Auth;

@Configuration
@EnableScheduling
public class ScheduledLog extends Auth {

	private static final Logger LOG = LoggerFactory.getLogger(ScheduledLog.class);

	@Autowired
	private CorreoService correoService;

	private Correo correo = null;
	private String SUBJECT = null;
	private String TO = null;
	private List<String> CC = null;
	private String FILE_MESSAGE = null;
	private String LOG_DIR = null;
	private boolean enviar = false;

	@PostConstruct
	private void postConstruct() {
		try {
			File file = this.getConfiguracionEmailFile();
			if (file != null) {
				if (file.exists()) {
					String REMITENTE = "LOG_REMITENTE";
					String SUBJECT = "LOG_SUBJECT";
					String TO = "LOG_TO";
					String CC = "LOG_CC";
					String FILE_MESSAGE = "LOG_FILE_MESSAGE";
					String LOG_DIR = "LOG_DIR";
					String SEED = "SEED";

					try {
						Properties properties = new Properties();
						FileReader fileReader = new FileReader(file);
						properties.load(fileReader);
						SEED = properties.getProperty(SEED);
						REMITENTE = properties.getProperty(REMITENTE);
						this.SUBJECT = properties.getProperty(SUBJECT);
						this.TO = properties.getProperty(TO);
						this.FILE_MESSAGE = properties.getProperty(FILE_MESSAGE);
						this.LOG_DIR = properties.getProperty(LOG_DIR);
						String aux = properties.getProperty(CC);
						if (aux != null && !aux.trim().isEmpty()) {
							this.CC = Arrays.asList(aux.split(" ")).stream().filter(x -> !x.isBlank())
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
							String psw = correo.getPassword();
							psw = desencriptar(psw, SEED);
							correo.setPassword(psw);
							isNull = isNull || this.SUBJECT.isEmpty();
							isNull = isNull || this.TO.isEmpty();
							isNull = isNull || this.FILE_MESSAGE.isEmpty();
							isNull = isNull || this.LOG_DIR.isEmpty();
							enviar = !isNull;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			correo = null;
		}
	}

	@Scheduled(cron = "00 00 */3 * * *")
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
			String cuerpo = new String();

			try {
				File fileMsg = new File(FILE_MESSAGE);
				if (fileMsg.exists() && fileMsg.canRead()) {
					FileReader fr = new FileReader(fileMsg);
					BufferedReader br = new BufferedReader(fr);
					StringBuilder sb = new StringBuilder();
					String linea;
					while ((linea = br.readLine()) != null) {
						sb.append(linea);
					}
					br.close();
					fr.close();
					sb.append("\n\n");
					cuerpo = sb.toString();
				}
				logs = logs.stream().map(file -> {
					File aux = new File(file.getParent(), file.getName().concat(".log"));
					file.renameTo(aux);
					file.deleteOnExit();
					return aux;
				}).collect(Collectors.toList());
				sendMailAttached(correo, cuerpo, TO, SUBJECT, CC, logs);
				logs.stream().forEach(x -> x.delete());
				log.append("Se enviaron y borraron los logs\n");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				log.append("Se produjo un FileNotFoundException\n");
			} catch (IOException e) {
				log.append("Se produjo un IOException\n");
				e.printStackTrace();
			}
		}
		LOG.info(log.toString());
	}
}
