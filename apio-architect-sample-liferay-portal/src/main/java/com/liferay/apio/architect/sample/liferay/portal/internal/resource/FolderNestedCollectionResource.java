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

package com.liferay.apio.architect.sample.liferay.portal.internal.resource;

import static com.liferay.apio.architect.sample.liferay.portal.internal.idempotent.Idempotent.idempotent;

import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.NestedCollectionResource;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.sample.liferay.portal.internal.form.FolderForm;
import com.liferay.apio.architect.sample.liferay.portal.internal.identifier.FolderIdentifier;
import com.liferay.apio.architect.sample.liferay.portal.internal.identifier.WebSiteIdentifier;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderModel;
import com.liferay.document.library.kernel.service.DLFolderService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the information necessary to expose folder resources through a web
 * API. The resources are mapped from the internal model {@code DLFolder}.
 *
 * @author Javier Gamarra
 */
@Component(immediate = true)
public class FolderNestedCollectionResource
	implements NestedCollectionResource<DLFolder, Long, FolderIdentifier,
		Long, WebSiteIdentifier> {

	@Override
	public NestedCollectionRoutes<DLFolder, Long> collectionRoutes(
		NestedCollectionRoutes.Builder<DLFolder, Long> builder) {

		return builder.addGetter(
			this::_getPageItems
		).addCreator(
			this::_addDLFolder, (credentials, id) -> true, FolderForm::buildForm
		).build();
	}

	@Override
	public String getName() {
		return "folders";
	}

	@Override
	public ItemRoutes<DLFolder, Long> itemRoutes(
		ItemRoutes.Builder<DLFolder, Long> builder) {

		return builder.addGetter(
			_dlFolderService::getFolder
		).addRemover(
			idempotent(_dlFolderService::deleteFolder),
			(credentials, id) -> true
		).addUpdater(
			this::_updateDLFolder, (credentials, id) -> true,
			FolderForm::buildForm
		).build();
	}

	@Override
	public Representor<DLFolder, Long> representor(
		Representor.Builder<DLFolder, Long> builder) {

		return builder.types(
			"Folder"
		).identifier(
			DLFolder::getFolderId
		).addBidirectionalModel(
			"interactionService", "folder", WebSiteIdentifier.class,
			DLFolderModel::getGroupId
		).addDate(
			"dateCreated", DLFolder::getCreateDate
		).addDate(
			"dateModified", DLFolder::getCreateDate
		).addDate(
			"datePublished", DLFolder::getCreateDate
		).addString(
			"name", DLFolder::getName
		).addString(
			"path", dlFolder -> Try.fromFallible(dlFolder::getPath).orElse(null)
		).build();
	}

	private DLFolder _addDLFolder(Long groupId, FolderForm folderForm)
		throws PortalException {

		long parentFolderId = 0;

		Try<DLFolder> dlFolderTry = Try.fromFallible(
			() -> _dlFolderService.getFolder(
				groupId, parentFolderId, folderForm.getName()));

		if (dlFolderTry.isSuccess()) {
			throw new BadRequestException(
				"A folder with that name already exists");
		}

		return _dlFolderService.addFolder(
			groupId, groupId, false, parentFolderId, folderForm.getName(),
			folderForm.getDescription(), new ServiceContext());
	}

	private PageItems<DLFolder> _getPageItems(
			Pagination pagination, Long groupId)
		throws PortalException {

		List<DLFolder> dlFolders = _dlFolderService.getFolders(
			groupId, 0, pagination.getStartPosition(),
			pagination.getEndPosition(), null);
		int count = _dlFolderService.getFoldersCount(groupId, 0);

		return new PageItems<>(dlFolders, count);
	}

	private DLFolder _updateDLFolder(Long dlFolderId, FolderForm folderForm)
		throws PortalException {

		DLFolder dlFolder = _dlFolderService.getFolder(dlFolderId);

		return _dlFolderService.updateFolder(
			dlFolderId, dlFolder.getParentFolderId(), folderForm.getName(),
			folderForm.getDescription(), dlFolder.getDefaultFileEntryTypeId(),
			new ArrayList<>(), dlFolder.getRestrictionType(),
			new ServiceContext());
	}

	@Reference
	private DLFolderService _dlFolderService;

}