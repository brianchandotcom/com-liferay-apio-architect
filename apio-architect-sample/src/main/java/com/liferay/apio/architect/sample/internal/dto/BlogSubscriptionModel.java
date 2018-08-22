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

package com.liferay.apio.architect.sample.internal.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a subscription. This is a mock class for sample purposes only.
 *
 * @author Alejandro Hernández
 */
public class BlogSubscriptionModel {

	public static BlogSubscriptionModel create(
		BlogPostingModel blogPostingModel, PersonModel personModel) {

		BlogSubscriptionModel blogSubscriptionModel = new BlogSubscriptionModel(
			blogPostingModel, personModel);

		_blogSubscriptions.put(
			blogSubscriptionModel._id, blogSubscriptionModel);

		return blogSubscriptionModel;
	}

	public BlogPostingModel getBlogPostingModel() {
		return _blogPostingModel;
	}

	public Long getId() {
		return _id;
	}

	public PersonModel getPersonModel() {
		return _personModel;
	}

	private BlogSubscriptionModel(
		BlogPostingModel blogPostingModel, PersonModel personModel) {

		_blogPostingModel = blogPostingModel;
		_personModel = personModel;
		_id = _count.getAndIncrement();
	}

	private static final Map<Long, BlogSubscriptionModel> _blogSubscriptions =
		new ConcurrentHashMap<>();
	private static final AtomicLong _count = new AtomicLong(0);

	private final BlogPostingModel _blogPostingModel;
	private final long _id;
	private final PersonModel _personModel;

}