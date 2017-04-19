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
import com.liferay.vulcan.wiring.osgi.internal.RepresentorBuilderImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

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

		_createRepresentorMaps(modelRepresentorMapper, modelClass);
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

		TreeSet<ModelRepresentorMapperTuple<?>> modelRepresentorMapperTuples =
			_modelRepresentorMappers.get(modelClass.getName());

		modelRepresentorMapperTuples.add(tuple);
	}

	private <T> void _createRepresentorMaps(
		ModelRepresentorMapper<T> modelRepresentorMapper, Class<T> modelClass) {

		Map<String, Function<?, Object>> fieldFunctions = new HashMap<>();
		List<RelationTuple<?, ?>> relationTuples = new ArrayList<>();
		List<String> types = new ArrayList<>();

		_fieldFunctionMaps.put(modelClass.getName(), fieldFunctions);
		_relationTupleLists.put(modelClass.getName(), relationTuples);
		_typeLists.put(modelClass.getName(), types);

		modelRepresentorMapper.buildRepresentor(
			new RepresentorBuilderImpl<>(
				modelClass, _identifierFunctions, fieldFunctions,
				relationTuples, types));
	}

	private final BundleContext _bundleContext = FrameworkUtil.getBundle(
		RepresentorManager.class).getBundleContext();
	private final ConcurrentMap<String, Map<String, Function<?, Object>>>
		_fieldFunctionMaps = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Function<?, String>>
		_identifierFunctions = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, TreeSet<ModelRepresentorMapperTuple<?>>>
		_modelRepresentorMappers = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, List<RelationTuple<?, ?>>>
		_relationTupleLists = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, List<String>> _typeLists =
		new ConcurrentHashMap<>();

}