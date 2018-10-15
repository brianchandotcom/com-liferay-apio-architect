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

import static com.liferay.apio.architect.internal.annotation.ActionKey.ANY_ROUTE;
import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.operation.HTTPMethod.DELETE;
import static com.liferay.apio.architect.operation.HTTPMethod.GET;

import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides utility function to transform a class annotated with actions
 * (Retrieve, Delete...) into the ActionManager
 *
 * @author Javier Gamarra
 * @review
 */
public class RouterTransformer {

	/**
	 * Transform a class annotated with @Action into routes in the ActionManager
	 * @review
	 */
	public static void toActionRouter(
		ActionRouter actionRouter, String name, ActionManager actionManager) {

		Class<? extends ActionRouter> actionRouterClass =
			actionRouter.getClass();

		List<Method> customGetters = _filter(
			getMethodsListWithAnnotation(actionRouterClass, Action.class),
			GET.name());

		customGetters.forEach(
			method -> _addGetter(name, method, actionRouter, actionManager));

		List<Method> getters = getMethodsListWithAnnotation(
			actionRouterClass, Actions.Retrieve.class);

		getters.forEach(
			method -> _addGetter(name, method, actionRouter, actionManager));

		List<Method> customRemovers = _filter(
			getMethodsListWithAnnotation(actionRouterClass, Action.class),
			DELETE.name());

		customRemovers.forEach(
			method -> _addRemover(name, method, actionRouter, actionManager));

		List<Method> removers = getMethodsListWithAnnotation(
			actionRouterClass, Actions.Remove.class);

		removers.forEach(
			method -> _addRemover(name, method, actionRouter, actionManager));
	}

	private static void _addGetter(
		String name, Method method, ActionRouter actionRouter,
		ActionManager actionManager) {

		ActionKey actionKey = _getActionKey(method, name);

		actionManager.add(
			actionKey, _getThrowableTriFunction(method, actionRouter),
			_getProviders(method));
	}

	private static void _addRemover(
		String name, Method method, ActionRouter actionRouter,
		ActionManager actionManager) {

		ActionKey actionKey = new ActionKey(DELETE.name(), name, ANY_ROUTE);

		actionManager.add(
			actionKey, _getThrowableTriFunction(method, actionRouter),
			_getProviders(method));
	}

	private static List<Method> _filter(
		List<Method> methodsListWithAnnotation, String httpMethodName) {

		Stream<Method> stream = methodsListWithAnnotation.stream();

		return stream.filter(
			method -> {
				Action annotation = method.getAnnotation(Action.class);

				return httpMethodName.equals(annotation.httpMethod());
			}
		).collect(
			Collectors.toList()
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

	private static ActionKey _getActionKey(Method method, String name) {

		boolean idAnnotation = _hasIdAnnotation(method);

		String customName = _getCustomName(method);

		Optional<String> nameOptional = _getParentNameOptional(method);

		String get = GET.name();

		if (nameOptional.isPresent()) {
			return new ActionKey(
				get, nameOptional.get(), ANY_ROUTE, name, customName);
		}
		else if (idAnnotation) {
			return new ActionKey(get, name, ANY_ROUTE, customName);
		}

		return new ActionKey(get, name, customName);
	}

	private static Optional<String> _getParentNameOptional(Method method) {
		Optional<Annotation> parentIdAnnotation = _getAnnotationFromClass(
			method.getParameterAnnotations(), ParentId.class);

		return parentIdAnnotation.map(
			annotation -> ((ParentId)annotation).value()
		).map(
			value -> value.getAnnotation(Vocabulary.Type.class)
		).map(
			type -> toLowercaseSlug(type.value())
		);
	}

	private static boolean _hasIdAnnotation(Method method) {
		return _getAnnotationFromClass(
			method.getParameterAnnotations(), Id.class).isPresent();
	}

	private static String _getCustomName(Method method) {
		return Optional.ofNullable(
				method.getAnnotation(Action.class)
			).map(
				Action::name
			).orElse(
				null
			);
	}

	private static Optional<Annotation> _getAnnotation(
		Annotation[] annotations, Class<? extends Annotation> annotationType) {

		return Stream.of(
			annotations
		).filter(
			annotation -> annotationType.isAssignableFrom(annotation.getClass())
		).findFirst();
	}

	private static Optional<Annotation> _getAnnotationFromClass(
		Annotation[][] annotations,
		Class<? extends Annotation> annotationType) {

		return Stream.of(
			annotations
		).flatMap(
			Stream::of
		).filter(
			annotation -> annotationType.isAssignableFrom(annotation.getClass())
		).findFirst();
	}

	private static Object[] _getParameters(
		Method method, Object id, Object body, List<Object> providers) {

		List<Object> list = new ArrayList<>();

		Parameter[] parameters = method.getParameters();

		for (Parameter parameter : parameters) {
			Annotation[] annotations = parameter.getAnnotations();

			if (_hasAnnotation(annotations, Id.class) ||
				_hasAnnotation(annotations, ParentId.class)) {

				list.add(id);
			}
			else if (_hasAnnotation(annotations, Body.class)) {
				list.add(body);
			}
			else {
				list.add(_findProvider(providers, parameter.getType()));
			}
		}

		return list.toArray();
	}

	private static Class[] _getProviders(Method method) {
		List<Class> classes = new ArrayList<>();

		Parameter[] parameters = method.getParameters();

		for (Parameter parameter : parameters) {
			Annotation[] annotations = parameter.getAnnotations();

			if (annotations.length == 0) {
				classes.add(parameter.getType());
			}
		}

		return classes.toArray(new Class<?>[0]);
	}

	private static ThrowableTriFunction
		<Object, Object, List<Object>, Object> _getThrowableTriFunction(
			Method method, ActionRouter actionRouter) {

		return (id, body, providers) -> method.invoke(
			actionRouter, _getParameters(method, id, body, providers));
	}

	private static boolean _hasAnnotation(
		Annotation[] annotations, Class<? extends Annotation> annotationType) {

		return _getAnnotation(annotations, annotationType).isPresent();
	}

}