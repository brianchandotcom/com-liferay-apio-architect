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

package com.liferay.vulcan.message;

import aQute.bnd.annotation.ConsumerType;

import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.json.JSONMessageBuilder;

import java.util.List;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@ConsumerType
public interface SingleModelJSONMessageMapper<T> {

	public String getMediaType();

	public default void mapEmbeddedResourceField(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, String field,
		Object value) {
	}

	public default void mapEmbeddedResourceLink(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, String field, String url) {
	}

	public default void mapEmbeddedResourceTypes(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, List<String> types) {
	}

	public default void mapEmbeddedResourceURL(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, String url) {
	}

	public default void mapField(
		JSONMessageBuilder jsonMessageBuilder, String fieldName, Object value) {
	}

	public default void mapLink(
		JSONMessageBuilder jsonMessageBuilder, String fieldName, String url) {
	}

	public default void mapLinkedResourceURL(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, String url) {
	}

	public default void mapSelfURL(
		JSONMessageBuilder jsonMessageBuilder, String url) {
	}

	public default void mapTypes(
		JSONMessageBuilder jsonMessageBuilder, List<String> types) {
	}

	public default void onFinish(
		JSONMessageBuilder jsonMessageBuilder, T model, Class<T> modelClass,
		RequestInfo requestInfo) {
	}

	public default void onStart(
		JSONMessageBuilder jsonMessageBuilder, T model, Class<T> modelClass,
		RequestInfo requestInfo) {
	}

	public default boolean supports(
		T model, Class<T> modelClass, RequestInfo requestInfo) {

		return true;
	}

}