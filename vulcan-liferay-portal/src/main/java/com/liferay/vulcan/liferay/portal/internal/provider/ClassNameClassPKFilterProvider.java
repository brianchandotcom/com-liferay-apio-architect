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

package com.liferay.vulcan.liferay.portal.internal.provider;

import static com.liferay.vulcan.liferay.portal.filter.ClassNameClassPKFilter.CLASS_NAME;
import static com.liferay.vulcan.liferay.portal.filter.ClassNameClassPKFilter.CLASS_PK;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.liferay.portal.filter.ClassNameClassPKFilter;
import com.liferay.vulcan.provider.Provider;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;

/**
 * Allows resources to provide {@link ClassNameClassPKFilter} in {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder}'s
 * <code>filteredCollectionPage</code> methods.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class ClassNameClassPKFilterProvider
	implements Provider<ClassNameClassPKFilter> {

	@Override
	public ClassNameClassPKFilter createContext(
		HttpServletRequest httpServletRequest) {

		String className = httpServletRequest.getParameter(CLASS_NAME);

		String classPKString = httpServletRequest.getParameter(CLASS_PK);

		Long classPK = GetterUtil.getLong(classPKString);

		if ((className == null) || (classPK == GetterUtil.DEFAULT_LONG)) {
			throw new BadRequestException();
		}

		return new ClassNameClassPKFilter(className, classPK);
	}

}