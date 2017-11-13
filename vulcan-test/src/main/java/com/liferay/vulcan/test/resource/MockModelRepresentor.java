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

package com.liferay.vulcan.test.resource;

import com.liferay.vulcan.alias.BinaryFunction;
import com.liferay.vulcan.language.Language;
import com.liferay.vulcan.resource.RelatedCollection;
import com.liferay.vulcan.resource.RelatedModel;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.identifier.StringIdentifier;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instances of this class represent a mock representor that can be used for
 * testing purposes.
 *
 * @author Alejandro Hernández
 * @review
 */
public class MockModelRepresentor<T extends Model>
	implements Representor<T, StringIdentifier> {

	public MockModelRepresentor(Model model) {
		_model = model;
	}

	@Override
	public Map<String, BinaryFunction<T>> getBinaryFunctions() {
		return _model.getBinaryFunctions();
	}

	@Override
	public Map<String, Function<T, Boolean>> getBooleanFunctions() {
		return convertToFunctionsMap(_model.getBooleanFields());
	}

	@Override
	public List<RelatedModel<T, ?>> getEmbeddedRelatedModels() {
		return _model.getEmbeddedRelatedModels();
	}

	@Override
	public StringIdentifier getIdentifier(Model model) {
		return model::getId;
	}

	@Override
	public Class<StringIdentifier> getIdentifierClass() {
		return StringIdentifier.class;
	}

	@Override
	public List<RelatedModel<T, ?>> getLinkedRelatedModels() {
		return _model.getLinkedRelatedModels();
	}

	@Override
	public Map<String, String> getLinks() {
		return _model.getLinks();
	}

	@Override
	public Map<String, BiFunction<T, Language, String>>
		getLocalizedStringFunctions() {

		Map<String, String> localizedStringFunctions =
			_model.getLocalizedStringFunctions();

		Set<Entry<String, String>> entries =
			localizedStringFunctions.entrySet();

		Stream<Entry<String, String>> stream = entries.stream();

		return stream.collect(
			Collectors.toMap(
				Entry::getKey, entry -> (t, language) -> entry.getValue()));
	}

	@Override
	public Map<String, Function<T, Number>> getNumberFunctions() {
		return convertToFunctionsMap(_model.getNumberFields());
	}

	@Override
	public Stream<RelatedCollection<T, ?>> getRelatedCollections() {
		Map<String, ? extends Model> relatedCollections =
			_model.getRelatedCollections();

		Set<? extends Entry<String, ? extends Model>> entries =
			relatedCollections.entrySet();

		Stream<? extends Entry<String, ? extends Model>> stream =
			entries.stream();

		return stream.map(
			entry -> new RelatedCollection<>(
				entry.getKey(), entry.getValue().getClass(),
				this::getIdentifier));
	}

	@Override
	public Map<String, Function<T, String>> getStringFunctions() {
		return convertToFunctionsMap(_model.getStringFields());
	}

	@Override
	public List<String> getTypes() {
		return _model.getTypes();
	}

	protected <V, W> Map<String, Function<V, W>> convertToFunctionsMap(
		Map<String, W> booleanFields) {

		Set<Entry<String, W>> entries = booleanFields.entrySet();

		Stream<Entry<String, W>> stream = entries.stream();

		return stream.collect(
			Collectors.toMap(Entry::getKey, entry -> __ -> entry.getValue()));
	}

	private final Model _model;

}