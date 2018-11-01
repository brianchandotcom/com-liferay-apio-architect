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

import static org.junit.Assert.assertThat;

import com.liferay.apio.architect.annotation.Vocabulary;
import com.liferay.apio.architect.annotation.Vocabulary.BidirectionalModel;
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.RelativeURL;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.wiring.osgi.util.GenericUtil;

import java.lang.reflect.Method;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;

/**
 * @author Víctor Galán Grande
 */
public class TypeProcessortTestUtil {

	public static <T> T getIdentifierType(Class<?> identifierClass) {
		return (T)GenericUtil.getGenericTypeArgumentTry(
			identifierClass, Identifier.class, 0
		).orElse(
			null
		);
	}

	public static <T extends FieldData> List<T> getOrderedList(
		Supplier<List<T>> supplier) {

		List<T> list = supplier.get();

		list.sort(Comparator.comparing(FieldData::getFieldName));

		return list;
	}

	public static void testBidirectionalData(
		BidirectionalFieldData bidirectionalFieldData, String fieldName,
		String bidirectionalName, Class<?> identifierClass) {

		BidirectionalModel bidirectionalModel =
			bidirectionalFieldData.getBidirectionalModel();

		Class<?> identifierType = getIdentifierType(identifierClass);

		assertThat(
			bidirectionalModel.modelClass(), IsEqual.equalTo(identifierClass));

		assertThat(
			bidirectionalModel.field().value(), Is.is(bidirectionalName));

		testFieldData(bidirectionalFieldData, fieldName, identifierType);
	}

	public static void testFieldData(
		FieldData fieldData, String fieldName, Class<?> returnType) {

		Field field = fieldData.getField();
		Method method = fieldData.getMethod();

		assertThat(field.value(), Is.is(fieldName));
		assertThat(method.getReturnType(), IsEqual.equalTo(returnType));
	}

	public static void testLinkedModelData(
		LinkedModelFieldData linkedModelFieldData, String fieldName,
		Class<?> identifierClass) {

		Vocabulary.LinkedModel linkedModel =
			linkedModelFieldData.getLinkedModel();
		Class<?> identifierType = getIdentifierType(identifierClass);

		assertThat(linkedModel.value(), IsEqual.equalTo(identifierClass));

		testFieldData(linkedModelFieldData, fieldName, identifierType);
	}

	public static void testListFieldData(
		ListFieldData listFieldData, String fieldName, Class<?> listType) {

		assertThat(listFieldData.getListType(), IsEqual.equalTo(listType));

		testFieldData(listFieldData, fieldName, List.class);
	}

	public static void testRelatedCollectionData(
		RelatedCollectionFieldData relatedCollectionFieldData, String fieldName,
		Class<?> identifierClass) {

		Vocabulary.RelatedCollection relatedCollection =
			relatedCollectionFieldData.getRelatedCollection();
		Class<?> identifierType = getIdentifierType(identifierClass);

		assertThat(
			relatedCollection.value(), Is.is(IsEqual.equalTo(identifierClass)));

		testFieldData(relatedCollectionFieldData, fieldName, identifierType);
	}

	public static void testRelativeURLData(
		RelativeURLFieldData relativeURLFieldData, String fieldName,
		boolean fromApplication) {

		RelativeURL relativeURL = relativeURLFieldData.getRelativeURL();

		assertThat(relativeURL.fromApplication(), Is.is(fromApplication));

		testFieldData(relativeURLFieldData, fieldName, String.class);
	}

}