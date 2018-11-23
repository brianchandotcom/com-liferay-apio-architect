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

package com.liferay.apio.architect.internal.action.resource;

import java.util.Optional;

/**
 * Instances of this class represent an API resource name.
 *
 * <p>
 * Only three implementations are allowed: {@link Item}, {@link Paged} and
 * {@link Nested}.
 * </p>
 *
 * <p>
 * This class should never be instantiated.
 * </p>
 *
 * @author Alejandro Hernández
 * @see    Item
 * @see    Paged
 * @see    Nested
 * @review
 */
public class Resource {

	/**
	 * The resource's name
	 *
	 * @review
	 */
	public String name() {
		return _name;
	}

	/**
	 * Instances of this class represent an item's ID.
	 *
	 * <p>
	 * This class should never be instantiated. Always use {@link #of(Object,
	 * String)} method to create a new instance.
	 * </p>
	 *
	 * @review
	 */
	public static class Id {

		/**
		 * Creates a new {@link ID} with the provided {@code object}-{@code
		 * string} pair.
		 *
		 * @review
		 */
		public static Id of(Object objectVersion, String stringVersion) {
			return new Id(objectVersion, stringVersion);
		}

		/**
		 * The ID as an object instance. This can be any class supported by a
		 * {@link com.liferay.apio.architect.uri.mapper.PathIdentifierMapper}.
		 *
		 * @review
		 */
		public Object asObject() {
			return _objectVersion;
		}

		/**
		 * The ID as an string instance.
		 *
		 * @review
		 */
		public String asString() {
			return _stringVersion;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj instanceof Id &&
				_objectVersion.equals(((Id)obj)._objectVersion) &&
				_stringVersion.equals(((Id)obj)._stringVersion)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int h = 5381;

			h += (h << 5) + _objectVersion.hashCode();
			h += (h << 5) + _stringVersion.hashCode();

			return h;
		}

		@Override
		public String toString() {
			return "Id{" + _stringVersion + "}";
		}

		private Id(Object objectVersion, String stringVersion) {
			_objectVersion = objectVersion;
			_stringVersion = stringVersion;
		}

		private final Object _objectVersion;
		private final String _stringVersion;

	}

	/**
	 * Instances of this class represent an item resource.
	 *
	 * <p>
	 * This class should never be instantiated. Always use {@link #of(String)}
	 * method to create a new instance.
	 * </p>
	 *
	 * @review
	 */
	public static class Item extends Resource {

		/**
		 * Creates a new {@link Item} with the provided {@code name}.
		 *
		 * @review
		 */
		public static Item of(String name) {
			return new Item(name, null);
		}

		/**
		 * Creates a new {@link Item} with the provided {@code name} and {@code
		 * ID}.
		 *
		 * @review
		 */
		public static Item of(String name, Id id) {
			return new Item(name, id);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj instanceof Item && name().equals(((Item)obj).name())) {
				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int h = 5381;

			h += (h << 5) + name().hashCode();

			return h;
		}

		/**
		 * The resource's ID. This component is not taken into account when
		 * performing an {@link #equals(Object)} check.
		 *
		 * @review
		 */
		public Optional<Id> id() {
			return Optional.ofNullable(_id);
		}

		@Override
		public String toString() {
			if (_id != null) {
				return "Item{name=" + name() + ", id=" + _id + "}";
			}

			return "Item{name=" + name() + "}";
		}

		private Item(String name, Id id) {
			super(name);

			_id = id;
		}

		private final Id _id;

	}

	/**
	 * Instances of this class represent a nested resource.
	 *
	 * <p>
	 * This class should never be instantiated. Always use {@link #of(Item,
	 * String)} method to create a new instance.
	 * </p>
	 *
	 * @review
	 */
	public static class Nested extends Resource {

		/**
		 * Creates a new {@link Nested} with the provided {@link Item parent}
		 * and {@code name} information.
		 *
		 * @review
		 */
		public static Nested of(Item parent, String name) {
			return new Nested(parent, name);
		}

		public Nested(Item parent, String name) {
			super(name);

			_parent = parent;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if ((obj instanceof Nested) &&
				name().equals(((Nested)obj).name()) &&
				_parent.equals(((Nested)obj)._parent)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int h = 5381;

			h += (h << 5) + name().hashCode();
			h += (h << 5) + _parent.hashCode();

			return h;
		}

		/**
		 * The resource's parent
		 *
		 * @review
		 */
		public Item parent() {
			return _parent;
		}

		@Override
		public String toString() {
			return "Nested{name=" + name() + ", parent=" + _parent + "}";
		}

		private final Item _parent;

	}

	/**
	 * Instances of this class represent a paged resource.
	 *
	 * <p>
	 * This class should never be instantiated. Always use {@link #of(String)}
	 * method to create a new instance.
	 * </p>
	 *
	 * @review
	 */
	public static class Paged extends Resource {

		/**
		 * Creates a new {@link Paged} with the provided {@code name}.
		 *
		 * @review
		 */
		public static Paged of(String name) {
			return new Paged(name);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if (obj instanceof Paged && name().equals(((Paged)obj).name())) {
				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int h = 5381;

			h += (h << 5) + name().hashCode();

			return h;
		}

		@Override
		public String toString() {
			return "Paged{name=" + name() + "}";
		}

		private Paged(String name) {
			super(name);
		}

	}

	private Resource(String name) {
		_name = name;
	}

	private final String _name;

}