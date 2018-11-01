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

package com.liferay.apio.architect.internal.jaxrs.filter;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

import com.liferay.apio.architect.internal.jaxrs.util.ErrorUtil;

import io.vavr.control.Try;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Filters and converts a {@link Try.Failure} entity to its corresponding {@code
 * Response};
 *
 * @author Alejandro Hernández
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(liferay.apio.architect.application=true)",
		"osgi.jaxrs.extension=true"
	},
	service = ContainerResponseFilter.class
)
public class FailureFilter implements ContainerResponseFilter {

	@Override
	public void filter(
		ContainerRequestContext containerRequestContext,
		ContainerResponseContext containerResponseContext) {

		Object entity = containerResponseContext.getEntity();

		if (entity instanceof Try) {
			Try<?> entityTry = (Try<?>)entity;

			entityTry.onFailure(
				throwable -> {
					Response response = _errorUtil.getErrorResponse(
						throwable, _request);

					_updateContext(containerResponseContext, response);
				});

			entityTry.onSuccess(containerResponseContext::setEntity);
		}
	}

	private static void _updateContext(
		ContainerResponseContext containerResponseContext, Response response) {

		containerResponseContext.setStatus(response.getStatus());

		MultivaluedMap<String, Object> headers =
			containerResponseContext.getHeaders();

		headers.remove(CONTENT_TYPE);

		MediaType mediaType = response.getMediaType();

		if (mediaType != null) {
			headers.add(CONTENT_TYPE, mediaType.toString());
		}

		containerResponseContext.setEntity(response.getEntity());
	}

	@Reference
	private ErrorUtil _errorUtil;

	@Context
	private Request _request;

}