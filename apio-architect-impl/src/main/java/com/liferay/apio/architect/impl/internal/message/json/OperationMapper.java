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

package com.liferay.apio.architect.impl.internal.message.json;

import com.liferay.apio.architect.operation.HTTPMethod;
import com.liferay.apio.architect.operation.Operation;

/**
 * Maps an {@link Operation} to its representation in a JSON object.
 *
 * <p>
 * The methods {@link #onStartOperation} and {@link #onFinishOperation} are
 * called when the writer starts and finishes the operation, respectively.
 * Otherwise, the message mapper's methods aren't called in a particular order.
 * </p>
 *
 * @author Javier Gamarra
 * @review
 */
public interface OperationMapper {

	/**
	 * Maps a resource operation form's URL to its JSON object representation.
	 *
	 * @param singleModelJSONObjectBuilder the JSON object builder for the model
	 * @param operationJSONObjectBuilder the JSON object builder for the
	 *        operation
	 * @param url the operation form's URL
	 */
	public default void mapOperationFormURL(
		JSONObjectBuilder singleModelJSONObjectBuilder,
		JSONObjectBuilder operationJSONObjectBuilder, String url) {
	}

	/**
	 * Maps a resource operation's method to its JSON object representation.
	 *
	 * @param singleModelJSONObjectBuilder the JSON object builder for the model
	 * @param operationJSONObjectBuilder the JSON object builder for the
	 *        operation
	 * @param httpMethod the operation's method
	 */
	public default void mapOperationMethod(
		JSONObjectBuilder singleModelJSONObjectBuilder,
		JSONObjectBuilder operationJSONObjectBuilder, HTTPMethod httpMethod) {
	}

	/**
	 * Finishes the operation. This is the final operation-mapper method the
	 * writer calls.
	 *
	 * @param singleModelJSONObjectBuilder the JSON object builder for the model
	 * @param operationJSONObjectBuilder the JSON object builder for the
	 *        operation
	 * @param operation the operation
	 */
	public default void onFinishOperation(
		JSONObjectBuilder singleModelJSONObjectBuilder,
		JSONObjectBuilder operationJSONObjectBuilder, Operation operation) {
	}

	/**
	 * Starts an operation. This is the first operation-mapper method the writer
	 * calls.
	 *
	 * @param singleModelJSONObjectBuilder the JSON object builder for the model
	 * @param operationJSONObjectBuilder the JSON object builder for the
	 *        operation
	 * @param operation the operation
	 */
	public default void onStartOperation(
		JSONObjectBuilder singleModelJSONObjectBuilder,
		JSONObjectBuilder operationJSONObjectBuilder, Operation operation) {
	}

}