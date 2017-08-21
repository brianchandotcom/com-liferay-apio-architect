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

package com.liferay.vulcan.jaxrs.writer.json.internal;

import com.liferay.vulcan.converter.ExceptionConverter;
import com.liferay.vulcan.result.APIError;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;

/**
 * Converts any exception to its {@link APIError} representation.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class GenericExceptionConverter
	implements ExceptionConverter<Exception> {

	@Override
	public APIError convert(Exception exception) {
		if (exception instanceof WebApplicationException) {
			WebApplicationException webApplicationException =
				(WebApplicationException)exception;

			Response response = webApplicationException.getResponse();

			return new APIErrorImpl(
				"An error occurred", exception.getMessage(), "web-application",
				response.getStatus());
		}
		else {
			return new APIErrorImpl("An error occurred", "server-error", 500);
		}
	}

}