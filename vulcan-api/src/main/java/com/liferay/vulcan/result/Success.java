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

import com.liferay.vulcan.exception.FalsePredicateException;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Implementation of the success case of a {@code Try}.
 *
 * Don't try to directly instantiate this class. To create an instance of this
 * class, use {@link #fromFallible(ThrowableSupplier)} if you don't know if the
 * operation is going to fail or not. Or {@link #success(Object)} to directly
 * create a {@link Success} from a T.
 *
 * @author Alejandro Hernández
 */
public class Success<T> extends Try<T> {

	@Override
	public Try<T> filter(Predicate<T> predicate) {
		Objects.requireNonNull(predicate);

		if (predicate.test(_value)) {
			return this;
		}
		else {
			return Try.fail(new FalsePredicateException(_value));
		}
	}

	@Override
	public <U> Try<U> flatMap(ThrowableFunction<? super T, Try<U>> function) {
		Objects.requireNonNull(function);

		try {
			return function.apply(_value);
		}
		catch (Throwable t) {
			return Try.fail(t);
		}
	}

	@Override
	public T get() throws Throwable {
		return _value;
	}

	@Override
	public boolean isFailure() {
		return false;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public <U> Try<U> map(
		ThrowableFunction<? super T, ? extends U> throwableFunction) {

		Objects.requireNonNull(throwableFunction);

		try {
			return Try.success(throwableFunction.apply(_value));
		}
		catch (Throwable t) {
			return Try.fail(t);
		}
	}

	@Override
	public T recover(Function<? super Throwable, T> function) {
		Objects.requireNonNull(function);

		return _value;
	}

	@Override
	public Try<T> recoverWith(
		ThrowableFunction<? super Throwable, Try<T>> throwableFunction) {

		Objects.requireNonNull(throwableFunction);

		return this;
	}

	protected Success(T value) {
		_value = value;
	}

	private final T _value;

}