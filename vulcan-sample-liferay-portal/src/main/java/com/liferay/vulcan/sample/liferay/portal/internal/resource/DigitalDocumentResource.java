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
import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryService;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.vulcan.identifier.LongIdentifier;
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
 * href="http://schema.org/DigitalDocument">DigitalDocument</a> resource through
 * a web API. <p> The resources are mapped from the internal {@link DLFileEntry}
 * model.
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
			"folder", "digitalDocuments", DLFolder.class,
			this::_getDLFolderOptional
		).addBinary(
			"contentStream", this::_getInputStream
		).addEmbeddedModel(
			"author", User.class, this::_getUserOptional
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
			"folderId", DLFileEntry::getFolderId
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
			this::_getDLFileEntry, LongIdentifier.class
		).collectionPage(
			this::_getPageItems, LongIdentifier.class
		).build();
	}

	private DLFileEntry _getDLFileEntry(LongIdentifier fileEntryIdentifier) {
		long fileEntryId = fileEntryIdentifier.getIdAsLong();

		try {
			return _dlFileEntryService.getFileEntry(fileEntryId);
		}
		catch (NoSuchEntryException | PrincipalException e) {
			throw new NotFoundException("Unable to get file " + fileEntryId, e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private Optional<DLFolder> _getDLFolderOptional(DLFileEntry dlFileEntry) {
		long folderId = dlFileEntry.getFolderId();

		try {
			return Optional.of(_dlFolderService.getFolder(folderId));
		}
		catch (NoSuchFolderException nsfe) {
			throw new NotFoundException(
				"Unable to get group " + folderId, nsfe);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private InputStream _getInputStream(DLFileEntry dlFileEntry) {
		try {
			return dlFileEntry.getContentStream();
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<DLFileEntry> _getPageItems(
		Pagination pagination, LongIdentifier folderIdentifier) {

		try {
			long folderId = folderIdentifier.getIdAsLong();

			DLFolder dlFolder = _dlFolderService.getFolder(folderId);

			List<DLFileEntry> dlFileEntries =
				_dlFileEntryService.getFileEntries(
					dlFolder.getGroupId(), dlFolder.getFolderId(),
					pagination.getStartPosition(), pagination.getEndPosition(),
					null);
			int count = _dlFileEntryService.getFileEntriesCount(
				dlFolder.getGroupId(), dlFolder.getFolderId());

			return new PageItems<>(dlFileEntries, count);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private Optional<User> _getUserOptional(DLFileEntry dlFileEntry) {
		long userId = dlFileEntry.getUserId();

		try {
			return Optional.ofNullable(_userService.getUserById(userId));
		}
		catch (NoSuchUserException | PrincipalException e) {
			throw new NotFoundException("Unable to get user " + userId, e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private DLFileEntryService _dlFileEntryService;

	@Reference
	private DLFolderService _dlFolderService;

	@Reference
	private UserLocalService _userService;

}