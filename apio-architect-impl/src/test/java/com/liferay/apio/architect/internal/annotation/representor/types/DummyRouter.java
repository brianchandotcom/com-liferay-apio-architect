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

import com.liferay.apio.architect.annotation.Actions;
import com.liferay.apio.architect.annotation.Body;
import com.liferay.apio.architect.annotation.EntryPoint;
import com.liferay.apio.architect.annotation.Id;
import com.liferay.apio.architect.annotation.ParentId;
import com.liferay.apio.architect.annotation.Vocabulary;
import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.identifier.Identifier;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.router.ActionRouter;

import java.util.Arrays;
import java.util.List;

/**
 * @author Javier Gamarra
 */
public class DummyRouter implements ActionRouter<DummyRouter.DummyIdentifier> {

	@Actions.Remove
	public void remove(@Id long id, Credentials credentials) {
	}

	@Actions.Retrieve
	public DummyIdentifier retrieve(@Id Long id) {
		return () -> 0;
	}

	@Actions.Retrieve
	public DummyIdentifier retrieveChild(
		@ParentId(value = DummyIdentifier.class) Long id) {

		return () -> 0;
	}

	@Actions.Action(httpMethod = "GET", name = "first-ten-elements")
	public PageItems<DummyIdentifier> retrieveFirstTenElements(
		Pagination pagination, Credentials credentials, @Id Long id,
		@Body String body) {

		List<DummyIdentifier> dummyIdentifiers = Arrays.asList(() -> 0);

		return new PageItems<>(dummyIdentifiers, dummyIdentifiers.size());
	}

	@Actions.Retrieve
	@EntryPoint
	public PageItems<DummyIdentifier> retrievePage(Pagination pagination) {
		List<DummyIdentifier> dummyIdentifiers = Arrays.asList(() -> 0);

		return new PageItems<>(dummyIdentifiers, dummyIdentifiers.size());
	}

	@Vocabulary.Type("dummy")
	public interface DummyIdentifier extends Identifier<Long> {

		public long getId();

	}

}