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

package com.liferay.apio.architect.internal.annotation;

import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.annotation.representor.RepresentorTransformer;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedTypeManager;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.representor.Representor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Computes all the representor using the parsed types of all classes annotated
 * with {@link Type}
 *
 * @author Víctor Galán
 * @review
 */
@Component(service = ActionRouterRepresentorManager.class)
public class ActionRouterRepresentorManager {

	@Activate
	public void activate() {
		INSTANCE.clear();
	}

	/**
	 * Creates representors for each registered parsed types.
	 *
	 * @param nameFunction the function that gets a class's {@code
	 *        com.liferay.apio.architect.resource.CollectionResource} name
	 * @param relatedCollections the list of the related collections of all
	 *        representors
	 */
	public void computeRepresentors(
		Function<Class<? extends Identifier<?>>, String> nameFunction,
		Map<String, List<RelatedCollection<?, ?>>> relatedCollections) {

		Map<String, ParsedType> parsedTypesMap =
			_parsedTypeManager.getParsedTypes();

		Collection<ParsedType> parsedTypes = parsedTypesMap.values();

		parsedTypes.forEach(
			parsedType -> {
				Representor<?> representor =
					RepresentorTransformer.toRepresentor(
						parsedType, nameFunction, relatedCollections);

				Type type = parsedType.getType();
				Class<?> typeClass = parsedType.getTypeClass();

				String name = toLowercaseSlug(type.value());

				INSTANCE.putName(typeClass.getName(), name);
				INSTANCE.putIdentifierClass(name, (Class)typeClass);
				INSTANCE.putRepresentor(name, representor);
			});
	}

	@Deactivate
	public void deactivate() {
		INSTANCE.clear();
	}

	@Reference
	private ParsedTypeManager _parsedTypeManager;

}