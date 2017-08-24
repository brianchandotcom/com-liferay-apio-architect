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

import java.util.Objects;
import java.util.function.Predicate;

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
	public Try<T> filter(Predicate<T> predicate) {
		return this;
	}

	@Override
	public <U> Try<U> flatMap(
		ThrowableFunction<? super T, Try<U>> throwableFunction) {

		Objects.requireNonNull(throwableFunction);

		return Try.fail(_throwable);
	}

	@Override
	public T get() throws Throwable {
		throw _throwable;
	}

	@Override
	public boolean isFailure() {
		return true;
	}

	@Override
	public boolean isSuccess() {
		return false;
	}

	@Override
	public <U> Try<U> map(
		ThrowableFunction<? super T, ? extends U> throwableFunction) {

		Objects.requireNonNull(throwableFunction);

		return Try.fail(_throwable);
	}

	protected Failure(Throwable throwable) {
		_throwable = throwable;
	}

	private final Throwable _throwable;

}