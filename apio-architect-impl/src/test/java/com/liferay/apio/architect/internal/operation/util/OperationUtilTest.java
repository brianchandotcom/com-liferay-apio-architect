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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.junit.MatcherAssert.assertThat;

import com.liferay.apio.architect.internal.annotation.Action;
import com.liferay.apio.architect.internal.annotation.ActionKey;
import com.liferay.apio.architect.internal.operation.CreateOperation;
import com.liferay.apio.architect.internal.operation.DeleteOperation;
import com.liferay.apio.architect.internal.operation.RetrieveOperation;
import com.liferay.apio.architect.internal.operation.UpdateOperation;
import com.liferay.apio.architect.operation.HTTPMethod;
import com.liferay.apio.architect.operation.Operation;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

/**
 * @author Javier Gamarra
 */
public class OperationUtilTest {

	@Test
	public void testDeleteActionIsConvertedToDeleteOperation() {
		Action action = _createFakeActionWithActionKey(
			new ActionKey("DELETE", "param1", "param2"));

		List<Operation> operations = OperationUtil.toOperations(
			Collections.singletonList(action));

		assertThat(operations.size(), is(1));
		Operation operation = operations.get(0);

		assertThat(operation, instanceOf(DeleteOperation.class));
		assertThat(operation.getHttpMethod(), is(HTTPMethod.DELETE));
		assertThat(operation.getName(), is("param1/delete"));
	}

	@Test
	public void testGetActionIsConvertedToRetrieveOperation() {
		Action action = _createFakeActionWithActionKey(
			new ActionKey("GET", "param1", "param2"));

		List<Operation> operations = OperationUtil.toOperations(
			Collections.singletonList(action));

		assertThat(operations.size(), is(1));
		Operation operation = operations.get(0);

		assertThat(operation, instanceOf(RetrieveOperation.class));
		assertThat(operation.getHttpMethod(), is(HTTPMethod.GET));
		assertThat(operation.getName(), is("param1/retrieve"));
	}

	@Test
	public void testGetCollectionActionIsConvertedToRetrieveCollectionOperation() {
		Action action = _createFakeActionWithActionKey(
			new ActionKey("GET", "param1"));

		List<Operation> operations = OperationUtil.toOperations(
			Collections.singletonList(action));

		assertThat(operations.size(), is(1));
		Operation operation = operations.get(0);

		assertThat(operation, instanceOf(RetrieveOperation.class));
		assertThat(operation.getHttpMethod(), is(HTTPMethod.GET));
		assertThat(operation.getName(), is("param1/retrieve"));
	}

	@Test
	public void testPostActionIsConvertedToCreateOperation() {
		Action action = _createFakeActionWithActionKey(
			new ActionKey("POST", "param1"));

		List<Operation> operations = OperationUtil.toOperations(
			Collections.singletonList(action));

		assertThat(operations.size(), is(1));
		Operation operation = operations.get(0);

		assertThat(operation, instanceOf(CreateOperation.class));
		assertThat(operation.getHttpMethod(), is(HTTPMethod.POST));
		assertThat(operation.getName(), is("param1/create"));
	}

	@Test
	public void testPutActionIsConvertedToUpdateOperation() {
		Action action = _createFakeActionWithActionKey(
			new ActionKey("PUT", "param1"));

		List<Operation> operations = OperationUtil.toOperations(
			Collections.singletonList(action));

		assertThat(operations.size(), is(1));
		Operation operation = operations.get(0);

		assertThat(operation, instanceOf(UpdateOperation.class));
		assertThat(operation.getHttpMethod(), is(HTTPMethod.PUT));
		assertThat(operation.getName(), is("param1/update"));
	}

	private Action _createFakeActionWithActionKey(ActionKey actionKey) {
		return new Action() {

			@Override
			public Object apply(HttpServletRequest httpServletRequest) {
				return null;
			}

			@Override
			public ActionKey getActionKey() {
				return actionKey;
			}

		};
	}

}