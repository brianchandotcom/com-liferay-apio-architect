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

package com.liferay.apio.architect.internal.operation.util;

import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.internal.annotation.ActionKey;
import com.liferay.apio.architect.internal.operation.CreateOperation;
import com.liferay.apio.architect.internal.operation.DeleteOperation;
import com.liferay.apio.architect.internal.operation.RetrieveOperation;
import com.liferay.apio.architect.internal.operation.UpdateOperation;
import com.liferay.apio.architect.operation.Operation;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Util class that converts between the old Operations API and the new Action
 * API
 *
 * @author Javier Gamarra
 */
public class OperationUtil {

	public static List<Operation> toOperations(List<Action> actions) {
		Stream<Action> stream = actions.stream();

		return stream.map(
			OperationUtil::_toOperation
		).collect(
			Collectors.toList()
		);
	}

	private static Operation _toOperation(Action action) {
		ActionKey actionKey = action.getActionKey();

		String resourceName = actionKey.getResourceName();

		Optional<String> uriOptional = action.getURIOptional();

		String uri = uriOptional.orElse(null);

		String httpMethodName = actionKey.getHttpMethodName();

		if (httpMethodName.equals("GET") && actionKey.isCollection()) {
			return new RetrieveOperation(resourceName, true, uri, null);
		}
		else if (httpMethodName.equals("POST")) {
			return new CreateOperation(null, resourceName, uri, null);
		}
		else if (httpMethodName.equals("DELETE")) {
			return new DeleteOperation(resourceName, uri, null);
		}
		else if (httpMethodName.equals("PUT")) {
			return new UpdateOperation(null, resourceName, uri, null);
		}

		return new RetrieveOperation(resourceName, false, uri, null);
	}

	private OperationUtil() {
	}

}