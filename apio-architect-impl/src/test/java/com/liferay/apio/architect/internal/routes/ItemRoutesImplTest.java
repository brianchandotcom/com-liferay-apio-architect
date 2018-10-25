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

import static com.liferay.apio.architect.internal.annotation.ActionKey.ANY_ROUTE;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.FORM_BUILDER_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.HAS_REMOVE_PERMISSION_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.HAS_UPDATE_PERMISSION_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.IDENTIFIER_TO_PATH_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.REQUEST_PROVIDE_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.keyValueFrom;
import static com.liferay.apio.architect.operation.HTTPMethod.DELETE;
import static com.liferay.apio.architect.operation.HTTPMethod.GET;
import static com.liferay.apio.architect.operation.HTTPMethod.PUT;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

import com.liferay.apio.architect.alias.routes.UpdateItemFunction;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.internal.routes.ItemRoutesImpl.BuilderImpl;
import com.liferay.apio.architect.operation.Operation;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.ItemRoutes.Builder;
import com.liferay.apio.architect.single.model.SingleModel;

import io.vavr.control.Either;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.NotFoundException;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class ItemRoutesImplTest extends BaseRoutesTest {

	@Test(expected = NotFoundException.class)
	public void testEmptyBuilderBuildsEmptyRoutes() {
		Builder<String, Long> builder = new BuilderImpl<>(
			"name", REQUEST_PROVIDE_FUNCTION,
			__ -> {
			},
			__ -> null, IDENTIFIER_TO_PATH_FUNCTION,
			__ -> Optional.empty(), actionManager);

		ItemRoutes<String, Long> itemRoutes = builder.build();

		assertThat(itemRoutes, is(notNullValue()));

		Either<Action.Error, Action> actionEither = actionManager.getAction(
			GET.name(), asList("name", ANY_ROUTE));

		assertThat(actionEither.isRight(), is(true));

		Action action = actionEither.get();

		Object result = action.apply(null);

		assertThat(result, is(nullValue()));
	}

	@Test(expected = NotFoundException.class)
	public void testEmptyBuilderBuildsEmptyRoutes1() {
		Builder<String, Long> builder = new BuilderImpl<>(
			"name", REQUEST_PROVIDE_FUNCTION,
			__ -> {
			},
			__ -> null, IDENTIFIER_TO_PATH_FUNCTION,
			__ -> Optional.empty(), actionManager);

		ItemRoutes<String, Long> itemRoutes = builder.build();

		assertThat(itemRoutes, is(notNullValue()));

		Either<Action.Error, Action> actionEither = actionManager.getAction(
			DELETE.name(), asList("name", ANY_ROUTE));

		assertThat(actionEither.isRight(), is(true));

		Action action = actionEither.get();

		Object result = action.apply(null);

		assertThat(result, is(nullValue()));
	}

	@Test
	public void testFiveParameterBuilderMethodsCreatesValidRoutes() {
		Set<String> neededProviders = new TreeSet<>();

		Builder<String, Long> builder = new BuilderImpl<>(
			"name", REQUEST_PROVIDE_FUNCTION, neededProviders::add, __ -> null,
			IDENTIFIER_TO_PATH_FUNCTION, __ -> Optional.empty(), actionManager);

		ItemRoutes<String, Long> itemRoutes = builder.addGetter(
			this::_testAndReturnFourParameterGetterRoute, String.class,
			Long.class, Boolean.class, Integer.class
		).addRemover(
			this::_testFourParameterRemoverRoute, String.class, Long.class,
			Boolean.class, Integer.class, HAS_REMOVE_PERMISSION_FUNCTION
		).addUpdater(
			this::_testAndReturnFourParameterUpdaterRoute, String.class,
			Long.class, Boolean.class, Integer.class,
			HAS_UPDATE_PERMISSION_FUNCTION, FORM_BUILDER_FUNCTION
		).build();

		assertThat(
			neededProviders,
			contains(
				Boolean.class.getName(), Integer.class.getName(),
				Long.class.getName(), String.class.getName()));

		_testItemRoutes(itemRoutes);
	}

	@Test
	public void testFourParameterBuilderMethodsCreatesValidRoutes() {
		Set<String> neededProviders = new TreeSet<>();

		Builder<String, Long> builder = new BuilderImpl<>(
			"name", REQUEST_PROVIDE_FUNCTION, neededProviders::add, __ -> null,
			IDENTIFIER_TO_PATH_FUNCTION, __ -> Optional.empty(), actionManager);

		ItemRoutes<String, Long> itemRoutes = builder.addGetter(
			this::_testAndReturnThreeParameterGetterRoute, String.class,
			Long.class, Boolean.class
		).addRemover(
			this::_testThreeParameterRemoverRoute, String.class, Long.class,
			Boolean.class, HAS_REMOVE_PERMISSION_FUNCTION
		).addUpdater(
			this::_testAndReturnThreeParameterUpdaterRoute, String.class,
			Long.class, Boolean.class, HAS_UPDATE_PERMISSION_FUNCTION,
			FORM_BUILDER_FUNCTION
		).build();

		assertThat(
			neededProviders,
			contains(
				Boolean.class.getName(), Long.class.getName(),
				String.class.getName()));

		_testItemRoutes(itemRoutes);
	}

	@Test
	public void testOneParameterBuilderMethodsCreatesValidRoutes() {
		Set<String> neededProviders = new TreeSet<>();

		Builder<String, Long> builder = new BuilderImpl<>(
			"name", REQUEST_PROVIDE_FUNCTION, neededProviders::add, __ -> null,
			IDENTIFIER_TO_PATH_FUNCTION, __ -> Optional.empty(), actionManager);

		ItemRoutes<String, Long> itemRoutes = builder.addGetter(
			this::_testAndReturnNoParameterGetterRoute
		).addRemover(
			this::_testAndReturnNoParameterRemoverRoute,
			HAS_REMOVE_PERMISSION_FUNCTION
		).addUpdater(
			this::_testAndReturnNoParameterUpdaterRoute,
			HAS_UPDATE_PERMISSION_FUNCTION, FORM_BUILDER_FUNCTION
		).build();

		assertThat(neededProviders.size(), is(0));

		_testItemRoutes(itemRoutes);
	}

	@Test
	public void testThreeParameterBuilderMethodsCreatesValidRoutes() {
		Set<String> neededProviders = new TreeSet<>();

		Builder<String, Long> builder = new BuilderImpl<>(
			"name", REQUEST_PROVIDE_FUNCTION, neededProviders::add, __ -> null,
			IDENTIFIER_TO_PATH_FUNCTION, __ -> Optional.empty(), actionManager);

		ItemRoutes<String, Long> itemRoutes = builder.addGetter(
			this::_testAndReturnTwoParameterGetterRoute, String.class,
			Long.class
		).addRemover(
			this::_testTwoParameterRemoverRoute, String.class, Long.class,
			HAS_REMOVE_PERMISSION_FUNCTION
		).addUpdater(
			this::_testAndReturnTwoParameterUpdaterRoute, String.class,
			Long.class, HAS_UPDATE_PERMISSION_FUNCTION, FORM_BUILDER_FUNCTION
		).build();

		assertThat(
			neededProviders,
			contains(Long.class.getName(), String.class.getName()));

		_testItemRoutes(itemRoutes);
	}

	@Test
	public void testTwoParameterBuilderMethodsCreatesValidRoutes() {
		Set<String> neededProviders = new TreeSet<>();

		Builder<String, Long> builder = new BuilderImpl<>(
			"name", REQUEST_PROVIDE_FUNCTION, neededProviders::add, __ -> null,
			IDENTIFIER_TO_PATH_FUNCTION, __ -> Optional.empty(), actionManager);

		ItemRoutes<String, Long> itemRoutes = builder.addGetter(
			this::_testAndReturnOneParameterGetterRoute, String.class
		).addRemover(
			this::_testOneParameterRemoverRoute, String.class,
			HAS_REMOVE_PERMISSION_FUNCTION
		).addUpdater(
			this::_testAndReturnOneParameterUpdaterRoute, String.class,
			HAS_UPDATE_PERMISSION_FUNCTION, FORM_BUILDER_FUNCTION
		).build();

		assertThat(neededProviders, contains(String.class.getName()));

		_testItemRoutes(itemRoutes);
	}

	private String _testAndReturnFourParameterGetterRoute(
		Long identifier, String string, Long aLong, Boolean aBoolean,
		Integer integer) {

		assertThat(integer, is(2017));

		return _testAndReturnThreeParameterGetterRoute(
			identifier, string, aLong, aBoolean);
	}

	private String _testAndReturnFourParameterUpdaterRoute(
		Long identifier, Map<String, Object> body, String string, Long aLong,
		Boolean aBoolean, Integer integer) {

		assertThat(integer, is(2017));

		return _testAndReturnThreeParameterUpdaterRoute(
			identifier, body, string, aLong, aBoolean);
	}

	private String _testAndReturnNoParameterGetterRoute(Long identifier) {
		assertThat(identifier, is(42L));

		return "Apio";
	}

	private void _testAndReturnNoParameterRemoverRoute(Long identifier) {
		assertThat(identifier, is(42L));
	}

	private String _testAndReturnNoParameterUpdaterRoute(
		Long identifier, Map<String, Object> body) {

		assertThat(identifier, is(42L));

		assertThat(body.get("key"), is(keyValueFrom(_body)));

		return "Updated";
	}

	private String _testAndReturnOneParameterGetterRoute(
		Long identifier, String string) {

		assertThat(string, is("Apio"));

		return _testAndReturnNoParameterGetterRoute(identifier);
	}

	private String _testAndReturnOneParameterUpdaterRoute(
		Long identifier, Map<String, Object> body, String string) {

		assertThat(string, is("Apio"));

		return _testAndReturnNoParameterUpdaterRoute(identifier, body);
	}

	private String _testAndReturnThreeParameterGetterRoute(
		Long identifier, String string, Long aLong, Boolean aBoolean) {

		assertThat(aBoolean, is(true));

		return _testAndReturnTwoParameterGetterRoute(identifier, string, aLong);
	}

	private String _testAndReturnThreeParameterUpdaterRoute(
		Long identifier, Map<String, Object> body, String string, Long aLong,
		Boolean aBoolean) {

		assertThat(aBoolean, is(true));

		return _testAndReturnTwoParameterUpdaterRoute(
			identifier, body, string, aLong);
	}

	private String _testAndReturnTwoParameterGetterRoute(
		Long identifier, String string, Long aLong) {

		assertThat(aLong, is(42L));

		return _testAndReturnOneParameterGetterRoute(identifier, string);
	}

	private String _testAndReturnTwoParameterUpdaterRoute(
		Long identifier, Map<String, Object> body, String string, Long aLong) {

		assertThat(aLong, is(42L));

		return _testAndReturnOneParameterUpdaterRoute(identifier, body, string);
	}

	private void _testFourParameterRemoverRoute(
		Long identifier, String string, Long aLong, Boolean aBoolean,
		Integer integer) {

		assertThat(integer, is(2017));

		_testThreeParameterRemoverRoute(identifier, string, aLong, aBoolean);
	}

	private void _testItemRoutes(ItemRoutes<String, Long> itemRoutes) {
		_testItemRoutesGetter();

		_testItemRoutesUpdater(itemRoutes);

		_testItemRoutesDeleter();
	}

	private void _testItemRoutesDeleter() {
		Either<Action.Error, Action> actionEither = actionManager.getAction(
			DELETE.name(), asList("name", "42"));

		if (actionEither.isLeft()) {
			throw new AssertionError("Action not present");
		}

		Action action = actionEither.get();

		Object pageItems = action.apply(null);

		System.out.println(pageItems);
	}

	private void _testItemRoutesGetter() {
		Either<Action.Error, Action> actionEither = actionManager.getAction(
			GET.name(), asList("name", "42"));

		if (actionEither.isLeft()) {
			throw new AssertionError("Action not present");
		}

		Action action = actionEither.get();

		Object model = action.apply(null);

		assertThat(model, is("Apio"));
	}

	private void _testItemRoutesUpdater(ItemRoutes<String, Long> itemRoutes) {
		Optional<Form> formOptional = itemRoutes.getFormOptional();

		if (!formOptional.isPresent()) {
			throw new AssertionError("Update Form not present");
		}

		Form form = formOptional.get();

		assertThat(form.getId(), is("u/name"));

		Map map = (Map)form.get(_body);

		assertThat(map.get("key"), is(keyValueFrom(_body)));

		Optional<UpdateItemFunction<String, Long>> updateItemFunctionOptional =
			itemRoutes.getUpdateItemFunctionOptional();

		if (!updateItemFunctionOptional.isPresent()) {
			throw new AssertionError("UpdateItemFunction not present");
		}

		UpdateItemFunction<String, Long> updateItemFunction =
			updateItemFunctionOptional.get();

		SingleModel<String> singleModel = updateItemFunction.apply(
			null
		).apply(
			42L
		).andThen(
			Try::getUnchecked
		).apply(
			_body
		);

		assertThat(singleModel.getResourceName(), is("name"));
		assertThat(singleModel.getModel(), is("Updated"));

		List<Operation> operations = singleModel.getOperations();

		assertThat(operations, hasSize(2));

		Operation firstOperation = operations.get(0);

		assertThat(firstOperation.getFormOptional(), is(emptyOptional()));
		assertThat(firstOperation.getHttpMethod(), is(DELETE));
		assertThat(firstOperation.getName(), is("name/delete"));
		assertThat(
			firstOperation.getURIOptional(),
			is(optionalWithValue(equalTo("name/id"))));

		Operation secondOperation = operations.get(1);

		assertThat(secondOperation.getFormOptional(), is(optionalWithValue()));
		assertThat(secondOperation.getHttpMethod(), is(PUT));
		assertThat(secondOperation.getName(), is("name/update"));
		assertThat(
			secondOperation.getURIOptional(),
			is(optionalWithValue(equalTo("name/id"))));
	}

	private void _testOneParameterRemoverRoute(Long identifier, String string) {
		assertThat(string, is("Apio"));

		_testAndReturnNoParameterRemoverRoute(identifier);
	}

	private void _testThreeParameterRemoverRoute(
		Long identifier, String string, Long aLong, Boolean aBoolean) {

		assertThat(aBoolean, is(true));

		_testTwoParameterRemoverRoute(identifier, string, aLong);
	}

	private void _testTwoParameterRemoverRoute(
		Long identifier, String string, Long aLong) {

		assertThat(aLong, is(42L));

		_testOneParameterRemoverRoute(identifier, string);
	}

	private final Body _body = __ -> Optional.of("Apio");

}