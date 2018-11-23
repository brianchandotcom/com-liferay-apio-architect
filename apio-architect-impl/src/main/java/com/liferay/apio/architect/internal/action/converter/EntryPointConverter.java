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

import static com.liferay.apio.architect.internal.action.Predicates.hasAnnotation;
import static com.liferay.apio.architect.internal.action.Predicates.isActionFor;
import static com.liferay.apio.architect.internal.action.Predicates.isRetrieveAction;
import static com.liferay.apio.architect.internal.action.Predicates.returnsAnyOf;

import static java.util.stream.Collectors.toList;

import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.internal.entrypoint.EntryPoint;
import com.liferay.apio.architect.pagination.Page;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Provides a method for getting an instance of the {@code EntryPoint} out of a
 * list of {@link ActionSemantics}.
 *
 * <p>
 * This class should not be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public final class EntryPointConverter {

	/**
	 * Filters entry point actions from the provided stream and creates an
	 * {@code EntryPoint} instance out of them.
	 *
	 * @review
	 */
	public static EntryPoint getEntryPointFrom(
		Stream<ActionSemantics> actionSemantics) {

		return () -> actionSemantics.filter(
			_isEntryPointAction
		).map(
			ActionSemantics::resource
		).map(
			Paged.class::cast
		).map(
			Paged::name
		).collect(
			toList()
		);
	}

	private EntryPointConverter() {
		throw new UnsupportedOperationException();
	}

	private static final Predicate<ActionSemantics> _isEntryPointAction =
		isRetrieveAction.and(
			returnsAnyOf(Page.class)
		).and(
			hasAnnotation(com.liferay.apio.architect.annotation.EntryPoint.class)
		).and(
			isActionFor(Paged.class)
		);

}