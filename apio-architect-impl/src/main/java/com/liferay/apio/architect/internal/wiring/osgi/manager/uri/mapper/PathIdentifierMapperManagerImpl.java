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

import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import static io.leangen.geantyref.GenericTypeReflector.getTypeParameter;

import com.liferay.apio.architect.identifier.Identifier;
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
		Optional<PathIdentifierMapper<Object>> optional =
			_getPathIdentifierMapperOptional(name);

		return optional.isPresent();
	}

	@Override
	public <T> T mapToIdentifierOrFail(Path path) {
		Optional<PathIdentifierMapper<T>> optional =
			_getPathIdentifierMapperOptional(path.getName());

		return optional.map(
			pathIdentifierMapper -> pathIdentifierMapper.map(path)
		).orElseThrow(
			() -> new MustHavePathIdentifierMapper(path)
		);
	}

	@Override
	public <T> Optional<Path> mapToPath(String name, T identifier) {
		Optional<PathIdentifierMapper<Object>> optional =
			_getPathIdentifierMapperOptional(name);

		return optional.map(
			pathIdentifierMapper -> pathIdentifierMapper.map(name, identifier));
	}

	@SuppressWarnings("unchecked")
	private <T> Optional<PathIdentifierMapper<T>>
		_getPathIdentifierMapperOptional(String name) {

		Optional<Class<Identifier>> identifierClassOptional =
			_identifierClassManager.getIdentifierClassOptional(name);

		Optional<Class<?>> genericParentClassOptional =
			INSTANCE.getReusableIdentifierClassOptional(name);

		return identifierClassOptional.map(
			clazz -> getTypeParameter(
				clazz, Identifier.class.getTypeParameters()[0])
		).filter(
			Class.class::isInstance
		).map(
			type -> (Class)type
		).flatMap(
			this::getServiceOptional
		).map(
			Optional::of
		).orElseGet(
			() -> genericParentClassOptional.flatMap(this::getServiceOptional)
		).map(
			service -> (PathIdentifierMapper<T>)service
		);
	}

	@Reference
	private IdentifierClassManager _identifierClassManager;

}