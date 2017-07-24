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

package com.liferay.vulcan.converter;

/**
 * Instances of this interface will be used to convert string identifiers
 * provided in requests to the final endpoint method types.
 *
 * The class of the converter can then be provided as a parameter in
 * <code>collectionItem</code> {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder} methods.
 *
 * @author Alejandro Hernández
 */
public interface Converter<T> {

	/**
	 * Converts an identifier from its String representation to the custom one.
	 *
	 * @param  id model identifier in string format.
	 * @return the model identifier in the custom format.
	 */
	public T convert(String id);

}