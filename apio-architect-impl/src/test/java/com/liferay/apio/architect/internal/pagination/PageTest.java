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

package com.liferay.apio.architect.internal.pagination;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;

import static java.util.Collections.emptyList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;

import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.resource.Resource.Paged;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Alejandro Hernández
 */
public class PageTest {

	@Before
	public void setUp() {
		_pageItems = new PageItems<>(Collections.singleton("apio"), 10);

		Pagination pagination = new PaginationImpl(1, 4);

		_paged = Paged.of("name");

		_page = new PageImpl<>(_paged, _pageItems, pagination);
	}

	@Test
	public void testGetItemsPerPageReturnsItemsPerPage() {
		assertThat(_page.getItemsPerPage(), is(1));
	}

	@Test
	public void testGetItemsReturnsItems() {
		assertThat(_page.getItems(), contains("apio"));
	}

	@Test
	public void testGetLastPageNumberIsOneWithEmptyList() {
		Pagination pagination = new PaginationImpl(30, 1);

		PageItems<String> pageItems = new PageItems<>(emptyList(), 0);

		Page<String> page = new PageImpl<>(_paged, pageItems, pagination);

		assertThat(page.getLastPageNumber(), is(1));
	}

	@Test
	public void testGetLastPageNumberReturnsLastPageNumber() {
		assertThat(_page.getLastPageNumber(), is(10));
	}

	@Test
	public void testGetOperationsReturnsEmptyList() {
		assertThat(_page.getOperations(), is(empty()));
	}

	@Test
	public void testGetPageNumberReturnsPageNumber() {
		assertThat(_page.getPageNumber(), is(4));
	}

	@Test
	public void testGetPathReturnsOptionalEmpty() {
		assertThat(_page.getPathOptional(), is(emptyOptional()));
	}

	@Test
	public void testGetResourceNameReturnsResourceName() {
		assertThat(_page.getResourceName(), is("name"));
	}

	@Test
	public void testGetTotalCountReturnsTotalCount() {
		assertThat(_page.getTotalCount(), is(10));
	}

	@Test
	public void testHasNextReturnsFalseWhenIsLast() {
		Pagination pagination = new PaginationImpl(1, 10);

		Page<String> page = new PageImpl<>(_paged, _pageItems, pagination);

		assertThat(page.hasNext(), is(false));
	}

	@Test
	public void testHasNextReturnsTrueWhenThereIsNext() {
		assertThat(_page.hasNext(), is(true));
	}

	@Test
	public void testHasPreviousReturnsFalseWhenIsFirst() {
		Pagination pagination = new PaginationImpl(1, 1);

		Page<String> page = new PageImpl<>(_paged, _pageItems, pagination);

		assertThat(page.hasPrevious(), is(false));
	}

	@Test
	public void testHasPreviousReturnsTrueWhenThereIsPrevious() {
		assertThat(_page.hasPrevious(), is(true));
	}

	private Page<String> _page;
	private Paged _paged;
	private PageItems<String> _pageItems;

}