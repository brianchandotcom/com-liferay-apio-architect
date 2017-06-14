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

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.vulcan.endpoint.RootEndpoint;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.pagination.SingleModel;
import com.liferay.vulcan.representor.Resource;
import com.liferay.vulcan.representor.Routes;
import com.liferay.vulcan.wiring.osgi.GenericUtil;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
public class LiferayRootEndpoint implements RootEndpoint {

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

		RoutesBuilderImpl<T> endpointBuilder = new RoutesBuilderImpl<>(
			_pagination);

		Routes<T> routes = resource.routes(endpointBuilder);

		Function<String, T> modelFunction = routes.getModelFunction();

		T model = modelFunction.apply(id);

		return new SingleModel<>(model, modelClass);
	}

	@Override
	public <T> Page<T> getCollectionPage(String path) {
		Resource<T> resource = (Resource<T>)_serviceTrackerMap.getService(path);

		RoutesBuilderImpl<T> endpointBuilder = new RoutesBuilderImpl<>(
			_pagination);

		Routes<T> routes = resource.routes(endpointBuilder);

		Supplier<Page<T>> pageSupplier = routes.getPageSupplier();

		return pageSupplier.get();
	}

	@Path("/group/{id}/")
	public LiferayRootEndpoint getGroupLiferayRootEndpoint(
		@PathParam("id") long id) {

		GroupThreadLocal.setGroupId(id);

		return this;
	}

	@Context
	private Pagination _pagination;

	private ServiceTrackerMap<String, Resource> _serviceTrackerMap;

}