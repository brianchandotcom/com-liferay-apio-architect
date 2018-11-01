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

package com.liferay.apio.architect.internal.annotation.form;

import static java.util.Arrays.asList;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType;
import com.liferay.apio.architect.internal.annotation.representor.processor.TypeProcessor;
import com.liferay.apio.architect.internal.annotation.representor.types.DummyWithNested;
import com.liferay.apio.architect.internal.annotation.representor.types.DummyWithNested.NestedDummy;
import com.liferay.apio.architect.internal.form.JSONBodyImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Víctor Galán
 */
public class FormTransformerNestedTest {

	@BeforeClass
	public static void setUpClass() {
		HashMap<String, Object> nestedMap = new HashMap<String, Object>() {
			{
				put("stringField", "stringFieldValue");
				put("stringListField", asList("stringListFieldValue"));
			}
		};

		HashMap<String, Object> bodyMap = new HashMap<String, Object>() {
			{
				put("nestedDummy", nestedMap);
				put("nestedDummyList", asList(nestedMap));
			}
		};

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.valueToTree(bodyMap);

		Body body = new JSONBodyImpl(jsonNode);

		ParsedType parsedType = TypeProcessor.processType(
			DummyWithNested.class);

		Form<DummyWithNested> objectForm = FormTransformer.toForm(
			parsedType, __ -> "", __ -> Optional.of("something"));

		_dummyWithNested = objectForm.get(body);
	}

	@Test
	public void testNested() {
		NestedDummy nestedDummy = _dummyWithNested.getNestedDummy();

		assertThat(nestedDummy.getStringField(), is("stringFieldValue"));
		assertThat(
			nestedDummy.getStringListField(),
			is(asList("stringListFieldValue")));
	}

	@Test
	public void testNestedList() {
		List<NestedDummy> nestedDummyList =
			_dummyWithNested.getNestedDummyList();

		NestedDummy nestedDummy = nestedDummyList.get(0);

		assertThat(nestedDummy.getStringField(), is("stringFieldValue"));
		assertThat(
			nestedDummy.getStringListField(),
			is(asList("stringListFieldValue")));
	}

	private static DummyWithNested _dummyWithNested;

}