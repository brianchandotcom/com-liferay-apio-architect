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

package com.liferay.vulcan.jax.rs.writer.json.internal;

import static org.osgi.service.component.annotations.ReferenceCardinality.AT_LEAST_ONE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.message.PageJSONMessageMapper;
import com.liferay.vulcan.message.RequestInfo;
import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.wiring.osgi.RepresentorManager;
import com.liferay.vulcan.wiring.osgi.URIResolver;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.net.URI;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
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
public class PageMessageBodyWriter<T> implements MessageBodyWriter<Page<T>> {

	@Override
	public long getSize(
		Page<T> page, Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		return -1;
	}

	@Override
	public boolean isWriteable(
		Class<?> type, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		Class<?> returnType = resourceInfo.getResourceMethod().getReturnType();

		if (!returnType.isAssignableFrom(Page.class)) {
			return false;
		}

		Type genericReturnType =
			resourceInfo.getResourceMethod().getGenericReturnType();

		Type[] actualTypeArguments =
			((ParameterizedType)genericReturnType).getActualTypeArguments();

		try {
			Class<T> modelClass = (Class<T>)actualTypeArguments[0];

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
			Page<T> page, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream)
		throws IOException, WebApplicationException {

		Type genericReturnType =
			resourceInfo.getResourceMethod().getGenericReturnType();

		Type[] actualTypeArguments =
			((ParameterizedType)genericReturnType).getActualTypeArguments();

		Class<T> modelClass = (Class<T>)actualTypeArguments[0];

		JSONObjectBuilder jsonObjectBuilder = new JSONObjectBuilderImpl();

		RequestInfo requestInfo = new RequestInfoImpl(mediaType, httpHeaders);

		Stream<PageJSONMessageMapper<T>> stream =
			_pageJSONMessageMappers.stream();

		PageJSONMessageMapper<T> pageJSONMessageMapper = stream.filter(
			bodyWriter ->
				mediaType.toString().equals(bodyWriter.getMediaType()) &&
					bodyWriter.supports(page, modelClass, requestInfo)
		).findFirst().orElseThrow(
			() -> new VulcanDeveloperError.CannotFindMessageMapper(
				mediaType.toString(), modelClass)
		);

		pageJSONMessageMapper.onStart(
			jsonObjectBuilder, page, modelClass, requestInfo);

		_writeItems(
			page, modelClass, requestInfo, pageJSONMessageMapper,
			jsonObjectBuilder);

		_writeItemTotalCount(pageJSONMessageMapper, jsonObjectBuilder, page);

		_writePageCount(pageJSONMessageMapper, jsonObjectBuilder, page);

		_writePageURLs(
			pageJSONMessageMapper, jsonObjectBuilder, page, modelClass);

		_writeCollectionURL(
			pageJSONMessageMapper, jsonObjectBuilder, modelClass);

		pageJSONMessageMapper.onFinish(
			jsonObjectBuilder, page, modelClass, requestInfo);
	}

	@Context
	protected ResourceInfo resourceInfo;

	@Context
	protected UriInfo uriInfo;

	private String _getAbsoluteURL(String relativeResourceURI) {
		UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().clone();

		URI uri = uriBuilder.path(relativeResourceURI).build();

		return uri.toString();
	}

	private String _getCollectionURL(Class<T> modelClass) {
		Optional<String> optional =
			_uriResolver.getCollectionResourceURIOptional(modelClass);

		String uri = optional.orElseThrow(
			() -> new VulcanDeveloperError.CannotCalculateURI(modelClass));

		return _getAbsoluteURL(uri);
	}

	private String _getPageURL(
		Class<T> modelClass, int page, int itemsPerPage) {

		String url = _getCollectionURL(modelClass);

		return url + "?page=" + page + "&per_page=" + itemsPerPage;
	}

	private void _writeCollectionURL(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, Class<T> modelClass) {

		String url = _getCollectionURL(modelClass);

		pageJSONMessageMapper.mapCollectionURL(jsonObjectBuilder, url);
	}

	private <U> void _writeItemFields(
		PageJSONMessageMapper<U> pageJSONMessageMapper,
		JSONObjectBuilder rootJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, U model, Class<U> modelClass) {

		Map<String, Function<U, Object>> fieldFunctions =
			_representorManager.getFieldFunctions(modelClass);

		for (String field : fieldFunctions.keySet()) {
			Object data = fieldFunctions.get(field).apply(model);

			if (data != null) {
				pageJSONMessageMapper.mapItemField(
					rootJSONObjectBuilder, itemJSONObjectBuilder, field, data);
			}
		}
	}

	private void _writeItemLinks(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder rootJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, Class<T> modelClass) {

		Map<String, String> links = _representorManager.getLinks(modelClass);

		for (String key : links.keySet()) {
			pageJSONMessageMapper.mapItemLink(
				rootJSONObjectBuilder, itemJSONObjectBuilder, key,
				links.get(key));
		}
	}

	private void _writeItems(
		Page<T> page, Class<T> modelClass, RequestInfo requestInfo,
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder) {

		for (T model : page.getItems()) {
			JSONObjectBuilder itemJSONObjectBuilder =
				new JSONObjectBuilderImpl();

			pageJSONMessageMapper.onStartItem(
				jsonObjectBuilder, itemJSONObjectBuilder, model, modelClass,
				requestInfo);

			_writeItemFields(
				pageJSONMessageMapper, jsonObjectBuilder, itemJSONObjectBuilder,
				model, modelClass);

			_writeItemLinks(
				pageJSONMessageMapper, jsonObjectBuilder, itemJSONObjectBuilder,
				modelClass);

			_writeItemTypes(
				pageJSONMessageMapper, jsonObjectBuilder, itemJSONObjectBuilder,
				modelClass);

			_writeItemURL(
				pageJSONMessageMapper, jsonObjectBuilder, itemJSONObjectBuilder,
				model, modelClass);

			pageJSONMessageMapper.onFinishItem(
				jsonObjectBuilder, itemJSONObjectBuilder, model, modelClass,
				requestInfo);
		}
	}

	private void _writeItemTotalCount(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, Page<T> page) {

		int totalCount = page.getTotalCount();

		pageJSONMessageMapper.mapItemTotalCount(jsonObjectBuilder, totalCount);
	}

	private void _writeItemTypes(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder rootJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, Class<T> modelClass) {

		List<String> types = _representorManager.getTypes(modelClass);

		pageJSONMessageMapper.mapItemTypes(
			rootJSONObjectBuilder, itemJSONObjectBuilder, types);
	}

	private void _writeItemURL(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder rootJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, T model, Class<T> modelClass) {

		Optional<String> optional = _uriResolver.getSingleResourceURIOptional(
			modelClass, model);

		String uri = optional.orElseThrow(
			() -> new VulcanDeveloperError.CannotCalculateURI(modelClass));

		String url = _getAbsoluteURL(uri);

		pageJSONMessageMapper.mapItemSelfURL(
			rootJSONObjectBuilder, itemJSONObjectBuilder, url);
	}

	private void _writePageCount(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, Page<T> page) {

		int count = page.getItems().size();

		pageJSONMessageMapper.mapPageCount(jsonObjectBuilder, count);
	}

	private void _writePageURLs(
		PageJSONMessageMapper<T> pageJSONMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, Page<T> page,
		Class<T> modelClass) {

		int itemsPerPage = page.getItemsPerPage();

		int currentPage = page.getPageNumber();

		pageJSONMessageMapper.mapCurrentPageURL(
			jsonObjectBuilder,
			_getPageURL(modelClass, currentPage, itemsPerPage));

		pageJSONMessageMapper.mapFirstPageURL(
			jsonObjectBuilder, _getPageURL(modelClass, 1, itemsPerPage));

		if (page.hasPrevious()) {
			pageJSONMessageMapper.mapPreviousPageURL(
				jsonObjectBuilder,
				_getPageURL(modelClass, currentPage - 1, itemsPerPage));
		}

		if (page.hasNext()) {
			pageJSONMessageMapper.mapNextPageURL(
				jsonObjectBuilder,
				_getPageURL(modelClass, currentPage + 1, itemsPerPage));
		}

		pageJSONMessageMapper.mapLastPageURL(
			jsonObjectBuilder,
			_getPageURL(modelClass, page.getLastPageNumber(), itemsPerPage));
	}

	@Reference(cardinality = AT_LEAST_ONE, policyOption = GREEDY)
	private List<PageJSONMessageMapper<T>> _pageJSONMessageMappers;

	@Reference
	private RepresentorManager _representorManager;

	@Reference
	private URIResolver _uriResolver;

}