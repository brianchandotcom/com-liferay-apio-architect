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

package com.liferay.apio.architect.internal.annotation.representor;

import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.findObjectOfClass;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.getAnnotationFromParametersOptional;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.hasAnnotation;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import com.liferay.apio.architect.annotation.Body;
import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.internal.annotation.representor.types.DummyRouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

/**
 * @author Javier Gamarra
 */
public class AnnotationUtilTest {

	@Test
	public void testFailsIfParameterDoesNotHaveAnnotation()
		throws NoSuchMethodException {

		Method retrieve = DummyRouter.class.getMethod("retrieve", Long.class);

		boolean hasAnnotation = hasAnnotation(
			retrieve.getParameters()[0], Body.class);

		assertThat(hasAnnotation, is(false));
	}

	@Test
	public void testFindObjectInList() {
		List<Object> objects = Arrays.asList("apio", 0L);

		Object objectOfClass = findObjectOfClass(objects, Long.class);

		assertThat(objectOfClass, is(0L));
	}

	@Test
	public void testParameterHasAnnotation() throws NoSuchMethodException {
		Method retrieve = DummyRouter.class.getMethod("retrieve", Long.class);

		boolean hasAnnotation = hasAnnotation(
			retrieve.getParameters()[0], Id.class);

		assertThat(hasAnnotation, is(true));
	}

	@Test
	public void testRetrieveAnnotationFromAParameter()
		throws NoSuchMethodException {

		Method retrieve = DummyRouter.class.getMethod("retrieve", Long.class);

		Optional<Annotation> annotationFromParametersOptional =
			getAnnotationFromParametersOptional(retrieve, Id.class);

		assertThat(annotationFromParametersOptional.isPresent(), is(true));

		Annotation annotation = annotationFromParametersOptional.get();

		Class<? extends Annotation> clazz = annotation.annotationType();

		assertThat(clazz.getName(), is(Id.class.getName()));
	}

	@Test
	public void testReturnsNullIfFailsToFindObjectInList() {
		List<Object> objects = Arrays.asList("apio", 0L);

		Object objectOfClass = findObjectOfClass(objects, Integer.class);

		assertThat(objectOfClass, is(nullValue()));
	}

}