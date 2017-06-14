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

package com.liferay.vulcan.liferay.internal;

import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.representor.Routes;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Alejandro Hernández
 */
public class RoutesImpl<T> implements Routes<T> {

	public RoutesImpl(
		Supplier<PageItems<T>> pageItemsSupplier,
		Function<String, T> modelFunction) {

		_pageItemsSupplier = pageItemsSupplier;
		_modelFunction = modelFunction;
	}

	@Override
	public Function<String, T> getModelFunction() {
		return _modelFunction;
	}

	@Override
	public Supplier<PageItems<T>> getPageItemsSupplier() {
		return _pageItemsSupplier;
	}

	private final Function<String, T> _modelFunction;
	private final Supplier<PageItems<T>> _pageItemsSupplier;

}