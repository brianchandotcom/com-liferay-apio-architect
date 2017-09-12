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

package com.liferay.vulcan.sample.liferay.portal.internal.identifier.converter;

import com.liferay.vulcan.identifier.Identifier;
import com.liferay.vulcan.identifier.converter.IdentifierConverter;
import com.liferay.vulcan.result.Try;
import com.liferay.vulcan.sample.liferay.portal.identifier.AggregateRatingIdentifier;
import com.liferay.vulcan.wiring.osgi.manager.ResourceManager;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * This converter can be used to convert from a generic identifier to an {@link
 * AggregateRatingIdentifierConverter}
 *
 * <p>
 * The class {@link AggregateRatingIdentifierConverter} can then be provided as
 * a parameter in {@link com.liferay.vulcan.resource.builder.RoutesBuilder}
 * methods.
 * </p>
 *
 * <p>
 * The <code>className</code>/<code>classPK</code> are extracted from the
 * destructuring of the ID.
 * </p>
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class AggregateRatingIdentifierConverter
	implements IdentifierConverter<AggregateRatingIdentifier> {

	@Override
	public AggregateRatingIdentifier convert(Identifier identifier) {
		String id = identifier.getId();
		String type = identifier.getType();

		String[] components = id.split(":");

		if (components.length == 2) {
			throw new BadRequestException(
				id + " should be a string with the form 'type:classPK'");
		}

		String className = _resourceManager.getClassName(type);

		Try<Long> longTry = Try.fromFallible(() -> Long.parseLong(id));

		Long classPK = longTry.orElseThrow(
			() -> new BadRequestException(
				"Unable to convert " + id + " to a long class PK"));

		return AggregateRatingIdentifier.create(className, classPK);
	}

	@Reference
	private ResourceManager _resourceManager;

}