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

package com.liferay.apio.architect.internal.action.converter;

import static com.liferay.apio.architect.internal.action.converter.PagedResourceConverter.filterRetrieveActionFor;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static java.util.Collections.emptyList;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.liferay.apio.architect.annotation.Actions.EntryPoint;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.ImmutableActionSemantics;
import com.liferay.apio.architect.internal.action.ImmutableEntryPoint;
import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.pagination.Page;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.hamcrest.Matcher;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class PagedResourceConverterTest {

	@Test(expected = UnsupportedOperationException.class)
	public void testConstructorThrowsException() throws Throwable {
		Constructor<?> constructor =
			PagedResourceConverter.class.getDeclaredConstructors()[0];

		constructor.setAccessible(true);

		try {
			constructor.newInstance();
		}
		catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
	}

	@Test
	public void testFilterRetrieveActionForFromEmptyStreamReturnsNone() {
		Stream<ActionSemantics> stream = Stream.empty();

		Optional<ActionSemantics> optional = filterRetrieveActionFor(
			Resource.Paged.of("name"), stream);

		assertThat(optional, is(emptyOptional()));
	}

	@Test
	public void testFilterRetrieveActionForReturnsIfPresent() {
		ImmutableActionSemantics immutableActionSemantics =
			(ImmutableActionSemantics)ActionSemantics.ofResource(
				Resource.Paged.of("name")
			).name(
				"retrieve"
			).method(
				"GET"
			).receivesNoParams(
			).returns(
				Page.class
			).annotatedWith(
				ImmutableEntryPoint.builder().build()
			).executeFunction(
				__ -> null
			).build();

		Stream<ActionSemantics> stream = Stream.of(
			immutableActionSemantics,
			immutableActionSemantics.withName("create"),
			immutableActionSemantics.withAnnotations(emptyList()),
			immutableActionSemantics.withResource(Resource.Paged.of("name2")),
			immutableActionSemantics.withReturnClass(String.class),
			immutableActionSemantics.withResource(Resource.Item.of("item")),
			immutableActionSemantics.withMethod("PUT"),
			immutableActionSemantics.withResource(Resource.Paged.of("name3")));

		Optional<ActionSemantics> optional = filterRetrieveActionFor(
			Resource.Paged.of("name"), stream);

		assertThat(optional, is(optionalWithValue()));

		optional.ifPresent(
			actionSemantics -> {
				assertThat(actionSemantics.method(), is("GET"));
				assertThat(actionSemantics.name(), is("retrieve"));
				assertThat(actionSemantics.annotations(), hasSize(1));
				assertThat(actionSemantics.annotations(), _hasEntryPoint);
				assertThat(
					actionSemantics.returnClass(), is(equalTo(Page.class)));
				assertThat(
					actionSemantics.resource(), is(Resource.Paged.of("name")));
			});
	}

	@SuppressWarnings("unchecked")
	private static final Matcher<List<? extends Annotation>> _hasEntryPoint =
		both(hasSize(1)).and((Matcher)hasItem(instanceOf(EntryPoint.class)));

}