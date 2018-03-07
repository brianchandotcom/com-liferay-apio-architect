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

package com.liferay.apio.architect.wiring.osgi.manager.message.json;

import aQute.bnd.annotation.ProviderType;

import com.liferay.apio.architect.message.json.SingleModelMessageMapper;

import java.util.Optional;

import javax.ws.rs.core.Request;

/**
 * Provides methods to get the {@link SingleModelMessageMapper} that corresponds
 * to the current request.
 *
 * @author Alejandro Hernández
 * @review
 */
@ProviderType
public interface SingleModelMessageMapperManager {

	/**
	 * Returns the {@code SingleModelMessageMapper} that corresponds to the
	 * current request. Returns {@code Optional#empty()} if no message mapper
	 * can be found.
	 *
	 * @param  request the current request
	 * @return the {@code SingleModelMessageMapper} that corresponds to the
	 *         request, if present; {@code Optional#empty()} otherwise.
	 * @review
	 */
	public <T> Optional<SingleModelMessageMapper<T>>
		getSingleModelMessageMapperOptional(Request request);

}