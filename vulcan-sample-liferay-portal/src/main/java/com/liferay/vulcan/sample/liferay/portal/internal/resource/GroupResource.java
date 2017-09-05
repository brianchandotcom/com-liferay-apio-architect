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

import static com.liferay.portal.kernel.model.GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION;
import static com.liferay.portal.kernel.model.GroupConstants.TYPE_SITE_OPEN;

import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.vulcan.identifier.LongIdentifier;
import com.liferay.vulcan.identifier.RootIdentifier;
import com.liferay.vulcan.liferay.portal.context.CurrentUser;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RepresentorBuilder;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.result.Try;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides all the necessary information to expose Group resources through a
 * web API.
 *
 * The resources are mapped from the internal {@link Group} model.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class GroupResource implements Resource<Group, LongIdentifier> {

	@Override
	public void buildRepresentor(
		RepresentorBuilder<Group, LongIdentifier> representorBuilder) {

		representorBuilder.identifier(
			group -> group::getGroupId
		).addField(
			"name", group -> group.getName(Locale.US)
		).addField(
			"groupType", Group::getTypeLabel
		).addType(
			"Group"
		);
	}

	@Override
	public String getPath() {
		return "groups";
	}

	@Override
	public Routes<Group> routes(
		RoutesBuilder<Group, LongIdentifier> routesBuilder) {

		return routesBuilder.collectionItem(
			this::_getGroup
		).collectionPage(
			this::_getPageItems, RootIdentifier.class
		).postCollectionItem(
			this::_addGroup, RootIdentifier.class, CurrentUser.class
		).build();
	}

	private Group _addGroup(
		RootIdentifier rootIdentifier, Map<String, Object> body,
		CurrentUser currentUser) {

		long userId = currentUser.getUserId();
		int parentGroupId = 0;

		String name = (String)body.get("name");

		if (Validator.isNull(name)) {
			throw new BadRequestException("Incorrect body");
		}

		String className = Group.class.getName();
		int classPK = 0;
		int liveGroupId = 0;
		Map<Locale, String> nameMap = Collections.singletonMap(Locale.US, name);
		Map<Locale, String> descriptionMap = Collections.emptyMap();
		boolean manualMembership = false;
		String friendlyURL = null;
		boolean site = true;
		boolean active = true;
		ServiceContext serviceContext = null;

		Try<Group> groupTry = Try.fromFallible(
			() -> _groupLocalService.addGroup(
				userId, parentGroupId, className, classPK, liveGroupId, nameMap,
				descriptionMap, TYPE_SITE_OPEN, manualMembership,
				DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, site, active,
				serviceContext));

		return groupTry.getUnchecked();
	}

	private Group _getGroup(LongIdentifier groupLongIdentifier) {
		try {
			return _groupLocalService.getGroup(
				groupLongIdentifier.getIdAsLong());
		}
		catch (NoSuchGroupException nsge) {
			throw new NotFoundException(
				"Unable to get group " + groupLongIdentifier.getIdAsLong(),
				nsge);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<Group> _getPageItems(
		Pagination pagination, RootIdentifier rootIdentifier) {

		List<Group> groups = _groupLocalService.getGroups(
			pagination.getStartPosition(), pagination.getEndPosition());
		int groupsCount = _groupLocalService.getGroupsCount();

		return new PageItems<>(groups, groupsCount);
	}

	@Reference
	private GroupLocalService _groupLocalService;

}