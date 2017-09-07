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

import com.liferay.vulcan.alias.BinaryFunction;
import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.function.TriConsumer;
import com.liferay.vulcan.identifier.Identifier;
import com.liferay.vulcan.resource.RelatedCollection;
import com.liferay.vulcan.resource.RelatedModel;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.result.Try;
import com.liferay.vulcan.wiring.osgi.internal.resource.builder.RepresentorBuilderImpl;
import com.liferay.vulcan.wiring.osgi.internal.resource.builder.RepresentorBuilderImpl.RepresentorImpl;
import com.liferay.vulcan.wiring.osgi.internal.resource.builder.RoutesBuilderImpl;
import com.liferay.vulcan.wiring.osgi.util.GenericUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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
	 * Returns the binary resources linked to a model
	 *
	 * @param  modelClass class name indexing the binary resources
	 * @return the binary resources for the model class
	 */
	public <T, U extends Identifier> Map<String, BinaryFunction<T>>
		getBinaryFunctions(Class<T> modelClass) {

		Representor<T, U> representor = getRepresentor(modelClass);

		return representor.getBinaryFunctions();
	}

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
	 * Returns the embedded related models for the model class.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the embedded related models for the model class.
	 * @see    Resource
	 */
	public <T, U extends Identifier> List<RelatedModel<T, ?>>
		getEmbeddedRelatedModels(Class<T> modelClass) {

		Representor<T, U> representor = getRepresentor(modelClass);

		return representor.getEmbeddedRelatedModels();
	}

	/**
	 * Returns the field names and functions of the model class.
	 *
	 * <p>
	 * These functions are separated in a map with the field names as keys.
	 * </p>
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the field names and functions of the model class.
	 * @see    Resource
	 */
	public <T, U extends Identifier> Map<String, Function<T, Object>>
		getFieldFunctions(Class<T> modelClass) {

		Representor<T, U> representor = getRepresentor(modelClass);

		return representor.getFieldFunctions();
	}

	/**
	 * Returns the identifier of the model if present. Returns {@code
	 * Optional#empty()} otherwise.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @param  model the model instance.
	 * @return the identifier of the model if present; {@code Optional#empty()}
	 *         otherwise.
	 * @see    Resource
	 */
	public <T, U extends Identifier> Optional<Identifier> getIdentifierOptional(
		Class<T> modelClass, T model) {

		Representor<T, U> representor = getRepresentor(modelClass);

		Optional<Function<T, Identifier>> optional = Optional.of(
			representor::getIdentifier);

		return optional.map(
			function -> function.apply(model)
		).flatMap(
			identifier -> {
				Optional<Resource<T, Identifier>> resourceOptional =
					getResourceOptional(modelClass);

				return resourceOptional.map(
					Resource::getPath
				).map(
					path -> new Identifier() {

						@Override
						public String getId() {
							return identifier.getId();
						}

						@Override
						public String getType() {
							return path;
						}

					}
				);
			}
		);
	}

	/**
	 * Returns the linked related models for the model class.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the linked related models for the model class.
	 */
	public <T, U extends Identifier> List<RelatedModel<T, ?>>
		getLinkedRelatedModels(Class<T> modelClass) {

		Representor<T, U> representor = getRepresentor(modelClass);

		return representor.getLinkedRelatedModels();
	}

	/**
	 * Returns the links for the model class.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the links for the model class.
	 */
	public <T, U extends Identifier> Map<String, String> getLinks(
		Class<T> modelClass) {

		Representor<T, U> representor = getRepresentor(modelClass);

		return representor.getLinks();
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
	 * Returns the related collections for the model class.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the related collections for the model class.
	 */
	public <T, U extends Identifier> List<RelatedCollection<T, ?>>
		getRelatedCollections(Class<T> modelClass) {

		Representor<T, U> representor = getRepresentor(modelClass);

		List<RelatedCollection<T, ?>> relatedCollections = new ArrayList<>(
			representor.getRelatedCollections());

		relatedCollections.addAll(
			(List)_relatedCollections.get(modelClass.getName()));

		return relatedCollections;
	}

	/**
	 * Returns the representor of the model class.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the representor of the model class.
	 */
	public <T, U extends Identifier> Representor<T, U> getRepresentor(
		Class<T> modelClass) {

		return (Representor<T, U>)_representors.get(modelClass.getName());
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
	public <T> Optional<Routes<T>> getRoutes(
		String path, HttpServletRequest httpServletRequest) {

		Optional<Function<HttpServletRequest, Routes<?>>> optional =
			Optional.ofNullable(_routesFunctions.get(path));

		return optional.map(
			routesFunction -> routesFunction.apply(httpServletRequest)
		).map(
			routes -> (Routes<T>)routes
		);
	}

	/**
	 * Returns the types of the model class.
	 *
	 * @param  modelClass the model class of a {@link Resource}.
	 * @return the types of the model class.
	 */
	public <T, U extends Identifier> List<String> getTypes(
		Class<T> modelClass) {

		Representor<T, U> representor = getRepresentor(modelClass);

		return representor.getTypes();
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

						Optional<? extends Resource<?, Identifier>>
							resourceOptional = getResourceOptional(modelClass);

						String type = resourceOptional.map(
							Resource::getPath
						).orElse(
							""
						);

						return new Identifier() {

							@Override
							public String getId() {
								return identifier.getId();
							}

							@Override
							public String getType() {
								return type;
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