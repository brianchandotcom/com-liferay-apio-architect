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

package com.liferay.vulcan.sample.liferay.portal.internal.rating;

import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.vulcan.sample.liferay.portal.rating.AggregateRating;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Alejandro Hernández
 */
public class AggregateRatingImpl implements AggregateRating {

	public AggregateRatingImpl(
		String className, Long classPK, List<RatingsEntry> ratingsEntries) {

		_className = className;
		_classPK = classPK;
		_ratingCount = ratingsEntries.size();

		Stream<RatingsEntry> stream = ratingsEntries.stream();

		_ratingValue = stream.mapToDouble(
			RatingsEntry::getScore
		).average(
		).orElse(
			0
		);
	}

	@Override
	public String getClassName() {
		return _className;
	}

	@Override
	public Long getClassPK() {
		return _classPK;
	}

	@Override
	public Integer getRatingCount() {
		return _ratingCount;
	}

	@Override
	public Double getRatingValue() {
		return _ratingValue;
	}

	private final String _className;
	private final Long _classPK;
	private final Integer _ratingCount;
	private final Double _ratingValue;

}