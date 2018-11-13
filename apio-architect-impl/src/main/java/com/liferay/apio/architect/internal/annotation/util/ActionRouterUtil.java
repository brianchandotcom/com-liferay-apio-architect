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
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.hasAnnotation;

import static io.leangen.geantyref.GenericTypeReflector.getTypeParameter;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.isNull;

import static java.util.Collections.emptyList;

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

import io.vavr.control.Option;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
			parameter -> Match(
				parameter
			).of(
				Case($(hasAnnotation(Id.class)), Id.class),
				Case($(hasAnnotation(ParentId.class)), ParentId.class),
				Case($(hasAnnotation(BODY_ANNOTATION)), Body.class),
				Case($(), Parameter::getType)
			)
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

		if (returnType.equals(PageItems.class)) {
			return Page.class;
		}

		if (returnType.equals(List.class)) {
			return Page.class;
		}

		if (returnType.getAnnotation(Type.class) != null) {
			return SingleModel.class;
		}

		if (returnType.equals(void.class)) {
			return Void.class;
		}

		return returnType;
	}

	/**
	 * Updates the list
	 *
	 * @param paramInstances
	 * @param bodyFunction
	 * @return
	 */
	public static Object[] updateParams(
		List<?> paramInstances, Function<Body, Object> bodyFunction) {

		Object[] updatedParams = paramInstances.toArray(new Object[0]);

		for (int i = 0; i < paramInstances.size(); i++) {
			if (updatedParams[i] instanceof Body) {
				updatedParams[i] = bodyFunction.apply((Body)updatedParams[i]);
			}

			if (updatedParams[i] instanceof Resource.Id) {
				updatedParams[i] = ((Resource.Id)updatedParams[i]).asObject();
			}
		}

		return updatedParams;
	}

	/**
	 * Updates the return of an action according to some conditions:
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
	 * @param  object the object being converter
	 * @param  params the list of param
	 * @param  name the resource's name
	 * @return the updated result
	 * @review
	 */
	public static Object updateReturn(
		Object object, List<?> params, String name) {

		return Match(
			object
		).of(
			Case($(isNull()), () -> null),
			Case($(instanceOf(List.class)), list -> getPage(list, name)),
			Case(
				$(instanceOf(PageItems.class)),
				pageItems -> getPage(pageItems, params, name)),
			Case($(), () -> new SingleModelImpl<>(object, name))
		);
	}

	private static final TypeVariable<Class<List>> _LIST_TYPE_PARAMETER =
		List.class.getTypeParameters()[0];

}