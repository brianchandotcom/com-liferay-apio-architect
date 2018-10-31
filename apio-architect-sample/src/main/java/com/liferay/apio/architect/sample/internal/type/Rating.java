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

import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.LinkedModel;
import com.liferay.apio.architect.annotation.Vocabulary.Type;

/**
 * Represents a rating exposed through the API. See <a
 * href="https://schema.org/Rating">Rating </a> for more information.
 *
 * @author Alejandro Hernández
 */
@Type("RatingAnnotated")
public interface Rating {

	/**
	 * Returns the best rating value that this rating allows. See <a
	 * href="https://schema.org/bestRating">bestRating </a> for more
	 * information.
	 *
	 * @return the best possible rating value
	 */
	@Field(readOnly = true, value = "bestRating")
	public default Number getBestRating() {
		return 5;
	}

	/**
	 * Returns the rating's creator. See <a
	 * href="https://schema.org/creator">creator </a> for more information.
	 *
	 * @return the rating's creator
	 */
	@Field("creator")
	@LinkedModel(Person.class)
	public Long getCreatorId();

	/**
	 * Returns the rating's value. See <a
	 * href="https://schema.org/ratingValue">ratingValue </a> for more
	 * information.
	 *
	 * @return the rating's value
	 */
	@Field("ratingValue")
	public Long getRatingValue();

	/**
	 * Returns the worst rating value that this rating allows. See <a
	 * href="https://schema.org/worstRating">worstRating </a> for more
	 * information.
	 *
	 * @return the worst possible rating value
	 */
	@Field(readOnly = true, value = "worstRating")
	public default Number getWorstRating() {
		return 0;
	}

}