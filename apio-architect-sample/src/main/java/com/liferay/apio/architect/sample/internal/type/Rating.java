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

/**
 * Instances of this interface represent a rating exposed through the API.
 *
 * @author Alejandro Hernández
 * @see    <a href="https://schema.org/Rating">Rating</a>
 * @review
 */
public interface Rating {

	/**
	 * Returns the rating's author.
	 *
	 * @return the rating's author
	 * @see    <a href="https://schema.org/author">author</a>
	 * @review
	 */
	public Long getAuthor();

	/**
	 * Returns the rating's best possible rating.
	 *
	 * @return the rating's best possible rating
	 * @see    <a href="https://schema.org/bestRating">bestRating</a>
	 * @review
	 */
	public default Number getBestRating() {
		return 5;
	}

	/**
	 * Returns the actual rating's value.
	 *
	 * @return the actual rating's value
	 * @see    <a href="https://schema.org/ratingValue">ratingValue</a>
	 * @review
	 */
	public Number getRatingValue();

	/**
	 * Returns the rating's worst possible value.
	 *
	 * @return the rating's worst possible value
	 * @see    <a href="https://schema.org/worstRating">worstRating</a>
	 * @review
	 */
	public default Number getWorstRating() {
		return 0;
	}

}