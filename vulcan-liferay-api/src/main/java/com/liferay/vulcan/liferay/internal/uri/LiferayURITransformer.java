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

package com.liferay.vulcan.liferay.internal.uri;

import com.liferay.vulcan.uri.CollectionResourceURITransformer;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class LiferayURITransformer implements CollectionResourceURITransformer {

	@Override
	public <T> String transformCollectionItemSingleResourceURI(
		String uri, Class<T> modelClass, T model) {

		return uri;
	}

	@Override
	public <T> String transformPageURI(String uri, Class<T> modelClass) {
		return uri;
	}

}