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

package com.liferay.apio.architect.impl.endpoint;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.liferay.apio.architect.batch.BatchResult;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.functional.Try;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @author Alejandro Hernández
 * @author Zoltán Takács
 */
public interface BatchEndpoint {

	/**
	 * Adds multiple {@link com.liferay.apio.architect.single.model.SingleModel}
	 * to the resource specified by {@code name}. This occurs via a POST request
	 * to the resource.
	 *
	 * @param  body the request's body
	 * @return the batch result operation, or an exception if an error occurred
	 */
	@Consumes(APPLICATION_JSON)
	@Path("/")
	@POST
	public Try<BatchResult<Object>> addBatchCollectionItems(Body body);

}