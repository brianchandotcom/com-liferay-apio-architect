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

package com.liferay.vulcan.liferay.portal.identifier;

/**
 * Allows developers to use a combination of folderId-groupId values as
 * identifier for a resource.
 *
 * <p>
 * To use this class, add it as the first parameter in
 * <code>collectionItem</code> {@link
 * com.liferay.vulcan.resource.builder.RoutesBuilder} methods.
 * </p>
 *
 * @author Javier Gamarra
 */
public class FolderIdGroupIdIdentifier {

	public FolderIdGroupIdIdentifier(long folderId, long groupId) {
		_folderId = folderId;
		_groupId = groupId;
	}

	/**
	 * Returns the folder ID to filter.
	 *
	 * @return folder ID to filter.
	 */
	public long getFolderId() {
		return _folderId;
	}

	/**
	 * Returns the group ID to filter.
	 *
	 * @return group ID to filter.
	 */
	public long getGroupId() {
		return _groupId;
	}

	private final long _folderId;
	private final long _groupId;

}