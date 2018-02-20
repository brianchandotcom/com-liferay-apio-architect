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

package com.liferay.apio.architect.wiring.osgi.internal.manager.router;

import static com.liferay.apio.architect.alias.ProvideFunction.curry;
import static com.liferay.apio.architect.unsafe.Unsafe.unsafeCast;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.TypeArgumentProperties.KEY_PARENT_IDENTIFIER_CLASS;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.TypeArgumentProperties.KEY_PRINCIPAL_TYPE_ARGUMENT;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.getGenericClassFromPropertyOrElse;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.getNameOrFail;
import static com.liferay.apio.architect.wiring.osgi.internal.manager.util.ManagerUtil.getTypeParamOrFail;

import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.router.NestedCollectionRouter;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes.Builder;
import com.liferay.apio.architect.unsafe.Unsafe;
import com.liferay.apio.architect.wiring.osgi.internal.manager.base.SimpleBaseManager;
import com.liferay.apio.architect.wiring.osgi.internal.service.reference.mapper.CustomServiceReferenceMapper;
import com.liferay.apio.architect.wiring.osgi.manager.ProviderManager;
import com.liferay.apio.architect.wiring.osgi.manager.representable.IdentifierClassManager;
import com.liferay.apio.architect.wiring.osgi.manager.representable.NameManager;
import com.liferay.apio.architect.wiring.osgi.manager.router.NestedCollectionRouterManager;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper.Emitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class NestedCollectionRouterManagerImpl
	extends SimpleBaseManager<NestedCollectionRouter>
	implements NestedCollectionRouterManager {

	public NestedCollectionRouterManagerImpl() {
		super(NestedCollectionRouter.class);
	}

	@Override
	public <T, S> Optional<NestedCollectionRoutes<T, S>>
		getNestedCollectionRoutesOptional(String name, String nestedName) {

		Optional<Class<Identifier>> optional1 =
			_identifierClassManager.getIdentifierClassOptional(name);

		Optional<Class<Identifier>> optional2 =
			_identifierClassManager.getIdentifierClassOptional(nestedName);

		return optional1.flatMap(
			class1 -> optional2.map(
				class2 -> _nestedCollectionRoutes.computeIfAbsent(
					_getKey(class1, class2),
					key -> _computeNestedCollectionRoutes(class1, class2, key)))
		).map(
			Unsafe::unsafeCast
		);
	}

	@Override
	protected void emit(
		ServiceReference<NestedCollectionRouter> serviceReference,
		Emitter<String> emitter) {

		Bundle bundle = FrameworkUtil.getBundle(
			NestedCollectionRouterManagerImpl.class);

		BundleContext bundleContext = bundle.getBundleContext();

		CustomServiceReferenceMapper<NestedCollectionRouter>
			customServiceReferenceMapper = new CustomServiceReferenceMapper<>(
				bundleContext, NestedCollectionRouter.class, 1);

		NestedCollectionRouter nestedCollectionRouter =
			bundleContext.getService(serviceReference);

		Class<?> clazz = _getParentIdentifierClass(
			serviceReference, nestedCollectionRouter);

		customServiceReferenceMapper.map(
			serviceReference, key -> emitter.emit(clazz.getName() + "-" + key));
	}

	@Override
	protected void onRemovedService(
		ServiceReference<NestedCollectionRouter> serviceReference,
		NestedCollectionRouter nestedCollectionRouter) {

		Class<?> identifierClass = getGenericClassFromPropertyOrElse(
			serviceReference, KEY_PRINCIPAL_TYPE_ARGUMENT,
			() -> getTypeParamOrFail(
				nestedCollectionRouter, NestedCollectionRouter.class, 1));

		Class<?> parentIdentifierClass = _getParentIdentifierClass(
			serviceReference, nestedCollectionRouter);

		_nestedCollectionRoutes.remove(
			_getKey(identifierClass, parentIdentifierClass));

		super.onRemovedService(serviceReference, nestedCollectionRouter);
	}

	private static String _getKey(
		Class<?> identifierClass, Class<?> parentIdentifierClass) {

		return identifierClass.getName() + "-" +
			parentIdentifierClass.getName();
	}

	private NestedCollectionRoutes _computeNestedCollectionRoutes(
		Class<Identifier> parentClass, Class<Identifier> nestedClass,
		String key) {

		Optional<NestedCollectionRouter> optional = getServiceOptional(key);

		String name = getNameOrFail(parentClass, _nameManager);

		String nestedName = getNameOrFail(nestedClass, _nameManager);

		return optional.map(
			nestedCollectionRouter -> _getNestedCollectionRoutes(
				unsafeCast(nestedCollectionRouter), name, nestedName)
		).orElse(
			null
		);
	}

	private <T, S, U extends Identifier<S>> NestedCollectionRoutes<T, S>
		_getNestedCollectionRoutes(
			NestedCollectionRouter<T, ?, S, U> nestedCollectionRouter,
			String name, String nestedName) {

		Builder<T, S> builder = new Builder<>(
			name, nestedName, curry(_providerManager::provideMandatory));

		return nestedCollectionRouter.collectionRoutes(builder);
	}

	private Class<?> _getParentIdentifierClass(
		ServiceReference<NestedCollectionRouter> serviceReference,
		NestedCollectionRouter nestedCollectionRouter) {

		return getGenericClassFromPropertyOrElse(
			serviceReference, KEY_PARENT_IDENTIFIER_CLASS,
			() -> getTypeParamOrFail(
				nestedCollectionRouter, NestedCollectionRouter.class, 3));
	}

	@Reference
	private IdentifierClassManager _identifierClassManager;

	@Reference
	private NameManager _nameManager;

	private Map<String, NestedCollectionRoutes<?, ?>> _nestedCollectionRoutes =
		new ConcurrentHashMap<>();

	@Reference
	private ProviderManager _providerManager;

}