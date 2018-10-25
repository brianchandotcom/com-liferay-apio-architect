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

import io.vavr.Function1;
import io.vavr.Function2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Declares a nested resource where nested APIs are called.
 *
 * @author Alejandro Hernández
 * @review
 */
public class NestedResource {

	/**
	 * Returns the result generated from executing an action over a non-default
	 * method (other than the ones included in {@link javax.ws.rs.HttpMethod})
	 * with the provided parameters.
	 *
	 * <p>
	 * Since JAX-RS resources cannot be created dynamically, this endpoint
	 * ensures that any custom HTTP method can be used in Apio Architect
	 * actions. The {@link
	 * com.liferay.apio.architect.internal.jaxrs.filter.HTTPMethodOverrideFilter}
	 * converts any non-default method into {@code CUSTOM} but the original
	 * method is used when making the call to the Apio Architect core.
	 * </p>
	 *
	 * @review
	 */
	@CUSTOM
	public Response custom(@Context HttpServletRequest httpServletRequest) {
		return _getResponseForMethod(httpServletRequest.getMethod());
	}

	/**
	 * Returns the result generated from executing a {@code DELETE} action with
	 * the provided parameters.
	 *
	 * @review
	 */
	@DELETE
	public Response delete() {
		return _getResponseForMethod("DELETE");
	}

	/**
	 * Returns the response generated from executing a {@code GET} action with
	 * the provided parameters.
	 *
	 * @review
	 */
	@GET
	public Response get() {
		return _getResponseForMethod("GET");
	}

	/**
	 * Returns the nested resource that handles the actions that contains one
	 * more parameter.
	 *
	 * @review
	 */
	@Path("/{param}")
	public NestedResource nestedResource(@PathParam("param") String param) {
		List<String> params = new ArrayList<>(_params);

		params.add(param);

		return Builder.params(
			params
		).responseFunction(
			_responseFunction
		).allowedMethodsFunction(
			_allowedMethodsFunction
		).build();
	}

	/**
	 * Returns a response containing an {@code Allow} header with allowed
	 * methods for the resource identified by the provided parameters.
	 *
	 * @review
	 */
	@OPTIONS
	public Response options() {
		return Response.noContent(
		).allow(
			_allowedMethodsFunction.apply(_params)
		).build();
	}

	/**
	 * Returns the response generated from executing a {@code PATCH} action with
	 * the provided parameters.
	 *
	 * @review
	 */
	@PATCH
	public Response patch() {
		return _getResponseForMethod("PATCH");
	}

	/**
	 * Returns the response generated from executing a {@code POST} action with
	 * the provided parameters.
	 *
	 * @review
	 */
	@POST
	public Response post() {
		return _getResponseForMethod("POST");
	}

	/**
	 * Returns the response generated from executing a {@code PUT} action with
	 * the provided parameters.
	 *
	 * @review
	 */
	@PUT
	public Response put() {
		return _getResponseForMethod("PUT");
	}

	/**
	 * Creates {@link NestedResource} instances.
	 *
	 * @review
	 */
	public interface Builder {

		/**
		 * Starts creating a new {@link NestedResource} by providing the list of
		 * previous params.
		 *
		 * @review
		 */
		public static ResponseFunctionStep params(List<String> params) {
			return responseFunction -> allowedMethodsFunction -> () ->
				new NestedResource(
					responseFunction, allowedMethodsFunction, params);
		}

		public interface AllowedMethodsFunctionStep {

			/**
			 * Provides the function used to obtain the allowed methods for a
			 * resource.
			 *
			 * @review
			 */
			public BuildStep allowedMethodsFunction(
				Function1<List<String>, Set<String>> allowedMethodsFunction);

		}

		public interface BuildStep {

			/**
			 * Creates and returns the {@link NestedResource} instance, using
			 * the information provided to the builder.
			 *
			 * @review
			 */
			public NestedResource build();

		}

		public interface ResponseFunctionStep {

			/**
			 * Provides the function used to obtain the response for a given
			 * method and params.
			 *
			 * @review
			 */
			public AllowedMethodsFunctionStep responseFunction(
				Function2<String, List<String>, Response> responseFunction);

		}

	}

	private NestedResource(
		Function2<String, List<String>, Response> responseFunction,
		Function1<List<String>, Set<String>> allowedMethodsFunction,
		List<String> params) {

		_responseFunction = responseFunction;
		_allowedMethodsFunction = allowedMethodsFunction;
		_params = params;
	}

	private Response _getResponseForMethod(String method) {
		return _responseFunction.apply(method, _params);
	}

	private final Function1<List<String>, Set<String>> _allowedMethodsFunction;
	private final List<String> _params;
	private final Function2<String, List<String>, Response> _responseFunction;

}