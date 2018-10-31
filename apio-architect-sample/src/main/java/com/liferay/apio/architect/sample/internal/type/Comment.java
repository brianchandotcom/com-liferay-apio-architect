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

import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.LinkedModel;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.identifier.Identifier;

import java.util.Date;

/**
 * Represents a comment exposed through the API. See <a
 * href="https://schema.org/Comment">Comment </a> for more information.
 *
 * @author Alejandro Hernández
 */
@Type("CommentAnnotated")
public interface Comment extends Identifier<Long> {

	/**
	 * Returns the comment's creator. See <a
	 * href="https://schema.org/creator">creator </a> for more information.
	 *
	 * @return the comment's creator
	 */
	@Field("creator")
	@LinkedModel(Person.class)
	public Long getCreatorId();

	/**
	 * Returns the comment's creation date. See <a
	 * href="https://schema.org/dateCreated">dateCreated </a> for more
	 * information.
	 *
	 * @return the comment's creation date
	 */
	@Field("dateCreated")
	public Date getDateCreated();

	/**
	 * Returns the comment's modification date. See <a
	 * href="https://schema.org/dateModified">dateModified </a> for more
	 * information.
	 *
	 * @return the comment's modification date
	 */
	@Field("dateModified")
	public Date getDateModified();

	/**
	 * Returns the comment's ID.
	 *
	 * @return the comment's ID
	 */
	@Id
	public Long getId();

	/**
	 * Returns the comment's text. See <a href="https://schema.org/text">text
	 * </a> for more information.
	 *
	 * @return the comment's text
	 */
	@Field("text")
	public String getText();

}