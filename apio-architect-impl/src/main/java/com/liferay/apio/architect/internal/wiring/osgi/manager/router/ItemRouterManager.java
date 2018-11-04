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

import static org.slf4j.LoggerFactory.getLogger;

import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.form.FormImpl;
import com.liferay.apio.architect.internal.routes.ItemRoutesImpl;
import com.liferay.apio.architect.internal.routes.ItemRoutesImpl.BuilderImpl;
import com.liferay.apio.architect.internal.wiring.osgi.manager.base.ClassNameBaseManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.NameManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper.PathIdentifierMapperManager;
import com.liferay.apio.architect.router.ItemRouter;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.ItemRoutes.Builder;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;

/**
 * Provides methods to retrieve the routes information provided by the different
 * {@link ItemRouter} instances.
 *
 * @author Alejandro Hernández
 * @see    ItemRouter
 */
@Component(service = ItemRouterManager.class)
public class ItemRouterManager extends ClassNameBaseManager<ItemRouter> {

	public ItemRouterManager() {
		super(ItemRouter.class, 2);
	}

	/**
	 * Returns the list of {@link ActionSemantics} created by the managed
	 * routers.
	 *
	 * @review
	 */
	public Stream<ActionSemantics> getActionSemantics() {
		return Optional.ofNullable(
			INSTANCE.getItemRoutesMap(this::_computeItemRoutes)
		).map(
			Map::values
		).map(
			Collection::stream
		).orElseGet(
			Stream::empty
		).map(
			ItemRoutesImpl.class::cast
		).map(
			ItemRoutesImpl::getActionSemantics
		).flatMap(
			Collection::stream
		);
	}

	private void _computeItemRoutes() {
		forEachService(
			(className, itemRouter) -> {
				Optional<String> nameOptional = _nameManager.getNameOptional(
					className);

				if (!nameOptional.isPresent()) {
					_logger.warn(
						"Unable to find a Representable for class name {}",
						className);

					return;
				}

				String name = nameOptional.get();

				boolean hasPathIdentifierMapper =
					_pathIdentifierMapperManager.hasPathIdentifierMapper(name);

				if (!hasPathIdentifierMapper) {
					_logger.warn(
						"Missing path identifier mapper for resource with " +
							"name {}",
						name);

					return;
				}

				Builder<Object, Object> builder = new BuilderImpl<>(
					Item.of(name),
					() -> new FormImpl.BuilderImpl<>(
						_pathIdentifierMapperManager::mapToIdentifierOrFail,
						_nameManager::getNameOptional),
					_nameManager::getNameOptional);

				@SuppressWarnings("unchecked")
				ItemRoutes itemRoutes = itemRouter.itemRoutes(builder);

				INSTANCE.putItemRoutes(name, itemRoutes);
			});
	}

	private Logger _logger = getLogger(getClass());

	@Reference
	private NameManager _nameManager;

	@Reference
	private PathIdentifierMapperManager _pathIdentifierMapperManager;

}