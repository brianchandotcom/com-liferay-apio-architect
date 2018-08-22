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
import com.liferay.apio.architect.custom.actions.CustomRoute;
import com.liferay.apio.architect.custom.actions.PostRoute;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.CollectionResource;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.sample.internal.auth.PermissionChecker;
import com.liferay.apio.architect.sample.internal.dao.BlogPostingModelService;
import com.liferay.apio.architect.sample.internal.dao.BlogSubscriptionModelService;
import com.liferay.apio.architect.sample.internal.dao.PersonModelService;
import com.liferay.apio.architect.sample.internal.dto.BlogPostingModel;
import com.liferay.apio.architect.sample.internal.dto.BlogSubscriptionModel;
import com.liferay.apio.architect.sample.internal.dto.PersonModel;
import com.liferay.apio.architect.sample.internal.dto.RatingModel;
import com.liferay.apio.architect.sample.internal.dto.ReviewModel;
import com.liferay.apio.architect.sample.internal.form.BlogPostingForm;
import com.liferay.apio.architect.sample.internal.form.BlogSubscriptionForm;
import com.liferay.apio.architect.sample.internal.type.BlogPosting;
import com.liferay.apio.architect.sample.internal.type.BlogSubscription;
import com.liferay.apio.architect.sample.internal.type.Comment;
import com.liferay.apio.architect.sample.internal.type.Person;

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
@Component(service = CollectionResource.class)
public class BlogPostingCollectionResource
	implements CollectionResource<BlogPostingModel, Long, BlogPosting> {

	@Override
	public CollectionRoutes<BlogPostingModel, Long> collectionRoutes(
		CollectionRoutes.Builder<BlogPostingModel, Long> builder) {

		return builder.addGetter(
			this::_getPageItems
		).addCustomRoute(
			_getSubscribeRoute(), this::_subscribePage, BlogSubscription.class,
			PermissionChecker::hasPermission, BlogSubscriptionForm::buildForm
		).addCreator(
			this::_addBlogPostingModel, Credentials.class,
			PermissionChecker::hasPermission, BlogPostingForm::buildForm
		).build();
	}

	@Override
	public String getName() {
		return "blog-postings";
	}

	@Override
	public ItemRoutes<BlogPostingModel, Long> itemRoutes(
		ItemRoutes.Builder<BlogPostingModel, Long> builder) {

		return builder.addGetter(
			this::_getBlogPostingModel
		).addCustomRoute(
			_getSubscribeRoute(), this::_subscribe, BlogSubscription.class,
			(credentials, blogId) -> hasPermission(credentials),
			BlogSubscriptionForm::buildForm
		).addRemover(
			this::_deleteBlogPostingModel, Credentials.class,
			(credentials, id) -> hasPermission(credentials)
		).addUpdater(
			this::_updateBlogPostingModel, Credentials.class,
			(credentials, id) -> hasPermission(credentials),
			BlogPostingForm::buildForm
		).build();
	}

	@Override
	public Representor<BlogPostingModel> representor(
		Representor.Builder<BlogPostingModel, Long> builder) {

		return builder.types(
			"BlogPosting"
		).identifier(
			BlogPostingModel::getId
		).addDate(
			"dateCreated", BlogPostingModel::getCreateDate
		).addDate(
			"dateModified", BlogPostingModel::getModifiedDate
		).addLinkedModel(
			"creator", Person.class, BlogPostingModel::getCreatorId
		).addNestedList(
			"review", BlogPostingModel::getReviewModels,
			reviewBuilder -> reviewBuilder.types(
				"Review"
			).addString(
				"reviewBody", ReviewModel::getBody
			).addNested(
				"reviewRating", ReviewModel::getRatingModel,
				ratingBuilder -> ratingBuilder.types(
					"Rating"
				).addLinkedModel(
					"author", Person.class, RatingModel::getAuthorId
				).addNumber(
					"bestRating", __ -> 5
				).addNumber(
					"ratingValue", RatingModel::getValue
				).addNumber(
					"worstRating", __ -> 0
				).build()
			).build()
		).addRelatedCollection(
			"comment", Comment.class
		).addString(
			"alternativeHeadline", BlogPostingModel::getSubtitle
		).addString(
			"articleBody", BlogPostingModel::getContent
		).addString(
			"fileFormat", __ -> "text/html"
		).addString(
			"headline", BlogPostingModel::getTitle
		).build();
	}

	private BlogPostingModel _addBlogPostingModel(
		BlogPostingForm blogPostingForm, Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		return _blogPostingModelService.create(
			blogPostingForm.getArticleBody(), blogPostingForm.getCreator(),
			blogPostingForm.getAlternativeHeadline(),
			blogPostingForm.getHeadline());
	}

	private void _deleteBlogPostingModel(long id, Credentials credentials) {
		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		_blogPostingModelService.remove(id);
	}

	private BlogPostingModel _getBlogPostingModel(long id) {
		Optional<BlogPostingModel> optional = _blogPostingModelService.get(id);

		return optional.orElseThrow(
			() -> new NotFoundException("Unable to get blog posting " + id));
	}

	private PageItems<BlogPostingModel> _getPageItems(Pagination pagination) {
		List<BlogPostingModel> blogPostingModels =
			_blogPostingModelService.getPage(
				pagination.getStartPosition(), pagination.getEndPosition());
		int count = _blogPostingModelService.getCount();

		return new PageItems<>(blogPostingModels, count);
	}

	private CustomRoute _getSubscribeRoute() {
		return new PostRoute() {

			@Override
			public String getName() {
				return "subscribe";
			}

		};
	}

	private BlogSubscriptionModel _subscribe(
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

	private BlogSubscriptionModel _subscribePage(
		Pagination pagination, BlogSubscriptionForm blogSubscriptionForm) {

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

	private BlogPostingModel _updateBlogPostingModel(
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