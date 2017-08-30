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
import com.liferay.vulcan.liferay.portal.filter.FolderIdFilter;
import com.liferay.vulcan.liferay.portal.internal.filter.FolderIdFilterImpl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Allows resources to provide {@link FolderIdFilter} in {@link
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
	service = {FilterProvider.class, FolderIdFilterProvider.class}
)
public class FolderIdFilterProvider implements FilterProvider<FolderIdFilter> {

	/**
	 * Creates a new {@link FolderIdFilter} from a given
	 * <code>folderId</code>.
	 *
	 * @param  folderId the folder ID that will be used to filter.
	 * @return an instance of a {@link FolderIdFilter}.
	 */
	public FolderIdFilter create(long folderId) {
		return new FolderIdFilterImpl(folderId);
	}

	@Override
	public String getFilterName() {
		return "folderId";
	}

	@Override
	public Map<String, String> getQueryParamMap(
		FolderIdFilter queryParamFilterType) {

		return _idFilterProviderHelper.getQueryParamMap(queryParamFilterType);
	}

	@Override
	public FolderIdFilter provide(HttpServletRequest httpServletRequest) {
		String folderIdString = _idFilterProviderHelper.getId(
			httpServletRequest);

		long folderId = GetterUtil.getLong(folderIdString);

		return new FolderIdFilterImpl(folderId);
	}

	@Reference
	private IdFilterProviderHelper _idFilterProviderHelper;

}