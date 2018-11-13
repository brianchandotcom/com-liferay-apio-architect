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

import static java.util.Objects.nonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Javier Gamarra
 */
public class AnnotationUtil {

	public static <A extends Annotation> Optional<A> findAnnotation(
		Class<A> annotationClass, Method method) {

		return Optional.ofNullable(
			method.getAnnotation(annotationClass)
		).map(
			Optional::of
		).orElseGet(
			() -> Stream.of(
				method.getAnnotations()
			).map(
				Annotation::annotationType
			).map(
				Class::getAnnotations
			).flatMap(
				Stream::of
			).filter(
				annotationClass::isInstance
			).map(
				annotationClass::cast
			).findFirst()
		);
	}

	public static <A extends Annotation> Optional<A>
		getAnnotationFromParametersOptional(
			Method method, Class<A> annotationClass) {

		return Stream.of(
			method.getParameterAnnotations()
		).flatMap(
			Stream::of
		).filter(
			annotationType -> annotationClass.isAssignableFrom(
				annotationType.getClass())
		).map(
			annotationClass::cast
		).findFirst();
	}

	public static <A extends Annotation> Predicate<Parameter> hasAnnotation(
		Class<A> annotationClass) {

		return parameter -> nonNull(parameter.getAnnotation(annotationClass));
	}

	private AnnotationUtil() {
	}

}