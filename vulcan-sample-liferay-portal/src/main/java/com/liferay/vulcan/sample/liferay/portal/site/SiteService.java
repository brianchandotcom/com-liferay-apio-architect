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

package com.liferay.vulcan.sample.liferay.portal.site;

import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;

import java.util.Optional;

/**
 * Instances of this service can be used to get a single {@link Site} or a
 * {@link Site}'s page based on a pagination params.
 *
 * @author Victor Oliveira
 * @author Alejandro Hernández
 * @review
 */
public interface SiteService {

	/**
	 * Return the {@code Site} {@code PageItems} for a combination of pagination
	 * and {@link com.liferay.portal.kernel.model.Company} ID.
	 *
	 * @param  pagination the pagination information.
	 * @param  companyId the ID of the company.
	 * @return the {@code Site} {@code PageItems}.
	 */
	public PageItems<Site> getPageItems(Pagination pagination, long companyId);

	/**
	 * Returns a {@code Site} with a certain ID if present. Returns {@code
	 * Optional#empty()} otherwise.
	 *
	 * @param  siteId the ID of the {@code Site}.
	 * @return the {@code Site} with that ID, if present; {@code
	 *         Optional#empty()} otherwise.
	 */
	public Optional<Site> getSite(long siteId);

}