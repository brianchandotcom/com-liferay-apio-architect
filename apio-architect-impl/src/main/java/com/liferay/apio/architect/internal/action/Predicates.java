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

import static java.util.Arrays.asList;

import com.liferay.apio.architect.internal.action.resource.Resource;

import java.lang.annotation.Annotation;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This class contains several {@link ActionSemantics}-related predicates that
 * can be used to test conditions or filter lists.
 *
 * <p>
 * This class should not be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public class Predicates {

	/**
	 * Checks if an action's method is {@code DELETE}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> isActionByDELETE = areEquals(
		ActionSemantics::method, "DELETE");

	/**
	 * Checks if an action's method is {@code GET}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> isActionByGET = areEquals(
		ActionSemantics::method, "GET");

	/**
	 * Checks if an action's method is {@code POST}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> isActionByPOST = areEquals(
		ActionSemantics::method, "POST");

	/**
	 * Checks if an action's method is {@code PUT}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> isActionByPUT = areEquals(
		ActionSemantics::method, "PUT");

	/**
	 * Checks if an action's method is {@code POST} and its name is {@code
	 * create}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> isCreateAction =
		isActionByPOST.and(isActionNamed("create"));

	/**
	 * Checks if an action's method is {@code DELETE} and its name is {@code
	 * remove}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> isRemoveAction =
		isActionByDELETE.and(isActionNamed("remove"));

	/**
	 * Checks if an action's method is {@code PUT} and its name is {@code
	 * replace}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> isReplaceAction =
		isActionByPUT.and(isActionNamed("replace"));

	/**
	 * Checks if an action's method is {@code GET} and its name is {@code
	 * retrieve}.
	 *
	 * @review
	 */
	public static final Predicate<ActionSemantics> isRetrieveAction =
		isActionByGET.and(isActionNamed("retrieve"));

	/**
	 * Transforms the object under testing with the provided function and checks
	 * equality between the result and the provided object.
	 *
	 * @param  transformFunction the function used to transform the object
	 * @param  s the object to check equality with
	 * @return {@code true} if the result of applying the function and the
	 *         provided object are equals; {@code false} otherwise
	 * @review
	 */
	public static <T, S> Predicate<T> areEquals(
		Function<T, S> transformFunction, S s) {

		return t -> transformFunction.andThen(
			s::equals
		).apply(
			t
		);
	}

	/**
	 * Checks if an action is annotated with a certain {@link Annotation}.
	 *
	 * @review
	 */
	public static <A extends Annotation> Predicate<ActionSemantics>
		hasAnnotation(Class<A> annotation) {

		return actionSemantics -> Stream.of(
			actionSemantics.annotations()
		).flatMap(
			List::stream
		).map(
			Annotation::getClass
		).anyMatch(
			annotation::isAssignableFrom
		);
	}

	/**
	 * Returns a predicate that checks if the action's resource is an instance
	 * of the provided resource class.
	 *
	 * @review
	 */
	public static <R extends Resource> Predicate<ActionSemantics> isActionFor(
		Class<R> clazz) {

		return actionSemantics -> clazz.isInstance(actionSemantics.resource());
	}

	/**
	 * Returns a predicate that checks if the action's resource is equals to the
	 * one provided.
	 *
	 * @review
	 */
	public static Predicate<ActionSemantics> isActionFor(Resource resource) {
		return areEquals(ActionSemantics::resource, resource);
	}

	/**
	 * Returns a predicate that checks if the action's name is equals to the one
	 * provided.
	 *
	 * @review
	 */
	public static Predicate<ActionSemantics> isActionNamed(String name) {
		return actionSemantics -> name.equals(actionSemantics.name());
	}

	/**
	 * Returns a predicate that checks if the action's return class is any of
	 * the provided ones.
	 *
	 * @review
	 */
	public static Predicate<ActionSemantics> returnsAnyOf(Class<?>... classes) {
		return actionSemantics -> {
			List<Class<?>> list = asList(classes);

			return list.contains(actionSemantics.returnClass());
		};
	}

	private Predicates() {
		throw new UnsupportedOperationException();
	}

}