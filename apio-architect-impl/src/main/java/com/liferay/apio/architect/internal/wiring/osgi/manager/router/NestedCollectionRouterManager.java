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

package com.liferay.apio.architect.internal.wiring.osgi.manager.router;

import static com.liferay.apio.architect.internal.wiring.osgi.manager.TypeArgumentProperties.KEY_PARENT_IDENTIFIER_CLASS;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.TypeArgumentProperties.KEY_PRINCIPAL_TYPE_ARGUMENT;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.util.ManagerUtil.getGenericClassFromProperty;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.util.ManagerUtil.getTypeParamTry;

import static org.slf4j.LoggerFactory.getLogger;

import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;
import com.liferay.apio.architect.internal.routes.NestedCollectionRoutesImpl;
import com.liferay.apio.architect.internal.routes.NestedCollectionRoutesImpl.BuilderImpl;
import com.liferay.apio.architect.internal.wiring.osgi.manager.base.ClassNameBaseManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.NameManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.RepresentableManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper.PathIdentifierMapperManager;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.router.NestedCollectionRouter;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes.Builder;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper.Emitter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;

/**
 * Provides methods to retrieve the routes information provided by the different
 * {@link NestedCollectionRouter} instances.
 *
 * @author Alejandro Hernández
 * @see    NestedCollectionRouter
 */
@Component(service = NestedCollectionRouterManager.class)
public class NestedCollectionRouterManager
	extends ClassNameBaseManager<NestedCollectionRouter> {

	public NestedCollectionRouterManager() {
		super(NestedCollectionRouter.class, 2);
	}

	/**
	 * Returns the list of {@link ActionSemantics} created by the managed
	 * routers.
	 *
	 * @review
	 */
	public Stream<ActionSemantics> getActionSemantics() {
		return Optional.ofNullable(
			INSTANCE.getNestedCollectionRoutesMap(
				this::_computeNestedCollectionRoutes)
		).map(
			Map::values
		).map(
			Collection::stream
		).orElseGet(
			Stream::empty
		).map(
			NestedCollectionRoutesImpl.class::cast
		).map(
			NestedCollectionRoutesImpl::getActionSemantics
		).flatMap(
			Collection::stream
		);
	}

	protected void emit(
		ServiceReference<NestedCollectionRouter> serviceReference,
		Emitter<String> emitter) {

		NestedCollectionRouter nestedCollectionRouter =
			bundleContext.getService(serviceReference);

		if (nestedCollectionRouter == null) {
			return;
		}

		Try<Class<Object>> identifierClassTry = getGenericClassFromProperty(
			serviceReference, KEY_PRINCIPAL_TYPE_ARGUMENT);

		Try<Class<Object>> parentClassTry = getGenericClassFromProperty(
			serviceReference, KEY_PARENT_IDENTIFIER_CLASS);

		identifierClassTry.recoverWith(
			__ -> getTypeParamTry(
				nestedCollectionRouter, NestedCollectionRouter.class, 2)
		).map(
			Class::getName
		).flatMap(
			identifierClassName -> parentClassTry.recoverWith(
				__ -> getTypeParamTry(
					nestedCollectionRouter, NestedCollectionRouter.class, 4)
			).map(
				Class::getName
			).map(
				parentClassName -> parentClassName + "-" + identifierClassName
			)
		).voidFold(
			__ -> _logger.warn(
				"Unable to get generic information from {}",
				nestedCollectionRouter.getClass()),
			emitter::emit
		);
	}

	private void _computeNestedCollectionRoutes() {
		forEachService(
			(key, nestedCollectionRouter) -> {
				String[] classNames = key.split("-");

				if (classNames.length != 2) {
					return;
				}

				String parentClassName = classNames[0];
				String nestedClassName = classNames[1];

				Optional<String> nameOptional = _nameManager.getNameOptional(
					parentClassName);

				if (!nameOptional.isPresent()) {
					_logger.warn(
						"Unable to find a Representable for parent class " +
							"name {}",
						parentClassName);

					return;
				}

				String parentName = nameOptional.get();

				Optional<String> nestedNameOptional =
					_nameManager.getNameOptional(nestedClassName);

				if (!nestedNameOptional.isPresent()) {
					_logger.warn(
						"Unable to find a Representable for nested class " +
							"name {}",
						nestedClassName);

					return;
				}

				String nestedName = nestedNameOptional.get();

				Optional<Representor<Object>> representorOptional =
					_representableManager.getRepresentorOptional(nestedName);

				if (!representorOptional.isPresent()) {
					_logger.warn(
						"Unable to find a Representable for nested class " +
							"name " + nestedClassName);

					return;
				}

				Representor<Object> representor = representorOptional.get();

				Builder builder = new BuilderImpl<>(
					Nested.of(Item.of(parentName), nestedName),
					identifier -> _pathIdentifierMapperManager.mapToPath(
						parentName, identifier),
					representor::getIdentifier, _nameManager::getNameOptional);

				@SuppressWarnings("unchecked")
				NestedCollectionRoutes nestedCollectionRoutes =
					nestedCollectionRouter.collectionRoutes(builder);

				INSTANCE.putNestedCollectionRoutes(
					parentName + "-" + nestedName, nestedCollectionRoutes);
			});
	}

	private Logger _logger = getLogger(getClass());

	@Reference
	private NameManager _nameManager;

	@Reference
	private PathIdentifierMapperManager _pathIdentifierMapperManager;

	@Reference
	private RepresentableManager _representableManager;

}