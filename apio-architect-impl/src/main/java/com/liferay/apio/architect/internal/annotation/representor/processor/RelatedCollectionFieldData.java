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
import com.liferay.apio.architect.annotation.Vocabulary.RelatedCollection;

import java.lang.reflect.Method;

/**
 * Holds information about a field annotated with {@link RelatedCollection}.
 *
 * @author Víctor Galán
 * @review
 */
public class RelatedCollectionFieldData extends FieldData {

	public RelatedCollectionFieldData(
		Field field, Method method, RelatedCollection relatedCollection) {

		super(field, method);

		_relatedCollection = relatedCollection;
	}

	/**
	 * Returns the relatedCollection annotation.
	 *
	 * @return the relatedCollection annotation
	 * @review
	 */
	public RelatedCollection getRelatedCollection() {
		return _relatedCollection;
	}

	private final RelatedCollection _relatedCollection;

}