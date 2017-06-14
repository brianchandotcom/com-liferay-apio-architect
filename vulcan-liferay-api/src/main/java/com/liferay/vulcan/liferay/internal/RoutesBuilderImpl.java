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

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.representor.Routes;
import com.liferay.vulcan.representor.RoutesBuilder;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.BadRequestException;

/**
 * @author Alejandro Hernández
 */
public class RoutesBuilderImpl<T> implements RoutesBuilder<T> {

	public RoutesBuilderImpl(Pagination pagination) {
		_pagination = pagination;
	}

	@Override
	public SingleStep<T> collectionPage(
		Function<Pagination, Page<T>> function) {

		_pageSupplier = () -> function.apply(_pagination);

		return new SingleStepImpl();
	}

	private Supplier<Page<T>> _pageSupplier;
	private final Pagination _pagination;

	private class SingleStepImpl implements SingleStep<T> {

		@Override
		public <U> Routes<T> collectionItem(
			Function<U, T> function, Class<U> identifierClass) {

			if (identifierClass.isAssignableFrom(Long.class)) {
				_modelFunction = id -> {
					Long longId = GetterUtil.getLong(id);

					if (longId == GetterUtil.DEFAULT_LONG) {
						throw new BadRequestException();
					}

					return function.apply((U)longId);
				};
			}
			else if (identifierClass.isAssignableFrom(Integer.class)) {
				_modelFunction = id -> {
					Integer integerId = GetterUtil.getInteger(id);

					if (integerId == GetterUtil.DEFAULT_INTEGER) {
						throw new BadRequestException();
					}

					return function.apply((U)integerId);
				};
			}
			else if (identifierClass.isAssignableFrom(String.class)) {
				_modelFunction = id -> function.apply((U)id);
			}
			else {
				throw new RuntimeException();
			}

			return new RoutesImpl<>(_pageSupplier, _modelFunction);
		}

		private Function<String, T> _modelFunction;

	}

}