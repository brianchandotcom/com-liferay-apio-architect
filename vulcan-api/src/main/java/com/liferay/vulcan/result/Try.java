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
 * Implementation of the monadic "Try" type.
 *
 * Represents the result of an operation that could have succeeded (with a type
 * T) or failed (with an {@link Exception}).
 *
 * Only two descendants of this class are allowed: {@link Success} for the
 * success case and {@link Failure} for the failure one.
 *
 * Never instantiate this class directly. To create an instance of this class,
 * use {@link
 * #fromFallible(ThrowableSupplier)}
 * if you don't know if the operation is going to fail or not. {@link
 * #fail(Exception)}
 * to directly create a {@link Failure} from an {@link Exception}. Or {@link
 * #success(Object)}
 * to directly create a {@link Success} from a T.
 *
 * @author Alejandro Hernández
 */
public abstract class Try<T> {

	/**
	 * Creates a new {@code Try} instance from an {@link Exception}. The
	 * instance will be created as a {@link Failure}.
	 *
	 * @param  exception the exception to include in the {@link Failure}
	 * @return the {@code Try} instance for the exception.
	 */
	public static <U> Try<U> fail(Exception exception) {
		return new Failure<>(exception);
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
		catch (Exception exception) {
			return fail(exception);
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
	 * If a value is present, and the value matches the given predicate, return
	 * a {@code Try} with the value, otherwise return a {@code Try} with an
	 * exception indicating the <code>false</code> predicate.
	 *
	 * @param  predicate a predicate to apply to the value, if present
	 * @return a {@code Try} with the value of this {@code Try} if a value is
	 *         present and the value matches the given predicate, otherwise a
	 *         {@code Try} with and exception for a <code>false</code>
	 *         predicate.
	 */
	public abstract Try<T> filter(Predicate<T> predicate);

	/**
	 * If success case, apply the provided {@code Try}-bearing mapping function
	 * to it, return that result; otherwise propagate the failure.
	 *
	 * <p>
	 * This method is similar to {@link #map(ThrowableFunction)}, but the
	 * provided mapper is one whose result is already a {@code Try}, and if
	 * invoked, {@code flatMap} does not wrap it with an additional {@code Try}.
	 * </p>
	 *
	 * @param  function a mapping function to apply to the value, if success.
	 * @return the result of applying a {@code Try}-bearing mapping function to
	 *         the value of this {@code Try}, if a success, otherwise propagates
	 *         the failure.
	 */
	public abstract <U> Try<U> flatMap(
		ThrowableFunction<? super T, Try<U>> function);

	/**
	 * Returns the value T on success or throws the cause of the failure.
	 *
	 * @return T if success case, throws {@code Exception} otherwise.
	 */
	public abstract T get() throws Exception;

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

	/**
	 * If success, apply the provided mapping function to it, and if the
	 * function doesn't throw an exception, return in inside a {@code Try}.
	 * Otherwise return a {@code Failure}.
	 *
	 * @param  throwableFunction a mapping function to apply to the value, if
	 *         success.
	 * @return a {@code Try} with the result of applying a mapping function to
	 *         the value inside this {@code Try}, if success case; a {@code
	 *         Failure} describing the exception otherwise.
	 */
	public abstract <U> Try<U> map(
		ThrowableFunction<? super T, ? extends U> throwableFunction);

	/**
	 * Return the value if success, otherwise return {@code other}.
	 *
	 * @param  other the value to be returned if failure.
	 * @return the value, if success, otherwise {@code other}.
	 */
	public abstract T orElse(T other);

	/**
	 * Return the value if success, otherwise invoke {@code supplier} and return
	 * the result of that invocation.
	 *
	 * @param  supplier a {@code Supplier} whose result is returned if failure.
	 * @return the value if success otherwise the result of {@code
	 *         supplier.get()}.
	 */
	public abstract T orElseGet(Supplier<? extends T> supplier);

	/**
	 * Return the contained value, if success, otherwise throw an exception to
	 * be created by the provided supplier.
	 *
	 * @param  supplier The supplier which will return the exception to be
	 *         thrown
	 * @return the present value
	 */
	public abstract <X extends Throwable> T orElseThrow(
			Supplier<? extends X> supplier)
		throws X;

	/**
	 * Returns the result extracted from the provided function if this instance
	 * is a failure case. Returns the inner result in success case.
	 *
	 * <p>
	 * The lambda passed as parameter gives access to the exception in the case
	 * of failure.
	 * </p>
	 *
	 * @param  function function to execute on failure result.
	 * @return the result from the function, if failure; the inner value on
	 *         success.
	 */
	public abstract T recover(Function<? super Exception, T> function);

	/**
	 * Returns a new {@code Try} instance, extracted from the provided function
	 * if this instance is a failure case. Returns the current success,
	 * otherwise.
	 *
	 * <p>
	 * The lambda passed as parameter gives access to the exception in the case
	 * of failure.
	 * </p>
	 *
	 * @param  throwableFunction function to execute on failure result.
	 * @return the new {@code Try} result from the function, if failure; the
	 *         current success, otherwise.
	 */
	public abstract Try<T> recoverWith(
		ThrowableFunction<? super Exception, Try<T>> throwableFunction);

	protected Try() {
	}

}