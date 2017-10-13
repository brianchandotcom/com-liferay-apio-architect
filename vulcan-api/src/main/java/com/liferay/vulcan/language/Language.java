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

package com.liferay.vulcan.language;

import java.util.Enumeration;
import java.util.Locale;

/**
 * Instances of this interface represent the information about the requested
 * language/es.
 *
 * @author Alejandro Hernández
 * @review
 */
public interface Language {

	/**
	 * Returns an {@code Enumeration} of {@code Locale} in decreasing order.
	 *
	 * <p>
	 * The enumeration starts with the first locale added on the {@code
	 * Accept-Language} header and continue with the rest of the header.
	 * </p>
	 *
	 * <p>
	 * If the request doesn't have an {@code Accept-Language} header, this
	 * method returns an {@code Enumeration} containing the default locale for
	 * the server.
	 * </p>
	 *
	 * @return an {@code Enumeration} of the preferred {@code Locale} of the
	 *         request, if the {@code Accept-Language} header is present; an
	 *         {@code Enumeration} containing the default locale for the server
	 *         otherwise.
	 * @review
	 */
	public Enumeration<Locale> getLocales();

	/**
	 * Returns the first {@code Locale} added on the {@code Accept-Language}
	 * header.
	 *
	 * <p>
	 * If the request doesn't have an {@code Accept-Language} header, this
	 * method returns the default locale for the server.
	 * </p>
	 *
	 * @return the first {@code Locale} of the request, if the {@code
	 *         Accept-Language} header is present; an {@code Enumeration}
	 *         containing the default locale for the server otherwise.
	 * @review
	 */
	public Locale getPreferredLocale();

}