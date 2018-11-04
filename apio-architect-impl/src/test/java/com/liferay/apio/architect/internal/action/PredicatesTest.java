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

import static com.liferay.apio.architect.internal.action.Predicates.areEquals;
import static com.liferay.apio.architect.internal.action.Predicates.hasAnnotation;
import static com.liferay.apio.architect.internal.action.Predicates.isAction;
import static com.liferay.apio.architect.internal.action.Predicates.isActionBy;
import static com.liferay.apio.architect.internal.action.Predicates.isActionByDELETE;
import static com.liferay.apio.architect.internal.action.Predicates.isActionByGET;
import static com.liferay.apio.architect.internal.action.Predicates.isActionByPOST;
import static com.liferay.apio.architect.internal.action.Predicates.isActionByPUT;
import static com.liferay.apio.architect.internal.action.Predicates.isActionFor;
import static com.liferay.apio.architect.internal.action.Predicates.isActionNamed;
import static com.liferay.apio.architect.internal.action.Predicates.isCreateAction;
import static com.liferay.apio.architect.internal.action.Predicates.isRemoveAction;
import static com.liferay.apio.architect.internal.action.Predicates.isReplaceAction;
import static com.liferay.apio.architect.internal.action.Predicates.isRetrieveAction;
import static com.liferay.apio.architect.internal.action.Predicates.isRootCollectionAction;
import static com.liferay.apio.architect.internal.action.Predicates.returnsAnyOf;

import static java.util.Collections.singletonList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.liferay.apio.architect.annotation.Actions.EntryPoint;
import com.liferay.apio.architect.annotation.Actions.Retrieve;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.pagination.Page;

import java.util.List;
import java.util.function.Predicate;

import org.immutables.value.Value.Include;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
@Include(EntryPoint.class)
public class PredicatesTest {

	@Test
	public void testEquals() {
		List<String> list = singletonList("Apio");

		Predicate<List<String>> truePredicate = areEquals(List::size, 1);
		Predicate<List<String>> falsePredicate = areEquals(List::size, 2);

		assertTrue(truePredicate.test(list));
		assertFalse(falsePredicate.test(list));
	}

	@Test
	public void testHasAnnotation() {
		Predicate<ActionSemantics> truePredicate = hasAnnotation(
			EntryPoint.class);
		Predicate<ActionSemantics> falsePredicate = hasAnnotation(
			Retrieve.class);

		assertTrue(truePredicate.test(_actionSemantics));
		assertFalse(falsePredicate.test(_actionSemantics));
	}

	@Test
	public void testIsAction() {
		Predicate<ActionSemantics> truePredicate = isAction("name", "GET");
		Predicate<ActionSemantics> falsePredicate = isAction("name", "DELETE");
		Predicate<ActionSemantics> anotherFalsePredicate = isAction(
			"retrieve", "GET");

		assertTrue(truePredicate.test(_actionSemantics));
		assertFalse(falsePredicate.test(_actionSemantics));
		assertFalse(anotherFalsePredicate.test(_actionSemantics));
	}

	@Test
	public void testIsActionBy() {
		assertTrue(isActionBy("GET").test(_actionSemantics));
		assertFalse(isActionBy("DELETE").test(_actionSemantics));
	}

	@Test
	public void testIsActionByDELETE() {
		assertTrue(isActionByDELETE.test(_withMethod("DELETE")));
		assertFalse(isActionByDELETE.test(_actionSemantics));
	}

	@Test
	public void testIsActionByGET() {
		assertTrue(isActionByGET.test(_actionSemantics));
		assertFalse(isActionByGET.test(_withMethod("DELETE")));
	}

	@Test
	public void testIsActionByPOST() {
		assertTrue(isActionByPOST.test(_withMethod("POST")));
		assertFalse(isActionByPOST.test(_actionSemantics));
	}

	@Test
	public void testIsActionByPUT() {
		assertTrue(isActionByPUT.test(_withMethod("PUT")));
		assertFalse(isActionByPUT.test(_actionSemantics));
	}

	@Test
	public void testIsActionForResource() {
		Predicate<ActionSemantics> truePredicate = isActionFor(
			Paged.of("Name"));

		Predicate<ActionSemantics> falsePredicate = isActionFor(
			Paged.of("NotName"));

		assertTrue(truePredicate.test(_actionSemantics));
		assertFalse(falsePredicate.test(_actionSemantics));
	}

	@Test
	public void testIsActionForResourceClass() {
		Predicate<ActionSemantics> truePredicate = isActionFor(Paged.class);
		Predicate<ActionSemantics> falsePredicate = isActionFor(Item.class);

		assertTrue(truePredicate.test(_actionSemantics));
		assertFalse(falsePredicate.test(_actionSemantics));
	}

	@Test
	public void testIsActionNamed() {
		Predicate<ActionSemantics> truePredicate = isActionNamed("name");
		Predicate<ActionSemantics> falsePredicate = isActionNamed("notName");

		assertTrue(truePredicate.test(_actionSemantics));
		assertFalse(falsePredicate.test(_actionSemantics));
	}

	@Test
	public void testIsCreateAction() {
		assertTrue(isCreateAction.test(_withAction("POST", "create")));
		assertFalse(isCreateAction.test(_actionSemantics));
	}

	@Test
	public void testIsRemoveAction() {
		assertTrue(isRemoveAction.test(_withAction("DELETE", "remove")));
		assertFalse(isRemoveAction.test(_actionSemantics));
	}

	@Test
	public void testIsReplaceAction() {
		assertTrue(isReplaceAction.test(_withAction("PUT", "replace")));
		assertFalse(isReplaceAction.test(_actionSemantics));
	}

	@Test
	public void testIsRetrieveAction() {
		assertTrue(isRetrieveAction.test(_withAction("GET", "retrieve")));
		assertFalse(isRetrieveAction.test(_actionSemantics));
	}

	@Test
	public void testIsRootCollectionAction() {
		assertTrue(isRootCollectionAction.test(_withAction("GET", "retrieve")));
		assertFalse(isRootCollectionAction.test(_actionSemantics));
	}

	@Test
	public void testReturnsAnyOf() {
		Predicate<ActionSemantics> truePredicate = returnsAnyOf(
			String.class, Page.class);
		Predicate<ActionSemantics> anotherTruePredicate = returnsAnyOf(
			Character.class, Page.class);
		Predicate<ActionSemantics> falsePredicate = returnsAnyOf(Void.class);

		assertTrue(truePredicate.test(_actionSemantics));
		assertTrue(anotherTruePredicate.test(_actionSemantics));
		assertFalse(falsePredicate.test(_actionSemantics));
	}

	private static ImmutableActionSemantics _withAction(
		String method, String action) {

		ImmutableActionSemantics immutableActionSemantics = _withMethod(method);

		return immutableActionSemantics.withName(action);
	}

	private static ImmutableActionSemantics _withMethod(String method) {
		ImmutableActionSemantics immutableActionSemantics =
			(ImmutableActionSemantics)_actionSemantics;

		return immutableActionSemantics.withMethod(method);
	}

	private static final ActionSemantics _actionSemantics =
		ActionSemantics.ofResource(
			Paged.of("Name")
		).name(
			"name"
		).method(
			"GET"
		).receivesParams(
			String.class, Integer.class
		).returns(
			Page.class
		).annotatedWith(
			ImmutableEntryPoint.builder().build()
		).executeFunction(
			__ -> null
		).build();

}