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

package com.liferay.vulcan.message.json.plain.internal;

import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.SingleModelJSONMessageMapper;
import com.liferay.vulcan.message.json.JSONMessageBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
public class SingleModelPlainJSONMessageMapper<T>
	implements SingleModelJSONMessageMapper<T> {

	@Override
	public String getMediaType() {
		return "application/json";
	}

	@Override
	public void mapEmbeddedResourceField(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, String fieldName,
		Object value) {

		jsonMessageBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).value(
			value
		);
	}

	@Override
	public void mapEmbeddedResourceLink(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, String fieldName,
		String url) {

		jsonMessageBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).field(
			fieldName
		).value(
			url
		);
	}

	@Override
	public void mapEmbeddedResourceURL(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		jsonMessageBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).field(
			"self"
		).value(
			url
		);
	}

	@Override
	public void mapField(
		JSONMessageBuilder jsonMessageBuilder, String fieldName, Object value) {

		jsonMessageBuilder.field(fieldName).value(value);
	}

	@Override
	public void mapLink(
		JSONMessageBuilder jsonMessageBuilder, String fieldName, String url) {

		jsonMessageBuilder.field(fieldName).value(url);
	}

	@Override
	public void mapLinkedResourceURL(
		JSONMessageBuilder jsonMessageBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		jsonMessageBuilder.nestedField(
			embeddedPathElements.head(),
			embeddedPathElements.tail().toArray(String[]::new)
		).value(
			url
		);
	}

	@Override
	public void mapSelfURL(JSONMessageBuilder jsonMessageBuilder, String url) {
		jsonMessageBuilder.field("self").value(url);
	}

}