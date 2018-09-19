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

import java.util.Date;
import java.util.List;

/**
 * Instances of this interface represent a blog posting exposed through the API.
 *
 * @author Alejandro Hernández
 * @see    <a href="https://schema.org/BlogPosting">BlogPosting</a>
 * @review
 */
public interface BlogPosting extends Identifier<Long> {

	/**
	 * Returns the blog posting's alternative headline.
	 *
	 * @return the blog posting's alternative headline
	 * @see    <a
	 *         href="https://schema.org/alternativeHeadline">alternativeHeadline</a>
	 * @review
	 */
	public String getAlternativeHeadline();

	/**
	 * Returns the blog posting's body.
	 *
	 * @return the blog posting's body
	 * @see    <a href="https://schema.org/articleBody">articleBody</a>
	 * @review
	 */
	public String getArticleBody();

	/**
	 * Returns the blog posting's creator ID.
	 *
	 * @return the blog posting's creator ID
	 * @see    <a href="https://schema.org/creator">creator</a>
	 * @review
	 */
	public Long getCreator();

	/**
	 * Returns the blog posting's creation date.
	 *
	 * @return the blog posting's creation date
	 * @see    <a href="https://schema.org/dateCreated">dateCreated</a>
	 * @review
	 */
	public Date getDateCreated();

	/**
	 * Returns the blog posting's modification date.
	 *
	 * @return the blog posting's modification date
	 * @see    <a href="https://schema.org/dateModified">dateModified</a>
	 * @review
	 */
	public Date getDateModified();

	/**
	 * Returns the blog posting's format.
	 *
	 * @return the blog posting's format
	 * @see    <a href="https://schema.org/fileFormat">fileFormat</a>
	 * @review
	 */
	public String getFileFormat();

	/**
	 * Returns the blog posting's headline.
	 *
	 * @return the blog posting's headline
	 * @see    <a href="https://schema.org/headline">headline</a>
	 * @review
	 */
	public String getHeadline();

	/**
	 * Returns the blog posting's ID.
	 *
	 * @return the blog posting's ID
	 * @review
	 */
	public Long getId();

	/**
	 * Returns the list of blog posting's reviews.
	 *
	 * @return the list of blog posting's reviews
	 * @see    <a href="https://schema.org/review">review</a>
	 * @review
	 */
	public List<Review> getReview();

}