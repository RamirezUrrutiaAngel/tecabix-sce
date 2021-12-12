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
import java.nio.ByteBuffer;
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

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;



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
	private static String PATCH_RESOURCE;
	private static String PATCH_RESOURCE_TEMP;
	private static Boolean RESOURCE_IS_LOCAL;
	private static String AWS_S3_BUCKET;
	private static String AWS_ACCESS_KEY_ID;
	private static String AWS_SECRET_ACCESS_KEY;
	private static Region AWS_S3_REGION;
	
	

	@PostConstruct
	private void postConstruct() {
		if(RESOURCE_IS_LOCAL == null) {
			RESOURCE_IS_LOCAL = false;
			try {
				Properties properties = new Properties();
				FileReader fileReader;
				fileReader = new FileReader(new File(configuracionResourcelFile).getAbsoluteFile());
				properties.load(fileReader);
				PATCH_RESOURCE = properties.getProperty("patch_resource");
				PATCH_RESOURCE_TEMP = properties.getProperty("patch_resource_temp");
				String RESOURCE_IS_LOCAL = properties.getProperty("resource_is_local");
				ResourceTCBX.RESOURCE_IS_LOCAL = (RESOURCE_IS_LOCAL == null || RESOURCE_IS_LOCAL.equalsIgnoreCase("true"));
				
				AWS_ACCESS_KEY_ID = properties.getProperty("aws.access_key_id");
				AWS_SECRET_ACCESS_KEY = properties.getProperty("aws.secret_access_key");
				AWS_S3_BUCKET = properties.getProperty("aws.s3.bucket");
				String AWS_S3_REGION = properties.getProperty("aws.s3.region");
				if(AWS_S3_REGION.equalsIgnoreCase("us-west-1"))ResourceTCBX.AWS_S3_REGION = Region.US_WEST_1;
				else if(AWS_S3_REGION.equalsIgnoreCase("us-west-2"))ResourceTCBX.AWS_S3_REGION = Region.US_WEST_2;
				else if(AWS_S3_REGION.equalsIgnoreCase("us-east-1"))ResourceTCBX.AWS_S3_REGION = Region.US_EAST_1;
				else if(AWS_S3_REGION.equalsIgnoreCase("us-east-2"))ResourceTCBX.AWS_S3_REGION = Region.US_EAST_2;
				else ResourceTCBX.RESOURCE_IS_LOCAL = false;
				
				ResourceTCBX.RESOURCE_IS_LOCAL = (AWS_ACCESS_KEY_ID != null && !AWS_ACCESS_KEY_ID.isBlank() && ResourceTCBX.RESOURCE_IS_LOCAL);
				ResourceTCBX.RESOURCE_IS_LOCAL = (AWS_SECRET_ACCESS_KEY != null && ! AWS_SECRET_ACCESS_KEY.isBlank() && ResourceTCBX.RESOURCE_IS_LOCAL);
				ResourceTCBX.RESOURCE_IS_LOCAL = (AWS_S3_BUCKET != null && !AWS_S3_BUCKET.isBlank() && ResourceTCBX.RESOURCE_IS_LOCAL);
				
				fileReader.close();
			} catch (FileNotFoundException e) {
				LOG.error("se produjo un FileNotFoundException en el postConstruct de ResourceTCBX");
				e.printStackTrace();
				
			} catch (IOException e) {
				LOG.error("se produjo un IOException en el postConstruct de ResourceTCBX");
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 
	 * @param name  Nombre con el que se va guardar el archivo.
	 * @param bytes Los bytes del archivo.
	 * @throws IOException Excepción que se puede producir al intentar guardar la imagen.
	 */
	public void writer(String name, byte[] bytes) throws IOException {
		if(ResourceTCBX.RESOURCE_IS_LOCAL) {
			File file = new File(ResourceTCBX.PATCH_RESOURCE,name);
			if(file.exists()) {
				file.delete();
			}
			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(bytes);
			outputStream.close();
		}else {
			amazonWriter(name, bytes);
		}
	}
	
	/**
	 * 
	 * @param name Nombre con el que se va guardar la imagen.
	 * @param compresion El complemento de la compresión de la imagen, es decir, si se manda 0.70 la compresión será del 30% de la imagen.
	 * @param inputStream Flujo de entrada donde se encuentra la imagen.
	 * @throws IOException Excepción que se puede producir al intentar guardar la imagen.
	 */
	public void writerJPG(String name, float compresion, InputStream inputStream) throws IOException {

		File file = new File(ResourceTCBX.PATCH_RESOURCE, name);
		if (file.exists()) {
			file.delete();
		}
		OutputStream outputStream = new FileOutputStream(file);

		if (compresion > 1) {
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
		if (!ResourceTCBX.RESOURCE_IS_LOCAL) {
			FileInputStream fis = new FileInputStream(file);
			amazonWriter(name, fis.readAllBytes());
			fis.close();
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
	/**
	 * 
	 * @param name Nombre del archivo.
	 * @return Los bytes del archivo.
	 * @throws IOException Excepción que se puede producir al intentar guardar la imagen.
	 */
	public byte[] read(String name) throws IOException {
		
		if(ResourceTCBX.RESOURCE_IS_LOCAL) {
			File file = new File(ResourceTCBX.PATCH_RESOURCE, name);
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
		}else {
			return amazonReader(name);
		}
	}
	
	public void delete(String name) {
		if(!ResourceTCBX.RESOURCE_IS_LOCAL) {
			amazonDelete(name);
		}
		File file = new File(ResourceTCBX.PATCH_RESOURCE, name);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 
	 * @param name Nombre del archivo.
	 * @param bytes Los bytes del archivo.
	 */
	private void amazonWriter(String name, byte[] bytes) {
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
		S3Client s3 = S3Client.builder().region(AWS_S3_REGION).credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
		PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(AWS_S3_BUCKET).key(name).build();
		s3.putObject(objectRequest, RequestBody.fromByteBuffer(ByteBuffer.wrap(bytes)));
	}
	
	/**
	 * 
	 * @param name Nombre del archivo.
	 * @return Los bytes del archivo.
	 * @throws IOException Excepción que se puede producir al intentar guardar la imagen.
	 */
	private byte[] amazonReader(String name) throws IOException {
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
		S3Client s3 = S3Client.builder().region(AWS_S3_REGION).credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
		GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(AWS_S3_BUCKET).key(name).build();
		return s3.getObject(getObjectRequest).readAllBytes();
	}
	
	/**
	 * 
	 * @param name Nombre del archivo.
	 */
	private void amazonDelete(String name)  {
		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY);
		S3Client s3 = S3Client.builder().region(AWS_S3_REGION).credentialsProvider(StaticCredentialsProvider.create(awsCreds)).build();
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(AWS_S3_BUCKET).key(name).build();
		s3.deleteObject(deleteObjectRequest);
	}
	
	public File getPathResource() {
		return new File(PATCH_RESOURCE_TEMP);
	}
}
