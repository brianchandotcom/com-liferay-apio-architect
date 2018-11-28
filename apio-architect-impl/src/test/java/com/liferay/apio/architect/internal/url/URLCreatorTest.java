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

package com.liferay.apio.architect.internal.url;

import static com.liferay.apio.architect.internal.url.URLCreator.createAbsoluteURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createActionURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createBinaryURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createCollectionPageURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createItemResourceURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createNestedResourceURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createPagedResourceURL;
import static com.liferay.apio.architect.internal.url.URLCreator.getPath;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import com.liferay.apio.architect.internal.pagination.PageImpl;
import com.liferay.apio.architect.internal.pagination.PageType;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.resource.Resource;
import com.liferay.apio.architect.resource.Resource.Id;
import com.liferay.apio.architect.resource.Resource.Item;
import com.liferay.apio.architect.resource.Resource.Nested;
import com.liferay.apio.architect.resource.Resource.Paged;
import com.liferay.apio.architect.uri.Path;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Hernández
 */
public class URLCreatorTest {

	@Test(expected = UnsupportedOperationException.class)
	public void testConstructorThrowsException() throws Throwable {
		Constructor<?> constructor =
			URLCreator.class.getDeclaredConstructors()[0];

		constructor.setAccessible(true);

		try {
			constructor.newInstance();
		}
		catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
	}

	@Test
	public void testCreateAbsoluteURL() {
		String url = createAbsoluteURL(_applicationURL, "/relative/url");

		assertThat(url, is("www.liferay.com/relative/url"));
	}

	@Test
	public void testCreateAbsoluteURLWithNullReturnsNull() {
		String url = createAbsoluteURL(_applicationURL, null);

		assertThat(url, is(nullValue()));
	}

	@Test
	public void testCreateActionURLReturnsEmptyIfMissingId() {
		Item item = Item.of("item");

		Nested nested = Nested.of(item, "nested");

		List<Tuple2<Resource, String>> list = asList(
			Tuple.of(item, "retrieve"), Tuple.of(item, "remove"),
			Tuple.of(item, "replace"), Tuple.of(item, "merge"),
			Tuple.of(nested, "retrieve"), Tuple.of(nested, "create"),
			Tuple.of(nested, "peek"));

		list.forEach(
			tuple -> {
				Optional<String> optional = createActionURL(
					_applicationURL, tuple._1, tuple._2);

				assertThat(optional, is(emptyOptional()));
			});
	}

	@Test
	public void testCreateActionURLReturnsValidURLIfIdPresent() {
		Paged paged = Paged.of("paged");

		Item item = Item.of("item", Id.of("id", "id"));

		Nested nested = Nested.of(item, "nested");

		Map<Tuple2<Resource, String>, String> map = HashMap.of(
			Tuple.of(paged, "retrieve"), "www.liferay.com/paged",
			Tuple.of(paged, "create"), "www.liferay.com/paged",
			Tuple.of(paged, "subscribe"), "www.liferay.com/paged/subscribe",
			Tuple.of(item, "retrieve"), "www.liferay.com/item/id",
			Tuple.of(item, "remove"), "www.liferay.com/item/id",
			Tuple.of(item, "replace"), "www.liferay.com/item/id",
			Tuple.of(item, "merge"), "www.liferay.com/item/id/merge",
			Tuple.of(nested, "retrieve"), "www.liferay.com/item/id/nested",
			Tuple.of(nested, "create"), "www.liferay.com/item/id/nested",
			Tuple.of(nested, "peek"), "www.liferay.com/item/id/nested/peek");

		map.forEach(
			(tuple, expected) -> {
				Optional<String> optional = createActionURL(
					_applicationURL, tuple._1, tuple._2);

				assertThat(optional, is(optionalWithValue(equalTo(expected))));
			});
	}

	@Test
	public void testCreateBinaryURL() {
		String binaryId = "binary";

		String url = createBinaryURL(_applicationURL, binaryId, _path);

		assertThat(url, is("www.liferay.com/name/id/binary"));
	}

	@Test
	public void testCreateCollectionPageURL() {
		Pagination pagination = Mockito.mock(Pagination.class);

		Mockito.when(
			pagination.getItemsPerPage()
		).thenReturn(
			30
		);

		Mockito.when(
			pagination.getPageNumber()
		).thenReturn(
			1
		);

		PageItems<String> pageItems = new PageItems<>(emptyList(), 0);

		Page page = new PageImpl<>("", pageItems, pagination);

		String firstPageURL = createCollectionPageURL(
			"www.liferay.com", page, PageType.FIRST);

		assertThat(firstPageURL, is("www.liferay.com?page=1&per_page=30"));
	}

	@Test
	public void testCreateCollectionURL() {
		Paged paged = Paged.of("resource");

		String url = createPagedResourceURL(_applicationURL, paged);

		assertThat(url, is("www.liferay.com/resource"));
	}

	@Test
	public void testCreateNestedResourceURLReturnsEmptyIfMissingId() {
		Nested nested = Nested.of(Item.of("parent"), "related");

		Optional<String> optional = createNestedResourceURL(
			_applicationURL, nested);

		assertThat(optional, is(emptyOptional()));
	}

	@Test
	public void testCreateSingleURL() {
		Item item = Item.of(_path.getName());

		Optional<String> optional = createItemResourceURL(
			_applicationURL, item);

		assertThat(optional, is(emptyOptional()));
	}

	@Test
	public void testExtractsPathFromSingleURL() {
		Optional<Path> optional = getPath("www.liferay.com/name/id", "name");

		if (!optional.isPresent()) {
			throw new AssertionError("Optional should not be empty");
		}

		Path path = optional.get();

		assertThat(path.getName(), is("name"));
		assertThat(path.getId(), is("id"));
	}

	private final ApplicationURL _applicationURL = () -> "www.liferay.com/";
	private final Path _path = new Path("name", "id");

}