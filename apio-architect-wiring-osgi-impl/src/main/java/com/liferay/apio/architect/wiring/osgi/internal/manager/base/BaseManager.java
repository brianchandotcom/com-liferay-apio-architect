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

package com.liferay.apio.architect.wiring.osgi.internal.manager.base;

import static com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory.openSingleValueMap;

import com.liferay.apio.architect.wiring.osgi.internal.service.reference.mapper.CustomServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;

import java.util.Optional;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

/**
 * Manages services that have a generic type.
 *
 * @author Alejandro Hernández
 */
public abstract class BaseManager<T> {

	@Activate
	public void activate(BundleContext bundleContext) {
		_serviceTrackerMap = openSingleValueMap(
			bundleContext, getManagedClass(), null,
			new CustomServiceReferenceMapper<>(
				bundleContext, getManagedClass()));
	}

	@Deactivate
	public void deactivate() {
		_serviceTrackerMap.close();
	}

	/**
	 * Returns the managed class.
	 *
	 * @return the managed class
	 * @review
	 */
	protected abstract Class<T> getManagedClass();

	/**
	 * Returns a service from the inner map based on the service's generic inner
	 * class, if the service exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @param  clazz the generic inner class
	 * @return the service, if present; {@code Optional#empty()} otherwise
	 */
	protected <U> Optional<T> getServiceOptional(Class<U> clazz) {
		return getServiceOptional(clazz.getName());
	}

	/**
	 * Returns a service from the inner map based on the service's generic inner
	 * class name, if the service exists. Returns {@code Optional#empty()}
	 * otherwise.
	 *
	 * @param  className the generic inner class name
	 * @return the service, if present; {@code Optional#empty()} otherwise
	 */
	protected Optional<T> getServiceOptional(String className) {
		return Optional.ofNullable(_serviceTrackerMap.getService(className));
	}

	private ServiceTrackerMap<String, T> _serviceTrackerMap;

}