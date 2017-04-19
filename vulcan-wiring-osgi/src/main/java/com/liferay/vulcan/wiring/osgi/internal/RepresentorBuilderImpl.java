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

package com.liferay.vulcan.wiring.osgi.internal;

import com.liferay.vulcan.representor.builder.RepresentorBuilder;
import com.liferay.vulcan.wiring.osgi.RelationTuple;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class RepresentorBuilderImpl<T> implements RepresentorBuilder<T> {

	public RepresentorBuilderImpl(
		Class<T> modelClass,
		ConcurrentHashMap<String, Function<?, String>> identifierFunctions,
		Map<String, Function<?, Object>> fieldFunctions,
		List<RelationTuple<?, ?>> relationTypes, List<String> types) {

		_modelClass = modelClass;
		_identifierFunctions = identifierFunctions;
		_fieldFunctions = fieldFunctions;
		_relationTypes = relationTypes;
		_types = types;
	}

	@Override
	public FirstStep<T> identifier(Function<T, String> identifierFunction) {
		_identifierFunctions.put(_modelClass.getName(), identifierFunction);

		return new FirstStep<T>() {

			@Override
			public <S> FirstStep<T> addEmbedded(
				String key, Class<S> clazz,
				Function<T, Optional<S>> objectFunction) {

				_relationTypes.add(
					new RelationTuple<>(key, clazz, objectFunction));

				return this;
			}

			@Override
			public FirstStep<T> addField(
				String key, Function<T, Object> valueFunction) {

				_fieldFunctions.put(key, valueFunction);

				return this;
			}

			@Override
			public FirstStep<T> addType(String type) {
				_types.add(type);

				return this;
			}

		};
	}

	private final Map<String, Function<?, Object>> _fieldFunctions;
	private final ConcurrentHashMap<String, Function<?, String>>
		_identifierFunctions;
	private final Class<T> _modelClass;
	private final List<RelationTuple<?, ?>> _relationTypes;
	private final List<String> _types;

}