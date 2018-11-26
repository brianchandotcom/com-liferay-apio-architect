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
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.getAnnotationFromParametersOptional;

import static io.leangen.geantyref.GenericTypeReflector.getTypeParameter;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.form.Form;
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
import io.vavr.control.Option;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Provides utility function to transform a class annotated with actions
 * (Retrieve, Delete...) into the ActionManager
 *
 * @author Javier Gamarra
 * @review
 */
public class ActionRouterUtil {

	public static final Class<com.liferay.apio.architect.annotation.Body>
		BODY_ANNOTATION = com.liferay.apio.architect.annotation.Body.class;

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
				return getPage((List<?>)result, resource.name());
			}

			if (result instanceof PageItems) {
				return getPage((PageItems<?>)result, params, resource.name());
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

	public static Option<Class<?>> getBodyResourceClass(Method method) {
		return Stream.of(
			method.getParameters()
		).filter(
			annotatedType -> annotatedType.isAnnotationPresent(BODY_ANNOTATION)
		).findFirst(
		).map(
			Option::of
		).orElseGet(
			Option::none
		).flatMap(
			parameter -> {
				Class<?> parameterClass = parameter.getType();

				if (parameterClass.equals(List.class)) {
					AnnotatedType annotatedType = parameter.getAnnotatedType();

					return Option.of(
						getTypeParameter(annotatedType, _LIST_TYPE_PARAMETER)
					).map(
						AnnotatedType::getType
					).map(
						type -> type instanceof Class ? (Class<?>)type : null
					);
				}

				return Option.of(parameterClass);
			}
		);
	}

	public static Optional<Form<Object>> getFormOptional(
		Method method, Function<String, Optional<Form<Object>>> function) {

		return Option.narrow(
			getBodyResourceClass(method)
		).map(
			Class::getName
		).flatMap(
			className -> Option.ofOptional(function.apply(className))
		).toJavaOptional();
	}

	public static Page getPage(List<?> list, String name) {
		PageItems<?> pageItems = new PageItems<>(list, list.size());

		Pagination pagination = new PaginationImpl(list.size(), 1);

		return new PageImpl<>(name, pageItems, pagination, emptyList());
	}

	public static Object getPage(
		PageItems<?> pageItems, List<?> paramInstances, String name) {

		Stream<?> stream = paramInstances.stream();

		Pagination pagination = stream.filter(
			Pagination.class::isInstance
		).map(
			Pagination.class::cast
		).findFirst(
		).orElseGet(
			() -> new PaginationImpl(pageItems.getTotalCount(), 1)
		);

		return new PageImpl<>(name, pageItems, pagination, emptyList());
	}

	public static Class<?>[] getParamClasses(Method method) {
		return Stream.of(
			method.getParameters()
		).<Class<?>>map(
			parameter -> {
				if (parameter.getAnnotation(Id.class) != null) {
					return Id.class;
				}
				else if (parameter.getAnnotation(ParentId.class) != null) {
					return ParentId.class;
				}
				else if (parameter.getAnnotation(BODY_ANNOTATION) != null) {
					return Body.class;
				}

				return parameter.getType();
			}
		).toArray(
			Class[]::new
		);
	}

	public static Resource getResource(Method method, String name) {
		Optional<ParentId> optionalParentId =
			getAnnotationFromParametersOptional(method, ParentId.class);

		if (optionalParentId.isPresent()) {
			ParentId parentId = optionalParentId.get();

			Class<? extends Identifier<?>> typeClass = parentId.value();

			Type type = typeClass.getAnnotation(Type.class);

			Item parent = Item.of(toLowercaseSlug(type.value()));

			return Nested.of(parent, name);
		}

		Optional<Id> optionalId = getAnnotationFromParametersOptional(
			method, Id.class);

		if (optionalId.isPresent()) {
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

	private static final TypeVariable<Class<List>> _LIST_TYPE_PARAMETER =
		List.class.getTypeParameters()[0];

}