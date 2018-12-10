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

package com.liferay.apio.architect.matchers;

import com.liferay.apio.architect.functional.Try;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Provides {@code Matcher} objects that can be used for testing the {@link Try}
 * monadic type.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 */
public final class TryMatchers {

	/**
	 * Returns a matcher that checks if this {@code Try} is a {@code
	 * Try.Failure}.
	 *
	 * @return the matcher that checks if this {@code Try} is a failure
	 */
	public static <T> Matcher<Try<T>> aFailTry() {
		return new TypeSafeDiagnosingMatcher<Try<T>>() {

			@Override
			public void describeTo(final Description description) {
				description.appendText("a Failure");
			}

			@Override
			protected boolean matchesSafely(
				final Try<T> tTry, final Description description) {

				if (tTry.isFailure()) {
					return true;
				}

				description.appendText("was a Success");

				return false;
			}

		};
	}

	/**
	 * Returns a matcher that checks if this {@code Try} is a {@code
	 * Try.Success}.
	 *
	 * @return the matcher that checks if this try is a success
	 */
	public static <T> Matcher<Try<T>> aSuccessTry() {
		return new TypeSafeDiagnosingMatcher<Try<T>>() {

			@Override
			public void describeTo(final Description description) {
				description.appendText("a Success");
			}

			@Override
			protected boolean matchesSafely(
				final Try<T> tTry, final Description description) {

				if (tTry.isSuccess()) {
					return true;
				}

				description.appendText("was a Failure");

				return false;
			}

		};
	}

	/**
	 * Returns a matcher that checks if this {@code Try} is a {@code
	 * Try.Success} with a certain value.
	 *
	 * @param  matcher the matcher used to check the value
	 * @return the matcher that checks if this {@code Try} is a success with a
	 *         certain value
	 */
	public static <T> Matcher<Try<T>> aTryWithValueThat(
		final Matcher<T> matcher) {

		return new TypeSafeDiagnosingMatcher<Try<T>>() {

			@Override
			public void describeTo(final Description description) {
				description.appendDescriptionOf(
					_successMatcher
				).appendText(
					" with a value that "
				).appendDescriptionOf(
					matcher
				);
			}

			@Override
			protected boolean matchesSafely(
				final Try<T> tTry, final Description description) {

				if (!_successMatcher.matches(tTry)) {
					_successMatcher.describeMismatch(tTry, description);

					return false;
				}

				T t = tTry.getUnchecked();

				if (matcher.matches(t)) {
					return true;
				}

				description.appendText("was a Success whose value ");

				matcher.describeMismatch(t, description);

				return false;
			}

			private final Matcher<Try<T>> _successMatcher = aSuccessTry();

		};
	}

	private TryMatchers() {
		throw new UnsupportedOperationException();
	}

}