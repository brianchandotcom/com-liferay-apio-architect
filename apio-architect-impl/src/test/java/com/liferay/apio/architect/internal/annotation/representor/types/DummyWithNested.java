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

package com.liferay.apio.architect.internal.annotation.representor.types;

import static com.liferay.apio.architect.annotation.Vocabulary.LinkTo.ResourceType.CHILD_COLLECTION;

import static java.util.Arrays.asList;

import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.Vocabulary.Field;
import com.liferay.apio.architect.annotation.Vocabulary.LinkTo;
import com.liferay.apio.architect.annotation.Vocabulary.Type;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.internal.annotation.representor.types.Dummy.IntegerIdentifier;

import java.util.Collections;
import java.util.List;

/**
 * @author Víctor Galán
 */
@Type("DummyWithNested")
public interface DummyWithNested extends Identifier<Long> {

	@Id
	public default Long getId() {
		return 1L;
	}

	@Field("nestedDummy")
	public default NestedDummy getNestedDummy() {
		return new DummyWithNested.NestedDummyImpl();
	}

	@Field("nestedDummyList")
	public default List<NestedDummy> getNestedDummyList() {
		return Collections.singletonList(new NestedDummyImpl());
	}

	@Type("NestedDummy")
	public interface NestedDummy {

		@Field("linkToChildCollection")
		@LinkTo(
			resource = IntegerIdentifier.class, resourceType = CHILD_COLLECTION
		)
		public default Integer getLinkToChildCollection() {
			return 2;
		}

		@Field("linkToSingle")
		@LinkTo(resource = IntegerIdentifier.class)
		public default Integer getLinkToSingle() {
			return 1;
		}

		@Field("stringField")
		public default String getStringField() {
			return "string";
		}

		@Field("stringListField")
		public default List<String> getStringListField() {
			return asList("one", "two");
		}

	}

	public class NestedDummyImpl implements NestedDummy {
	}

}