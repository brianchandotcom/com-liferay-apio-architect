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

package com.liferay.apio.architect.internal.endpoint;

import static com.liferay.apio.architect.internal.endpoint.ExceptionSupplierUtil.notAllowed;
import static com.liferay.apio.architect.operation.HTTPMethod.POST;
import static com.liferay.apio.architect.operation.HTTPMethod.PUT;

import static javax.ws.rs.core.Response.noContent;

import com.liferay.apio.architect.alias.IdentifierFunction;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.function.throwable.ThrowableTriFunction;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.internal.annotation.ActionKey;
import com.liferay.apio.architect.internal.annotation.ActionManager;
import com.liferay.apio.architect.internal.pagination.PageImpl;
import com.liferay.apio.architect.internal.wiring.osgi.manager.provider.ProviderManager;
import com.liferay.apio.architect.operation.HTTPMethod;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.single.model.SingleModel;
import com.liferay.apio.architect.supplier.ThrowableSupplier;
import com.liferay.apio.architect.uri.Path;

import io.vavr.control.Either;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Response;

/**
 * @author Alejandro Hernández
 */
public class PageEndpointImpl<T, S> implements PageEndpoint<T> {

	public PageEndpointImpl(
		String name, HttpServletRequest httpServletRequest,
		Function<String, Try<SingleModel<T>>> singleModelFunction,
		ThrowableSupplier<CollectionRoutes<T, S>> collectionRoutesSupplier,
		ThrowableSupplier<Representor<T>> representorSupplier,
		ThrowableSupplier<ItemRoutes<T, S>> itemRoutesSupplier,
		ThrowableTriFunction
			<String, String, String, NestedCollectionRoutes<T, S, Object>>
				nestedCollectionRoutesFunction,
		IdentifierFunction<S> pathToIdentifierFunction,
		Supplier<ActionManager> actionManagerSupplier,
		ProviderManager providerManager) {

		_name = name;
		_httpServletRequest = httpServletRequest;
		_singleModelFunction = singleModelFunction;
		_collectionRoutesSupplier = collectionRoutesSupplier;
		_representorSupplier = representorSupplier;
		_itemRoutesSupplier = itemRoutesSupplier;
		_nestedCollectionRoutesFunction = nestedCollectionRoutesFunction;
		_pathToIdentifierFunction = pathToIdentifierFunction;

		_actionManagerSupplier = actionManagerSupplier;
		_providerManager = providerManager;
	}

	@Override
	public Try<SingleModel<T>> addCollectionItem(Body body) {
		return Try.fromFallible(
			_collectionRoutesSupplier
		).mapOptional(
			CollectionRoutes::getCreateItemFunctionOptional,
			notAllowed(POST, _name)
		).map(
			requestFunction -> requestFunction.apply(_httpServletRequest)
		).flatMap(
			bodyFunction -> bodyFunction.apply(body)
		);
	}

	@Override
	public Try<SingleModel<T>> addNestedCollectionItem(
		String id, String nestedName, Body body) {

		return Try.fromFallible(
			() -> _nestedCollectionRoutesFunction.apply(_name, nestedName, id)
		).mapOptional(
			NestedCollectionRoutes::getNestedCreateItemFunctionOptional
		).map(
			requestFunction -> requestFunction.apply(_httpServletRequest)
		).flatMap(
			identifierFunction -> _getFunctionTry(
				id, nestedName, identifierFunction)
		).flatMap(
			bodyFunction -> bodyFunction.apply(body)
		).mapFailMatching(
			NoSuchElementException.class,
			notAllowed(POST, _name, id, nestedName)
		);
	}

	@Override
	public Response deleteCollectionItem(String id) {
		ActionManager actionManager = _actionManagerSupplier.get();

		Either<Action.Error, Action> actionEither = actionManager.getAction(
			HTTPMethod.DELETE.name(), _name, id);

		actionEither.forEach(action -> action.apply(_httpServletRequest));

		return noContent().build();
	}

	@Override
	public Try<SingleModel<T>> getCollectionItemSingleModelTry(String id) {
		return _singleModelFunction.apply(id);
	}

	@Override
	public Try<Page<T>> getCollectionPageTry() {
		ActionManager actionManager = _actionManagerSupplier.get();

		Either<Action.Error, Action> actionEither = actionManager.getAction(
			HTTPMethod.GET.name(), _name);

		return _getPageTry(actionEither, _name);
	}

	@Override
	public Try<Page<T>> getNestedCollectionPageTry(
		String id, String nestedName) {

		ActionManager actionManager = _actionManagerSupplier.get();

		Either<Action.Error, Action> actionEither = actionManager.getAction(
			HTTPMethod.GET.name(), _name, id, nestedName);

		return _getPageTry(actionEither, nestedName);
	}

	@Override
	public Try<SingleModel<T>> updateCollectionItem(String id, Body body) {
		return Try.fromFallible(
			_itemRoutesSupplier
		).mapOptional(
			ItemRoutes::getUpdateItemFunctionOptional,
			notAllowed(PUT, _name, id)
		).map(
			requestFunction -> requestFunction.apply(_httpServletRequest)
		).map(
			identifierFunction -> identifierFunction.compose(
				_pathToIdentifierFunction)
		).map(
			pathFunction -> pathFunction.apply(new Path(_name, id))
		).flatMap(
			bodyFunction -> bodyFunction.apply(body)
		);
	}

	private Try<Function<Body, Try<SingleModel<T>>>> _getFunctionTry(
		String id, String nestedName,
		Function<Object, Function<Body, Try<SingleModel<T>>>>
			identifierFunction) {

		if (_name.equals("r")) {
			return Try.fromFallible(
				() -> identifierFunction.apply(
					_pathToIdentifierFunction.apply(new Path(id, nestedName))));
		}
		else {
			return _singleModelFunction.apply(
				id
			).map(
				this::_getIdentifierFunction
			).map(
				identifierFunction::apply
			);
		}
	}

	private Object _getIdentifierFunction(SingleModel<T> singleModel)
		throws Exception {

		Representor<T> representor = _representorSupplier.get();

		return representor.getIdentifier(singleModel.getModel());
	}

	private Try<Page<T>> _getPageTry(
		Either<Action.Error, Action> actionEither, String name) {

		return Try.fromFallible(
			() -> actionEither.map(
				action -> action.apply(_httpServletRequest)
			).map(
				pageItems -> {
					ActionManager actionManager = _actionManagerSupplier.get();

					return new PageImpl(
						name, (PageItems<T>)pageItems, _getPagination(),
						actionManager.getActions(
							new ActionKey(HTTPMethod.GET.name(), name), null));
				}
			).getOrElseThrow(
				notAllowed(HTTPMethod.GET, _name)
			)
		);
	}

	private Pagination _getPagination() {
		return _providerManager.provideMandatory(
			_httpServletRequest, Pagination.class);
	}

	private final Supplier<ActionManager> _actionManagerSupplier;
	private final ThrowableSupplier<CollectionRoutes<T, S>>
		_collectionRoutesSupplier;
	private final HttpServletRequest _httpServletRequest;
	private final ThrowableSupplier<ItemRoutes<T, S>> _itemRoutesSupplier;
	private final String _name;
	private final ThrowableTriFunction
		<String, String, String, NestedCollectionRoutes<T, S, Object>>
			_nestedCollectionRoutesFunction;
	private final IdentifierFunction<S> _pathToIdentifierFunction;
	private final ProviderManager _providerManager;
	private final ThrowableSupplier<Representor<T>> _representorSupplier;
	private final Function<String, Try<SingleModel<T>>> _singleModelFunction;

}