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

import com.liferay.apio.architect.annotation.Actions;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.pagination.Page;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Provides methods for filtering actions for a {@link Resource.Paged}.
 *
 * <p>
 * This class should not be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public class PagedResourceConverter {

	/**
	 * Returns the {@link ActionSemantics} that identifies the action for
	 * retrieving the provided {@link Resource.Paged}, if present. Returns
	 * {@code Optional#empty()} otherwise.
	 *
	 * @review
	 */
	public static Optional<ActionSemantics> filterRetrieveActionFor(
		Resource.Paged pagedResource, Stream<ActionSemantics> actionSemantics) {

		return actionSemantics.filter(
			_isRootCollectionAction
		).filter(
			isActionFor(pagedResource)
		).findFirst();
	}

	private PagedResourceConverter() {
		throw new UnsupportedOperationException();
	}

	private static final Predicate<ActionSemantics> _isRootCollectionAction =
		isRetrieveAction.and(
			returnsAnyOf(Page.class)
		).and(
			hasAnnotation(Actions.EntryPoint.class)
		).and(
			isActionFor(Resource.Paged.class)
		);

}