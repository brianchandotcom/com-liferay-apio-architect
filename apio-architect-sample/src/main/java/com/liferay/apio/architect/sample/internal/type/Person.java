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
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.RelativeURL;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.identifier.Identifier;

import java.util.Date;
import java.util.List;

/**
 * Represents a person resource exposed through the API. See <a
 * href="https://schema.org/Person">Person </a> for more information.
 *
 * @author Alejandro Hernández
 */
@Type("PersonAnnotated")
public interface Person extends Identifier<Long> {

	/**
	 * Returns the person's birth date. See <a
	 * href="https://schema.org/birthDate">birthDate </a> for more information.
	 *
	 * @return the person's birth date
	 */
	@Field("birthDate")
	public Date getBirthDate();

	/**
	 * Returns the person's email. See <a href="https://schema.org/email">email
	 * </a> for more information.
	 *
	 * @return the person's email
	 */
	@Field("email")
	public String getEmail();

	/**
	 * Returns the person's family name. See <a
	 * href="https://schema.org/familyName">familyName </a> for more
	 * information.
	 *
	 * @return the person's family name
	 */
	@Field("familyName")
	public String getFamilyName();

	/**
	 * Returns the person's given name. See <a
	 * href="https://schema.org/givenName">givenName </a> for more information.
	 *
	 * @return the person's given name
	 */
	@Field("givenName")
	public String getGivenName();

	/**
	 * Returns the person's ID.
	 *
	 * @return the person's ID
	 */
	@Id
	public Long getId();

	/**
	 * Returns the person's image URL. See <a
	 * href="https://schema.org/image">image </a> for more information.
	 *
	 * @return the person's image URL
	 */
	@Field("image")
	@RelativeURL(fromApplication = true)
	public String getImage();

	/**
	 * Returns the list of the person's job titles.
	 *
	 * @return the list of job titles
	 */
	@Field("jobTitle")
	public List<String> getJobTitles();

	/**
	 * Returns the person's full name. See <a
	 * href="https://schema.org/name">name </a> for more information.
	 *
	 * @return the person's full name
	 */
	@Field("name")
	public String getName();

	/**
	 * Returns the person's postal address. See <a
	 * href="https://schema.org/PostalAddress">PostalAddress </a> for more
	 * information.
	 *
	 * @return the postal address
	 */
	@Field("postalAddress")
	public PostalAddress getPostalAddress();

}