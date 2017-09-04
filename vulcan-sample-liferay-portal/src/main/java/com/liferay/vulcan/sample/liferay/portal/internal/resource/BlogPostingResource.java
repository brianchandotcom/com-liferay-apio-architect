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

package com.liferay.vulcan.sample.liferay.portal.internal.resource;

import com.liferay.blogs.kernel.exception.NoSuchEntryException;
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryService;
import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.vulcan.identifier.LongIdentifier;
import com.liferay.vulcan.liferay.portal.identifier.ClassNameClassPKIdentifier;
import com.liferay.vulcan.liferay.portal.identifier.creator.ClassNameClassPKIdentifierCreator;
import com.liferay.vulcan.pagination.PageItems;
import com.liferay.vulcan.pagination.Pagination;
import com.liferay.vulcan.resource.Resource;
import com.liferay.vulcan.resource.Routes;
import com.liferay.vulcan.resource.builder.RepresentorBuilder;
import com.liferay.vulcan.resource.builder.RoutesBuilder;
import com.liferay.vulcan.result.Try;
import com.liferay.vulcan.sample.liferay.portal.rating.AggregateRating;
import com.liferay.vulcan.sample.liferay.portal.rating.AggregateRatingService;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides all the necessary information to expose <a
 * href="http://schema.org/BlogPosting">BlogPosting</a> resources through a web
 * API.
 *
 * The resources are mapped from the internal {@link BlogsEntry} model.
 *
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true)
public class BlogPostingResource
	implements Resource<BlogsEntry, LongIdentifier> {

	@Override
	public void buildRepresentor(
		RepresentorBuilder<BlogsEntry, LongIdentifier> representorBuilder) {

		Function<Date, Object> formatFunction = date -> {
			if (date == null) {
				return null;
			}

			DateFormat dateFormat = DateUtil.getISO8601Format();

			return dateFormat.format(date);
		};

		representorBuilder.identifier(
			blogsEntry -> blogsEntry::getEntryId
		).addBidirectionalModel(
			"group", "blogs", Group.class, this::_getGroupOptional
		).addEmbeddedModel(
			"aggregateRating", AggregateRating.class,
			this::_getAggregateRatingOptional
		).addEmbeddedModel(
			"creator", User.class, this::_getUserOptional
		).addField(
			"alternativeHeadline", BlogsEntry::getSubtitle
		).addField(
			"articleBody", BlogsEntry::getContent
		).addField(
			"createDate",
			blogsEntry -> formatFunction.apply(blogsEntry.getCreateDate())
		).addField(
			"description", BlogsEntry::getDescription
		).addField(
			"displayDate",
			blogsEntry -> formatFunction.apply(blogsEntry.getDisplayDate())
		).addField(
			"fileFormat", blogsEntry -> "text/html"
		).addField(
			"headline", BlogsEntry::getTitle
		).addField(
			"modifiedDate",
			blogsEntry -> formatFunction.apply(blogsEntry.getModifiedDate())
		).addField(
			"publishedDate",
			blogsEntry -> formatFunction.apply(blogsEntry.getLastPublishDate())
		).addLink(
			"license", "https://creativecommons.org/licenses/by/4.0"
		).addLinkedModel(
			"author", User.class, this::_getUserOptional
		).addRelatedCollection(
			"comment", Comment.class
		).addType(
			"BlogPosting"
		);
	}

	@Override
	public String getPath() {
		return "blogs";
	}

	@Override
	public Routes<BlogsEntry> routes(
		RoutesBuilder<BlogsEntry, LongIdentifier> routesBuilder) {

		return routesBuilder.collectionItem(
			this::_getBlogsEntry
		).collectionPage(
			this::_getPageItems, LongIdentifier.class
		).postCollectionItem(
			this::_addBlogsEntry, LongIdentifier.class
		).build();
	}

	private BlogsEntry _addBlogsEntry(
		LongIdentifier groupLongIdentifier, Map<String, Object> body) {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(groupLongIdentifier.getIdAsLong());

		String title = (String)body.get("headline");
		String subtitle = (String)body.get("alternativeHeadline");
		String description = (String)body.get("description");
		String content = (String)body.get("articleBody");
		String displayDateString = (String)body.get("displayDate");

		Supplier<BadRequestException> incorrectBodyExceptionSupplier =
			() -> new BadRequestException("Incorrect body");

		if (Validator.isNull(title) || Validator.isNull(subtitle) ||
			Validator.isNull(description) || Validator.isNull(content) ||
			Validator.isNull(displayDateString)) {

			throw incorrectBodyExceptionSupplier.get();
		}

		Try<DateFormat> dateFormatTry = Try.success(
			DateUtil.getISO8601Format());

		Date displayDate = dateFormatTry.map(
			dateFormat -> dateFormat.parse(displayDateString)
		).mapFailMatching(
			ParseException.class, incorrectBodyExceptionSupplier
		).getUnchecked();

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(displayDate);

		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int year = calendar.get(Calendar.YEAR);
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);

		Try<BlogsEntry> blogsEntryTry = Try.fromFallible(
			() -> _blogsService.addEntry(
				title, subtitle, description, content, month, day, year, hour,
				minute, false, false, null, null, null, null, serviceContext));

		return blogsEntryTry.getUnchecked();
	}

	private Optional<AggregateRating> _getAggregateRatingOptional(
		BlogsEntry blogsEntry) {

		ClassNameClassPKIdentifier classNameClassPKIdentifier =
			_classNameClassPKIdentifierCreator.create(
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

		return Optional.of(
			_aggregateRatingService.getAggregateRating(
				classNameClassPKIdentifier));
	}

	private BlogsEntry _getBlogsEntry(LongIdentifier blogsEntryLongIdentifier) {
		try {
			return _blogsService.getEntry(
				blogsEntryLongIdentifier.getIdAsLong());
		}
		catch (NoSuchEntryException | PrincipalException e) {
			throw new NotFoundException(
				"Unable to get blogs entry " +
					blogsEntryLongIdentifier.getIdAsLong(),
				e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private Optional<Group> _getGroupOptional(BlogsEntry blogsEntry) {
		try {
			return Optional.of(
				_groupLocalService.getGroup(blogsEntry.getGroupId()));
		}
		catch (NoSuchGroupException nsge) {
			throw new NotFoundException(
				"Unable to get group " + blogsEntry.getGroupId(), nsge);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<BlogsEntry> _getPageItems(
		Pagination pagination, LongIdentifier groupLongIdentifier) {

		List<BlogsEntry> blogsEntries = _blogsService.getGroupEntries(
			groupLongIdentifier.getIdAsLong(), 0, pagination.getStartPosition(),
			pagination.getEndPosition());
		int count = _blogsService.getGroupEntriesCount(
			groupLongIdentifier.getIdAsLong(), 0);

		return new PageItems<>(blogsEntries, count);
	}

	private Optional<User> _getUserOptional(BlogsEntry blogsEntry) {
		try {
			return Optional.ofNullable(
				_userService.getUserById(blogsEntry.getUserId()));
		}
		catch (NoSuchUserException | PrincipalException e) {
			throw new NotFoundException(
				"Unable to get user " + blogsEntry.getUserId(), e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private AggregateRatingService _aggregateRatingService;

	@Reference
	private BlogsEntryService _blogsService;

	@Reference
	private ClassNameClassPKIdentifierCreator
		_classNameClassPKIdentifierCreator;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserService _userService;

}