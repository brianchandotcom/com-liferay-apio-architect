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

package com.liferay.apio.architect.documentation.contributor;

import aQute.bnd.annotation.ProviderType;

import java.util.Locale;
import java.util.function.Function;

/**
 * Holds information about the documentation of the resources' properties and
 * operations.
 *
 * @author Víctor Galán
 */
public interface CustomDocumentation {

	/**
	 * Returns a function that returns the description of a property or
	 * operation for the locale.
	 *
	 * @param name the name of the property or operation
	 */
	public Function<Locale, String> getDescriptionFunction(String name);

	/**
	 * Creates a {@code CustomDocumentation} object.
	 */
	@ProviderType
	public interface Builder {

		/**
		 * Adds a description for the name.
		 *
		 * @param name the name
		 * @param description the description
		 */
		public Builder addDescription(String name, String description);

		/**
		 * Adds the name's localized description via the function that takes the
		 * locale and returns the description.
		 *
		 * @param name the name
		 * @param stringFunction the function
		 */
		public Builder addLocalizedDescription(
			String name, Function<Locale, String> stringFunction);

		/**
		 * Constructs and returns a {@code CustomDocumentation} instance with
		 * the information provided to the builder.
		 */
		public CustomDocumentation build();

	}

}