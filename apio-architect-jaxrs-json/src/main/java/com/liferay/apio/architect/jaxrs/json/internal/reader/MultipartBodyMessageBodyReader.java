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

package com.liferay.apio.architect.jaxrs.json.internal.reader;

import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import static org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent;

import com.liferay.apio.architect.form.Body;

import java.io.IOException;
import java.io.InputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import org.osgi.service.component.annotations.Component;

/**
 * Reads {@code "multipart/form-data"} as a {@link Body}.
 *
 * @author Alejandro Hernández
 */
@Component(
	immediate = true,
	property = "liferay.apio.architect.message.body.reader=true"
)
@Consumes(MULTIPART_FORM_DATA)
@Provider
public class MultipartBodyMessageBodyReader implements MessageBodyReader<Body> {

	@Override
	public boolean isReadable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return true;
	}

	@Override
	public Body readFrom(
			Class<Body> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream)
		throws IOException {

		if (!isMultipartContent(_httpServletRequest)) {
			throw new BadRequestException(
				"Request body is not a valid multipart form");
		}

		ServletFileUpload upload = new ServletFileUpload();

		try {
			FileItemIterator fileItemIterator = upload.getItemIterator(
				_httpServletRequest);

			Map<String, String> body = new HashMap<>();

			while (fileItemIterator.hasNext()) {
				FileItemStream fileItemStream = fileItemIterator.next();

				String name = fileItemStream.getFieldName();

				if (fileItemStream.isFormField()) {
					InputStream stream = fileItemStream.openStream();

					body.put(name, Streams.asString(stream));
				}
			}

			return key -> Optional.ofNullable(body.get(key));
		}
		catch (FileUploadException fue) {
			throw new BadRequestException(
				"Request body is not a valid multipart form", fue);
		}
	}

	@Context
	private HttpServletRequest _httpServletRequest;

}