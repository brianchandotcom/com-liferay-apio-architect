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

package com.liferay.apio.architect.sample.internal.type;

import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.Vocabulary.BidirectionalModel;
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.identifier.Identifier;

/**
 * Represents a contact point exposed through the API. See <a
 * href="https://schema.org/ContactPoint">ContactPoint </a> for more
 * information.
 *
 * @author Alejandro Hernández
 */
@Type("ContactPointAnnotated")
public interface ContactPoint extends Identifier<Long> {

	/**
	 * Returns the contact point's contact option. See <a
	 * href="https://schema.org/contactOption">contactOption </a> for more
	 * information.
	 *
	 * @return the contact option
	 */
	@Field("contactOption")
	public String getContactOption();

	/**
	 * Returns the contact point's email. See <a
	 * href="https://schema.org/email">email </a> for more information.
	 *
	 * @return the contact point's email
	 */
	@Field("email")
	public String getEmail();

	/**
	 * Returns the contact point's fax number. See <a
	 * href="https://schema.org/faxNumber">faxNumber </a> for more information.
	 *
	 * @return the contact point's fax number
	 */
	@Field("getFaxNumber")
	public String getFaxNumber();

	/**
	 * Returns the contact point's ID.
	 *
	 * @return the contact point's ID
	 */
	@Id
	public Long getId();

	/**
	 * Returns the ID of the contact point's person.
	 *
	 * @return the ID of the contact point's person
	 */
	@BidirectionalModel(
		field = @Field("contactPoint"), modelClass = Person.class
	)
	@Field("person")
	public Long getPersonId();

	/**
	 * Returns the contact point's telephone number. See <a
	 * https://schema.org/telephone">telephone </a> for more information.
	 *
	 * @return the telephone number
	 */
	@Field("telephone")
	public String getTelephone();

}