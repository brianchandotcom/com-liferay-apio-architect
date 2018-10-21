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

package com.liferay.apio.architect.internal.annotation;

import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.IDENTIFIER_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.IDENTIFIER_TO_PATH_FUNCTION;
import static com.liferay.apio.architect.internal.routes.RoutesTestUtil.PROVIDE_FUNCTION;

import static org.mockito.Matchers.any;

import com.liferay.apio.architect.internal.wiring.osgi.manager.provider.ProviderManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper.PathIdentifierMapperManager;

import org.junit.Before;

import org.mockito.Mockito;

/**
 * @author Javier Gamarra
 */
public class BaseRoutesTest {

	@Before
	public void setUp() {
		PathIdentifierMapperManager pathIdentifierMapperManager = Mockito.mock(
			PathIdentifierMapperManager.class);

		Mockito.when(
			pathIdentifierMapperManager.mapToIdentifierOrFail(any())
		).thenAnswer(
			invocation -> IDENTIFIER_FUNCTION.apply(null)
		);

		Mockito.when(
			pathIdentifierMapperManager.mapToPath(any(), any())
		).thenAnswer(
			invocation -> IDENTIFIER_TO_PATH_FUNCTION.apply(null)
		);

		ProviderManager providerManager = Mockito.mock(ProviderManager.class);

		Mockito.when(
			providerManager.provideMandatory(any(), any())
		).thenAnswer(
			invocation -> PROVIDE_FUNCTION.apply(
				invocation.getArgumentAt(1, Class.class))
		);

		actionManager = ActionManagerImpl.newTestInstance(
			pathIdentifierMapperManager, providerManager);
	}

	protected ActionManager actionManager;

}