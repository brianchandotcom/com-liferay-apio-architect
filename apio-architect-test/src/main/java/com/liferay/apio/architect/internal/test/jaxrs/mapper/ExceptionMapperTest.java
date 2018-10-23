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

package com.liferay.apio.architect.internal.test.jaxrs.mapper;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import com.liferay.apio.architect.internal.test.base.BaseTest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.Path;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test suite for both {@link
 * com.liferay.apio.architect.internal.jaxrs.mapper.WebApplicationExceptionMapper}
 * and {@link
 * com.liferay.apio.architect.internal.jaxrs.mapper.GeneralExceptionMapper}
 * classes.
 *
 * @author Alejandro Hernández
 * @review
 */
public class ExceptionMapperTest extends BaseTest {

	@BeforeClass
	public static void setUpClass() {
		BaseTest.setUpClass();

		beforeClassRegisterResource(new ThrowableResource(), noProperties);
	}

	@Test
	public void testAnyExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/any"
		).request(
		).get();

		assertThat(response.getStatus(), is(500));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "server-error"
			).put(
				"statusCode", 500
			).put(
				"title", "Internal Server Error"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testBadRequestExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/bad-request"
		).request(
		).get();

		assertThat(response.getStatus(), is(400));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "bad-request"
			).put(
				"description", "You are wrong"
			).put(
				"statusCode", 400
			).put(
				"title", "Bad Request"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testClientErrorExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/any-client-error"
		).request(
		).get();

		assertThat(response.getStatus(), is(403));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "client-error"
			).put(
				"description", "Do you have a code clearance?"
			).put(
				"statusCode", 403
			).put(
				"title", "Forbidden"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testForbiddenExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/forbidden"
		).request(
		).get();

		assertThat(response.getStatus(), is(403));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "forbidden"
			).put(
				"description", "You shall not pass"
			).put(
				"statusCode", 403
			).put(
				"title", "Forbidden"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testInternalServerErrorExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/internal-server-error"
		).request(
		).get();

		assertThat(response.getStatus(), is(500));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "server-error"
			).put(
				"description", "Something bad happened"
			).put(
				"statusCode", 500
			).put(
				"title", "Internal Server Error"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testNotAcceptableExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/not-acceptable"
		).request(
		).get();

		assertThat(response.getStatus(), is(406));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "not-acceptable"
			).put(
				"description", "That is unacceptable"
			).put(
				"statusCode", 406
			).put(
				"title", "Not Acceptable"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testNotAllowedExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/not-allowed"
		).request(
		).get();

		assertThat(response.getStatus(), is(405));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "not-allowed"
			).put(
				"description", "I said that you shall not pass"
			).put(
				"statusCode", 405
			).put(
				"title", "Method Not Allowed"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testNotAuthorizedExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/not-authorized"
		).request(
		).get();

		assertThat(response.getStatus(), is(401));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "not-authorized"
			).put(
				"description", "Am I not clear enough?"
			).put(
				"statusCode", 401
			).put(
				"title", "Unauthorized"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testNotFoundExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/not-found"
		).request(
		).get();

		assertThat(response.getStatus(), is(404));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "not-found"
			).put(
				"description", "These are not the resources you are looking for"
			).put(
				"statusCode", 404
			).put(
				"title", "Not Found"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testNotSupportedExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/not-supported"
		).request(
		).get();

		assertThat(response.getStatus(), is(415));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "not-supported"
			).put(
				"description", "I do not know how to do this"
			).put(
				"statusCode", 415
			).put(
				"title", "Unsupported Media Type"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testServiceUnavailableExceptionIsTransformed() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.path(
			"throw/service-unavailable"
		).request(
		).get();

		assertThat(response.getStatus(), is(503));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "unavailable"
			).put(
				"description", "Nope"
			).put(
				"statusCode", 503
			).put(
				"title", "Service Unavailable"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	public static class AClientErrorException extends ClientErrorException {

		public AClientErrorException(String message, Response.Status status) {
			super(message, status);
		}

	}

	@Path("/throw")
	public static class ThrowableResource {

		@GET
		@Path("/any")
		public Response anyException() {
			throw new IllegalArgumentException("A message that won't appear");
		}

		@GET
		@Path("/bad-request")
		public Response badRequestException() {
			throw new BadRequestException("You are wrong");
		}

		@GET
		@Path("/any-client-error")
		public Response clientErrorException() {
			throw new AClientErrorException(
				"Do you have a code clearance?", FORBIDDEN);
		}

		@GET
		@Path("/forbidden")
		public Response forbiddenException() {
			throw new ForbiddenException("You shall not pass");
		}

		@GET
		@Path("/internal-server-error")
		public Response internalServerErrorException() {
			throw new InternalServerErrorException("Something bad happened");
		}

		@GET
		@Path("/not-acceptable")
		public Response notAcceptableException() {
			throw new NotAcceptableException("That is unacceptable");
		}

		@GET
		@Path("/not-allowed")
		public Response notAllowedException() {
			throw new NotAllowedException(
				"I said that you shall not pass", "POST", new String[0]);
		}

		@GET
		@Path("/not-authorized")
		public Response notAuthorizedException() {
			throw new NotAuthorizedException(
				"Am I not clear enough?", "1", "2");
		}

		@GET
		@Path("/not-found")
		public Response notFoundException() {
			throw new NotFoundException(
				"These are not the resources you are looking for");
		}

		@GET
		@Path("/not-supported")
		public Response notSupportedException() {
			throw new NotSupportedException("I do not know how to do this");
		}

		@GET
		@Path("/service-unavailable")
		public Response serviceUnavailableException() {
			throw new ServiceUnavailableException("Nope");
		}

	}

}