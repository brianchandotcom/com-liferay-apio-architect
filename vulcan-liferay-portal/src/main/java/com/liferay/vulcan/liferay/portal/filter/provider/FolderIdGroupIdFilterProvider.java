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

package com.liferay.vulcan.liferay.portal.filter.provider;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.filter.FilterProvider;
import com.liferay.vulcan.filter.IdFilterProviderHelper;
import com.liferay.vulcan.liferay.portal.filter.FolderIdGroupIdFilter;
import com.liferay.vulcan.liferay.portal.internal.filter.FolderIdGroupIdFilterImpl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Allows resources to provide {@link FolderIdGroupIdFilter} in {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder}'s
 * <code>filteredCollectionPage</code> methods.
 *
 * As well as some utility methods for getting a filter's query param map, a
 * filter's name or creating a new filter based on a <code>folderId</code> and
 * a <code>groupId</code>.
 *
 * @author Javier Gamarra
 */
@Component(
	immediate = true,
	service = {FilterProvider.class, FolderIdGroupIdFilterProvider.class}
)
public class FolderIdGroupIdFilterProvider
	implements FilterProvider<FolderIdGroupIdFilter> {

	/**
	 * Creates a new {@link FolderIdGroupIdFilter} from a given
	 * <code>folderId</code> and <code>groupId</code>.
	 *
	 * @param  folderId the folder id that will be used to filter.
	 * @param  groupId the group id that will be used to filter.
	 * @return an instance of a {@link FolderIdGroupIdFilter}.
	 */
	public FolderIdGroupIdFilter create(long folderId, long groupId) {
		return new FolderIdGroupIdFilterImpl(folderId, groupId);
	}

	@Override
	public String getFilterName() {
		return "folderId";
	}

	@Override
	public Map<String, String> getQueryParamMap(
		FolderIdGroupIdFilter queryParamFilterType) {

		return _idFilterProviderHelper.getQueryParamMap(queryParamFilterType);
	}

	@Override
	public FolderIdGroupIdFilter provide(
		HttpServletRequest httpServletRequest) {

		String groupIdString = _idFilterProviderHelper.getId(
			httpServletRequest);

		long groupId = GetterUtil.getLong(groupIdString);

		String folderIdString = httpServletRequest.getParameter("folderId");

		long folderId = GetterUtil.getLong(folderIdString);

		return new FolderIdGroupIdFilterImpl(folderId, groupId);
	}

	@Reference
	private IdFilterProviderHelper _idFilterProviderHelper;

}