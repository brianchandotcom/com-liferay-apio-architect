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

package com.liferay.apio.architect.wiring.osgi.internal.manager;

import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.routes.CollectionRoutes;

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
		_names = null;
		_representors = null;
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

	public Optional<Map<String, Representor>> getRepresentorsOptional() {
		return Optional.ofNullable(_representors);
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
	 * Sets the collection routes
	 *
	 * @param  collectionRoutes the collection routes
	 * @review
	 */
	public void setCollectionRoutes(
		Map<String, CollectionRoutes> collectionRoutes) {

		_collectionRoutes = collectionRoutes;
	}

	/**
	 * Sets the identifier classes
	 *
	 * @param  identifierClasses the identifier classes
	 * @review
	 */
	public void setIdentifierClasses(
		Map<String, Class<Identifier>> identifierClasses) {

		_identifierClasses = identifierClasses;
	}

	/**
	 * Sets the resource names
	 *
	 * @param  names the resource names
	 * @review
	 */
	public void setNames(Map<String, String> names) {
		_names = names;
	}

	/**
	 * Sets the representors
	 *
	 * @param  representors the representors
	 * @review
	 */
	public void setRepresentors(Map<String, Representor> representors) {
		_representors = representors;
	}

	/**
	 * Sets the root resource names
	 *
	 * @param  rootResourceNames the root resource names
	 * @review
	 */
	public void setRootResourceNames(List<String> rootResourceNames) {
		_rootResourceNames = rootResourceNames;
	}

	private ManagerCache() {
	}

	private Map<String, CollectionRoutes> _collectionRoutes;
	private Map<String, Class<Identifier>> _identifierClasses;
	private Map<String, String> _names;
	private Map<String, Representor> _representors;
	private List<String> _rootResourceNames;

}