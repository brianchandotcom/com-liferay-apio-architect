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
import static com.liferay.apio.architect.internal.url.URLCreator.createBinaryURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createCollectionPageURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createItemResourceURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createNestedResourceURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createOperationURL;
import static com.liferay.apio.architect.internal.url.URLCreator.createPagedResourceURL;
import static com.liferay.apio.architect.internal.url.URLCreator.getPath;

import static com.spotify.hamcrest.optional.OptionalMatchers.emptyOptional;
import static com.spotify.hamcrest.optional.OptionalMatchers.optionalWithValue;

import static java.util.Collections.emptyList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.internal.action.resource.Resource.Id;
import com.liferay.apio.architect.internal.action.resource.Resource.Item;
import com.liferay.apio.architect.internal.action.resource.Resource.Nested;
import com.liferay.apio.architect.internal.action.resource.Resource.Paged;
import com.liferay.apio.architect.internal.operation.CreateOperation;
import com.liferay.apio.architect.internal.operation.DeleteOperation;
import com.liferay.apio.architect.internal.operation.RetrieveOperation;
import com.liferay.apio.architect.internal.operation.UpdateOperation;
import com.liferay.apio.architect.internal.pagination.PageImpl;
import com.liferay.apio.architect.internal.pagination.PageType;
import com.liferay.apio.architect.operation.HTTPMethod;
import com.liferay.apio.architect.operation.Operation;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.uri.Path;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.NoSuchElementException;
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

		Page page = new PageImpl<>("", pageItems, pagination, null);

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
	public void testCreateNestedResourceURLReturnsURLIfPresentId() {
		Id id = Id.of(_path.getId(), _path.getId());

		Nested nested = Nested.of(Item.of("parent", id), "related");

		Optional<String> optional = createNestedResourceURL(
			_applicationURL, nested);

		String expected = "www.liferay.com/name/id/related";

		assertThat(optional, is(optionalWithValue(equalTo(expected))));
	}

	@Test(expected = NoSuchElementException.class)
	public void testCreateOperationWithoutURIReturnsOptionalEmpty() {
		CreateOperation createOperation = new CreateOperation(null, "");

		_validateOperationURL(createOperation, "");
	}

	@Test
	public void testCreateOperationWithURIHasValidURL() {
		CreateOperation createOperation = new CreateOperation(null, "", "name");

		_validateOperationURL(createOperation, "www.liferay.com/name");
	}

	@Test
	public void testCreateSingleURLReturnsEmptyIfMissingId() {
		Item item = Item.of(_path.getName());

		Optional<String> optional = createItemResourceURL(
			_applicationURL, item);

		assertThat(optional, is(emptyOptional()));
	}

	@Test
	public void testCreateSingleURLReturnsURLIfPresentId() {
		Id id = Id.of(_path.getId(), _path.getId());

		Item item = Item.of(_path.getName(), id);

		Optional<String> optional = createItemResourceURL(
			_applicationURL, item);

		String expected = "www.liferay.com/name/id";

		assertThat(optional, is(optionalWithValue(equalTo(expected))));
	}

	@Test(expected = NoSuchElementException.class)
	public void testDeleteOperationWithoutURIReturnsOptionalEmpty() {
		DeleteOperation deleteOperation = new DeleteOperation("");

		_validateOperationURL(deleteOperation, "");
	}

	@Test
	public void testDeleteOperationWithURIHasValidURL() {
		DeleteOperation deleteOperation = new DeleteOperation("", "name");

		_validateOperationURL(deleteOperation, "www.liferay.com/name");
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

	@Test(expected = NoSuchElementException.class)
	public void testNotSpecificOperationReturnsOptionalEmpty() {
		Operation operation = new Operation() {

			@Override
			public String getCustomRoute() {
				return null;
			}

			@Override
			public Optional<Form> getFormOptional() {
				return Optional.empty();
			}

			@Override
			public HTTPMethod getHttpMethod() {
				return null;
			}

			@Override
			public String getName() {
				return null;
			}

			@Override
			public Optional<String> getURIOptional() {
				return Optional.of("name");
			}

			@Override
			public boolean isCollection() {
				return false;
			}

			@Override
			public boolean isCustom() {
				return false;
			}

		};

		_validateOperationURL(operation, "");
	}

	@Test(expected = NoSuchElementException.class)
	public void testRetrieveOperationWithoutURIReturnsOptionalEmpty() {
		RetrieveOperation retrieveOperation = new RetrieveOperation("", false);

		_validateOperationURL(retrieveOperation, "");
	}

	@Test
	public void testRetrieveOperationWithURIHasValidURL() {
		RetrieveOperation retrieveOperation = new RetrieveOperation(
			"", false, "name");

		_validateOperationURL(retrieveOperation, "www.liferay.com/name");
	}

	@Test(expected = NoSuchElementException.class)
	public void testUpdateOperationWithoutURIReturnsOptionalEmpty() {
		UpdateOperation updateOperation = new UpdateOperation(null, "");

		_validateOperationURL(updateOperation, "");
	}

	@Test
	public void testUpdateOperationWithURIHasValidURL() {
		UpdateOperation updateOperation = new UpdateOperation(null, "", "name");

		_validateOperationURL(updateOperation, "www.liferay.com/name");
	}

	private void _validateOperationURL(
		Operation operation, String expectedURL) {

		Optional<String> optional = createOperationURL(
			_applicationURL, operation);

		String url = optional.orElseThrow(NoSuchElementException::new);

		assertThat(url, is(expectedURL));
	}

	private final ApplicationURL _applicationURL = () -> "www.liferay.com/";
	private final Path _path = new Path("name", "id");

}