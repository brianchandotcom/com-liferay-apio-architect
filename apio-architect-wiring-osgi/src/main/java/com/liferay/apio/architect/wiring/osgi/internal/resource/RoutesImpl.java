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

package com.liferay.apio.architect.wiring.osgi.internal.resource;

import com.liferay.apio.architect.alias.routes.CreateItemFunction;
import com.liferay.apio.architect.alias.routes.DeleteItemConsumer;
import com.liferay.apio.architect.alias.routes.GetItemFunction;
import com.liferay.apio.architect.alias.routes.GetPageFunction;
import com.liferay.apio.architect.alias.routes.UpdateItemFunction;
import com.liferay.apio.architect.resource.Routes;

import java.util.Optional;

/**
 * @author Alejandro Hernández
 */
public class RoutesImpl<T> implements Routes<T> {

	@Override
	public Optional<CreateItemFunction<T>> getCreateItemFunctionOptional() {
		return Optional.ofNullable(_createItemFunction);
	}

	@Override
	public Optional<DeleteItemConsumer> getDeleteConsumerOptional() {
		return Optional.ofNullable(_deleteItemConsumer);
	}

	@Override
	public Optional<GetPageFunction<T>> getGetPageFunctionOptional() {
		return Optional.ofNullable(_getPageFunction);
	}

	@Override
	public Optional<GetItemFunction<T>> getItemFunctionOptional() {
		return Optional.ofNullable(_singleModelFunction);
	}

	@Override
	public Optional<UpdateItemFunction<T>> getUpdateItemFunctionOptional() {
		return Optional.ofNullable(_updateItemFunction);
	}

	public void setCreateItemFunction(
		CreateItemFunction<T> createItemFunction) {

		_createItemFunction = createItemFunction;
	}

	public void setDeleteItemConsumer(DeleteItemConsumer deleteItemConsumer) {
		_deleteItemConsumer = deleteItemConsumer;
	}

	public void setGetPageFunction(GetPageFunction<T> getPageFunction) {
		_getPageFunction = getPageFunction;
	}

	public void setSingleModelFunction(GetItemFunction<T> singleModelFunction) {
		_singleModelFunction = singleModelFunction;
	}

	public void setUpdateItemFunction(
		UpdateItemFunction<T> updateItemFunction) {

		_updateItemFunction = updateItemFunction;
	}

	private CreateItemFunction<T> _createItemFunction;
	private DeleteItemConsumer _deleteItemConsumer;
	private GetPageFunction<T> _getPageFunction;
	private GetItemFunction<T> _singleModelFunction;
	private UpdateItemFunction<T> _updateItemFunction;

}