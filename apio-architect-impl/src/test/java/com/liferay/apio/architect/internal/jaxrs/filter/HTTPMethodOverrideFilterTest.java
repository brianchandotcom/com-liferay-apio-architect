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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.vavr.collection.List;

import javax.ws.rs.container.ContainerRequestContext;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class HTTPMethodOverrideFilterTest {

	@Before
	public void setUp() {
		_httpMethodOverrideFilter = new HTTPMethodOverrideFilter();
		_containerRequestContext = mock(ContainerRequestContext.class);
	}

	@Test
	public void testFilterDoNotTransformsAnyDefaultHTTPMethod() {
		List.of(
			"DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"
		).forEach(
			method -> {
				when(_containerRequestContext.getMethod()).thenReturn(method);

				_httpMethodOverrideFilter.filter(_containerRequestContext);
			}
		);

		verify(_containerRequestContext, never()).setMethod(anyString());
	}

	@Test
	public void testFilterTransformsAnyNonDefaultHTTPMethod() {
		List.of(
			"LINK", "MERGE", "REMOVE", "SUBSCRIBE", "UNLINK"
		).forEach(
			method -> {
				when(_containerRequestContext.getMethod()).thenReturn(method);

				_httpMethodOverrideFilter.filter(_containerRequestContext);
			}
		);

		verify(_containerRequestContext, times(5)).setMethod("CUSTOM");
	}

	private ContainerRequestContext _containerRequestContext;
	private HTTPMethodOverrideFilter _httpMethodOverrideFilter;

}