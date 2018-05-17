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

package com.liferay.apio.architect.application.internal;

import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;

/**
 * Registers the application's root endpoint, writers, and mappers in JAX-RS.
 *
 * <p>
 * The default {@link com.liferay.apio.architect.endpoint.RootEndpoint} already deploys
 * all components that implement the representor. Developers only need to
 * provide a valid {@code RootEndpoint} via this class if they want to customize
 * the default behavior.
 * </p>
 *
 * @author Alejandro Hernández
 * @author Carlos Sierra
 * @author Jorge Ferrer
 */
@Component(
	immediate = true,
	property = {
		"liferay.apio.architect.application=true",
		"osgi.jaxrs.application.base=/api"
	},
	service = Application.class
)
public class ApioApplication extends Application {
}