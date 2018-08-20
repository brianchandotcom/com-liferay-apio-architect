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

package com.liferay.apio.architect.sample.internal.form;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.form.Form.Builder;
import com.liferay.apio.architect.sample.internal.identifier.BlogPostingIdentifier;
import com.liferay.apio.architect.sample.internal.identifier.PersonIdentifier;

import java.util.Optional;

/**
 * Represents the values extracted from a blog subscription form.
 *
 * @author Javier Gamarra
 * @review
 */
public class BlogSubscriptionForm {

	/**
	 * Builds and returns a {@link Form} that generates a {@code
	 * BlogSubscriptionForm} that depends on the HTTP body.
	 *
	 * @param  formBuilder the {@code Form} builder
	 * @return the {@code BlogSubscriptionForm}
	 * @review
	 */
	public static Form<BlogSubscriptionForm> buildForm(
		Builder<BlogSubscriptionForm> formBuilder) {

		return formBuilder.title(
			__ -> "The blog subscription form"
		).description(
			__ ->
				"This form can be used to create or update a blog subscription"
		).constructor(
			BlogSubscriptionForm::new
		).addOptionalLinkedModel(
			"blogPosting", BlogPostingIdentifier.class,
			BlogSubscriptionForm::setBlog
		).addRequiredLinkedModel(
			"person", PersonIdentifier.class, BlogSubscriptionForm::setPerson
		).build();
	}

	public Optional<Long> getBlog() {
		return Optional.ofNullable(_blog);
	}

	public Long getPerson() {
		return _person;
	}

	public void setBlog(Long blog) {
		_blog = blog;
	}

	public void setPerson(Long person) {
		_person = person;
	}

	private Long _blog;
	private Long _person;

}