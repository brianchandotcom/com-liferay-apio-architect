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

package com.liferay.apio.architect.internal.annotation.representor;

import static com.liferay.apio.architect.internal.annotation.ActionKey.ANY_ROUTE;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getActionKey;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getParameters;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;

import static org.junit.Assert.assertThat;

import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.internal.annotation.ActionKey;
import com.liferay.apio.architect.internal.annotation.representor.types.DummyRouter;
import com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil;
import com.liferay.apio.architect.internal.pagination.PaginationImpl;
import com.liferay.apio.architect.pagination.Pagination;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * @author Javier Gamarra
 */
public class ActionRouterUtilTest {

	@Test
	public void testACollectionRouteCreatesAParam1ActionKey()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrievePage", Pagination.class);

		ActionKey actionKey = getActionKey(method, "blog", "GET");

		assertThat(actionKey.getHttpMethodName(), is("GET"));
		assertThat(actionKey.getResource(), is("blog"));
		assertThat(actionKey.getIdOrAction(), is(nullValue()));
		assertThat(actionKey.getNestedResource(), is(nullValue()));
	}

	@Test
	public void testACustomRouteWithIdCreatesAParam3ActionKey()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrieveFirstTenElements", Pagination.class, Credentials.class,
			Long.class, String.class);

		ActionKey actionKey = getActionKey(method, "blog", "GET");

		assertThat(actionKey.getHttpMethodName(), is("GET"));
		assertThat(actionKey.getResource(), is("blog"));
		assertThat(actionKey.getIdOrAction(), is(ANY_ROUTE));
		assertThat(actionKey.getNestedResource(), is("first-ten-elements"));
	}

	@Test
	public void testANestedRouteCreatesAParam3ActionKey()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrieveChild", Long.class);

		ActionKey actionKey = getActionKey(method, "blog", "GET");

		assertThat(actionKey.getHttpMethodName(), is("GET"));
		assertThat(actionKey.getResource(), is("dummy"));
		assertThat(actionKey.getIdOrAction(), is(ANY_ROUTE));
		assertThat(actionKey.getNestedResource(), is("blog"));
	}

	@Test
	public void testAnItemRouteCreatesAParam2ActionKey()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod("retrieve", Long.class);

		ActionKey actionKey = getActionKey(method, "blog", "GET");

		assertThat(actionKey.getHttpMethodName(), is("GET"));
		assertThat(actionKey.getResource(), is("blog"));
		assertThat(actionKey.getIdOrAction(), is(ANY_ROUTE));
		assertThat(actionKey.getNestedResource(), is(nullValue()));
	}

	@Test
	public void testFillParametersInTheRightOrder()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrieveFirstTenElements", Pagination.class, Credentials.class,
			Long.class, String.class);

		List<Object> objects = Arrays.asList(
			(Credentials)() -> null,
			new PaginationImpl(0, 10) {
			});

		Object[] parameters = getParameters(method, 1L, "", objects);

		assertThat(parameters.length, is(4));
		assertThat((Pagination)parameters[0], isA(Pagination.class));
		assertThat((Credentials)parameters[1], isA(Credentials.class));
		assertThat(parameters[2].getClass(), equalTo(Long.class));
		assertThat(parameters[3].getClass(), equalTo(String.class));
	}

	@Test
	public void testFindObjectInList() {
	}

	@Test
	public void testGetProvidersFromMethod() throws NoSuchMethodException {
		Method method = DummyRouter.class.getMethod(
			"retrievePage", Pagination.class);

		Class<?>[] providers = ActionRouterUtil.getProviders(method);

		assertThat(providers.length, is(1));
		assertThat(providers, hasItemInArray(Pagination.class));
	}

	@Test
	public void testGetProvidersFromMethodWithId()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrieveFirstTenElements", Pagination.class, Credentials.class,
			Long.class, String.class);

		Class<?>[] providers = ActionRouterUtil.getProviders(method);

		assertThat(providers.length, is(2));
		assertThat(providers, hasItemInArray(Pagination.class));
		assertThat(providers, hasItemInArray(Credentials.class));
	}

	@Test
	public void testGetProvidersFromMethodWithoutThem()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod("retrieve", Long.class);

		Class<?>[] providers = ActionRouterUtil.getProviders(method);

		assertThat(providers.length, is(0));
	}

	@Test
	public void testMethodWithoutAnnotationsDetectParameters()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrievePage", Pagination.class);

		List<Object> objects = Arrays.asList(
			(Credentials)() -> null,
			new PaginationImpl(0, 10) {
			});

		Object[] parameters = getParameters(method, 1L, null, objects);

		assertThat(parameters.length, is(1));
		assertThat((Pagination)parameters[0], isA(Pagination.class));
	}

}