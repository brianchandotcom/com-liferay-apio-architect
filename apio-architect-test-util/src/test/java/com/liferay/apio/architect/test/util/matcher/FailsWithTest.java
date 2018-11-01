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

import static com.liferay.apio.architect.test.util.matcher.FailsWith.failsWith;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import io.vavr.CheckedRunnable;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class FailsWithTest {

	@Test
	public void testFailsWithMatcherUpdatesDescription() {
		Description description = new StringDescription();

		failsWith(IllegalArgumentException.class).describeTo(description);

		String expected =
			"fails with an instance of java.lang.IllegalArgumentException";

		assertThat(description.toString(), is(expected));
	}

	@Test
	public void testNotThrowableFunctionFails() {
		CheckedRunnable checkedRunnable = () -> {
		};

		assertThat(
			checkedRunnable, not(failsWith(IllegalArgumentException.class)));
	}

	@Test
	public void testThrowableFunctionUpdatesDescription() {
		CheckedRunnable checkedRunnable = () -> {
			throw new IllegalArgumentException();
		};

		Description description = new StringDescription();

		Matcher<CheckedRunnable> functionalListMatcher = failsWith(
			IllegalArgumentException.class);

		functionalListMatcher.describeMismatch(checkedRunnable, description);

		String expected = "was <" + checkedRunnable.toString() + ">";

		assertThat(description.toString(), is(expected));
	}

	@Test
	public void testThrowableFunctionValidates() {
		CheckedRunnable checkedRunnable = () -> {
			throw new IllegalArgumentException();
		};

		boolean matches = failsWith(
			IllegalArgumentException.class).matches(checkedRunnable);

		assertThat(matches, is(true));
	}

}