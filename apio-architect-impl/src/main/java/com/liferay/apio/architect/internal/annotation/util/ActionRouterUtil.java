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

import static com.liferay.apio.architect.internal.annotation.ActionKey.ANY_ROUTE;
import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.findObjectOfClass;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.getAnnotationFromParametersOptional;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.hasAnnotation;

import com.liferay.apio.architect.annotation.Actions.Action;
import com.liferay.apio.architect.annotation.Body;
import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.annotation.Vocabulary;
import com.liferay.apio.architect.internal.annotation.ActionKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Provides utility function to transform a class annotated with actions
 * (Retrieve, Delete...) into the ActionManager
 *
 * @author Javier Gamarra
 * @review
 */
public class ActionRouterUtil {

	public static ActionKey getActionKey(
		Method method, String name, String httpMethodName) {

		Optional<String> nestedNameOptional = _getNestedNameOptional(method);

		String customActionName = _getCustomActionName(method);

		if (nestedNameOptional.isPresent()) {
			return new ActionKey(
				httpMethodName, nestedNameOptional.get(), ANY_ROUTE, name,
				customActionName);
		}
		else if (getAnnotationFromParametersOptional(
					method, Id.class).isPresent()) {

			return new ActionKey(
				httpMethodName, name, ANY_ROUTE, customActionName);
		}

		return new ActionKey(httpMethodName, name, customActionName);
	}

	public static Object[] getParameters(
		Method method, Object id, Object body, List<Object> providers) {

		Stream<Parameter> stream = Arrays.stream(method.getParameters());

		return stream.map(
			parameter -> _getParameter(parameter, id, body, providers)
		).toArray();
	}

	public static Class[] getProviders(Method method) {
		Stream<Parameter> stream = Arrays.stream(method.getParameters());

		return stream.filter(
			parameter -> {
				Annotation[] annotations = parameter.getAnnotations();

				return annotations.length == 0;
			}
		).map(
			parameter -> (Class)parameter.getType()
		).toArray(
			Class[]::new
		);
	}

	private static String _getCustomActionName(Method method) {
		return Optional.ofNullable(
			method.getAnnotation(Action.class)
		).map(
			Action::name
		).orElse(
			null
		);
	}

	private static Optional<String> _getNestedNameOptional(Method method) {
		Optional<Annotation> parentIdAnnotation =
			getAnnotationFromParametersOptional(method, ParentId.class);

		return parentIdAnnotation.map(
			annotation -> ((ParentId)annotation).value()
		).map(
			resource -> resource.getAnnotation(Vocabulary.Type.class)
		).map(
			type -> toLowercaseSlug(type.value())
		);
	}

	private static Object _getParameter(
		Parameter parameter, Object id, Object body, List<Object> providers) {

		if (hasAnnotation(parameter, Id.class) ||
			hasAnnotation(parameter, ParentId.class)) {

			return id;
		}
		else if (hasAnnotation(parameter, Body.class)) {
			return body;
		}

		return findObjectOfClass(providers, parameter.getType());
	}

}