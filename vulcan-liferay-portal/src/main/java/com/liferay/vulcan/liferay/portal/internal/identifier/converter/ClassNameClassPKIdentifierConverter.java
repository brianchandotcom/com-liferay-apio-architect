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

package com.liferay.vulcan.liferay.portal.internal.identifier.converter;

import com.liferay.vulcan.identifier.Identifier;
import com.liferay.vulcan.identifier.converter.IdentifierConverter;
import com.liferay.vulcan.liferay.portal.identifier.ClassNameClassPKIdentifier;
import com.liferay.vulcan.wiring.osgi.manager.ResourceManager;

import java.util.Optional;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * This converter can be used to convert from a generic identifier to a {@link
 * ClassNameClassPKIdentifier}
 *
 * <p>
 * The class {@link ClassNameClassPKIdentifier} can then be provided as a
 * parameter in {@link com.liferay.vulcan.resource.builder.RoutesBuilder}
 * methods.
 * </p>
 *
 * <p>
 * The class name is extracted from the type obtained by calling {@link
 * Identifier#getType()} and getting the <code>Resource's</code> class name
 * matching the <code>type</code> as its path. If no matching resource is found,
 * throws an exception.
 * </p>
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class ClassNameClassPKIdentifierConverter
	implements IdentifierConverter<ClassNameClassPKIdentifier> {

	@Override
	public ClassNameClassPKIdentifier convert(Identifier identifier) {
		return new ClassNameClassPKIdentifier() {

			@Override
			public String getClassName() {
				String className = _resourceManager.getClassName(getType());

				Optional<String> optional = Optional.ofNullable(className);

				return optional.orElseThrow(
					() -> new BadRequestException(
						"Unable to find a resource with type " + getType()));
			}

			@Override
			public String getId() {
				return identifier.getId();
			}

			@Override
			public String getType() {
				return identifier.getType();
			}

		};
	}

	@Reference
	private ResourceManager _resourceManager;

}