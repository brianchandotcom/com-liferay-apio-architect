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

/**
 * Mode in which a field of a type can be used
 *
 * @author Víctor Galán
 * @review
 */
public enum FieldMode {

	/**
	 * Indicates that a field should only be used when instantiating the
	 * interface out of the HTTP request body.
	 *
	 * <p>
	 * A field with this mode will be ignored instantiating the interface out of
	 * the HTTP request body.
	 * </p>
	 *
	 * <p>
	 * Opposite attribute to {@link #WRITE_ONLY}.
	 * </p>
	 *
	 * @see    #WRITE_ONLY
	 * @review
	 */
	READ_ONLY,

	/**
	 * Indicates that a field should be used when instantiating the interface
	 * out of the HTTP request body and also when representing the type.
	 *
	 * <p>
	 * {@link #READ_ONLY} and {@link #WRITE_ONLY} together
	 * </p>
	 *
	 * @review
	 */
	READ_WRITE,

	/**
	 * Indicates that a field should only be used when instantiating the
	 * interface out of the HTTP request body.
	 *
	 * <p>
	 * A field with this mode will be ignored when representing the type in any
	 * format.
	 * </p>
	 *
	 * <p>
	 * Opposite attribute to {@link #READ_ONLY}.
	 * </p>
	 *
	 * @see    #READ_ONLY
	 * @review
	 */
	WRITE_ONLY

}