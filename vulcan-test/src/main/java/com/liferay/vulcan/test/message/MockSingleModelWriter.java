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

package com.liferay.vulcan.test.message;

import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.json.JSONObjectBuilder;
import com.liferay.vulcan.message.json.SingleModelMessageMapper;
import com.liferay.vulcan.test.resource.InnerModel;
import com.liferay.vulcan.test.resource.RootModel;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

/**
 * This class provides methods that can be used for testing single model message
 * mappers.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public class MockSingleModelWriter {

	/**
	 * Writes a model of type {@link RootModel}, with an embedded {@link
	 * InnerModel} and some links.
	 *
	 * @param  singleModelMessageMapper the message mapper to use for writing
	 *         the json object
	 * @param  jsonObjectBuilder the json object builder used to construct the
	 *         {@link com.google.gson.JsonObject}
	 * @param  httpHeaders the HTTP headers from the request
	 * @review
	 */
	public static void write(
		SingleModelMessageMapper<RootModel> singleModelMessageMapper,
		JSONObjectBuilder jsonObjectBuilder, HttpHeaders httpHeaders) {

		RootModel rootModel = new RootModel();

		singleModelMessageMapper.onStart(
			jsonObjectBuilder, rootModel, RootModel.class, httpHeaders);

		Map<String, String> stringFields = rootModel.getStringFields();

		stringFields.forEach(
			(key, value) -> singleModelMessageMapper.mapStringField(
				jsonObjectBuilder, key, value));

		Map<String, Number> numberFields = rootModel.getNumberFields();

		numberFields.forEach(
			(key, value) -> singleModelMessageMapper.mapNumberField(
				jsonObjectBuilder, key, value));

		Map<String, Boolean> booleanFields = rootModel.getBooleanFields();

		booleanFields.forEach(
			(key, value) -> singleModelMessageMapper.mapBooleanField(
				jsonObjectBuilder, key, value));

		Map<String, String> links = rootModel.getLinks();

		links.forEach(
			(key, value) -> singleModelMessageMapper.mapLink(
				jsonObjectBuilder, key, value));

		singleModelMessageMapper.mapTypes(
			jsonObjectBuilder, rootModel.getTypes());

		singleModelMessageMapper.mapSelfURL(
			jsonObjectBuilder, rootModel.getURL());

		singleModelMessageMapper.mapLinkedResourceURL(
			jsonObjectBuilder, _first_linked_path_elements,
			"localhost:8080/first-linked");

		singleModelMessageMapper.mapLinkedResourceURL(
			jsonObjectBuilder, _second_linked_path_elements,
			"localhost:8080/second-linked");

		singleModelMessageMapper.mapLinkedResourceURL(
			jsonObjectBuilder, _third_linked_path_elements,
			"localhost:8080/third-linked");

		writeEmbeddedModel(
			singleModelMessageMapper, jsonObjectBuilder,
			_first_embedded_path_elements);

		writeEmbeddedModel(
			singleModelMessageMapper, jsonObjectBuilder,
			_second_embedded_path_elements);

		singleModelMessageMapper.onFinish(
			jsonObjectBuilder, rootModel, RootModel.class, httpHeaders);
	}

	/**
	 * Writes an embedded model of type {@link InnerModel} with some links.
	 *
	 * @param  singleModelMessageMapper the message mapper to use for writing
	 *         the json object
	 * @param  jsonObjectBuilder the json object builder used to construct the
	 *         {@link com.google.gson.JsonObject}
	 * @review
	 */
	public static void writeEmbeddedModel(
		SingleModelMessageMapper singleModelMessageMapper,
		JSONObjectBuilder jsonObjectBuilder,
		FunctionalList<String> embeddedPathElements) {

		InnerModel innerModel = new InnerModel("21");

		Map<String, String> stringFields = innerModel.getStringFields();

		stringFields.forEach(
			(key, value) ->
				singleModelMessageMapper.mapEmbeddedResourceStringField(
					jsonObjectBuilder, embeddedPathElements, key, value));

		Map<String, Number> numberFields = innerModel.getNumberFields();

		numberFields.forEach(
			(key, value) ->
				singleModelMessageMapper.mapEmbeddedResourceNumberField(
					jsonObjectBuilder, embeddedPathElements, key, value));

		Map<String, Boolean> booleanFields = innerModel.getBooleanFields();

		booleanFields.forEach(
			(key, value) ->
				singleModelMessageMapper.mapEmbeddedResourceBooleanField(
					jsonObjectBuilder, embeddedPathElements, key, value));

		Map<String, String> links = innerModel.getLinks();

		links.forEach(
			(key, value) -> singleModelMessageMapper.mapEmbeddedResourceLink(
				jsonObjectBuilder, embeddedPathElements, key, value));

		List<String> types = innerModel.getTypes();

		singleModelMessageMapper.mapEmbeddedResourceTypes(
			jsonObjectBuilder, embeddedPathElements, types);

		String url = innerModel.getURL();

		singleModelMessageMapper.mapEmbeddedResourceURL(
			jsonObjectBuilder, embeddedPathElements, url);
	}

	private MockSingleModelWriter() {
		throw new UnsupportedOperationException();
	}

	private static final FunctionalList<String> _first_embedded_path_elements =
		new FunctionalList<>(null, "first-embedded");
	private static final FunctionalList<String> _first_linked_path_elements =
		new FunctionalList<>(null, "first-linked");
	private static final FunctionalList<String> _second_embedded_path_elements =
		new FunctionalList<>(_first_embedded_path_elements, "second-embedded");
	private static final FunctionalList<String> _second_linked_path_elements =
		new FunctionalList<>(_first_embedded_path_elements, "second-linked");
	private static final FunctionalList<String> _third_linked_path_elements =
		new FunctionalList<>(_second_embedded_path_elements, "third-linked");

}