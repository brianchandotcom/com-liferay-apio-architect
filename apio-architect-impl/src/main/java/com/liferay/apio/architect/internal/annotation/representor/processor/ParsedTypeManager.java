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

import static io.leangen.geantyref.GenericTypeReflector.annotate;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import static org.slf4j.LoggerFactory.getLogger;

import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.router.ActionRouter;

import io.leangen.geantyref.GenericTypeReflector;

import java.lang.reflect.AnnotatedType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	 * Returns the parsed type extracted from a given {@code className}, if
	 * present; otherwise returns {@code Optional#empty()}.
	 *
	 * @review
	 */
	public Optional<ParsedType> getParsedTypeOptional(String key) {
		Map<String, ParsedType> parsedTypes = getParsedTypes();

		return Optional.ofNullable(parsedTypes.get(key));
	}

	/**
	 * Returns all the parsed types.
	 *
	 * @return the parsed type extracted from the type
	 * @review
	 */
	public Map<String, ParsedType> getParsedTypes() {
		return Optional.ofNullable(
			INSTANCE.getParsedTypesMap(
				() -> _actionRouters.forEach(this::_compute))
		).orElseGet(
			Collections::emptyMap
		);
	}

	private void _compute(ActionRouter actionRouter) {
		AnnotatedType annotatedType = GenericTypeReflector.getTypeParameter(
			annotate(actionRouter.getClass()),
			ActionRouter.class.getTypeParameters()[0]);

		if (annotatedType == null) {
			_logger.warn(
				"Unable to extract class from action router {}", actionRouter);

			return;
		}

		if (!(annotatedType.getType() instanceof Class)) {
			_logger.warn("{} is not a valid class", annotatedType.getType());

			return;
		}

		Class<?> clazz = (Class)annotatedType.getType();

		if (!Identifier.class.isAssignableFrom(clazz)) {
			_logger.warn("Class {} must implement {}", clazz, Identifier.class);

			return;
		}

		if (!clazz.isAnnotationPresent(Type.class)) {
			_logger.warn(
				"ActionRouter {} resource ({}) is not annotated with {}",
				actionRouter, annotatedType.getType(), Type.class);

			return;
		}

		ParsedType parsedType = TypeProcessor.processType(
			(Class<? extends Identifier>)clazz);

		INSTANCE.putParsedType(clazz.getName(), parsedType);
	}

	@Reference(
		cardinality = MULTIPLE, policyOption = GREEDY,
		service = ActionRouter.class
	)
	private List<ActionRouter<?>> _actionRouters;

	private final Logger _logger = getLogger(getClass());

}