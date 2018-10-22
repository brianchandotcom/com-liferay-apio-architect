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

import static com.liferay.apio.architect.internal.endpoint.ExceptionSupplierUtil.notFound;
import static com.liferay.apio.architect.internal.operation.util.OperationUtil.toOperations;

import com.liferay.apio.architect.documentation.APIDescription;
import com.liferay.apio.architect.documentation.APITitle;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.internal.annotation.ActionKey;
import com.liferay.apio.architect.internal.annotation.ActionManager;
import com.liferay.apio.architect.internal.annotation.ActionRouterManager;
import com.liferay.apio.architect.internal.documentation.Documentation;
import com.liferay.apio.architect.internal.entrypoint.CustomOperationsEndpoint;
import com.liferay.apio.architect.internal.entrypoint.EntryPoint;
import com.liferay.apio.architect.internal.single.model.SingleModelImpl;
import com.liferay.apio.architect.internal.url.ApplicationURL;
import com.liferay.apio.architect.internal.wiring.osgi.manager.provider.ProviderManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.RepresentableManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.router.CollectionRouterManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.router.ItemRouterManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.router.NestedCollectionRouterManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.router.ReusableNestedCollectionRouterManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper.PathIdentifierMapperManager;
import com.liferay.apio.architect.operation.HTTPMethod;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.single.model.SingleModel;

import io.vavr.control.Either;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Context;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(service = RootEndpoint.class)
public class RootEndpointImpl implements RootEndpoint {

	@Activate
	public void activate() {
		_documentation = _getActionManager().getDocumentation(
			() -> _provide(APITitle.class),
			() -> _provide(APIDescription.class),
			() -> _provide(ApplicationURL.class));
	}

	@Override
	public BatchEndpoint batchEndpoint(String name) {
		return BatchEndpointBuilder.name(
			name
		).httpServletRequest(
			_httpServletRequest
		).singleModelFunction(
			id -> _getSingleModelTry(name, id)
		).representorSupplier(
			() -> _getRepresentorOrFail(name)
		).collectionRoutesSupplier(
			() -> _getCollectionRoutesOrFail(name)
		).nestedCollectionRoutesFunction(
			nestedName -> _getNestedCollectionRoutesOrFail(
				name, nestedName, null)
		).build();
	}

	@Override
	public BinaryEndpoint binaryEndpoint() {
		return new BinaryEndpoint(
			this::_getRepresentorOrFail, this::_getSingleModelTry);
	}

	@Override
	public CustomOperationsEndpoint customOperationsEndpoint(String name) {
		return new CustomOperationsEndpoint<>(
			name, _httpServletRequest,
			() -> _collectionRouterManager.getCollectionRoutesOptional(name),
			() -> _itemRouterManager.getItemRoutesOptional(name),
			_pathIdentifierMapperManager::mapToIdentifierOrFail);
	}

	@Override
	public Documentation documentation() {
		return _documentation;
	}

	@Override
	public FormEndpoint formEndpoint() {
		return new FormEndpoint(
			this::_getCollectionRoutesOrFail, this::_getItemRoutesOrFail,
			this::_getNestedCollectionRoutesOrFail);
	}

	@Override
	public EntryPoint home() {
		return _getActionManager().getEntryPoint();
	}

	@Override
	public PageEndpointImpl pageEndpoint(String name) {
		return new PageEndpointImpl<>(
			name, _httpServletRequest, id -> _getSingleModelTry(name, id),
			() -> _getCollectionRoutesOrFail(name),
			() -> _getRepresentorOrFail(name), () -> _getItemRoutesOrFail(name),
			this::_getNestedCollectionRoutesOrFail,
			_pathIdentifierMapperManager::mapToIdentifierOrFail,
			this::_getActionManager, _providerManager);
	}

	private ActionManager _getActionManager() {
		List<String> resourceNames = _actionRouterManager.getResourceNames();

		if (resourceNames.isEmpty()) {
			_actionRouterManager.initializeRouterManagers();
		}

		return _actionManager;
	}

	private CollectionRoutes<Object, Object> _getCollectionRoutesOrFail(
		String name) {

		Optional<CollectionRoutes<Object, Object>> optional =
			_collectionRouterManager.getCollectionRoutesOptional(name);

		return optional.orElseThrow(notFound(name));
	}

	private ItemRoutes<Object, Object> _getItemRoutesOrFail(String name) {
		Optional<ItemRoutes<Object, Object>> optional =
			_itemRouterManager.getItemRoutesOptional(name);

		return optional.orElseThrow(notFound(name));
	}

	private NestedCollectionRoutes<Object, Object, Object>
		_getNestedCollectionRoutesOrFail(
			String name, String nestedName, String id) {

		Optional<NestedCollectionRoutes<Object, Object, Object>> optional =
			_nestedCollectionRouterManager.getNestedCollectionRoutesOptional(
				name, nestedName);

		return optional.orElseGet(
			() -> _getReusableNestedCollectionRoutes(name, nestedName, id));
	}

	private Representor<Object> _getRepresentorOrFail(String name) {
		Optional<Representor<Object>> optional =
			_representableManager.getRepresentorOptional(name);

		return optional.orElseThrow(notFound(name));
	}

	private NestedCollectionRoutes _getReusableNestedCollectionRoutes(
		String name, String nestedName, String id) {

		Optional<NestedCollectionRoutes> reusableCollectionRoutesOptional =
			_reusableNestedCollectionRouterManager.
				getReusableCollectionRoutesOptional(id);

		return reusableCollectionRoutesOptional.orElseThrow(
			notFound(name, "{id}", nestedName)
		);
	}

	private Try<SingleModel<Object>> _getSingleModelTry(
		String name, String id) {

		Either<Action.Error, Action> get = _getActionManager().getAction(
			HTTPMethod.GET.name(), name, id);

		return Try.fromFallible(
			() -> get.map(
				action -> action.apply(_httpServletRequest)
			).map(
				model -> new SingleModelImpl<>(
					model, name,
					toOperations(
						_getActionManager().getActions(
							new ActionKey(HTTPMethod.GET.name(), name, id),
							null)))
			).get()
		);
	}

	private <T> Optional<T> _provide(Class<T> clazz) {
		return _providerManager.provideOptional(_httpServletRequest, clazz);
	}

	@Reference
	private ActionManager _actionManager;

	@Reference
	private ActionRouterManager _actionRouterManager;

	@Reference
	private CollectionRouterManager _collectionRouterManager;

	private Documentation _documentation;

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private ItemRouterManager _itemRouterManager;

	@Reference
	private NestedCollectionRouterManager _nestedCollectionRouterManager;

	@Reference
	private PathIdentifierMapperManager _pathIdentifierMapperManager;

	@Reference
	private ProviderManager _providerManager;

	@Reference
	private RepresentableManager _representableManager;

	@Reference
	private ReusableNestedCollectionRouterManager
		_reusableNestedCollectionRouterManager;

}