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

import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation;

import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.Vocabulary.BidirectionalModel;
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.LinkedModel;
import com.liferay.apio.architect.annotation.Vocabulary.RelatedCollection;
import com.liferay.apio.architect.annotation.Vocabulary.RelativeURL;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType.Builder;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import java.util.List;

/**
 * Class responsible of extracting the data of an annotated type and construct a
 * {@link ParsedType} with that information.
 *
 * @author Víctor Galán
 * @review
 */
public class TypeProcessor {

	/**
	 * Returns a parsed type created from the type provided.
	 *
	 * @param  typeClass the annotated class
	 * @return the parsed type
	 * @review
	 */
	public static ParsedType proccesType(
		Class<? extends Identifier> typeClass) {

		return _processType(typeClass, false);
	}

	private static void _addListType(
		Builder builder, Field field, Method method) {

		ParameterizedType parameterizedType =
			(ParameterizedType)method.getGenericReturnType();

		Class<?> listClass =
			(Class<?>)parameterizedType.getActualTypeArguments()[0];

		if (listClass.isAnnotationPresent(Type.class)) {
			ParsedType parsedType = _processType(listClass, true);

			builder.addListParsedType(
				new NestedParsedType(field, method, parsedType));
		}
		else {
			ListFieldData listFieldData = new ListFieldData(
				field, method, listClass);

			builder.addListFieldData(listFieldData);
		}
	}

	private static IdFieldData _getIdField(Class<?> typeClass) {
		return Try.fromFallible(
			() -> getMethodsListWithAnnotation(typeClass, Id.class)
		).filter(
			methods -> !methods.isEmpty()
		).map(
			methods -> methods.get(0)
		).map(
			method -> new IdFieldData(method, method.getAnnotation(Id.class))
		).orElse(
			null
		);
	}

	private static void _processMethod(Builder builder, Method method) {
		LinkedModel linkedModel = method.getAnnotation(LinkedModel.class);
		Field field = method.getAnnotation(Field.class);

		if (linkedModel != null) {
			LinkedModelFieldData linkedModelFieldData =
				new LinkedModelFieldData(field, method, linkedModel);

			builder.addLinkedModelFieldData(linkedModelFieldData);

			return;
		}

		RelatedCollection relatedCollection = method.getAnnotation(
			RelatedCollection.class);

		if (relatedCollection != null) {
			RelatedCollectionFieldData relatedCollectionFieldData =
				new RelatedCollectionFieldData(
					field, method, relatedCollection);

			builder.addRelatedCollectionFieldData(relatedCollectionFieldData);

			return;
		}

		RelativeURL relativeURL = method.getAnnotation(RelativeURL.class);

		if (relativeURL != null) {
			RelativeURLFieldData relativeURLFieldData =
				new RelativeURLFieldData(field, method, relativeURL);

			builder.addRelativeURLFieldData(relativeURLFieldData);

			return;
		}

		BidirectionalModel bidirectionalModel = method.getAnnotation(
			BidirectionalModel.class);

		if (bidirectionalModel != null) {
			BidirectionalFieldData bidirectionalFieldData =
				new BidirectionalFieldData(field, method, bidirectionalModel);

			builder.addBidirectionalFieldData(bidirectionalFieldData);

			return;
		}

		Class<?> returnType = method.getReturnType();

		if (returnType == List.class) {
			_addListType(builder, field, method);
		}
		else if (returnType.isAnnotationPresent(Type.class)) {
			ParsedType parsedType = _processType(returnType, true);

			builder.addParsedType(
				new NestedParsedType(field, method, parsedType));
		}
		else {
			FieldData fieldData = new FieldData(field, method);

			builder.addFieldData(fieldData);
		}
	}

	private static ParsedType _processType(Class<?> typeClass, boolean nested) {
		Type type = typeClass.getAnnotation(Type.class);

		Builder builder = new Builder(type, typeClass);

		if (!nested) {
			IdFieldData idFieldData = _getIdField(typeClass);

			builder.idFieldData(idFieldData);
		}

		List<Method> methods = getMethodsListWithAnnotation(
			typeClass, Field.class);

		methods.forEach(method -> _processMethod(builder, method));

		return builder.build();
	}

}