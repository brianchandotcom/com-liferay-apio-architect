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

package com.liferay.vulcan.message.json;

import com.liferay.vulcan.documentation.Documentation;
import com.liferay.vulcan.test.result.MockAPIError;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.HttpHeaders;

/**
 * @author Alejandro Hernández
 */
public class DocumentationMessageMapperTest {

	@Test
	public void testMessageMapperIsEmptyByDefaultAndSupportsMapping() {
		DocumentationMessageMapper documentationMessageMapper =
			() -> "mediaType";

		HttpHeaders httpHeaders = Mockito.mock(HttpHeaders.class);

		Documentation documentation = Mockito.mock(Documentation.class);

		assertThat(
			documentationMessageMapper.supports(documentation, httpHeaders),
			is(equalTo(true)));
	}

}