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

import static com.liferay.apio.architect.internal.unsafe.Unsafe.unsafeCast;

import com.liferay.apio.architect.alias.BinaryFunction;
import com.liferay.apio.architect.annotation.FieldMode;
import com.liferay.apio.architect.annotation.Vocabulary;
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.RelativeURL;
import com.liferay.apio.architect.file.BinaryFile;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.internal.annotation.representor.processor.FieldData;
import com.liferay.apio.architect.internal.annotation.representor.processor.LinkedModelFieldData;
import com.liferay.apio.architect.internal.annotation.representor.processor.ListFieldData;
import com.liferay.apio.architect.internal.annotation.representor.processor.NestedParsedType;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType;
import com.liferay.apio.architect.internal.annotation.representor.processor.RelativeURLFieldData;
import com.liferay.apio.architect.language.AcceptLanguage;
import com.liferay.apio.architect.representor.BaseRepresentor;

import java.lang.reflect.Method;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides utility functions to fill a representor builder by using an instance
 * of an annotated method.
 *
 * @author Víctor Galán
 */
public class RepresentorTransformerUtil {

	/**
	 * Fills the builder using the parsed type provided.
	 *
	 * @param  firstStep the builder's first step
	 * @param  parsedType the parsed type
	 * @review
	 */
	public static void addCommonFields(
		BaseRepresentor.BaseFirstStep<?, ?, ?> firstStep,
		ParsedType parsedType) {

		List<LinkedModelFieldData> linkedModelFieldDataList =
			filterWritableFields(parsedType::getLinkedModelFieldDataList);

		linkedModelFieldDataList.forEach(
			linkedModelFieldData -> {
				Vocabulary.LinkedModel linkedModel =
					linkedModelFieldData.getLinkedModel();

				firstStep.addLinkedModel(
					linkedModelFieldData.getFieldName(),
					unsafeCast(linkedModel.value()),
					getMethodFunction(linkedModelFieldData.getMethod()));
			});

		List<FieldData> fieldDataList = filterWritableFields(
			parsedType::getFieldDataList);

		fieldDataList.forEach(
			fieldData -> _addBasicFields(firstStep, fieldData));

		List<ListFieldData> listFieldData = filterWritableFields(
			parsedType::getListFieldDataList);

		listFieldData.forEach(
			listField -> _addListFields(firstStep, listField));

		List<RelativeURLFieldData> relativeURLFieldDataList =
			filterWritableFields(parsedType::getRelativeURLFieldDataList);

		relativeURLFieldDataList.forEach(
			relativeURLFieldData -> {
				RelativeURL relativeURL = relativeURLFieldData.getRelativeURL();
				String key = relativeURLFieldData.getFieldName();
				Method method = relativeURLFieldData.getMethod();

				if (relativeURL.fromApplication()) {
					firstStep.addApplicationRelativeURL(
						key, getMethodFunction(method));
				}
				else {
					firstStep.addRelativeURL(key, getMethodFunction(method));
				}
			});

		List<NestedParsedType> nestedParsedTypes = filterWritableFields(
			parsedType::getParsedTypes);

		nestedParsedTypes.forEach(
			nestedParsedType -> {
				ParsedType nested = nestedParsedType.getParsedType();

				firstStep.addNested(
					nestedParsedType.getFieldName(),
					getMethodFunction(nestedParsedType.getMethod()),
					builder -> unsafeCast(
						NestedRepresentorTransformer.toRepresentor(
							nested, builder)));
			});

		List<NestedParsedType> nestedListParsedTypes = filterWritableFields(
			parsedType::getListParsedTypes);

		nestedListParsedTypes.forEach(
			nestedParsedType -> {
				ParsedType nested = nestedParsedType.getParsedType();

				firstStep.addNestedList(
					nestedParsedType.getFieldName(),
					getMethodFunction(nestedParsedType.getMethod()),
					builder -> unsafeCast(
						NestedRepresentorTransformer.toRepresentor(
							nested, builder)));
			});
	}

	public static <T extends FieldData> List<T> filterWritableFields(
		Supplier<List<T>> supplier) {

		List<T> list = supplier.get();

		Stream<T> stream = list.stream();

		return stream.filter(
			fieldData -> {
				Field field = fieldData.getField();

				FieldMode fieldMode = field.mode();

				if (fieldMode.equals(FieldMode.WRITE_ONLY) ||
					fieldMode.equals(FieldMode.READ_WRITE)) {

					return true;
				}

				return false;
			}
		).collect(
			Collectors.toList()
		);
	}

	public static <A, T, S> BiFunction<T, A, S> getMethodBiFunction(
		Method method) {

		return (t, a) -> Try.fromFallible(
			() -> (S)method.invoke(t, a)
		).orElse(
			null
		);
	}

	public static <T, S> Function<T, S> getMethodFunction(Method method) {
		return t -> Try.fromFallible(
			() -> (S)method.invoke(t)
		).orElse(
			null
		);
	}

	private static void _addBasicFields(
		BaseRepresentor.BaseFirstStep<?, ?, ?> firstStep, FieldData fieldData) {

		Field field = fieldData.getField();

		Method method = fieldData.getMethod();

		Class<?> returnType = method.getReturnType();

		String key = field.value();

		if (returnType == String.class) {
			_addStringFields(firstStep, method, key);
		}
		else if (returnType == Date.class) {
			firstStep.addDate(key, getMethodFunction(method));
		}
		else if (returnType == Boolean.class) {
			firstStep.addBoolean(key, getMethodFunction(method));
		}
		else if (returnType == BinaryFile.class) {
			firstStep.addBinary(key, (BinaryFunction)getMethodFunction(method));
		}
		else if (Number.class.isAssignableFrom(returnType)) {
			firstStep.addNumber(key, getMethodFunction(method));
		}
	}

	private static void _addListFields(
		BaseRepresentor.BaseFirstStep<?, ?, ?> firstStep,
		ListFieldData listFieldData) {

		Class<?> listClass = listFieldData.getListType();
		String key = listFieldData.getFieldName();
		Method method = listFieldData.getMethod();

		if (listClass == String.class) {
			firstStep.addStringList(key, getMethodFunction(method));
		}
		else if (listClass == Boolean.class) {
			firstStep.addBooleanList(key, getMethodFunction(method));
		}
		else if (Number.class.isAssignableFrom(listClass)) {
			firstStep.addNumberList(key, getMethodFunction(method));
		}
	}

	private static void _addStringFields(
		BaseRepresentor.BaseFirstStep<?, ?, ?> firstStep, Method method,
		String key) {

		Class<?>[] parameters = method.getParameterTypes();

		if (parameters.length > 0) {
			Class<?> firstParameter = parameters[0];

			if (firstParameter == Locale.class) {
				firstStep.addLocalizedStringByLocale(
					key, getMethodBiFunction(method));
			}
			else if (firstParameter == AcceptLanguage.class) {
				firstStep.addLocalizedStringByLanguage(
					key, getMethodBiFunction(method));
			}
		}
		else {
			firstStep.addString(key, getMethodFunction(method));
		}
	}

}