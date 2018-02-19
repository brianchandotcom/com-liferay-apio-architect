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

package com.liferay.apio.architect.wiring.osgi.internal.manager.representable;

import static com.liferay.apio.architect.unsafe.Unsafe.unsafeCast;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.TypeArgumentProperties.KEY_PRINCIPAL_TYPE_ARGUMENT;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.getGenericClassFromPropertyOrElse;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.getTypeParamOrFail;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.logger.ApioLogger;
import com.liferay.apio.architect.related.RelatedCollection;
import com.liferay.apio.architect.representor.Representable;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.wiring.osgi.manager.representable.IdentifierClassManager;
import com.liferay.apio.architect.wiring.osgi.manager.representable.NameManager;
import com.liferay.apio.architect.wiring.osgi.manager.representable.RepresentableManager;
import com.liferay.osgi.service.tracker.collections.ServiceReferenceServiceTuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class RepresentableManagerImpl
	implements NameManager, IdentifierClassManager, RepresentableManager {

	@Override
	public <T extends Identifier> Optional<Class<T>> getIdentifierClassOptional(
		String name) {

		if (_identifierClasses == null) {
			_computeMaps();
		}

		return Optional.ofNullable(unsafeCast(_identifierClasses.get(name)));
	}

	@Override
	public Optional<String> getNameOptional(String className) {
		if (_names == null) {
			_computeMaps();
		}

		return Optional.ofNullable(_names.get(className));
	}

	@Override
	public <T, U> Optional<Representor<T, U>> getRepresentorOptional(
		String name) {

		if (_representors == null) {
			_computeMaps();
		}

		return Optional.ofNullable(unsafeCast(_representors.get(name)));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Reference(cardinality = MULTIPLE, policyOption = GREEDY, unbind = "-")
	protected void setServiceReference(
		ServiceReference<Representable> serviceReference) {

		_serviceReferences.add(serviceReference);
	}

	private void _computeMaps() {
		Map<Class<Identifier>, TreeSet<ServiceReferenceServiceTuple
			<Representable, Representable>>> map = new HashMap<>();

		for (ServiceReference<Representable> serviceReference :
				_serviceReferences) {

			Representable representable = _bundleContext.getService(
				serviceReference);

			Class<Identifier> clazz = getGenericClassFromPropertyOrElse(
				serviceReference, KEY_PRINCIPAL_TYPE_ARGUMENT,
				() -> getTypeParamOrFail(
					representable, Representable.class, 2));

			TreeSet<ServiceReferenceServiceTuple<Representable, Representable>>
				serviceReferenceServiceTuples = map.computeIfAbsent(
					clazz, name -> new TreeSet<>());

			ServiceReferenceServiceTuple<Representable, Representable>
				serviceReferenceServiceTuple =
					new ServiceReferenceServiceTuple<>(
						serviceReference, representable);

			serviceReferenceServiceTuples.add(serviceReferenceServiceTuple);
		}

		_names = new HashMap<>();
		_identifierClasses = new HashMap<>();
		_representors = new HashMap<>();

		map.forEach(
			(clazz, treeSet) -> {
				ServiceReferenceServiceTuple<Representable, Representable>
					serviceReferenceServiceTuple = treeSet.first();

				Representable representable =
					serviceReferenceServiceTuple.getService();

				String name = representable.getName();

				Set<Map.Entry<String, String>> entries = _names.entrySet();

				Stream<Map.Entry<String, String>> stream = entries.stream();

				Optional<String> optional = stream.filter(
					entry -> Objects.equals(entry.getValue(), name)
				).map(
					Map.Entry::getKey
				).findFirst();

				if (optional.isPresent()) {
					StringBuilder stringBuilder = new StringBuilder();

					String message = stringBuilder.append(
						"Representable registered under "
					).append(
						clazz
					).append(
						" has name "
					).append(
						name
					).append(
						", but it's already in use by Representable "
					).append(
						"registered under "
					).append(
						optional.get()
					).toString();

					_apioLogger.warning(message);

					return;
				}

				_names.put(clazz.getName(), name);
				_identifierClasses.put(name, clazz);
				_representors.put(
					name,
					_getRepresentor(
						unsafeCast(representable), unsafeCast(clazz)));
			});
	}

	private <T, S, U extends Identifier<S>> Representor<T, S> _getRepresentor(
		Representable<T, S, U> representable, Class<U> clazz) {

		Supplier<List<RelatedCollection<?>>> relatedCollectionSupplier =
			() -> _relatedCollections.get(clazz.getName());

		BiConsumer<Class<?>, RelatedCollection<?>> biConsumer =
			(identifierClass, relatedCollection) -> {
				List<RelatedCollection<?>> relatedCollections =
					_relatedCollections.computeIfAbsent(
						identifierClass.getName(), __ -> new ArrayList<>());

				relatedCollections.add(relatedCollection);
			};

		Representor.Builder<T, S> builder = new Representor.Builder<>(
			clazz, biConsumer, relatedCollectionSupplier);

		return representable.representor(builder);
	}

	@Reference
	private ApioLogger _apioLogger;

	private BundleContext _bundleContext;
	private Map<String, Class<Identifier>> _identifierClasses;
	private Map<String, String> _names;
	private final Map<String, List<RelatedCollection<?>>> _relatedCollections =
		new ConcurrentHashMap<>();
	private Map<String, Representor> _representors;
	private final List<ServiceReference<Representable>> _serviceReferences =
		new ArrayList<>();

}