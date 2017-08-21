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

package com.liferay.vulcan.result;

/**
 * Instances of this interface describe an API error. Thrown {@link
 * javax.ws.rs.WebApplicationException} and descendants will be converted to a
 * special case of {@code APIError} with meaningful information. All other
 * exceptions will be converted to a {@code 500} error with a standard message.
 *
 * @author Alejandro Hernández
 */
public interface APIError {

	/**
	 * The description of this {@code APIError}. Specific to this {@code
	 * APIError} instance.
	 *
	 * @return the description of this error.
	 */
	public String getDescription();

	/**
	 * The HTTP status code for this {@code APIError}.
	 *
	 * @return the http status code of this error.
	 */
	public int getStatusCode();

	/**
	 * The title of this {@code APIError}. The same for all {@code APIErrors} of
	 * the same type.
	 *
	 * @return the title of this error.
	 */
	public String getTitle();

	/**
	 * The type of this error. Identifies errors with the same meaning.
	 *
	 * @return the type of this error.
	 */
	public String getType();

}