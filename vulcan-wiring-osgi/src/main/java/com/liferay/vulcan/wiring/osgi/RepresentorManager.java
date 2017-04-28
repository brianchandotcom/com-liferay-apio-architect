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
import com.liferay.vulcan.wiring.osgi.internal.ModelRepresentorMapperTuple;
import com.liferay.vulcan.wiring.osgi.internal.RepresentorBuilderImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
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

	public <T> Map<String, Function<T, Object>> getFieldFunctions(
		Class<T> modelClass) {

		return (Map)_fieldFunctionMaps.get(modelClass.getName());
	}

	public <T> String getIdentifier(Class<T> modelClass, T model) {
		Function<T, String> identifierFunction =
			(Function<T, String>)_identifierFunctions.get(modelClass.getName());

		return identifierFunction.apply(model);
	}

	public <T> Optional<ModelRepresentorMapper<T>> getModelRepresentorMapper(
		Class<T> modelClass) {

		TreeSet<ModelRepresentorMapperTuple<?>> modelRepresentorMapperTuples =
			_modelRepresentorMappers.get(modelClass.getName());

		Optional<TreeSet<ModelRepresentorMapperTuple<?>>>
			modelRepresentorMapperTuplesOptional = Optional.ofNullable(
				modelRepresentorMapperTuples);

		Optional<ModelRepresentorMapperTuple<?>>
			firstModelRepresentorMapperTuple =
				modelRepresentorMapperTuplesOptional.map(TreeSet::first);

		return firstModelRepresentorMapperTuple.map(
			modelRepresentorMapperTuple ->
				(ModelRepresentorMapper<T>)modelRepresentorMapperTuple.
					getModelRepresentorMapper());
	}

	public <T, V> List<RelationTuple<T, V>> getRelations(Class<T> modelClass) {
		return (List)_relationTupleLists.get(modelClass.getName());
	}

	public <T> List<String> getTypes(Class<T> modelClass) {
		return _typeLists.get(modelClass.getName());
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected <T> void setServiceReference(
			ServiceReference<ModelRepresentorMapper<T>> serviceReference)
		throws IllegalArgumentException {

		ModelRepresentorMapper<T> modelRepresentorMapper =
			_bundleContext.getService(serviceReference);

		Class<T> modelClass = _getModelClass(modelRepresentorMapper);

		_addModelRepresentorMapper(
			serviceReference, modelRepresentorMapper, modelClass);

		_createRepresentorMaps(modelRepresentorMapper, modelClass);
	}

	protected <T> void unsetServiceReference(
			ServiceReference<ModelRepresentorMapper<T>> serviceReference)
		throws IllegalArgumentException {

		ModelRepresentorMapper<T> modelRepresentorMapper =
			_bundleContext.getService(serviceReference);

		Class<T> modelClass = _getModelClass(modelRepresentorMapper);

		_removeModelRepresentorMapper(modelRepresentorMapper, modelClass);

		_removeRepresentorMaps(modelClass);

		getModelRepresentorMapper(modelClass).ifPresent(
			firstModelRepresentorMapper ->
				_createRepresentorMaps(
					firstModelRepresentorMapper, modelClass));
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

	private <T> Class<T> _getModelClass(
			ModelRepresentorMapper<T> modelRepresentorMapper)
		throws IllegalArgumentException {

		Optional<Class<T>> optional = GenericUtil.getGenericClassOptional(
			modelRepresentorMapper, ModelRepresentorMapper.class);

		if (!optional.isPresent()) {
			Class<?> clazz = modelRepresentorMapper.getClass();

			throw new IllegalArgumentException(
				"Class " + clazz.getName() + " must have a generic type");
		}

		return optional.get();
	}

	private <T> void _removeModelRepresentorMapper(
		ModelRepresentorMapper modelRepresentorMapper, Class<T> modelClass) {

		_modelRepresentorMappers.get(modelClass.getName()).removeIf(tuple ->
			tuple.getModelRepresentorMapper() == modelRepresentorMapper);
	}

	private <T> void _removeRepresentorMaps(Class<T> modelClass) {
		_fieldFunctionMaps.remove(modelClass.getName());
		_identifierFunctions.remove(modelClass.getName());
		_relationTupleLists.remove(modelClass.getName());
		_typeLists.remove(modelClass.getName());
	}

	private final BundleContext _bundleContext = FrameworkUtil.getBundle(
		RepresentorManager.class).getBundleContext();
	private final Map<String, Map<String, Function<?, Object>>>
		_fieldFunctionMaps = new ConcurrentHashMap<>();
	private final Map<String, Function<?, String>> _identifierFunctions =
		new ConcurrentHashMap<>();
	private final Map<String, TreeSet<ModelRepresentorMapperTuple<?>>>
		_modelRepresentorMappers = new ConcurrentHashMap<>();
	private final Map<String, List<RelationTuple<?, ?>>> _relationTupleLists =
		new ConcurrentHashMap<>();
	private final Map<String, List<String>> _typeLists =
		new ConcurrentHashMap<>();

}