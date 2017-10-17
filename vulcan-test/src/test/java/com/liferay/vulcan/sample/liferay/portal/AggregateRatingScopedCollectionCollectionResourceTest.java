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

package com.liferay.vulcan.sample.liferay.portal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

import com.liferay.vulcan.sample.liferay.portal.internal.resource.AggregateRatingScopedCollectionResource;

import java.util.function.Function;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Javier Gamarra
 */
public class AggregateRatingScopedCollectionCollectionResourceTest
	extends CollectionResourceTest {

	@Test
	public void testBuildRepresentor() {
		AggregateRatingScopedCollectionResource
			aggregateRatingScopedCollectionResource =
				new AggregateRatingScopedCollectionResource();

		aggregateRatingScopedCollectionResource.buildRepresentor(
			representorBuilderSpy);

		Mockito.verify(
			representorBuilderSpy
		).identifier(
			any(Function.class)
		);

		verifyIdentifier().addNumber(eq("bestRating"), any(Function.class));
		verifyIdentifier().addNumber(eq("ratingCount"), any(Function.class));
		verifyIdentifier().addNumber(eq("ratingValue"), any(Function.class));
		verifyIdentifier().addNumber(eq("worstRating"), any(Function.class));
		verifyIdentifier().addType(eq("AggregateRating"));
	}

}