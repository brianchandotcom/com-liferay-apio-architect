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

package com.liferay.vulcan.result;

/**
 * Implementation of the failure case of a {@code Try}.
 *
 * Don't try to directly instantiate this class. To create an instance of this
 * class, use {@link #fromFallible(ThrowableSupplier)} if you don't know if the
 * operation is going to fail or not. Or {@link #fail(Throwable)} to directly
 * create a {@link Failure} from a {@code Throwable}.
 *
 * @author Alejandro Hernández
 */
public class Failure<T> extends Try<T> {

	@Override
	public T get() throws Throwable {
		throw _throwable;
	}

	protected Failure(Throwable throwable) {
		_throwable = throwable;
	}

	private final Throwable _throwable;

}