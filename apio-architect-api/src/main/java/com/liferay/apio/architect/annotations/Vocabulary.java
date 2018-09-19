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

package com.liferay.apio.architect.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation provides namespace for vocabulary annotations. Use one of the
 * nested annotation.
 *
 * @author Alejandro Hernández
 */
public @interface Vocabulary {

	/**
	 * Provides information about a type. This annotation should always be used
	 * on an interface.
	 *
	 * <p>
	 * Annotation has attributes to customize the schema URL (<a
	 * href="https://schema.org">schema.org</a> by default) and the description
	 * of the type (in the case the type is a custom one).
	 * </p>
	 *
	 * @review
	 */
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface Type {

		/**
		 * Returns the type description, if it is a custom one. Returns empty
		 * string otherwise.
		 *
		 * @return the type description, if it is a custom one; empty string
		 *         otherwise
		 * @review
		 */
		public String description() default "";

		/**
		 * Returns the type's schema URL
		 *
		 * @return the type's schema URL
		 * @review
		 */
		public String schemaURL() default "https://www.schema.org/";

		/**
		 * Returns the type's name
		 *
		 * @return the type's name
		 * @review
		 */
		public String value();

	}

}