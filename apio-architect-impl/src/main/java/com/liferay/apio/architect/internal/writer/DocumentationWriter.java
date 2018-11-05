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

package com.liferay.apio.architect.internal.writer;

import static com.liferay.apio.architect.internal.unsafe.Unsafe.unsafeCast;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.BOOLEAN;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.BOOLEAN_LIST;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.FILE;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.LINKED_MODEL;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.NESTED_MODEL;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.NESTED_MODEL_LIST;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.NUMBER;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.NUMBER_LIST;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.RELATED_COLLECTION;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.STRING;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.STRING_LIST;

import com.liferay.apio.architect.alias.representor.FieldFunction;
import com.liferay.apio.architect.alias.representor.NestedFieldFunction;
import com.liferay.apio.architect.consumer.TriConsumer;
import com.liferay.apio.architect.documentation.contributor.CustomDocumentation;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.internal.documentation.Documentation;
import com.liferay.apio.architect.internal.message.json.DocumentationMessageMapper;
import com.liferay.apio.architect.internal.message.json.JSONObjectBuilder;
import com.liferay.apio.architect.internal.request.RequestInfo;
import com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField;
import com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType;
import com.liferay.apio.architect.language.AcceptLanguage;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.related.RelatedModel;
import com.liferay.apio.architect.representor.BaseRepresentor;
import com.liferay.apio.architect.representor.Representor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Writes the documentation.
 *
 * @author Alejandro Hernández
 */
public class DocumentationWriter {

	/**
	 * Creates a new {@code DocumentationWriter} object, without creating the
	 * builder.
	 *
	 * @param  function the function that transforms a builder into the {@code
	 *         DocumentationWriter}
	 * @return the {@code DocumentationWriter} instance
	 */
	public static DocumentationWriter create(
		Function<Builder, DocumentationWriter> function) {

		return function.apply(new Builder());
	}

	public DocumentationWriter(Builder builder) {
		_documentation = builder._documentation;
		_documentationMessageMapper = builder._documentationMessageMapper;
		_requestInfo = builder._requestInfo;
		_typeFunction = builder._typeFunction;

		_customDocumentation = _documentation.getCustomDocumentation();
	}

	/**
	 * Writes the {@link Documentation} to a string.
	 *
	 * @return the JSON representation of the {@code Documentation}
	 */
	public String write() {
		Function<Resource, String> nameFunction = resource -> {
			if (resource instanceof Paged) {
				return ((Paged)resource).name();
			}

			return ((Item)resource).name();
		};

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

		_writeDocumentationMetadata(jsonObjectBuilder);

		Map<String, Representor> representors =
			_documentation.getRepresentors();

		Stream<Resource> stream = _documentation.getResourceStream();

		stream.filter(
			resource -> resource instanceof Item || resource instanceof Paged
		).forEach(
			resource -> {
				String name = nameFunction.apply(resource);

				Representor representor = representors.get(name);

				_writeRoute(
					jsonObjectBuilder, representor,
					_getResourceMapperTriConsumer(resource),
					(type, resourceJsonObjectBuilder) -> _writeOperations(
						name, type, resource, resourceJsonObjectBuilder),
					_getWriteFieldsRepresentorConsumer(resource, representor));
			}
		);

		_documentationMessageMapper.onFinish(jsonObjectBuilder, _documentation);

		return jsonObjectBuilder.build();
	}

	/**
	 * Creates {@code DocumentationWriter} instances.
	 */
	public static class Builder {

		/**
		 * Add information about the documentation being written to the builder.
		 *
		 * @param  documentation the documentation being written
		 * @return the updated builder
		 */
		public DocumentationMessageMapperStep documentation(
			Documentation documentation) {

			_documentation = documentation;

			return new DocumentationMessageMapperStep();
		}

		public class BuildStep {

			/**
			 * Constructs and returns a {@code DocumentationWriter} instance
			 * with the information provided to the builder.
			 *
			 * @return the {@code DocumentationWriter} instance
			 */
			public DocumentationWriter build() {
				return new DocumentationWriter(Builder.this);
			}

		}

		public class DocumentationMessageMapperStep {

			/**
			 * Adds information to the builder about the {@link
			 * DocumentationMessageMapper}.
			 *
			 * @param  documentationMessageMapper the {@code
			 *         DocumentationMessageMapper}
			 * @return the updated builder
			 */
			public RequestInfoStep documentationMessageMapper(
				DocumentationMessageMapper documentationMessageMapper) {

				_documentationMessageMapper = documentationMessageMapper;

				return new RequestInfoStep();
			}

		}

		public class RequestInfoStep {

			/**
			 * Adds information to the builder about the request.
			 *
			 * @param  requestInfo the information obtained from the request. It
			 *         can be created by using a {@link RequestInfo.Builder}.
			 * @return the updated builder
			 */
			public TypeFunctionStep requestInfo(RequestInfo requestInfo) {
				_requestInfo = requestInfo;

				return new TypeFunctionStep();
			}

		}

		public class TypeFunctionStep {

			/**
			 * Adds information to the builder about how to get the type from an
			 * identifier class.
			 *
			 * @param  typeFunction the information obtained from the request.
			 * @return the updated builder
			 * @review
			 */
			public BuildStep typeFunction(
				Function<Class<?>, Optional<String>> typeFunction) {

				_typeFunction = typeFunction;

				return new BuildStep();
			}

		}

		private Documentation _documentation;
		private DocumentationMessageMapper _documentationMessageMapper;
		private RequestInfo _requestInfo;
		private Function<Class<?>, Optional<String>> _typeFunction;

	}

	private Stream<DocumentationField> _calculateNestableFieldNames(
		BaseRepresentor representor) {

		List<RelatedModel> relatedModels = representor.getRelatedModels();

		Stream<RelatedModel> relatedModelStream = relatedModels.stream();

		Stream<DocumentationField> relatedModelDocumentationFieldStream =
			relatedModelStream.map(_createDocumentationFieldFromModel());

		Stream<DocumentationField> fieldsStream = Stream.of(
			_getDocumentationFieldStream(
				representor.getApplicationRelativeURLFunctions(), STRING),
			_getDocumentationFieldStream(
				representor.getBinaryFunctions(), FILE),
			_getDocumentationFieldStream(
				representor.getBooleanFunctions(), BOOLEAN),
			_getDocumentationFieldStream(
				representor.getBooleanListFunctions(), BOOLEAN_LIST),
			_getDocumentationFieldStream(
				representor.getLinkFunctions(), STRING),
			_getDocumentationFieldStream(
				representor.getLocalizedStringFunctions(), STRING),
			_getDocumentationFieldStream(
				representor.getNestedFieldFunctions(), NESTED_MODEL),
			_getDocumentationFieldStream(
				representor.getNestedFieldFunctions(), NESTED_MODEL_LIST),
			_getDocumentationFieldStream(
				representor.getNumberFunctions(), NUMBER),
			_getDocumentationFieldStream(
				representor.getNumberListFunctions(), NUMBER_LIST),
			_getDocumentationFieldStream(
				representor.getRelativeURLFunctions(), STRING),
			_getDocumentationFieldStream(
				representor.getStringFunctions(), STRING),
			_getDocumentationFieldStream(
				representor.getStringListFunctions(), STRING_LIST),
			relatedModelDocumentationFieldStream
		).flatMap(
			unsafeCast(Function.identity())
		);

		List<? extends NestedFieldFunction<?, ?>> nestedFieldFunctions =
			representor.getNestedFieldFunctions();

		Stream<? extends NestedFieldFunction<?, ?>> nestedFieldFunctionStream =
			nestedFieldFunctions.stream();

		Stream<DocumentationField> nestedFieldNamesStream =
			nestedFieldFunctionStream.map(
				nestedFieldFunction -> _calculateNestableFieldNames(
					nestedFieldFunction.getNestedRepresentor())
			).reduce(
				Stream::concat
			).orElseGet(
				Stream::empty
			);

		return Stream.concat(fieldsStream, nestedFieldNamesStream);
	}

	private Function<RelatedCollection, DocumentationField>
		_createDocumentationFieldFromCollection() {

		return relatedCollection -> {
			String extraType = _typeFunction.apply(
				relatedCollection.getIdentifierClass()
			).orElse(
				null
			);

			return DocumentationField.of(
				relatedCollection.getKey(), RELATED_COLLECTION, extraType);
		};
	}

	private Function<RelatedModel, DocumentationField>
		_createDocumentationFieldFromModel() {

		return relatedCollection -> {
			String extraType = _typeFunction.apply(
				relatedCollection.getIdentifierClass()
			).orElse(
				null
			);

			return DocumentationField.of(
				relatedCollection.getKey(), LINKED_MODEL, extraType);
		};
	}

	private String _getCustomDocumentation(String name) {
		return Optional.ofNullable(
			_customDocumentation.getDescriptionFunction(name)
		).map(
			localeFunction -> {
				AcceptLanguage acceptLanguage =
					_requestInfo.getAcceptLanguage();

				return localeFunction.apply(
					acceptLanguage.getPreferredLocale());
			}
		).orElse(
			null
		);
	}

	private Stream<DocumentationField> _getDocumentationFieldStream(
		List<FieldFunction> fieldFunctionList, FieldType fieldType) {

		Stream<FieldFunction> stream = fieldFunctionList.stream();

		return stream.map(
			fieldFunction -> DocumentationField.of(
				fieldFunction.getKey(), fieldType));
	}

	private TriConsumer<JSONObjectBuilder, String, String>
		_getResourceMapperTriConsumer(Resource resource) {

		if (resource instanceof Paged) {
			return _documentationMessageMapper::mapResourceCollection;
		}

		return _documentationMessageMapper::mapResource;
	}

	private Consumer<JSONObjectBuilder> _getWriteFieldsRepresentorConsumer(
		Resource resource, Representor representor) {

		if (resource instanceof Paged) {
			return __ -> {
			};
		}

		return resourceJsonObjectBuilder -> _writeAllFields(
			representor, resourceJsonObjectBuilder);
	}

	private void _writeActionSemantics(
		ActionSemantics actionSemantics, JSONObjectBuilder jsonObjectBuilder,
		String name, String type) {

		JSONObjectBuilder operationJsonObjectBuilder = new JSONObjectBuilder();

		String customDocumentation = _getCustomDocumentation(
			name + "/" + actionSemantics.name());

		_documentationMessageMapper.mapAction(
			operationJsonObjectBuilder, name, type, actionSemantics,
			customDocumentation);

		_documentationMessageMapper.onFinishAction(
			jsonObjectBuilder, operationJsonObjectBuilder, actionSemantics);
	}

	private void _writeAllFields(
		Representor<?> representor,
		JSONObjectBuilder resourceJsonObjectBuilder) {

		Stream<DocumentationField> fieldNamesStream =
			_calculateNestableFieldNames(representor);

		Stream<? extends RelatedCollection<?, ? extends Identifier>>
			relatedCollections = representor.getRelatedCollections();

		Stream<DocumentationField> relatedCollectionsNamesStream =
			relatedCollections.map(_createDocumentationFieldFromCollection());

		fieldNamesStream = Stream.concat(
			fieldNamesStream, relatedCollectionsNamesStream);

		_writeFields(fieldNamesStream, resourceJsonObjectBuilder);
	}

	private void _writeDocumentationMetadata(
		JSONObjectBuilder jsonObjectBuilder) {

		Optional<String> apiTitleOptional =
			_documentation.getAPITitleOptional();

		apiTitleOptional.ifPresent(
			title -> _documentationMessageMapper.mapTitle(
				jsonObjectBuilder, title));

		Optional<String> apiDescriptionOptional =
			_documentation.getAPIDescriptionOptional();

		apiDescriptionOptional.ifPresent(
			description -> _documentationMessageMapper.mapDescription(
				jsonObjectBuilder, description));

		Optional<String> entryPointOptional =
			_documentation.getEntryPointOptional();

		entryPointOptional.ifPresent(
			entryPoint -> _documentationMessageMapper.mapEntryPoint(
				jsonObjectBuilder, entryPoint)
		);
	}

	private void _writeFields(
		Stream<DocumentationField> fields,
		JSONObjectBuilder resourceJsonObjectBuilder) {

		Stream<DocumentationField> stream = fields.distinct();

		stream.forEach(
			field -> _writeFormField(resourceJsonObjectBuilder, field));
	}

	private void _writeFormField(
		JSONObjectBuilder resourceJsonObjectBuilder,
		DocumentationField documentationField) {

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

		String customDocumentation = _getCustomDocumentation(
			documentationField.getName());

		_documentationMessageMapper.mapProperty(
			jsonObjectBuilder, documentationField, customDocumentation);

		_documentationMessageMapper.onFinishProperty(
			resourceJsonObjectBuilder, jsonObjectBuilder,
			documentationField.getName());
	}

	private void _writeOperations(
		String name, String type, Resource resource,
		JSONObjectBuilder resourceJsonObjectBuilder) {

		Function<Resource, Stream<ActionSemantics>> actionSemanticsFunction =
			_documentation.getActionSemanticsFunction();

		actionSemanticsFunction.apply(
			resource
		).forEach(
			actionSemantics -> _writeActionSemantics(
				actionSemantics, resourceJsonObjectBuilder, name, type)
		);
	}

	private void _writeRoute(
		JSONObjectBuilder jsonObjectBuilder, Representor<?> representor,
		TriConsumer<JSONObjectBuilder, String, String> writeResourceTriConsumer,
		BiConsumer<String, JSONObjectBuilder> writeOperationsBiConsumer,
		Consumer<JSONObjectBuilder> writeFieldsRepresentor) {

		String type = representor.getPrimaryType();

		JSONObjectBuilder resourceJsonObjectBuilder = new JSONObjectBuilder();

		String customDocumentation = _getCustomDocumentation(type);

		writeResourceTriConsumer.accept(
			resourceJsonObjectBuilder, type, customDocumentation);

		writeOperationsBiConsumer.accept(type, resourceJsonObjectBuilder);

		writeFieldsRepresentor.accept(resourceJsonObjectBuilder);

		_documentationMessageMapper.onFinishResource(
			jsonObjectBuilder, resourceJsonObjectBuilder, type);
	}

	private final CustomDocumentation _customDocumentation;
	private final Documentation _documentation;
	private final DocumentationMessageMapper _documentationMessageMapper;
	private final RequestInfo _requestInfo;
	private final Function<Class<?>, Optional<String>> _typeFunction;

}