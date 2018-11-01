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

import com.liferay.apio.architect.annotation.Vocabulary.Field;

import java.lang.reflect.Method;

/**
 * Holds information about a field annotated with {@link Field} that is a list.
 *
 * @author Víctor Galán
 * @review
 */
public class ListFieldData extends FieldData {

	public ListFieldData(Field field, Method method, Class<?> listType) {
		super(field, method);

		_listType = listType;
	}

	/**
	 * Returns the list's type.
	 *
	 * @return the type of the list
	 * @review
	 */
	public Class<?> getListType() {
		return _listType;
	}

	private final Class<?> _listType;

}