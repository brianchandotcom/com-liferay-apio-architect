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

import static com.liferay.apio.architect.internal.action.ImmutableActionSemantics.actionSemantics;
import static com.liferay.apio.architect.internal.action.Predicates.isActionFor;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.alias.ProvideFunction;
import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.operation.HTTPMethod;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;

import java.lang.annotation.Annotation;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;
import org.immutables.value.Value.Style;

/**
 * Instances of this class contains semantic information about an action like
 *
 * @author Alejandro Hernández
 * @review
 */
@Immutable(builder = false)
@Style(allParameters = true, of = "actionSemantics")
public abstract class ActionSemantics {

	/**
	 * Starts filtering an {@link ActionSemantics} from a {@link Stream}.
	 *
	 * @review
	 */
	public static ResourceStep filter(Stream<ActionSemantics> stream) {
		return resource -> predicate -> stream.filter(
			predicate.and(isActionFor(resource))
		).findFirst();
	}

	/**
	 * Starts creating a new {@link ActionSemantics} by providing information
	 * about the action's resource.
	 *
	 * @review
	 */
	public static NameStep ofResource(Resource resource) {
		return name -> method -> paramClasses -> returnClass -> annotations ->
			executeFunction -> () -> actionSemantics(
				resource, name, method, asList(paramClasses), returnClass,
				asList(annotations), executeFunction);
	}

	/**
	 * Returns a function that transforms an {@link ActionSemantics} instance
	 * into its {@link Action}.
	 *
	 * @param  provideFunction the function used to provide instances of action
	 *         params
	 * @return the function that transforms an {@link ActionSemantics} into an
	 *         {@link Action}
	 */
	public static Function<ActionSemantics, Action> toAction(
		ProvideFunction provideFunction) {

		return actionSemantics -> request -> Try.of(
			actionSemantics.paramClasses()::stream
		).map(
			stream -> stream.map(
				provideFunction.apply(request)
			).collect(
				toList()
			)
		).mapTry(
			actionSemantics.executeFunction()
		);
	}

	/**
	 * The list of annotations.
	 *
	 * @review
	 */
	@Parameter(order = 5)
	public abstract List<Annotation> annotations();

	/**
	 * The function that executes the action.
	 *
	 * @review
	 */
	@Parameter(order = 6)
	public abstract CheckedFunction1<List<?>, ?> executeFunction();

	/**
	 * The method in which the action is executed.
	 *
	 * @review
	 */
	@Parameter(order = 2)
	public abstract String method();

	/**
	 * The action's name.
	 *
	 * @review
	 */
	@Parameter(order = 1)
	public abstract String name();

	/**
	 * The list of param classes.
	 *
	 * @review
	 */
	@Parameter(order = 3)
	public abstract List<Class<?>> paramClasses();

	/**
	 * The action's resource.
	 *
	 * @review
	 */
	@Parameter(order = 0)
	public abstract Resource resource();

	/**
	 * The class returned by the action.
	 *
	 * @review
	 */
	@Parameter(order = 4)
	public abstract Class<?> returnClass();

	@FunctionalInterface
	public interface AnnotationsStep {

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
		public ExecuteStep annotatedWith(Annotation... annotations);

		/**
		 * Specifies that the action does not receive any params.
		 *
		 * @review
		 */
		public default ExecuteStep notAnnotated() {
			return annotatedWith();
		}

	}

	@FunctionalInterface
	public interface BuildStep {

		/**
		 * Creates the {@link ActionSemantics} object with the information
		 * provided to the builder.
		 *
		 * @review
		 */
		public ActionSemantics build();

	}

	@FunctionalInterface
	public interface ExecuteStep {

		/**
		 * Provides information about the function action's execute function.
		 * This function receives the list of params in the order provided in
		 * the {@link ProvideMethodStep#receivesParams(Class[])} method.
		 *
		 * @review
		 */
		public BuildStep executeFunction(
			CheckedFunction1<List<?>, ?> executeFunction);

	}

	@FunctionalInterface
	public interface FilterStep {

		/**
		 * Provides information about the predicate to apply for filtering the
		 * {@link ActionSemantics}.
		 *
		 * @review
		 */
		public Optional<ActionSemantics> withPredicate(
			Predicate<ActionSemantics> predicate);

	}

	@FunctionalInterface
	public interface MethodStep {

		/**
		 * Provides information about the method in which the action is
		 * executed.
		 *
		 * @review
		 */
		public default ProvideMethodStep method(HTTPMethod httpMethod) {
			return method(httpMethod.name());
		}

		/**
		 * Provides information about the method in which the action is
		 * executed.
		 *
		 * @review
		 */
		public ProvideMethodStep method(String method);

	}

	@FunctionalInterface
	public interface NameStep {

		/**
		 * Provides information about the the action's name.
		 *
		 * @review
		 */
		public MethodStep name(String name);

	}

	@FunctionalInterface
	public interface ProvideMethodStep {

		/**
		 * Specifies that the action does not receive any params.
		 *
		 * @review
		 */
		public default ReturnStep receivesNoParams() {
			return receivesParams();
		}

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
		public ReturnStep receivesParams(Class<?>... classes);

	}

	@FunctionalInterface
	public interface ResourceStep {

		/**
		 * Provides information about the resource for which the action has to
		 * be filtered.
		 *
		 * @review
		 */
		public FilterStep forResource(Resource resource);

	}

	@FunctionalInterface
	public interface ReturnStep {

		/**
		 * Provides information about the class returned by the action.
		 *
		 * @review
		 */
		public AnnotationsStep returns(Class<?> returnClass);

		/**
		 * Specifies that the action does not return anything.
		 *
		 * @review
		 */
		public default AnnotationsStep returnsNothing() {
			return returns(Void.class);
		}

	}

}