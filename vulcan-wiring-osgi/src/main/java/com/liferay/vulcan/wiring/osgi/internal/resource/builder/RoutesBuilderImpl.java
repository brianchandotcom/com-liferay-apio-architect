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

package com.liferay.vulcan.wiring.osgi.internal.resource.builder;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.error.VulcanDeveloperError.MustHaveConverter;
import com.liferay.vulcan.error.VulcanDeveloperError.MustHaveProvider;
import com.liferay.vulcan.function.DecaFunction;
import com.liferay.vulcan.function.EnneaFunction;
import com.liferay.vulcan.function.HeptaFunction;
import com.liferay.vulcan.function.HexaFunction;
import com.liferay.vulcan.function.OctaFunction;
import com.liferay.vulcan.function.PentaFunction;
import com.liferay.vulcan.function.TetraFunction;
import com.liferay.vulcan.function.TriFunction;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.wiring.osgi.internal.resource.RoutesImpl;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.ws.rs.BadRequestException;

/**
 * @author Alejandro Hernández
 */
public class RoutesBuilderImpl<T> implements RoutesBuilder<T> {

	@Override
	public Routes<T> build() {
		return _routesImpl;
	}

	@Override
	public <U, A> RoutesBuilder<T> collectionItem(
		BiFunction<U, A, T> biFunction, Class<U> identifierClass,
		Class<A> aClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);

					return biFunction.apply(id, a);
				}
			)
		);

		return this;
	}

	@Override
	public <U, A, B, C, D, E, F, G, H, I> RoutesBuilder<T> collectionItem(
		DecaFunction<U, A, B, C, D, E, F, G, H, I, T> decaFunction,
		Class<U> identifierClass, Class<A> aClass, Class<B> bClass,
		Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
		Class<G> gClass, Class<H> hClass, Class<I> iClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);
					B b = _provide(bClass, provideFunction);
					C c = _provide(cClass, provideFunction);
					D d = _provide(dClass, provideFunction);
					E e = _provide(eClass, provideFunction);
					F f = _provide(fClass, provideFunction);
					G g = _provide(gClass, provideFunction);
					H h = _provide(hClass, provideFunction);
					I i = _provide(iClass, provideFunction);

					return decaFunction.apply(id, a, b, c, d, e, f, g, h, i);
				}
			));

		return this;
	}

	@Override
	public <U, A, B, C, D, E, F, G, H> RoutesBuilder<T> collectionItem(
		EnneaFunction<U, A, B, C, D, E, F, G, H, T> enneaFunction,
		Class<U> identifierClass, Class<A> aClass, Class<B> bClass,
		Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
		Class<G> gClass, Class<H> hClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);
					B b = _provide(bClass, provideFunction);
					C c = _provide(cClass, provideFunction);
					D d = _provide(dClass, provideFunction);
					E e = _provide(eClass, provideFunction);
					F f = _provide(fClass, provideFunction);
					G g = _provide(gClass, provideFunction);
					H h = _provide(hClass, provideFunction);

					return enneaFunction.apply(id, a, b, c, d, e, f, g, h);
				}
			));

		return this;
	}

	@Override
	public <U> RoutesBuilder<T> collectionItem(
		Function<U, T> function, Class<U> identifierClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				function
			));

		return this;
	}

	@Override
	public <U, A, B, C, D, E, F> RoutesBuilder<T> collectionItem(
		HeptaFunction<U, A, B, C, D, E, F, T> heptaFunction,
		Class<U> identifierClass, Class<A> aClass, Class<B> bClass,
		Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);
					B b = _provide(bClass, provideFunction);
					C c = _provide(cClass, provideFunction);
					D d = _provide(dClass, provideFunction);
					E e = _provide(eClass, provideFunction);
					F f = _provide(fClass, provideFunction);

					return heptaFunction.apply(id, a, b, c, d, e, f);
				}
			));

		return this;
	}

	@Override
	public <U, A, B, C, D, E> RoutesBuilder<T> collectionItem(
		HexaFunction<U, A, B, C, D, E, T> hexaFunction,
		Class<U> identifierClass, Class<A> aClass, Class<B> bClass,
		Class<C> cClass, Class<D> dClass, Class<E> eClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);
					B b = _provide(bClass, provideFunction);
					C c = _provide(cClass, provideFunction);
					D d = _provide(dClass, provideFunction);
					E e = _provide(eClass, provideFunction);

					return hexaFunction.apply(id, a, b, c, d, e);
				}
			));

		return this;
	}

	@Override
	public <U, A, B, C, D, E, F, G> RoutesBuilder<T> collectionItem(
		OctaFunction<U, A, B, C, D, E, F, G, T> octaFunction,
		Class<U> identifierClass, Class<A> aClass, Class<B> bClass,
		Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
		Class<G> gClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);
					B b = _provide(bClass, provideFunction);
					C c = _provide(cClass, provideFunction);
					D d = _provide(dClass, provideFunction);
					E e = _provide(eClass, provideFunction);
					F f = _provide(fClass, provideFunction);
					G g = _provide(gClass, provideFunction);

					return octaFunction.apply(id, a, b, c, d, e, f, g);
				}
			));

		return this;
	}

	@Override
	public <U, A, B, C, D> RoutesBuilder<T> collectionItem(
		PentaFunction<U, A, B, C, D, T> pentaFunction, Class<U> identifierClass,
		Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);
					B b = _provide(bClass, provideFunction);
					C c = _provide(cClass, provideFunction);
					D d = _provide(dClass, provideFunction);

					return pentaFunction.apply(id, a, b, c, d);
				}
			));

		return this;
	}

	@Override
	public <U, A, B, C> RoutesBuilder<T> collectionItem(
		TetraFunction<U, A, B, C, T> tetraFunction, Class<U> identifierClass,
		Class<A> aClass, Class<B> bClass, Class<C> cClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);
					B b = _provide(bClass, provideFunction);
					C c = _provide(cClass, provideFunction);

					return tetraFunction.apply(id, a, b, c);
				}
			));

		return this;
	}

	@Override
	public <U, A, B> RoutesBuilder<T> collectionItem(
		TriFunction<U, A, B, T> triFunction, Class<U> identifierClass,
		Class<A> aClass, Class<B> bClass) {

		_routesImpl.setModelFunction(
			convertFunction -> provideFunction -> _convertIdentifier(
				identifierClass, convertFunction
			).andThen(
				id -> {
					A a = _provide(aClass, provideFunction);
					B b = _provide(bClass, provideFunction);

					return triFunction.apply(id, a, b);
				}
			));

		return this;
	}

	@Override
	public <A> RoutesBuilder<T> collectionPage(
		BiFunction<Pagination, A, PageItems<T>> biFunction, Class<A> aClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);

				return biFunction.apply(pagination, a);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H, I> RoutesBuilder<T> collectionPage(
		DecaFunction<Pagination, A, B, C, D, E, F, G, H, I,
			PageItems<T>> decaFunction, Class<A> aClass, Class<B> bClass,
		Class<C> cClass, Class<D> dClass, Class<E> eClass, Class<F> fClass,
		Class<G> gClass, Class<H> hClass, Class<I> iClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);
				B b = _provide(bClass, provideFunction);
				C c = _provide(cClass, provideFunction);
				D d = _provide(dClass, provideFunction);
				E e = _provide(eClass, provideFunction);
				F f = _provide(fClass, provideFunction);
				G g = _provide(gClass, provideFunction);
				H h = _provide(hClass, provideFunction);
				I i = _provide(iClass, provideFunction);

				return decaFunction.apply(
					pagination, a, b, c, d, e, f, g, h, i);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G, H> RoutesBuilder<T> collectionPage(
		EnneaFunction<Pagination, A, B, C, D, E, F, G, H, PageItems<T>>
			enneaFunction, Class<A> aClass, Class<B> bClass, Class<C> cClass,
		Class<D> dClass, Class<E> eClass, Class<F> fClass, Class<G> gClass,
		Class<H> hClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);
				B b = _provide(bClass, provideFunction);
				C c = _provide(cClass, provideFunction);
				D d = _provide(dClass, provideFunction);
				E e = _provide(eClass, provideFunction);
				F f = _provide(fClass, provideFunction);
				G g = _provide(gClass, provideFunction);
				H h = _provide(hClass, provideFunction);

				return enneaFunction.apply(pagination, a, b, c, d, e, f, g, h);
			});

		return this;
	}

	@Override
	public RoutesBuilder<T> collectionPage(
		Function<Pagination, PageItems<T>> function) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);

				return function.apply(pagination);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F> RoutesBuilder<T> collectionPage(
		HeptaFunction<Pagination, A, B, C, D, E, F, PageItems<T>> heptaFunction,
		Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
		Class<E> eClass, Class<F> fClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);
				B b = _provide(bClass, provideFunction);
				C c = _provide(cClass, provideFunction);
				D d = _provide(dClass, provideFunction);
				E e = _provide(eClass, provideFunction);
				F f = _provide(fClass, provideFunction);

				return heptaFunction.apply(pagination, a, b, c, d, e, f);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E> RoutesBuilder<T> collectionPage(
		HexaFunction<Pagination, A, B, C, D, E, PageItems<T>> hexaFunction,
		Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass,
		Class<E> eClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);
				B b = _provide(bClass, provideFunction);
				C c = _provide(cClass, provideFunction);
				D d = _provide(dClass, provideFunction);
				E e = _provide(eClass, provideFunction);

				return hexaFunction.apply(pagination, a, b, c, d, e);
			});

		return this;
	}

	@Override
	public <A, B, C, D, E, F, G> RoutesBuilder<T> collectionPage(
		OctaFunction<Pagination, A, B, C, D, E, F, G, PageItems<T>>
			octaFunction, Class<A> aClass, Class<B> bClass, Class<C> cClass,
		Class<D> dClass, Class<E> eClass, Class<F> fClass, Class<G> gClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);
				B b = _provide(bClass, provideFunction);
				C c = _provide(cClass, provideFunction);
				D d = _provide(dClass, provideFunction);
				E e = _provide(eClass, provideFunction);
				F f = _provide(fClass, provideFunction);
				G g = _provide(gClass, provideFunction);

				return octaFunction.apply(pagination, a, b, c, d, e, f, g);
			});

		return this;
	}

	@Override
	public <A, B, C, D> RoutesBuilder<T> collectionPage(
		PentaFunction<Pagination, A, B, C, D, PageItems<T>> pentaFunction,
		Class<A> aClass, Class<B> bClass, Class<C> cClass, Class<D> dClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);
				B b = _provide(bClass, provideFunction);
				C c = _provide(cClass, provideFunction);
				D d = _provide(dClass, provideFunction);

				return pentaFunction.apply(pagination, a, b, c, d);
			});

		return this;
	}

	@Override
	public <A, B, C> RoutesBuilder<T> collectionPage(
		TetraFunction<Pagination, A, B, C, PageItems<T>> tetraFunction,
		Class<A> aClass, Class<B> bClass, Class<C> cClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);
				B b = _provide(bClass, provideFunction);
				C c = _provide(cClass, provideFunction);

				return tetraFunction.apply(pagination, a, b, c);
			});

		return this;
	}

	@Override
	public <A, B> RoutesBuilder<T> collectionPage(
		TriFunction<Pagination, A, B, PageItems<T>> triFunction,
		Class<A> aClass, Class<B> bClass) {

		_routesImpl.setPageItemsFunction(
			provideFunction -> {
				Pagination pagination = _provide(
					Pagination.class, provideFunction);
				A a = _provide(aClass, provideFunction);
				B b = _provide(bClass, provideFunction);

				return triFunction.apply(pagination, a, b);
			});

		return this;
	}

	private <U> Function<String, U> _convertIdentifier(
		Class<U> identifierClass,
		BiFunction<Class<?>, String, ?> convertFunction) {

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
			return id -> {
				Optional<U> optional = (Optional<U>)convertFunction.apply(
					identifierClass, id);

				return optional.orElseThrow(
					() -> new MustHaveConverter(identifierClass));
			};
		}
	}

	private <U> U _provide(
		Class<U> clazz, Function<Class<?>, Optional<?>> provideFunction) {

		Optional<U> optional = (Optional<U>)provideFunction.apply(clazz);

		return optional.orElseThrow(() -> new MustHaveProvider(clazz));
	}

	private final RoutesImpl<T> _routesImpl = new RoutesImpl<>();

}