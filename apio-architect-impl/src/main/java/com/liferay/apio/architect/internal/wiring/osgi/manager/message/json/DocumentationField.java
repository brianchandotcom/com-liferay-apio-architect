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

package com.liferay.apio.architect.internal.wiring.osgi.manager.message.json;

import java.util.Optional;

/**
 * Hols information about a field and its type. In the case of a linked
 * collection or model, it also contains information about the related type
 *
 * @author Víctor Galán
 * @review
 */
public class DocumentationField {

	public static DocumentationField of(String name, FieldType fieldType) {
		return of(name, fieldType, null);
	}

	public static DocumentationField of(
		String name, FieldType fieldType, String extraType) {

		return new DocumentationField(name, fieldType, extraType);
	}

	public Optional<String> getExtraType() {
		return Optional.ofNullable(_extraType);
	}

	public String getName() {
		return _name;
	}

	public FieldType getType() {
		return _type;
	}

	public enum FieldType {

		BOOLEAN, BOOLEAN_LIST, DATE, DATE_LIST, FILE, LINKED_MODEL,
		NESTED_MODEL, NESTED_MODEL_LIST, NUMBER, NUMBER_LIST,
		RELATED_COLLECTION, STRING, STRING_LIST

	}

	private DocumentationField(String name, FieldType type, String extraType) {
		_name = name;
		_type = type;
		_extraType = extraType;
	}

	private final String _extraType;
	private final String _name;
	private final FieldType _type;

}