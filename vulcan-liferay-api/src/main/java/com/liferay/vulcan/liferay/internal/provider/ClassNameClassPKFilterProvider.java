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

package com.liferay.vulcan.liferay.internal.provider;

import static com.liferay.vulcan.liferay.internal.filter.ClassNameClassPKFilterImpl.CLASS_NAME;
import static com.liferay.vulcan.liferay.internal.filter.ClassNameClassPKFilterImpl.CLASS_PK;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.liferay.filter.ClassNameClassPKFilter;
import com.liferay.vulcan.liferay.internal.filter.ClassNameClassPKFilterImpl;
import com.liferay.vulcan.provider.Provider;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;

/**
 * Allows resources to provide {@link ClassNameClassPKFilter} as a parameter in
 * {@link com.liferay.vulcan.resource.builder.RoutesBuilder} methods.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class ClassNameClassPKFilterProvider
	implements Provider<ClassNameClassPKFilter> {

	@Override
	public ClassNameClassPKFilter createContext(
		HttpServletRequest httpServletRequest) {

		Map<String, String[]> parameterMap =
			httpServletRequest.getParameterMap();

		String[] classNames = parameterMap.get(CLASS_NAME);
		String[] classPKs = parameterMap.get(CLASS_PK);

		if ((classNames == null) || (classPKs == null) ||
			(classNames.length != 1) || (classPKs.length != 1)) {

			throw new BadRequestException();
		}

		String className = classNames[0];
		Long classPK = GetterUtil.getLong(classPKs[0]);

		if (classPK == GetterUtil.DEFAULT_LONG) {
			throw new BadRequestException();
		}

		return new ClassNameClassPKFilterImpl(className, classPK);
	}

}