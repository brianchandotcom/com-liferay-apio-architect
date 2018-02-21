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

package com.liferay.apio.architect.wiring.osgi.internal.manager.cache;

import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class acts as a central cache for most of the managers.
 *
 * <p>
 * There should only be one instance of this class, accessible through {@link
 * #INSTANCE}.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public class ManagerCache {

	/**
	 * The {@link ManagerCache} instance.
	 *
	 * @review
	 */
	public static final ManagerCache INSTANCE = new ManagerCache();

	/**
	 * Clears the cache.
	 *
	 * @review
	 */
	public void clear() {
		_collectionRoutes = null;
		_identifierClasses = null;
		_itemRoutes = null;
		_names = null;
		_nestedCollectionRoutes = null;
		_representors = null;
		_reusableNestedCollectionRoutes = null;
		_rootResourceNames = null;
	}

	/**
	 * Returns the map containing the collection routes if they have been set.
	 * Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the map containing the collection routes if they have been set;
	 *         returns {@code Optional#empty()} otherwise
	 * @review
	 */
	public Optional<Map<String, CollectionRoutes>>
		getCollectionRoutesOptional() {

		return Optional.ofNullable(_collectionRoutes);
	}

	/**
	 * Returns the map containing the classes for the different resource names
	 * if they have been set. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the map containing the classes for the different resource names
	 *         if they have been set; returns {@code Optional#empty()} otherwise
	 * @review
	 */
	public Optional<Map<String, Class<Identifier>>>
		getIdentifierClassesOptional() {

		return Optional.ofNullable(_identifierClasses);
	}

	/**
	 * Returns the map containing the item routes if they have been set. Returns
	 * {@code Optional#empty()} otherwise.
	 *
	 * @return the map containing the item routes if they have been set; returns
	 *         {@code Optional#empty()} otherwise
	 * @review
	 */
	public Optional<Map<String, ItemRoutes>> getItemRoutesOptional() {
		return Optional.ofNullable(_itemRoutes);
	}

	/**
	 * Returns the map containing the names for the different resource
	 * identifier classes if they have been set. Returns {@code
	 * Optional#empty()} otherwise.
	 *
	 * @return the map containing the names for the different resource
	 *         identifier classes if they have been set; returns {@code
	 *         Optional#empty()} otherwise
	 * @review
	 */
	public Optional<Map<String, String>> getNamesOptional() {
		return Optional.ofNullable(_names);
	}

	/**
	 * Returns the map containing the nested collection routes if they have been
	 * set. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the map containing the nested collection routes if they have been
	 *         set; returns {@code Optional#empty()} otherwise
	 * @review
	 */
	public Optional<Map<String, NestedCollectionRoutes>>
		getNestedCollectionRoutesOptional() {

		return Optional.ofNullable(_nestedCollectionRoutes);
	}

	/**
	 * Returns the map containing the representors for the different resource
	 * names if they have been set. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the map containing the representors for the different resource
	 *         names if they have been set; returns {@code Optional#empty()}
	 *         otherwise
	 * @review
	 */
	public Optional<Map<String, Representor>> getRepresentorsOptional() {
		return Optional.ofNullable(_representors);
	}

	/**
	 * Returns the map containing the reusable nested collection routes if they
	 * have been set. Returns {@code Optional#empty()} otherwise.
	 *
	 * @return the map containing the reusable nested collection routes if they
	 *         have been set; returns {@code Optional#empty()} otherwise
	 * @review
	 */
	public Optional<Map<String, NestedCollectionRoutes>>
		getReusableNestedCollectionRoutesOptional() {

		return Optional.ofNullable(_reusableNestedCollectionRoutes);
	}

	/**
	 * Returns the list of root resource names if they have been set. Returns
	 * {@code Optional#empty()} otherwise.
	 *
	 * @return the list of root resource names if they have been set; {@code
	 *         Optional#empty()} otherwise
	 * @review
	 */
	public Optional<List<String>> getRootResourceNamesOptional() {
		return Optional.ofNullable(_rootResourceNames);
	}

	/**
	 * Returns {@code true} if the collection routes have been set in the cache.
	 *
	 * @return {@code true} if the collection routes have been set
	 * @review
	 */
	public boolean hasCollectionRoutes() {
		if (_collectionRoutes != null) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if the identifier classes have been set in the
	 * cache.
	 *
	 * @return {@code true} if the identifier classes have been set
	 * @review
	 */
	public boolean hasIdentifierClasses() {
		if (_identifierClasses != null) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if the item routes have been set in the cache.
	 *
	 * @return {@code true} if the item routes have been set
	 * @review
	 */
	public boolean hasItemRoutes() {
		if (_itemRoutes != null) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if the resource names have been set in the cache.
	 *
	 * @return {@code true} if the resource names have been set
	 * @review
	 */
	public boolean hasNames() {
		if (_names != null) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if the nested collection routes have been set in the
	 * cache.
	 *
	 * @return {@code true} if the nested collection routes have been set
	 * @review
	 */
	public boolean hasNestedCollectionRoutes() {
		if (_nestedCollectionRoutes != null) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if the representors have been set in the cache.
	 *
	 * @return {@code true} if the representors have been set
	 * @review
	 */
	public boolean hasRepresentors() {
		if (_representors != null) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if the reusable nested collection routes have been
	 * set in the cache.
	 *
	 * @return {@code true} if the reusable nested collection routes have been
	 *         set
	 * @review
	 */
	public boolean hasReusableNestedCollectionRoutes() {
		if (_reusableNestedCollectionRoutes != null) {
			return true;
		}

		return false;
	}

	/**
	 * Returns {@code true} if the root resource names have been set in the
	 * cache.
	 *
	 * @return {@code true} if the root resource names have been set
	 * @review
	 */
	public boolean hasRootResourceNames() {
		if (_rootResourceNames != null) {
			return true;
		}

		return false;
	}

	/**
	 * Adds a collection routes
	 *
	 * @param  key the key
	 * @param  collectionRoutes the collection routes
	 * @review
	 */
	public void putCollectionRoutes(
		String key, CollectionRoutes collectionRoutes) {

		if (_collectionRoutes == null) {
			_collectionRoutes = new HashMap<>();
		}

		_collectionRoutes.put(key, collectionRoutes);
	}

	/**
	 * Adds an identifier class
	 *
	 * @param  key the key
	 * @param  identifierClass the identifier class
	 * @review
	 */
	public void putIdentifierClass(
		String key, Class<Identifier> identifierClass) {

		if (_identifierClasses == null) {
			_identifierClasses = new HashMap<>();
		}

		_identifierClasses.put(key, identifierClass);
	}

	/**
	 * Adds an item routes
	 *
	 * @param  key the key
	 * @param  itemRoutes the item routes
	 * @review
	 */
	public void putItemRoutes(String key, ItemRoutes itemRoutes) {
		if (_itemRoutes == null) {
			_itemRoutes = new HashMap<>();
		}

		_itemRoutes.put(key, itemRoutes);
	}

	/**
	 * Adds a resource name
	 *
	 * @param  key the key
	 * @param  name the resource name
	 * @review
	 */
	public void putName(String key, String name) {
		if (_names == null) {
			_names = new HashMap<>();
		}

		_names.put(key, name);
	}

	/**
	 * Adds a nested collection routes
	 *
	 * @param  key the key
	 * @param  nestedCollectionRoutes the nested collection routes
	 * @review
	 */
	public void putNestedCollectionRoutes(
		String key, NestedCollectionRoutes nestedCollectionRoutes) {

		if (_nestedCollectionRoutes == null) {
			_nestedCollectionRoutes = new HashMap<>();
		}

		_nestedCollectionRoutes.put(key, nestedCollectionRoutes);
	}

	/**
	 * Adds a representors
	 *
	 * @param  key the key
	 * @param  representor the representor
	 * @review
	 */
	public void putRepresentor(String key, Representor representor) {
		if (_representors == null) {
			_representors = new HashMap<>();
		}

		_representors.put(key, representor);
	}

	/**
	 * Adds a reusable nested collection routes
	 *
	 * @param  key the key
	 * @param  reusableNestedCollectionRoutes the reusable nested collection
	 *         routes
	 * @review
	 */
	public void putReusableNestedCollectionRoutes(
		String key, NestedCollectionRoutes reusableNestedCollectionRoutes) {

		if (_reusableNestedCollectionRoutes == null) {
			_reusableNestedCollectionRoutes = new HashMap<>();
		}

		_reusableNestedCollectionRoutes.put(
			key, reusableNestedCollectionRoutes);
	}

	/**
	 * Adds a root resource name
	 *
	 * @param  rootResourceName the root resource name
	 * @review
	 */
	public void putRootResourceNames(String rootResourceName) {
		if (_rootResourceNames == null) {
			_rootResourceNames = new ArrayList<>();
		}

		_rootResourceNames.add(rootResourceName);
	}

	private ManagerCache() {
	}

	private Map<String, CollectionRoutes> _collectionRoutes;
	private Map<String, Class<Identifier>> _identifierClasses;
	private Map<String, ItemRoutes> _itemRoutes;
	private Map<String, String> _names;
	private Map<String, NestedCollectionRoutes> _nestedCollectionRoutes;
	private Map<String, Representor> _representors;
	private Map<String, NestedCollectionRoutes> _reusableNestedCollectionRoutes;
	private List<String> _rootResourceNames;

}