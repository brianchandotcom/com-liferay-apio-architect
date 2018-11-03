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

package com.liferay.apio.architect.internal.body;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

/**
 * This is a mock class for testing purposes. It allows transforming a common
 * {@link InputStream} into a request {@link ServletInputStream}.
 *
 * @author Alejandro Hernández
 * @review
 */
public class MockServletInputStream extends ServletInputStream {

	/**
	 * Create a MockServletInputStream for the given source stream.
	 *
	 * @param  inputStream the source stream, should not be {@code null}
	 * @review
	 */
	public MockServletInputStream(InputStream inputStream) {
		assertThat(inputStream, is(not(nullValue())));
		_inputStream = inputStream;
	}

	@Override
	public void close() throws IOException {
		super.close();

		_inputStream.close();
	}

	@Override
	public int read() throws IOException {
		return _inputStream.read();
	}

	private final InputStream _inputStream;

}