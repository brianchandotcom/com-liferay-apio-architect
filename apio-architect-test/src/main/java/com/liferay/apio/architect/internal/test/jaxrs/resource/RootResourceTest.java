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

package com.liferay.apio.architect.internal.test.jaxrs.resource;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import static java.lang.String.format;
import static java.lang.String.join;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static javax.ws.rs.core.HttpHeaders.ALLOW;

import static org.apache.commons.collections.CollectionUtils.isEqualCollection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.internal.annotation.Action.Error.NotAllowed;
import com.liferay.apio.architect.internal.annotation.Action.Error.NotFound;
import com.liferay.apio.architect.internal.annotation.ActionKey;
import com.liferay.apio.architect.internal.annotation.ActionManager;
import com.liferay.apio.architect.internal.documentation.Documentation;
import com.liferay.apio.architect.internal.entrypoint.EntryPoint;
import com.liferay.apio.architect.internal.test.base.BaseTest;

import io.vavr.CheckedFunction3;
import io.vavr.control.Either;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test suite for {@link
 * com.liferay.apio.architect.internal.jaxrs.resource.RootResource} and {@link
 * com.liferay.apio.architect.internal.jaxrs.resource.NestedResource} classes.
 *
 * @author Alejandro Hernández
 */
public class RootResourceTest extends BaseTest {

	@BeforeClass
	public static void setUpClass() {
		BaseTest.setUpClass();

		beforeClassUnregisterImplementationFor(ActionManager.class);
		beforeClassRegisterImplementationFor(
			ActionManager.class, new ActionManagerImpl(), noProperties);
	}

	@Test
	public void testDocumentationEndpoint() {
		Response response = _makeRequestTo("doc", "GET");

		String entity = response.readEntity(String.class);

		JSONObject jsonObject = new JSONObject(entity);

		assertThat(response.getStatus(), is(200));
		assertThat(jsonObject.getString("@type"), is("ApiDocumentation"));
	}

	@Test
	public void testEntryPointEndpoint() {
		WebTarget webTarget = createDefaultTarget();

		Response response = webTarget.request().get();

		String entity = response.readEntity(String.class);

		JSONObject jsonObject = new JSONObject(entity);

		assertThat(response.getStatus(), is(200));
		assertThat(jsonObject.getString("@type"), is("EntryPoint"));
	}

	@Test
	public void testNotAllowedEndpoint() {
		Response response = _makeRequestTo("not-allowed", "GET");

		List<String> allowedMethods = _getAllowedMethods(response);

		assertThat(response.getStatus(), is(405));

		assertThat(allowedMethods, containsInAnyOrder("PUT", "GET"));
	}

	@Test
	public void testNotAllowedNestedEndpoint() {
		Response response = _makeRequestTo("nested/not-allowed", "GET");

		List<String> allowedMethods = _getAllowedMethods(response);

		assertThat(response.getStatus(), is(405));

		assertThat(allowedMethods, containsInAnyOrder("POST", "PUT"));
	}

	@Test
	public void testNotFoundEndpoint() {
		Response response = _makeRequestTo("not-found", "GET");

		assertThat(response.getStatus(), is(404));
	}

	@Test
	public void testNotFoundNestedEndpoint() {
		Response response = _makeRequestTo("nested/not-found", "GET");

		assertThat(response.getStatus(), is(404));
	}

	@Test
	public void testResourceEndpoints() {
		List<String> endpoints = asList("hello", "hello/hi", "hello/hi/bye");

		List<String> methods = asList("DELETE", "GET", "PATCH", "POST", "PUT");

		endpoints.forEach(
			endpoint -> methods.forEach(
				method -> _testEndpoint(endpoint, method)));
	}

	private List<String> _getAllowedMethods(Response response) {
		String allow = response.getHeaderString(ALLOW);

		return asList(allow.split("\\s*,\\s*"));
	}

	private Response _makeRequestTo(String path, String method) {
		WebTarget webTarget = createDefaultTarget();

		return webTarget.path(
			path
		).request(
		).method(
			method
		);
	}

	private void _testEndpoint(String endpoint, String method) {
		Response response = _makeRequestTo(endpoint, method);

		String expected = format(
			"Endpoint = %s, Method = %s", endpoint, method);

		assertThat(response.getStatus(), is(200));
		assertThat(response.readEntity(String.class), is(expected));
	}

	private static class ActionManagerImpl implements ActionManager {

		@Override
		public void add(
			ActionKey actionKey,
			CheckedFunction3<Object, ?, List<Object>, ?> actionFunction,
			Class... providers) {
		}

		@Override
		public Either<Action.Error, Action> getAction(
			String method, List<String> params) {

			if (isEqualCollection(params, singletonList("not-found"))) {
				return left(_notFound);
			}

			if (isEqualCollection(params, asList("nested", "not-found"))) {
				return left(_notFound);
			}

			if (isEqualCollection(params, singletonList("not-allowed"))) {
				return left((NotAllowed)() -> _specialAllowedMethods);
			}

			if (isEqualCollection(params, asList("nested", "not-allowed"))) {
				return left((NotAllowed)() -> _specialNestedAllowedMethods);
			}

			Action action = __ -> format(
				"Endpoint = %s, Method = %s", join("/", params), method);

			return right(action);
		}

		@Override
		public List<Action> getActions(
			ActionKey actionKey, Credentials credentials) {

			return emptyList();
		}

		@Override
		public Documentation getDocumentation(
			HttpServletRequest httpServletRequest) {

			return new Documentation(
				Optional::empty, Optional::empty, Optional::empty,
				Collections::emptyMap, Collections::emptySet,
				__ -> emptyList(), () -> __ -> Locale::toString);
		}

		@Override
		public EntryPoint getEntryPoint() {
			return Collections::emptyList;
		}

		private static final NotFound _notFound = new NotFound() {
		};
		private static final HashSet<String> _specialAllowedMethods =
			new HashSet<>(asList("PUT", "GET"));
		private static final HashSet<String> _specialNestedAllowedMethods =
			new HashSet<>(asList("POST", "PUT"));

	}

}