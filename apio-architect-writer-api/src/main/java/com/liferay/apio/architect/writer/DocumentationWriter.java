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

package com.liferay.apio.architect.writer;

import com.google.gson.JsonObject;

import com.liferay.apio.architect.alias.RequestFunction;
import com.liferay.apio.architect.documentation.Documentation;
import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.form.FormField;
import com.liferay.apio.architect.message.json.DocumentationMessageMapper;
import com.liferay.apio.architect.message.json.JSONObjectBuilder;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.request.RequestInfo;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

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
	}

	/**
	 * Writes the {@link Documentation} to a string.
	 *
	 * @return the JSON representation of the {@code Documentation}
	 */
	public String write() {
		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

		RequestFunction<Optional<String>> apiTitleRequestFunction =
			_documentation.getAPITitleRequestFunction();

		apiTitleRequestFunction.apply(
			_requestInfo.getHttpServletRequest()
		).ifPresent(
			title -> _documentationMessageMapper.mapTitle(
				jsonObjectBuilder, title)
		);

		RequestFunction<Optional<String>> apiDescriptionRequestFunction =
			_documentation.getAPIDescriptionRequestFunction();

		apiDescriptionRequestFunction.apply(
			_requestInfo.getHttpServletRequest()
		).ifPresent(
			description -> _documentationMessageMapper.mapDescription(
				jsonObjectBuilder, description)
		);

		_documentationMessageMapper.onStart(
			jsonObjectBuilder, _documentation, _requestInfo.getHttpHeaders());

		Supplier<Map<String, Representor>> representorMapSupplier =
			_documentation.getRepresentorMapSupplier();

		Map<String, Representor> representorMap = representorMapSupplier.get();

		Supplier<Map<String, CollectionRoutes>> routesMapSupplier =
			_documentation.getRoutesMapSupplier();

		Map<String, CollectionRoutes> routesMap = routesMapSupplier.get();

		routesMap.forEach(
			(name, collectionRoutes) -> _writeRoute(
				jsonObjectBuilder, representorMap, routesMap, name,
				this::_writePageOperations));

		Supplier<Map<String, ItemRoutes>> itemRoutesMapSupplier =
			_documentation.getItemRoutesMapSupplier();

		Map<String, ItemRoutes> itemRoutesMap = itemRoutesMapSupplier.get();

		itemRoutesMap.forEach(
			(name, itemRoutes) -> _writeRoute(
				jsonObjectBuilder, representorMap, routesMap, name,
				this::_writeItemOperations));

		_documentationMessageMapper.onFinish(
			jsonObjectBuilder, _documentation, _requestInfo.getHttpHeaders());

		JsonObject jsonObject = jsonObjectBuilder.build();

		return jsonObject.toString();
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
			public BuildStep requestInfo(RequestInfo requestInfo) {
				_requestInfo = requestInfo;

				return new BuildStep();
			}

		}

		private Documentation _documentation;
		private DocumentationMessageMapper _documentationMessageMapper;
		private RequestInfo _requestInfo;

	}

	private Optional<FormField> _findFormFieldWithName(
		Optional<Form<FormField>> form, String name) {

		return form.map(
			Form::getFormFields
		).orElseGet(
			ArrayList::new
		).stream(
		).filter(
			x -> x.name.equals(name)
		).findFirst();
	}

	private void _writeFields(
		Map<String, Function> functionMap,
		JSONObjectBuilder resourceJsonObjectBuilder,
		Optional<Form<FormField>> formOptional) {

		functionMap.forEach(
			(field, function) -> {
				JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

				_documentationMessageMapper.onStartProperty(
					jsonObjectBuilder, jsonObjectBuilder);

				_documentationMessageMapper.mapFormField(
					jsonObjectBuilder, field,
					_findFormFieldWithName(formOptional, field));

				_documentationMessageMapper.onFinishProperty(
					resourceJsonObjectBuilder, jsonObjectBuilder);
			});
	}

	private void _writeFields(
		Representor representor, JSONObjectBuilder resourceJsonObjectBuilder,
		Optional<CollectionRoutes> collectionRoutesOptional) {

		Optional<Form<FormField>> formOptional = collectionRoutesOptional.map(
			collectionRoutes -> collectionRoutes.getFormOptional()
		).orElse(
			Optional.empty()
		);

		_writeFields(
			representor.getBooleanFunctions(), resourceJsonObjectBuilder,
			formOptional);

		_writeFields(
			representor.getBooleanListFunctions(), resourceJsonObjectBuilder,
			formOptional);

		_writeFields(
			representor.getStringFunctions(), resourceJsonObjectBuilder,
			formOptional);

		_writeFields(
			representor.getStringListFunctions(), resourceJsonObjectBuilder,
			formOptional);

		_writeFields(
			representor.getNumberFunctions(), resourceJsonObjectBuilder,
			formOptional);

		_writeFields(
			representor.getNumberListFunctions(), resourceJsonObjectBuilder,
			formOptional);

		_writeFields(
			representor.getNestedFunctions(), resourceJsonObjectBuilder,
			formOptional);

		_writeFields(
			representor.getBinaryFunctions(), resourceJsonObjectBuilder,
			formOptional);

		_writeFields(
			representor.getLinks(), resourceJsonObjectBuilder, formOptional);
	}

	private void _writeItemOperations(
		String name, JSONObjectBuilder resourceJsonObjectBuilder) {

		Supplier<Map<String, ItemRoutes>> itemRoutesMapSupplier =
			_documentation.getItemRoutesMapSupplier();

		Map<String, ItemRoutes> itemRoutesMap = itemRoutesMapSupplier.get();

		Optional<ItemRoutes> itemRoutesOptional = Optional.ofNullable(
			itemRoutesMap.getOrDefault(name, null));

		itemRoutesOptional.ifPresent(
			itemRoutes -> {
				_writeOperation(
					itemRoutes.getItemFunctionOptional(),
					resourceJsonObjectBuilder, name);

				_writeOperation(
					itemRoutes.getUpdateItemFunctionOptional(),
					resourceJsonObjectBuilder, name);

				_writeOperation(
					itemRoutes.getDeleteConsumerOptional(),
					resourceJsonObjectBuilder, name);
			});
	}

	private void _writeOperation(
		Optional<RequestFunction> requestFunctionOptional,
		JSONObjectBuilder jsonObjectBuilder, String resource) {

		JSONObjectBuilder operationJsonObjectBuilder = new JSONObjectBuilder();

		_documentationMessageMapper.onStartOperation(
			jsonObjectBuilder, operationJsonObjectBuilder);

		requestFunctionOptional.ifPresent(
			requestFunction -> _documentationMessageMapper.mapOperation(
				operationJsonObjectBuilder, resource, requestFunction));

		_documentationMessageMapper.onFinishOperation(
			jsonObjectBuilder, operationJsonObjectBuilder);
	}

	private void _writePageOperations(
		String resource, JSONObjectBuilder resourceJsonObjectBuilder) {

		Supplier<Map<String, CollectionRoutes>> routesMapSupplier =
			_documentation.getRoutesMapSupplier();

		Map<String, CollectionRoutes> collectionRoutesMap =
			routesMapSupplier.get();

		Optional<CollectionRoutes> collectionRoutesOptional =
			Optional.ofNullable(
				collectionRoutesMap.getOrDefault(resource, null));

		collectionRoutesOptional.ifPresent(
			collectionRoutes -> {
				_writeOperation(
					collectionRoutes.getGetPageFunctionOptional(),
					resourceJsonObjectBuilder, resource);

				_writeOperation(
					collectionRoutes.getCreateItemFunctionOptional(),
					resourceJsonObjectBuilder, resource);
			});
	}

	private void _writeRoute(
		JSONObjectBuilder jsonObjectBuilder,
		Map<String, Representor> representorMap,
		Map<String, CollectionRoutes> collectionRoutesMap, String name,
		BiConsumer<String, JSONObjectBuilder> writeOperationsBiConsumer) {

		JSONObjectBuilder resourceJsonObjectBuilder = new JSONObjectBuilder();

		_documentationMessageMapper.onStartResource(
			jsonObjectBuilder, resourceJsonObjectBuilder);

		_documentationMessageMapper.mapResource(
			resourceJsonObjectBuilder, name);

		writeOperationsBiConsumer.accept(name, resourceJsonObjectBuilder);

		Representor representor = representorMap.get(name);

		Optional<CollectionRoutes> collectionRoutesOptional =
			Optional.ofNullable(collectionRoutesMap.getOrDefault(name, null));

		_writeFields(
			representor, resourceJsonObjectBuilder, collectionRoutesOptional);

		_documentationMessageMapper.onFinishResource(
			jsonObjectBuilder, resourceJsonObjectBuilder);
	}

	private final Documentation _documentation;
	private final DocumentationMessageMapper _documentationMessageMapper;
	private final RequestInfo _requestInfo;

}