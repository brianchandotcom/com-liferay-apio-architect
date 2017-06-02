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

import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_COLLECTION;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_ELEMENTS;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_FIRST;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_LAST;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.MEDIA_TYPE;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_NEXT;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_PAGES;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_PAGE_COUNT;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_PREV;
import static com.liferay.vulcan.message.json.plain.internal.JSONPlainConstants.FIELD_NAME_TOTAL_COUNT;

import com.liferay.vulcan.list.FunctionalList;
import com.liferay.vulcan.message.PageJSONMessageMapper;
import com.liferay.vulcan.message.RequestInfo;
import com.liferay.vulcan.message.json.JSONObjectBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class PagePlainJSONMessageMapper<T> implements PageJSONMessageMapper<T> {

	@Override
	public String getMediaType() {
		return MEDIA_TYPE;
	}

	@Override
	public void mapCollectionURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.field(FIELD_NAME_COLLECTION).value(url);
	}

	@Override
	public void mapCurrentPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		_singleModelPlainJSONMessageMapper.mapSelfURL(jsonObjectBuilder, url);
	}

	@Override
	public void mapFirstPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			FIELD_NAME_PAGES, FIELD_NAME_FIRST
		).value(
			url
		);
	}

	@Override
	public void mapItemEmbeddedResourceField(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, String fieldName,
		Object value) {

		_singleModelPlainJSONMessageMapper.mapEmbeddedResourceField(
			itemJSONObjectBuilder, embeddedPathElements, fieldName, value);
	}

	@Override
	public void mapItemEmbeddedResourceLink(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, String fieldName,
		String url) {

		_singleModelPlainJSONMessageMapper.mapEmbeddedResourceLink(
			itemJSONObjectBuilder, embeddedPathElements, fieldName, url);
	}

	@Override
	public void mapItemEmbeddedResourceURL(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		_singleModelPlainJSONMessageMapper.mapEmbeddedResourceURL(
			itemJSONObjectBuilder, embeddedPathElements, url);
	}

	@Override
	public void mapItemField(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, String fieldName,
		Object value) {

		_singleModelPlainJSONMessageMapper.mapField(
			itemJSONObjectBuilder, fieldName, value);
	}

	@Override
	public void mapItemLink(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, String fieldName, String url) {

		_singleModelPlainJSONMessageMapper.mapLink(
			itemJSONObjectBuilder, fieldName, url);
	}

	@Override
	public void mapItemLinkedResourceURL(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder,
		FunctionalList<String> embeddedPathElements, String url) {

		_singleModelPlainJSONMessageMapper.mapLinkedResourceURL(
			itemJSONObjectBuilder, embeddedPathElements, url);
	}

	@Override
	public void mapItemSelfURL(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, String url) {

		_singleModelPlainJSONMessageMapper.mapSelfURL(
			itemJSONObjectBuilder, url);
	}

	@Override
	public void mapItemTotalCount(
		JSONObjectBuilder jsonObjectBuilder, int totalCount) {

		jsonObjectBuilder.field(FIELD_NAME_TOTAL_COUNT).value(totalCount);
	}

	@Override
	public void mapLastPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			FIELD_NAME_PAGES, FIELD_NAME_LAST
		).value(
			url
		);
	}

	@Override
	public void mapNextPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			FIELD_NAME_PAGES, FIELD_NAME_NEXT
		).value(
			url
		);
	}

	@Override
	public void mapPageCount(JSONObjectBuilder jsonObjectBuilder, int count) {
		jsonObjectBuilder.field(FIELD_NAME_PAGE_COUNT).value(count);
	}

	@Override
	public void mapPreviousPageURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {

		jsonObjectBuilder.nestedField(
			FIELD_NAME_PAGES, FIELD_NAME_PREV
		).value(
			url
		);
	}

	@Override
	public void onFinishItem(
		JSONObjectBuilder pageJSONObjectBuilder,
		JSONObjectBuilder itemJSONObjectBuilder, T item, Class<T> modelClass,
		RequestInfo requestInfo) {

		pageJSONObjectBuilder.field(
			FIELD_NAME_ELEMENTS
		).arrayValue().add(
			itemJSONObjectBuilder
		);
	}

	@Reference
	private SingleModelPlainJSONMessageMapper
		_singleModelPlainJSONMessageMapper;

}