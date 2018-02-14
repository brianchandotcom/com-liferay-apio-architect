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

package com.liferay.apio.architect.wiring.osgi.internal.manager;

import static com.liferay.apio.architect.wiring.osgi.util.GenericUtil.getGenericTypeArgumentTry;

import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.unsafe.Unsafe;
import com.liferay.apio.architect.uri.Path;
import com.liferay.apio.architect.uri.mapper.PathIdentifierMapper;
import com.liferay.apio.architect.wiring.osgi.internal.manager.base.SimpleBaseManager;
import com.liferay.apio.architect.wiring.osgi.manager.PathIdentifierMapperManager;
import com.liferay.apio.architect.wiring.osgi.manager.representable.IdentifierClassManager;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class PathIdentifierMapperManagerImpl
	extends SimpleBaseManager<PathIdentifierMapper>
	implements PathIdentifierMapperManager {

	public PathIdentifierMapperManagerImpl() {
		super(PathIdentifierMapper.class);
	}

	@Override
	public <T> Optional<T> mapToIdentifier(Path path) {
		Optional<PathIdentifierMapper<T>> optional =
			_getPathIdentifierMapperOptional(path.getName());

		return optional.map(
			pathIdentifierMapper -> pathIdentifierMapper.map(path));
	}

	@Override
	public <T> Optional<Path> mapToPath(String name, T identifier) {
		return _getPathIdentifierMapperOptional(name).map(
			pathIdentifierMapper -> pathIdentifierMapper.map(name, identifier));
	}

	private <T> Optional<PathIdentifierMapper<T>>
		_getPathIdentifierMapperOptional(String name) {

		Optional<Class<Identifier>> optional =
			_identifierClassManager.getIdentifierClassOptional(name);

		return optional.map(
			clazz -> getGenericTypeArgumentTry(clazz, Identifier.class, 0)
		).flatMap(
			classTry -> classTry.map(
				this::getServiceOptional
			).orElseGet(
				Optional::empty
			)
		).map(
			Unsafe::unsafeCast
		);
	}

	@Reference
	private IdentifierClassManager _identifierClassManager;

}