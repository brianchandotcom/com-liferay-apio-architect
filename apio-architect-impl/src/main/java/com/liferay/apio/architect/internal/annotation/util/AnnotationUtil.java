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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Javier Gamarra
 */
public class AnnotationUtil {

	private AnnotationUtil() {
		super();
	}

	public static boolean _hasAnnotation(
		Parameter parameter, Class<? extends Annotation> annotation) {

		return parameter.getAnnotation(annotation) != null;
	}

	public static Optional<Annotation> _getAnnotationFromMethodParameters(
		Method method,
		Class<? extends Annotation> annotation) {

		return Stream.of(
			method.getParameterAnnotations()
		).flatMap(
			Stream::of
		).filter(
			annotationType -> annotation.isAssignableFrom(
				annotationType.getClass())
		).findFirst();
	}
}
