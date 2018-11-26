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

import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.execute;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getParamClasses;
import static com.liferay.apio.architect.internal.annotation.util.ActionRouterUtil.getResource;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.form.Body;
import com.liferay.apio.architect.internal.action.resource.Resource;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.internal.annotation.representor.types.DummyRouter;
import com.liferay.apio.architect.internal.pagination.PaginationImpl;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;

import java.lang.reflect.Method;

import org.junit.Test;

/**
 * @author Javier Gamarra
 */
public class ActionRouterUtilTest {

	@Test
	public void testExecuteReturnLeavesNullAsNull() throws Throwable {
		Object result = execute(Paged.of("name"), emptyList(), __ -> null);

		assertNull(result);
	}

	@Test
	public void testGetParamClassesFromMethodWithIdAndBody()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrieveFirstTenElements", Pagination.class, Credentials.class,
			Long.class, String.class);

		Class<?>[] providers = getParamClasses(method);

		Class<?>[] expected =
			{Pagination.class, Credentials.class, Id.class, Body.class};

		assertThat(providers, is(arrayContaining(expected)));
	}

	@Test
	public void testGetParamClassesFromMethodWithParentId()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrieveChild", Long.class);

		Class<?>[] providers = getParamClasses(method);

		assertThat(providers, is(arrayContaining(ParentId.class)));
	}

	@Test
	public void testGetProvidersFromMethod() throws NoSuchMethodException {
		Method method = DummyRouter.class.getMethod(
			"retrievePage", Pagination.class);

		Class<?>[] paramClasses = getParamClasses(method);

		assertThat(paramClasses, is(arrayContaining(Pagination.class)));
	}

	@Test
	public void testGetResourceReturnsItemResource()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod("retrieve", Long.class);

		Resource resource = getResource(method, "blog");

		assertThat(resource, is(Item.of("blog")));
	}

	@Test
	public void testGetResourceReturnsNestedResource()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrieveChild", Long.class);

		Resource resource = getResource(method, "blog");

		assertThat(resource, is(Nested.of(Item.of("dummy"), "blog")));
	}

	@Test
	public void testGetResourceReturnsPagedResource()
		throws NoSuchMethodException {

		Method method = DummyRouter.class.getMethod(
			"retrievePage", Pagination.class);

		Resource resource = getResource(method, "blog");

		assertThat(resource, is(Paged.of("blog")));
	}

	@Test
	public void testUpdateReturnTransformsListIntoPage() throws Throwable {
		Object object = execute(
			Paged.of("name"), emptyList(), __ -> singletonList("1"));

		assertThat(object, is(notNullValue()));
		assertThat(object, is(instanceOf(Page.class)));

		Page<?> page = (Page<?>)object;

		assertThat(page.getItems(), contains("1"));
		assertThat(page.getItemsPerPage(), is(1));
		assertThat(page.getLastPageNumber(), is(1));
		assertThat(page.getOperations(), is(empty()));
		assertThat(page.getPageNumber(), is(1));
		assertThat(page.getPathOptional(), is(emptyOptional()));
		assertThat(page.getResourceName(), is("name"));
		assertThat(page.getTotalCount(), is(1));
		assertThat(page.hasNext(), is(false));
		assertThat(page.hasPrevious(), is(false));
	}

	@Test
	public void testUpdateReturnTransformsPageItemsIntoPageWithoutParam()
		throws Throwable {

		PageItems<?> pageItems = new PageItems<>(singletonList("1"), 31);

		Object result = execute(Paged.of("name"), emptyList(), __ -> pageItems);

		assertThat(result, is(notNullValue()));
		assertThat(result, is(instanceOf(Page.class)));

		Page<?> page = (Page<?>)result;

		assertThat(page.getItems(), contains("1"));
		assertThat(page.getItemsPerPage(), is(31));
		assertThat(page.getLastPageNumber(), is(1));
		assertThat(page.getOperations(), is(empty()));
		assertThat(page.getPageNumber(), is(1));
		assertThat(page.getPathOptional(), is(emptyOptional()));
		assertThat(page.getResourceName(), is("name"));
		assertThat(page.getTotalCount(), is(31));
		assertThat(page.hasNext(), is(false));
		assertThat(page.hasPrevious(), is(false));
	}

	@Test
	public void testUpdateReturnTransformsPageItemsIntoPageWithParam()
		throws Throwable {

		PageItems<?> pageItems = new PageItems<>(singletonList("1"), 31);

		Pagination pagination = new PaginationImpl(30, 2);

		Object result = execute(
			Paged.of("name"), singletonList(pagination), __ -> pageItems);

		assertThat(result, is(notNullValue()));
		assertThat(result, is(instanceOf(Page.class)));

		Page<?> page = (Page<?>)result;

		assertThat(page.getItems(), contains("1"));
		assertThat(page.getItemsPerPage(), is(30));
		assertThat(page.getLastPageNumber(), is(2));
		assertThat(page.getOperations(), is(empty()));
		assertThat(page.getPageNumber(), is(2));
		assertThat(page.getPathOptional(), is(emptyOptional()));
		assertThat(page.getResourceName(), is("name"));
		assertThat(page.getTotalCount(), is(31));
		assertThat(page.hasNext(), is(false));
		assertThat(page.hasPrevious(), is(true));
	}

}