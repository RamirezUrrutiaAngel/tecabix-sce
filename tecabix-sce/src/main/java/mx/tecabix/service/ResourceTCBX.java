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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * 
 * @author Ramirez Urrutia Angel Abinadi
 * 
 */
@Service
public class ResourceTCBX {

	private static final Logger LOG = LoggerFactory.getLogger(ResourceTCBX.class);
	
	@Value("${configuracion.resource}")
	private String configuracionResourcelFile;
	private String PATCH_RESOURCE;
	

	@PostConstruct
	private void postConstruct() {
		try {
			Properties properties = new Properties();
			FileReader fileReader;
			fileReader = new FileReader(new File(configuracionResourcelFile).getAbsoluteFile());
			properties.load(fileReader);
			PATCH_RESOURCE = properties.getProperty("PATCH_RESOURCE");
			fileReader.close();
		} catch (FileNotFoundException e) {
			LOG.error("se produjo un FileNotFoundException en el postConstruct de ResourceTCBX");
			e.printStackTrace();
			
		} catch (IOException e) {
			LOG.error("se produjo un IOException en el postConstruct de ResourceTCBX");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param name  Nombre con el que se va guardar el archivo.
	 * @param bytes Los bytes del archivo.
	 * @throws IOException Excepción que se puede producir al intentar guardar la imagen.
	 */
	public void writer(String name, byte[] bytes) throws IOException {
		File file = new File(this.PATCH_RESOURCE,name);
		if(file.exists()) {
			file.delete();
		}
		OutputStream outputStream = new FileOutputStream(file);
		outputStream.write(bytes);
		outputStream.close();
	}
	
	/**
	 * 
	 * @param name Nombre con el que se va guardar la imagen.
	 * @param compresion El complemento de la compresión de la imagen, es decir, si se manda 0.70 la compresión será del 30% de la imagen.
	 * @param inputStream Flujo de entrada donde se encuentra la imagen.
	 * @throws IOException Excepción que se puede producir al intentar guardar la imagen.
	 */
	public void writerJPG(String name,float compresion,InputStream inputStream) throws IOException {
		File file = new File(this.PATCH_RESOURCE,name);
		if(file.exists()) {
			file.delete();
		}
		OutputStream outputStream = new FileOutputStream(file);
		
	    if(compresion > 1) {
	    	compresion = 1f;
	    }
	    
	    BufferedImage bufferedImage = ImageIO.read(inputStream);

	    Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
	    ImageWriter writer = (ImageWriter) writers.next();

	    ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream);
	    writer.setOutput(ios);

	    ImageWriteParam param = writer.getDefaultWriteParam();

	    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	    
	    param.setCompressionQuality(compresion);
	    writer.write(null, new IIOImage(bufferedImage, null, null), param);
	    
	    ios.close();
	    writer.dispose();
		outputStream.close();
	}
	
	/**
	 * 
	 * @param name Nombre del archivo.
	 * @return Los bytes del archivo.
	 * @throws IOException Excepción que se puede producir al intentar guardar la imagen.
	 */
	public byte[] read(String name) throws IOException {
		File file = new File(this.PATCH_RESOURCE, name);
		if(!file.exists()) {
			return null;
		}
		if(!file.canRead()) {
			return null;
		}
		InputStream inputStream = new FileInputStream(file);
		byte[] bytes = inputStream.readAllBytes();
		inputStream.close();
		return bytes;
	}
}
