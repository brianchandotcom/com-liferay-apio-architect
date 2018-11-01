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

package com.liferay.apio.architect.test.util.matcher;

import static org.hamcrest.CoreMatchers.instanceOf;

import io.vavr.CheckedRunnable;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Provides a matcher that can be used to test that a method call throws an
 * specific exception.
 *
 * @author Alejandro Hernández
 * @review
 */
public final class FailsWith<E extends Throwable>
	extends TypeSafeMatcher<CheckedRunnable> {

	/**
	 * Returns a matcher that checks if a runnable throws an exception of the
	 * provided type.
	 *
	 * @param  throwableType the exception type
	 * @return a matcher that checks if a runnable throws an exception of the
	 *         provided type
	 * @review
	 */
	public static <E extends Throwable> Matcher<CheckedRunnable> failsWith(
		final Class<E> throwableType) {

		return new FailsWith<>(instanceOf(throwableType));
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText(
			"fails with "
		).appendDescriptionOf(
			_matcher
		);
	}

	@Override
	protected boolean matchesSafely(final CheckedRunnable runnable) {
		try {
			runnable.run();

			return false;
		}
		catch (final Throwable t) {
			return _matcher.matches(t);
		}
	}

	private FailsWith(final Matcher<? super E> matcher) {
		_matcher = matcher;
	}

	private final Matcher<? super E> _matcher;

}