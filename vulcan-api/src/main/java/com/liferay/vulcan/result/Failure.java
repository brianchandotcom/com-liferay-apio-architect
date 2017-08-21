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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Implementation of the failure case of a {@code Try}.
 *
 * Don't try to directly instantiate this class. To create an instance of this
 * class, use {@link #fromFallible(ThrowableSupplier)} if you don't know if the
 * operation is going to fail or not. Or {@link #fail(Exception)} to directly
 * create a {@link Failure} from an {@code Exception}.
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

		return Try.fail(_exception);
	}

	@Override
	public T get() throws Exception {
		throw _exception;
	}

	/**
	 * Returns the cause of this failure.
	 *
	 * @return the cause of this failure.
	 */
	public Exception getException() {
		return _exception;
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

		return Try.fail(_exception);
	}

	@Override
	public T orElse(T other) {
		return other;
	}

	@Override
	public T orElseGet(Supplier<? extends T> supplier) {
		return supplier.get();
	}

	@Override
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> supplier)
		throws X {

		throw supplier.get();
	}

	@Override
	public T recover(Function<? super Exception, T> function) {
		Objects.requireNonNull(function);

		return function.apply(_exception);
	}

	@Override
	public Try<T> recoverWith(
		ThrowableFunction<? super Exception, Try<T>> throwableFunction) {

		Objects.requireNonNull(throwableFunction);
		try {
			return throwableFunction.apply(_exception);
		}
		catch (Exception e) {
			return Try.fail(e);
		}
	}

	protected Failure(Exception exception) {
		_exception = exception;
	}

	private final Exception _exception;

}