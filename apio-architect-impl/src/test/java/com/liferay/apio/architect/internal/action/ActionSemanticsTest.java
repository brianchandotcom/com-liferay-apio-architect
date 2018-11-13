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

import static com.liferay.apio.architect.internal.action.ActionSemantics.toAction;
import static com.liferay.apio.architect.internal.action.Predicates.isRootCollectionAction;
import static com.liferay.apio.architect.operation.HTTPMethod.GET;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static java.lang.String.join;

import static java.util.Collections.emptyList;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;

import com.liferay.apio.architect.annotation.Actions.EntryPoint;
import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.pagination.Page;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;

import java.lang.annotation.Annotation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.hamcrest.Matcher;

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
		).receivesParams(
			String.class, Long.class
		).returns(
			Long.class
		).annotatedWith(
			_myAnnotation
		).executeFunction(
			_join
		).build();

		assertThat(actionSemantics.resource(), is(Paged.of("name")));
		assertThat(actionSemantics.name(), is("action"));
		assertThat(actionSemantics.method(), is("GET"));
		assertThat(
			actionSemantics.paramClasses(), contains(String.class, Long.class));
		assertThat(actionSemantics.returnClass(), is(equalTo(Long.class)));
		assertThat(actionSemantics.annotations(), contains(_myAnnotation));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		String result = (String)executeFunction.apply(Arrays.asList("1", "2"));

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
		).receivesNoParams(
		).returnsNothing(
		).notAnnotated(
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

		String result = (String)executeFunction.apply(Arrays.asList("1", "2"));

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
		).receivesNoParams(
		).returns(
			Long.class
		).annotatedWith(
			_myAnnotation
		).executeFunction(
			_join
		).build();

		assertThat(actionSemantics.resource(), is(Paged.of("name")));
		assertThat(actionSemantics.name(), is("action"));
		assertThat(actionSemantics.method(), is("GET"));
		assertThat(actionSemantics.paramClasses(), is(empty()));
		assertThat(actionSemantics.returnClass(), is(equalTo(Long.class)));
		assertThat(actionSemantics.annotations(), contains(_myAnnotation));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		String result = (String)executeFunction.apply(Arrays.asList("1", "2"));

		assertThat(result, is("1-2"));
	}

	@Test
	public void testBuilderWithoutReturnCreatesActionSemantics()
		throws Throwable {

		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Paged.of("name")
		).name(
			"action"
		).method(
			GET
		).receivesNoParams(
		).returnsNothing(
		).annotatedWith(
			_myAnnotation
		).executeFunction(
			_join
		).build();

		assertThat(actionSemantics.resource(), is(Paged.of("name")));
		assertThat(actionSemantics.name(), is("action"));
		assertThat(actionSemantics.method(), is("GET"));
		assertThat(actionSemantics.paramClasses(), is(empty()));
		assertThat(actionSemantics.returnClass(), is(equalTo(Void.class)));
		assertThat(actionSemantics.annotations(), contains(_myAnnotation));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		String result = (String)executeFunction.apply(Arrays.asList("1", "2"));

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
		).receivesNoParams(
		).returnsNothing(
		).annotatedWith(
			_myAnnotation
		).executeFunction(
			_join
		).build();

		assertThat(actionSemantics.resource(), is(Paged.of("name")));
		assertThat(actionSemantics.name(), is("action"));
		assertThat(actionSemantics.method(), is("POST"));
		assertThat(actionSemantics.paramClasses(), is(empty()));
		assertThat(actionSemantics.returnClass(), is(equalTo(Void.class)));
		assertThat(actionSemantics.annotations(), contains(_myAnnotation));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		String result = (String)executeFunction.apply(Arrays.asList("1", "2"));

		assertThat(result, is("1-2"));
	}

	@Test
	public void testFilterFromEmptyStreamReturnsNone() {
		Optional<ActionSemantics> optional = ActionSemantics.filter(
			Stream.empty()
		).forResource(
			Resource.Paged.of("name")
		).withPredicate(
			__ -> true
		);

		assertThat(optional, is(emptyOptional()));
	}

	@Test
	public void testFilterReturnsCorrectActionSemantics() {
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

		Optional<ActionSemantics> optional = ActionSemantics.filter(
			stream
		).forResource(
			Resource.Paged.of("name")
		).withPredicate(
			isRootCollectionAction
		);

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

	@Test
	public void testToActionTransformsAnActionSemanticsIntoAnAction() {
		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			Resource.Paged.of("name")
		).name(
			"action"
		).method(
			GET
		).receivesParams(
			String.class, Long.class
		).returns(
			String.class
		).annotatedWith(
			_myAnnotation
		).executeFunction(
			_join
		).build();

		Function<ActionSemantics, Action> toActionFunction = toAction(
			__ -> Class::getSimpleName);

		Action action = toActionFunction.apply(actionSemantics);

		Object object = action.apply(null);

		assertThat(object, is(instanceOf(Try.class)));

		@SuppressWarnings("unchecked")
		Try<String> stringTry = (Try<String>)object;

		assertThat(stringTry.get(), is("String-Long"));
	}

	@Immutable(singleton = true)
	public static @interface MyAnnotation {
	}

	@SuppressWarnings("unchecked")
	private static final Matcher<List<? extends Annotation>> _hasEntryPoint =
		both(hasSize(1)).and((Matcher)hasItem(instanceOf(EntryPoint.class)));

	@SuppressWarnings("unchecked")
	private static final CheckedFunction1<List<?>, Object> _join = list -> join(
		"-", (List<String>)list);

	private static final MyAnnotation _myAnnotation =
		ImmutableMyAnnotation.of();

}