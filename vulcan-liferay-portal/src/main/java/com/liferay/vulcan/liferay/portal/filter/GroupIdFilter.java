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

package com.liferay.vulcan.liferay.portal.filter;

import com.liferay.vulcan.filter.QueryParamFilterType;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the groupId filter applied to the current request.
 *
 * <p>
 * To use this class, add it as a parameter in {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder}
 * <code>filteredCollectionPage</code> methods.
 * </p>
 *
 * @author Alejandro Hernández
 */
public class GroupIdFilter implements QueryParamFilterType {

	public static final String GROUP_ID = "groupId";

	public GroupIdFilter(Long groupId) {
		_groupId = groupId;
	}

	@Override
	public String getFilterName() {
		return "groupId";
	}

	public Long getGroupId() {
		return _groupId;
	}

	@Override
	public Map<String, String> getQueryParamMap() {
		return new HashMap<String, String>() {
			{
				put(GROUP_ID, String.valueOf(_groupId));
			}
		};
	}

	private final Long _groupId;

}