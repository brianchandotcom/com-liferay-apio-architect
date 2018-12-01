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

package com.liferay.apio.architect.internal.annotation.util;

import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.findAnnotationInAnyParameter;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.findAnnotationInMethodOrInItsAnnotations;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import com.liferay.apio.architect.annotation.Actions.Action;
import com.liferay.apio.architect.annotation.Id;

import java.lang.reflect.Method;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Alejandro Hernández
 * @author Javier Gamarra
 */
public class AnnotationUtilTest {

	@BeforeClass
	public static void setUpClass() throws NoSuchMethodException {
		_annotatedWithActionMethod = MyAnnotatedInterface.class.getMethod(
			"annotatedWithAction");
		_annotatedWithCreateMethod = MyAnnotatedInterface.class.getMethod(
			"annotatedWithCreate");
		_annotatedWithRemoveMethod = MyAnnotatedInterface.class.getMethod(
			"annotatedWithRemove");
		_notAnnotatedMethod = MyAnnotatedInterface.class.getMethod(
			"notAnnotated");
		_withParameterAnnotatedMethod = MyAnnotatedInterface.class.getMethod(
			"withParameterAnnotated", long.class);
		_withParameterNotAnnotatedMethod = MyAnnotatedInterface.class.getMethod(
			"withParametersNotAnnotated", long.class);
	}

	@Test
	public void test() {
		Action actionMethodAction = findAnnotationInMethodOrInItsAnnotations(
			_annotatedWithActionMethod, Action.class);
		Action createMethodAction = findAnnotationInMethodOrInItsAnnotations(
			_annotatedWithCreateMethod, Action.class);
		Action nullAction = findAnnotationInMethodOrInItsAnnotations(
			_notAnnotatedMethod, Action.class);
		Action removeMethodAction = findAnnotationInMethodOrInItsAnnotations(
			_annotatedWithRemoveMethod, Action.class);

		assertThat(actionMethodAction, is(notNullValue()));
		assertThat(actionMethodAction.name(), is("name"));
		assertThat(actionMethodAction.httpMethod(), is("GET"));
		assertThat(actionMethodAction.reusable(), is(false));

		assertThat(createMethodAction, is(notNullValue()));
		assertThat(createMethodAction.name(), is("create"));
		assertThat(createMethodAction.httpMethod(), is("POST"));
		assertThat(createMethodAction.reusable(), is(false));

		assertThat(nullAction, is(nullValue()));

		assertThat(removeMethodAction, is(notNullValue()));
		assertThat(removeMethodAction.name(), is("remove"));
		assertThat(removeMethodAction.httpMethod(), is("DELETE"));
		assertThat(removeMethodAction.reusable(), is(false));
	}

	@Test
	public void testFindAnnotationInAnyParameter() {
		Id id = findAnnotationInAnyParameter(
			_withParameterAnnotatedMethod, Id.class);
		Id nullId1 = findAnnotationInAnyParameter(
			_withParameterNotAnnotatedMethod, Id.class);
		Id nullId2 = findAnnotationInAnyParameter(
			_notAnnotatedMethod, Id.class);

		assertThat(id, is(instanceOf(Id.class)));
		assertThat(nullId1, is(nullValue()));
		assertThat(nullId2, is(nullValue()));
	}

	private static Method _annotatedWithActionMethod;
	private static Method _annotatedWithCreateMethod;
	private static Method _annotatedWithRemoveMethod;
	private static Method _notAnnotatedMethod;
	private static Method _withParameterAnnotatedMethod;
	private static Method _withParameterNotAnnotatedMethod;

}