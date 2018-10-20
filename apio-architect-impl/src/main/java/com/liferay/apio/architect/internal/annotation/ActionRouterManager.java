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
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getActionKey;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getParameters;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getProviders;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import static org.apache.commons.lang3.reflect.MethodUtils.getMethodsListWithAnnotation;

import static org.slf4j.LoggerFactory.getLogger;

import com.liferay.apio.architect.annotation.Actions.Action;
import com.liferay.apio.architect.annotation.Actions.Remove;
import com.liferay.apio.architect.annotation.Actions.Retrieve;
import com.liferay.apio.architect.annotation.EntryPoint;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.annotation.representor.ActionRouterTypeExtractor;
import com.liferay.apio.architect.internal.url.ApplicationURL;
import com.liferay.apio.architect.internal.url.ServerURL;
import com.liferay.apio.architect.internal.wiring.osgi.manager.base.ClassNameBaseManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.provider.ProviderManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.router.CollectionRouterManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.router.ItemRouterManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.router.NestedCollectionRouterManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.router.ReusableNestedCollectionRouterManager;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.router.ActionRouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

	public List<String> getResourceNames() {
		return INSTANCE.getRootResourceNames();
	}

	public void initializeRouterManagers() {
		_computeActionRouters();
		_itemRouterManager.getItemRoutes();
		_reusableNestedCollectionRouterManager.getReusableCollectionRoutes();
		_nestedCollectionRouterManager.getNestedCollectionRoutes();
		_collectionRouterManager.getCollectionRoutes();
	}

	private void _computeActionRouters() {
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

						_registerActionRouter(actionRouter, name);

						_registerEntryPoint(actionRouter, name);
					});
			});
	}

	private void _registerActionRouter(ActionRouter actionRouter, String name) {
		_annotationsToSearch.forEach(
			annotationClass -> getMethodsListWithAnnotation(
				actionRouter.getClass(), annotationClass
			).forEach(
				method -> {
					Action annotation = method.getAnnotation(Action.class);

					if (annotation == null) {
						annotation = annotationClass.getAnnotation(
							Action.class);
					}

					ActionKey actionKey = getActionKey(
						method, name, annotation.httpMethod());

					_actionManager.add(
						actionKey,
						(id, body, providers) -> method.invoke(
							actionRouter,
							getParameters(method, id, body, providers)),
						getProviders(method));
				}
			)
		);
	}

	private void _registerEntryPoint(ActionRouter actionRouter, String name) {
		Stream<Method> stream = getMethodsListWithAnnotation(
			actionRouter.getClass(), EntryPoint.class).stream();

		stream.distinct(
		).forEach(
			method -> INSTANCE.putRootResourceName(name)
		);
	}

	private static final List<Class<? extends Annotation>>
		_annotationsToSearch = Arrays.asList(
			Action.class, Retrieve.class, Remove.class);
	private static final List<String> _mandatoryClassNames = Arrays.asList(
		ApplicationURL.class.getName(), Credentials.class.getName(),
		Pagination.class.getName(), ServerURL.class.getName());

	@Reference
	private ActionManager _actionManager;

	@Reference
	private CollectionRouterManager _collectionRouterManager;

	@Reference
	private ItemRouterManager _itemRouterManager;

	private Logger _logger = getLogger(getClass());

	@Reference
	private NestedCollectionRouterManager _nestedCollectionRouterManager;

	@Reference
	private ProviderManager _providerManager;

	@Reference
	private ReusableNestedCollectionRouterManager
		_reusableNestedCollectionRouterManager;

}