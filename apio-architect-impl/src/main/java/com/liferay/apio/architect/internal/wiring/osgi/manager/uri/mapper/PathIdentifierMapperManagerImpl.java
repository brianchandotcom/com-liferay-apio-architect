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

package com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper;

import static com.liferay.apio.architect.internal.annotation.ActionKey.ANY_ROUTE;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;
import static com.liferay.apio.architect.internal.wiring.osgi.util.GenericUtil.getGenericTypeArgumentTry;

import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.unsafe.Unsafe;
import com.liferay.apio.architect.internal.wiring.osgi.error.ApioDeveloperError.MustHavePathIdentifierMapper;
import com.liferay.apio.architect.internal.wiring.osgi.manager.base.ClassNameBaseManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.IdentifierClassManager;
import com.liferay.apio.architect.uri.Path;
import com.liferay.apio.architect.uri.mapper.PathIdentifierMapper;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(service = PathIdentifierMapperManager.class)
public class PathIdentifierMapperManagerImpl
	extends ClassNameBaseManager<PathIdentifierMapper>
	implements PathIdentifierMapperManager {

	public PathIdentifierMapperManagerImpl() {
		super(PathIdentifierMapper.class, 0);
	}

	@Override
	public boolean hasPathIdentifierMapper(String name) {
		return _getPathIdentifierMapperTry(name).isSuccess();
	}

	@Override
	public <T> T mapToIdentifierOrFail(Path path) {
		Try<PathIdentifierMapper<T>> pathIdentifierMapperTry =
			_getPathIdentifierMapperTry(path.getName());

		return pathIdentifierMapperTry.map(
			service -> service.map(path)
		).orElseGet(
			() -> _getReusablePathIdentifierMapper(path)
		);
	}

	@Override
	public <T> Optional<Path> mapToPath(String name, T identifier) {
		Try<PathIdentifierMapper<T>> pathIdentifierMapperTry =
			_getPathIdentifierMapperTry(name);

		Path value = pathIdentifierMapperTry.map(
			service -> service.map(name, identifier)
		).orElseGet(
			() -> _getReusablePath(name, identifier)
		);

		return Optional.ofNullable(value);
	}

	private <T> Try<PathIdentifierMapper<T>> _getPathIdentifierMapperTry(
		String name) {

		return Try.success(
			name
		).mapOptional(
			_identifierClassManager::getIdentifierClassOptional
		).flatMap(
			clazz -> getGenericTypeArgumentTry(clazz, Identifier.class, 0)
		).mapOptional(
			this::getServiceOptional
		).map(
			Unsafe::unsafeCast
		);
	}

	private <T> Path _getReusablePath(String name, T identifier) {
		Optional<PathIdentifierMapper<T>> pathIdentifierMapper =
			_getReusablePathIdentifierMapper(name);

		return pathIdentifierMapper.map(
			service -> service.map(name, identifier)
		).orElse(
			null
		);
	}

	private <T> T _getReusablePathIdentifierMapper(Path path) {
		Optional<PathIdentifierMapper<T>> pathIdentifierMapper =
			_getReusablePathIdentifierMapper(path.getName());

		return pathIdentifierMapper.filter(
			__ -> path.getId() != null && !ANY_ROUTE.equals(path.getId())
		).map(
			service -> service.map(path)
		).orElseThrow(
			() -> new MustHavePathIdentifierMapper(path)
		);
	}

	private <T> Optional<PathIdentifierMapper<T>>
		_getReusablePathIdentifierMapper(String name) {

		Optional<Class<?>> reusableIdentifierClassOptional =
			INSTANCE.getReusableIdentifierClassOptional(name);

		return reusableIdentifierClassOptional.flatMap(
			this::getServiceOptional
		).map(
			Unsafe::unsafeCast
		);
	}

	@Reference
	private IdentifierClassManager _identifierClassManager;

}