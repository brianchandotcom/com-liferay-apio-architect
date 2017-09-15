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

package com.liferay.vulcan.wiring.osgi.manager;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.function.TriConsumer;
import com.liferay.vulcan.identifier.Identifier;
import com.liferay.vulcan.resource.RelatedCollection;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.result.Try;
import com.liferay.vulcan.wiring.osgi.internal.resource.builder.RepresentorBuilderImpl;
import com.liferay.vulcan.wiring.osgi.internal.resource.builder.RepresentorBuilderImpl.RepresentorImpl;
import com.liferay.vulcan.wiring.osgi.internal.resource.builder.RoutesBuilderImpl;
import com.liferay.vulcan.wiring.osgi.util.GenericUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides methods to retrieve information provided by the different {@link
 * Resource} instances, such as field functions, types, identifier functions,
 * and so on.
 *
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 * @see    Resource
 */
@Component(immediate = true, service = ResourceManager.class)
public class ResourceManager extends BaseManager<Resource> {

	/**
	 * Returns the model class name, exposed in a certain path.
	 *
	 * @param  path path of the resource for the class name.
	 * @return the class name exposed in the path.
	 */
	public String getClassName(String path) {
		return getModelClass(path).getName();
	}

	/**
	 * Returns the model class, exposed in a certain path.
	 *
	 * @param  path path of the resource for the class.
	 * @return the class exposed in the path.
	 */
	public <T> Class<T> getModelClass(String path) {
		return (Class<T>)_classes.get(path);
	}

	/**
	 * Returns the path in which a class is exposed.
	 *
	 * @param  modelClass the class of a {@link Resource}
	 * @return the path in which the class is exposed.
	 */
	public Optional<String> getPathOptional(Class<?> modelClass) {
		return getPathOptional(modelClass.getName());
	}

	/**
	 * Returns the path in which a class name is exposed.
	 *
	 * @param  className the class name of a {@link Resource}
	 * @return the path in which the class name is exposed.
	 */
	public Optional<String> getPathOptional(String className) {
		Optional<? extends Resource<?, Identifier>> optional =
			getResourceOptional(className);

		return optional.map(Resource::getPath);
	}

	/**
	 * Returns a stream with the related collections for the model class, if
	 * present. Returns {@code Optional#empty()} if no related collections can
	 * be found.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the related collections for the model class, if present; {@code
	 *         Optional#empty()} otherwise.
	 */
	public <T, U extends Identifier> Optional<Stream<RelatedCollection<T, ?>>>
		getRelatedCollectionsOptional(Class<T> modelClass) {

		Optional<Representor<T, U>> optional = getRepresentorOptional(
			modelClass);

		List<RelatedCollection<T, ?>> extraRelatedCollections =
			(List)_relatedCollections.get(modelClass.getName());

		return optional.map(
			Representor::getRelatedCollections
		).map(
			relatedCollections ->
				Stream.of(relatedCollections, extraRelatedCollections)
		).map(
			stream -> stream.filter(
				Objects::nonNull
			).flatMap(
				Collection::stream
			)
		);
	}

	/**
	 * Returns the representor of the model class, if present. Returns {@code
	 * Optional#empty()} if no representor can be found.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the representor of the model class, if present; {@code
	 *         Optional#empty()} otherwise.
	 */
	public <T, U extends Identifier> Optional<Representor<T, U>>
		getRepresentorOptional(Class<T> modelClass) {

		Optional<RepresentorImpl> optional = Optional.ofNullable(
			_representors.get(modelClass.getName()));

		return optional.map(representor -> (Representor<T, U>)representor);
	}

	/**
	 * Returns the {@link Resource} of the model class. Returns
	 * <code>Optional#empty()</code> if the {@link Resource} isn't present.
	 *
	 * @param  modelClass the model class.
	 * @return the {@link Resource}, if present; <code>Optional#empty()</code>
	 *         otherwise.
	 */
	public <T, U extends Identifier> Optional<Resource<T, U>>
		getResourceOptional(Class<T> modelClass) {

		return getResourceOptional(modelClass.getName());
	}

	/**
	 * Returns the {@link Resource} of the model class name. Returns
	 * <code>Optional#empty()</code> if the {@link Resource} isn't present.
	 *
	 * @param  modelClassName the model class name.
	 * @return the {@link Resource}, if present; <code>Optional#empty()</code>
	 *         otherwise.
	 */
	public <T, U extends Identifier> Optional<Resource<T, U>>
		getResourceOptional(String modelClassName) {

		return getServiceOptional(modelClassName).map(
			resource -> (Resource<T, U>)resource);
	}

	/**
	 * Returns the routes of the model class for a certain path.
	 *
	 * @param  path the path of a {@link Resource}.
	 * @param  httpServletRequest the actual request.
	 * @return the routes of the model class.
	 */
	public <T> Optional<Routes<T>> getRoutesOptional(
		String path, HttpServletRequest httpServletRequest) {

		Optional<Function<HttpServletRequest, Routes<?>>> optional =
			Optional.ofNullable(_routesFunctions.get(path));

		return optional.map(
			routesFunction -> routesFunction.apply(httpServletRequest)
		).map(
			routes -> (Routes<T>)routes
		);
	}

	@Reference(cardinality = MULTIPLE, policy = DYNAMIC, policyOption = GREEDY)
	protected <T> void setServiceReference(
		ServiceReference<Resource> serviceReference) {

		Optional<Class<Object>> optional = addService(
			serviceReference, Resource.class);

		optional.ifPresent(this::_addModelClassMaps);
	}

	@SuppressWarnings("unused")
	protected void unsetServiceReference(
		ServiceReference<Resource> serviceReference) {

		Optional<Class<Object>> optional = removeService(
			serviceReference, Resource.class);

		optional.ifPresent(this::_removeModelClassMaps);

		optional.filter(
			modelClass -> getResourceOptional(modelClass).isPresent()
		).ifPresent(
			this::_addModelClassMaps
		);
	}

	private <T, U extends Identifier> void _addModelClassMaps(
		Class<T> modelClass) {

		Optional<Resource<T, U>> optional = getResourceOptional(modelClass);

		optional.ifPresent(
			resource -> {
				_classes.put(resource.getPath(), modelClass);

				RepresentorImpl representor =
					(RepresentorImpl)resource.buildRepresentor(
						new RepresentorBuilderImpl<>(
							resource.getPath(),
							_addRelatedCollectionTriConsumer(modelClass)));

				_representors.put(modelClass.getName(), representor);

				Function<HttpServletRequest, Routes<?>> routesFunction =
					_getRoutesFunction(
						modelClass, _getIdentifierClass(resource), resource);

				_routesFunctions.put(resource.getPath(), routesFunction);
			});
	}

	private <T> TriConsumer<String, Class<?>, Function<Object, Identifier>>
		_addRelatedCollectionTriConsumer(Class<T> relatedModelClass) {

		return (key, modelClass, identifierFunction) -> {
			List<RelatedCollection<?, ?>> relatedCollections =
				_relatedCollections.computeIfAbsent(
					modelClass.getName(), className -> new ArrayList<>());

			relatedCollections.add(
				new RelatedCollection<>(
					key, relatedModelClass,
					object -> {
						Identifier identifier = identifierFunction.apply(
							object);

						Optional<String> optional = getPathOptional(modelClass);

						return new Identifier() {

							@Override
							public String getId() {
								return identifier.getId();
							}

							@Override
							public String getType() {
								return optional.orElse("");
							}

						};
					}));
		};
	}

	private <T, U extends Identifier> Class<U> _getIdentifierClass(
		Resource<T, U> resource) {

		Class<? extends Resource> resourceClass = resource.getClass();

		Try<Class<U>> classTry = GenericUtil.getGenericClassTry(
			resourceClass, Resource.class, 1);

		return classTry.orElseThrow(
			() -> new VulcanDeveloperError.MustHaveValidGenericType(
				resourceClass));
	}

	private Function<Class<?>, Optional<?>> _getProvideClassFunction(
		HttpServletRequest httpServletRequest) {

		return clazz -> _providerManager.provide(clazz, httpServletRequest);
	}

	private <T, U extends Identifier> Function<HttpServletRequest, Routes<?>>
		_getRoutesFunction(
			Class<T> modelClass, Class<U> identifierClass,
			Resource<T, U> resource) {

		return httpServletRequest -> {
			RoutesBuilderImpl<T, U> routesBuilder = new RoutesBuilderImpl<>(
				modelClass, identifierClass,
				_getProvideClassFunction(httpServletRequest),
				_identifierConverterManager::convert);

			return resource.routes(routesBuilder);
		};
	}

	private <T> void _removeModelClassMaps(Class<T> modelClass) {
		Collection<Class<?>> classes = _classes.values();

		classes.removeIf(next -> next.equals(modelClass));

		_relatedCollections.forEach(
			(className, relatedCollections) -> relatedCollections.removeIf(
				relatedCollection ->
					relatedCollection.getModelClass().equals(modelClass)));
		_representors.remove(modelClass.getName());
	}

	private final Map<String, Class<?>> _classes = new ConcurrentHashMap<>();

	@Reference
	private IdentifierConverterManager _identifierConverterManager;

	@Reference
	private ProviderManager _providerManager;

	private final Map<String, List<RelatedCollection<?, ?>>>
		_relatedCollections = new ConcurrentHashMap<>();
	private final Map<String, RepresentorImpl> _representors =
		new ConcurrentHashMap<>();
	private final Map<String, Function<HttpServletRequest, Routes<?>>>
		_routesFunctions = new ConcurrentHashMap<>();

}