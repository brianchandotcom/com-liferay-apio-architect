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

/**
 * Provides utility functions for dealing with annotations.
 *
 * <p>This class should not be instantiated.
 *
 * @author Alejandro Hernández
 * @author Javier Gamarra
 */
public final class AnnotationUtil {

	/**
	 * Tries to extract the first occurrence an annotation of a certain class
	 * inside a method's parameters. Returns {@code null} if the annotation
	 * could not be found.
	 *
	 * @param  method the method whose parameter annotations will be searched
	 * @param  annotationClass the annotation class to search
	 * @return the annotation, if found; {@code null} otherwise
	 * @review
	 */
	public static <A extends Annotation> A findAnnotationInAnyParameter(
		Method method, Class<A> annotationClass) {

		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(annotationClass)) {
				return parameter.getAnnotation(annotationClass);
			}
		}

		return null;
	}

	/**
	 * Tries to extract an annotation of a certain class directly annotated on
	 * to the method or any of its annotations.
	 *
	 * @param  method the method in which to look for
	 * @param  annotationClass the annotation class to search
	 * @return the annotation, if found; {@code null} otherwise
	 * @review
	 */
	public static <A extends Annotation> A
		findAnnotationInMethodOrInItsAnnotations(
			Method method, Class<A> annotationClass) {

		if (method.isAnnotationPresent(annotationClass)) {
			return method.getAnnotation(annotationClass);
		}

		for (Annotation annotation : method.getAnnotations()) {
			Class<? extends Annotation> annotationType =
				annotation.annotationType();

			if (annotationType.isAnnotationPresent(annotationClass)) {
				return annotationType.getAnnotation(annotationClass);
			}
		}

		return null;
	}

	private AnnotationUtil() {
	}

}