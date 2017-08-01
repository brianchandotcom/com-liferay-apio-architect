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

package com.liferay.vulcan.liferay.portal.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.vulcan.endpoint.RootEndpoint;
import com.liferay.vulcan.error.VulcanDeveloperError.MustHaveProvider;
import com.liferay.vulcan.liferay.portal.internal.pagination.PageImpl;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.pagination.SingleModel;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.wiring.osgi.manager.ProviderManager;
import com.liferay.vulcan.wiring.osgi.manager.ResourceManager;
import com.liferay.vulcan.wiring.osgi.util.GenericUtil;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
public class RootEndpointImpl implements RootEndpoint {

	@Activate
	public void activate(BundleContext bundleContext) {
		ServiceReferenceMapper<String, Resource> serviceReferenceMapper =
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(service, emitter) -> emitter.emit(service.getPath()));

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, Resource.class, null, serviceReferenceMapper);
	}

	@Deactivate
	public void deactivate() {
		_serviceTrackerMap.close();
	}

	@Override
	public <T> SingleModel<T> getCollectionItemSingleModel(
		String path, String id) {

		Resource<T> resource = (Resource<T>)_serviceTrackerMap.getService(path);

		Class<T> modelClass = GenericUtil.getGenericClass(
			resource, Resource.class);

		Routes<T> routes = _resourceManager.getRoutes(
			modelClass, _httpServletRequest);

		Optional<Function<String, T>> optional =
			routes.getModelFunctionOptional();

		Function<String, T> modelFunction = optional.orElseThrow(
			NotFoundException::new);

		T model = modelFunction.apply(id);

		return new SingleModel<>(model, modelClass);
	}

	@Override
	public <T> Page<T> getCollectionPage(String path) {
		Resource<T> resource = (Resource<T>)_serviceTrackerMap.getService(path);

		Class<T> modelClass = GenericUtil.getGenericClass(
			resource, Resource.class);

		Routes<T> routes = _resourceManager.getRoutes(
			modelClass, _httpServletRequest);

		String filterClassName = _httpServletRequest.getParameter(
			"filterClassName");

		Optional<Supplier<PageItems<T>>> optional = Optional.empty();

		if (filterClassName != null) {
			optional = routes.getFilteredPageItemsFunctionOptional(
				filterClassName);
		}
		else {
			optional = routes.getPageItemsFunctionOptional();
		}

		Supplier<PageItems<T>> pageItemsSupplier = optional.orElseThrow(
			NotFoundException::new);

		PageItems<T> pageItems = pageItemsSupplier.get();

		Optional<Pagination> paginationOptional = _provide(Pagination.class);

		Pagination pagination = paginationOptional.orElseThrow(
			() -> new MustHaveProvider(Pagination.class));

		return new PageImpl<>(
			modelClass, pageItems.getItems(), pagination.getItemsPerPage(),
			pagination.getPageNumber(), pageItems.getTotalCount());
	}

	private <U> Optional<U> _provide(Class<U> clazz) {
		return _providerManager.provide(clazz, _httpServletRequest);
	}

	@Context
	private HttpServletRequest _httpServletRequest;

	@Reference
	private ProviderManager _providerManager;

	@Reference
	private ResourceManager _resourceManager;

	private ServiceTrackerMap<String, Resource> _serviceTrackerMap;

}