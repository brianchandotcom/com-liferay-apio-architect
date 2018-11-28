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

package com.liferay.apio.architect.resource;

import aQute.bnd.annotation.ProviderType;

import java.util.Optional;

/**
 * Instances of this class represent an API resource.
 *
 * <p>Only three implementations are allowed: {@link Item}, {@link Paged} and
 * {@link Nested}.
 *
 * <p>This class should never be directly instantiated. Use one of its
 * descendants
 * static methods ({@link Paged#of}, {@link Item#of} and {@link Nested#of})
 * instead.
 *
 * @author Alejandro Hernández
 * @see    Item
 * @see    Paged
 * @see    Nested
 * @review
 */
@ProviderType
public class Resource {

	/**
	 * The resource's name
	 *
	 * @review
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Instances of this class represent an item's ID.
	 *
	 * <p>This class should never be directly instantiated. Always use {@link
	 * #of} method to create a new instance.
	 *
	 * @review
	 */
	public static class Id {

		/**
		 * Creates a new {@link Id} with the provided {@code object}-{@code
		 * string} pair.
		 *
		 * @review
		 */
		public static Id of(Object objectVersion, String stringVersion) {
			return new Id(objectVersion, stringVersion);
		}

		/**
		 * The {@link Id} as an object instance. The result of this method can
		 * be any class supported by a {@link
		 * com.liferay.apio.architect.uri.mapper.PathIdentifierMapper}.
		 *
		 * @review
		 */
		public Object asObject() {
			return _objectVersion;
		}

		/**
		 * The {@link Id} as an string instance.
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
	 * <p>This class should never be directly instantiated. Always use {@link
	 * #of}
	 * method to create a new instance.
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
		 * Id}.
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

			if (obj instanceof Item &&
				getName().equals(((Item)obj).getName())) {

				return true;
			}

			return false;
		}

		/**
		 * The resource's Id, if present; {@code Optional#empty} otherwise. This
		 * component is not taken into account when performing an {@link
		 * #equals)} check.
		 *
		 * @review
		 */
		public Optional<Id> getIdOptional() {
			return Optional.ofNullable(_id);
		}

		@Override
		public int hashCode() {
			int h = 5381;

			h += (h << 5) + getName().hashCode();

			return h;
		}

		@Override
		public String toString() {
			if (_id != null) {
				return "Item{name=" + getName() + ", id=" + _id + "}";
			}

			return "Item{name=" + getName() + "}";
		}

		/**
		 * Copies the current {@link Item} by setting a value for the {@link
		 * Item#getIdOptional() ID} attribute. A shallow reference equality
		 * check is used to prevent copying of the same value by returning
		 * {@code this}.
		 *
		 * @param  id the new ID
		 * @return A modified copy of {@code this} object
		 * @review
		 */
		public Item withId(Id id) {
			if ((_id != null) && _id.equals(id)) {
				return this;
			}

			return new Item(getName(), id);
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
	 * <p>This class should never be directly instantiated. Always use {@link
	 * #of} method to create a new instance.
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
		public static Nested of(Item parentItem, String name) {
			return new Nested(parentItem, name);
		}

		public Nested(Item parentItem, String name) {
			super(name);

			_parentItem = parentItem;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			if ((obj instanceof Nested) &&
				getName().equals(((Nested)obj).getName()) &&
				_parentItem.equals(((Nested)obj)._parentItem)) {

				return true;
			}

			return false;
		}

		/**
		 * The resource's parent {@link Item}
		 *
		 * @review
		 */
		public Item getParentItem() {
			return _parentItem;
		}

		@Override
		public int hashCode() {
			int h = 5381;

			h += (h << 5) + getName().hashCode();
			h += (h << 5) + _parentItem.hashCode();

			return h;
		}

		@Override
		public String toString() {
			return "Nested{name=" + getName() + ", parent=" + _parentItem + "}";
		}

		/**
		 * Copies the current {@link Nested} by setting a value for the parent's
		 * {@link Item#getIdOptional() ID} attribute. A shallow reference
		 * equality check is used to prevent copying of the same value by
		 * returning {@code this}.
		 *
		 * @param  id the new ID
		 * @return A modified copy of {@code this} object
		 * @review
		 */
		public Nested withParentId(Id id) {
			if ((_parentItem._id != null) && _parentItem._id.equals(id)) {
				return this;
			}

			return new Nested(_parentItem.withId(id), getName());
		}

		private final Item _parentItem;

	}

	/**
	 * Instances of this class represent a paged resource.
	 *
	 * <p>
	 * This class should never be directly instantiated. Always use {@link #of}
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

			if (obj instanceof Paged &&
				getName().equals(((Paged)obj).getName())) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			int h = 5381;

			h += (h << 5) + getName().hashCode();

			return h;
		}

		@Override
		public String toString() {
			return "Paged{name=" + getName() + "}";
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