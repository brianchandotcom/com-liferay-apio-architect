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

package com.liferay.apio.architect.impl.internal.provider;

import com.liferay.apio.architect.impl.internal.url.ApplicationServerURL;
import com.liferay.apio.architect.impl.internal.url.ServerURL;
import com.liferay.apio.architect.provider.Provider;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * Creates the server's URL based on the HTTP request and the forwarded header,
 * to account for proxies.
 *
 * @author Javier Gamarra
 */
@Component
public class ApplicationURLProvider implements Provider<ApplicationServerURL> {

	@Override
	public ApplicationServerURL createContext(
		HttpServletRequest httpServletRequest) {

		return () -> {
			ServerURLProvider serverURLProvider = new ServerURLProvider();

			ServerURL serverURLContext = serverURLProvider.createContext(
				httpServletRequest);

			String serverURL = serverURLContext.get();

			StringBuilder sb = new StringBuilder(serverURL);

			sb.append(httpServletRequest.getContextPath());

			return sb.toString();
		};
	}

}