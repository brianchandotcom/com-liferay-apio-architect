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

package com.liferay.apio.architect.internal.routes;

import static com.liferay.apio.architect.internal.action.Predicates.isCreateAction;
import static com.liferay.apio.architect.internal.action.Predicates.isRetrieveAction;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.FORM_BUILDER_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.FORM_BUILDER_SUPPLIER;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.IDENTIFIER_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.IS_BATCH_CREATE_ACTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.PAGINATION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.filterActionSemantics;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.getParams;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.hasNestedAddingPermissionFunction;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.prependWith;
import static com.liferay.apio.architect.test.util.matcher.FailsWith.failsWith;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.batch.BatchResult;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;
import com.liferay.apio.architect.internal.routes.NestedCollectionRoutesImpl.BuilderImpl;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.single.model.SingleModel;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class NestedCollectionRoutesImplTest {

	@Before
	public void setUp() {
		_builder = new BuilderImpl<>(
			Nested.of(Item.of("parent"), "name"), FORM_BUILDER_SUPPLIER,
			IDENTIFIER_FUNCTION);
	}

	@Test
	public void testEmptyBuilderDoesNotGenerateActionSemantics() {
		NestedCollectionRoutes<String, Long, Long> nestedCollectionRoutes =
			_builder.build();

		assertThat(
			nestedCollectionRoutes,
			instanceOf(NestedCollectionRoutesImpl.class));

		NestedCollectionRoutesImpl<String, Long, Long>
			nestedCollectionRoutesImpl =
				(NestedCollectionRoutesImpl<String, Long, Long>)
					nestedCollectionRoutes;

		assertThat(
			nestedCollectionRoutesImpl.getActionSemantics(), is(empty()));
	}

	@Test
	public void testFiveParameterBuilderMethodsCreatesActionSemantics() {
		NestedCollectionRoutes<String, Long, Long> nestedCollectionRoutes =
			_builder.addCreator(
				this::_testAndReturnFourParameterCreatorRoute, String.class,
				Long.class, Boolean.class, Integer.class,
				hasNestedAddingPermissionFunction(), FORM_BUILDER_FUNCTION
			).addGetter(
				this::_testAndReturnFourParameterGetterRoute, String.class,
				Long.class, Boolean.class, Integer.class
			).build();

		assertThat(
			nestedCollectionRoutes,
			instanceOf(NestedCollectionRoutesImpl.class));

		_testActionSemantics(
			(NestedCollectionRoutesImpl<String, Long, Long>)
				nestedCollectionRoutes,
			asList(String.class, Long.class, Boolean.class, Integer.class));
	}

	@Test
	public void testFourParameterBuilderMethodsCreatesActionSemantics() {
		NestedCollectionRoutes<String, Long, Long> nestedCollectionRoutes =
			_builder.addCreator(
				this::_testAndReturnThreeParameterCreatorRoute, String.class,
				Long.class, Boolean.class, hasNestedAddingPermissionFunction(),
				FORM_BUILDER_FUNCTION
			).addGetter(
				this::_testAndReturnThreeParameterGetterRoute, String.class,
				Long.class, Boolean.class
			).build();

		assertThat(
			nestedCollectionRoutes,
			instanceOf(NestedCollectionRoutesImpl.class));

		_testActionSemantics(
			(NestedCollectionRoutesImpl<String, Long, Long>)
				nestedCollectionRoutes,
			asList(String.class, Long.class, Boolean.class));
	}

	@Test
	public void testNestedCollectionRoutesDeprecatedMethodsThrowsException() {
		NestedCollectionRoutes<String, Long, Long> nestedCollectionRoutes =
			_builder.build();

		assertThat(
			nestedCollectionRoutes::getFormOptional,
			failsWith(UnsupportedOperationException.class));

		assertThat(
			nestedCollectionRoutes::getNestedCreateItemFunctionOptional,
			failsWith(UnsupportedOperationException.class));

		assertThat(
			nestedCollectionRoutes::getNestedGetPageFunctionOptional,
			failsWith(UnsupportedOperationException.class));

		assertThat(
			nestedCollectionRoutes::getNestedBatchCreateItemFunctionOptional,
			failsWith(UnsupportedOperationException.class));
	}

	@Test
	public void testOneParameterBuilderMethodsCreatesActionSemantics() {
		NestedCollectionRoutes<String, Long, Long> nestedCollectionRoutes =
			_builder.addCreator(
				this::_testAndReturnNoParameterCreatorRoute,
				hasNestedAddingPermissionFunction(), FORM_BUILDER_FUNCTION
			).addGetter(
				this::_testAndReturnNoParameterGetterRoute
			).build();

		assertThat(
			nestedCollectionRoutes,
			instanceOf(NestedCollectionRoutesImpl.class));

		_testActionSemantics(
			(NestedCollectionRoutesImpl<String, Long, Long>)
				nestedCollectionRoutes,
			emptyList());
	}

	@Test
	public void testThreeParameterBuilderMethodsCreatesActionSemantics() {
		NestedCollectionRoutes<String, Long, Long> nestedCollectionRoutes =
			_builder.addCreator(
				this::_testAndReturnTwoParameterCreatorRoute, String.class,
				Long.class, hasNestedAddingPermissionFunction(),
				FORM_BUILDER_FUNCTION
			).addGetter(
				this::_testAndReturnTwoParameterGetterRoute, String.class,
				Long.class
			).build();

		assertThat(
			nestedCollectionRoutes,
			instanceOf(NestedCollectionRoutesImpl.class));

		_testActionSemantics(
			(NestedCollectionRoutesImpl<String, Long, Long>)
				nestedCollectionRoutes,
			asList(String.class, Long.class));
	}

	@Test
	public void testTwoParameterBuilderMethodsCreatesActionSemantics() {
		NestedCollectionRoutes<String, Long, Long> nestedCollectionRoutes =
			_builder.addCreator(
				this::_testAndReturnOneParameterCreatorRoute, String.class,
				hasNestedAddingPermissionFunction(), FORM_BUILDER_FUNCTION
			).addGetter(
				this::_testAndReturnOneParameterGetterRoute, String.class
			).build();

		assertThat(
			nestedCollectionRoutes,
			instanceOf(NestedCollectionRoutesImpl.class));

		_testActionSemantics(
			(NestedCollectionRoutesImpl<String, Long, Long>)
				nestedCollectionRoutes,
			singletonList(String.class));
	}

	private void _testActionSemantics(
		NestedCollectionRoutesImpl<String, Long, Long>
			nestedCollectionRoutesImpl,
		List<Class<?>> paramClasses) {

		List<ActionSemantics> actionSemantics =
			nestedCollectionRoutesImpl.getActionSemantics();

		assertThat(actionSemantics.size(), is(3));

		_testRetrieveActionSemantics(
			prependWith(paramClasses, Pagination.class, ParentId.class),
			filterActionSemantics(actionSemantics, isRetrieveAction));

		_testCreateActionSemantics(
			prependWith(paramClasses, ParentId.class, Body.class),
			filterActionSemantics(actionSemantics, isCreateAction));

		_testBatchCreateActionSemantics(
			prependWith(paramClasses, ParentId.class, Body.class),
			filterActionSemantics(actionSemantics, IS_BATCH_CREATE_ACTION));
	}

	private String _testAndReturnFourParameterCreatorRoute(
		Long parentId, Map<String, Object> body, String string, Long aLong,
		Boolean aBoolean, Integer integer) {

		assertThat(integer, is(2017));

		return _testAndReturnThreeParameterCreatorRoute(
			parentId, body, string, aLong, aBoolean);
	}

	private PageItems<String> _testAndReturnFourParameterGetterRoute(
		Pagination pagination, Long parentId, String string, Long aLong,
		Boolean aBoolean, Integer integer) {

		assertThat(integer, is(2017));

		return _testAndReturnThreeParameterGetterRoute(
			pagination, parentId, string, aLong, aBoolean);
	}

	private String _testAndReturnNoParameterCreatorRoute(
		Long parentId, Map<String, Object> body) {

		assertThat(parentId, is(21L));

		assertThat(body.get("key"), is("Apio"));

		return "Apio";
	}

	private PageItems<String> _testAndReturnNoParameterGetterRoute(
		Pagination pagination, Long parentId) {

		assertThat(pagination, is(PAGINATION));

		assertThat(parentId, is(21L));

		return new PageItems<>(singletonList("Apio"), 1);
	}

	private String _testAndReturnOneParameterCreatorRoute(
		Long parentId, Map<String, Object> body, String string) {

		assertThat(string, is("Apio"));

		return _testAndReturnNoParameterCreatorRoute(parentId, body);
	}

	private PageItems<String> _testAndReturnOneParameterGetterRoute(
		Pagination pagination, Long parentId, String string) {

		assertThat(string, is("Apio"));

		return _testAndReturnNoParameterGetterRoute(pagination, parentId);
	}

	private String _testAndReturnThreeParameterCreatorRoute(
		Long parentId, Map<String, Object> body, String string, Long aLong,
		Boolean aBoolean) {

		assertThat(aBoolean, is(true));

		return _testAndReturnTwoParameterCreatorRoute(
			parentId, body, string, aLong);
	}

	private PageItems<String> _testAndReturnThreeParameterGetterRoute(
		Pagination pagination, Long parentId, String string, Long aLong,
		Boolean aBoolean) {

		assertThat(aBoolean, is(true));

		return _testAndReturnTwoParameterGetterRoute(
			pagination, parentId, string, aLong);
	}

	private String _testAndReturnTwoParameterCreatorRoute(
		Long parentId, Map<String, Object> body, String string, Long aLong) {

		assertThat(aLong, is(42L));

		return _testAndReturnOneParameterCreatorRoute(parentId, body, string);
	}

	private PageItems<String> _testAndReturnTwoParameterGetterRoute(
		Pagination pagination, Long parentId, String string, Long aLong) {

		assertThat(aLong, is(42L));

		return _testAndReturnOneParameterGetterRoute(
			pagination, parentId, string);
	}

	private void _testBatchCreateActionSemantics(
		List<Class<?>> paramClasses, ActionSemantics actionSemantics) {

		assertThat(actionSemantics.method(), is("POST"));
		assertThat(actionSemantics.name(), is("batch-create"));
		assertThat(actionSemantics.paramClasses(), is(paramClasses));
		assertThat(
			actionSemantics.resource(),
			is(Nested.of(Item.of("parent"), "name")));
		assertThat(
			actionSemantics.returnClass(), is(equalTo(BatchResult.class)));
		assertThat(actionSemantics.annotations(), hasSize(0));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		BatchResult<?> batchResult = Try.of(
			() -> executeFunction.apply(getParams(paramClasses))
		).map(
			BatchResult.class::cast
		).get();

		assertThat(batchResult.getIdentifiers(), contains(42L, 42L));
	}

	private void _testCreateActionSemantics(
		List<Class<?>> paramClasses, ActionSemantics actionSemantics) {

		assertThat(actionSemantics.method(), is("POST"));
		assertThat(actionSemantics.name(), is("create"));
		assertThat(actionSemantics.paramClasses(), is(paramClasses));
		assertThat(
			actionSemantics.resource(),
			is(Nested.of(Item.of("parent"), "name")));
		assertThat(
			actionSemantics.returnClass(), is(equalTo(SingleModel.class)));
		assertThat(actionSemantics.annotations(), hasSize(0));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		SingleModel<?> singleModel = Try.of(
			() -> executeFunction.apply(getParams(paramClasses))
		).map(
			SingleModel.class::cast
		).get();

		assertThat(singleModel.getModel(), is("Apio"));
		assertThat(singleModel.getOperations(), is(empty()));
		assertThat(singleModel.getResourceName(), is("name"));
	}

	private void _testRetrieveActionSemantics(
		List<Class<?>> paramClasses, ActionSemantics actionSemantics) {

		assertThat(actionSemantics.method(), is("GET"));
		assertThat(actionSemantics.name(), is("retrieve"));
		assertThat(actionSemantics.paramClasses(), is(paramClasses));
		assertThat(
			actionSemantics.resource(),
			is(Nested.of(Item.of("parent"), "name")));
		assertThat(actionSemantics.returnClass(), is(equalTo(Page.class)));
		assertThat(actionSemantics.annotations(), hasSize(0));

		CheckedFunction1<List<?>, ?> executeFunction =
			actionSemantics.executeFunction();

		Page<?> page = Try.of(
			() -> executeFunction.apply(getParams(paramClasses))
		).map(
			Page.class::cast
		).get();

		assertThat(page.getItems(), contains("Apio"));
		assertThat(page.getItemsPerPage(), is(10));
		assertThat(page.getLastPageNumber(), is(1));
		assertThat(page.getOperations(), is(empty()));
		assertThat(page.getPageNumber(), is(1));
		assertThat(page.getPathOptional(), is(emptyOptional()));
		assertThat(page.getResourceName(), is("name"));
		assertThat(page.getTotalCount(), is(1));
	}

	private BuilderImpl<String, Long, Long> _builder;

}