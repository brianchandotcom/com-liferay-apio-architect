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

import com.liferay.apio.architect.internal.jaxrs.resource.CUSTOM;

import io.vavr.collection.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;

import org.osgi.service.component.annotations.Component;

/**
 * This filter ensures that any custom HTTP method can be used in Apio Architect
 * actions. It converts any non-default method (other than the ones included in
 * {@link javax.ws.rs.HttpMethod}) into {@link CUSTOM#NAME}. Since JAX-RS
 * resources cannot be created dynamically, this filter forces that any
 * non-standard HTTP method is tunneled into an endpoint annotated with {@link
 * CUSTOM}
 *
 * @author Alejandro Hernández
 * @review
 */
@Component(
	immediate = true,
	property = {
		"osgi.jaxrs.application.select=(liferay.apio.architect.application=true)",
		"osgi.jaxrs.extension=true"
	},
	service = ContainerRequestFilter.class
)
@PreMatching
public class HTTPMethodOverrideFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext) {
		String method = requestContext.getMethod();

		if (_isNotDefault(method)) {
			requestContext.setMethod(CUSTOM.NAME);
		}
	}

	private static boolean _isNotDefault(String method) {
		return !_defaultMethods.contains(method);
	}

	private static final List<String> _defaultMethods = List.of(
		"DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT");

}