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

package com.liferay.apio.architect.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.liferay.apio.architect.identifier.Identifier;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines an annotation that namespaces vocabulary annotations. Use one of this
 * interface's nested annotations.
 *
 * @author Alejandro Hernández
 */
public @interface Vocabulary {

	/**
	 * Indicates that a type has a bidirectional link to another resource's
	 * page.
	 */
	@Retention(RUNTIME)
	@Target(METHOD)
	public @interface BidirectionalModel {

		/**
		 * Returns the property field that holds information about the property
		 * added to the linked type.
		 *
		 * @return the property field
		 */
		public Field field();

		/**
		 * Returns the class of the resource being linked to.
		 *
		 * @return the resource's class
		 */
		public Class<? extends Identifier<?>> modelClass();

	}

	/**
	 * Defines an annotation that provides information about a field. This
	 * annotation should always be used on an interface method.
	 *
	 * <p>
	 * This annotation has attributes to customize the schema URL (<a
	 * href="https://schema.org">schema.org </a> by default) and the field
	 * description (in the case of a custom field).
	 * </p>
	 */
	@Retention(RUNTIME)
	@Target(METHOD)
	public @interface Field {
		/**
		 * Returns the field description, if the field is a custom field;
		 * otherwise returns an empty string.
		 *
		 * @return the custom field's description; an empty string otherwise
		 */
		public String description() default "";

		/**
		 * Returns {@code true} if a field should only be used when representing
		 * the type. If this attribute is {@code true}, it will be ignored when
		 * instantiating the interface from the HTTP request body. This
		 * attribute is the opposite of {@link #writeOnly()}.
		 *
		 * @see #writeOnly()
		 */
		public boolean readOnly() default false;

		/**
		 * Returns the field's schema URL.
		 *
		 * @return the field's schema URL
		 */
		public String schemaURL() default "https://www.schema.org/";

		/**
		 * Returns the field's name.
		 *
		 * @return the field's name
		 */
		public String value();

		/**
		 * Returns {@code true} if a field should only be used when
		 * instantiating the interface from the HTTP request body. If this
		 * attribute is {@code true}, it will be ignored when representing the
		 * type in any format. This attribute is the opposite of {@link
		 * #readOnly()}.
		 *
		 * @see #readOnly()
		 */
		public boolean writeOnly() default false;

	}

	/**
	 * Defines an annotation that indicates a field should be expressed as a
	 * link to another resource. For this to be possible, the method must
	 * provide information about another resource's ID.
	 */
	@Retention(RUNTIME)
	@Target(METHOD)
	public @interface LinkedModel {

		/**
		 * Returns the class of the resource being linked to.
		 *
		 * @return the resource's class
		 */
		public Class<? extends Identifier<?>> value();

	}

	/**
	 * Defines an annotation that indicates a type has a link to another
	 * resource's page.
	 */
	@Retention(RUNTIME)
	@Target(METHOD)
	public @interface RelatedCollection {

		/**
		 * Returns the class of the resource being linked to.
		 *
		 * @return the resource's class
		 */
		public Class<? extends Identifier<?>> value();

		public boolean reusable() default false;

	}

	/**
	 * Defines an annotation that indicates a field contains a relative URL that
	 * should be expressed as an absolute one. The annotated method must return
	 * the relative URL as a string.
	 */
	@Retention(RUNTIME)
	@Target(METHOD)
	public @interface RelativeURL {

		/**
		 * Returns {@code true} if the provided URL is relative to the JAX-RS
		 * application.
		 *
		 * @return {@code true} if the provided URL is relative to the JAX-RS
		 *         application; {@code false} otherwise
		 */
		public boolean fromApplication() default false;

	}

	/**
	 * Defines an annotation that provides information about a type. This
	 * annotation should always be used on an interface.
	 */
	@Retention(RUNTIME)
	@Target(TYPE)
	public @interface Type {

		/**
		 * Returns the type description, if the type is a custom type; returns
		 * an empty string otherwise.
		 *
		 * @return the type description if the type is a custom type; an empty
		 *         string otherwise
		 */
		public String description() default "";

		/**
		 * Returns the type's schema URL.
		 *
		 * @return the type's schema URL
		 */
		public String schemaURL() default "https://www.schema.org/";

		/**
		 * Returns the type's name.
		 *
		 * @return the type's name
		 */
		public String value();

	}

}