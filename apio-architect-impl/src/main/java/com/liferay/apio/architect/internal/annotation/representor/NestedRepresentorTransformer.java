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

package com.liferay.apio.architect.internal.annotation.representor;

import static com.liferay.apio.architect.internal.annotation.representor.RepresentorTransformerUtil.addCommonFields;
import static com.liferay.apio.architect.internal.annotation.representor.RepresentorTransformerUtil.getMethodFunction;

import com.liferay.apio.architect.annotation.Vocabulary.RelatedCollection;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType;
import com.liferay.apio.architect.internal.annotation.representor.processor.RelatedCollectionFieldData;
import com.liferay.apio.architect.representor.NestedRepresentor;

import java.util.List;

/**
 * Provides a utility function to transform a class annotated with {@code Type}
 * into a nested representor.
 *
 * @author Víctor Galán
 * @review
 */
public class NestedRepresentorTransformer {

	/**
	 * Uses the nested representor builder to transform a parsed type into a
	 * nested representor.
	 *
	 * @param  parsedType the parsed type
	 * @param  builder the nested representor builder
	 * @return the nested representor
	 * @review
	 */
	public static NestedRepresentor<?> toRepresentor(
		ParsedType parsedType, NestedRepresentor.Builder<?> builder) {

		Type type = parsedType.getType();

		NestedRepresentor.FirstStep<?> firstStep = builder.types(
			type.value()
		);

		_processFields(parsedType, firstStep);

		return firstStep.build();
	}

	private static void _processFields(
		ParsedType parsedType, NestedRepresentor.FirstStep<?> firstStep) {

		List<RelatedCollectionFieldData> relatedCollectionFieldDataList =
			parsedType.getRelatedCollectionFieldDataList();

		relatedCollectionFieldDataList.forEach(
			relatedCollectionFieldData -> {
				RelatedCollection relatedCollection =
					relatedCollectionFieldData.getRelatedCollection();

				firstStep.addRelatedCollection(
					relatedCollectionFieldData.getFieldName(),
					relatedCollection.value(),
					getMethodFunction(relatedCollectionFieldData.getMethod()));
			});

		addCommonFields(firstStep, parsedType);
	}

}