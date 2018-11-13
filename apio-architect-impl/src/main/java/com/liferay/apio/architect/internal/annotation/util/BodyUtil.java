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

import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.getAnnotationFromParametersOptional;

import com.liferay.apio.architect.annotation.Body;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Alejandro Hernández
 */
public class BodyUtil {

	public static boolean isListBody(Method method) {
		return Stream.of(
			method.getParameters()
		).filter(
			annotatedType -> annotatedType.isAnnotationPresent(Body.class)
		).findFirst(
		).map(
			Parameter::getType
		).map(
			List.class::isAssignableFrom
		).orElse(
			false
		);
	}

	public static boolean needsBody(Method method) {
		Optional<Body> optional = getAnnotationFromParametersOptional(
			method, Body.class);

		return optional.isPresent();
	}

}