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

package com.liferay.vulcan.jaxrs.json.internal.writer;

import static org.osgi.service.component.annotations.ReferenceCardinality.AT_LEAST_ONE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.google.gson.JsonObject;

import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.error.VulcanDeveloperError.MustHaveProvider;
import com.liferay.vulcan.language.Language;
import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.message.json.SingleModelMessageMapper;
import com.liferay.vulcan.pagination.SingleModel;
import com.liferay.vulcan.request.RequestInfo;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.identifier.Identifier;
import com.liferay.vulcan.response.control.Embedded;
import com.liferay.vulcan.response.control.Fields;
import com.liferay.vulcan.result.Try;
import com.liferay.vulcan.uri.Path;
import com.liferay.vulcan.url.ServerURL;
import com.liferay.vulcan.wiring.osgi.manager.CollectionResourceManager;
import com.liferay.vulcan.wiring.osgi.manager.PathIdentifierMapperManager;
import com.liferay.vulcan.wiring.osgi.manager.ProviderManager;
import com.liferay.vulcan.wiring.osgi.util.GenericUtil;
import com.liferay.vulcan.writer.FieldsWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Writes single models by using the {@link SingleModelMessageMapper} that
 * corresponds to the media type.
 *
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(
	immediate = true, property = "liferay.vulcan.message.body.writer=true"
)
@Provider
public class SingleModelMessageBodyWriter<T>
	implements MessageBodyWriter<Try.Success<SingleModel<T>>> {

	@Override
	public long getSize(
		Try.Success<SingleModel<T>> success, Class<?> clazz, Type genericType,
		Annotation[] annotations, MediaType mediaType) {

		return -1;
	}

	@Override
	public boolean isWriteable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		Try<Class<Object>> classTry =
			GenericUtil.getFirstGenericTypeArgumentTry(genericType);

		return classTry.filter(
			SingleModel.class::equals
		).isSuccess();
	}

	@Override
	public void writeTo(
			Try.Success<SingleModel<T>> success, Class<?> clazz,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
			entityStream, StandardCharsets.UTF_8);

		PrintWriter printWriter = new PrintWriter(outputStreamWriter, true);

		Stream<SingleModelMessageMapper<T>> stream =
			_singleModelMessageMappers.stream();

		String mediaTypeString = mediaType.toString();

		SingleModel<T> singleModel = success.getValue();

		SingleModelMessageMapper<T> singleModelMessageMapper = stream.filter(
			messageMapper ->
				mediaTypeString.equals(messageMapper.getMediaType()) &&
				messageMapper.supports(singleModel, _httpHeaders)
		).findFirst(
		).orElseThrow(
			() -> new VulcanDeveloperError.MustHaveMessageMapper(
				mediaTypeString, singleModel.getModelClass())
		);

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilder();

		Optional<ServerURL> optional = _providerManager.provideOptional(
			ServerURL.class, _httpServletRequest);

		ServerURL serverURL = optional.orElseThrow(
			() -> new MustHaveProvider(ServerURL.class));

		RequestInfo requestInfo = RequestInfo.create(
			builder -> builder.httpHeaders(
				_httpHeaders
			).serverURL(
				serverURL
			).embedded(
				_providerManager.provideOrNull(
					Embedded.class, _httpServletRequest)
			).fields(
				_providerManager.provideOrNull(Fields.class, _httpServletRequest)
			).language(
				_providerManager.provideOrNull(
					Language.class, _httpServletRequest)
			).build());

		_writeModel(
			singleModelMessageMapper, jsonObjectBuilder, singleModel,
			requestInfo);

		JsonObject jsonObject = jsonObjectBuilder.build();

		printWriter.println(jsonObject.toString());

		printWriter.close();
	}

	private <U> Optional<FieldsWriter<U, Identifier>> _getFieldsWriter(
		SingleModel<U> singleModel, FunctionalList<String> embeddedPathElements,
		RequestInfo requestInfo) {

		Optional<Representor<U, Identifier>> representorOptional =
			_collectionResourceManager.getRepresentorOptional(
				singleModel.getModelClass());

		Optional<Path> pathOptional = _getPathOptional(singleModel);

		return representorOptional.flatMap(
			representor -> pathOptional.map(
				path -> new FieldsWriter<>(
					singleModel, requestInfo, representor, path,
					embeddedPathElements)));
	}

	private <V> Optional<Path> _getPathOptional(SingleModel<V> singleModel) {
		Optional<Representor<V, Identifier>> optional =
			_collectionResourceManager.getRepresentorOptional(
				singleModel.getModelClass());

		return optional.flatMap(
			representor -> _pathIdentifierMapperManager.map(
				representor.getIdentifier(singleModel.getModel()),
				representor.getIdentifierClass(), singleModel.getModelClass()));
	}

	private <V> void _writeEmbeddedModelFields(
		SingleModelMessageMapper<?> singleModelMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, SingleModel<V> singleModel,
		FunctionalList<String> embeddedPathElements, RequestInfo requestInfo) {

		Optional<FieldsWriter<V, Identifier>> optional = _getFieldsWriter(
			singleModel, embeddedPathElements, requestInfo);

		if (!optional.isPresent()) {
			return;
		}

		FieldsWriter<V, Identifier> fieldsWriter = optional.get();

		fieldsWriter.writeBooleanFields(
			(field, value) ->
				singleModelMessageMapper.mapEmbeddedResourceBooleanField(
					jsonObjectBuilder, embeddedPathElements, field, value));

		fieldsWriter.writeLocalizedStringFields(
			(field, value) ->
				singleModelMessageMapper.mapEmbeddedResourceStringField(
					jsonObjectBuilder, embeddedPathElements, field, value));

		fieldsWriter.writeNumberFields(
			(field, value) ->
				singleModelMessageMapper.mapEmbeddedResourceNumberField(
					jsonObjectBuilder, embeddedPathElements, field, value));

		fieldsWriter.writeStringFields(
			(field, value) ->
				singleModelMessageMapper.mapEmbeddedResourceStringField(
					jsonObjectBuilder, embeddedPathElements, field, value));

		fieldsWriter.writeLinks(
			(fieldName, link) ->
				singleModelMessageMapper.mapEmbeddedResourceLink(
					jsonObjectBuilder, embeddedPathElements, fieldName, link));

		fieldsWriter.writeTypes(
			types -> singleModelMessageMapper.mapEmbeddedResourceTypes(
				jsonObjectBuilder, embeddedPathElements, types));

		fieldsWriter.writeBinaries(
			(field, value) ->
				singleModelMessageMapper.mapEmbeddedResourceStringField(
					jsonObjectBuilder, embeddedPathElements, field, value));

		fieldsWriter.writeEmbeddedRelatedModels(
			this::_getPathOptional,
			(relatedSingleModel, resourceEmbeddedPathElements) ->
				_writeEmbeddedModelFields(
					singleModelMessageMapper, jsonObjectBuilder,
					relatedSingleModel, resourceEmbeddedPathElements,
					requestInfo),
			(resourceURL, resourceEmbeddedPathElements) ->
				singleModelMessageMapper.mapLinkedResourceURL(
					jsonObjectBuilder, resourceEmbeddedPathElements,
					resourceURL),
			(resourceURL, resourceEmbeddedPathElements) ->
				singleModelMessageMapper.mapEmbeddedResourceURL(
					jsonObjectBuilder, resourceEmbeddedPathElements,
					resourceURL));

		fieldsWriter.writeLinkedRelatedModels(
			this::_getPathOptional,
			(url, resourceEmbeddedPathElements) ->
				singleModelMessageMapper.mapLinkedResourceURL(
					jsonObjectBuilder, resourceEmbeddedPathElements, url));

		fieldsWriter.writeRelatedCollections(
			className -> _collectionResourceManager.getNameOptional(className),
			(url, resourceEmbeddedPathElements) ->
				singleModelMessageMapper.mapLinkedResourceURL(
					jsonObjectBuilder, resourceEmbeddedPathElements, url));
	}

	private <U> void _writeModel(
		SingleModelMessageMapper<U> singleModelMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, SingleModel<U> singleModel,
		RequestInfo requestInfo) {

		Optional<FieldsWriter<U, Identifier>> optional = _getFieldsWriter(
			singleModel, null, requestInfo);

		if (!optional.isPresent()) {
			return;
		}

		FieldsWriter<U, Identifier> fieldsWriter = optional.get();

		singleModelMessageMapper.onStart(
			jsonObjectBuilder, singleModel.getModel(),
			singleModel.getModelClass(), requestInfo.getHttpHeaders());

		fieldsWriter.writeBooleanFields(
			(field, value) -> singleModelMessageMapper.mapBooleanField(
				jsonObjectBuilder, field, value));

		fieldsWriter.writeLocalizedStringFields(
			(field, value) -> singleModelMessageMapper.mapStringField(
				jsonObjectBuilder, field, value));

		fieldsWriter.writeNumberFields(
			(field, value) -> singleModelMessageMapper.mapNumberField(
				jsonObjectBuilder, field, value));

		fieldsWriter.writeStringFields(
			(field, value) -> singleModelMessageMapper.mapStringField(
				jsonObjectBuilder, field, value));

		fieldsWriter.writeLinks(
			(fieldName, link) -> singleModelMessageMapper.mapLink(
				jsonObjectBuilder, fieldName, link));

		fieldsWriter.writeTypes(
			types -> singleModelMessageMapper.mapTypes(
				jsonObjectBuilder, types));

		fieldsWriter.writeBinaries(
			(field, value) -> singleModelMessageMapper.mapStringField(
				jsonObjectBuilder, field, value));

		fieldsWriter.writeSingleURL(
			url -> singleModelMessageMapper.mapSelfURL(jsonObjectBuilder, url));

		fieldsWriter.writeEmbeddedRelatedModels(
			this::_getPathOptional,
			(relatedSingleModel, embeddedPathElements) ->
				_writeEmbeddedModelFields(
					singleModelMessageMapper, jsonObjectBuilder,
					relatedSingleModel, embeddedPathElements, requestInfo),
			(resourceURL, embeddedPathElements) ->
				singleModelMessageMapper.mapLinkedResourceURL(
					jsonObjectBuilder, embeddedPathElements, resourceURL),
			(resourceURL, embeddedPathElements) ->
				singleModelMessageMapper.mapEmbeddedResourceURL(
					jsonObjectBuilder, embeddedPathElements, resourceURL));

		fieldsWriter.writeLinkedRelatedModels(
			this::_getPathOptional,
			(url, embeddedPathElements) ->
				singleModelMessageMapper.mapLinkedResourceURL(
					jsonObjectBuilder, embeddedPathElements, url));

		fieldsWriter.writeRelatedCollections(
			className -> _collectionResourceManager.getNameOptional(className),
			(url, embeddedPathElements) ->
				singleModelMessageMapper.mapLinkedResourceURL(
					jsonObjectBuilder, embeddedPathElements, url));

		singleModelMessageMapper.onFinish(
			jsonObjectBuilder, singleModel.getModel(),
			singleModel.getModelClass(), requestInfo.getHttpHeaders());
	}

	@Reference
	private CollectionResourceManager _collectionResourceManager;

	@Context
	private HttpHeaders _httpHeaders;

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private PathIdentifierMapperManager _pathIdentifierMapperManager;

	@Reference
	private ProviderManager _providerManager;

	@Reference(cardinality = AT_LEAST_ONE, policyOption = GREEDY)
	private List<SingleModelMessageMapper<T>> _singleModelMessageMappers;

	@Reference
	private WriterHelper _writerHelper;

}