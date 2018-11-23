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

package com.liferay.apio.architect.internal.action;

import static java.util.stream.Collectors.toList;

import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.alias.ProvideFunction;
import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.operation.HTTPMethod;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;

import java.lang.annotation.Annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Instances of this class contains semantic information about an action like
 *
 * @author Alejandro Hernández
 * @review
 */
public final class ActionSemantics {

	/**
	 * Starts creating a new {@link ActionSemantics} by providing information
	 * about the action's resource.
	 *
	 * @review
	 */
	public static NameStep ofResource(Resource resource) {
		ActionSemantics actionSemantics = new ActionSemantics();

		actionSemantics._resource = resource;

		return new Builder(actionSemantics);
	}

	/**
	 * The list of annotations.
	 *
	 * @review
	 */
	public List<Annotation> annotations() {
		return _annotations;
	}

	/**
	 * The function that executes the action.
	 *
	 * @review
	 */
	public CheckedFunction1<List<?>, ?> executeFunction() {
		return _executeFunction;
	}

	/**
	 * The method in which the action is executed.
	 *
	 * @review
	 */
	public String method() {
		return _method;
	}

	/**
	 * The action's name.
	 *
	 * @review
	 */
	public String name() {
		return _name;
	}

	/**
	 * The list of param classes.
	 *
	 * @review
	 */
	public List<Class<?>> paramClasses() {
		return _paramClasses;
	}

	/**
	 * The action's resource.
	 *
	 * @review
	 */
	public Resource resource() {
		return _resource;
	}

	/**
	 * The class returned by the action.
	 *
	 * @review
	 */
	public Class<?> returnClass() {
		return _returnClass;
	}

	/**
	 * Transforms this {@link ActionSemantics} instance into its {@link Action}.
	 *
	 * @param  provideFunction the function used to provide instances of action
	 *         params
	 * @review
	 */
	public Action toAction(ProvideFunction provideFunction) {
		return request -> Try.of(
			paramClasses()::stream
		).map(
			stream -> stream.map(
				provideFunction.apply(this, request)
			).collect(
				toList()
			)
		).mapTry(
			executeFunction()
		);
	}

	/**
	 * Copies the current {@link ActionSemantics} by setting a value for the
	 * {@link ActionSemantics#annotations()} annotations} attribute. A shallow
	 * reference equality check is used to prevent copying of the same value by
	 * returning {@code this}.
	 *
	 * @param  annotations the new annotations list
	 * @return A modified copy of {@code this} object
	 * @review
	 */
	public ActionSemantics withAnnotations(List<Annotation> annotations) {
		if (_annotations.equals(annotations)) {
			return this;
		}

		ActionSemantics actionSemantics = new ActionSemantics();

		actionSemantics._annotations = annotations;
		actionSemantics._executeFunction = _executeFunction;
		actionSemantics._method = _method;
		actionSemantics._name = _name;
		actionSemantics._paramClasses = _paramClasses;
		actionSemantics._resource = _resource;
		actionSemantics._returnClass = _returnClass;

		return actionSemantics;
	}

	/**
	 * Copies the current {@link ActionSemantics} by setting a value for the
	 * {@link ActionSemantics#method()} method} attribute. A shallow reference
	 * equality check is used to prevent copying of the same value by returning
	 * {@code this}.
	 *
	 * @param  method the new method
	 * @return A modified copy of {@code this} object
	 * @review
	 */
	public ActionSemantics withMethod(String method) {
		if (_method.equals(method)) {
			return this;
		}

		ActionSemantics actionSemantics = new ActionSemantics();

		actionSemantics._annotations = _annotations;
		actionSemantics._executeFunction = _executeFunction;
		actionSemantics._method = method;
		actionSemantics._name = _name;
		actionSemantics._paramClasses = _paramClasses;
		actionSemantics._resource = _resource;
		actionSemantics._returnClass = _returnClass;

		return actionSemantics;
	}

	/**
	 * Copies the current {@link ActionSemantics} by setting a value for the
	 * {@link ActionSemantics#name()} name} attribute. A shallow reference
	 * equality check is used to prevent copying of the same value by returning
	 * {@code this}.
	 *
	 * @param  name the new name
	 * @return A modified copy of {@code this} object
	 * @review
	 */
	public ActionSemantics withName(String name) {
		if (_name.equals(name)) {
			return this;
		}

		ActionSemantics actionSemantics = new ActionSemantics();

		actionSemantics._annotations = _annotations;
		actionSemantics._executeFunction = _executeFunction;
		actionSemantics._method = _method;
		actionSemantics._name = name;
		actionSemantics._paramClasses = _paramClasses;
		actionSemantics._resource = _resource;
		actionSemantics._returnClass = _returnClass;

		return actionSemantics;
	}

	/**
	 * Copies the current {@link ActionSemantics} by setting a value for the
	 * {@link ActionSemantics#resource() resource} attribute. A shallow
	 * reference equality check is used to prevent copying of the same value by
	 * returning {@code this}.
	 *
	 * @param  resource the new resource
	 * @return A modified copy of {@code this} object
	 * @review
	 */
	public ActionSemantics withResource(Resource resource) {
		if (_resource.equals(resource)) {
			return this;
		}

		ActionSemantics actionSemantics = new ActionSemantics();

		actionSemantics._annotations = _annotations;
		actionSemantics._executeFunction = _executeFunction;
		actionSemantics._method = _method;
		actionSemantics._name = _name;
		actionSemantics._paramClasses = _paramClasses;
		actionSemantics._resource = resource;
		actionSemantics._returnClass = _returnClass;

		return actionSemantics;
	}

	/**
	 * Copies the current {@link ActionSemantics} by setting a value for the
	 * {@link ActionSemantics#returnClass()} return class} attribute. A shallow
	 * reference equality check is used to prevent copying of the same value by
	 * returning {@code this}.
	 *
	 * @param  returnClass the new return class
	 * @return A modified copy of {@code this} object
	 * @review
	 */
	public ActionSemantics withReturnClass(Class<?> returnClass) {
		if (_returnClass.equals(returnClass)) {
			return this;
		}

		ActionSemantics actionSemantics = new ActionSemantics();

		actionSemantics._annotations = _annotations;
		actionSemantics._executeFunction = _executeFunction;
		actionSemantics._method = _method;
		actionSemantics._name = _name;
		actionSemantics._paramClasses = _paramClasses;
		actionSemantics._resource = _resource;
		actionSemantics._returnClass = returnClass;

		return actionSemantics;
	}

	public static class Builder
		implements NameStep, MethodStep, ReturnStep, ExecuteStep, FinalStep {

		public Builder(ActionSemantics actionSemantics) {
			_actionSemantics = actionSemantics;
		}

		@Override
		public FinalStep annotatedWith(Annotation annotation) {
			_actionSemantics._annotations.add(annotation);

			return this;
		}

		@Override
		public FinalStep annotatedWith(Annotation... annotations) {
			_actionSemantics._annotations = Arrays.asList(annotations);

			return this;
		}

		@Override
		public ActionSemantics build() {
			return _actionSemantics;
		}

		@Override
		public FinalStep executeFunction(
			CheckedFunction1<List<?>, ?> executeFunction) {

			_actionSemantics._executeFunction = executeFunction;

			return this;
		}

		@Override
		public ReturnStep method(String method) {
			_actionSemantics._method = method;

			return this;
		}

		@Override
		public MethodStep name(String name) {
			_actionSemantics._name = name;

			return this;
		}

		@Override
		public FinalStep receivesParams(Class<?>... classes) {
			_actionSemantics._paramClasses = Arrays.asList(classes);

			return this;
		}

		@Override
		public ExecuteStep returns(Class<?> returnClass) {
			_actionSemantics._returnClass = returnClass;

			return this;
		}

		private final ActionSemantics _actionSemantics;

	}

	public interface ExecuteStep {

		/**
		 * Provides information about the function action's execute function.
		 * This function receives the list of params in the order provided in
		 * the {@link FinalStep#receivesParams(Class[])} method.
		 *
		 * @review
		 */
		public FinalStep executeFunction(
			CheckedFunction1<List<?>, ?> executeFunction);

	}

	public interface FinalStep {

		/**
		 * Provides information about the params needed by the action.
		 *
		 * <p>
		 * The param instances will be provided in the {@link
		 * #executeFunction()} in the same order as their classes in this
		 * method. {@link Void} classes will be ignored (will be provided as
		 * {@code null}. For the {@link
		 * com.liferay.apio.architect.annotation.Id} or {@link
		 * com.liferay.apio.architect.annotation.ParentId} params, the
		 * annotation class should be provided to the list.
		 * </p>
		 *
		 * @review
		 */
		public FinalStep annotatedWith(Annotation annotations);

		/**
		 * Provides information about the params needed by the action.
		 *
		 * <p>
		 * The param instances will be provided in the {@link
		 * #executeFunction()} in the same order as their classes in this
		 * method. {@link Void} classes will be ignored (will be provided as
		 * {@code null}. For the {@link
		 * com.liferay.apio.architect.annotation.Id} or {@link
		 * com.liferay.apio.architect.annotation.ParentId} params, the
		 * annotation class should be provided to the list.
		 * </p>
		 *
		 * @review
		 */
		public FinalStep annotatedWith(Annotation... annotations);

		/**
		 * Creates the {@link ActionSemantics} object with the information
		 * provided to the builder.
		 *
		 * @review
		 */
		public ActionSemantics build();

		/**
		 * Provides information about the params needed by the action.
		 *
		 * <p>
		 * The param instances will be provided in the {@link
		 * #executeFunction()} in the same order as their classes in this
		 * method. {@link Void} classes will be ignored (will be provided as
		 * {@code null}. For the {@link
		 * com.liferay.apio.architect.annotation.Id} or {@link
		 * com.liferay.apio.architect.annotation.ParentId} params, the
		 * annotation class should be provided to the list.
		 * </p>
		 *
		 * @review
		 */
		public FinalStep receivesParams(Class<?>... classes);

	}

	public interface MethodStep {

		/**
		 * Provides information about the method in which the action is
		 * executed.
		 *
		 * @review
		 */
		public default ReturnStep method(HTTPMethod httpMethod) {
			return method(httpMethod.name());
		}

		/**
		 * Provides information about the method in which the action is
		 * executed.
		 *
		 * @review
		 */
		public ReturnStep method(String method);

	}

	public interface NameStep {

		/**
		 * Provides information about the the action's name.
		 *
		 * @review
		 */
		public MethodStep name(String name);

	}

	public interface ReturnStep {

		/**
		 * Provides information about the class returned by the action.
		 *
		 * @review
		 */
		public ExecuteStep returns(Class<?> returnClass);

	}

	private List<Annotation> _annotations = new ArrayList<>();
	private CheckedFunction1<List<?>, ?> _executeFunction;
	private String _method;
	private String _name;
	private List<Class<?>> _paramClasses = new ArrayList<>();
	private Resource _resource;
	private Class<?> _returnClass;

}