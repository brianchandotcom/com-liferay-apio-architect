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

package com.liferay.apio.architect.internal.util.matcher;

import com.liferay.apio.architect.internal.list.FunctionalList;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Provides a {@code Matcher} that checks if an element is a {@link
 * FunctionalList} containing some values.
 *
 * @author Alejandro Hernández
 * @param  <E> the type of elements in the list
 * @review
 */
public class IsAFunctionalList<E>
	extends TypeSafeDiagnosingMatcher<FunctionalList<E>> {

	/**
	 * Returns a matcher that checks if this is a functional list that conforms
	 * with the {@code matcher} parameter.
	 *
	 * @param  matcher the matcher used to check the contents of this functional
	 *         interface
	 * @return the matcher that checks if this is a functional list that
	 *         conforms with the {@code matcher} parameter
	 */
	public static <T> Matcher<FunctionalList<T>> aFunctionalListThat(
		Matcher<Iterable<? extends T>> matcher) {

		return new IsAFunctionalList<>(matcher);
	}

	public IsAFunctionalList(Matcher<Iterable<? extends E>> matcher) {
		_matcher = matcher;
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText(
			"a functional list that conforms with an "
		).appendDescriptionOf(
			_matcher
		);
	}

	@Override
	protected boolean matchesSafely(
		final FunctionalList<E> functionalList, final Description description) {

		E head = functionalList.head();

		List<E> list = Stream.concat(
			Stream.of(head), functionalList.tailStream()
		).collect(
			Collectors.toList()
		);

		if (_matcher.matches(list)) {
			return true;
		}

		description.appendText("was a functional list whose ");

		_matcher.describeMismatch(list, description);

		return false;
	}

	private final Matcher<Iterable<? extends E>> _matcher;

}