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

package com.liferay.apio.architect.message.json.ld.internal;

import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonArrayThat;
import static com.liferay.apio.architect.test.json.JsonMatchers.aJsonString;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;

import com.google.gson.JsonElement;

import com.liferay.apio.architect.test.json.JsonMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;

/**
 * Provides utility functions for testing JSON Plain message mappers.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 */
public class JSONLDTestUtil {

	/**
	 * Returns a {@link Matcher} that checks if the field contains the provided
	 * types as a JSON Array.
	 *
	 * @param  types the types to match
	 * @return a matcher for a type JSON Array
	 * @review
	 */
	public static Matcher<? extends JsonElement> containsTheTypes(
		String... types) {

		Stream<String> stream = Arrays.stream(types);

		List<Matcher<? super JsonElement>> matchers = stream.map(
			CoreMatchers::equalTo
		).map(
			JsonMatchers::aJsonString
		).collect(
			Collectors.toList()
		);

		return is(aJsonArrayThat(contains(matchers)));
	}

	/**
	 * Returns a {@link Matcher} that checks if the field is a link to the
	 * provided URL.
	 *
	 * @param  url the URL to match
	 * @return a matcher for URL fields
	 * @review
	 */
	public static Matcher<? extends JsonElement> isALinkTo(String url) {
		return is(aJsonString(equalTo(url)));
	}

}