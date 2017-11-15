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

import java.util.List;
import java.util.Map;

/**
 * Instances of this interface represent a mock model that can be written using
 * a {@link com.liferay.vulcan.message.json.SingleModelMessageMapper}.
 *
 * @author Alejandro Hernández
 * @review
 */
public interface Model {

	/**
	 * Returns the binary resources linked to a model.
	 *
	 * @return the binary resources linked to a model
	 */
	public <T extends Model> Map<String, BinaryFunction<T>>
		getBinaryFunctions();

	/**
	 * Returns the map containing the boolean field names and values
	 *
	 * @return the map containing the boolean field names and values
	 */
	public Map<String, Boolean> getBooleanFields();

	/**
	 * Returns the embedded related models.
	 *
	 * @return the embedded related models
	 */
	public <T extends Model> List<RelatedModel<T, ?>>
		getEmbeddedRelatedModels();

	/**
	 * Returns the ID.
	 *
	 * @return the ID
	 */
	public String getId();

	/**
	 * Returns the linked related models.
	 *
	 * @return the linked related models
	 */
	public <T extends Model> List<RelatedModel<T, ?>> getLinkedRelatedModels();

	/**
	 * Returns the map containing the link field names and values
	 *
	 * @return the map containing the link field names and values
	 */
	public Map<String, String> getLinks();

	/**
	 * Returns the map containing the localized string field names and values
	 *
	 * @return the map containing the localized string field names and values
	 */
	public Map<String, String> getLocalizedStringFunctions();

	/**
	 * Returns the map containing the number field names and values
	 *
	 * @return the map containing the number field names and values
	 */
	public Map<String, Number> getNumberFields();

	/**
	 * Returns the related collections.
	 *
	 * @return the related collections
	 */
	public Map<String, ? extends Model> getRelatedCollections();

	/**
	 * Returns the map containing the string field names and values
	 *
	 * @return the map containing the string field names and values
	 */
	public Map<String, String> getStringFields();

	/**
	 * Returns the types.
	 *
	 * @return the types
	 */
	public List<String> getTypes();

	/**
	 * Returns the url.
	 *
	 * @return the url
	 */
	public String getURL();

}