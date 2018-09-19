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

import com.liferay.apio.architect.identifier.Identifier;

import java.util.Date;
import java.util.List;

/**
 * Instances of this interface represent a person exposed through the API.
 *
 * @author Alejandro Hernández
 * @see    <a href="https://schema.org/Person">Person</a>
 * @review
 */
public interface Person extends Identifier<Long> {

	/**
	 * Returns the person's birth date.
	 *
	 * @return the person's birth date
	 * @see    <a href="https://schema.org/birthDate">birthDate</a>
	 * @review
	 */
	public Date getBirthDate();

	/**
	 * Returns the person's email.
	 *
	 * @return the person's email
	 * @see    <a href="https://schema.org/email">email</a>
	 * @review
	 */
	public String getEmail();

	/**
	 * Returns the person's family name.
	 *
	 * @return the person's family name
	 * @see    <a href="https://schema.org/familyName">familyName</a>
	 * @review
	 */
	public String getFamilyName();

	/**
	 * Returns the person's given name.
	 *
	 * @return the person's given name
	 * @see    <a href="https://schema.org/givenName">givenName</a>
	 * @review
	 */
	public String getGivenName();

	/**
	 * Returns the person's ID.
	 *
	 * @return the person's ID
	 * @review
	 */
	public Long getId();

	/**
	 * Returns the person's image.
	 *
	 * @return the person's image
	 * @see    <a href="https://schema.org/image">image</a>
	 * @review
	 */
	public String getImage();

	/**
	 * Returns the list of the person's job titles.
	 *
	 * @return the list of the person's job titles.
	 * @see    <a href="https://schema.org/jobTitle">jobTitle</a>
	 * @review
	 */
	public List<String> getJobTitle();

	/**
	 * Returns the person's full name.
	 *
	 * @return the person's full name
	 * @see    <a href="https://schema.org/name">name</a>
	 * @review
	 */
	public String getName();

	/**
	 * Returns the person's postal address.
	 *
	 * @return the person's postal address
	 * @see    <a href="https://schema.org/postalAddress">postalAddress</a>
	 * @review
	 */
	public PostalAddress getPostalAddress();

}