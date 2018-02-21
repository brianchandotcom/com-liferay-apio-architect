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

import static com.liferay.apio.architect.wiring.osgi.internal.manager.ManagerCache.INSTANCE;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.TypeArgumentProperties.KEY_PRINCIPAL_TYPE_ARGUMENT;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.getGenericClassFromPropertyOrElse;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.getTypeParamOrFail;

import com.liferay.apio.architect.wiring.osgi.internal.service.tracker.map.listener.ClearCacheServiceTrackerMapListener;
import com.liferay.osgi.service.tracker.collections.internal.DefaultServiceTrackerCustomizer;
import com.liferay.osgi.service.tracker.collections.internal.map.ServiceTrackerMapImpl;
import com.liferay.osgi.service.tracker.collections.internal.map.SingleValueServiceTrackerBucketFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper.Emitter;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

/**
 * Manages services that have a generic type.
 *
 * @author Alejandro Hernández
 */
public abstract class BaseManager<T> {

	public BaseManager(
		Class<T> managedClass, Integer principalTypeParamPosition) {

		_managedClass = managedClass;
		_principalTypeParamPosition = principalTypeParamPosition;
	}

	@Activate
	public void activate(BundleContext bundleContext) {
		this.bundleContext = bundleContext;

		serviceTrackerMap = new ServiceTrackerMapImpl<>(
			bundleContext, _managedClass, null, this::emit,
			new DefaultServiceTrackerCustomizer<>(bundleContext),
			new SingleValueServiceTrackerBucketFactory<>(),
			new ClearCacheServiceTrackerMapListener<>());

		serviceTrackerMap.open();

		INSTANCE.clear();
	}

	@Deactivate
	public void deactivate() {
		serviceTrackerMap.close();
		INSTANCE.clear();
	}

	/**
	 * Returns the service tracker key stream.
	 *
	 * @return the service tracker key stream
	 * @review
	 */
	public Stream<String> getKeyStream() {
		Set<String> keys = serviceTrackerMap.keySet();

		return keys.stream();
	}

	/**
	 * Emits a service's key using an {@code Emitter<String>}.
	 *
	 * @param serviceReference the service reference
	 * @param emitter the emitter
	 */
	protected void emit(
		ServiceReference<T> serviceReference, Emitter<String> emitter) {

		T t = bundleContext.getService(serviceReference);

		Class<?> genericClass = getGenericClassFromPropertyOrElse(
			serviceReference, KEY_PRINCIPAL_TYPE_ARGUMENT,
			() -> getTypeParamOrFail(
				t, _managedClass, _principalTypeParamPosition));

		emitter.emit(genericClass.getName());
	}

	/**
	 * Returns a service from the inner map based on the service's generic inner
	 * class, if the service exists. Returns {@code Optional#empty()} otherwise.
	 *
	 * @param  clazz the generic inner class
	 * @return the service, if present; {@code Optional#empty()} otherwise
	 */
	protected <U> Optional<T> getServiceOptional(Class<U> clazz) {
		return Optional.ofNullable(
			serviceTrackerMap.getService(clazz.getName()));
	}

	protected BundleContext bundleContext;
	protected ServiceTrackerMap<String, T> serviceTrackerMap;

	private final Class<T> _managedClass;
	private final Integer _principalTypeParamPosition;

}