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

package com.liferay.apio.architect.related;

import com.liferay.apio.architect.identifier.Identifier;

import java.util.function.Function;

/**
 * Represents the relation between a resource and a collection.
 *
 * @author Alejandro Hernández
 * @param  <T> the model's type
 * @param  <S> the type of the related collection item's identifier (e.g.,
 *         {@code Long}, {@code String}, etc.)
 * @param  <U> the type of the related collection's identifier. It must be a
 *         subclass of {@code Identifier<S>}.
 * @review
 */
public class RelatedCollection<T, S extends Identifier, U> {

	public RelatedCollection(
		String key, Class<S> itemIdentifierClass,
		Class<? extends Identifier<U>> collectionIdentifierClass,
		Function<T, U> identifierFunction) {

		_key = key;
		_itemIdentifierClass = itemIdentifierClass;
		_collectionIdentifierClass = collectionIdentifierClass;
		_identifierFunction = identifierFunction;
	}

	/**
	 * Returns the class of the collection items' identifier.
	 *
	 * @return the class of the collection items' identifier
	 */
	public Class<? extends Identifier<U>> getCollectionIdentifierClass() {
		return _collectionIdentifierClass;
	}

	/**
	 * Returns the function that calculates the related collection's identifier.
	 *
	 * @return the function that calculates the related collection's identifier
	 */
	public Function<T, U> getIdentifierFunction() {
		return _identifierFunction;
	}

	/**
	 * Returns the class of the collection items' identifier.
	 *
	 * @return the class of the collection items' identifier
	 */
	public Class<S> getItemIdentifierClass() {
		return _itemIdentifierClass;
	}

	/**
	 * Returns the relation's key.
	 *
	 * @return the relation's key
	 */
	public String getKey() {
		return _key;
	}

	private final Class<? extends Identifier<U>> _collectionIdentifierClass;
	private final Function<T, U> _identifierFunction;
	private final Class<S> _itemIdentifierClass;
	private final String _key;

}