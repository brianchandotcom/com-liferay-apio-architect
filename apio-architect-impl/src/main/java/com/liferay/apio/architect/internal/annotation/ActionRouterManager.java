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

package com.liferay.apio.architect.internal.annotation;

import static com.liferay.apio.architect.internal.annotation.representor.StringUtil.toLowercaseSlug;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getFormOptional;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getParamClasses;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getResource;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getReturnClass;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.updateParams;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.updateReturn;
import static com.liferay.apio.architect.internal.annotation.util.AnnotationUtil.findAnnotation;
import static com.liferay.apio.architect.internal.annotation.util.BodyUtil.isListBody;
import static com.liferay.apio.architect.internal.annotation.util.BodyUtil.needsBody;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import static io.leangen.geantyref.GenericTypeReflector.annotate;
import static io.leangen.geantyref.GenericTypeReflector.getTypeParameter;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import static org.slf4j.LoggerFactory.getLogger;

import com.liferay.apio.architect.annotation.Actions.Action;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.url.ApplicationURL;
import com.liferay.apio.architect.internal.url.ServerURL;
import com.liferay.apio.architect.internal.wiring.osgi.manager.provider.ProviderManager;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.router.ActionRouter;

import io.vavr.Tuple;
import io.vavr.control.Option;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;

/**
 * Keeps track of all {@link ActionRouter} registered and provides a method to
 * computeActionRouters the actions from each action router.
 *
 * @author Javier Gamarra
 * @review
 */
@Component(service = ActionRouterManager.class)
public class ActionRouterManager {

	@Deactivate
	public void deactivate() {
		INSTANCE.clear();
	}

	public Stream<ActionSemantics> getActionSemantics() {
		return Optional.ofNullable(
			INSTANCE.getActionSemantics(this::_computeActionSemantics)
		).map(
			List::stream
		).orElseGet(
			Stream::empty
		);
	}

	@SuppressWarnings("Convert2MethodRef")
	private void _computeActionSemantics() {
		List<String> list = _providerManager.getMissingProviders(
			_mandatoryClassNames);

		if (!list.isEmpty()) {
			_logger.warn("Missing providers for mandatory classes: {}", list);

			return;
		}

		for (ActionRouter<?> actionRouter : _actionRouters) {
			Class<? extends ActionRouter> clazz = actionRouter.getClass();

			Option<String> optionName = Option.of(
				getTypeParameter(clazz, _actionRouterTypeParameter)
			).map(
				type -> annotate(type)
			).flatMap(
				type -> Option.of(type.getAnnotation(Type.class))
			).map(
				Type::value
			).map(
				string -> toLowercaseSlug(string)
			);

			if (optionName.isEmpty()) {
				_logger.warn("Unable to get name for ActionRouter {}", clazz);

				continue;
			}

			String name = optionName.get();

			Stream.of(
				clazz.getMethods()
			).map(
				method -> Tuple.of(
					method, findAnnotation(Action.class, method), name)
			).filter(
				tuple -> tuple._2.isPresent()
			).map(
				tuple -> tuple.map2(Optional::get)
			).map(
				tuple -> _getActionSemanticsOption(
					actionRouter, tuple._3, tuple._1, tuple._2)
			).filter(
				Option::isDefined
			).map(
				Option::get
			).forEach(
				INSTANCE::addActionSemantics
			);
		}
	}

	private Option<ActionSemantics> _getActionSemanticsOption(
		ActionRouter actionRouter, String name, Method method, Action action) {

		Optional<Form<Object>> formOptional = getFormOptional(
			method, _formManager::getFormOptional);

		if (needsBody(method) && !formOptional.isPresent()) {
			_logger.warn(
				"Unable to find form for method with name: {}",
				method.getName());

			return none();
		}

		Function<Body, Object> bodyFunction = body -> formOptional.map(
			form -> isListBody(method) ? form.getList(body) : form.get(body)
		).orElse(
			null
		);

		Class<?>[] paramClasses = getParamClasses(method);

		Class<?> returnClass = getReturnClass(method);

		ActionSemantics actionSemantics = ActionSemantics.ofResource(
			getResource(method, name)
		).name(
			action.name()
		).method(
			action.httpMethod()
		).receivesParams(
			paramClasses
		).returns(
			returnClass
		).annotatedWith(
			method.getDeclaredAnnotations()
		).executeFunction(
			paramInstances -> {
				Object[] updatedParams = updateParams(
					paramInstances, bodyFunction);

				Object result = method.invoke(actionRouter, updatedParams);

				return updateReturn(result, paramInstances, name);
			}
		).build();

		return some(actionSemantics);
	}

	private static final TypeVariable<Class<ActionRouter>>
		_actionRouterTypeParameter = ActionRouter.class.getTypeParameters()[0];
	private static final List<String> _mandatoryClassNames = Arrays.asList(
		ApplicationURL.class.getName(), Credentials.class.getName(),
		Pagination.class.getName(), ServerURL.class.getName());

	@Reference(
		cardinality = MULTIPLE, policyOption = GREEDY,
		service = ActionRouter.class
	)
	private List<ActionRouter<?>> _actionRouters;

	@Reference
	private FormManager _formManager;

	private final Logger _logger = getLogger(getClass());

	@Reference
	private ProviderManager _providerManager;

}