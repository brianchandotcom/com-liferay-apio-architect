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

import static com.liferay.apio.architect.internal.annotation.ActionKey.ANY_ROUTE;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;
import static com.liferay.apio.architect.operation.HTTPMethod.DELETE;
import static com.liferay.apio.architect.operation.HTTPMethod.GET;

import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.documentation.APIDescription;
import com.liferay.apio.architect.documentation.APITitle;
import com.liferay.apio.architect.function.throwable.ThrowableTriFunction;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.internal.documentation.Documentation;
import com.liferay.apio.architect.internal.entrypoint.EntryPoint;
import com.liferay.apio.architect.internal.url.ApplicationURL;
import com.liferay.apio.architect.internal.wiring.osgi.manager.documentation.contributor.CustomDocumentationManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.provider.ProviderManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.RepresentableManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper.PathIdentifierMapperManager;
import com.liferay.apio.architect.uri.Path;

import io.vavr.control.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.NotFoundException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides methods to get the different actions provided by the different
 * routers.
 *
 * @author Javier Gamarra
 * @review
 */
@Component(service = ActionManager.class)
public class ActionManagerImpl implements ActionManager {

	public static ActionManager newTestInstance(
		PathIdentifierMapperManager pathIdentifierMapperManager,
		ProviderManager providerManager) {

		ActionManagerImpl actionManagerImpl = new ActionManagerImpl();

		actionManagerImpl.pathIdentifierMapperManager =
			pathIdentifierMapperManager;
		actionManagerImpl.providerManager = providerManager;

		return actionManagerImpl;
	}

	@Override
	public void add(
		ActionKey actionKey,
		ThrowableTriFunction<Object, ?, List<Object>, ?> throwableTriFunction,
		Class... providers) {

		_actionsMap.put(actionKey, throwableTriFunction);

		_providers.put(actionKey, providers);
	}

	public void addCollectionGetter(
		String name,
		ThrowableTriFunction<Object, ?, List<Object>, ?> throwableTriFunction,
		Class... providers) {

		ActionKey actionKey = new ActionKey(GET.name(), name);

		add(actionKey, throwableTriFunction, providers);
	}

	public void addItemGetter(
		String name,
		ThrowableTriFunction<Object, ?, List<Object>, ?> throwableTriFunction,
		Class... providers) {

		ActionKey actionKey = new ActionKey(GET.name(), name, ANY_ROUTE);

		add(actionKey, throwableTriFunction, providers);
	}

	public void addItemRemover(
		String name,
		ThrowableTriFunction<Object, ?, List<Object>, ?> throwableTriFunction,
		Class... providers) {

		ActionKey actionKey = new ActionKey(DELETE.name(), name, ANY_ROUTE);

		add(actionKey, throwableTriFunction, providers);
	}

	public void addNestedGetter(
		String name, String nestedName,
		ThrowableTriFunction<Object, ?, List<Object>, ?> throwableTriFunction,
		Class... providers) {

		ActionKey actionKey = new ActionKey(
			GET.name(), name, ANY_ROUTE, nestedName);

		add(actionKey, throwableTriFunction, providers);
	}

	@Override
	public Either<Action.Error, Action> getAction(
		String method, String param1) {

		ActionKey actionKey = new ActionKey(method, param1);

		return Either.right(_getAction(actionKey, null));
	}

	@Override
	public Either<Action.Error, Action> getAction(
		String method, String param1, String param2) {

		ActionKey actionKey = new ActionKey(method, param1, param2);

		return _getActionsWithId(actionKey);
	}

	@Override
	public Either<Action.Error, Action> getAction(
		String method, String param1, String param2, String param3) {

		ActionKey actionKey = new ActionKey(method, param1, param2, param3);

		return _getActionsWithId(actionKey);
	}

	@Override
	public Either<Action.Error, Action> getAction(
		String method, String param1, String param2, String param3,
		String param4) {

		ActionKey actionKey = new ActionKey(
			method, param1, param2, param3, param4);

		return _getActionsWithId(actionKey);
	}

	@Override
	public List<Action> getActions(
		ActionKey actionKey, Credentials credentials) {

		Set<ActionKey> actionKeys = _actionsMap.keySet();

		Stream<ActionKey> stream = actionKeys.stream();

		return stream.filter(
			childActionKey ->
				_sameResource(actionKey, childActionKey) &&
				 (_isCustomActionOfCollection(actionKey, childActionKey) ||
				  _isCustomActionOfItem(actionKey, childActionKey) ||
				  _isCustomOfActionNested(actionKey, childActionKey))
		).filter(
			this::_isValidAction
		).map(
			childActionKey -> {
				Object id = _getId(
					actionKey.getResource(), actionKey.getIdOrAction());

				return _getAction(childActionKey, id);
			}
		).collect(
			Collectors.toList()
		);
	}

	public Map<ActionKey, ThrowableTriFunction<Object, ?, List<Object>, ?>>
		getActionsMap() {

		return _actionsMap;
	}

	@Override
	public Documentation getDocumentation(
		Supplier<Optional<APITitle>> apiTitleOptionalSupplier,
		Supplier<Optional<APIDescription>> apiDescriptionOptionalSupplier,
		Supplier<Optional<ApplicationURL>> applicationUrlOptionalSupplier) {

		return new Documentation(
			apiTitleOptionalSupplier, apiDescriptionOptionalSupplier,
			applicationUrlOptionalSupplier,
			() -> _representableManager.getRepresentors(), () -> this,
			() -> _customDocumentationManager.getCustomDocumentation());
	}

	@Override
	public EntryPoint getEntryPoint() {
		return () -> {
			List<String> list = new ArrayList<>();

			list.addAll(INSTANCE.getRootResourceNamesSdk());
			list.addAll(INSTANCE.getRootResourceNames());

			return list;
		};
	}

	@Reference
	protected PathIdentifierMapperManager pathIdentifierMapperManager;

	@Reference
	protected ProviderManager providerManager;

	private Action _getAction(ActionKey actionKey, Object id) {
		return new Action() {

			@Override
			public Object apply(HttpServletRequest httpServletRequest) {
				return Try.fromFallible(
					() -> _getActionThrowableTriFunction(actionKey)
				).map(
					action -> action.apply(
						id, null,
						(List<Object>)_getProviders(
							httpServletRequest, actionKey))
				).orElseThrow(
					NotFoundException::new
				);
			}

			@Override
			public ActionKey getActionKey() {
				return actionKey;
			}

			@Override
			public Optional<String> getURIOptional() {
				Optional<Path> optionalPath =
					pathIdentifierMapperManager.mapToPath(
						actionKey.getResource(), actionKey.getIdOrAction());

				return optionalPath.map(
					path -> path.asURI() + "/" + actionKey.getNestedResource());
			}

		};
	}

	private Either<Action.Error, Action> _getActionsWithId(
		ActionKey actionKey) {

		Object id = _getId(actionKey.getResource(), actionKey.getIdOrAction());

		return Either.right(_getAction(actionKey, id));
	}

	private ThrowableTriFunction
		<Object, ?, List<Object>, ?> _getActionThrowableTriFunction(
			ActionKey actionKey) {

		if (_actionsMap.containsKey(actionKey)) {
			return _actionsMap.get(actionKey);
		}

		return _actionsMap.get(actionKey.getGenericActionKey());
	}

	private Object _getId(String param1, String param2) {
		try {
			return pathIdentifierMapperManager.mapToIdentifierOrFail(
				new Path(param1, param2));
		}
		catch (Error e) {
			return null;
		}
	}

	private List<Object> _getProvidedObjects(
		Class<Object>[] value, HttpServletRequest httpServletRequest) {

		return Stream.of(
			value
		).map(
			provider -> providerManager.provideMandatory(
				httpServletRequest, provider)
		).collect(
			Collectors.toList()
		);
	}

	private List<Object> _getProviders(
		HttpServletRequest httpServletRequest, ActionKey actionKey) {

		return Try.fromFallible(
			() -> _getProvidersByParam(actionKey)
		).map(
			value -> _getProvidedObjects(value, httpServletRequest)
		).orElseGet(
			ArrayList::new
		);
	}

	private Class<Object>[] _getProvidersByParam(ActionKey actionKey) {
		if (_providers.containsKey(actionKey)) {
			return _providers.get(actionKey);
		}

		return _providers.get(actionKey.getGenericActionKey());
	}

	private boolean _isCustomActionOfCollection(
		ActionKey actionKey, ActionKey childActionKey) {

		if (actionKey.isCollection() && childActionKey.isCustom()) {
			return true;
		}

		return false;
	}

	private boolean _isCustomActionOfItem(
		ActionKey actionKey, ActionKey childActionKey) {

		if (!actionKey.isNested() && actionKey.isItem() &&
			childActionKey.isItem() &&
			(_getActionThrowableTriFunction(
				new ActionKey("GET", childActionKey.getNestedResource())) ==
					null)) {

			return true;
		}

		return false;
	}

	private boolean _isCustomOfActionNested(
		ActionKey actionKey, ActionKey childActionKey) {

		String nestedResource = actionKey.getNestedResource();

		if (actionKey.isNested() &&
			nestedResource.equals(childActionKey.getNestedResource())) {

			return true;
		}

		return false;
	}

	private boolean _isValidAction(ActionKey actionKey) {
		if (actionKey.isCollection()) {
			return _actionsMap.containsKey(actionKey);
		}

		if (_getActionThrowableTriFunction(actionKey) != null) {
			return true;
		}

		return false;
	}

	private boolean _sameResource(
		ActionKey actionKey, ActionKey childActionKey) {

		String resource = childActionKey.getResource();

		return resource.equals(actionKey.getResource());
	}

	private final Map
		<ActionKey, ThrowableTriFunction<Object, ?, List<Object>, ?>>
			_actionsMap = new HashMap<>();

	@Reference
	private CustomDocumentationManager _customDocumentationManager;

	private final Map<ActionKey, Class[]> _providers = new HashMap<>();

	@Reference
	private RepresentableManager _representableManager;

}