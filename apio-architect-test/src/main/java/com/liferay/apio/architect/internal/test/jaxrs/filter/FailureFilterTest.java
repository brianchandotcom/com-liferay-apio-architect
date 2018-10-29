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

import static io.vavr.control.Try.failure;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;

import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import com.liferay.apio.architect.internal.test.base.BaseTest;

import io.vavr.control.Try;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test suite for {@link
 * com.liferay.apio.architect.internal.jaxrs.filter.FailureFilter}.
 *
 * @author Alejandro Hernández
 */
public class FailureFilterTest extends BaseTest {

	@BeforeClass
	public static void setUpClass() {
		BaseTest.setUpClass();

		beforeClassRegisterResource(new HelloWorldResource(), noProperties);
	}

	@Test
	public void testExceptionIsConverted() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"hello/failure-exception"
		).request(
		).get();

		assertThat(response.getStatus(), is(404));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "not-found"
			).put(
				"description", "Nope!"
			).put(
				"statusCode", 404
			).put(
				"title", "Not Found"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testSuccessTryIsUnwrapped() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"hello/success"
		).request(
		).get();

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is("Hello World"));
	}

	@Test
	public void testThrowableIsConvertedToInternalServerError() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"hello/failure-throwable"
		).request(
		).get();

		assertThat(response.getStatus(), is(500));
		assertThat(response.readEntity(String.class), isEmptyString());
	}

	@Path("hello")
	public static class HelloWorldResource {

		@GET
		@Path("failure-exception")
		public Try<String> exception() {
			return failure(new NotFoundException("Nope!"));
		}

		@GET
		@Path("success")
		public Try<String> success() {
			return Try.of(() -> "Hello World");
		}

		@GET
		@Path("failure-throwable")
		public Try<String> throwable() {
			return failure(new Error("This is bad..."));
		}

	}

}