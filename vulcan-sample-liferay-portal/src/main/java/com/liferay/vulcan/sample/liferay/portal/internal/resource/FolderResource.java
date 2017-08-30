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

package com.liferay.vulcan.sample.liferay.portal.internal.resource;

import com.liferay.blogs.kernel.exception.NoSuchEntryException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.vulcan.liferay.portal.filter.GroupIdFilter;
import com.liferay.vulcan.liferay.portal.filter.provider.GroupIdFilterProvider;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RepresentorBuilder;
import com.liferay.vulcan.resource.builder.RoutesBuilder;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides all the necessary information to expose folder resource through a
 * web API. <p> The resources are mapped from the internal {@link DLFolder}
 * model.
 *
 * @author Javier Gamarra
 *
 * @review
 */
@Component(immediate = true)
public class FolderResource implements Resource<DLFolder> {

	@Override
	public void buildRepresentor(
		RepresentorBuilder<DLFolder> representorBuilder) {

		representorBuilder.identifier(
			dlFolder -> String.valueOf(dlFolder.getFolderId())
		).addBidirectionalModel(
			"group", "folders", Group.class, this::_getGroupOptional,
			this::_getGroupIdFilter
		).addField(
			"dateCreated", DLFolder::getCreateDate
		).addField(
			"dateModified", DLFolder::getModifiedDate
		).addField(
			"datePublished", DLFolder::getLastPublishDate
		).addField(
			"name", DLFolder::getName
		).addField(
			"path", this::_getPath
		).addType(
			"Folder"
		);
	}

	@Override
	public String getPath() {
		return "folders";
	}

	@Override
	public Routes<DLFolder> routes(RoutesBuilder<DLFolder> routesBuilder) {
		return routesBuilder.collectionItem(
			this::_getDLFolder, Long.class
		).filteredCollectionPage(
			this::_getPageItems, GroupIdFilter.class
		).build();
	}

	private DLFolder _getDLFolder(Long id) {
		try {
			return _dlFolderService.getFolder(id);
		}
		catch (NoSuchEntryException | PrincipalException e) {
			throw new NotFoundException(e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private GroupIdFilter _getGroupIdFilter(Group group) {
		return _groupIdFilterProvider.create(group.getGroupId());
	}

	private Optional<Group> _getGroupOptional(DLFolder dlFolder) {
		try {
			return Optional.of(
				_groupLocalService.getGroup(dlFolder.getGroupId()));
		}
		catch (NoSuchGroupException nsge) {
			throw new NotFoundException(nsge);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<DLFolder> _getPageItems(
		GroupIdFilter groupIdFilter, Pagination pagination) {

		try {
			Long groupId = groupIdFilter.getId();

			List<DLFolder> folders = _dlFolderService.getFolders(
				groupId, 0, pagination.getStartPosition(),
				pagination.getEndPosition(), null);

			int foldersCount = _dlFolderService.getFoldersCount(groupId, 0);

			return new PageItems<>(folders, foldersCount);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private String _getPath(DLFolder dlFolder) {
		try {
			return dlFolder.getPath();
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private DLFolderService _dlFolderService;

	@Reference
	private GroupIdFilterProvider _groupIdFilterProvider;

	@Reference
	private GroupLocalService _groupLocalService;

}