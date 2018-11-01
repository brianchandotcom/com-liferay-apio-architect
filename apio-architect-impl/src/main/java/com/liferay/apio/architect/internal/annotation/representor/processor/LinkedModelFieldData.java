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
import com.liferay.apio.architect.annotation.Vocabulary.LinkedModel;

import java.lang.reflect.Method;

/**
 * Holds information about a field annotated with {@link LinkedModel}
 *
 * @author Víctor Galán
 * @review
 */
public class LinkedModelFieldData extends FieldData {

	public LinkedModelFieldData(
		Field field, Method method, LinkedModel linkedModel) {

		super(field, method);

		_linkedModel = linkedModel;
	}

	/**
	 * Returns the linkedModel annotation
	 *
	 * @return the linkedModel
	 * @review
	 */
	public LinkedModel getLinkedModel() {
		return _linkedModel;
	}

	private final LinkedModel _linkedModel;

}