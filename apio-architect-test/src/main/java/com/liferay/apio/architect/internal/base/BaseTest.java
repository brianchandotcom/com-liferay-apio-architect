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

package com.liferay.apio.architect.internal.base;

import javax.ws.rs.client.ClientBuilder;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;

/**
 * Base class for integration tests. Provides utility methods for basic
 * whiteboard/Apio operations/checks.
 *
 * @author Alejandro Hernández
 * @review
 */
public class BaseTest {

	/**
	 * Returns a new {@code ClientBuilder} instance using the bundle context to
	 * retrieve the registered client builder implementation.
	 *
	 * @review
	 */
	protected static ClientBuilder getClientBuilder() {
		return _clientBuilders.getService();
	}

	private static final BundleContext _bundleContext;
	private static final ServiceObjects<ClientBuilder> _clientBuilders;

	static {
		Bundle bundle = FrameworkUtil.getBundle(BaseTest.class);

		_bundleContext = bundle.getBundleContext();

		ServiceReference<ClientBuilder> clientBuilderServiceReference =
			_bundleContext.getServiceReference(ClientBuilder.class);

		_clientBuilders = _bundleContext.getServiceObjects(
			clientBuilderServiceReference);
	}

}