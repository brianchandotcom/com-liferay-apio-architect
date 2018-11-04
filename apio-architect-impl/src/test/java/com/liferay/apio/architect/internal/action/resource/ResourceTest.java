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

package com.liferay.apio.architect.internal.action.resource;

import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertEquals;

import com.liferay.apio.architect.internal.action.resource.Resource.Id;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;

import java.util.Optional;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class ResourceTest {

	@Test
	public void testIdOfCreatesValidId() {
		Id id = Id.of(42L, "42");

		assertThat(id.asObject(), is(42L));
		assertThat(id.asString(), is("42"));
		assertEquals(id, Id.of(42L, "42"));
	}

	@Test
	public void testItemOfCreatesValidResourceItem() {
		Item item = Item.of("name");

		assertThat(item.name(), is("name"));
		assertEquals(item, Item.of("name"));
	}

	@Test
	public void testItemOfWithIdCreatesValidResourceItem() {
		Item itemResource = Item.of("name", Id.of(42L, "42"));

		assertThat(itemResource.name(), is("name"));

		Optional<Id> optional = itemResource.id();

		assertThat(optional, is(optionalWithValue()));

		optional.ifPresent(id -> assertThat(id, is(Id.of(42L, "42"))));

		assertEquals(itemResource, Item.of("name"));
	}

	@Test
	public void testNestedOfCreatesValidResourceNested() {
		Item parent = Item.of("parent");

		Nested nested = Nested.of(parent, "name");

		assertThat(nested.name(), is("name"));
		assertThat(nested.parent(), is(parent));
		assertEquals(nested, Nested.of(Item.of("parent"), "name"));
	}

	@Test
	public void testPagedOfCreatesValidResourcePaged() {
		Paged paged = Paged.of("name");

		assertThat(paged.name(), is("name"));
		assertEquals(paged, Paged.of("name"));
	}

}