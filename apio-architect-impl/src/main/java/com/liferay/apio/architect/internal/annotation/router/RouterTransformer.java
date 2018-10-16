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

package com.liferay.apio.architect.internal.annotation.router;

import com.liferay.apio.architect.annotation.Actions;
import com.liferay.apio.architect.annotation.Actions.Action;
import com.liferay.apio.architect.annotation.Body;
import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.annotation.Vocabulary;
import com.liferay.apio.architect.function.throwable.ThrowableTriFunction;
import com.liferay.apio.architect.internal.annotation.ActionKey;
import com.liferay.apio.architect.internal.annotation.ActionManager;
import com.liferay.apio.architect.router.ActionRouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.liferay.apio.architect.internal.annotation.ActionKey.ANY_ROUTE;
import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil._getAnnotationFromMethodParameters;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil._hasAnnotation;
import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation;

/**
 * Provides utility function to transform a class annotated with actions
 * (Retrieve, Delete...) into the ActionManager
 *
 * @author Javier Gamarra
 * @review
 */
public class RouterTransformer {

	private static List<Class<? extends Annotation>> _annotationsToSearch =
		Arrays.asList(
			Action.class, Actions.Retrieve.class, Actions.Remove.class);

	/**
	 * Transform a class annotated with @Action into routes in the ActionManager
	 * @review
	 */
	public static void toActionRouter(
		ActionRouter actionRouter, String name, ActionManager actionManager) {

		Class<? extends ActionRouter> actionRouterClass =
			actionRouter.getClass();

		_annotationsToSearch.forEach(
			annotationClass -> getMethodsListWithAnnotation(
				actionRouterClass, annotationClass
			).forEach(
				method -> {
					Action annotation = method.getAnnotation(Action.class);
					if (annotation == null) {
						annotation =
							annotationClass.getAnnotation(Action.class);
					}

					ActionKey actionKey = _getActionKey(
						method, name, annotation.httpMethod());

					actionManager.add(
						actionKey,
						_getThrowableTriFunction(method, actionRouter),
						_getProviders(method));
				}
			)
		);
	}

	private static Object _findProvider(List<Object> providers, Class<?> type) {
		Stream<Object> stream = providers.stream();

		return stream.filter(
			provider -> type.isAssignableFrom(provider.getClass())
		).findFirst(
		).orElse(
			null
		);
	}

	private static ActionKey _getActionKey(
		Method method, String name, String httpMethodName) {

		Optional<String> nestedNameOptional = _getNestedNameOptional(method);

		String customName = _getCustomAction(method);

		if (nestedNameOptional.isPresent()) {
			return new ActionKey(
				httpMethodName, nestedNameOptional.get(), ANY_ROUTE, name,
				customName);
		}
		else if (_getAnnotationFromMethodParameters(method, Id.class)
			.isPresent()) {
			return new ActionKey(httpMethodName, name, ANY_ROUTE, customName);
		}

		return new ActionKey(httpMethodName, name, customName);
	}

	private static Optional<String> _getNestedNameOptional(Method method) {
		Optional<Annotation> parentIdAnnotation =
			_getAnnotationFromMethodParameters(method, ParentId.class);

		return parentIdAnnotation.map(
			annotation -> ((ParentId) annotation).value()
		).map(
			resource -> resource.getAnnotation(Vocabulary.Type.class)
		).map(
			type -> toLowercaseSlug(type.value())
		);
	}

	private static String _getCustomAction(Method method) {
		return Optional.ofNullable(
			method.getAnnotation(Action.class)
		).map(
			Action::name
		).orElse(
			null
		);
	}

	private static Object[] _getParameters(
		Method method, Object id, Object body, List<Object> providers) {

		return Arrays.stream(
			method.getParameters()
		).map(
			parameter -> _getParameter(parameter, id, body, providers)
		).toArray();
	}

	private static Object _getParameter(
		Parameter parameter, Object id, Object body, List<Object> providers) {
		if (_hasAnnotation(parameter, Id.class) ||
			_hasAnnotation(parameter, ParentId.class)) {

			return id;
		}
		else if (_hasAnnotation(parameter, Body.class)) {
			return body;
		}

		return _findProvider(providers, parameter.getType());
	}

	private static Class[] _getProviders(Method method) {
		return Arrays.stream(
			method.getParameters()
		).filter(
			parameter -> parameter.getAnnotations().length == 0
		).map(
			parameter -> (Class) parameter.getType()
		).toArray(
			Class[]::new
		);
	}

	private static ThrowableTriFunction
		<Object, Object, List<Object>, Object> _getThrowableTriFunction(
		Method method, ActionRouter actionRouter) {

		return (id, body, providers) -> method.invoke(
			actionRouter, _getParameters(method, id, body, providers));
	}

}