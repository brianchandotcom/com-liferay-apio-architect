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
import com.liferay.apio.architect.annotation.Vocabulary.Type;

/**
 * Represents a review exposed through the API. See <a
 * href="https://schema.org/Review">Review </a> for more information.
 *
 * @author Alejandro Hernández
 */
@Type("ReviewAnnotated")
public interface Review {

	/**
	 * Returns the review's rating. See <a
	 * href="https://schema.org/Rating">Rating </a> for more information.
	 *
	 * @return the review's rating
	 */
	@Field("rating")
	public Rating getRating();

	/**
	 * Returns the review's body.
	 *
	 * @return the review's body
	 */
	@Field("reviewBody")
	public String getReviewBody();

}