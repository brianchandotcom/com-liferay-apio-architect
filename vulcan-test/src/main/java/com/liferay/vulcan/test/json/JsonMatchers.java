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

package com.liferay.vulcan.test.json;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.liferay.vulcan.test.internal.json.IsJsonBoolean;
import com.liferay.vulcan.test.internal.json.IsJsonInt;
import com.liferay.vulcan.test.internal.json.IsJsonLong;
import com.liferay.vulcan.test.internal.json.IsJsonObjectString;
import com.liferay.vulcan.test.internal.json.IsJsonString;
import com.liferay.vulcan.test.json.Conditions.Builder;

import org.hamcrest.Matcher;

/**
 * This class provides {@code Hamcrest} {@link Matcher}s that can be used for
 * testing json files/strings.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public final class JsonMatchers {

	/**
	 * Returns a matcher that checks that an element is a boolean {@link
	 * JsonPrimitive}.
	 *
	 * @return a matcher that checks that an element is a boolean
	 * @review
	 */
	public static Matcher<JsonPrimitive> aJsonBoolean(boolean bool) {
		return new IsJsonBoolean(is(equalTo(bool)));
	}

	/**
	 * Returns a matcher that checks that an element is an integer number {@link
	 * JsonPrimitive}.
	 *
	 * @return a matcher that checks that an element is an integer number
	 * @review
	 */
	public static Matcher<JsonPrimitive> aJsonInt(
		Matcher<Integer> numberMatcher) {

		return new IsJsonInt(is(numberMatcher));
	}

	/**
	 * Returns a matcher that checks that an element is a long number {@link
	 * JsonPrimitive}.
	 *
	 * @return a matcher that checks that an element is a long number
	 * @review
	 */
	public static Matcher<JsonPrimitive> aJsonLong(
		Matcher<Long> numberMatcher) {

		return new IsJsonLong(is(numberMatcher));
	}

	/**
	 * Returns a matcher that checks that a string is a json object.
	 *
	 * @return a matcher that checks that a string is a json object.
	 * @review
	 */
	public static Matcher<String> aJsonObjectStringWith(Conditions conditions) {
		return new IsJsonObjectString(conditions);
	}

	/**
	 * Returns a matcher that checks that a {@link JsonObject} is a valid json
	 * object with one element inside.
	 *
	 * @return a matcher that checks that a {@code JsonObject} is a valid json
	 *         object.
	 * @review
	 */
	public static Matcher<JsonObject> aJsonObjectWhere(
		String key, Matcher<? extends JsonElement> matcher) {

		Builder builder = new Builder();

		Conditions conditions = builder.where(
			key, matcher
		).build();

		return aJsonObjectWith(conditions);
	}

	/**
	 * Returns a matcher that checks that a {@link JsonObject} is a valid json
	 * object by using different conditions.
	 *
	 * @return a matcher that checks that a {@code JsonObject} is a valid json
	 *         object.
	 * @review
	 */
	public static Matcher<JsonObject> aJsonObjectWith(Conditions conditions) {
		return conditions;
	}

	/**
	 * Returns a matcher that checks that an element is a string {@link
	 * JsonPrimitive}.
	 *
	 * @return a matcher that checks that an element is a string
	 * @review
	 */
	public static Matcher<JsonPrimitive> aJsonString(
		Matcher<String> stringMatcher) {

		return new IsJsonString(is(stringMatcher));
	}

	private JsonMatchers() {
		throw new UnsupportedOperationException();
	}

}