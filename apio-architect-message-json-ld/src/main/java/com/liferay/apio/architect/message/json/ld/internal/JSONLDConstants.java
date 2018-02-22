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

package com.liferay.apio.architect.message.json.ld.internal;

/**
 * Constants for JSON-LD message mappers with the reserved types and fields
 * names.
 *
 * @author Alejandro Hernández
 * @review
 */
public class JSONLDConstants {

	/**
	 * JSON-LD <a href="https://json-ld.org/spec/latest/json-ld/#the-context">
	 * Context </a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_CONTEXT = "@context";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#description">
	 * description</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_DESCRIPTION = "description";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#expects">expects</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_EXPECTS = "expects";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#first">first</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_FIRST = "first";

	/**
	 * Hydra qualifier.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_HYDRA = "hydra";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#operation">operation</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_HYDRA_OPERATION = hydra("operation");

	/**
	 * JSON-LD <a href="
	 * https://json-ld.org/spec/latest/json-ld/#node-identifiers">
	 * Identifier</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_ID = "@id";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#last">last</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_LAST = "last";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#member">member</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_MEMBER = "member";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#method">method</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_METHOD = "method";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#next">next</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_NEXT = "next";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#numberOfItems">
	 * numberOfItems</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_NUMBER_OF_ITEMS = "numberOfItems";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#previous">previous</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_PREVIOUS = "previous";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#property">property</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_PROPERTY = "property";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#readable">readable</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_READABLE = "readable";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#required">required</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_REQUIRED = "required";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#statusCode">
	 * statusCode</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_STATUS_CODE = "statusCode";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#supportedProperty">
	 * supportedProperty</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_SUPPORTED_PROPERTY =
		"supportedProperty";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#title">title</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_TITLE = "title";

	/**
	 * Hydra <a
	 * href="https://www.w3.org/ns/hydra/core#totalItems">totalItems</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_TOTAL_ITEMS = "totalItems";

	/**
	 * JSON-LD <a href="
	 * https://json-ld.org/spec/latest/json-ld/#specifying-the-type">Type</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_TYPE = "@type";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#view">view</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_VIEW = "view";

	/**
	 * JSON-LD <a href="
	 * https://json-ld.org/spec/latest/json-ld/#default-vocabulary">Vocab</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_VOCAB = "@vocab";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#writeable">writeable</a>.
	 *
	 * @review
	 */
	public static final String FIELD_NAME_WRITEABLE = "writeable";

	/**
	 * JSON-LD <a href="
	 * https://json-ld.org/spec/latest/json-ld/#application-ld-json">Media
	 * Type</a>.
	 *
	 * @review
	 */
	public static final String MEDIA_TYPE = "application/ld+json";

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#ApiDocumentation">
	 * ApiDocumentation</a>.
	 *
	 * @review
	 */
	public static final String TYPE_API_DOCUMENTATION = hydra(
		"ApiDocumentation");

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#Class">Class</a>.
	 *
	 * @review
	 */
	public static final String TYPE_HYDRA_CLASS = hydra("Class");

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#Collection">
	 * Collection</a>.
	 *
	 * @review
	 */
	public static final String TYPE_HYDRA_COLLECTION = hydra("Collection");

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#PartialCollectionView">
	 * PartialCollectionView</a>.
	 *
	 * @review
	 */
	public static final String TYPE_HYDRA_PARTIAL_COLLECTION_VIEW = hydra(
		"PartialCollectionView");

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#SupportedProperty">
	 * SupportedProperty</a>.
	 *
	 * @review
	 */
	public static final String TYPE_HYDRA_SUPPORTED_PROPERTY = hydra(
		"SupportedProperty");

	/**
	 * Hydra <a href="https://www.w3.org/ns/hydra/core#Operation">Operation</a>.
	 *
	 * @review
	 */
	public static final String TYPE_OPERATION = "Operation";

	/**
	 * Hydra profile URL.
	 *
	 * @review
	 */
	public static final String URL_HYDRA_PROFILE =
		"https://www.w3.org/ns/hydra/core#";

	/**
	 * schema.org URL.
	 *
	 * @review
	 */
	public static final String URL_SCHEMA_ORG = "http://schema.org/";

	/**
	 * Prefixes a type with the Hydra qualifier
	 *
	 * @param  type the type to prefix
	 * @return the type prefixed with the Hydra qualifier
	 * @review
	 */
	public static String hydra(String type) {
		return FIELD_NAME_HYDRA + ":" + type;
	}

}