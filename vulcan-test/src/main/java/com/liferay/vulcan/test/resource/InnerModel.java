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

package com.liferay.vulcan.test.resource;

import com.liferay.vulcan.alias.BinaryFunction;
import com.liferay.vulcan.resource.RelatedModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Instances of this class represent a mock inner model that can be written
 * using a {@link com.liferay.vulcan.message.json.SingleModelMessageMapper}.
 *
 * @author Alejandro Hernández
 * @review
 */
public class InnerModel implements Model {

	public InnerModel(String id) {
		_id = id;
	}

	@Override
	public <T extends Model>
		Map<String, BinaryFunction<T>> getBinaryFunctions() {

		return Collections.emptyMap();
	}

	@Override
	public Map<String, Boolean> getBooleanFields() {
		return Collections.singletonMap("boolean", true);
	}

	@Override
	public <T extends Model>
		List<RelatedModel<T, ?>> getEmbeddedRelatedModels() {

		return Collections.emptyList();
	}

	@Override
	public String getId() {
		return _id;
	}

	@Override
	public <T extends Model> List<RelatedModel<T, ?>> getLinkedRelatedModels() {
		return Collections.emptyList();
	}

	@Override
	public Map<String, String> getLinks() {
		return Collections.singletonMap("link", "www.liferay.com");
	}

	@Override
	public Map<String, String> getLocalizedStringFunctions() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Number> getNumberFields() {
		return Collections.singletonMap("number", 42);
	}

	@Override
	public Map<String, ? extends Model> getRelatedCollections() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, String> getStringFields() {
		return Collections.singletonMap("string", "A string");
	}

	@Override
	public List<String> getTypes() {
		return Collections.singletonList("Type");
	}

	@Override
	public String getURL() {
		return "localhost:8080/inner";
	}

	private final String _id;

}