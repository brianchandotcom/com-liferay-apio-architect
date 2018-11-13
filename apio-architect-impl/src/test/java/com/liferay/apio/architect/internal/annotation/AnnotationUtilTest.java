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

package com.liferay.apio.architect.internal.annotation;

import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.getAnnotationFromParametersOptional;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.hasAnnotation;

import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static junit.framework.TestCase.assertTrue;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import com.liferay.apio.architect.annotation.Body;
import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.internal.annotation.representor.types.DummyRouter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Test;

/**
 * @author Javier Gamarra
 */
public class AnnotationUtilTest {

	@Test
	public void testFailsIfParameterDoesNotHaveAnnotation()
		throws NoSuchMethodException {

		Method retrieve = DummyRouter.class.getMethod("retrieve", Long.class);

		Parameter parameter = retrieve.getParameters()[0];

		Predicate<Parameter> predicate = hasAnnotation(Body.class);

		assertFalse(predicate.test(parameter));
	}

	@Test
	public void testParameterHasAnnotation() throws NoSuchMethodException {
		Method retrieve = DummyRouter.class.getMethod("retrieve", Long.class);

		Parameter parameter = retrieve.getParameters()[0];

		Predicate<Parameter> predicate = hasAnnotation(Id.class);

		assertTrue(predicate.test(parameter));
	}

	@Test
	public void testRetrieveAnnotationFromAParameter()
		throws NoSuchMethodException {

		Method retrieve = DummyRouter.class.getMethod("retrieve", Long.class);

		Optional<Id> optional = getAnnotationFromParametersOptional(
			retrieve, Id.class);

		assertThat(optional, is(optionalWithValue(instanceOf(Id.class))));
	}

}