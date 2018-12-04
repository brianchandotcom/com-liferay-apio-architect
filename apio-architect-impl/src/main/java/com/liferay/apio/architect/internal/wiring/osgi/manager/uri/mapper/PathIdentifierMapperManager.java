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

import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import static io.leangen.geantyref.GenericTypeReflector.getTypeParameter;

import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.wiring.osgi.error.ApioDeveloperError.MustHavePathIdentifierMapper;
import com.liferay.apio.architect.internal.wiring.osgi.manager.base.ClassNameBaseManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.IdentifierClassManager;
import com.liferay.apio.architect.uri.Path;
import com.liferay.apio.architect.uri.mapper.PathIdentifierMapper;

import io.vavr.control.Try;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides methods to map a {@code Path} to an identifier, and vice versa.
 *
 * @author Alejandro Hernández
 */
@Component(service = PathIdentifierMapperManager.class)
public class PathIdentifierMapperManager
	extends ClassNameBaseManager<PathIdentifierMapper> {

	public PathIdentifierMapperManager() {
		super(PathIdentifierMapper.class, 0);
	}

	/**
	 * Converts a {@code Path} to its equivalent identifier of type {@code T},
	 * if a valid {@link PathIdentifierMapper} can be found; throws a {@code
	 * MustHavePathIdentifierMapper} exception otherwise.
	 *
	 * @param  path the {@code Path}
	 * @return the identifier
	 */
	public <T> T mapToIdentifierOrFail(Path path) {
		Optional<Class<Identifier>> identifierClassOptional =
			_identifierClassManager.getIdentifierClassOptional(path.getName());

		Optional<PathIdentifierMapper<T>> pathIdentifierMapperOptional =
			identifierClassOptional.map(
				clazz -> getTypeParameter(
					clazz, Identifier.class.getTypeParameters()[0])
			).filter(
				Class.class::isInstance
			).map(
				type -> (Class)type
			).flatMap(
				clazz -> getServiceOptional(clazz)
			);

		return Try.of(
			pathIdentifierMapperOptional::get
		).map(
			pathIdentifierMapper -> pathIdentifierMapper.map(path)
		).toJavaOptional(
		).orElseGet(
			() -> _getGenericParentIdentifierOptional(path)
		);
	}

	/**
	 * Converts an identifier to its equivalent {@code Path}, if a valid {@code
	 * PathIdentifierMapper} can be found. Returns {@code Optional#empty()}
	 * otherwise.
	 *
	 * @param  name the resource's name
	 * @param  identifier the identifier
	 * @return the {@code Path}, if a valid {@code PathIdentifierMapper} is
	 *         present; {@code Optional#empty()} otherwise
	 */
	@SuppressWarnings("unchecked")
	public <T> Optional<Path> mapToPath(String name, T identifier) {
		Optional<Class<Identifier>> identifierClassOptional =
			_identifierClassManager.getIdentifierClassOptional(name);

		Optional<PathIdentifierMapper<T>> pathIdentifierMapperOptional =
			identifierClassOptional.map(
				clazz -> getTypeParameter(
					clazz, Identifier.class.getTypeParameters()[0])
			).filter(
				Class.class::isInstance
			).map(
				type -> (Class)type
			).flatMap(
				clazz -> getServiceOptional(clazz)
			);

		return Try.of(
			pathIdentifierMapperOptional::get
		).map(
			pathIdentifierMapper -> pathIdentifierMapper.map(name, identifier)
		).map(
			Optional::of
		).toJavaOptional(
		).orElseGet(
			() -> _getGenericParentPathOptional(name, identifier)
		);
	}

	private <T> T _getGenericParentIdentifierOptional(Path path) {
		Optional<Class<?>> genericParentClassOptional =
			INSTANCE.getReusableIdentifierClassOptional(path.getName());

		if (!genericParentClassOptional.isPresent()) {
			throw new MustHavePathIdentifierMapper(path);
		}

		Class<?> genericParentClass = genericParentClassOptional.get();

		Optional<PathIdentifierMapper> pathIdentifierMapperOptional =
			getServiceOptional(genericParentClass);

		return pathIdentifierMapperOptional.map(
			service -> (PathIdentifierMapper<T>)service
		).map(
			pathIdentifierMapper -> pathIdentifierMapper.map(path)
		).orElseThrow(
			() -> new MustHavePathIdentifierMapper(path)
		);
	}

	private <T> Optional<Path> _getGenericParentPathOptional(
		String name, T identifier) {

		Optional<Class<?>> genericParentClassOptional =
			INSTANCE.getReusableIdentifierClassOptional(name);

		if (!genericParentClassOptional.isPresent()) {
			return Optional.empty();
		}

		Class<?> genericParentClass = genericParentClassOptional.get();

		Optional<PathIdentifierMapper> pathIdentifierMapperOptional =
			getServiceOptional(genericParentClass);

		return pathIdentifierMapperOptional.map(
			service -> (PathIdentifierMapper<T>)service
		).map(
			pathIdentifierMapper -> pathIdentifierMapper.map(name, identifier)
		).map(
			path -> new Path(
				toLowercaseSlug(genericParentClass.getSimpleName()),
				path.getId())
		);
	}

	@Reference
	private IdentifierClassManager _identifierClassManager;

}