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

package com.liferay.vulcan.uri;

/**
 * Represents a resource path.
 *
 * <p>
 * For example a resource obtained in the URL: <code>/p/customers/2012</code>
 * will have a path containing <code>2012</code> and <code>"customers"</code> as
 * the ID and type respectively.
 * </p>
 *
 * @author Alejandro Hernández
 */
public class Path {

	public Path() {
		_type = "";
		_id = "";
	}

	public Path(String type, String id) {
		_type = type;
		_id = id;
	}

	/**
	 * Returns this resource's path as an URI.
	 *
	 * @return the chain of identifiers as an URI.
	 */
	public String asURI() {
		return "/" + getType() + "/" + getId();
	}

	/**
	 * Returns the ID part of this {@code Path}.
	 *
	 * <p>
	 * For example with a resource obtained in the URI:
	 * <code>/p/customers/2012</code> this method will return <code>2012</code>.
	 * </p>
	 *
	 * @return the ID part of the {@code Path}.
	 */
	public String getId() {
		return _id;
	}

	/**
	 * Returns the type part of this {@code Path}
	 *
	 * <p>
	 * For example with a resource obtained in the URI:
	 * <code>/p/customers/2012</code> this method will return
	 * <code>"customer"</code>.
	 * </p>
	 *
	 * @return the type part of the {@code Path}.
	 */
	public String getType() {
		return _type;
	}

	private final String _id;
	private final String _type;

}