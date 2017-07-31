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

package com.liferay.vulcan.wiring.osgi.internal.resource;

import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.resource.Routes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Alejandro Hernández
 */
public class RoutesImpl<T> implements Routes<T> {

	public void addFilteredPageItemsFunction(
		String filterClassName, Function<Function<Class<?>, Optional<?>>,
			PageItems<T>> filteredPageItemFunction) {

		_filteredPageItemsFunctions.put(
			filterClassName, filteredPageItemFunction);
	}

	@Override
	public Optional<Function<Function<Class<?>, Optional<?>>, PageItems<T>>>
		getFilteredPageItemsFunctionOptional(String filterClassName) {

		return Optional.ofNullable(
			_filteredPageItemsFunctions.get(filterClassName));
	}

	@Override
	public Optional<Function<BiFunction<Class<?>, String, ?>,
		Function<Function<Class<?>, Optional<?>>, Function<String, T>>>>
			getModelFunctionOptional() {

		return Optional.ofNullable(_modelFunction);
	}

	@Override
	public Optional<Function<Function<Class<?>, Optional<?>>, PageItems<T>>>
		getPageItemsFunctionOptional() {

		return Optional.ofNullable(_pageItemsFunction);
	}

	public void setModelFunction(
		Function<BiFunction<Class<?>, String, ?>, Function<Function<Class<?>,
			Optional<?>>, Function<String, T>>> modelFunction) {

		_modelFunction = modelFunction;
	}

	public void setPageItemsFunction(
		Function<Function<Class<?>, Optional<?>>, PageItems<T>>
			pageItemsFunction) {

		_pageItemsFunction = pageItemsFunction;
	}

	private final Map<String, Function<Function<Class<?>, Optional<?>>,
		PageItems<T>>> _filteredPageItemsFunctions = new HashMap<>();
	private Function<BiFunction<Class<?>, String, ?>, Function
		<Function<Class<?>, Optional<?>>, Function<String, T>>> _modelFunction;
	private Function<Function<Class<?>, Optional<?>>, PageItems<T>>
		_pageItemsFunction;

}