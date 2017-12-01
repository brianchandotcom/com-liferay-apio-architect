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

package com.liferay.vulcan.test.writer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.liferay.vulcan.documentation.Documentation;
import com.liferay.vulcan.message.json.DocumentationMessageMapper;
import com.liferay.vulcan.request.RequestInfo;
import com.liferay.vulcan.writer.DocumentationWriter;

import java.util.Optional;

import javax.ws.rs.core.HttpHeaders;

/**
 * Provides methods that test {@link
 * DocumentationMessageMapper} objects.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 */
public class MockDocumentationWriter {

	/**
	 * Returns a {@link RequestInfo} with the
	 * provided {@code HttpHeaders}, a mock {@link
	 * com.liferay.vulcan.url.ServerURL}, and a mock {@code
	 * javax.servlet.http.HttpServletRequest}.
	 *
	 * @param  httpHeaders the HTTP headers
	 * @return the {@code RequestInfo}
	 */
	public static RequestInfo getRequestInfo(HttpHeaders httpHeaders) {
		return RequestInfo.create(
			builder -> builder.httpHeaders(
				httpHeaders
			).httpServletRequest(
				null
			).serverURL(
				() -> "localhost"
			).build());
	}

	/**
	 * Writes a {@link Documentation} object.
	 *
	 * @param httpHeaders the request's HTTP headers
	 * @param documentationMessageMapper the {@code DocumentationMessageMapper}
	 *        to use for writing the JSON object
	 */
	public static JsonObject write(
		HttpHeaders httpHeaders,
		DocumentationMessageMapper documentationMessageMapper) {

		RequestInfo requestInfo = getRequestInfo(httpHeaders);

		Documentation documentation = new Documentation(
			__ -> Optional.of(() -> "Title"),
			__ -> Optional.of(() -> "Description"));

		DocumentationWriter documentationWriter = DocumentationWriter.create(
			builder -> builder.documentation(
				documentation
			).documentationMessageMapper(
				documentationMessageMapper
			).requestInfo(
				requestInfo
			).build());

		return new Gson().fromJson(
			documentationWriter.write(), JsonObject.class);
	}

	private MockDocumentationWriter() {
		throw new UnsupportedOperationException();
	}

}