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

package com.liferay.apio.architect.internal.message.json.ld;

import static com.liferay.apio.architect.internal.message.json.ld.JSONLDMessageMapperUtil.getActionId;
import static com.liferay.apio.architect.internal.message.json.ld.JSONLDMessageMapperUtil.getActionTypes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import com.liferay.apio.architect.resource.Resource.Item;
import com.liferay.apio.architect.resource.Resource.Nested;
import com.liferay.apio.architect.resource.Resource.Paged;
import com.liferay.apio.architect.test.util.internal.util.DescriptionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.List;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class JSONLDMessageMapperUtilTest {

	@Test(expected = UnsupportedOperationException.class)
	public void testConstructorThrowsException() throws Throwable {
		Constructor<?> constructor =
			DescriptionUtil.class.getDeclaredConstructors()[0];

		constructor.setAccessible(true);

		try {
			constructor.newInstance();
		}
		catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
	}

	@Test
	public void testGetActionId() {
		Paged paged = Paged.of("paged");

		Item item = Item.of("item");

		Nested nested = Nested.of(item, "nested");

		String pagedId = getActionId(paged, "retrieve");
		String itemId = getActionId(item, "remove");
		String nestedId = getActionId(nested, "create");

		assertThat(pagedId, is("_:paged/retrieve"));
		assertThat(itemId, is("_:item/remove"));
		assertThat(nestedId, is("_:item/nested/create"));
	}

	@Test
	public void testGetActionType() {
		List<String> retrieveTypes = getActionTypes("retrieve");
		List<String> createTypes = getActionTypes("create");
		List<String> removeTypes = getActionTypes("remove");
		List<String> replaceTypes = getActionTypes("replace");
		List<String> customActionTypes = getActionTypes("subscribe");

		assertThat(retrieveTypes, contains("Operation"));
		assertThat(createTypes, contains("CreateAction", "Operation"));
		assertThat(removeTypes, contains("DeleteAction", "Operation"));
		assertThat(replaceTypes, contains("ReplaceAction", "Operation"));
		assertThat(customActionTypes, contains("Operation"));
	}

}