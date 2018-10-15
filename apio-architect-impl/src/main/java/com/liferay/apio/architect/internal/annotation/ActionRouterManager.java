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

package com.liferay.apio.architect.internal.annotation;

import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import static org.slf4j.LoggerFactory.getLogger;

import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.annotation.representor.ActionRouterTypeExtractor;
import com.liferay.apio.architect.internal.annotation.router.RouterTransformer;
import com.liferay.apio.architect.internal.url.ApplicationURL;
import com.liferay.apio.architect.internal.url.ServerURL;
import com.liferay.apio.architect.internal.wiring.osgi.manager.base.ClassNameBaseManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.provider.ProviderManager;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.router.ActionRouter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;

/**
 * Keeps track of all {@link ActionRouter} registered and provides a method to
 * computeActionRouters the actions from each action router.
 *
 * @author Javier Gamarra
 * @review
 */
@Component(service = ActionRouterManager.class)
public class ActionRouterManager extends ClassNameBaseManager<ActionRouter> {

	public ActionRouterManager() {
		super(ActionRouter.class, 0);
	}

	public void computeActionRouters() {
		List<String> list = _providerManager.getMissingProviders(
			_mandatoryClassNames);

		if (!list.isEmpty()) {
			_logger.warn("Missing providers for mandatory classes: {}", list);

			return;
		}

		forEachService(
			(className, actionRouter) -> {
				Try<Class<? extends Identifier>> classTry =
					ActionRouterTypeExtractor.extractTypeClass(actionRouter);

				classTry.ifSuccess(
					typeClass -> {
						Type type = typeClass.getAnnotation(Type.class);

						String name = toLowercaseSlug(type.value());

						RouterTransformer.toActionRouter(
							actionRouter, name, _actionManager);

						INSTANCE.putRootResourceName(name);
					});
			});
	}

	public List<String> getResourceNames() {
		return new ArrayList<>(
			INSTANCE.getRootResourceNames(this::computeActionRouters));
	}

	private static final List<String> _mandatoryClassNames = Arrays.asList(
		ApplicationURL.class.getName(), Credentials.class.getName(),
		Pagination.class.getName(), ServerURL.class.getName());

	@Reference
	private ActionManager _actionManager;

	private Logger _logger = getLogger(getClass());

	@Reference
	private ProviderManager _providerManager;

}