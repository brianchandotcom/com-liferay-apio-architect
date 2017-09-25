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

import com.liferay.portal.kernel.model.Company;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.CollectionResource;
import com.liferay.vulcan.resource.Representor;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RepresentorBuilder;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.resource.identifier.LongIdentifier;
import com.liferay.vulcan.resource.identifier.RootIdentifier;
import com.liferay.vulcan.sample.liferay.portal.site.Site;
import com.liferay.vulcan.sample.liferay.portal.site.SiteService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Victor Oliveira
 */
@Component(immediate = true)
public class SiteCollectionResource
	implements CollectionResource<Site, LongIdentifier> {

	@Override
	public Representor<Site, LongIdentifier> buildRepresentor(
		RepresentorBuilder<Site, LongIdentifier> representorBuilder) {

		return representorBuilder.identifier(
			Site::getSiteLongIdentifier
		).addField(
			"name", Site::getName
		).addField(
			"description", Site::getDescription
		).addType(
			"WebSite"
		).build();
	}

	@Override
	public String getName() {
		return "sites";
	}

	@Override
	public Routes<Site> routes(
		RoutesBuilder<Site, LongIdentifier> routesBuilder) {

		return routesBuilder.addCollectionPageGetter(
			this::_getPageItems, RootIdentifier.class, Company.class
		).build();
	}

	private PageItems<Site> _getPageItems(
		Pagination pagination, RootIdentifier rootIdentifier, Company company) {

		return _siteService.getPageItems(pagination, company.getCompanyId());
	}

	@Reference
	private SiteService _siteService;

}