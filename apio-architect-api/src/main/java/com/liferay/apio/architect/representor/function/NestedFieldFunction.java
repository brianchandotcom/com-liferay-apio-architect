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

package com.liferay.apio.architect.representor.function;

import com.liferay.apio.architect.representor.Representor;

import java.util.function.Function;

/**
 * Instances of this class represent a {@code Representor} nested field.
 *
 * @author Alejandro Hernández
 * @review
 */
public class NestedFieldFunction<T, S> {

	public NestedFieldFunction(
		String key, Function<T, S> function, Representor<S> representor) {

		this.representor = representor;
		this.key = key;
		this.function = function;
	}

	/**
	 * The function that transforms the model into the value used in the {@code
	 * NestedRepresentor#Builder}.
	 *
	 * @review
	 */
	public final Function<T, S> function;

	/**
	 * The field's key
	 *
	 * @review
	 */
	public final String key;

	/**
	 * The field's {@code Representor}
	 */
	public final Representor<S> representor;

}