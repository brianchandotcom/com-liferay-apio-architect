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

package com.liferay.apio.architect.endpoint;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.functional.Try;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Declares the endpoint for the form operations.
 *
 * @author Alejandro Hernández
 * @review
 */
public interface FormEndpoint {

	/**
	 * Returns the creator {@link Form} for the specified resource.
	 *
	 * @param  name the resource's name, extracted from the URL
	 * @return the {@link Form} for the specified resource, or an exception if
	 *         an error occurred
	 */
	@GET
	@Path("c/{name}")
	public Try<Form> getCreatorFormTry(@PathParam("name") String name);

	/**
	 * Returns the nested creator {@link Form} for the specified resource.
	 *
	 * @param  name the parent resource's name, extracted from the URL
	 * @param  nestedName the nested resource's name, extracted from the URL
	 * @return the {@link Form} for the specified resource, or an exception if
	 *         an error occurred
	 */
	@GET
	@Path("c/{name}/{nestedName}")
	public Try<Form> getNestedCreatorFormTry(
		@PathParam("name") String name,
		@PathParam("nestedName") String nestedName);

	/**
	 * Returns the updater {@link Form} for the specified resource.
	 *
	 * @param  name the resource's name, extracted from the URL
	 * @return the {@link Form} for the specified resource, or an exception if
	 *         an error occurred
	 */
	@GET
	@Path("u/{name}")
	public Try<Form> getUpdaterFormTry(@PathParam("name") String name);

}