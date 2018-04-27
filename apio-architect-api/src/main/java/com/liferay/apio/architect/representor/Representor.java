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

package com.liferay.apio.architect.representor;

import static com.liferay.apio.architect.date.DateTransformer.asString;

import com.liferay.apio.architect.alias.BinaryFunction;
import com.liferay.apio.architect.file.BinaryFile;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.language.Language;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.related.RelatedModel;
import com.liferay.apio.architect.representor.function.FieldFunction;
import com.liferay.apio.architect.representor.function.NestedFieldFunction;
import com.liferay.apio.architect.unsafe.Unsafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Holds information about the metadata supported for a resource.
 *
 * <p>
 * Instances of this interface should always be created by using a {@link
 * Representor.Builder}.
 * </p>
 *
 * @author Alejandro Hernández
 * @param  <T> the model's type
 * @see    Representor.Builder
 */
public class Representor<T> {

	/**
	 * Returns the binary resources linked to a model.
	 *
	 * @return the binary resources linked to a model
	 */
	public Optional<BinaryFunction<T>> getBinaryFunction(String binaryId) {
		return Optional.ofNullable(_binaryFunctions.get(binaryId));
	}

	/**
	 * Returns the binary resources linked to a model.
	 *
	 * @return the binary resources linked to a model
	 */
	public List<FieldFunction<T, BinaryFile>> getBinaryFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("BINARY")
		).<List<FieldFunction<T, BinaryFile>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the map containing the boolean field names and the functions to
	 * get those fields.
	 *
	 * @return the map containing the boolean field names and functions
	 */
	public List<FieldFunction<T, Boolean>> getBooleanFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("BOOLEAN")
		).<List<FieldFunction<T, Boolean>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the map containing the boolean list field names and the functions
	 * to get those fields.
	 *
	 * @return the map containing the boolean list field names and functions
	 */
	public List<FieldFunction<T, List<Boolean>>> getBooleanListFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("BOOLEAN_LIST")
		).<List<FieldFunction<T, List<Boolean>>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the model's identifier.
	 *
	 * @param  model the model instance
	 * @return the model's identifier
	 */
	public Object getIdentifier(T model) {
		return _identifierFunction.apply(model);
	}

	/**
	 * Returns the links.
	 *
	 * @return the links
	 */
	public List<FieldFunction<T, String>> getLinkFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("LINK")
		).<List<FieldFunction<T, String>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns a map containing the localized string field names and the
	 * functions to get those fields.
	 *
	 * @return the map containing the localized string field names and functions
	 */
	public List<FieldFunction<T, Function<Language, String>>>
		getLocalizedStringFunctions() {

		return Optional.ofNullable(
			_fieldFunctions.get("LOCALIZED")
		).<List<FieldFunction<T, Function<Language, String>>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the list of nested field functions.
	 *
	 * @return the list of nested field functions.
	 */
	public List<NestedFieldFunction<T, ?>> getNestedFieldFunctions() {
		return _nestedFieldFunctions;
	}

	/**
	 * Returns the map containing the number field names and the functions to
	 * get those fields.
	 *
	 * @return the map containing the number field names and functions
	 */
	public List<FieldFunction<T, Number>> getNumberFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("NUMBER")
		).<List<FieldFunction<T, Number>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the map containing the number list field names and the functions
	 * to get those fields.
	 *
	 * @return the map containing the number list field names and functions
	 */
	public List<FieldFunction<T, List<Number>>> getNumberListFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("NUMBER_LIST")
		).<List<FieldFunction<T, List<Number>>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the related collections.
	 *
	 * @return the related collections
	 */
	public Stream<RelatedCollection<? extends Identifier>>
		getRelatedCollections() {

		Stream<List<RelatedCollection<? extends Identifier>>> stream =
			Stream.of(_relatedCollections, _supplier.get());

		return stream.filter(
			Objects::nonNull
		).flatMap(
			Collection::stream
		);
	}

	/**
	 * Returns the related models.
	 *
	 * @return the related models
	 */
	public List<RelatedModel<T, ?>> getRelatedModels() {
		return _relatedModels;
	}

	/**
	 * Returns the map containing the string field names and the functions to
	 * get those fields.
	 *
	 * @return the map containing the string field names and functions
	 */
	public List<FieldFunction<T, String>> getStringFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("STRING")
		).<List<FieldFunction<T, String>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the map containing the string list field names and the functions
	 * to get those fields.
	 *
	 * @return the map containing the string list field names and functions
	 */
	public List<FieldFunction<T, List<String>>> getStringListFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("STRING_LIST")
		).<List<FieldFunction<T, List<String>>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the types.
	 *
	 * @return the types
	 */
	public List<String> getTypes() {
		return _types;
	}

	public boolean isNested() {
		if (_identifierFunction == null) {
			return true;
		}

		return false;
	}

	/**
	 * Creates generic representations of your domain models that Apio
	 * hypermedia writers can understand.
	 */
	public static class Builder<T, U> {

		public Builder() {
			this(null);
		}

		public Builder(Class<? extends Identifier<U>> identifierClass) {
			_identifierClass = identifierClass;

			Supplier<List<RelatedCollection<?>>> supplier =
				Collections::emptyList;

			_biConsumer = (clazz, relatedCollection) -> {
			};
			_representor = new Representor<>(supplier);
		}

		public Builder(
			Class<? extends Identifier<U>> identifierClass,
			BiConsumer<Class<?>, RelatedCollection<?>> biConsumer,
			Supplier<List<RelatedCollection<?>>> supplier) {

			_identifierClass = identifierClass;
			_biConsumer = biConsumer;

			_representor = new Representor<>(supplier);
		}

		/**
		 * Adds a type for a nested resource, skipping the identifier function
		 * because nested resources don't have an ID.
		 *
		 * @param  type the type name
		 * @return the builder's step
		 */
		public FirstStep nestedTypes(String type, String... types) {
			_representor._addTypes(type, types);

			return new FirstStep();
		}

		/**
		 * Adds a type for the model.
		 *
		 * @param  type the type name
		 * @return the builder's step
		 */
		public IdentifierStep types(String type, String... types) {
			_representor._addTypes(type, types);

			return new IdentifierStep();
		}

		public class FirstStep {

			/**
			 * Adds information about the bidirectional relation of a linked
			 * resource in the actual resource and a collection of items in the
			 * related resource.
			 *
			 * @param  key the relation's name in the resource
			 * @param  relatedKey the relation's name in the related resource
			 * @param  identifierClass the related resource identifier's class
			 * @param  identifierFunction the function used to get the related
			 *         resource's identifier
			 * @return the builder's step
			 */
			public <S> FirstStep addBidirectionalModel(
				String key, String relatedKey,
				Class<? extends Identifier<S>> identifierClass,
				Function<T, S> identifierFunction) {

				RelatedCollection<?> relatedCollection =
					new RelatedCollection<>(relatedKey, _identifierClass);

				_biConsumer.accept(identifierClass, relatedCollection);

				_representor._addRelatedModel(
					key, identifierClass, identifierFunction);

				return this;
			}

			/**
			 * Adds binary files to a resource.
			 *
			 * @param  key the binary resource's name
			 * @param  binaryFunction the function used to get the binaries
			 * @return the builder's step
			 */
			public FirstStep addBinary(
				String key, BinaryFunction<T> binaryFunction) {

				_representor._addBinaryFunction(key, binaryFunction);

				return this;
			}

			/**
			 * Adds information about a resource's boolean field.
			 *
			 * @param  key the field's name
			 * @param  function the function used to get the boolean value
			 * @return the builder's step
			 */
			public FirstStep addBoolean(
				String key, Function<T, Boolean> function) {

				_representor._addBooleanFunction(key, function);

				return this;
			}

			/**
			 * Adds information about a resource's boolean list field.
			 *
			 * @param  key the field's name
			 * @param  function the function used to get the boolean list
			 * @return the builder's step
			 */
			public FirstStep addBooleanList(
				String key, Function<T, List<Boolean>> function) {

				_representor._addBooleanListFunction(key, function);

				return this;
			}

			/**
			 * Adds information about a resource's date field.
			 *
			 * @param  key the field's name
			 * @param  function the function used to get the date value
			 * @return the builder's step
			 */
			public FirstStep addDate(String key, Function<T, Date> function) {
				Function<Date, String> formatFunction = date -> {
					if (date == null) {
						return null;
					}

					return asString(date);
				};

				_representor._addStringFunction(
					key, function.andThen(formatFunction));

				return this;
			}

			/**
			 * Adds information about a resource link.
			 *
			 * @param  key the field's name
			 * @param  url the link's URL
			 * @return the builder's step
			 */
			public FirstStep addLink(String key, String url) {
				_representor._addLinkFunction(key, __ -> url);

				return this;
			}

			/**
			 * Adds information about an embeddable related resource.
			 *
			 * @param  key the relation's name
			 * @param  identifierClass the related resource identifier's class
			 * @param  identifierFunction the function used to get the related
			 *         resource's identifier
			 * @return the builder's step
			 */
			public <S> FirstStep addLinkedModel(
				String key, Class<? extends Identifier<S>> identifierClass,
				Function<T, S> identifierFunction) {

				_representor._addRelatedModel(
					key, identifierClass, identifierFunction);

				return this;
			}

			/**
			 * Provides information about a resource localized string field.
			 *
			 * @param  key the field's name
			 * @param  stringFunction the function used to get the string value
			 * @return builder's step
			 */
			public FirstStep addLocalizedStringByLanguage(
				String key, BiFunction<T, Language, String> stringFunction) {

				_representor._addLanguageFunction(
					key, t -> language -> stringFunction.apply(t, language));

				return this;
			}

			/**
			 * Provides information about a resource localized string field.
			 *
			 * @param  key the field's name
			 * @param  stringFunction the function used to get the string value
			 * @return builder's step
			 */
			public FirstStep addLocalizedStringByLocale(
				String key, BiFunction<T, Locale, String> stringFunction) {

				return addLocalizedStringByLanguage(
					key,
					(t, language) -> stringFunction.apply(
						t, language.getPreferredLocale()));
			}

			/**
			 * Provides information about a nested field.
			 *
			 * @param  key the field's name
			 * @param  transformFunction the function that transforms the model
			 *         into the model used inside the nested representor
			 * @param  representorFunction the function that creates the nested
			 *         representor
			 * @return the builder's step
			 */
			public <W> FirstStep addNested(
				String key, Function<T, W> transformFunction,
				Function<Builder<W, ?>, Representor<W>> representorFunction) {

				_representor._addNestedFieldFunction(
					key, transformFunction, representorFunction);

				return this;
			}

			/**
			 * Adds information about a resource's number field.
			 *
			 * @param  key the field's name
			 * @param  function the function used to get the number's value
			 * @return the builder's step
			 */
			public FirstStep addNumber(
				String key, Function<T, Number> function) {

				_representor._addNumberFunction(key, function);

				return this;
			}

			/**
			 * Adds information about a resource's number list field.
			 *
			 * @param  key the field's name
			 * @param  function the function used to get the number list
			 * @return the builder's step
			 */
			public FirstStep addNumberList(
				String key, Function<T, List<Number>> function) {

				_representor._addNumberListFunction(key, function);

				return this;
			}

			/**
			 * Adds information about a related collection.
			 *
			 * @param  key the relation's name
			 * @param  itemIdentifierClass the class of the collection items'
			 *         identifier
			 * @return the builder's step
			 */
			public <S extends Identifier> FirstStep addRelatedCollection(
				String key, Class<S> itemIdentifierClass) {

				_representor._addRelatedCollection(key, itemIdentifierClass);

				return this;
			}

			/**
			 * Adds information about a resource's string field.
			 *
			 * @param  key the field's name
			 * @param  function the function used to get the string's value
			 * @return the builder's step
			 */
			public FirstStep addString(
				String key, Function<T, String> function) {

				_representor._addStringFunction(key, function);

				return this;
			}

			/**
			 * Adds information about a resource's string list field.
			 *
			 * @param  key the field's name
			 * @param  function the function used to get the string list
			 * @return the builder's step
			 */
			public FirstStep addStringList(
				String key, Function<T, List<String>> function) {

				_representor._addStringListFunction(key, function);

				return this;
			}

			/**
			 * Constructs and returns a {@link Representor} instance _with the
			 * information provided to the builder.
			 *
			 * @return the {@code Representor} instance
			 */
			public Representor<T> build() {
				return _representor;
			}

			private FirstStep() {
			}

		}

		public class IdentifierStep {

			/**
			 * Provides a lambda function that can be used to obtain a model's
			 * identifier.
			 *
			 * @param  identifierFunction lambda function used to obtain a
			 *         model's identifier
			 * @return the builder's next step
			 */
			public FirstStep identifier(Function<T, U> identifierFunction) {
				_representor._setIdentifierFunction(identifierFunction);

				return new FirstStep();
			}

			private IdentifierStep() {
			}

		}

		private final BiConsumer<Class<?>, RelatedCollection<?>> _biConsumer;
		private Class<? extends Identifier> _identifierClass;
		private final Representor<T> _representor;

	}

	private Representor(Supplier<List<RelatedCollection<?>>> supplier) {
		_binaryFunctions = new LinkedHashMap<>();
		_fieldFunctions = new LinkedHashMap<>();
		_nestedFieldFunctions = new ArrayList<>();
		_relatedCollections = new ArrayList<>();
		_supplier = supplier;
		_relatedModels = new ArrayList<>();
		_types = new ArrayList<>();
	}

	private void _addBinaryFunction(
		String key, BinaryFunction<T> binaryFunction) {

		_binaryFunctions.put(key, binaryFunction);

		_addFieldFunction(key, binaryFunction, "BINARY");
	}

	private void _addBooleanFunction(
		String key, Function<T, Boolean> function) {

		_addFieldFunction(key, function, "BOOLEAN");
	}

	private void _addBooleanListFunction(
		String key, Function<T, List<Boolean>> function) {

		_addFieldFunction(key, function, "BOOLEAN_LIST");
	}

	private <S> void _addFieldFunction(
		String key, Function<T, S> function, String mapKey) {

		List<FieldFunction<T, ?>> list = _fieldFunctions.computeIfAbsent(
			mapKey, __ -> new ArrayList<>());

		FieldFunction<T, S> fieldFunction = new FieldFunction<>(key, function);

		list.add(fieldFunction);
	}

	private void _addLanguageFunction(
		String key, Function<T, Function<Language, String>> function) {

		_addFieldFunction(key, function, "LOCALIZED");
	}

	private void _addLinkFunction(String key, Function<T, String> function) {
		_addFieldFunction(key, function, "LINK");
	}

	private <S> void _addNestedFieldFunction(
		String key, Function<T, S> transformFunction,
		Function<Builder<S, ?>, Representor<S>> representorFunction) {

		Representor<S> representor = representorFunction.apply(new Builder<>());

		NestedFieldFunction<T, S> nestedFieldFunction =
			new NestedFieldFunction<>(key, transformFunction, representor);

		_nestedFieldFunctions.add(nestedFieldFunction);
	}

	private void _addNumberFunction(String key, Function<T, Number> function) {
		_addFieldFunction(key, function, "NUMBER");
	}

	private void _addNumberListFunction(
		String key, Function<T, List<Number>> function) {

		_addFieldFunction(key, function, "NUMBER_LIST");
	}

	private <S extends Identifier> void _addRelatedCollection(
		String key, Class<S> itemIdentifierClass) {

		RelatedCollection<S> relatedCollection = new RelatedCollection<>(
			key, itemIdentifierClass);

		_relatedCollections.add(relatedCollection);
	}

	private <S> void _addRelatedModel(
		String key, Class<? extends Identifier<S>> identifierClass,
		Function<T, S> identifierFunction) {

		RelatedModel<T, S> relatedModel = new RelatedModel<>(
			key, identifierClass, identifierFunction);

		_relatedModels.add(relatedModel);
	}

	private void _addStringFunction(String key, Function<T, String> function) {
		_addFieldFunction(key, function, "STRING");
	}

	private void _addStringListFunction(
		String key, Function<T, List<String>> function) {

		_addFieldFunction(key, function, "STRING_LIST");
	}

	private void _addTypes(String type, String... types) {
		_types.add(type);
		Collections.addAll(_types, types);
	}

	private void _setIdentifierFunction(Function<T, ?> identifierFunction) {
		_identifierFunction = identifierFunction;
	}

	private final Map<String, BinaryFunction<T>> _binaryFunctions;
	private final Map<String, List<FieldFunction<T, ?>>> _fieldFunctions;
	private Function<T, ?> _identifierFunction;
	private final List<NestedFieldFunction<T, ?>> _nestedFieldFunctions;
	private final List<RelatedCollection<?>> _relatedCollections;
	private final List<RelatedModel<T, ?>> _relatedModels;
	private final Supplier<List<RelatedCollection<?>>> _supplier;
	private final List<String> _types;

}