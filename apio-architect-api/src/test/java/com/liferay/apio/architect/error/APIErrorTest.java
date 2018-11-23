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

package com.liferay.apio.architect.error;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class APIErrorTest {

	@Test
	public void testExceptionIsUsedAsMessageIfNoMessageCanBeFound() {
		Exception exception = new IllegalArgumentException();

		APIError apiError = new APIError(exception, "", "", 500);

		assertThat(
			apiError.getMessage(), is("java.lang.IllegalArgumentException"));
	}

	@Test
	public void testFirstConstructorCreatesValidInstance() {
		Exception exception = new IllegalArgumentException("Illegal!");

		APIError apiError = new APIError(exception, "Title", "Type", 500);

		assertThat(apiError.getDescription(), is(emptyOptional()));
		assertThat(apiError.getException(), is(exception));
		assertThat(apiError.getMessage(), is("Illegal!"));
		assertThat(apiError.getStatusCode(), is(500));
		assertThat(apiError.getTitle(), is("Title"));
		assertThat(apiError.getType(), is("Type"));
	}

	@Test
	public void testSecondConstructorCreatesValidInstance() {
		Exception exception = new IllegalArgumentException("Illegal!");

		APIError apiError = new APIError(
			exception, "Title", "Description", "Type", 500);

		assertThat(
			apiError.getDescription(),
			is(optionalWithValue(equalTo("Description"))));
		assertThat(apiError.getException(), is(exception));
		assertThat(apiError.getMessage(), is("Description"));
		assertThat(apiError.getStatusCode(), is(500));
		assertThat(apiError.getTitle(), is("Title"));
		assertThat(apiError.getType(), is("Type"));
	}

}