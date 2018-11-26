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

import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.findAnnotationInAnyParameter;

import static io.leangen.geantyref.GenericTypeReflector.getTypeParameter;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.internal.pagination.PageImpl;
import com.liferay.apio.architect.internal.pagination.PaginationImpl;
import com.liferay.apio.architect.internal.single.model.SingleModelImpl;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.single.model.SingleModel;

import io.vavr.CheckedFunction1;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.List;
import java.util.stream.Stream;

/**
 * Provides utility function to transform a class annotated with actions
 * (Retrieve, Delete...) into the ActionManager
 *
 * @author Javier Gamarra
 * @review
 */
public final class ActionRouterUtil {

	/**
	 * Executes the provided {@code actionExecuteFunction} and returns its
	 * result. An updated version of the provided param list is used, with all
	 * {@link Resource.Id} values transformed with {@link
	 * Resource.Id#asObject()}. After executing the function an updated result
	 * is returned:
	 *
	 * <p>
	 * If the element is {@code null}, {@code null} will be returned.
	 * </p>
	 *
	 * <p>
	 * If the element is a {@code List} or a {@code PageItems}, it will be
	 * transformed to a {@link Page}.
	 * </p>
	 *
	 * <p>
	 * Otherwise, it gets wrapped in a {@code SingleModel}.
	 * </p>
	 *
	 * @param  resource the action's resource
	 * @param  params the action's params
	 * @param  actionExecuteFunction the function used to execute the action
	 * @return the action's result updated to a type that Apio understands
	 * @throws Throwable if the action throws any exception, its cause is thrown
	 *         instead
	 * @review
	 */
	public static Object execute(
			Resource resource, List<?> params,
			CheckedFunction1<Object[], Object> actionExecuteFunction)
		throws Throwable {

		Object[] updatedParams = params.toArray(new Object[0]);

		for (int i = 0; i < params.size(); i++) {
			if (updatedParams[i] instanceof Resource.Id) {
				updatedParams[i] = ((Resource.Id)updatedParams[i]).asObject();
			}
		}

		try {
			Object result = actionExecuteFunction.apply(updatedParams);

			if (result == null) {
				return null;
			}

			if (result instanceof List) {
				List<?> list = (List<?>)result;

				PageItems<?> pageItems = new PageItems<>(list, list.size());

				Pagination pagination = new PaginationImpl(list.size(), 1);

				return new PageImpl<>(
					resource.name(), pageItems, pagination, emptyList());
			}

			if (result instanceof PageItems) {
				PageItems<?> pageItems = (PageItems<?>)result;

				Pagination pagination = getInstanceOf(
					params, Pagination.class,
					new PaginationImpl(pageItems.getTotalCount(), 1));

				return new PageImpl<>(
					resource.name(), pageItems, pagination, emptyList());
			}

			return new SingleModelImpl<>(result, resource.name());
		}
		catch (Throwable throwable) {
			if (nonNull(throwable.getCause())) {
				throw throwable.getCause();
			}

			throw throwable;
		}
	}

	public static String getBodyResourceClassName(Method method) {
		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(_BODY_ANNOTATION)) {
				Class<?> parameterType = parameter.getType();

				if (!List.class.isAssignableFrom(parameterType)) {
					return parameterType.getName();
				}

				AnnotatedType typeParameter = getTypeParameter(
					parameter.getAnnotatedType(),
					List.class.getTypeParameters()[0]);

				if (Class.class.isInstance(typeParameter.getType())) {
					return ((Class)typeParameter.getType()).getName();
				}
			}
		}

		return null;
	}

	public static <T> T getInstanceOf(
		List<?> list, Class<T> searchedClass, T defaultValue) {

		for (Object object : list) {
			if (searchedClass.isInstance(object)) {
				return searchedClass.cast(object);
			}
		}

		return defaultValue;
	}

	public static Class<?>[] getParamClasses(Method method) {
		return Stream.of(
			method.getParameters()
		).map(
			parameter -> {
				if (parameter.getAnnotation(Id.class) != null) {
					return Id.class;
				}
				else if (parameter.getAnnotation(ParentId.class) != null) {
					return ParentId.class;
				}
				else if (parameter.getAnnotation(_BODY_ANNOTATION) != null) {
					return Body.class;
				}

				return parameter.getType();
			}
		).toArray(
			Class[]::new
		);
	}

	public static Resource getResource(Method method, String name) {
		ParentId parentId = findAnnotationInAnyParameter(
			method, ParentId.class);

		if (parentId != null) {
			Class<? extends Identifier<?>> typeClass = parentId.value();

			Type type = typeClass.getAnnotation(Type.class);

			Item parent = Item.of(toLowercaseSlug(type.value()));

			return Nested.of(parent, name);
		}

		Id id = findAnnotationInAnyParameter(method, Id.class);

		if (id != null) {
			return Item.of(name);
		}

		return Paged.of(name);
	}

	public static Class<?> getReturnClass(Method method) {
		Class<?> returnType = method.getReturnType();

		if (PageItems.class.equals(returnType)) {
			return Page.class;
		}

		if (List.class.equals(returnType)) {
			return Page.class;
		}

		if (returnType.getAnnotation(Type.class) != null) {
			return SingleModel.class;
		}

		if (void.class.equals(returnType)) {
			return Void.class;
		}

		return returnType;
	}

	public static boolean isListBody(Method method) {
		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(_BODY_ANNOTATION)) {
				return List.class.isAssignableFrom(parameter.getType());
			}
		}

		return false;
	}

	public static boolean needsParameterFromBody(Method method) {
		return nonNull(findAnnotationInAnyParameter(method, _BODY_ANNOTATION));
	}

	private ActionRouterUtil() {
	}

	private static final Class<com.liferay.apio.architect.annotation.Body>
		_BODY_ANNOTATION = com.liferay.apio.architect.annotation.Body.class;

}