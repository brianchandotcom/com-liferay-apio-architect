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
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryService;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.vulcan.liferay.portal.filter.FolderIdGroupIdFilter;
import com.liferay.vulcan.liferay.portal.filter.provider.FolderIdGroupIdFilterProvider;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RepresentorBuilder;
import com.liferay.vulcan.resource.builder.RoutesBuilder;

import java.io.InputStream;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides all the necessary information to expose <a
 * href="http://schema.org/DigitalDocument">DigitalDocument</a> resource through a web API.
 * <p>
 * The resources are mapped from the internal {@link DLFileEntry} model.
 *
 * @author Javier Gamarra
 */
@Component(immediate = true)
public class DigitalDocumentResource implements Resource<DLFileEntry> {

	@Override
	public void buildRepresentor(
		RepresentorBuilder<DLFileEntry> representorBuilder) {

		representorBuilder.identifier(
			dlFileEntry -> String.valueOf(dlFileEntry.getFileEntryId())
		).addBidirectionalModel(
			"group", "digitalDocuments", Group.class, this::_getGroupOptional,
			this::_getFolderIdGroupIdFilter
		).addBinary(
			"contentStream", this::_getContentStream
		).addEmbeddedModel(
			"author", User.class, this::_getUserOptional
		).addField(
			"folderId", DLFileEntry::getFolderId
		).addField(
			"contentSize", DLFileEntry::getSize
		).addField(
			"dateCreated", DLFileEntry::getCreateDate
		).addField(
			"dateModified", DLFileEntry::getModifiedDate
		).addField(
			"datePublished", DLFileEntry::getLastPublishDate
		).addField(
			"fileFormat", DLFileEntry::getMimeType
		).addField(
			"headline", DLFileEntry::getTitle
		).addField(
			"name", DLFileEntry::getName
		).addField(
			"text", DLFileEntry::getDescription
		).addType(
			"MediaObject"
		);
	}

	@Override
	public String getPath() {
		return "digitalDocuments";
	}

	@Override
	public Routes<DLFileEntry> routes(
		RoutesBuilder<DLFileEntry> routesBuilder) {

		return routesBuilder.collectionItem(
			this::_getDLFileEntry, Long.class
		).filteredCollectionPage(
			this::_getPageItems, FolderIdGroupIdFilter.class
		).build();
	}

	private InputStream _getContentStream(DLFileEntry dlFileEntry) {
		try {
			return dlFileEntry.getContentStream();
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private DLFileEntry _getDLFileEntry(Long id) {
		try {
			return _dlFileEntryService.getFileEntry(id);
		}
		catch (NoSuchEntryException | PrincipalException e) {
			throw new NotFoundException(e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private FolderIdGroupIdFilter _getFolderIdGroupIdFilter(Group group) {
		return _folderIdGroupIdFilterProvider.create(
			group.getGroupId(), group.getGroupId());
	}

	private Optional<Group> _getGroupOptional(DLFileEntry dlFileEntry) {
		try {
			return Optional.of(
				_groupLocalService.getGroup(dlFileEntry.getGroupId()));
		}
		catch (NoSuchGroupException nsge) {
			throw new NotFoundException(nsge);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<DLFileEntry> _getPageItems(
		FolderIdGroupIdFilter folderIdGroupIdFilter, Pagination pagination) {

		try {
			Long folderId = folderIdGroupIdFilter.getFolderId();
			Long groupId = folderIdGroupIdFilter.getId();

			List<DLFileEntry> fileEntries = _dlFileEntryService.getFileEntries(
				groupId, folderId, pagination.getStartPosition(),
				pagination.getEndPosition(), null);

			int fileEntriesCount = _dlFileEntryService.getFileEntriesCount(
				groupId, folderId);

			return new PageItems<>(fileEntries, fileEntriesCount);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private Optional<User> _getUserOptional(DLFileEntry dlFileEntry) {
		try {
			return Optional.ofNullable(
				_userService.getUserById(dlFileEntry.getUserId()));
		}
		catch (NoSuchUserException | PrincipalException e) {
			throw new NotFoundException(e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private DLFileEntryService _dlFileEntryService;

	@Reference
	private FolderIdGroupIdFilterProvider _folderIdGroupIdFilterProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userService;

}