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

package com.liferay.apio.architect.internal.action;

import static com.liferay.apio.architect.operation.HTTPMethod.GET;

import static java.lang.String.join;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertEquals;

import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.pagination.Page;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;

import java.util.Collections;
import java.util.List;

import org.immutables.value.Value.Immutable;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class ActionSemanticsTest {

	@Test
	public void testBuilderCreatesActionSemantics() throws Throwable {
		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"action"
		).method(
			GET
		).returns(
			Long.class
		).executeFunction(
			_join
		).receivesParams(
			String.class, Long.class
		).annotatedWith(
			_myAnnotation
		).build();

		assertThat(actionSemantics.annotations(), contains(_myAnnotation));
		assertThat(actionSemantics.name(), is("action"));
		assertThat(actionSemantics.method(), is("GET"));

		List<Class<?>> paramClasses = actionSemantics.paramClasses();

		assertThat(paramClasses, contains(String.class, Long.class));

		assertThat(actionSemantics.resource(), is(Paged.of("name")));
		assertThat(actionSemantics.returnClass(), is(equalTo(Long.class)));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		String result = (String)executeFunction.apply(asList("1", "2"));

		assertThat(result, is("1-2"));
	}

	@Test
	public void testBuilderWithoutAnnotationsCreatesActionSemantics()
		throws Throwable {

		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"action"
		).method(
			GET
		).returns(
			Void.class
		).executeFunction(
			_join
		).build();

		assertThat(actionSemantics.resource(), is(Paged.of("name")));
		assertThat(actionSemantics.name(), is("action"));
		assertThat(actionSemantics.method(), is("GET"));
		assertThat(actionSemantics.paramClasses(), is(empty()));
		assertThat(actionSemantics.returnClass(), is(equalTo(Void.class)));
		assertThat(actionSemantics.annotations(), is(empty()));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		String result = (String)executeFunction.apply(asList("1", "2"));

		assertThat(result, is("1-2"));
	}

	@Test
	public void testBuilderWithoutParamsCreatesActionSemantics()
		throws Throwable {

		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"action"
		).method(
			GET
		).returns(
			Long.class
		).executeFunction(
			_join
		).annotatedWith(
			_myAnnotation
		).build();

		assertThat(actionSemantics.resource(), is(Paged.of("name")));
		assertThat(actionSemantics.name(), is("action"));
		assertThat(actionSemantics.method(), is("GET"));
		assertThat(actionSemantics.paramClasses(), is(empty()));
		assertThat(actionSemantics.returnClass(), is(equalTo(Long.class)));
		assertThat(actionSemantics.annotations(), contains(_myAnnotation));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		String result = (String)executeFunction.apply(asList("1", "2"));

		assertThat(result, is("1-2"));
	}

	@Test
	public void testBuilderWithStringMethodCreatesActionSemantics()
		throws Throwable {

		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"action"
		).method(
			"POST"
		).returns(
			Void.class
		).executeFunction(
			_join
		).annotatedWith(
			_myAnnotation
		).build();

		assertThat(actionSemantics.resource(), is(Paged.of("name")));
		assertThat(actionSemantics.name(), is("action"));
		assertThat(actionSemantics.method(), is("POST"));
		assertThat(actionSemantics.paramClasses(), is(empty()));
		assertThat(actionSemantics.returnClass(), is(equalTo(Void.class)));
		assertThat(actionSemantics.annotations(), contains(_myAnnotation));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		String result = (String)executeFunction.apply(asList("1", "2"));

		assertThat(result, is("1-2"));
	}

	@Test
	public void testToActionTransformsAnActionSemanticsIntoAnAction() {
		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Resource.Paged.of("name")
		).name(
			"action"
		).method(
			GET
		).returns(
			String.class
		).executeFunction(
			_join
		).receivesParams(
			String.class, Long.class
		).annotatedWith(
			_myAnnotation
		).build();

		Action action = actionSemantics.toAction(
			(semantics, request, clazz) -> clazz.getSimpleName());

		Object object = action.apply(null);

		assertThat(object, is(instanceOf(Try.class)));

		@SuppressWarnings("unchecked")
		Try<String> stringTry = (Try<String>)object;

		assertThat(stringTry.get(), is("String-Long"));
	}

	@Test
	public void testWithAnnotationsReturnsActionSemanticsWithAnnotations() {
		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"retrieve"
		).method(
			"GET"
		).returns(
			Page.class
		).executeFunction(
			__ -> null
		).annotatedWith(
			_myAnnotation
		).build();

		ActionSemantics newActionSemantics = actionSemantics.withAnnotations(
			Collections.emptyList());

		assertEquals(
			actionSemantics,
			actionSemantics.withAnnotations(singletonList(_myAnnotation)));
		assertThat(actionSemantics.annotations(), contains(_myAnnotation));

		assertThat(newActionSemantics.annotations(), is(empty()));
	}

	@Test
	public void testWithMethodReturnsActionSemanticsWithDifferentMethod() {
		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"retrieve"
		).method(
			"GET"
		).returns(
			Page.class
		).executeFunction(
			__ -> null
		).build();

		ActionSemantics newActionSemantics = actionSemantics.withMethod(
			"DELETE");

		assertEquals(actionSemantics, actionSemantics.withMethod("GET"));
		assertThat(actionSemantics.method(), is("GET"));

		assertThat(newActionSemantics.method(), is("DELETE"));
	}

	@Test
	public void testWithNameReturnsActionSemanticsWithDifferentName() {
		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"retrieve"
		).method(
			"GET"
		).returns(
			Page.class
		).executeFunction(
			__ -> null
		).build();

		ActionSemantics newActionSemantics = actionSemantics.withName("create");

		assertEquals(actionSemantics, actionSemantics.withName("retrieve"));
		assertThat(actionSemantics.name(), is("retrieve"));

		assertThat(newActionSemantics.name(), is("create"));
	}

	@Test
	public void testWithResourceReturnsActionSemanticsWithDifferentResource() {
		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"retrieve"
		).method(
			"GET"
		).returns(
			Page.class
		).executeFunction(
			__ -> null
		).build();

		ActionSemantics newActionSemantics = actionSemantics.withResource(
			Item.of("name"));

		assertEquals(
			actionSemantics, actionSemantics.withResource(Paged.of("name")));
		assertThat(actionSemantics.resource(), is(Paged.of("name")));

		assertThat(newActionSemantics.resource(), is(Item.of("name")));
	}

	@Test
	public void testWithReturnClassReturnsActionSemanticsWithReturnClass() {
		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"retrieve"
		).method(
			"GET"
		).returns(
			Page.class
		).executeFunction(
			__ -> null
		).build();

		ActionSemantics newActionSemantics = actionSemantics.withReturnClass(
			Void.class);

		assertEquals(
			actionSemantics, actionSemantics.withReturnClass(Page.class));
		assertThat(actionSemantics.returnClass(), is(equalTo(Page.class)));

		assertThat(newActionSemantics.returnClass(), is(equalTo(Void.class)));
	}

	@Immutable(singleton = true)
	public static @interface MyAnnotation {
	}

	@SuppressWarnings("unchecked")
	private static final CheckedFunction1<List<?>, Object> _join = list -> join(
		"-", (List<String>)list);

	private static final MyAnnotation _myAnnotation =
		ImmutableMyAnnotation.of();

}