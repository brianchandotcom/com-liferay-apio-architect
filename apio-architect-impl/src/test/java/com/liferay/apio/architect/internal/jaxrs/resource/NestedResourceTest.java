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

package com.liferay.apio.architect.internal.jaxrs.resource;

import static java.lang.String.join;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class NestedResourceTest {

	@Before
	public void setUp() {
		_nestedResource = NestedResource.Builder.params(
			asList("1", "2")
		).responseFunction(
			(method, params) -> Response.ok(
				"Endpoint = " + join("/", params) + ", Method = " + method
			).build()
		).allowedMethodsFunction(
			__ -> singleton("PUT")
		).build();
	}

	@Test
	public void testCustomCallsResultFunctionWithCustomMethod() {
		HttpServletRequest request = mock(HttpServletRequest.class);

		when(request.getMethod()).thenReturn("CUSTOM");

		Response response = _nestedResource.custom(request);

		assertThat(response.getStatus(), is(200));
		assertThat(response.getEntity(), is("Endpoint = 1/2, Method = CUSTOM"));
	}

	@Test
	public void testDeleteCallsResultFunctionWithDeleteMethod() {
		Response response = _nestedResource.delete();

		assertThat(response.getStatus(), is(200));
		assertThat(response.getEntity(), is("Endpoint = 1/2, Method = DELETE"));
	}

	@Test
	public void testGetCallsResultFunctionWithGetMethod() {
		Response response = _nestedResource.get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.getEntity(), is("Endpoint = 1/2, Method = GET"));
	}

	@Test
	public void testNestedResourceReturnsNestedResourceWithMergedParams() {
		NestedResource childNestedResource = _nestedResource.nestedResource(
			"3");

		Response response = childNestedResource.get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.getEntity(), is("Endpoint = 1/2/3, Method = GET"));
	}

	@Test
	public void testOptionsCallsResultFunctionWithOptionsMethod() {
		Response response = _nestedResource.options();

		assertThat(response.getStatus(), is(204));
		assertThat(response.getAllowedMethods(), is(singleton("PUT")));
	}

	@Test
	public void testPatchCallsResultFunctionWithPatchMethod() {
		Response response = _nestedResource.patch();

		assertThat(response.getStatus(), is(200));
		assertThat(response.getEntity(), is("Endpoint = 1/2, Method = PATCH"));
	}

	@Test
	public void testPostCallsResultFunctionWithPostMethod() {
		Response response = _nestedResource.post();

		assertThat(response.getStatus(), is(200));
		assertThat(response.getEntity(), is("Endpoint = 1/2, Method = POST"));
	}

	@Test
	public void testPutCallsResultFunctionWithPutMethod() {
		Response response = _nestedResource.put();

		assertThat(response.getStatus(), is(200));
		assertThat(response.getEntity(), is("Endpoint = 1/2, Method = PUT"));
	}

	private NestedResource _nestedResource;

}