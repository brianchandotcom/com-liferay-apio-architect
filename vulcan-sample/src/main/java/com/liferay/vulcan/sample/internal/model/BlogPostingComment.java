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

package com.liferay.vulcan.sample.internal.model;

import com.github.javafaker.DateAndTime;
import com.github.javafaker.Faker;
import com.github.javafaker.Shakespeare;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instances of this class represents a {@link BlogPosting} comment. This is a
 * mock class for sample purposes. It contains methods for
 * retrieving/updating/deleting blog post comments and a in-memory database with
 * fake data.
 *
 * @author Alejandro Hernández
 * @review
 */
public class BlogPostingComment {

	/**
	 * Adds a new {@code BlogPostingComment} to the database.
	 *
	 * @param  authorId the ID of the author of the {@link BlogPosting} comment.
	 * @param  blogPostingId the ID of the {@link BlogPosting} comment.
	 * @param  content the content of the {@link BlogPosting} comment.
	 * @return the added {@code BlogPostingComment}.
	 * @review
	 */
	public static BlogPostingComment addBlogPostingComment(
		Long authorId, Long blogPostingId, String content) {

		long blogPostingCommentId = _count.incrementAndGet();

		BlogPostingComment blogPostingComment = new BlogPostingComment(
			blogPostingCommentId, blogPostingId, authorId, content, new Date(), new Date());

		Map<Long, BlogPostingComment> blogPostComments =
			_blogPostCommentsMap.computeIfAbsent(
				blogPostingId, __ -> new HashMap<>());

		blogPostComments.put(blogPostingCommentId, blogPostingComment);

		return blogPostingComment;
	}

	/**
	 * Deletes a {@code BlogPostingComment} with a certain {@code ID} from the
	 * database.
	 *
	 * @param  blogPostingCommentId the ID of the {@link BlogPosting} comment to delete.
	 * @review
	 */
	public static void deleteBlogPostingComment(long blogPostingCommentId) {
		_blogPostCommentsMap.remove(blogPostingCommentId);
	}

	/**
	 * Returns a {@code BlogPostingComment} with a certain {@code ID} from the
	 * database if present. Returns {@code Optional#empty()} otherwise.
	 *
	 * @param  blogPostingCommentId the ID of the blog post comment to retrieve.
	 * @return the {@code BlogPostingComment} for the requested ID if present;
	 *         {@code Optional#empty()} otherwise.
	 * @review
	 */
	public static Optional<BlogPostingComment> getBlogPostingComment(long blogPostingCommentId) {
		Collection<Map<Long, BlogPostingComment>> blogPostComments =
			_blogPostCommentsMap.values();

		Stream<Map<Long, BlogPostingComment>> blogPostCommentsStream =
			blogPostComments.stream();

		return blogPostCommentsStream.map(
			Map::values
		).map(
			Collection::stream
		).flatMap(
			stream -> stream
		).filter(
			blogPostingComment -> blogPostingComment.getBlogPostingCommentId() == blogPostingCommentId
		).findFirst();
	}

	/**
	 * Returns a page of {@code BlogPostingComment} of {@link BlogPosting} from
	 * the database.
	 *
	 * @param  blogPostingId the ID of the {@link BlogPosting}.
	 * @param  start the start position.
	 * @param  end the end position.
	 * @return the list of blog post comments between {@code start} and {@code
	 *         end} of a {@link BlogPosting}.
	 * @review
	 */
	public static List<BlogPostingComment> getBlogPostingComments(
		long blogPostingId, int start, int end) {

		Map<Long, BlogPostingComment> blogPostComments =
			_blogPostCommentsMap.get(blogPostingId);

		Collection<BlogPostingComment> blogPostingCommentsValues =
			blogPostComments.values();

		Stream<BlogPostingComment> stream = blogPostingCommentsValues.stream();

		return stream.skip(
			start
		).limit(
			end
		).collect(
			Collectors.toList()
		);
	}

	/**
	 * Return the total number of comments for a {@link BlogPosting} in the
	 * database.
	 *
	 * @param  blogPostingId the ID of the {@link BlogPosting}.
	 * @return the total number of comments for a {@link BlogPosting} in the
	 *         database.
	 * @review
	 */
	public static int getBlogPostingCommentsCount(long blogPostingId) {
		if (_blogPostCommentsMap.containsKey(blogPostingId)) {
			Map<Long, BlogPostingComment> blogPostComments =
				_blogPostCommentsMap.get(blogPostingId);

			return blogPostComments.size();
		}

		return 0;
	}

	/**
	 * Updates a {@code BlogPostingComment} with a certain {@code ID} in the
	 * database if present.
	 *
	 * @param  blogPostingCommentId the ID of the {@link BlogPosting} comment to update.
	 * @param  content the content of the {@link BlogPosting} comment.
	 * @return the updated {@code BlogPostingComment} if present; {@code
	 *         Optional#empty()} otherwise.
	 * @review
	 */
	public static Optional<BlogPostingComment> updateBlogPostingComment(
		long blogPostingCommentId, String content) {

		Optional<BlogPostingComment> oldBlogPostingComment =
			getBlogPostingComment(blogPostingCommentId);

		Optional<BlogPostingComment> newBlogPostingComment =
			oldBlogPostingComment.map(
				blogPostingComment -> {
					Date createDate = blogPostingComment.getCreateDate();

					long authorId = blogPostingComment.getAuthorId();

					long blogPostingId = blogPostingComment.getBlogPostId();

					return new BlogPostingComment(
						blogPostingCommentId, blogPostingId, authorId, content, createDate,
						new Date());
				});

		newBlogPostingComment.ifPresent(
			blogPostingComment -> {
				long blogPostingId = blogPostingComment.getBlogPostId();

				Map<Long, BlogPostingComment> blogPostComments =
					_blogPostCommentsMap.computeIfAbsent(
						blogPostingId, __ -> new HashMap<>());

				blogPostComments.put(blogPostingCommentId, blogPostingComment);
			});

		return newBlogPostingComment;
	}

	/**
	 * The ID of the author of this {@code BlogPostingComment}.
	 *
	 * @return the ID of the author of the {@link BlogPosting} comment.
	 * @review
	 */
	public Long getAuthorId() {
		return _authorId;
	}

	/**
	 * Returns the ID of the {@link BlogPosting} of this {@code
	 * BlogPostingComment}.
	 *
	 * @return the ID of the {@link BlogPosting} of the {@link BlogPosting}
	 *         comment.
	 * @review
	 */
	public Long getBlogPostId() {
		return _blogPostingId;
	}

	/**
	 * Returns the content of this {@code BlogPostingComment}.
	 *
	 * @return the content of the {@link BlogPosting} comment.
	 * @review
	 */
	public String getContent() {
		return _content;
	}

	/**
	 * The create date of this {@code BlogPostingComment}.
	 *
	 * @return the create date of the {@link BlogPosting} comment.
	 * @review
	 */
	public Date getCreateDate() {
		return _createDate;
	}

	/**
	 * The ID of this {@code BlogPostingComment}.
	 *
	 * @return the ID of the {@link BlogPosting} comment.
	 * @review
	 */
	public long getBlogPostingCommentId() {
		return _blogPostingCommentId;
	}

	/**
	 * Returns the modified date of this {@code BlogPostingComment}.
	 *
	 * @return the modified date of the {@link BlogPosting} comment.
	 * @review
	 */
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	private BlogPostingComment(
		long blogPostingCommentId, Long blogPostingId, Long authorId, String content,
		Date createDate, Date modifiedDate) {

		_blogPostingCommentId = blogPostingCommentId;
		_blogPostingId = blogPostingId;
		_authorId = authorId;
		_content = content;
		_createDate = createDate;
		_modifiedDate = modifiedDate;
	}

	private static Map<Long, Map<Long, BlogPostingComment>>
		_blogPostCommentsMap;
	private static final AtomicLong _count = new AtomicLong(0);

	static {
		_blogPostCommentsMap = new HashMap<>();

		Random random = new Random();

		for (long i = 0; i < BlogPosting.getBlogPostingCount(); i++) {
			HashMap<Long, BlogPostingComment> blogPostComments =
				new HashMap<>();

			for (int j = 0; j < random.nextInt(70); j++) {
				long blogPostingCommentId = _count.getAndIncrement();

				Faker faker = new Faker();

				long creatorId = random.nextInt(Person.getPeopleCount());

				Shakespeare shakespeare = faker.shakespeare();

				String content = shakespeare.hamletQuote();

				DateAndTime dateAndTime = faker.date();

				Date createDate = dateAndTime.past(400, TimeUnit.DAYS);

				BlogPostingComment blogPostingComment = new BlogPostingComment(
					blogPostingCommentId, i, creatorId, content, createDate, createDate);

				blogPostComments.put(blogPostingCommentId, blogPostingComment);
			}

			_blogPostCommentsMap.put(i, blogPostComments);
		}
	}

	private final Long _authorId;
	private final Long _blogPostingId;
	private final String _content;
	private final Date _createDate;
	private final long _blogPostingCommentId;
	private final Date _modifiedDate;

}