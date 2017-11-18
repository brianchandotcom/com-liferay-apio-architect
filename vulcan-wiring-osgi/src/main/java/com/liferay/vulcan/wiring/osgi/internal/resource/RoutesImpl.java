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

import com.liferay.vulcan.alias.RequestFunction;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.pagination.SingleModel;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.identifier.Identifier;
import com.liferay.vulcan.uri.Path;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Alejandro Hernández
 */
public class RoutesImpl<T> implements Routes<T> {

	@Override
	public Optional<RequestFunction<Consumer<Path>>>
		getDeleteSingleModelConsumerOptional() {

		return Optional.ofNullable(_deleteSingleModelConsumer);
	}

	@Override
	public Optional<RequestFunction<Function<Path,
		Function<Identifier, Page<T>>>>> getPageFunctionOptional() {

		return Optional.ofNullable(_pageFunction);
	}

	@Override
	public Optional<RequestFunction<Function<Identifier,
		Function<Map<String, Object>, SingleModel<T>>>>>
			getPostSingleModelFunctionOptional() {

		return Optional.ofNullable(_postSingleModelFunction);
	}

	@Override
	public Optional<RequestFunction<Function<Path,
		SingleModel<T>>>> getSingleModelFunctionOptional() {

		return Optional.ofNullable(_singleModelFunction);
	}

	@Override
	public Optional<RequestFunction<Function<Path, Function<
		Map<String, Object>, SingleModel<T>>>>>
			getUpdateSingleModelFunctionOptional() {

		return Optional.ofNullable(_putSingleModelFunction);
	}

	public void setDeleteSingleModelConsumer(
		RequestFunction<Consumer<Path>> deleteSingleModelConsumer) {

		_deleteSingleModelConsumer = deleteSingleModelConsumer;
	}

	public void setPageFunction(
		RequestFunction<Function<Path, Function<Identifier,
			Page<T>>>> pageFunction) {

		_pageFunction = pageFunction;
	}

	public void setPostSingleModelFunction(
		RequestFunction<Function<Identifier, Function
			<Map<String, Object>, SingleModel<T>>>> postSingleModelFunction) {

		_postSingleModelFunction = postSingleModelFunction;
	}

	public void setPutSingleModelFunction(
		RequestFunction<Function<Path, Function
			<Map<String, Object>, SingleModel<T>>>> putSingleModelFunction) {

		_putSingleModelFunction = putSingleModelFunction;
	}

	public void setSingleModelFunction(
		RequestFunction<Function<Path, SingleModel<T>>> singleModelFunction) {

		_singleModelFunction = singleModelFunction;
	}

	private RequestFunction<Consumer<Path>> _deleteSingleModelConsumer;
	private RequestFunction<Function<Path,
		Function<Identifier, Page<T>>>> _pageFunction;
	private RequestFunction<Function<Identifier, Function
		<Map<String, Object>, SingleModel<T>>>> _postSingleModelFunction;
	private RequestFunction<Function<Path,
		Function<Map<String, Object>, SingleModel<T>>>> _putSingleModelFunction;
	private RequestFunction<Function<Path, SingleModel<T>>>
		_singleModelFunction;

}