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

package com.liferay.apio.architect.internal.message.json.ld;

import static java.lang.String.join;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;

import java.util.List;

/**
 * Provides utility functions for JSON-LD message mappers.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 */
public class JSONLDMessageMapperUtil {

	/**
	 * Returns an action's ID.
	 *
	 * @param  resource the action's resource
	 * @param  actionName the action's name
	 * @return the action's ID
	 * @review
	 */
	public static String getActionId(Resource resource, String actionName) {
		String resourceName = "";

		if (resource instanceof Nested) {
			Item parent = ((Nested)resource).parent();

			resourceName = join("/", parent.name(), resource.name());
		}
		else {
			resourceName = resource.name();
		}

		return "_:" + join("/", resourceName, actionName);
	}

	/**
	 * Returns the list of types for a certain action based on its name.
	 *
	 * @review
	 */
	public static List<String> getActionTypes(String actionName) {
		if ("create".equals(actionName)) {
			return asList("CreateAction", "Operation");
		}
		else if ("batch-create".equals(actionName)) {
			return asList("CreateAction", "Operation");
		}
		else if ("remove".equals(actionName)) {
			return asList("DeleteAction", "Operation");
		}
		else if ("replace".equals(actionName)) {
			return asList("ReplaceAction", "Operation");
		}
		else {
			return singletonList("Operation");
		}
	}

	private JSONLDMessageMapperUtil() {
		throw new UnsupportedOperationException();
	}

}