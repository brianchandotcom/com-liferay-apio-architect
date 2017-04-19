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

import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.wiring.osgi.internal.GenericUtil;
import com.liferay.vulcan.wiring.osgi.internal.InvalidGenericException;
import com.liferay.vulcan.wiring.osgi.internal.ModelRepresentorMapperTuple;

import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true, service = RepresentorManager.class)
public class RepresentorManager {

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected <T> void setServiceReference(
			ServiceReference<ModelRepresentorMapper<T>> serviceReference)
		throws InvalidGenericException {

		ModelRepresentorMapper<T> modelRepresentorMapper =
			_bundleContext.getService(serviceReference);

		Optional<Class<T>> genericClass = GenericUtil.getGenericClass(
			modelRepresentorMapper, ModelRepresentorMapper.class);

		if (!genericClass.isPresent()) {
			throw new InvalidGenericException(
				modelRepresentorMapper.getClass());
		}

		Class<T> modelClass = genericClass.get();

		_addModelRepresentorMapper(
			serviceReference, modelRepresentorMapper, modelClass);
	}

	protected <T> void unsetServiceReference(
		ServiceReference<ModelRepresentorMapper<T>> serviceReference) {
	}

	private <T> void _addModelRepresentorMapper(
		ServiceReference<ModelRepresentorMapper<T>> serviceReference,
		ModelRepresentorMapper<T> modelRepresentorMapper, Class<T> modelClass) {

		_modelRepresentorMappers.computeIfAbsent(
			modelClass.getName(), name -> new TreeSet<>());

		ModelRepresentorMapperTuple<T> tuple =
			new ModelRepresentorMapperTuple<>(
				serviceReference, modelRepresentorMapper);

		_modelRepresentorMappers.get(modelClass.getName()).add(tuple);
	}

	private final BundleContext _bundleContext = FrameworkUtil.getBundle(
		RepresentorManager.class).getBundleContext();
	private final ConcurrentMap<String, TreeSet<ModelRepresentorMapperTuple<?>>>
		_modelRepresentorMappers = new ConcurrentHashMap<>();

}