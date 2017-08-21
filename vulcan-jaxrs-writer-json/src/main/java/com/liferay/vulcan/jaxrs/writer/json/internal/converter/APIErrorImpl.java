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

package com.liferay.vulcan.jaxrs.writer.json.internal.converter;

import com.liferay.vulcan.result.APIError;

/**
 * @author Alejandro Hernández
 */
public class APIErrorImpl implements APIError {

	public APIErrorImpl(String title, String type, int statusCode) {
		_title = title;
		_type = type;
		_description = "";
		_statusCode = statusCode;
	}

	public APIErrorImpl(
		String title, String description, String type, int statusCode) {

		_title = title;
		_description = description;
		_type = type;
		_statusCode = statusCode;
	}

	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public int getStatusCode() {
		return _statusCode;
	}

	@Override
	public String getTitle() {
		return _title;
	}

	@Override
	public String getType() {
		return _type;
	}

	private final String _description;
	private final int _statusCode;
	private final String _title;
	private final String _type;

}