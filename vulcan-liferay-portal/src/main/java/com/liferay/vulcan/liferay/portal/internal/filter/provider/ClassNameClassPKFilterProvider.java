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

package com.liferay.vulcan.liferay.portal.internal.filter.provider;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.filter.FilterProvider;
import com.liferay.vulcan.liferay.portal.filter.ClassNameClassPKFilter;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.wiring.osgi.manager.ResourceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Allows resources to provide {@link ClassNameClassPKFilter} in {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder}'s
 * <code>filteredCollectionPage</code> methods.
 *
 * As well as some utility methods for getting a filter's query param map, or a
 * filter's name.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class ClassNameClassPKFilterProvider
	implements FilterProvider<ClassNameClassPKFilter> {

	@Override
	public String getFilterName() {
		return "pathClassPK";
	}

	@Override
	public Map<String, String> getQueryParamMap(
		ClassNameClassPKFilter queryParamFilterType) {

		Optional<Resource<Object>> optional =
			_resourceManager.getResourceOptional(
				queryParamFilterType.getClassName());

		Resource resource = optional.orElseThrow(NotFoundException::new);

		return new HashMap<String, String>() {
			{
				put("path", resource.getPath());
				put(
					"classPK",
					String.valueOf(queryParamFilterType.getClassPK()));
			}
		};
	}

	@Override
	public ClassNameClassPKFilter provide(
		HttpServletRequest httpServletRequest) {

		String classPKString = httpServletRequest.getParameter("classPK");

		Long classPK = GetterUtil.getLong(classPKString);

		String path = httpServletRequest.getParameter("path");

		if ((path == null) || (classPK == GetterUtil.DEFAULT_LONG)) {
			throw new BadRequestException();
		}

		String className = _resourceManager.getClassName(path);

		return new ClassNameClassPKFilter(className, classPK);
	}

	@Reference
	private ResourceManager _resourceManager;

}