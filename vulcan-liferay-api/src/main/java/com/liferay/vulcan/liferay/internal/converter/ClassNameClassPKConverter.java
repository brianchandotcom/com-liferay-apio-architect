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

package com.liferay.vulcan.liferay.internal.converter;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.converter.Converter;
import com.liferay.vulcan.liferay.identifier.ClassNameClassPKIdentifier;
import com.liferay.vulcan.liferay.internal.identifier.ClassNameClassPKIdentifierImpl;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;

/**
 * Allows resources to use {@link ClassNameClassPKIdentifier} as the identifier
 * in <code>collectionItem</code> {@link
 * com.liferay.vulcan.representor.RoutesBuilder} methods.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class ClassNameClassPKConverter
	implements Converter<ClassNameClassPKIdentifier> {

	@Override
	public ClassNameClassPKIdentifier convert(String id) {
		String[] components = id.split("-");

		if (components.length == 2) {
			String className = components[0];

			String classPKString = components[1];

			Long classPK = GetterUtil.getLong(classPKString);

			if (classPK != GetterUtil.DEFAULT_LONG) {
				return new ClassNameClassPKIdentifierImpl(className, classPK);
			}
		}

		throw new BadRequestException();
	}

}