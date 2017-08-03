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
import com.liferay.vulcan.liferay.portal.filter.GroupIdFilter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;

/**
 * Allows resources to provide {@link GroupIdFilter} in {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder}'s
 * <code>filteredCollectionPage</code> methods.
 *
 * As well as some utility methods for getting a filter's query param map, or a
 * filter's name.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class GroupIdFilterProvider implements FilterProvider<GroupIdFilter> {

	@Override
	public String getFilterName() {
		return "group";
	}

	@Override
	public Map<String, String> getQueryParamMap(GroupIdFilter groupIdFilter) {
		return new HashMap<String, String>() {
			{
				put("groupId", String.valueOf(groupIdFilter.getGroupId()));
			}
		};
	}

	@Override
	public GroupIdFilter provide(HttpServletRequest httpServletRequest) {
		String groupIdString = httpServletRequest.getParameter("groupId");

		Long groupId = GetterUtil.getLong(groupIdString);

		if ((groupIdString == null) || (groupId == GetterUtil.DEFAULT_LONG)) {
			throw new BadRequestException();
		}

		return new GroupIdFilter(groupId);
	}

}