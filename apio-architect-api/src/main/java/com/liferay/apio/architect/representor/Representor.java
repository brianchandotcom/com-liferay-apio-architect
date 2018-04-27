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
import com.liferay.apio.architect.unsafe.Unsafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
			_fieldFunctions.get("BINARY_FUNCTIONS")
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
			_fieldFunctions.get("BOOLEAN_FUNCTIONS")
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
			_fieldFunctions.get("BOOLEAN_LIST_FUNCTIONS")
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
			_fieldFunctions.get("LINK_FUNCTIONS")
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
			_fieldFunctions.get("LOCALIZED_STRING_FUNCTIONS")
		).<List<FieldFunction<T, Function<Language, String>>>>map(
			Unsafe::unsafeCast
		).orElseGet(
			Collections::emptyList
		);
	}

	/**
	 * Returns the representors used to render the subresources.
	 *
	 * @return the representors
	 */
	public Map<String, Representor<?>> getNested() {
		return _nested;
	}

	/**
	 * Returns the mappers for the resource and the subresource.
	 *
	 * @return the mappers
	 */
	public Map<String, Function<T, ?>> getNestedFunctions() {
		return _nestedFunctions;
	}

	/**
	 * Returns the map containing the number field names and the functions to
	 * get those fields.
	 *
	 * @return the map containing the number field names and functions
	 */
	public List<FieldFunction<T, Number>> getNumberFunctions() {
		return Optional.ofNullable(
			_fieldFunctions.get("NUMBER_FUNCTIONS")
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
			_fieldFunctions.get("NUMBER_LIST_FUNCTIONS")
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
			Stream.of(_relatedCollections, _relatedCollectionsSupplier.get());

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
			_fieldFunctions.get("STRING_FUNCTIONS")
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
			_fieldFunctions.get("STRING_LIST_FUNCTIONS")
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
			Supplier<List<RelatedCollection<?>>> listSupplier =
				Collections::emptyList;

			_relatedCollectionBiConsumer = (clazz, relatedCollection) -> {
			};
			_representor = new Representor<>(identifierClass, listSupplier);
		}

		public Builder(
			Class<? extends Identifier<U>> identifierClass,
			BiConsumer<Class<?>, RelatedCollection<?>>
				relatedCollectionBiConsumer,
			Supplier<List<RelatedCollection<?>>> relatedCollectionsSupplier) {

			_relatedCollectionBiConsumer = relatedCollectionBiConsumer;

			_representor = new Representor<>(
				identifierClass, relatedCollectionsSupplier);
		}

		/**
		 * Adds a type for a nested resource, skipping the identifier function
		 * because nested resources don't have an ID.
		 *
		 * @param  type the type name
		 * @return the builder's step
		 */
		public FirstStep nestedTypes(String type, String... types) {
			_representor._types.add(type);

			Collections.addAll(_representor._types, types);

			return new FirstStep();
		}

		/**
		 * Adds a type for the model.
		 *
		 * @param  type the type name
		 * @return the builder's step
		 */
		public IdentifierStep types(String type, String... types) {
			_representor._types.add(type);

			Collections.addAll(_representor._types, types);

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

				_representor._relatedModels.add(
					new RelatedModel<>(
						key, identifierClass, identifierFunction));

				RelatedCollection<?> relatedCollection =
					new RelatedCollection<>(
						relatedKey, _representor._identifierClass);

				_relatedCollectionBiConsumer.accept(
					identifierClass, relatedCollection);

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

				_representor._binaryFunctions.put(key, binaryFunction);

				List<FieldFunction<T, BinaryFile>> list =
					_representor.getBinaryFunctions();

				list.add(new FieldFunction<>(key, binaryFunction));

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

				List<FieldFunction<T, Boolean>> list =
					_representor.getBooleanFunctions();

				list.add(new FieldFunction<>(key, function));

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

				List<FieldFunction<T, List<Boolean>>> list =
					_representor.getBooleanListFunctions();

				list.add(new FieldFunction<>(key, function));

				return this;
			}

			/**
			 * Adds information about a resource's date field.
			 *
			 * @param  key the field's name
			 * @param  dateFunction the function used to get the date value
			 * @return the builder's step
			 */
			public FirstStep addDate(
				String key, Function<T, Date> dateFunction) {

				Function<Date, String> formatFunction = date -> {
					if (date == null) {
						return null;
					}

					return asString(date);
				};

				List<FieldFunction<T, String>> list =
					_representor.getStringFunctions();

				FieldFunction<T, String> fieldFunction = new FieldFunction<>(
					key, dateFunction.andThen(formatFunction));

				list.add(fieldFunction);

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
				List<FieldFunction<T, String>> list =
					_representor.getLinkFunctions();

				list.add(new FieldFunction<>(key, __ -> url));

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

				_representor._relatedModels.add(
					new RelatedModel<>(
						key, identifierClass, identifierFunction));

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

				List<FieldFunction<T, Function<Language, String>>> list =
					_representor.getLocalizedStringFunctions();

				FieldFunction<T, Function<Language, String>> fieldFunction =
					new FieldFunction<>(
						key,
						t -> language -> stringFunction.apply(t, language));

				list.add(fieldFunction);

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

				_representor._nested.put(
					key, representorFunction.apply(new Builder<>()));

				_representor._nestedFunctions.put(key, transformFunction);

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

				List<FieldFunction<T, Number>> list =
					_representor.getNumberFunctions();

				list.add(new FieldFunction<>(key, function));

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

				List<FieldFunction<T, List<Number>>> list =
					_representor.getNumberListFunctions();

				list.add(new FieldFunction<>(key, function));

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

				RelatedCollection<S> relatedCollection =
					new RelatedCollection<>(key, itemIdentifierClass);

				_representor._relatedCollections.add(relatedCollection);

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

				List<FieldFunction<T, String>> list =
					_representor.getStringFunctions();

				list.add(new FieldFunction<>(key, function));

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

				List<FieldFunction<T, List<String>>> list =
					_representor.getStringListFunctions();

				list.add(new FieldFunction<>(key, function));

				return this;
			}

			/**
			 * Constructs and returns a {@link Representor} instance with the
			 * information provided to the builder.
			 *
			 * @return the {@code Representor} instance
			 */
			public Representor<T> build() {
				return _representor;
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
				_representor._identifierFunction = identifierFunction;

				return new FirstStep();
			}

		}

		private final BiConsumer<Class<?>, RelatedCollection<?>>
			_relatedCollectionBiConsumer;
		private final Representor<T> _representor;

	}

	private Representor(
		Class<? extends Identifier<?>> identifierClass,
		Supplier<List<RelatedCollection<? extends Identifier>>>
			relatedCollectionsSupplier) {

		_identifierClass = identifierClass;
		_relatedCollectionsSupplier = relatedCollectionsSupplier;

		_fieldFunctions =
			new LinkedHashMap<String, List<FieldFunction<T, ?>>>() {
				{
					put("BINARY_FUNCTIONS", new ArrayList<>());
					put("BOOLEAN_FUNCTIONS", new ArrayList<>());
					put("BOOLEAN_LIST_FUNCTIONS", new ArrayList<>());
					put("LINK_FUNCTIONS", new ArrayList<>());
					put("LOCALIZED_STRING_FUNCTIONS", new ArrayList<>());
					put("NUMBER_FUNCTIONS", new ArrayList<>());
					put("NUMBER_LIST_FUNCTIONS", new ArrayList<>());
					put("STRING_FUNCTIONS", new ArrayList<>());
					put("STRING_LIST_FUNCTIONS", new ArrayList<>());
				}
			};
		_binaryFunctions = new LinkedHashMap<>();
		_nested = new HashMap<>();
		_nestedFunctions = new HashMap<>();
		_relatedCollections = new ArrayList<>();
		_relatedModels = new ArrayList<>();
		_types = new ArrayList<>();
	}

	private final Map<String, BinaryFunction<T>> _binaryFunctions;
	private final Map<String, List<FieldFunction<T, ?>>> _fieldFunctions;
	private final Class<? extends Identifier<?>> _identifierClass;
	private Function<T, ?> _identifierFunction;
	private final Map<String, Representor<?>> _nested;
	private final Map<String, Function<T, ?>> _nestedFunctions;
	private final List<RelatedCollection<? extends Identifier>>
		_relatedCollections;
	private final Supplier<List<RelatedCollection<?>>>
		_relatedCollectionsSupplier;
	private final List<RelatedModel<T, ?>> _relatedModels;
	private final List<String> _types;

}