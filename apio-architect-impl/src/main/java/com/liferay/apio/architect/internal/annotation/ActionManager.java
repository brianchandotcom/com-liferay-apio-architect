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

import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.internal.annotation.Action.Error;
import com.liferay.apio.architect.internal.documentation.Documentation;
import com.liferay.apio.architect.internal.entrypoint.EntryPoint;

import io.vavr.CheckedFunction3;
import io.vavr.control.Either;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides methods to get the different actions provided by the different
 * routers.
 *
 * <p>
 * It also contains special methods for obtaining the API {@link EntryPoint} and
 * {@link Documentation}.
 * </p>
 *
 * @author Alejandro Hernández
 * @see    com.liferay.apio.architect.router.ActionRouter
 * @see    com.liferay.apio.architect.router.CollectionRouter
 * @see    com.liferay.apio.architect.router.ItemRouter
 * @see    com.liferay.apio.architect.router.NestedCollectionRouter
 * @review
 */
public interface ActionManager {

	/**
	 * Adds an action with the key specified by the actionKey parameter, that
	 * calls a actionFunction with ID, body and parameters based on a
	 * varags of providers
	 *
	 * @param  actionKey the path parameters to add an Action
	 * @param  actionFunction the method to call in that path
	 * @param  providers the list of providers to supply to the action
	 * @review
	 */
	public void add(
		ActionKey actionKey,
		CheckedFunction3<Object, ?, List<Object>, ?> actionFunction,
		Class... providers);

	/**
	 * Returns the action that requires one parameter for the provided HTTP
	 * method, if found. Returns {@link Error.NotAllowed} if an action exist,
	 * but not with the provided method. Returns {@link Error.NotFound} if no
	 * action is found.
	 *
	 * @param  method the HTTP method of the action
	 * @param  param the parameter
	 * @return the action, if found; an {@link Error} otherwise
	 * @review
	 */
	public Either<Error, Action> getAction(String method, String param);

	/**
	 * Returns the action that requires two parameters for the provided HTTP
	 * method, if found. Returns {@link Error.NotAllowed} if an action exist,
	 * but not with the provided method. Returns {@link Error.NotFound} if no
	 * action is found.
	 *
	 * @param  method the HTTP method of the action
	 * @param  param1 the first parameter
	 * @param  param2 the second parameter
	 * @return the action, if found; an {@link Error} otherwise
	 * @review
	 */
	public Either<Error, Action> getAction(
		String method, String param1, String param2);

	/**
	 * Returns the action that requires three parameters for the provided HTTP
	 * method, if found. Returns {@link Error.NotAllowed} if an action exist,
	 * but not with the provided method. Returns {@link Error.NotFound} if no
	 * action is found.
	 *
	 * @param  method the HTTP method of the action
	 * @param  param1 the first parameter
	 * @param  param2 the second parameter
	 * @param  param3 the third parameter
	 * @return the action, if found; an {@link Error} otherwise
	 * @review
	 */
	public Either<Error, Action> getAction(
		String method, String param1, String param2, String param3);

	/**
	 * Returns the action that requires four parameters for the provided HTTP
	 * method, if found. Returns {@link Error.NotAllowed} if an action exist,
	 * but not with the provided method. Returns {@link Error.NotFound} if no
	 * action is found.
	 *
	 * @param  method the HTTP method of the action
	 * @param  param1 the first parameter
	 * @param  param2 the second parameter
	 * @param  param3 the third parameter
	 * @param  param4 the fourth parameter
	 * @return the action, if found; an {@link Error} otherwise
	 * @review
	 */
	public Either<Error, Action> getAction(
		String method, String param1, String param2, String param3,
		String param4);

	/**
	 * Return the list of actions that are valid in that path
	 *
	 * @param  actionKey the path parameters to add an Action
	 * @param  credentials the user logged in to calculate the right operations
	 * @review
	 */
	public List<Action> getActions(
		ActionKey actionKey, Credentials credentials);

	/**
	 * The API documentation with the list of actions and resources.
	 *
	 * @review
	 */
	public Documentation getDocumentation(
		HttpServletRequest httpServletRequest);

	/**
	 * The API entry point with the root resources.
	 *
	 * @review
	 */
	public EntryPoint getEntryPoint();

}