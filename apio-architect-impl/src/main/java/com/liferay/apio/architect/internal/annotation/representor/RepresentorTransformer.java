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
import static com.liferay.apio.architect.internal.annotation.representor.RepresentorTransformerUtil.filterWritableFields;
import static com.liferay.apio.architect.internal.annotation.representor.RepresentorTransformerUtil.getMethodFunction;
import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.unsafe.Unsafe.unsafeCast;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation;

import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.Vocabulary;
import com.liferay.apio.architect.annotation.Vocabulary.BidirectionalModel;
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.annotation.representor.processor.BidirectionalFieldData;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType;
import com.liferay.apio.architect.internal.annotation.representor.processor.RelatedCollectionFieldData;
import com.liferay.apio.architect.internal.representor.RepresentorImpl;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.representor.Representor.FirstStep;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Provides a utility function that transforms a parsed type into a representor
 *
 * @author Alejandro Hernandez
 * @author Víctor Galán
 * @review
 */
public class RepresentorTransformer {

	/**
	 * Transforms a parsed type into a representor.
	 *
	 * @param  parsedType the parsed type {@link ParsedType}
	 * @param  nameFunction the function that gets a class's {@link
	 *         com.liferay.apio.architect.resource.CollectionResource} name
	 * @param  relatedCollections the list of the related collections of all
	 *         representors
	 * @return the instance of representor created
	 * @review
	 */
	public static <T extends Identifier<S>, S> Representor<T> toRepresentor(
		ParsedType parsedType,
		Function<Class<? extends Identifier<?>>, String> nameFunction,
		Map<String, List<RelatedCollection<?, ?>>> relatedCollections) {

		Type type = parsedType.getType();

		Class<T> typeClass = unsafeCast(parsedType.getTypeClass());

		Representor.Builder<T, S> builder = _createBuilder(
			typeClass, nameFunction, unsafeCast(relatedCollections));

		FirstStep<T> firstStep = builder.types(
			type.value()
		).identifier(
			s -> Try.fromFallible(
				() -> getMethodsListWithAnnotation(typeClass, Id.class)
			).filter(
				methods -> !methods.isEmpty()
			).map(
				methods -> methods.get(0)
			).map(
				identifierMethod -> (S)identifierMethod.invoke(s)
			).orElse(
				null
			)
		);

		_processFields(parsedType, firstStep);

		return firstStep.build();
	}

	private static void _addReusableClass(String name, Class returnType) {
		INSTANCE.putReusableIdentifierClass(name, returnType);
	}

	private static <T extends Identifier<S>, S> Representor.Builder<T, S>
		_createBuilder(
			Class<T> typeClass,
			Function<Class<? extends Identifier<?>>, String> nameFunction,
			Map<String, List<RelatedCollection<T, ?>>> relatedCollections) {

		Supplier<List<RelatedCollection<T, ?>>> relatedCollectionsSupplier =
			() -> relatedCollections.get(typeClass.getName());

		BiConsumer<Class<?>, RelatedCollection<T, ?>> biConsumer =
			(identifierClass, relatedCollection) -> {
				List<RelatedCollection<T, ?>> list =
					relatedCollections.computeIfAbsent(
						identifierClass.getName(), __ -> new ArrayList<>());

				list.add(relatedCollection);
			};

		return new RepresentorImpl.BuilderImpl<>(
			typeClass, nameFunction, biConsumer, relatedCollectionsSupplier);
	}

	private static <T extends Identifier<?>> void
		_processFields(ParsedType parsedType, FirstStep<T> firstStep) {

		List<BidirectionalFieldData> bidirectionalFieldDataList =
			filterWritableFields(parsedType::getBidirectionalFieldDataList);

		bidirectionalFieldDataList.forEach(
			bidirectionalFieldData -> {
				BidirectionalModel bidirectionalModel =
					bidirectionalFieldData.getBidirectionalModel();

				Field bidirectionalModelField = bidirectionalModel.field();

				firstStep.addBidirectionalModel(
					bidirectionalFieldData.getFieldName(),
					bidirectionalModelField.value(),
					unsafeCast(bidirectionalModel.modelClass()),
					getMethodFunction(bidirectionalFieldData.getMethod()));
			});

		List<RelatedCollectionFieldData> relatedCollectionFieldDataList =
			filterWritableFields(parsedType::getRelatedCollectionFieldDataList);

		relatedCollectionFieldDataList.forEach(
			relatedCollectionFieldData -> {
				Vocabulary.RelatedCollection relatedCollection =
					relatedCollectionFieldData.getRelatedCollection();

				String key = relatedCollectionFieldData.getFieldName();
				Method method = relatedCollectionFieldData.getMethod();

				if (relatedCollection.reusable()) {
					firstStep.addRelatedCollection(
						key, relatedCollection.value(),
						model -> Try.fromFallible(
							() -> method.invoke(model)
						).orElse(
							null
						));

					Class<? extends Identifier<?>> typeClass =
						relatedCollection.value();

					Type type = typeClass.getAnnotation(Type.class);

					String name = toLowercaseSlug(type.value());

					_addReusableClass(name, method.getReturnType());
				}
				else {
					firstStep.addRelatedCollection(
						key, relatedCollection.value());
				}
			});

		addCommonFields(firstStep, parsedType);
	}

}