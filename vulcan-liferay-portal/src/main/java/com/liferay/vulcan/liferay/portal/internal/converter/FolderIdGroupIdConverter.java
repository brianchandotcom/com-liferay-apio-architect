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

package com.liferay.vulcan.liferay.portal.internal.converter;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.vulcan.converter.Converter;
import com.liferay.vulcan.liferay.portal.identifier.FolderIdGroupIdIdentifier;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;

/**
 * Allows resources to use {@link FolderIdGroupIdIdentifier} as the identifier
 * in <code>collectionItem</code> {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder} methods.
 *
 * @author Javier Gamarra
 */
@Component(immediate = true)
public class FolderIdGroupIdConverter
	implements Converter<FolderIdGroupIdIdentifier> {

	@Override
	public String convert(FolderIdGroupIdIdentifier id) {
		return id.getFolderId() + "_" + id.getGroupId();
	}

	@Override
	public FolderIdGroupIdIdentifier convert(String id) {
		String[] components = id.split("_");

		if (components.length == 2) {
			String folderIdString = components[0];
			String groupIdString = components[1];

			long folderId = GetterUtil.getLong(folderIdString);
			long groupId = GetterUtil.getLong(groupIdString);

			if (groupId != GetterUtil.DEFAULT_LONG) {
				return new FolderIdGroupIdIdentifier(folderId, groupId);
			}
		}

		throw new BadRequestException(
			id + " can't be converted to a folderId + groupId identifier");
	}

}