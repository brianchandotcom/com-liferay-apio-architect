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

package com.liferay.apio.architect.internal.test.jaxrs.filter;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

import com.liferay.apio.architect.internal.jaxrs.resource.CUSTOM;
import com.liferay.apio.architect.internal.test.base.BaseTest;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test suite for {@link
 * com.liferay.apio.architect.internal.jaxrs.filter.HTTPMethodOverrideFilter}.
 *
 * @author Alejandro Hernández
 */
public class HTTPMethodOverrideFilterTest extends BaseTest {

	@BeforeClass
	public static void setUpClass() {
		BaseTest.setUpClass();

		beforeClassRegisterResource(new HelloWorldResource(), noProperties);
	}

	@Test
	public void testDefaultHTTPMethodIsNotChanged() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"hello"
		).request(
		).get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("Hello World!"));
	}

	@Test
	public void testNonDefaultHTTPMethodIsChanged() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"hello"
		).request(
		).method(
			"GREET"
		);

		assertThat(response.getStatus(), is(200));

		String expected = "Hello World! Method = GREET";

		assertThat(response.readEntity(String.class), is(expected));
	}

	@Path("hello")
	public static class HelloWorldResource {

		@GET
		public String hello() {
			return "Hello World!";
		}

		@CUSTOM
		public String hello(@Context HttpServletRequest request) {
			return "Hello World! Method = " + request.getMethod();
		}

	}

}