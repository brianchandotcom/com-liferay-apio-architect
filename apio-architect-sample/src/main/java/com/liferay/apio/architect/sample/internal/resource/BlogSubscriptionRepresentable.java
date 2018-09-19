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

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.representor.Representable;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.sample.internal.resource.BlogPostingCollectionResource.BlogPostingIdentifier;
import com.liferay.apio.architect.sample.internal.resource.PersonCollectionResource.PersonIdentifier;
import com.liferay.apio.architect.sample.internal.type.BlogSubscription;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 */
@Component
public class BlogSubscriptionRepresentable
	implements Representable
		<BlogSubscription, Long,
		 BlogSubscriptionRepresentable.BlogSubscriptionIdentifier> {

	public static Form<BlogSubscriptionForm> buildForm(
		Form.Builder<BlogSubscriptionForm> formBuilder) {

		return formBuilder.title(
			__ -> "The blog subscription form"
		).description(
			__ ->
				"This form can be used to create or update a blog subscription"
		).constructor(
			BlogSubscriptionForm::new
		).addRequiredLinkedModel(
			"person", PersonIdentifier.class, BlogSubscriptionForm::setPerson
		).build();
	}

	@Override
	public String getName() {
		return "blog-subscription-old";
	}

	@Override
	public Representor<BlogSubscription> representor(
		Representor.Builder<BlogSubscription, Long> builder) {

		return builder.types(
			"BlogSubscriptionOld"
		).identifier(
			BlogSubscription::getId
		).addLinkedModel(
			"blog", BlogPostingIdentifier.class, BlogSubscription::getBlog
		).addLinkedModel(
			"person", PersonIdentifier.class, BlogSubscription::getPerson
		).build();
	}

	public static class BlogSubscriptionForm implements BlogSubscription {

		@Override
		public Long getBlog() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Long getId() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Long getPerson() {
			return _person;
		}

		public void setPerson(Long person) {
			_person = person;
		}

		private Long _person;

	}

	public interface BlogSubscriptionIdentifier extends Identifier<Long> {
	}

}