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

package com.liferay.apio.architect.internal.body;

import static com.liferay.apio.architect.internal.body.JSONToBodyConverter.jsonToBody;

import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.liferay.apio.architect.form.Body;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Matcher;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Alejandro Hernández
 * @review
 */
public class JSONToBodyConverterTest {

	@Before
	public void setUp() {
		_request = mock(HttpServletRequest.class);
	}

	@Test
	public void testTransformingJSONArrayIntoBody() throws IOException {
		InputStream inputStream = _getInputStream("/body/json-body-2.json");

		when(
			_request.getInputStream()
		).thenReturn(
			new MockServletInputStream(inputStream)
		);

		Body body = jsonToBody(_request);

		Optional<List<Body>> optional = body.getBodyMembersOptional();

		assertThat(optional, is(optionalWithValue()));

		optional.ifPresent(
			list -> {
				_testLanguage(list.get(0), "Spanish", "es-ES", "Apio");
				_testLanguage(list.get(1), "English", "en", "Celery");
			});
	}

	@Test
	public void testTransformingJSONObjectIntoBody() throws IOException {
		InputStream inputStream = _getInputStream("/body/json-body-1.json");

		when(
			_request.getInputStream()
		).thenReturn(
			new MockServletInputStream(inputStream)
		);

		Body body = jsonToBody(_request);

		_assertValue(body, "kingdom", "Plantae");
		_assertValue(body, "genus", "Apium");

		Matcher<Iterable<? extends String>> containingVariants = contains(
			"Apium graveolens var. graveolens",
			"Apium graveolens var. rapaceum",
			"Apium graveolens var. secalinum");

		assertThat(
			body.getValueListOptional("variants"),
			is(optionalWithValue(containingVariants)));

		Optional<Body> optionalNameBody = body.getNestedBodyOptional("name");

		assertThat(optionalNameBody, is(optionalWithValue()));
		optionalNameBody.ifPresent(
			nameBody -> {
				_assertValue(nameBody, "spanish", "Apio");
				_assertValue(nameBody, "english", "Celery");
			});

		Optional<List<Body>> optionalCultivationBody =
			body.getNestedBodyListOptional("cultivation");

		assertThat(optionalCultivationBody, is(optionalWithValue()));

		optionalCultivationBody.ifPresent(
			list -> {
				_testCultivation(list.get(0), "North America", "Pascal");
				_testCultivation(list.get(1), "Europe", "Celeriac");
				_testCultivation(list.get(2), "Asia", "Leaf");
			});
	}

	private void _assertValue(Body body, String key, String value) {
		Optional<String> optional = body.getValueOptional(key);

		assertThat(optional, is(optionalWithValue(equalTo(value))));
	}

	private InputStream _getInputStream(String name) {
		return getClass().getResourceAsStream(name);
	}

	private void _testCultivation(Body body, String region, String typeName) {
		_assertValue(body, "region", region);

		Optional<Body> optional = body.getNestedBodyOptional("type");

		assertThat(optional, is(optionalWithValue()));

		optional.ifPresent(
			typeBody -> _assertValue(typeBody, "name", typeName));
	}

	private void _testLanguage(
		Body body, String language, String code, String name) {

		_assertValue(body, "language", language);
		_assertValue(body, "code", code);
		_assertValue(body, "name", name);
	}

	private HttpServletRequest _request;

}