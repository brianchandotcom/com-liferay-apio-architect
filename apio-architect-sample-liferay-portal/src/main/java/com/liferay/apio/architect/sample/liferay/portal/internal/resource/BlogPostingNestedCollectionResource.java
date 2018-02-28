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

package com.liferay.apio.architect.sample.liferay.portal.internal.resource;

import static com.liferay.apio.architect.sample.liferay.portal.internal.idempotent.Idempotent.idempotent;

import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.NestedCollectionResource;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.apio.architect.sample.liferay.portal.internal.form.BlogPostingForm;
import com.liferay.apio.architect.sample.liferay.portal.internal.identifier.BlogPostingIdentifier;
import com.liferay.apio.architect.sample.liferay.portal.internal.identifier.WebSiteIdentifier;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.model.BlogsEntryModel;
import com.liferay.blogs.kernel.service.BlogsEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.service.ServiceContext;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the information necessary to expose <a
 * href="http://schema.org/BlogPosting">BlogPosting </a> resources through a web
 * API. The resources are mapped from the internal model {@code BlogsEntry}.
 *
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
public class BlogPostingNestedCollectionResource
	implements NestedCollectionResource<BlogsEntry, Long, BlogPostingIdentifier,
		Long, WebSiteIdentifier> {

	@Override
	public NestedCollectionRoutes<BlogsEntry, Long> collectionRoutes(
		NestedCollectionRoutes.Builder<BlogsEntry, Long> builder) {

		return builder.addGetter(
			this::_getPageItems
		).addCreator(
			this::_addBlogsEntry, (credentials, id) -> true,
			BlogPostingForm::buildForm
		).build();
	}

	@Override
	public String getName() {
		return "blog-postings";
	}

	@Override
	public ItemRoutes<BlogsEntry, Long> itemRoutes(
		ItemRoutes.Builder<BlogsEntry, Long> builder) {

		return builder.addGetter(
			_blogsService::getEntry
		).addRemover(
			idempotent(_blogsService::deleteEntry), (credentials, id) -> true
		).addUpdater(
			this::_updateBlogsEntry, (credentials, id) -> true,
			BlogPostingForm::buildForm
		).build();
	}

	@Override
	public Representor<BlogsEntry, Long> representor(
		Representor.Builder<BlogsEntry, Long> builder) {

		return builder.types(
			"BlogPosting"
		).identifier(
			BlogsEntry::getEntryId
		).addBidirectionalModel(
			"webSite", "blogs", WebSiteIdentifier.class,
			BlogsEntryModel::getGroupId
		).addDate(
			"createDate", BlogsEntry::getCreateDate
		).addDate(
			"displayDate", BlogsEntry::getDisplayDate
		).addDate(
			"modifiedDate", BlogsEntry::getModifiedDate
		).addDate(
			"publishedDate", BlogsEntry::getLastPublishDate
		).addLink(
			"license", "https://creativecommons.org/licenses/by/4.0"
		).addString(
			"alternativeHeadline", BlogsEntry::getSubtitle
		).addString(
			"articleBody", BlogsEntry::getContent
		).addString(
			"description", BlogsEntry::getDescription
		).addString(
			"fileFormat", __ -> "text/html"
		).addString(
			"headline", BlogsEntry::getTitle
		).build();
	}

	private BlogsEntry _addBlogsEntry(
		Long classNameClassPK, BlogPostingForm blogPostingForm) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(classNameClassPK);

		Try<BlogsEntry> blogsEntryTry = Try.fromFallible(
			() -> _blogsService.addEntry(
				blogPostingForm.getHeadline(),
				blogPostingForm.getAlternativeHeadline(),
				blogPostingForm.getDescription(),
				blogPostingForm.getArticleBody(),
				blogPostingForm.getDisplayDateMonth(),
				blogPostingForm.getDisplayDateDay(),
				blogPostingForm.getDisplayDateYear(),
				blogPostingForm.getDisplayDateHour(),
				blogPostingForm.getDisplayDateMinute(), false, false, null,
				null, null, null, serviceContext));

		return blogsEntryTry.getUnchecked();
	}

	private PageItems<BlogsEntry> _getPageItems(
		Pagination pagination, Long classNameClassPK) {

		List<BlogsEntry> blogsEntries = _blogsService.getGroupEntries(
			classNameClassPK, 0, pagination.getStartPosition(),
			pagination.getEndPosition());
		int count = _blogsService.getGroupEntriesCount(classNameClassPK, 0);

		return new PageItems<>(blogsEntries, count);
	}

	private BlogsEntry _updateBlogsEntry(
			Long blogsEntryId, BlogPostingForm blogPostingForm)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		BlogsEntry blogsEntry = _blogsService.getEntry(blogsEntryId);

		serviceContext.setScopeGroupId(blogsEntry.getGroupId());

		Try<BlogsEntry> blogsEntryTry = Try.fromFallible(
			() -> _blogsService.updateEntry(
				blogsEntryId, blogPostingForm.getHeadline(),
				blogPostingForm.getAlternativeHeadline(),
				blogPostingForm.getDescription(),
				blogPostingForm.getArticleBody(),
				blogPostingForm.getDisplayDateMonth(),
				blogPostingForm.getDisplayDateDay(),
				blogPostingForm.getDisplayDateYear(),
				blogPostingForm.getDisplayDateHour(),
				blogPostingForm.getDisplayDateMinute(), false, false, null,
				null, null, null, serviceContext));

		return blogsEntryTry.getUnchecked();
	}

	@Reference
	private BlogsEntryService _blogsService;

}