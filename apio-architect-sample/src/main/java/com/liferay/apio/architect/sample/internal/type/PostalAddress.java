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

import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.Type;

/**
 * Represents a postal address exposed through the API. See <a
 * href="https://schema.org/PostalAddress">PostalAddress </a> for more
 * information.
 *
 * @author Alejandro Hernández
 */
@Type("PostalAddressAnnotated")
public interface PostalAddress {

	/**
	 * Returns the postal address's country. See <a
	 * href="https://schema.org/addressCountry">addressCountry </a> for more
	 * information.
	 *
	 * @return the postal address's country
	 */
	@Field("addressCountry")
	public String getAddressCountry();

	/**
	 * Returns the postal address's locality. See <a
	 * href="https://schema.org/addressLocality">addressLocality </a> for more
	 * information.
	 *
	 * @return the postal address's locality
	 */
	@Field("addressLocality")
	public String getAddressLocality();

	/**
	 * Returns the postal address's region. See <a
	 * href="https://schema.org/addressRegion">addressRegion </a> for more
	 * information.
	 *
	 * @return the postal address's region
	 */
	@Field("addressRegion")
	public String getAddressRegion();

	/**
	 * Returns the address's postal code. See <a
	 * href="https://schema.org/postalCode">postalCode </a> for more
	 * information.
	 *
	 * @return the address's postal code
	 */
	@Field("postalCode")
	public String getPostalCode();

	/**
	 * Returns the street address. See <a
	 * href="https://schema.org/streetAddress">streetAddress </a> for more
	 * information.
	 *
	 * @return the street address
	 */
	@Field("streetAddress")
	public String getStreetAddress();

}