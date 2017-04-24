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

package com.liferay.vulcan.sample.rest.internal.blogs.resource;

import com.liferay.blogs.kernel.exception.NoSuchEntryException;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.vulcan.liferay.scope.GroupScoped;
import com.liferay.vulcan.pagination.Page;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.CollectionResource;
import com.liferay.vulcan.resource.SingleResource;

import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true, service = BlogPostingCollectionResource.class)
public class BlogPostingCollectionResource
	implements CollectionResource<BlogsEntry>, GroupScoped {

	@Override
	public SingleResource<BlogsEntry> getSingleResource(String id) {
		BlogsEntry blogsEntry = null;

		try {
			long entryId = Long.parseLong(id);

			blogsEntry = _blogsService.getEntry(entryId);
		}
		catch (NoSuchEntryException | PrincipalException e) {
			throw new NotFoundException();
		}
		catch (NumberFormatException nfe) {
			throw new BadRequestException();
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}

		return new BlogPostingSingleResource(blogsEntry);
	}

	@Override
	public Page<BlogsEntry> getPage(Pagination pagination) {
		List<BlogsEntry> groupEntries = _blogsService.getGroupEntries(
			_groupId, 0, pagination.getStartPosition(),
			pagination.getEndPosition());

		int groupEntriesCount = _blogsService.getGroupEntriesCount(_groupId, 0);

		return pagination.createPage(groupEntries, groupEntriesCount);
	}

	@Override
	public void setGroupId(long groupId) {
		_groupId = groupId;
	}

	@Reference
	private BlogsEntryService _blogsService;

	private long _groupId;

}