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

import static java.lang.String.join;

import com.liferay.apio.architect.internal.jaxrs.resource.NestedResource;
import com.liferay.apio.architect.internal.jaxrs.resource.RootResource;
import com.liferay.apio.architect.internal.operation.BatchCreateOperation;
import com.liferay.apio.architect.internal.operation.CreateOperation;
import com.liferay.apio.architect.internal.operation.DeleteOperation;
import com.liferay.apio.architect.internal.operation.RetrieveOperation;
import com.liferay.apio.architect.internal.operation.UpdateOperation;
import com.liferay.apio.architect.internal.pagination.PageType;
import com.liferay.apio.architect.operation.Operation;
import com.liferay.apio.architect.pagination.Page;
import com.liferay.apio.architect.uri.Path;

import java.net.URI;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.ws.rs.core.UriBuilder;

/**
 * Manages the creation of URLs and has all their necessary information.
 *
 * <p>
 * This class shouldn't be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 */
public final class URLCreator {

	/**
	 * Returns the absolute version of an application's relative URL.
	 *
	 * @param  applicationURL the application URL
	 * @param  relativeURL the relative URL
	 * @return the absolute URL
	 */
	public static String createAbsoluteURL(
		ApplicationURL applicationURL, String relativeURL) {

		return _buildURL(applicationURL.get(), relativeURL);
	}

	/**
	 * Returns the absolute version of a relative URL.
	 *
	 * @param  serverURL the server URL
	 * @param  relativeURL the relative URL
	 * @return the absolute URL
	 */
	public static String createAbsoluteURL(
		ServerURL serverURL, String relativeURL) {

		return _buildURL(serverURL.get(), relativeURL);
	}

	/**
	 * Returns the URL for a binary resource.
	 *
	 * @param  applicationURL the application URL
	 * @param  binaryId the binary resource's ID
	 * @param  path the resource's {@code com.liferay.apio.architect.uri.Path}
	 * @return the URL for a binary resource
	 */
	public static String createBinaryURL(
		ApplicationURL applicationURL, String binaryId, Path path) {

		URI uri = UriBuilder.fromMethod(
			RootResource.class, "nestedResource"
		).resolveTemplate(
			"param", path.getName()
		).path(
			NestedResource.class, "nestedResource"
		).resolveTemplate(
			"param", path.getId()
		).path(
			NestedResource.class, "nestedResource"
		).resolveTemplate(
			"param", binaryId
		).build();

		return createAbsoluteURL(applicationURL, uri.toString());
	}

	/**
	 * Returns the URL for a collection page.
	 *
	 * @param  collectionURL the collection URL
	 * @param  page the page
	 * @return the collection page URL
	 */
	public static String createCollectionPageURL(
		String collectionURL, Page page, PageType pageType) {

		return UriBuilder.fromUri(
			collectionURL
		).queryParam(
			"page", pageType.getPageNumber(page)
		).queryParam(
			"per_page", page.getItemsPerPage()
		).build(
		).toString();
	}

	/**
	 * Returns the URL for a collection.
	 *
	 * @param  applicationURL the application URL
	 * @param  name the resource's name
	 * @return the collection URL
	 */
	public static String createCollectionURL(
		ApplicationURL applicationURL, String name) {

		URI uri = UriBuilder.fromMethod(
			RootResource.class, "nestedResource"
		).resolveTemplate(
			"param", name
		).build();

		return createAbsoluteURL(applicationURL, uri.toString());
	}

	/**
	 * Returns the URL for a nested collection.
	 *
	 * @param  applicationURL the application URL
	 * @param  path the single resource's {@link Path}
	 * @param  name the nested resource's name
	 * @return the collection URL
	 */
	public static String createNestedCollectionURL(
		ApplicationURL applicationURL, Path path, String name) {

		URI uri = UriBuilder.fromMethod(
			RootResource.class, "nestedResource"
		).resolveTemplate(
			"param", path.getName()
		).path(
			NestedResource.class, "nestedResource"
		).resolveTemplate(
			"param", path.getId()
		).path(
			NestedResource.class, "nestedResource"
		).resolveTemplate(
			"param", name
		).build();

		return createAbsoluteURL(applicationURL, uri.toString());
	}

	/**
	 * Returns an operation's URL.
	 *
	 * @param  applicationURL the application URL
	 * @param  operation the operation
	 * @return the operation's URL
	 */
	public static Optional<String> createOperationURL(
		ApplicationURL applicationURL, Operation operation) {

		Optional<String> optional = operation.getURIOptional();

		return optional.map(
			uri -> {
				if (operation instanceof BatchCreateOperation) {
					return uri;
				}

				if (operation.isCustom()) {
					return uri + "/" + operation.getCustomRoute();
				}

				if (operation instanceof CreateOperation) {
					return uri;
				}

				if (operation instanceof DeleteOperation) {
					return uri;
				}

				if (operation instanceof RetrieveOperation) {
					return uri;
				}

				if (operation instanceof UpdateOperation) {
					return uri;
				}

				return null;
			}
		).map(
			uri -> createAbsoluteURL(applicationURL, uri)
		);
	}

	/**
	 * Returns the URL of a model's resource.
	 *
	 * @param  applicationURL the application URL
	 * @param  path the resource's {@link Path}
	 * @return the URL for the {@link
	 *         com.liferay.apio.architect.resource.CollectionResource}
	 */
	public static String createSingleURL(
		ApplicationURL applicationURL, Path path) {

		URI uri = UriBuilder.fromMethod(
			RootResource.class, "nestedResource"
		).resolveTemplate(
			"param", path.getName()
		).path(
			NestedResource.class, "nestedResource"
		).resolveTemplate(
			"param", path.getId()
		).build();

		return createAbsoluteURL(applicationURL, uri.toString());
	}

	/**
	 * Returns a {@link Path} from the URL if it's a valid URL for a resource
	 * with the provided name. Returns {@link Optional#empty()} otherwise.
	 *
	 * @param  url the resource URL to parse
	 * @param  name the resource's name
	 * @return a {@link Path} if the URL is valid; {@link Optional#empty()}
	 *         otherwise
	 * @review
	 */
	public static Optional<Path> getPath(String url, String name) {
		return Optional.of(
			url.lastIndexOf(name)
		).filter(
			index -> index != -1
		).map(
			url::substring
		).map(
			uri -> uri.split("/")
		).filter(
			components -> components.length == 2
		).map(
			components -> new Path(components[0], components[1])
		).filter(
			_isNotEmpty(Path::getName)
		).filter(
			_isNotEmpty(Path::getId)
		);
	}

	private static String _buildURL(String baseUrl, String relativeURL) {
		if ((relativeURL == null) || relativeURL.isEmpty()) {
			return null;
		}

		if (baseUrl.endsWith("/")) {
			baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
		}

		if (relativeURL.startsWith("/")) {
			relativeURL = relativeURL.substring(1);
		}

		return join("/", baseUrl, relativeURL);
	}

	private static Predicate<Path> _isNotEmpty(
		Function<Path, String> function) {

		return path -> function.andThen(
			s -> s != null && !s.isEmpty()
		).apply(
			path
		);
	}

	private URLCreator() {
		throw new UnsupportedOperationException();
	}

}