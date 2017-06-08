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

package com.liferay.vulcan.jaxrs.writer.json.internal;

import static org.osgi.service.component.annotations.ReferenceCardinality.AT_LEAST_ONE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.RequestInfo;
import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.message.json.SingleModelMessageMapper;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.wiring.osgi.RelatedModel;
import com.liferay.vulcan.wiring.osgi.RepresentorManager;
import com.liferay.vulcan.wiring.osgi.URIResolver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(
	immediate = true, property = "liferay.vulcan.message.body.writer=true"
)
@Provider
public class SingleModelMessageBodyWriter<T> implements MessageBodyWriter<T> {

	@Override
	public long getSize(
		T model, Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return -1;
	}

	@Override
	public boolean isWriteable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		try {
			Class<T> modelClass = (Class<T>)genericType;

			if (modelClass.isAssignableFrom(Page.class)) {
				return false;
			}

			Optional<ModelRepresentorMapper<T>> optional =
				_representorManager.getModelRepresentorMapperOptional(
					modelClass);

			if (!optional.isPresent()) {
				return false;
			}

			return true;
		}
		catch (ClassCastException cce) {
			return false;
		}
	}

	@Override
	public void writeTo(
			T model, Class<?> clazz, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		Class<T> modelClass = (Class<T>)genericType;

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilderImpl();

		RequestInfo requestInfo = new RequestInfoImpl(mediaType, httpHeaders);

		Stream<SingleModelMessageMapper<T>> stream =
			_singleModelMessageMappers.stream();

		String mediaTypeString = mediaType.toString();

		SingleModelMessageMapper<T> singleModelMessageMapper = stream.filter(
			messageMapper ->
				mediaTypeString.equals(messageMapper.getMediaType()) &&
					messageMapper.supports(model, modelClass, requestInfo)
		).findFirst(
		).orElseThrow(
			() -> new VulcanDeveloperError.MustHaveMessageMapper(
				mediaTypeString, modelClass)
		);

		_writeModel(
			singleModelMessageMapper, jsonObjectBuilder, model, modelClass,
			requestInfo);

		PrintWriter printWriter = new PrintWriter(entityStream, true);

		JSONObject jsonObject = jsonObjectBuilder.build();

		printWriter.println(jsonObject.toString());

		printWriter.close();
	}

	private <U, V> void _writeEmbeddedRelatedModel(
		SingleModelMessageMapper<?> singleModelMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, RelatedModel<U, V> relatedModel,
		U parentModel, FunctionalList<String> parentEmbeddedPathElements) {

		_writerHelper.writeRelatedModel(
			relatedModel, parentModel, parentEmbeddedPathElements, _uriInfo,
			(model, modelClass, url, embeddedPathElements) -> {
				_writerHelper.writeFields(
					model, modelClass, (fieldName, value) ->
						singleModelMessageMapper.mapEmbeddedResourceField(
							jsonObjectBuilder, embeddedPathElements, fieldName,
							value));

				_writerHelper.writeLinks(
					modelClass, (fieldName, link) ->
						singleModelMessageMapper.mapEmbeddedResourceLink(
							jsonObjectBuilder, embeddedPathElements, fieldName,
							link));

				_writerHelper.writeTypes(modelClass, types ->
					singleModelMessageMapper.mapEmbeddedResourceTypes(
						jsonObjectBuilder, embeddedPathElements, types));

				singleModelMessageMapper.mapEmbeddedResourceURL(
					jsonObjectBuilder, embeddedPathElements, url);

				List<RelatedModel<V, ?>> embeddedRelatedModels =
					_representorManager.getEmbeddedRelatedModels(modelClass);

				embeddedRelatedModels.forEach(
					embeddedRelatedModel -> _writeEmbeddedRelatedModel(
						singleModelMessageMapper, jsonObjectBuilder,
						embeddedRelatedModel, model, embeddedPathElements));

				List<RelatedModel<V, ?>> linkedRelatedModels =
					_representorManager.getLinkedRelatedModels(modelClass);

				linkedRelatedModels.forEach(
					linkedRelatedModel -> _writeLinkedRelatedModel(
						singleModelMessageMapper, jsonObjectBuilder,
						linkedRelatedModel, model, embeddedPathElements));
			});
	}

	private <U, V> void _writeLinkedRelatedModel(
		SingleModelMessageMapper<?> singleModelMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, RelatedModel<U, V> relatedModel,
		U parentModel, FunctionalList<String> parentEmbeddedPathElements) {

		_writerHelper.writeRelatedModel(
			relatedModel, parentModel, parentEmbeddedPathElements, _uriInfo,
			(model, modelClass, url, embeddedPathElements) ->
				singleModelMessageMapper.mapLinkedResourceURL(
					jsonObjectBuilder, embeddedPathElements, url));
	}

	private <U> void _writeModel(
		SingleModelMessageMapper<U> singleModelMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, U model, Class<U> modelClass,
		RequestInfo requestInfo) {

		singleModelMessageMapper.onStart(
			jsonObjectBuilder, model, modelClass, requestInfo);

		_writerHelper.writeFields(
			model, modelClass, (field, value) ->
				singleModelMessageMapper.mapField(
					jsonObjectBuilder, field, value));

		_writerHelper.writeLinks(
			modelClass, (fieldName, link) -> singleModelMessageMapper.mapLink(
				jsonObjectBuilder, fieldName, link));

		_writerHelper.writeTypes(
			modelClass, types -> singleModelMessageMapper.mapTypes(
				jsonObjectBuilder, types));

		_writerHelper.writeSingleResourceURL(
			model, modelClass, _uriInfo, url ->
				singleModelMessageMapper.mapSelfURL(jsonObjectBuilder, url));

		List<RelatedModel<U, ?>> embeddedRelatedModels =
			_representorManager.getEmbeddedRelatedModels(modelClass);

		embeddedRelatedModels.forEach(
			embeddedRelatedModel -> _writeEmbeddedRelatedModel(
				singleModelMessageMapper, jsonObjectBuilder,
				embeddedRelatedModel, model, null));

		List<RelatedModel<U, ?>> linkedRelatedModels =
			_representorManager.getLinkedRelatedModels(modelClass);

		linkedRelatedModels.forEach(
			linkedRelatedModel -> _writeLinkedRelatedModel(
				singleModelMessageMapper, jsonObjectBuilder, linkedRelatedModel,
				model, null));

		singleModelMessageMapper.onFinish(
			jsonObjectBuilder, model, modelClass, requestInfo);
	}

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private RepresentorManager _representorManager;

	@Reference(cardinality = AT_LEAST_ONE, policyOption = GREEDY)
	private List<SingleModelMessageMapper<T>> _singleModelMessageMappers;

	@Context
	private UriInfo _uriInfo;

	@Reference
	private URIResolver _uriResolver;

	@Reference
	private WriterHelper _writerHelper;

}