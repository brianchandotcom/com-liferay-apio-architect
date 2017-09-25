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

package com.liferay.vulcan.architect.jaxrs.json.internal;

import com.liferay.vulcan.architect.message.json.JSONObjectBuilder;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import javax.json.JsonValue;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class JSONObjectBuilderImpl implements JSONObjectBuilder {

	@Override
	public JsonObject build() {
		return _jsonObjectBuilder.build();
	}

	@Override
	public FieldStep field(String name) {
		return new FieldStepImpl(name, _jsonObjectBuilder);
	}

	@Override
	public FieldStep ifElseCondition(
		boolean condition, Function<JSONObjectBuilder, FieldStep> ifFunction,
		Function<JSONObjectBuilder, FieldStep> elseFunction) {

		if (condition) {
			return ifFunction.apply(this);
		}
		else {
			return elseFunction.apply(this);
		}
	}

	@Override
	public FieldStep nestedField(String parentName, String... nestedNames) {
		FieldStep fieldStep = field(parentName);

		for (String nestedName : nestedNames) {
			fieldStep = fieldStep.field(nestedName);
		}

		return fieldStep;
	}

	@Override
	public FieldStep nestedPrefixedField(
		String prefix, String parentName, String... nestedNames) {

		FieldStep fieldStep = nestedField(prefix, parentName);

		for (String nestedName : nestedNames) {
			fieldStep = fieldStep.nestedField(prefix, nestedName);
		}

		return fieldStep;
	}

	@Override
	public FieldStep nestedSuffixedField(
		String suffix, String parentName, String... nestedNames) {

		FieldStep fieldStep = nestedField(parentName, suffix);

		for (String nestedName : nestedNames) {
			fieldStep = fieldStep.nestedField(nestedName, suffix);
		}

		return fieldStep;
	}

	public static class ArrayValueStepImpl implements ArrayValueStep {

		public ArrayValueStepImpl(JsonArrayBuilder jsonArrayBuilder) {
			_jsonArrayBuilder = jsonArrayBuilder;
		}

		@Override
		public void add(Consumer<JSONObjectBuilder> consumer) {
			JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilderImpl();

			consumer.accept(jsonObjectBuilder);

			add(jsonObjectBuilder);
		}

		@Override
		public void add(JSONObjectBuilder jsonObjectBuilder) {
			_jsonArrayBuilder.add(jsonObjectBuilder.build());
		}

		@Override
		public void addAllBooleans(Collection<Boolean> collection) {
			Stream<Boolean> stream = collection.stream();

			stream.map(
				bool -> {
					if (bool) {
						return JsonValue.TRUE;
					}

					return JsonValue.FALSE;
				}
			).forEach(
				_jsonArrayBuilder::add
			);
		}

		@Override
		public void addAllJsonObjects(Collection<JsonObject> collection) {
			collection.forEach(_jsonArrayBuilder::add);
		}

		@Override
		public void addAllNumbers(Collection<Number> collection) {
			Stream<Number> stream = collection.stream();

			stream.map(
				JSONObjectBuilderImpl::_getJsonNumberOptional
			).forEach(
				optional -> optional.ifPresent(_jsonArrayBuilder::add)
			);
		}

		@Override
		public void addAllStrings(Collection<String> collection) {
			Stream<String> stream = collection.stream();

			stream.map(
				Json::createValue
			).forEach(
				_jsonArrayBuilder::add
			);
		}

		@Override
		public void addBoolean(Boolean value) {
			if (value != null) {
				if (value) {
					_jsonArrayBuilder.add(JsonValue.TRUE);
				}
				else {
					_jsonArrayBuilder.add(JsonValue.FALSE);
				}
			}
		}

		@Override
		public void addNumber(Number value) {
			if (value != null) {
				Optional<JsonNumber> optional = _getJsonNumberOptional(value);

				optional.ifPresent(_jsonArrayBuilder::add);
			}
		}

		@Override
		public void addString(String value) {
			if (value != null) {
				JsonString jsonString = Json.createValue(value);

				_jsonArrayBuilder.add(jsonString);
			}
		}

		private final JsonArrayBuilder _jsonArrayBuilder;

	}

	private static Optional<JsonNumber> _getJsonNumberOptional(Number number) {
		if (number instanceof Integer) {
			return Optional.of(Json.createValue(number.intValue()));
		}
		else if (number instanceof Long) {
			return Optional.of(Json.createValue(number.longValue()));
		}
		else if (number instanceof Short) {
			return Optional.of(Json.createValue(number.shortValue()));
		}
		else if (number instanceof Double) {
			return Optional.of(Json.createValue(number.doubleValue()));
		}
		else if (number instanceof Float) {
			return Optional.of(Json.createValue(number.floatValue()));
		}
		else if (number instanceof Byte) {
			return Optional.of(Json.createValue(number.byteValue()));
		}

		return Optional.empty();
	}

	private final JsonObjectBuilder _jsonObjectBuilder =
		Json.createObjectBuilder();

	private static class FieldStepImpl implements FieldStep {

		public FieldStepImpl(String name, JsonObjectBuilder jsonObjectBuilder) {
			_name = name;
			_jsonObjectBuilder = jsonObjectBuilder;
		}

		@Override
		public ArrayValueStep arrayValue() {
			JsonArrayBuilder jsonArrayBuilder = null;

			try {
				JsonObject jsonObject = _jsonObjectBuilder.build();

				JsonArray jsonArray = jsonObject.getJsonArray(_name);

				jsonArrayBuilder = Json.createArrayBuilder(jsonArray);
			}
			catch (ClassCastException cce) {
				jsonArrayBuilder = Json.createArrayBuilder();
			}

			_jsonObjectBuilder.add(_name, jsonArrayBuilder);

			return new ArrayValueStepImpl(jsonArrayBuilder);
		}

		@Override
		public void booleanValue(Boolean value) {
			if (value != null) {
				if (value) {
					_jsonObjectBuilder.add(_name, JsonValue.TRUE);
				}
				else {
					_jsonObjectBuilder.add(_name, JsonValue.FALSE);
				}
			}
		}

		@Override
		public FieldStep field(String name) {
			JsonObjectBuilder jsonObjectBuilder = null;

			try {
				JsonObject jsonObject = _jsonObjectBuilder.build();

				JsonObject previousJSONObject = jsonObject.getJsonObject(_name);

				jsonObjectBuilder = Json.createObjectBuilder(
					previousJSONObject);
			}
			catch (ClassCastException cce) {
				jsonObjectBuilder = Json.createObjectBuilder();
			}

			_jsonObjectBuilder.add(_name, jsonObjectBuilder);

			return new FieldStepImpl(name, jsonObjectBuilder);
		}

		@Override
		public FieldStep ifCondition(
			boolean condition, Function<FieldStep, FieldStep> ifFunction) {

			if (condition) {
				return ifFunction.apply(this);
			}
			else {
				return this;
			}
		}

		@Override
		public FieldStep ifElseCondition(
			boolean condition, Function<FieldStep, FieldStep> ifFunction,
			Function<FieldStep, FieldStep> elseFunction) {

			if (condition) {
				return ifFunction.apply(this);
			}
			else {
				return elseFunction.apply(this);
			}
		}

		@Override
		public FieldStep nestedField(String parentName, String... nestedNames) {
			FieldStep fieldStep = field(parentName);

			for (String nestedName : nestedNames) {
				fieldStep = fieldStep.field(nestedName);
			}

			return fieldStep;
		}

		@Override
		public FieldStep nestedPrefixedField(
			String prefix, String parentName, String... nestedNames) {

			FieldStep fieldStep = nestedField(prefix, parentName);

			for (String nestedName : nestedNames) {
				fieldStep = fieldStep.nestedField(prefix, nestedName);
			}

			return fieldStep;
		}

		@Override
		public FieldStep nestedSuffixedField(
			String suffix, String parentName, String... nestedNames) {

			FieldStep fieldStep = nestedField(parentName, suffix);

			for (String nestedName : nestedNames) {
				fieldStep = fieldStep.nestedField(nestedName, suffix);
			}

			return fieldStep;
		}

		@Override
		public void numberValue(Number value) {
			if (value != null) {
				Optional<JsonNumber> optional = _getJsonNumberOptional(value);

				optional.ifPresent(
					jsonNumber -> _jsonObjectBuilder.add(_name, jsonNumber));
			}
		}

		@Override
		public void stringValue(String value) {
			if (value != null) {
				JsonString jsonString = Json.createValue(value);

				_jsonObjectBuilder.add(_name, jsonString);
			}
		}

		private final JsonObjectBuilder _jsonObjectBuilder;
		private final String _name;

	}

}