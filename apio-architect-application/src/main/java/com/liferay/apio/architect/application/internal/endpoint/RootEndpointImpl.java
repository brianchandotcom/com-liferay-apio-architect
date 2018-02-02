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

import com.google.gson.JsonObject;

import com.liferay.apio.architect.alias.BinaryFunction;
import com.liferay.apio.architect.alias.RequestFunction;
import com.liferay.apio.architect.documentation.APIDescription;
import com.liferay.apio.architect.documentation.APITitle;
import com.liferay.apio.architect.documentation.Documentation;
import com.liferay.apio.architect.endpoint.RootEndpoint;
import com.liferay.apio.architect.error.ApioDeveloperError;
import com.liferay.apio.architect.form.Form;
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

import java.io.InputStream;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

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
	public <T> Try<SingleModel<T>> addCollectionItem(
		String name, Map<String, Object> body) {

		Try<CollectionRoutes<T>> collectionRoutesTry = _getCollectionRoutesTry(
			name);

		return collectionRoutesTry.mapOptional(
			CollectionRoutes::getCreateItemFunctionOptional,
			_getNotAllowedExceptionSupplier("POST", name)
		).map(
			function -> function.apply(_httpServletRequest)
		).map(
			function -> function.apply(body)
		);
	}

	@Override
	public <T> Try<SingleModel<T>> addNestedCollectionItem(
		String name, String id, String nestedName, Map<String, Object> body) {

		Try<NestedCollectionRoutes<T, Object>> nestedCollectionRoutesTry =
			_getNestedCollectionRoutesTry(name, nestedName);

		return nestedCollectionRoutesTry.map(
			NestedCollectionRoutes::getNestedCreateItemFunctionOptional
		).map(
			Optional::get
		).map(
			function -> function.apply(_httpServletRequest)
		).flatMap(
			postFunction -> {
				Try<SingleModel<T>> parentSingleModelTry =
					getCollectionItemSingleModelTry(name, id);

				return parentSingleModelTry.map(
					_getIdentifierFunction(name, nestedName)
				).map(
					optional -> optional.map(postFunction)
				);
			}
		).mapOptional(
			optional -> optional.map(function -> function.apply(body)),
			_getNotAllowedExceptionSupplier(
				"POST", String.join("/", name, id, nestedName))
		);
	}

	@Override
	public Response deleteCollectionItem(String name, String id) {
		Try<ItemRoutes<Object>> itemRoutesTry = _getItemRoutesTry(name);

		itemRoutesTry.mapOptional(
			ItemRoutes::getDeleteConsumerOptional,
			_getNotAllowedExceptionSupplier("DELETE", name + "/" + id)
		).getUnchecked(
		).apply(
			_httpServletRequest
		).accept(
			new Path(name, id)
		);

		ResponseBuilder responseBuilder = Response.noContent();

		return responseBuilder.build();
	}

	@Override
	public Try<InputStream> getCollectionItemInputStreamTry(
		String name, String id, String binaryId) {

		Try<String> stringTry = Try.success(name);

		return stringTry.mapOptional(
			_representableManager::getRepresentorOptional,
			_getNotFoundExceptionSupplier(String.join("/", name, id, binaryId))
		).map(
			Representor::getBinaryFunctions
		).map(
			binaryFunctions -> binaryFunctions.get(binaryId)
		).flatMap(
			binaryFunction -> _getInputStreamTry(name, id, binaryFunction)
		);
	}

	@Override
	public <T> Try<SingleModel<T>> getCollectionItemSingleModelTry(
		String name, String id) {

		Try<ItemRoutes<T>> itemRoutesTry = _getItemRoutesTry(name);

		return itemRoutesTry.mapOptional(
			ItemRoutes::getItemFunctionOptional,
			_getNotFoundExceptionSupplier(name + "/" + id)
		).map(
			function -> function.apply(_httpServletRequest)
		).map(
			function -> function.apply(new Path(name, id))
		);
	}

	@Override
	public <T> Try<Page<T>> getCollectionPageTry(String name) {
		Try<CollectionRoutes<T>> collectionRoutesTry = _getCollectionRoutesTry(
			name);

		return collectionRoutesTry.mapOptional(
			CollectionRoutes::getGetPageFunctionOptional,
			_getNotFoundExceptionSupplier(name)
		).map(
			function -> function.apply(_httpServletRequest)
		);
	}

	@Override
	public Try<Form> getCreatorFormTry(String name) {
		Try<CollectionRoutes<Object>> collectionRoutesTry =
			_getCollectionRoutesTry(name);

		return collectionRoutesTry.mapOptional(
			CollectionRoutes::getFormOptional, NotFoundException::new);
	}

	@Override
	public Documentation getDocumentation() {
		return _documentation;
	}

	@Override
	public String getHome() {
		List<String> resourceNames =
			_collectionRouterManager.getResourceNames();

		Optional<ServerURL> optional = _providerManager.provideOptional(
			_httpServletRequest, ServerURL.class);

		ServerURL serverURL = optional.orElseThrow(
			() -> new ApioDeveloperError.MustHaveProvider(ServerURL.class));

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
	public <T> Try<Page<T>> getNestedCollectionPageTry(
		String name, String id, String nestedName) {

		Try<NestedCollectionRoutes<T, Object>> nestedCollectionRoutesTry =
			_getNestedCollectionRoutesTry(name, nestedName);

		return nestedCollectionRoutesTry.map(
			NestedCollectionRoutes::getNestedGetPageFunctionOptional
		).map(
			Optional::get
		).map(
			function -> function.apply(_httpServletRequest)
		).map(
			function -> function.apply(new Path(name, id))
		).flatMap(
			_getNestedCollectionPageTryFunction(name, id, nestedName)
		).map(
			Optional::get
		).mapFailMatching(
			NoSuchElementException.class,
			_getNotFoundExceptionSupplier(
				String.join("/", name, id, nestedName))
		);
	}

	@Override
	public Try<Form> getNestedCreatorFormTry(String name, String nestedName) {
		Try<NestedCollectionRoutes<Object, Object>> nestedCollectionRoutesTry =
			_getNestedCollectionRoutesTry(name, nestedName);

		return nestedCollectionRoutesTry.mapOptional(
			NestedCollectionRoutes::getFormOptional, NotFoundException::new);
	}

	@Override
	public Try<Form> getUpdaterFormTry(String name) {
		Try<ItemRoutes<Object>> itemRoutesTry = _getItemRoutesTry(name);

		return itemRoutesTry.mapOptional(
			ItemRoutes::getFormOptional, NotFoundException::new);
	}

	@Override
	public <T> Try<SingleModel<T>> updateCollectionItem(
		String name, String id, Map<String, Object> body) {

		Try<ItemRoutes<T>> itemRoutesTry = _getItemRoutesTry(name);

		return itemRoutesTry.mapOptional(
			ItemRoutes::getUpdateItemFunctionOptional,
			_getNotAllowedExceptionSupplier("PUT", name + "/" + id)
		).map(
			function -> function.apply(_httpServletRequest)
		).map(
			function -> function.apply(new Path(name, id))
		).map(
			function -> function.apply(body)
		);
	}

	private <T> Try<CollectionRoutes<T>> _getCollectionRoutesTry(String name) {
		Try<String> stringTry = Try.success(name);

		return stringTry.mapOptional(
			_collectionRouterManager::getCollectionRoutesOptional,
			() -> new NotFoundException("No resource found for path " + name));
	}

	private Predicate<RelatedCollection<?>>
		_getFilterRelatedCollectionPredicate(String nestedName) {

		return relatedCollection -> {
			Class<?> relatedModelClass = relatedCollection.getIdentifierClass();

			String relatedClassName = relatedModelClass.getName();

			Optional<Class<Identifier>> optional =
				_identifierClassManager.getIdentifierClassOptional(nestedName);

			return optional.map(
				Class::getName
			).map(
				relatedClassName::equals
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

	private <T> Try<InputStream> _getInputStreamTry(
		String name, String id, BinaryFunction<T> binaryFunction) {

		Try<SingleModel<T>> singleModelTry = getCollectionItemSingleModelTry(
			name, id);

		return singleModelTry.map(
			SingleModel::getModel
		).map(
			binaryFunction::apply
		);
	}

	private <T> Try<ItemRoutes<T>> _getItemRoutesTry(String name) {
		Try<String> stringTry = Try.success(name);

		return stringTry.mapOptional(
			_itemRouterManager::getItemRoutesOptional,
			() -> new NotFoundException("No resource found for path " + name));
	}

	private <T> ThrowableFunction<Function<Object, Page<T>>,
		Try<Optional<Page<T>>>> _getNestedCollectionPageTryFunction(
			String name, String id, String nestedName) {

		return pageFunction -> {
			Try<SingleModel<T>> parentSingleModelTry =
				getCollectionItemSingleModelTry(name, id);

			return parentSingleModelTry.map(
				_getIdentifierFunction(name, nestedName)
			).map(
				optional -> optional.map(pageFunction)
			);
		};
	}

	private <T> Try<NestedCollectionRoutes<T, Object>>
		_getNestedCollectionRoutesTry(String name, String nestedName) {

		Try<NestedCollectionRouterManager> managerTry = Try.success(
			_nestedCollectionRouterManager);

		return managerTry.mapOptional(
			manager -> manager.getNestedCollectionRoutesOptional(
				name, nestedName),
			() -> new NotFoundException("No resource found for path " + name)
		).recoverWith(
			__ -> _getReusableNestedCollectionRoutesTry(nestedName)
		).map(
			Unsafe::unsafeCast
		);
	}

	private Supplier<NotAllowedException> _getNotAllowedExceptionSupplier(
		String method, String path) {

		return () -> new NotAllowedException(
			method + " method is not allowed for path " + path);
	}

	private Supplier<NotFoundException> _getNotFoundExceptionSupplier(
		String name) {

		return () -> new NotFoundException("No endpoint found at path " + name);
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