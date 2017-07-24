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

package com.liferay.vulcan.wiring.osgi;

import com.liferay.vulcan.converter.Converter;
import com.liferay.vulcan.wiring.osgi.internal.ServiceReferenceServiceTuple;

import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * Provides methods to convert String identifiers to custom types.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true, service = ConverterManager.class)
public class ConverterManager {

	public ConverterManager() {
		Bundle bundle = FrameworkUtil.getBundle(ConverterManager.class);

		_bundleContext = bundle.getBundleContext();
	}

	/**
	 * Converts a string identifier to its equivalent of type T if a valid
	 * {@link Converter} can be found. Returns <code>Optional#empty()</code>
	 * otherwise.
	 *
	 * @param  clazz the type class to be converted to.
	 * @param  id the string identifier.
	 * @return the converted identifier, if a valid {@link Converter} is
	 *         present; <code>Optional#empty()</code> otherwise.
	 */
	public <T> Optional<T> convert(Class<T> clazz, String id) {
		TreeSet<ServiceReferenceServiceTuple<Converter<?>>>
			serviceReferenceServiceTuples = _converters.get(clazz.getName());

		Optional<TreeSet<ServiceReferenceServiceTuple<Converter<?>>>> optional =
			Optional.ofNullable(serviceReferenceServiceTuples);

		return optional.filter(
			treeSet -> !treeSet.isEmpty()
		).map(
			TreeSet::first
		).map(
			serviceReferenceServiceTuple ->
				(Converter<T>)
					serviceReferenceServiceTuple.getService()
		).map(
			converter -> converter.convert(id)
		);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected <T> void setServiceReference(
		ServiceReference<Converter<T>> serviceReference) {

		Converter<T> converter = _bundleContext.getService(serviceReference);

		Class<T> modelClass = GenericUtil.getGenericClass(
			converter, Converter.class);

		_converters.computeIfAbsent(
			modelClass.getName(), name -> new TreeSet<>());

		TreeSet<ServiceReferenceServiceTuple<Converter<?>>>
			serviceReferenceServiceTuples = _converters.get(
				modelClass.getName());

		ServiceReferenceServiceTuple<Converter<T>>
			serviceReferenceServiceTuple = new ServiceReferenceServiceTuple<>(
				serviceReference, converter);

		serviceReferenceServiceTuples.add(
			(ServiceReferenceServiceTuple)serviceReferenceServiceTuple);
	}

	protected <T> void unsetServiceReference(
		ServiceReference<Converter<T>> serviceReference) {

		Converter<T> resource = _bundleContext.getService(serviceReference);

		Class<T> modelClass = GenericUtil.getGenericClass(
			resource, Converter.class);

		TreeSet<ServiceReferenceServiceTuple<Converter<?>>>
			serviceReferenceServiceTuples = _converters.get(
				modelClass.getName());

		serviceReferenceServiceTuples.removeIf(
			serviceReferenceServiceTuple -> {
				if (serviceReferenceServiceTuple.getService() == resource) {
					return true;
				}

				return false;
			});
	}

	private final BundleContext _bundleContext;
	private final Map
		<String, TreeSet<ServiceReferenceServiceTuple<Converter<?>>>>
			_converters = new ConcurrentHashMap<>();

}