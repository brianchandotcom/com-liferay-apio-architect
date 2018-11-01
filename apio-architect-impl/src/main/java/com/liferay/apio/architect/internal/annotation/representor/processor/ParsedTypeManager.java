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

import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import static org.slf4j.LoggerFactory.getLogger;

import com.liferay.apio.architect.internal.annotation.representor.ActionRouterTypeExtractor;
import com.liferay.apio.architect.router.ActionRouter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;

/**
 * Provides methods to retrieve information provided by the different {@link
 * ParsedType} instances.
 *
 * @author Víctor Galán
 * @review
 */
@Component(service = ParsedTypeManager.class)
public class ParsedTypeManager {

	@Deactivate
	public void deactivate() {
		INSTANCE.clear();
	}

	/**
	 * Returns the parsed type extracted from a given className.
	 *
	 * @param  key the className of the annotated type
	 * @return the parsed type extracted from the type
	 * @review
	 */
	public Optional<ParsedType> getParsedType(String key) {
		return INSTANCE.getParsedTypeOptional(key, this::_computeParsedTypes);
	}

	/**
	 * Returns all the parsed types.
	 *
	 * @return the parsed type extracted from the type
	 * @review
	 */
	public Map<String, ParsedType> getParsedTypes() {
		return INSTANCE.getParsedTypesMap(this::_computeParsedTypes);
	}

	private void _computeParsedTypes() {
		Stream<ActionRouter<?>> actionRouterStream = _actionRouters.stream();

		actionRouterStream.map(
			ActionRouterTypeExtractor::extractTypeClass
		).forEach(
			typeClassTry -> typeClassTry.voidFold(
				__ -> _logger.warn(
					"Unable to extract class from action router"),
				typeClass -> {
					ParsedType parsedType = TypeProcessor.processType(
						typeClass);

					INSTANCE.putParsedType(typeClass.getName(), parsedType);
				})
		);
	}

	@Reference(
		cardinality = MULTIPLE, policyOption = GREEDY,
		service = ActionRouter.class
	)
	private List<ActionRouter<?>> _actionRouters;

	private final Logger _logger = getLogger(getClass());

}