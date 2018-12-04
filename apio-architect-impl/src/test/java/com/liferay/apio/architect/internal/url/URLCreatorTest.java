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
import static com.liferay.apio.architect.internal.url.URLCreator.createGenericParentResourceURL;
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
import com.liferay.apio.architect.resource.Resource.GenericParent;
import com.liferay.apio.architect.resource.Resource.Id;
import com.liferay.apio.architect.resource.Resource.Item;
import com.liferay.apio.architect.resource.Resource.Nested;
import com.liferay.apio.architect.resource.Resource.Paged;
import com.liferay.apio.architect.uri.Path;

import io.vavr.Tuple;
import io.vavr.Tuple2;

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

		GenericParent genericParent = GenericParent.of("parent", "name");

		List<Tuple2<Resource, String>> list = asList(
			Tuple.of(item, "retrieve"), Tuple.of(item, "remove"),
			Tuple.of(item, "replace"), Tuple.of(item, "merge"),
			Tuple.of(nested, "retrieve"), Tuple.of(nested, "create"),
			Tuple.of(nested, "peek"), Tuple.of(genericParent, "retrieve"),
			Tuple.of(genericParent, "create"));

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

		GenericParent genericParent = GenericParent.of(
			"parent", Id.of("id", "id"), "name");

		Optional<String> optional1 = createActionURL(
			_applicationURL, paged, "retrieve");

		assertThat(
			optional1, is(optionalWithValue(equalTo("www.liferay.com/paged"))));

		Optional<String> optional2 = createActionURL(
			_applicationURL, paged, "create");

		assertThat(
			optional2, is(optionalWithValue(equalTo("www.liferay.com/paged"))));

		Optional<String> optional3 = createActionURL(
			_applicationURL, paged, "subscribe");

		assertThat(
			optional3,
			is(optionalWithValue(equalTo("www.liferay.com/paged/subscribe"))));

		Optional<String> optional4 = createActionURL(
			_applicationURL, item, "retrieve");

		assertThat(
			optional4,
			is(optionalWithValue(equalTo("www.liferay.com/item/id"))));

		Optional<String> optional5 = createActionURL(
			_applicationURL, item, "remove");

		assertThat(
			optional5,
			is(optionalWithValue(equalTo("www.liferay.com/item/id"))));

		Optional<String> optional6 = createActionURL(
			_applicationURL, item, "replace");

		assertThat(
			optional6,
			is(optionalWithValue(equalTo("www.liferay.com/item/id"))));

		Optional<String> optional7 = createActionURL(
			_applicationURL, item, "merge");

		assertThat(
			optional7,
			is(optionalWithValue(equalTo("www.liferay.com/item/id/merge"))));

		Optional<String> optional8 = createActionURL(
			_applicationURL, nested, "retrieve");

		assertThat(
			optional8,
			is(optionalWithValue(equalTo("www.liferay.com/item/id/nested"))));

		Optional<String> optional = createActionURL(
			_applicationURL, nested, "create");

		assertThat(
			optional,
			is(optionalWithValue(equalTo("www.liferay.com/item/id/nested"))));

		Optional<String> optional9 = createActionURL(
			_applicationURL, nested, "peek");

		assertThat(
			optional9,
			is(
				optionalWithValue(
					equalTo("www.liferay.com/item/id/nested/peek"))));

		Optional<String> optional10 = createActionURL(
			_applicationURL, genericParent, "retrieve");

		assertThat(
			optional10,
			is(optionalWithValue(equalTo("www.liferay.com/name/parent/id"))));

		Optional<String> optional11 = createActionURL(
			_applicationURL, genericParent, "create");

		assertThat(
			optional11,
			is(optionalWithValue(equalTo("www.liferay.com/name/parent/id"))));

		Optional<String> optional12 = createActionURL(
			_applicationURL, genericParent, "fix");

		assertThat(
			optional12,
			is(
				optionalWithValue(
					equalTo("www.liferay.com/name/parent/id/fix"))));
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

		Page page = new PageImpl<>(Paged.of("name"), pageItems, pagination);

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
	public void testCreateGenericParentResourceURLReturnsEmptyIfMissingId() {
		GenericParent genericParent = GenericParent.of("parent", "name");

		Optional<String> optional = createGenericParentResourceURL(
			_applicationURL, genericParent);

		assertThat(optional, is(emptyOptional()));
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