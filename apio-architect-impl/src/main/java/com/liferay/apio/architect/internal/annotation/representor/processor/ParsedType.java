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

package com.liferay.apio.architect.internal.annotation.representor.processor;

import com.liferay.apio.architect.annotation.Vocabulary.BidirectionalModel;
import com.liferay.apio.architect.annotation.Vocabulary.LinkedModel;
import com.liferay.apio.architect.annotation.Vocabulary.RelatedCollection;
import com.liferay.apio.architect.annotation.Vocabulary.RelativeURL;
import com.liferay.apio.architect.annotation.Vocabulary.Type;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds information about a class annotated with {@link Type}.
 *
 * @author Víctor Galán
 * @review
 */
public class ParsedType {

	/**
	 * Returns the list of bidirectionalfield data.
	 *
	 * @return the list of bidirectionalfield data
	 * @review
	 */
	public List<FieldData<BidirectionalModel>> getBidirectionalFieldDataList() {
		return _bidirectionalFieldData;
	}

	/**
	 * Returns the list of field data.
	 *
	 * @return the list of field data
	 * @review
	 */
	public List<FieldData> getFieldDataList() {
		return _fieldDataList;
	}

	/**
	 * The method used to obtain the ID of the type.
	 *
	 * @review
	 */
	public Method getIdMethod() {
		return _method;
	}

	/**
	 * Returns the list of linkedModelField data.
	 *
	 * @return the list of linkedModelField data
	 * @review
	 */
	public List<FieldData<LinkedModel>> getLinkedModelFieldDataList() {
		return _linkedModelFieldData;
	}

	/**
	 * Returns the list of listField data.
	 *
	 * @return the list of listField data
	 * @review
	 */
	public List<FieldData<Class<?>>> getListFieldDataList() {
		return _listFieldData;
	}

	/**
	 * Returns the list of listParsedTypes data.
	 *
	 * @return the list of listParsedTypes data
	 * @review
	 */
	public List<FieldData<ParsedType>> getListParsedTypes() {
		return _listParsedTypes;
	}

	/**
	 * Returns the list of nestedList parsed type data.
	 *
	 * @return the list of nestedList parsed type data
	 * @review
	 */
	public List<FieldData<ParsedType>> getParsedTypes() {
		return _parsedTypes;
	}

	/**
	 * Returns the list of relatedCollectionField data.
	 *
	 * @return the list of relatedCollectionField data
	 * @review
	 */
	public List<FieldData<RelatedCollection>>
		getRelatedCollectionFieldDataList() {

		return _relatedCollectionFieldData;
	}

	/**
	 * Returns the list of relativeURLData data.
	 *
	 * @return the list of relativeURLData data
	 * @review
	 */
	public List<FieldData<RelativeURL>> getRelativeURLFieldDataList() {
		return _relativeURLFieldData;
	}

	/**
	 * Returns the type annotation.
	 *
	 * @return the type annotation
	 * @review
	 */
	public Type getType() {
		return _type;
	}

	/**
	 * Returns the typeClass.
	 *
	 * @return the typeClass
	 * @review
	 */
	public Class<?> getTypeClass() {
		return _typeClass;
	}

	public static class Builder {

		public Builder(Type type, Class<?> typeClass) {
			_parsedType = new ParsedType();

			_parsedType._type = type;
			_parsedType._typeClass = typeClass;
		}

		public Builder addBidirectionalFieldData(
			FieldData<BidirectionalModel> bidirectionalFieldData) {

			_parsedType._bidirectionalFieldData.add(bidirectionalFieldData);

			return this;
		}

		public Builder addFieldData(FieldData fieldData) {
			_parsedType._fieldDataList.add(fieldData);

			return this;
		}

		public Builder addLinkedModelFieldData(
			FieldData<LinkedModel> linkedModelFieldData) {

			_parsedType._linkedModelFieldData.add(linkedModelFieldData);

			return this;
		}

		public Builder addListFieldData(FieldData<Class<?>> listFieldData) {
			_parsedType._listFieldData.add(listFieldData);

			return this;
		}

		public Builder addListParsedType(FieldData<ParsedType> parsedType) {
			_parsedType._listParsedTypes.add(parsedType);

			return this;
		}

		public Builder addParsedType(FieldData<ParsedType> parsedType) {
			_parsedType._parsedTypes.add(parsedType);

			return this;
		}

		public Builder addRelatedCollectionFieldData(
			FieldData<RelatedCollection> relatedCollectionFieldData) {

			_parsedType._relatedCollectionFieldData.add(
				relatedCollectionFieldData);

			return this;
		}

		public Builder addRelativeURLFieldData(
			FieldData<RelativeURL> relativeURLFieldData) {

			_parsedType._relativeURLFieldData.add(relativeURLFieldData);

			return this;
		}

		public ParsedType build() {
			return _parsedType;
		}

		public Builder idMethod(Method method) {
			_parsedType._method = method;

			return this;
		}

		private final ParsedType _parsedType;

	}

	private ParsedType() {
	}

	private List<FieldData<BidirectionalModel>> _bidirectionalFieldData =
		new ArrayList<>();
	private List<FieldData> _fieldDataList = new ArrayList<>();
	private List<FieldData<LinkedModel>> _linkedModelFieldData =
		new ArrayList<>();
	private List<FieldData<Class<?>>> _listFieldData = new ArrayList<>();
	private List<FieldData<ParsedType>> _listParsedTypes = new ArrayList<>();
	private Method _method;
	private List<FieldData<ParsedType>> _parsedTypes = new ArrayList<>();
	private List<FieldData<RelatedCollection>> _relatedCollectionFieldData =
		new ArrayList<>();
	private List<FieldData<RelativeURL>> _relativeURLFieldData =
		new ArrayList<>();
	private Type _type;
	private Class<?> _typeClass;

}