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

import static com.liferay.apio.architect.internal.action.Predicates.isActionByGET;
import static com.liferay.apio.architect.internal.action.Predicates.isActionByPOST;
import static com.liferay.apio.architect.internal.action.Predicates.isActionNamed;

import static java.util.Arrays.asList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.liferay.apio.architect.alias.form.FormBuilderFunction;
import com.liferay.apio.architect.alias.routes.permission.HasNestedAddingPermissionFunction;
import com.liferay.apio.architect.alias.routes.permission.HasRemovePermissionFunction;
import com.liferay.apio.architect.alias.routes.permission.HasUpdatePermissionFunction;
import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.custom.actions.CustomRoute;
import com.liferay.apio.architect.custom.actions.GetRoute;
import com.liferay.apio.architect.custom.actions.PostRoute;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.form.FormImpl;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.test.util.pagination.PaginationRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides utilities for testing routes classes.
 *
 * @author Alejandro Hernández
 */
public class RoutesTestUtil {

	/**
	 * A mock {@link Body} object.
	 *
	 * @review
	 */
	public static final Body BODY = __ -> Optional.of("Apio");

	/**
	 * A mock {@link Credentials} object.
	 *
	 * @review
	 */
	public static final Credentials CREDENTIALS = () -> "auth";

	/**
	 * A {@code FormBuilderFunction} that creates a {@code Map<String, Object>}
	 * {@code Form}.
	 */
	public static final FormBuilderFunction<Map<String, Object>>
		FORM_BUILDER_FUNCTION = builder -> builder.title(
			__ -> ""
		).description(
			__ -> ""
		).constructor(
			HashMap::new
		).addRequiredString(
			"key", (map, value) -> map.put("key", value)
		).build();

	/**
	 * A supplier of form builders.
	 *
	 * @review
	 */
	public static final Supplier<Form.Builder> FORM_BUILDER_SUPPLIER = () ->
		new FormImpl.BuilderImpl<>(__ -> null, __ -> Optional.of("custom"));

	/**
	 * A mock {@link CustomRoute} with name {@code read} and method {@code GET}.
	 *
	 * @review
	 */
	public static final CustomRoute GET_CUSTOM_ROUTE = new GetRoute() {

		@Override
		public String getName() {
			return "read";
		}

	};

	/**
	 * An item permission function that always returns {@code true}.
	 */
	public static final HasRemovePermissionFunction<Long>
		HAS_REMOVE_PERMISSION_FUNCTION = (credentials, id) -> true;

	/**
	 * An item permission function that always returns {@code true}.
	 */
	public static final HasUpdatePermissionFunction<Long>
		HAS_UPDATE_PERMISSION_FUNCTION = (credentials, id) -> true;

	/**
	 * An identifier function that provides identifiers for {@code String}
	 * models
	 */
	public static final Function<String, Long> IDENTIFIER_FUNCTION = __ -> 42L;

	/**
	 * Checks if an {@link ActionSemantics} method is {@code POST} and the
	 * action's name is {@code batch-create}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> IS_BATCH_CREATE_ACTION =
		isActionByPOST.and(isActionNamed("batch-create"));

	/**
	 * Checks if an {@link ActionSemantics} method is {@code GET} and the
	 * action's name is {@code read}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> IS_READ_ACTION =
		isActionByGET.and(isActionNamed("read"));

	/**
	 * Checks if an {@link ActionSemantics} method is {@code POST} and the
	 * action's name is {@code write}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> IS_WRITE_ACTION =
		isActionByPOST.and(isActionNamed("write"));

	/**
	 * A mock {@code Pagination} object.
	 */
	public static final Pagination PAGINATION = PaginationRequest.of(10, 1);

	/**
	 * A mock {@link CustomRoute} with name {@code write} and method {@code
	 * POST}.
	 *
	 * @review
	 */
	public static final CustomRoute POST_CUSTOM_ROUTE = new PostRoute() {

		@Override
		public String getName() {
			return "write";
		}

	};

	/**
	 * A function that provides instances of {@code String}, {@code Long},
	 * {@code Integer}, {@code Boolean}, {@code Float}, {@code Pagination}, and
	 * {@code Credentials}.
	 */
	public static final Function<Class<?>, ?> PROVIDE_FUNCTION = aClass -> {
		if (aClass.equals(String.class)) {
			return "Apio";
		}
		else if (aClass.equals(Long.class)) {
			return 42L;
		}
		else if (aClass.equals(Integer.class)) {
			return 2017;
		}
		else if (aClass.equals(Boolean.class)) {
			return true;
		}
		else if (aClass.equals(Float.class)) {
			return 0.1F;
		}
		else if (aClass.equals(Pagination.class)) {
			return PAGINATION;
		}
		else if (aClass.equals(Credentials.class)) {
			return CREDENTIALS;
		}
		else if (aClass.equals(Body.class)) {
			return TestBody.INSTANCE;
		}
		else if (aClass.equals(Void.class)) {
			return null;
		}
		else if (aClass.equals(Id.class)) {
			return 42L;
		}
		else if (aClass.equals(ParentId.class)) {
			return 21L;
		}
		else {
			throw new AssertionError("Class " + aClass + " is not supported");
		}
	};

	/**
	 * Filters a list of {@link ActionSemantics} using the provided builder and
	 * returns the first occurrence. It also check that there is only one
	 * occurrence that matches the predicate.
	 *
	 * @review
	 */
	public static ActionSemantics filterActionSemantics(
		List<ActionSemantics> actionSemantics,
		Predicate<ActionSemantics> predicate) {

		Stream<ActionSemantics> stream = actionSemantics.stream();

		List<ActionSemantics> matchingActionSemantics = stream.filter(
			predicate
		).collect(
			Collectors.toList()
		);

		assertThat(matchingActionSemantics.size(), is(1));

		return matchingActionSemantics.get(0);
	}

	/**
	 * Converts a list of param classes into param instances using the {@link
	 * #PROVIDE_FUNCTION}.
	 *
	 * @review
	 */
	public static List<?> getParams(List<Class<?>> params) {
		Stream<Class<?>> stream = params.stream();

		return stream.map(
			PROVIDE_FUNCTION
		).collect(
			Collectors.toList()
		);
	}

	/**
	 * Returns a nested collection permission function that always returns
	 * {@code true}.
	 */
	public static <S> HasNestedAddingPermissionFunction<S>
		hasNestedAddingPermissionFunction() {

		return (credentials, s) -> true;
	}

	/**
	 * Prepends a list with the provided elements. This method always returns a
	 * new list.
	 *
	 * @review
	 */
	@SafeVarargs
	public static <T> List<T> prependWith(List<T> list, T... elements) {
		return new ArrayList<T>(asList(elements)) {
			{
				addAll(list);
			}
		};
	}

	/**
	 * Mock {@link Body} implementation that contains both list and single body.
	 *
	 * @review
	 */
	public static class TestBody implements Body {

		public static final TestBody INSTANCE = new TestBody();

		@Override
		public Optional<List<Body>> getBodyMembersOptional() {
			return Optional.of(asList(BODY, BODY));
		}

		@Override
		public Optional<String> getValueOptional(String key) {
			return BODY.getValueOptional(key);
		}

	}

	/**
	 * Mock identifier class to be used for custom actions.
	 *
	 * @review
	 */
	public interface CustomIdentifier extends Identifier<Long> {
	}

}