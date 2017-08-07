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

/**
 * Implementation of the monadic "Try" type.
 *
 * Represents the result of an operation that could have succeeded (with a type
 * T) or failed (with a {@link Throwable}).
 *
 * Only two descendants of this class are allowed: {@link Success} for the
 * success case and {@link Failure} for the failure one.
 *
 * Never instantiate this class directly. To create an instance of this class,
 * use {@link
 * #fromFallible(ThrowableSupplier)}
 * if you don't know if the operation is going to fail or not. {@link
 * #fail(Throwable)}
 * to directly create a {@link Failure} from a {@link Throwable}. Or {@link
 * #success(Object)}
 * to directly create a {@link Success} from a T.
 *
 * @author Alejandro Hernández
 */
public abstract class Try<T> {

	/**
	 * Creates a new {@code Try} instance from a {@link Throwable}. The instance
	 * will be created as a {@link Failure}.
	 *
	 * @param  throwable the throwable to include in the {@link Failure}
	 * @return the {@code Try} instance for the throwable.
	 */
	public static <U> Try<U> fail(Throwable throwable) {
		return new Failure<>(throwable);
	}

	/**
	 * Creates a new {@code Try} instance by executing a fallible lambda. If
	 * executing the lambda throws an exception a {@link Failure} instance will
	 * be created; otherwise, a {@link Success} instance with the lambda result
	 * will be created.
	 *
	 * @param  throwableSupplier the supplier to be executed in order to obtain
	 *         the value.
	 * @return a {@code Try} instance with the value obtained by the supplier:
	 *         {@link Failure} if the supplier throws an exception; {@link
	 *         Success} otherwise.
	 */
	public static <U> Try<U> fromFallible(
		ThrowableSupplier<U> throwableSupplier) {

		Objects.requireNonNull(throwableSupplier);

		try {
			return success(throwableSupplier.get());
		}
		catch (Throwable throwable) {
			return fail(throwable);
		}
	}

	/**
	 * Creates a new {@code Try} instance from a valid object. The instance will
	 * be created as a {@link Success}.
	 *
	 * @param  u object to include as value of the {@link Success}.
	 * @return the {@code Try} instance for the value.
	 */
	public static <U> Try<U> success(U u) {
		return new Success<>(u);
	}

	/**
	 * Returns the value T on success or throws the cause of the failure.
	 *
	 * @return T if success case, throws {@code Throwable} otherwise.
	 */
	public abstract T get() throws Throwable;

	/**
	 * Returns <code>true</code> if this {@code Try} instance is a failure.
	 * Returns <code>false</code> otherwise.
	 *
	 * @return <code>true</code> if instance is a failure; <code>false</code>
	 *         otherwise.
	 */
	public abstract boolean isFailure();

	/**
	 * Returns <code>true</code> if this {@code Try} instance is a success.
	 * Returns <code>false</code> otherwise.
	 *
	 * @return <code>true</code> if instance is a success; <code>false</code>
	 *         otherwise.
	 */
	public abstract boolean isSuccess();

	protected Try() {
	}

}