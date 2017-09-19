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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RepresentorBuilder;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.resource.identifier.LongIdentifier;
import com.liferay.vulcan.result.Try;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.BadRequestException;
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
 */
@Component(immediate = true)
public class FolderResource implements Resource<DLFolder, LongIdentifier> {

	@Override
	public Representor<DLFolder, LongIdentifier> buildRepresentor(
		RepresentorBuilder<DLFolder, LongIdentifier> representorBuilder) {

		return representorBuilder.identifier(
			dlFolder -> dlFolder::getFolderId
		).addBidirectionalModel(
			"group", "folders", Group.class, this::_getGroupOptional,
			group -> (LongIdentifier)group::getGroupId
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
		).build();
	}

	@Override
	public String getName() {
		return "folders";
	}

	@Override
	public Routes<DLFolder> routes(
		RoutesBuilder<DLFolder, LongIdentifier> routesBuilder) {

		return routesBuilder.collectionPageGetter(
			this::_getPageItems, LongIdentifier.class
		).collectionPageItemCreator(
			this::_addDLFolder, LongIdentifier.class
		).collectionPageItemGetter(
			this::_getDLFolder
		).collectionPageItemRemover(
			this::_deleteDLFolder
		).build();
	}

	private DLFolder _addDLFolder(
		LongIdentifier groupLongIdentifier, Map<String, Object> body) {

		long parentFolderId = 0;

		String name = (String)body.get("name");

		if (Validator.isNull(name)) {
			throw new BadRequestException("Incorrect body");
		}

		Try<DLFolder> dlFolderTry = Try.fromFallible(
			() -> _dlFolderService.getFolder(
				groupLongIdentifier.getId(), parentFolderId, name));

		if (dlFolderTry.isSuccess()) {
			throw new BadRequestException(
				"A folder with that name already exists");
		}

		String description = (String)body.get("description");

		if (Validator.isNull(description)) {
			throw new BadRequestException("Incorrect body");
		}

		dlFolderTry = Try.fromFallible(
			() -> _dlFolderService.addFolder(
				groupLongIdentifier.getId(), groupLongIdentifier.getId(), false,
				parentFolderId, name, description, new ServiceContext()));

		return dlFolderTry.getUnchecked();
	}

	private void _deleteDLFolder(LongIdentifier dlFolderLongerIdentifier) {
		try {
			_dlFolderService.deleteFolder(dlFolderLongerIdentifier.getId());
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private DLFolder _getDLFolder(LongIdentifier dlFolderLongIdentifier) {
		try {
			return _dlFolderService.getFolder(dlFolderLongIdentifier.getId());
		}
		catch (NoSuchEntryException | PrincipalException e) {
			throw new NotFoundException(
				"Unable to get folder " + dlFolderLongIdentifier.getId(), e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
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
		Pagination pagination, LongIdentifier groupLongIdentifier) {

		try {
			List<DLFolder> dlFolders = _dlFolderService.getFolders(
				groupLongIdentifier.getId(), 0, pagination.getStartPosition(),
				pagination.getEndPosition(), null);
			int count = _dlFolderService.getFoldersCount(
				groupLongIdentifier.getId(), 0);

			return new PageItems<>(dlFolders, count);
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
	private GroupLocalService _groupLocalService;

}