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

package com.liferay.vulcan.liferay.internal;

import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.vulcan.endpoint.RootEndpoint;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
@Path("/")
public class LiferayRootEndpoint implements RootEndpoint {

	@Path("/group/{id}/")
	public LiferayRootEndpoint getGroupLiferayRootEndpoint(
		@PathParam("id") long id) {

		GroupThreadLocal.setGroupId(id);

		return this;
	}

}