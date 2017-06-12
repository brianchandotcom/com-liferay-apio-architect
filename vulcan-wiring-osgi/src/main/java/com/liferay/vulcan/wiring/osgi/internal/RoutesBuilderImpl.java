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

package com.liferay.vulcan.wiring.osgi.internal;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.error.VulcanDeveloperError.MustHaveProvider;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.representor.Routes;
import com.liferay.vulcan.representor.RoutesBuilder;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.ws.rs.BadRequestException;

/**
 * @author Alejandro Hernández
 */
public class RoutesBuilderImpl<T> implements RoutesBuilder<T> {

	@Override
	public <A> SingleStep<T> collectionPage(
		BiFunction<Pagination, A, PageItems<T>> biFunction, Class<A> aClass) {

		_pageItemsFunction = provideFunction -> {
			Pagination pagination = _provide(Pagination.class, provideFunction);
			A a = _provide(aClass, provideFunction);

			return biFunction.apply(pagination, a);
		};
		return new SingleStepImpl();
	}

	@Override
	public SingleStep<T> collectionPage(
		Function<Pagination, PageItems<T>> function) {

		_pageItemsFunction = provideFunction -> {
			Pagination pagination = _provide(Pagination.class, provideFunction);

			return function.apply(pagination);
		};
		return new SingleStepImpl();
	}

	private <U> U _provide(
		Class<U> clazz, Function<Class<?>, Optional<?>> provideFunction) {

		Optional<U> optional = (Optional<U>)provideFunction.apply(clazz);

		return optional.orElseThrow(
			() -> new MustHaveProvider(Pagination.class));
	}

	private Function<Function<Class<?>, Optional<?>>, PageItems<T>>
		_pageItemsFunction;

	private class SingleStepImpl implements SingleStep<T> {

		@Override
		public <U> Routes<T> collectionItem(
			Function<U, T> function, Class<U> identifierClass) {

			_modelFunction = provideFunction -> _convertIdentifier(
				identifierClass
			).andThen(
				function
			);

			return new RoutesImpl<>(_pageItemsFunction, _modelFunction);
		}

		private <U> Function<String, U> _convertIdentifier(
			Class<U> identifierClass) {

			if (identifierClass.isAssignableFrom(Long.class)) {
				return id -> {
					Long longId = GetterUtil.getLong(id);

					if (longId == GetterUtil.DEFAULT_LONG) {
						throw new BadRequestException();
					}

					return (U)longId;
				};
			}
			else if (identifierClass.isAssignableFrom(Integer.class)) {
				return id -> {
					Integer integerId = GetterUtil.getInteger(id);

					if (integerId == GetterUtil.DEFAULT_INTEGER) {
						throw new BadRequestException();
					}

					return (U)integerId;
				};
			}
			else if (identifierClass.isAssignableFrom(String.class)) {
				return id -> (U)id;
			}
			else {
				throw new RuntimeException();
			}
		}

		private Function<Function<Class<?>, Optional<?>>, Function<String, T>>
			_modelFunction;

	}

}