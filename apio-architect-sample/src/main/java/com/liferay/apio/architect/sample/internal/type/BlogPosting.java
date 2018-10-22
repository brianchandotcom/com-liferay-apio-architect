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
import com.liferay.apio.architect.annotation.Vocabulary.RelatedCollection;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.sample.internal.identifier.ModelNameModelIdIdentifier;

import java.util.Date;
import java.util.List;

/**
 * Represents a blog posting exposed through the API. See <a
 * href="https://schema.org/BlogPosting">BlogPosting </a> for more information.
 *
 * @author Alejandro Hernández
 */
@Type("BlogPostingAnnotated")
public interface BlogPosting extends Identifier<Long> {

	/**
	 * Returns the blog posting's alternate headline. See <a
	 * href="https://schema.org/alternativeHeadline">alternativeHeadline </a>
	 * for more information.
	 *
	 * @return the alternate headline
	 */
	@Field("alternativeHeadline")
	public String getAlternativeHeadline();

	/**
	 * Returns the blog posting's body. See <a
	 * href="https://schema.org/articleBody">articleBody </a> for more
	 * information.
	 *
	 * @return the blog posting's body
	 */
	@Field("articleBody")
	public String getArticleBody();

	@Field("people")
	@RelatedCollection(value = Comment.class, reusable=true)
	public default ModelNameModelIdIdentifier getModelNameModelIdIdentifier() {
		return new ModelNameModelIdIdentifier() {
			@Override
			public long getModelId() {
				return 0;
			}

			@Override
			public String getModelName() {
				return "0";
			}
		};
	}

	/**
	 * Returns the parent ID of the blog posting's comments. See <a
	 * href="https://schema.org/comment">comment </a> for more information.
	 *
	 * @return the parent ID of the comments
	 */
	@Field("comment")
	@RelatedCollection(Comment.class)
	public default Long getCommentIds() {
		return getId();
	}

	/**
	 * Returns the ID of the blog posting's creator. See <a
	 * href="https://schema.org/creator">creator </a> for more information.
	 *
	 * @return the creator's ID
	 */
	@Field("creator")
	@LinkedModel(Person.class)
	public Long getCreatorId();

	/**
	 * Returns the blog posting's creation date. See <a
	 * href="https://schema.org/dateCreated">dateCreated </a> for more
	 * information.
	 *
	 * @return the creation date
	 */
	@Field("dateCreated")
	public Date getDateCreated();

	/**
	 * Returns the blog posting's modification date. See <a
	 * href="https://schema.org/dateModified">dateModified </a> for more
	 * information.
	 *
	 * @return the modification date
	 */
	@Field("dateModified")
	public Date getDateModified();

	/**
	 * Returns the blog posting's format. See <a
	 * href="https://schema.org/fileFormat">fileFormat </a> for more
	 * information.
	 *
	 * @return the format
	 */
	@Field("fileFormat")
	public String getFileFormat();

	/**
	 * Returns the blog posting's headline. See <a
	 * href="https://schema.org/headline">headline </a> for more information.
	 *
	 * @return the headline
	 */
	@Field("headline")
	public String getHeadline();

	/**
	 * Returns the blog posting's ID.
	 *
	 * @return the blog posting's ID
	 */
	@Id
	public Long getId();

	/**
	 * Returns the list of the blog posting's reviews. See <a
	 * href="https://schema.org/review">review </a> for more information.
	 *
	 * @return the list of reviews
	 */
	@Field("review")
	public List<Review> getReviews();

}