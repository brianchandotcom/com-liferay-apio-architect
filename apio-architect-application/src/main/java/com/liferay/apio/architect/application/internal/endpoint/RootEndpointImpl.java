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

package com.liferay.apio.architect.application.internal.endpoint;

import static com.liferay.apio.architect.endpoint.ExceptionSupplierUtil.notAllowed;
import static com.liferay.apio.architect.endpoint.ExceptionSupplierUtil.notFound;
import static com.liferay.apio.architect.operation.Method.DELETE;
import static com.liferay.apio.architect.operation.Method.POST;
import static com.liferay.apio.architect.operation.Method.PUT;

import static javax.ws.rs.core.Response.noContent;

import com.google.gson.JsonObject;

import com.liferay.apio.architect.alias.RequestFunction;
import com.liferay.apio.architect.alias.routes.NestedCreateItemFunction;
import com.liferay.apio.architect.documentation.APIDescription;
import com.liferay.apio.architect.documentation.APITitle;
import com.liferay.apio.architect.documentation.Documentation;
import com.liferay.apio.architect.endpoint.BinaryEndpoint;
import com.liferay.apio.architect.endpoint.FormEndpoint;
import com.liferay.apio.architect.endpoint.PageEndpoint;
import com.liferay.apio.architect.endpoint.RootEndpoint;
import com.liferay.apio.architect.error.ApioDeveloperError.MustHaveProvider;
import com.liferay.apio.architect.function.ThrowableFunction;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.single.model.SingleModel;
import com.liferay.apio.architect.unsafe.Unsafe;
import com.liferay.apio.architect.uri.Path;
import com.liferay.apio.architect.url.ServerURL;
import com.liferay.apio.architect.wiring.osgi.manager.ProviderManager;
import com.liferay.apio.architect.wiring.osgi.manager.representable.IdentifierClassManager;
import com.liferay.apio.architect.wiring.osgi.manager.representable.RepresentableManager;
import com.liferay.apio.architect.wiring.osgi.manager.router.CollectionRouterManager;
import com.liferay.apio.architect.wiring.osgi.manager.router.ItemRouterManager;
import com.liferay.apio.architect.wiring.osgi.manager.router.NestedCollectionRouterManager;
import com.liferay.apio.architect.wiring.osgi.manager.router.ReusableNestedCollectionRouterManager;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
public class RootEndpointImpl implements RootEndpoint {

	@Activate
	public void activate() {
		RequestFunction<Optional<APITitle>> apiTitleRequestFunction =
			httpServletRequest -> _providerManager.provideOptional(
				httpServletRequest, APITitle.class);

		RequestFunction<Optional<APIDescription>>
			apiDescriptionRequestFunction =
				httpServletRequest -> _providerManager.provideOptional(
					httpServletRequest, APIDescription.class);

		_documentation = new Documentation(
			apiTitleRequestFunction, apiDescriptionRequestFunction);
	}

	@Override
	public BinaryEndpoint binaryEndpoint() {
		return new BinaryEndpoint(
			_representableManager::getRepresentorOptional,
			this::_getSingleModelTry);
	}

	@Override
	public Documentation documentation() {
		return _documentation;
	}

	@Override
	public FormEndpoint formEndpoint() {
		return new FormEndpoint(
			_collectionRouterManager::getCollectionRoutesOptional,
			_itemRouterManager::getItemRoutesOptional,
			_nestedCollectionRouterManager::getNestedCollectionRoutesOptional);
	}

	@Override
	public String home() {
		List<String> resourceNames =
			_collectionRouterManager.getResourceNames();

		Optional<ServerURL> optional = _providerManager.provideOptional(
			_httpServletRequest, ServerURL.class);

		ServerURL serverURL = optional.orElseThrow(
			() -> new MustHaveProvider(ServerURL.class));

		JsonObject resourcesJsonObject = new JsonObject();

		resourceNames.forEach(
			name -> {
				String url = serverURL.get() + "/p/" + name;

				JsonObject jsonObject = new JsonObject();

				jsonObject.addProperty("href", url);

				resourcesJsonObject.add(name, jsonObject);
			});

		JsonObject rootJsonObject = new JsonObject();

		rootJsonObject.add("resources", resourcesJsonObject);

		return rootJsonObject.toString();
	}

	@Override
	public PageEndpoint pageEndpoint() {
		return new PageEndpoint() {

			@Override
			public <T> Try<SingleModel<T>> addCollectionItem(
				String name, Map<String, Object> body) {

				Try<CollectionRoutes<T>> collectionRoutesTry =
					_getCollectionRoutesTry(name);

				return collectionRoutesTry.mapOptional(
					CollectionRoutes::getCreateItemFunctionOptional,
					notAllowed(POST, name)
				).map(
					function -> function.apply(_httpServletRequest)
				).map(
					function -> function.apply(body)
				);
			}

			@Override
			public <T> Try<SingleModel<T>> addNestedCollectionItem(
				String name, String id, String nestedName,
				Map<String, Object> body) {

				Try<NestedCollectionRoutes<T, Object>>
					nestedCollectionRoutesTry = _getNestedCollectionRoutesTry(
						name, nestedName);

				return nestedCollectionRoutesTry.mapOptional(
					NestedCollectionRoutes::getNestedCreateItemFunctionOptional
				).flatMap(
					(NestedCreateItemFunction<T, Object>
						nestedCreateItemFunction) -> {
						Try<SingleModel<T>> singleModelTry =
							getCollectionItemSingleModelTry(name, id);

						return singleModelTry.mapOptional(
							_getIdentifierFunction(name, nestedName)
						).map(
							identifier -> nestedCreateItemFunction.apply(
								_httpServletRequest
							).apply(
								identifier
							).apply(
								body
							)
						);
					}
				).mapFailMatching(
					NoSuchElementException.class,
					notAllowed(POST, name, id, nestedName)
				);
			}

			@Override
			public Response deleteCollectionItem(String name, String id) {
				Try<String> stringTry = Try.success(name);

				stringTry.flatMap(
					RootEndpointImpl.this::_getItemRoutesTry
				).mapOptional(
					ItemRoutes::getDeleteConsumerOptional,
					notAllowed(DELETE, name, id)
				).getUnchecked(
				).apply(
					_httpServletRequest
				).accept(
					new Path(name, id)
				);

				return noContent().build();
			}

			@Override
			public <T> Try<SingleModel<T>> getCollectionItemSingleModelTry(
				String name, String id) {

				return _getSingleModelTry(name, id);
			}

			@Override
			public <T> Try<Page<T>> getCollectionPageTry(String name) {
				Try<CollectionRoutes<T>> collectionRoutesTry =
					_getCollectionRoutesTry(name);

				return collectionRoutesTry.mapOptional(
					CollectionRoutes::getGetPageFunctionOptional, notFound(name)
				).map(
					function -> function.apply(_httpServletRequest)
				);
			}

			@Override
			public <T> Try<Page<T>> getNestedCollectionPageTry(
				String name, String id, String nestedName) {

				Try<NestedCollectionRoutes<T, Object>>
					nestedCollectionRoutesTry = _getNestedCollectionRoutesTry(
						name, nestedName);

				return nestedCollectionRoutesTry.map(
					NestedCollectionRoutes::getNestedGetPageFunctionOptional
				).map(
					Optional::get
				).map(
					function -> function.apply(_httpServletRequest)
				).map(
					function -> function.apply(new Path(name, id))
				).flatMap(
					pageFunction -> {
						Try<SingleModel<T>> parentSingleModelTry =
							getCollectionItemSingleModelTry(name, id);

						return parentSingleModelTry.map(
							_getIdentifierFunction(name, nestedName)
						).map(
							optional -> optional.map(pageFunction)
						);
					}
				).map(
					Optional::get
				).mapFailMatching(
					NoSuchElementException.class, notFound(name, id, nestedName)
				);
			}

			@Override
			public <T> Try<SingleModel<T>> updateCollectionItem(
				String name, String id, Map<String, Object> body) {

				Try<ItemRoutes<T>> itemRoutesTry = _getItemRoutesTry(name);

				return itemRoutesTry.mapOptional(
					ItemRoutes::getUpdateItemFunctionOptional,
					notAllowed(PUT, name, id)
				).map(
					function -> function.apply(_httpServletRequest)
				).map(
					function -> function.apply(new Path(name, id))
				).map(
					function -> function.apply(body)
				);
			}

		};
	}

	private <T> Try<CollectionRoutes<T>> _getCollectionRoutesTry(String name) {
		Try<String> stringTry = Try.success(name);

		return stringTry.mapOptional(
			_collectionRouterManager::getCollectionRoutesOptional,
			notFound(name));
	}

	private Predicate<RelatedCollection<?>>
		_getFilterRelatedCollectionPredicate(String nestedName) {

		return relatedCollection -> {
			Class<?> relatedIdentifierClass =
				relatedCollection.getIdentifierClass();

			String className = relatedIdentifierClass.getName();

			Optional<Class<Identifier>> optional =
				_identifierClassManager.getIdentifierClassOptional(nestedName);

			return optional.map(
				Class::getName
			).map(
				className::equals
			).orElse(
				false
			);
		};
	}

	private <T> ThrowableFunction<SingleModel<T>, Optional<Object>>
		_getIdentifierFunction(String name, String nestedName) {

		return parentSingleModel -> {
			Optional<Representor<T, Object>> optional =
				_representableManager.getRepresentorOptional(name);

			return optional.map(
				Representor::getRelatedCollections
			).filter(
				stream -> stream.anyMatch(
					_getFilterRelatedCollectionPredicate(nestedName))
			).flatMap(
				__ -> optional
			).map(
				representor -> representor.getIdentifier(
					parentSingleModel.getModel())
			);
		};
	}

	private <T> Try<ItemRoutes<T>> _getItemRoutesTry(String name) {
		Try<String> stringTry = Try.success(name);

		return stringTry.mapOptional(
			_itemRouterManager::getItemRoutesOptional, notFound(name));
	}

	private <T> Try<NestedCollectionRoutes<T, Object>>
		_getNestedCollectionRoutesTry(String name, String nestedName) {

		Try<NestedCollectionRouterManager> managerTry = Try.success(
			_nestedCollectionRouterManager);

		return managerTry.mapOptional(
			manager -> manager.getNestedCollectionRoutesOptional(
				name, nestedName),
			notFound(name, nestedName)
		).recoverWith(
			__ -> _getReusableNestedCollectionRoutesTry(nestedName)
		).map(
			Unsafe::unsafeCast
		);
	}

	private <T, S> Try<NestedCollectionRoutes<T, S>>
		_getReusableNestedCollectionRoutesTry(String nestedName) {

		Try<String> stringTry = Try.success(nestedName);

		return stringTry.map(
			_reusableNestedCollectionRouterManager::
				getNestedCollectionRoutesOptional
		).map(
			Optional::get
		).map(
			Unsafe::unsafeCast
		);
	}

	private <T> Try<SingleModel<T>> _getSingleModelTry(String name, String id) {
		Try<ItemRoutes<T>> itemRoutesTry = _getItemRoutesTry(name);

		return itemRoutesTry.mapOptional(
			ItemRoutes::getItemFunctionOptional, notFound(name, id)
		).map(
			function -> function.apply(_httpServletRequest)
		).map(
			function -> function.apply(new Path(name, id))
		);
	}

	@Reference
	private CollectionRouterManager _collectionRouterManager;

	private Documentation _documentation;

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private IdentifierClassManager _identifierClassManager;

	@Reference
	private ItemRouterManager _itemRouterManager;

	@Reference
	private NestedCollectionRouterManager _nestedCollectionRouterManager;

	@Reference
	private ProviderManager _providerManager;

	@Reference
	private RepresentableManager _representableManager;

	@Reference
	private ReusableNestedCollectionRouterManager
		_reusableNestedCollectionRouterManager;

}