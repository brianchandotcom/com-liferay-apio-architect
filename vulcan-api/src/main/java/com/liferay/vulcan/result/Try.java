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

import java.io.Closeable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Implements the monadic <code>Try</code> type. Instances of this class
 * represent the result of an operation that either succeeds with type
 * <code>T</code>, or fails with an exception. Only two descendants of this
 * class are allowed: {@link Success} for the success case, and {@link Failure}
 * for the failure case.
 *
 * Never instantiate this class directly. If you're unsure whether the operation
 * will succeed, use {@link #fromFallible(ThrowableSupplier)} to create an
 * instance of this class. To create a <code>Failure</code> instance directly
 * from an exception, use {@link #fail(Exception)}. To create a
 * <code>Success</code> instance directly from <code>T</code>, use
 * {@link #success(Object)}.
 *
 * @author Alejandro Hernández
 */
@SuppressWarnings("unused")
public abstract class Try<T> {

	/**
	 * Creates a new <code>Try</code> instance from an exception. This method
	 * creates the instance as a <code>Failure</code> object.
	 *
	 * @param  exception the exception to create the <code>Failure</code> from
	 * @return the <code>Try</code> instance created from the exception
	 */
	public static <U> Try<U> fail(Exception exception) {
		return new Failure<>(exception);
	}

	/**
	 * Creates a new <code>Try</code> instance by executing a fallible lambda in
	 * a {@link ThrowableSupplier}. If this throws an exception, a
	 * <code>Failure</code> instance is created. Otherwise, a
	 * <code>Success</code> instance with the lambda's result is created.
	 *
	 * @param  throwableSupplier the throwable supplier that contains the
	 *         fallible lambda
	 * @return <code>Failure</code> if the throwable supplier throws an
	 *         exception; <code>Success</code> otherwise
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
	 * Creates a new <code>Try</code> instance by composing two functions:
	 * <code>throwableSupplier</code> and <code>throwableFunction</code>. The
	 * result of <code>throwableSupplier</code> is passed as the only parameter
	 * to <code>throwableFunction</code>. If either function fails, a
	 * <code>Failure</code> instance is returned. Otherwise, a
	 * <code>Success</code> instance with the results of
	 * <code>throwableFunction</code> is returned.
	 *
	 * @param  throwableFunction the function to execute with the results of
	 *         <code>throwableSupplier</code>
	 * @return a <code>Success</code> instance if the functions succeed; a
	 *         <code>Failure</code> instance otherwise
	 */
	public static <U, V extends Closeable> Try<U> fromFallibleWithResources(
		ThrowableSupplier<V> throwableSupplier,
		ThrowableFunction<V, U> throwableFunction) {

		Objects.requireNonNull(throwableFunction);
		Objects.requireNonNull(throwableSupplier);

		try (V v = throwableSupplier.get()) {
			return success(throwableFunction.apply(v));
		}
		catch (Exception exception) {
			return fail(exception);
		}
	}

	/**
	 * Creates a new <code>Try</code> instance from an object. This method
	 * creates the instance as a <code>Success</code> object.
	 *
	 * @param  u the object to create the <code>Success</code> object from
	 * @return the <code>Success</code> object
	 */
	public static <U> Try<U> success(U u) {
		return new Success<>(u);
	}

	/**
	 * Returns a <code>Try</code> instance with a value, if that value matches
	 * the predicate. Otherwise, this method returns a <code>Try</code> instance
	 * with an exception that indicates the <code>false</code> predicate.
	 *
	 * @param  predicate the predicate to apply to a value
	 * @return a <code>Try</code> instance with a value, if that value matches
	 *         the predicate; otherwise a <code>Try</code> instance with an
	 *         exception for a {@code <code>false</code>} predicate
	 */
	public abstract Try<T> filter(Predicate<T> predicate);

	/**
	 * Returns the result of applying the mapping function to the
	 * <code>Success</code> instance's value, if the current <code>Try</code>
	 * instance is a <code>Success</code>; otherwise returns the
	 * <code>Failure</code> instance.
	 *
	 * <p>
	 * This method is similar to {@link #map(ThrowableFunction)}, but the
	 * mapping function's result is already a <code>Try</code>, and if invoked,
	 * <code>flatMap</code> doesn't wrap it in an additional <code>Try</code>.
	 * </p>
	 *
	 * @param  function the mapping function
	 * @return the result of the mapping function if applied to the
	 *         <code>Success</code> instance's value; the <code>Failure</code>
	 *         instance otherwise
	 */
	public abstract <U> Try<U> flatMap(
		ThrowableFunction<? super T, Try<U>> function);

	/**
	 * Returns a <code>Success</code> instance's value, or a
	 * <code>Failure</code> instance's exception. What this method returns
	 * therefore depends on whether the current <code>Try</code> instance is a
	 * <code>Success</code> or <code>Failure</code>.
	 *
	 * @return a <code>Success</code> instance's value; otherwise the
	 *         <code>Failure</code> instance's exception
	 * @throws Exception if the operation failed
	 */
	public abstract T get() throws Exception;

	/**
	 * Returns a <code>Success</code> instance's value, or a
	 * <code>Failure</code> instance's exception wrapped in a
	 * <code>RuntimeException</code>. What this method returns therefore depends
	 * on whether the current <code>Try</code> instance is a
	 * <code>Success</code> or <code>Failure</code>.
	 *
	 * @return a <code>Success</code> instance's value; otherwise the
	 *         <code>Failure</code> instance's exception wrapped in a
	 *         <code>RuntimeException</code>
	 */
	public abstract T getUnchecked();

	/**
	 * Returns <code>true</code> if the current <code>Try</code> instance is a
	 * <code>Failure</code>; otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the current <code>Try</code> instance is a
	 *         <code>Failure</code>; <code>false</code>} otherwise
	 */
	public abstract boolean isFailure();

	/**
	 * Returns <code>true</code> if the current <code>Try</code> instance is a
	 * <code>Success</code>; otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the current <code>Try</code> instance is a
	 *         <code>Success</code>; <code>false</code>} otherwise
	 */
	public abstract boolean isSuccess();

	/**
	 * Returns the result of applying the mapping function to the
	 * <code>Success</code> instance's value, if the current <code>Try</code>
	 * instance is a <code>Success</code>; otherwise returns the
	 * <code>Failure</code> instance.
	 *
	 * <p>
	 * This function is similar to {@link #flatMap(ThrowableFunction)}, but the
	 * mapping function's result isn't a <code>Try</code>, and if invoked,
	 * <code>map</code> wraps it in <code>Try</code>.
	 * </p>
	 *
	 * @param  throwableFunction the mapping function
	 * @return the result of the mapping function if applied to the
	 *         <code>Success</code> instance's value; the <code>Failure</code>
	 *         instance otherwise
	 */
	public abstract <U> Try<U> map(
		ThrowableFunction<? super T, ? extends U> throwableFunction);

	/**
	 * Returns a <code>Try</code> instance that contains the result of applying
	 * the mapping function to the current <code>Try</code> instance's
	 * exception, if that instance is a <code>Failure</code> object. If the
	 * current <code>Try</code> instance is a <code>Success</code> object, this
	 * method returns it unmodified.
	 *
	 * @param  function the mapping function to apply to the
	 *         <code>Failure</code> instance's exception
	 * @return the <code>Try</code> with the mapping function's result; the
	 *         <code>Success</code> object otherwise
	 */
	public abstract <X extends Exception> Try<T> mapFail(
		Function<Exception, X> function);

	/**
	 * Returns a <code>Try</code> instance that contains the exception provided
	 * by the <code>Supplier</code>, if the current
	 * <code>Try</code> instance is a <code>Failure</code> object whose
	 * exception class matches that of the <code>exceptionClass</code>
	 * parameter. If the current <code>Try</code> instance is a
	 * <code>Success</code> object, this method returns it unmodified.
	 *
	 * @param  supplier the supplier
	 * @return the <code>Try</code> with the exception from the supplier; the
	 *         <code>Success</code> object otherwise
	 */
	public abstract <X extends Exception, Y extends Exception> Try<T>
		mapFailMatching(Class<Y> exceptionClass, Supplier<X> supplier);

	/**
	 * Returns the <code>Success</code> instance's value, if the current
	 * <code>Try</code> instance is a <code>Success</code> object. If the
	 * current <code>Try</code> instance is a <code>Failure</code> object, this
	 * method returns the <code>other</code> parameter.
	 *
	 * @param  other the value to return if current <code>Try</code> instance is
	 *         a <code>Failure</code> object
	 * @return the <code>Success</code> instance's value, or <code>other</code>
	 */
	public abstract T orElse(T other);

	/**
	 * Returns the <code>Success</code> instance's value, if the current
	 * <code>Try</code> instance is a <code>Success</code> object. If the
	 * current <code>Try</code> instance is a <code>Failure</code> object, this
	 * method returns the result of invoking
	 * <code>Supplier#get()</code>.
	 *
	 * @param  supplier the supplier
	 * @return the <code>Success</code> instance's value, or the result of the
	 *         supplier's <code>get</code> method
	 */
	public abstract T orElseGet(Supplier<? extends T> supplier);

	/**
	 * Returns the <code>Success</code> instance's value, if the current
	 * <code>Try</code> instance is a <code>Success</code> object. If the
	 * current <code>Try</code> instance is a <code>Failure</code> object, this
	 * method throws the exception that results from invoking
	 * <code>Supplier#get()</code>.
	 *
	 * @param  supplier the supplier
	 * @return the <code>Success</code> instance's value
	 * @throws X if the current <code>Try</code> instance was a
	 *         <code>Failure</code> object
	 */
	public abstract <X extends Throwable> T orElseThrow(
			Supplier<? extends X> supplier)
		throws X;

	/**
	 * Returns the result of applying the function to the <code>Failure</code>
	 * object's exception, if the current <code>Try</code> instance is a
	 * <code>Failure</code> object. If the current <code>Try</code> instance is
	 * a <code>Success</code> object, this method returns that object's value.
	 *
	 * @param  function the function
	 * @return the function's result when applied to the <code>Failure</code>;
	 *         the <code>Success</code> object's value otherwise
	 */
	public abstract T recover(Function<? super Exception, T> function);

	/**
	 * Returns a new <code>Try</code> instance extracted from the provided
	 * function, if the current <code>Try</code> instance is a
	 * <code>Failure</code> object. If the current <code>Try</code> instance is
	 * a <code>Success</code> object, this method returns that object.
	 *
	 * @param  throwableFunction the function
	 * @return the new <code>Try</code> instance, if the current
	 *         <code>Try</code> instance is a <code>Failure</code> object; the
	 *         current <code>Success</code> object otherwise
	 */
	public abstract Try<T> recoverWith(
		ThrowableFunction<? super Exception, Try<T>> throwableFunction);

	/**
	 * The implementation of <code>Try</code>'s failure case. Don't try to
	 * instantiate this class directly. To instantiate this class when you don't
	 * know if the operation will fail, use {@link
	 * #fromFallible(ThrowableSupplier)}. To instantiate this class from an
	 * exception, use {@link #fail(Exception)}.
	 */
	public static class Failure<T> extends Try<T> {

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
		 * Returns the current <code>Failure</code> instance's exception.
		 *
		 * @return the exception
		 */
		public Exception getException() {
			return _exception;
		}

		@Override
		public T getUnchecked() {
			if (_exception instanceof RuntimeException) {
				throw (RuntimeException)_exception;
			}

			throw new RuntimeException(_exception);
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
		public <X extends Exception> Try<T> mapFail(
			Function<Exception, X> function) {

			Objects.requireNonNull(function);

			return Try.fail(function.apply(_exception));
		}

		@Override
		public <X extends Exception, Y extends Exception> Try<T>
			mapFailMatching(Class<Y> exceptionClass, Supplier<X> supplier) {

			Objects.requireNonNull(supplier);

			Class<? extends Exception> causeClass = _exception.getClass();

			if (causeClass.equals(exceptionClass)) {
				return Try.fail(supplier.get());
			}

			return this;
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
		public <X extends Throwable> T orElseThrow(
				Supplier<? extends X> supplier)
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

		private Failure(Exception exception) {
			_exception = exception;
		}

		private final Exception _exception;

	}

	/**
	 * The implementation of <code>Try</code>'s success case. Don't try to
	 * instantiate this class directly. To instantiate this class when you don't
	 * know if the operation will fail, use {@link
	 * #fromFallible(ThrowableSupplier)}. To instantiate this class from a value
	 * of type <code>T</code>, use {@link #success(Object)}.
	 */
	public static class Success<T> extends Try<T> {

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
		public <U> Try<U> flatMap(
			ThrowableFunction<? super T, Try<U>> function) {

			Objects.requireNonNull(function);

			try {
				return function.apply(_value);
			}
			catch (Exception e) {
				return Try.fail(e);
			}
		}

		@Override
		public T get() throws Exception {
			return _value;
		}

		@Override
		public T getUnchecked() {
			return _value;
		}

		/**
		 * Returns the current <code>Success</code> instance's value.
		 *
		 * @return the current <code>Success</code> instance's value
		 */
		public T getValue() {
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
			catch (Exception e) {
				return Try.fail(e);
			}
		}

		@Override
		public <X extends Exception> Try<T> mapFail(
			Function<Exception, X> function) {

			Objects.requireNonNull(function);

			return this;
		}

		@Override
		public <X extends Exception, Y extends Exception> Try<T>
			mapFailMatching(Class<Y> exceptionClass, Supplier<X> supplier) {

			Objects.requireNonNull(supplier);

			return this;
		}

		@Override
		public T orElse(T other) {
			return _value;
		}

		@Override
		public T orElseGet(Supplier<? extends T> supplier) {
			return _value;
		}

		@Override
		public <X extends Throwable> T orElseThrow(
				Supplier<? extends X> supplier)
			throws X {

			return _value;
		}

		@Override
		public T recover(Function<? super Exception, T> function) {
			Objects.requireNonNull(function);

			return _value;
		}

		@Override
		public Try<T> recoverWith(
			ThrowableFunction<? super Exception, Try<T>> throwableFunction) {

			Objects.requireNonNull(throwableFunction);

			return this;
		}

		private Success(T value) {
			_value = value;
		}

		private final T _value;

	}

	private Try() {
	}

}