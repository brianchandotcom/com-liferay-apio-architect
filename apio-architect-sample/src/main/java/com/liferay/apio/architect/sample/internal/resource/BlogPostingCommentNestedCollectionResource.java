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
import com.liferay.apio.architect.sample.internal.dao.BlogPostingCommentModelService;
import com.liferay.apio.architect.sample.internal.dto.BlogPostingCommentModel;
import com.liferay.apio.architect.sample.internal.form.BlogPostingCommentCreatorForm;
import com.liferay.apio.architect.sample.internal.form.BlogPostingCommentUpdaterForm;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides all the information necessary to expose <a
 * href="http://schema.org/Comment">Comment</a> resources through a web API. The
 * resources are mapped from the internal {@link BlogPostingCommentModel} model.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class BlogPostingCommentNestedCollectionResource {

	public BlogPostingCommentModel addBlogPostingComment(
		Long blogPostingModelId,
		BlogPostingCommentCreatorForm blogPostingCommentCreatorForm,
		Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		return _blogPostingCommentModelService.create(
			blogPostingCommentCreatorForm.getAuthor(), blogPostingModelId,
			blogPostingCommentCreatorForm.getText());
	}

	public void deleteBlogPostingComment(long id, Credentials credentials) {
		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		_blogPostingCommentModelService.remove(id);
	}

	public BlogPostingCommentModel getBlogPostingComment(long id) {
		Optional<BlogPostingCommentModel> optional =
			_blogPostingCommentModelService.get(id);

		return optional.orElseThrow(
			() -> new NotFoundException(
				"Unable to get blog posting comment " + id));
	}

	public PageItems<BlogPostingCommentModel> getPageItems(
		Pagination pagination, Long blogPostingModelId) {

		List<BlogPostingCommentModel> blogPostingCommentModels =
			_blogPostingCommentModelService.getPage(
				blogPostingModelId, pagination.getStartPosition(),
				pagination.getEndPosition());
		int count = _blogPostingCommentModelService.getCount(
			blogPostingModelId);

		return new PageItems<>(blogPostingCommentModels, count);
	}

	public BlogPostingCommentModel updateBlogPostingComment(
		long id, BlogPostingCommentUpdaterForm blogPostingCommentUpdaterForm,
		Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		Optional<BlogPostingCommentModel> optional =
			_blogPostingCommentModelService.update(
				id, blogPostingCommentUpdaterForm.getText());

		return optional.orElseThrow(
			() -> new NotFoundException(
				"Unable to get blog posting comment " + id));
	}

	@Reference
	private BlogPostingCommentModelService _blogPostingCommentModelService;

}