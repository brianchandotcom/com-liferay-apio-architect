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
import com.liferay.apio.architect.internal.documentation.Documentation;
import com.liferay.apio.architect.internal.entrypoint.EntryPoint;
import com.liferay.apio.architect.internal.url.ApplicationURL;
import com.liferay.apio.architect.internal.wiring.osgi.manager.documentation.contributor.CustomDocumentationManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.provider.ProviderManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.RepresentableManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper.PathIdentifierMapperManager;
import com.liferay.apio.architect.uri.Path;

import io.vavr.CheckedFunction3;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.util.ArrayList;
import java.util.Collections;
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
		CheckedFunction3<Object, ?, List<Object>, ?> actionFunction,
		Class... providers) {

		_actionsMap.put(actionKey, actionFunction);

		_providers.put(actionKey, providers);
	}

	public void addCollectionGetter(
		String name,
		CheckedFunction3<Object, ?, List<Object>, ?> actionFunction,
		Class... providers) {

		ActionKey actionKey = new ActionKey(GET.name(), name);

		add(actionKey, actionFunction, providers);
	}

	public void addItemGetter(
		String name,
		CheckedFunction3<Object, ?, List<Object>, ?> actionFunction,
		Class... providers) {

		ActionKey actionKey = new ActionKey(GET.name(), name, ANY_ROUTE);

		add(actionKey, actionFunction, providers);
	}

	public void addItemRemover(
		String name,
		CheckedFunction3<Object, ?, List<Object>, ?> actionFunction,
		Class... providers) {

		ActionKey actionKey = new ActionKey(DELETE.name(), name, ANY_ROUTE);

		add(actionKey, actionFunction, providers);
	}

	public void addNestedGetter(
		String name, String nestedName,
		CheckedFunction3<Object, ?, List<Object>, ?> actionFunction,
		Class... providers) {

		ActionKey actionKey = _getActionKeyForNested(name, nestedName);

		add(actionKey, actionFunction, providers);
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
				Object id = _getId(actionKey.getPath());

				return _getAction(childActionKey, id);
			}
		).collect(
			Collectors.toList()
		);
	}

	@Override
	public Documentation getDocumentation(
		HttpServletRequest httpServletRequest) {

		Supplier<Optional<APITitle>> apiTitleSupplier =
			() -> providerManager.provideOptional(
				httpServletRequest, APITitle.class);

		Supplier<Optional<APIDescription>> apiDescriptionSupplier =
			() -> providerManager.provideOptional(
				httpServletRequest, APIDescription.class);

		Supplier<Optional<ApplicationURL>> applicationUrlSupplier =
			() -> providerManager.provideOptional(
				httpServletRequest, ApplicationURL.class);

		return new Documentation(
			apiTitleSupplier, apiDescriptionSupplier, applicationUrlSupplier,
			() -> _representableManager.getRepresentors(), _actionsMap::keySet,
			actionKey -> getActions(actionKey, null),
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
				return Try.of(
					() -> _getActionFunction(actionKey)
				).mapTry(
					action -> action.apply(
						id, null,
						(List<Object>)_getProviders(
							httpServletRequest, actionKey))
				).getOrElseThrow(
					() -> new NotFoundException("Not Found")
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

	private CheckedFunction3<Object, ?, List<Object>, ?> _getActionFunction(
		ActionKey actionKey) {

		if (_actionsMap.containsKey(actionKey)) {
			return _actionsMap.get(actionKey);
		}

		return _actionsMap.get(actionKey.getGenericActionKey());
	}

	private ActionKey _getActionKeyForNested(String name, String nestedName) {
		if (name.equals("r")) {
			return new ActionKey(GET.name(), name, nestedName, ANY_ROUTE);
		}

		return new ActionKey(GET.name(), name, ANY_ROUTE, nestedName);
	}

	private Either<Action.Error, Action> _getActionsWithId(
		ActionKey actionKey) {

		Object id = _getId(actionKey.getPath());

		return Either.right(_getAction(actionKey, id));
	}

	private Object _getId(Path path) {
		try {
			return pathIdentifierMapperManager.mapToIdentifierOrFail(path);
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

		return Try.of(
			() -> _getProvidersByParam(actionKey)
		).map(
			value -> _getProvidedObjects(value, httpServletRequest)
		).getOrElse(
			Collections::emptyList
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

		if (actionKey.isCollection() &&
			(childActionKey.isCollection() || childActionKey.isCustom())) {

			return true;
		}

		return false;
	}

	private boolean _isCustomActionOfItem(
		ActionKey actionKey, ActionKey childActionKey) {

		if (!actionKey.isNested() && actionKey.isItem() &&
			childActionKey.isItem() &&
			(_getActionFunction(
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

		if (_getActionFunction(actionKey) != null) {
			return true;
		}

		return false;
	}

	private boolean _sameResource(
		ActionKey actionKey, ActionKey childActionKey) {

		String resource = childActionKey.getResource();

		return resource.equals(actionKey.getResource());
	}

	private final Map<ActionKey, CheckedFunction3<Object, ?, List<Object>, ?>>
		_actionsMap = new HashMap<>();

	@Reference
	private CustomDocumentationManager _customDocumentationManager;

	private final Map<ActionKey, Class[]> _providers = new HashMap<>();

	@Reference
	private RepresentableManager _representableManager;

}