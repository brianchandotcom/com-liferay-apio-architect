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

package com.liferay.apio.architect.internal.annotation.representor.processor;

import com.liferay.apio.architect.annotation.Id;

import java.lang.reflect.Method;

/**
 * Holds information about a field annotated with {@link ID}
 *
 * @author Víctor Galán
 * @review
 */
public class IdFieldData {

	public IdFieldData(Method method, Id id) {
		_method = method;
		_id = id;
	}

	/**
	 * Returns the ID annotation associated to a field
	 *
	 * @return the ID annotation
	 * @review
	 */
	public Id getId() {
		return _id;
	}

	/**
	 * Returns the method name in which the annotation was placed
	 *
	 * @return the method name
	 * @review
	 */
	public Method getMethod() {
		return _method;
	}

	private final Id _id;
	private final Method _method;

}