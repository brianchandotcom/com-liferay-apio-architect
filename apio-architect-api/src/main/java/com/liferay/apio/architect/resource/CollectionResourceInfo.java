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

package com.liferay.apio.architect.resource;

import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.routes.Routes;

/**
 * Contains information about a {@link CollectionResource}.
 *
 * @author Alejandro Hernández
 */
public class CollectionResourceInfo<T, U extends Identifier> {

	public CollectionResourceInfo(
		String name, Representor<T, U> representor, Routes<T> routes) {

		_name = name;
		_representor = representor;
		_routes = routes;
	}

	/**
	 * Returns the resource's name.
	 *
	 * @return the resource's name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Returns the {@link Representor} declared in the resource.
	 *
	 * @return the {@link Representor} declared in the resource
	 */
	public Representor<T, U> getRepresentor() {
		return _representor;
	}

	/**
	 * Returns the {@link Routes} declared in the resource.
	 *
	 * @return the {@link Routes} declared in the resource
	 */
	public Routes<T> getRoutes() {
		return _routes;
	}

	private final String _name;
	private final Representor<T, U> _representor;
	private final Routes<T> _routes;

}