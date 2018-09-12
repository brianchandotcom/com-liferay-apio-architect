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

package com.liferay.apio.architect.sample.internal.resource;

import static com.liferay.apio.architect.sample.internal.auth.PermissionChecker.hasPermission;
import static com.liferay.apio.architect.sample.internal.converter.BlogPostingConverter.toBlogPosting;
import static com.liferay.apio.architect.sample.internal.converter.BlogSubscriptionConverter.toBlogSubscription;

import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.sample.internal.converter.BlogPostingConverter;
import com.liferay.apio.architect.sample.internal.dao.BlogPostingModelService;
import com.liferay.apio.architect.sample.internal.dao.BlogSubscriptionModelService;
import com.liferay.apio.architect.sample.internal.dao.PersonModelService;
import com.liferay.apio.architect.sample.internal.dto.BlogPostingModel;
import com.liferay.apio.architect.sample.internal.dto.BlogSubscriptionModel;
import com.liferay.apio.architect.sample.internal.dto.PersonModel;
import com.liferay.apio.architect.sample.internal.form.BlogPostingForm;
import com.liferay.apio.architect.sample.internal.form.BlogSubscriptionForm;
import com.liferay.apio.architect.sample.internal.type.BlogPosting;
import com.liferay.apio.architect.sample.internal.type.BlogSubscription;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides all the information necessary to expose <a
 * href="http://schema.org/BlogPosting">BlogPosting</a> resources through a web
 * API. The resources are mapped from the internal {@link BlogPostingModel}
 * model.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class BlogPostingCollectionResource {

	public BlogPosting addBlogPosting(
		BlogPostingForm blogPostingForm, Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		BlogPostingModel blogPostingModel = _blogPostingModelService.create(
			blogPostingForm.getArticleBody(), blogPostingForm.getCreator(),
			blogPostingForm.getAlternativeHeadline(),
			blogPostingForm.getHeadline());

		return toBlogPosting(blogPostingModel);
	}

	public void deleteBlogPosting(long id, Credentials credentials) {
		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		_blogPostingModelService.remove(id);
	}

	public BlogPosting getBlogPosting(long id) {
		Optional<BlogPostingModel> optional = _blogPostingModelService.get(id);

		return optional.map(
			BlogPostingConverter::toBlogPosting
		).orElseThrow(
			() -> new NotFoundException("Unable to get blog posting " + id)
		);
	}

	public PageItems<BlogPosting> getPageItems(Pagination pagination) {
		List<BlogPostingModel> blogPostingModels =
			_blogPostingModelService.getPage(
				pagination.getStartPosition(), pagination.getEndPosition());
		int count = _blogPostingModelService.getCount();

		Stream<BlogPostingModel> stream = blogPostingModels.stream();

		List<BlogPosting> blogPostings = stream.map(
			BlogPostingConverter::toBlogPosting
		).collect(
			Collectors.toList()
		);

		return new PageItems<>(blogPostings, count);
	}

	public BlogSubscription subscribe(
		Long blogId, BlogSubscriptionForm blogSubscriptionForm) {

		Optional<PersonModel> personModelOptional = _personModelService.get(
			blogSubscriptionForm.getPerson());

		Optional<BlogPostingModel> blogPostingModelOptional =
			_blogPostingModelService.get(blogId);

		if (personModelOptional.isPresent() &&
			blogPostingModelOptional.isPresent()) {

			BlogSubscriptionModel blogSubscriptionModel =
				_blogSubscriptionModelService.create(
					blogPostingModelOptional.get(), personModelOptional.get());

			return toBlogSubscription(blogSubscriptionModel);
		}

		throw new BadRequestException();
	}

	public BlogSubscription subscribePage(
		BlogSubscriptionForm blogSubscriptionForm) {

		Optional<PersonModel> personModelOptional = _personModelService.get(
			blogSubscriptionForm.getPerson());

		Optional<Long> blog = blogSubscriptionForm.getBlog();

		Optional<BlogPostingModel> blogPostingModelOptional = blog.flatMap(
			_blogPostingModelService::get);

		if (personModelOptional.isPresent() &&
			blogPostingModelOptional.isPresent()) {

			BlogSubscriptionModel blogSubscriptionModel =
				_blogSubscriptionModelService.create(
					blogPostingModelOptional.get(), personModelOptional.get());

			return toBlogSubscription(blogSubscriptionModel);
		}

		throw new BadRequestException();
	}

	public BlogPosting updateBlogPostingModel(
		long id, BlogPostingForm blogPostingForm, Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		Optional<BlogPostingModel> optional = _blogPostingModelService.update(
			id, blogPostingForm.getArticleBody(), blogPostingForm.getCreator(),
			blogPostingForm.getAlternativeHeadline(),
			blogPostingForm.getHeadline());

		return optional.map(
			BlogPostingConverter::toBlogPosting
		).orElseThrow(
			() -> new NotFoundException("Unable to get blog posting " + id)
		);
	}

	@Reference
	private BlogPostingModelService _blogPostingModelService;

	@Reference
	private BlogSubscriptionModelService _blogSubscriptionModelService;

	@Reference
	private PersonModelService _personModelService;

}