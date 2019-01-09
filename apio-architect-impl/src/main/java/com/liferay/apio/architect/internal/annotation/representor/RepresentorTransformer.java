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

import static com.liferay.apio.architect.annotation.Vocabulary.LinkTo.ResourceType.CHILD_COLLECTION;
import static com.liferay.apio.architect.annotation.Vocabulary.LinkTo.ResourceType.GENERIC_PARENT_COLLECTION;
import static com.liferay.apio.architect.internal.annotation.representor.RepresentorTransformerUtil.addCommonFields;
import static com.liferay.apio.architect.internal.annotation.representor.RepresentorTransformerUtil.filterWritableFields;
import static com.liferay.apio.architect.internal.annotation.representor.RepresentorTransformerUtil.getMethodFunction;
import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.unsafe.Unsafe.unsafeCast;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import com.liferay.apio.architect.annotation.Vocabulary.BidirectionalModel;
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.LinkTo;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.annotation.representor.processor.FieldData;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType;
import com.liferay.apio.architect.internal.representor.RepresentorImpl;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.representor.Representor.FirstStep;

import io.vavr.control.Try;

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
 * @author Alejandro Hernández
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
			t -> Try.of(
				parsedType::getIdMethod
			).mapTry(
				method -> (S)method.invoke(t)
			).getOrNull()
		);

		_processFields(parsedType, firstStep);

		return firstStep.build();
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

		List<FieldData<BidirectionalModel>> bidirectionalFieldDataList =
			filterWritableFields(parsedType::getBidirectionalFieldDataList);

		bidirectionalFieldDataList.forEach(
			bidirectionalFieldData -> {
				BidirectionalModel bidirectionalModel =
					bidirectionalFieldData.getData();

				Field field = bidirectionalModel.field();

				firstStep.addBidirectionalModel(
					bidirectionalFieldData.getFieldName(), field.value(),
					unsafeCast(bidirectionalModel.modelClass()),
					getMethodFunction(bidirectionalFieldData.getMethod()));
			});

		List<FieldData<LinkTo>> linkToFieldDataList = filterWritableFields(
			parsedType::getLinkToFieldDataList);

		for (FieldData<LinkTo> fieldData : linkToFieldDataList) {
			LinkTo linkTo = fieldData.getData();

			if (CHILD_COLLECTION.equals(linkTo.resourceType())) {
				firstStep.addRelatedCollection(
					fieldData.getFieldName(), linkTo.resource());
			}
			else if (GENERIC_PARENT_COLLECTION.equals(linkTo.resourceType())) {
				Method method = fieldData.getMethod();

				firstStep.addRelatedCollection(
					fieldData.getFieldName(), linkTo.resource(),
					model -> Try.of(
						() -> method.invoke(model)
					).getOrNull());

				Class<? extends Identifier<?>> typeClass = linkTo.resource();

				Type type = typeClass.getAnnotation(Type.class);

				String name = toLowercaseSlug(type.value());

				INSTANCE.putReusableIdentifierClass(
					name, method.getReturnType());
			}
		}

		addCommonFields(firstStep, parsedType);
	}

}