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

import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.sample.internal.dao.BlogPostingModelService;
import com.liferay.apio.architect.sample.internal.dao.BlogSubscriptionModelService;
import com.liferay.apio.architect.sample.internal.dao.PersonModelService;
import com.liferay.apio.architect.sample.internal.dto.BlogPostingModel;
import com.liferay.apio.architect.sample.internal.dto.BlogSubscriptionModel;
import com.liferay.apio.architect.sample.internal.dto.PersonModel;
import com.liferay.apio.architect.sample.internal.form.BlogPostingForm;
import com.liferay.apio.architect.sample.internal.form.BlogSubscriptionForm;

import java.util.List;
import java.util.Optional;

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

	public BlogPostingModel addBlogPostingModel(
		BlogPostingForm blogPostingForm, Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		return _blogPostingModelService.create(
			blogPostingForm.getArticleBody(), blogPostingForm.getCreator(),
			blogPostingForm.getAlternativeHeadline(),
			blogPostingForm.getHeadline());
	}

	public void deleteBlogPostingModel(long id, Credentials credentials) {
		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		_blogPostingModelService.remove(id);
	}

	public BlogPostingModel getBlogPostingModel(long id) {
		Optional<BlogPostingModel> optional = _blogPostingModelService.get(id);

		return optional.orElseThrow(
			() -> new NotFoundException("Unable to get blog posting " + id));
	}

	public PageItems<BlogPostingModel> getPageItems(Pagination pagination) {
		List<BlogPostingModel> blogPostingModels =
			_blogPostingModelService.getPage(
				pagination.getStartPosition(), pagination.getEndPosition());
		int count = _blogPostingModelService.getCount();

		return new PageItems<>(blogPostingModels, count);
	}

	public BlogSubscriptionModel subscribe(
		Long blogId, BlogSubscriptionForm blogSubscriptionForm) {

		Optional<PersonModel> personModelOptional = _personModelService.get(
			blogSubscriptionForm.getPerson());

		Optional<BlogPostingModel> blogPostingModelOptional =
			_blogPostingModelService.get(blogId);

		if (personModelOptional.isPresent()) {
			return _blogSubscriptionModelService.create(
				blogPostingModelOptional.get(), personModelOptional.get());
		}

		throw new BadRequestException();
	}

	public BlogSubscriptionModel subscribePage(
		BlogSubscriptionForm blogSubscriptionForm) {

		Optional<PersonModel> personModelOptional = _personModelService.get(
			blogSubscriptionForm.getPerson());

		Optional<Long> blog = blogSubscriptionForm.getBlog();

		Optional<BlogPostingModel> blogPostingModelOptional = blog.flatMap(
			_blogPostingModelService::get);

		if (personModelOptional.isPresent() &&
			blogPostingModelOptional.isPresent()) {

			return _blogSubscriptionModelService.create(
				blogPostingModelOptional.get(), personModelOptional.get());
		}

		throw new BadRequestException();
	}

	public BlogPostingModel updateBlogPostingModel(
		long id, BlogPostingForm blogPostingForm, Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		Optional<BlogPostingModel> optional = _blogPostingModelService.update(
			id, blogPostingForm.getArticleBody(), blogPostingForm.getCreator(),
			blogPostingForm.getAlternativeHeadline(),
			blogPostingForm.getHeadline());

		return optional.orElseThrow(
			() -> new NotFoundException("Unable to get blog posting " + id));
	}

	@Reference
	private BlogPostingModelService _blogPostingModelService;

	@Reference
	private BlogSubscriptionModelService _blogSubscriptionModelService;

	@Reference
	private PersonModelService _personModelService;

}