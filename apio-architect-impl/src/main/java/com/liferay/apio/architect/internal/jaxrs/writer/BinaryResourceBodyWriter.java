/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.apio.architect.internal.jaxrs.writer;

import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

import com.liferay.apio.architect.file.BinaryFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.Collections;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;

/**
 * Writes an input stream as a binary output stream.
 *
 * @author Javier Gamarra
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(liferay.apio.architect.application=true)",
		"osgi.jaxrs.extension=true"
	},
	service = MessageBodyWriter.class
)
@Provider
public class BinaryResourceBodyWriter implements MessageBodyWriter<BinaryFile> {

	public long getSize(
		BinaryFile binaryFile, Class<?> aClass, Type type,
		Annotation[] annotations, MediaType mediaType) {

		return -1;
	}

	public boolean isWriteable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return BinaryFile.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(
			BinaryFile binaryFile, Class<?> aClass, Type type,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> multivaluedMap,
			OutputStream outputStream)
		throws IOException, WebApplicationException {

		multivaluedMap.put(
			CONTENT_TYPE, Collections.singletonList(binaryFile.getMimeType()));

		multivaluedMap.put(
			CONTENT_LENGTH, Collections.singletonList(binaryFile.getSize()));

		byte[] bytes = new byte[1024];

		InputStream inputStream = binaryFile.getInputStream();
		int value = -1;

		while ((value = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, value);
		}

		outputStream.close();
	}

}