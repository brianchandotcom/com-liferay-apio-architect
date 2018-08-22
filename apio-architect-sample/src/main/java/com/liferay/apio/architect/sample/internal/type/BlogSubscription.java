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

package com.liferay.apio.architect.sample.internal.type;

import com.liferay.apio.architect.identifier.Identifier;

/**
 * Instances of this interface represent a person's blog post subscription
 * exposed through the API.
 *
 * @author Alejandro Hernández
 * @review
 */
public interface BlogSubscription extends Identifier<Long> {

	/**
	 * Returns the blog's ID.
	 *
	 * @return the blog's ID
	 * @review
	 */
	public Long getBlog();

	/**
	 * Returns the blog's title.
	 *
	 * @return the blog's title
	 * @review
	 */
	public String getBlogTitle();

	/**
	 * Returns the subscription's ID.
	 *
	 * @return the subscription's ID
	 * @review
	 */
	public Long getId();

	/**
	 * Returns the person's ID.
	 *
	 * @return the person's ID
	 * @review
	 */
	public Long getPerson();

	/**
	 * Returns the person's name.
	 *
	 * @return the person's name
	 * @review
	 */
	public String getPersonFullName();

}