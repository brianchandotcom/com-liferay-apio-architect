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

package com.liferay.apio.architect.internal.alias;

import com.liferay.apio.architect.internal.action.ActionSemantics;

import io.vavr.Function3;

import javax.servlet.http.HttpServletRequest;

/**
 * Defines a type alias for a function that receives the {@link
 * ActionSemantics}, the HTTP request, and the class being provided and returns
 * an instance of that class.
 *
 * @author Alejandro Hernández
 */
public interface ProvideFunction
	extends Function3<ActionSemantics, HttpServletRequest, Class<?>, Object> {
}