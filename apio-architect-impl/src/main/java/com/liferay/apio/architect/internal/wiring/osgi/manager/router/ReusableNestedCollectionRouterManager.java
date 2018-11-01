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

import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;
import static com.liferay.apio.architect.internal.wiring.osgi.util.GenericUtil.getGenericTypeArgumentTry;

import static org.slf4j.LoggerFactory.getLogger;

import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;
import com.liferay.apio.architect.internal.routes.NestedCollectionRoutesImpl;
import com.liferay.apio.architect.internal.wiring.osgi.manager.base.ClassNameBaseManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.NameManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.RepresentableManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper.PathIdentifierMapperManager;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.router.ReusableNestedCollectionRouter;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes.Builder;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;

/**
 * Provides methods to retrieve the route information provided by the different
 * {@code ReusableNestedCollectionRouter} instances.
 *
 * @author Alejandro Hernández
 * @see    ReusableNestedCollectionRouter
 */
@Component(service = ReusableNestedCollectionRouterManager.class)
public class ReusableNestedCollectionRouterManager
	extends ClassNameBaseManager<ReusableNestedCollectionRouter> {

	public ReusableNestedCollectionRouterManager() {
		super(ReusableNestedCollectionRouter.class, 2);
	}

	/**
	 * Returns the list of {@link ActionSemantics} created by the managed
	 * routers.
	 *
	 * @review
	 */
	public Stream<ActionSemantics> getActionSemantics() {
		return Optional.ofNullable(
			INSTANCE.getReusableCollectionRoutesMap(
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

	private void _computeNestedCollectionRoutes() {
		forEachService(
			(className, reusableNestedCollectionRouter) -> {
				Optional<String> nameOptional = _nameManager.getNameOptional(
					className);

				if (!nameOptional.isPresent()) {
					if (_logger.isWarnEnabled()) {
						_logger.warn(
							"Unable to find a Representable for class name " +
								className);
					}

					return;
				}

				String name = nameOptional.get();

				Optional<Representor<Object>> representorOptional =
					_representableManager.getRepresentorOptional(name);

				if (!representorOptional.isPresent()) {
					_logger.warn(
						"Unable to find a Representable for nested class " +
							"name " + className);

					return;
				}

				Representor<Object> representor = representorOptional.get();

				Builder builder = new NestedCollectionRoutesImpl.BuilderImpl<>(
					Nested.of(Item.of("r"), name),
					identifier -> _pathIdentifierMapperManager.mapToPath(
						name, identifier),
					representor::getIdentifier, _nameManager::getNameOptional);

				@SuppressWarnings("unchecked")
				NestedCollectionRoutes nestedCollectionRoutes =
					reusableNestedCollectionRouter.collectionRoutes(builder);

				INSTANCE.putReusableNestedCollectionRoutes(
					name, nestedCollectionRoutes);

				getGenericTypeArgumentTry(
					reusableNestedCollectionRouter.getClass(),
					ReusableNestedCollectionRouter.class, 3
				).ifSuccess(
					objectClass -> INSTANCE.putReusableIdentifierClass(
						name, objectClass)
				);
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