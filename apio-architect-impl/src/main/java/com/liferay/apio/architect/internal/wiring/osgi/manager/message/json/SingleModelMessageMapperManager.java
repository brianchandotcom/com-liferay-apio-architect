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

import static com.liferay.apio.architect.internal.wiring.osgi.manager.cache.ManagerCache.INSTANCE;

import com.liferay.apio.architect.internal.message.json.SingleModelMessageMapper;
import com.liferay.apio.architect.internal.wiring.osgi.manager.base.MessageMapperBaseManager;

import java.util.Optional;

import javax.ws.rs.core.Request;

import org.osgi.service.component.annotations.Component;

/**
 * Provides methods to get the {@link SingleModelMessageMapper} that corresponds
 * to the current request.
 *
 * @author Alejandro Hernández
 */
@Component(service = SingleModelMessageMapperManager.class)
public class SingleModelMessageMapperManager
	extends MessageMapperBaseManager<SingleModelMessageMapper> {

	public SingleModelMessageMapperManager() {
		super(
			SingleModelMessageMapper.class,
			INSTANCE::putSingleModelMessageMapper);
	}

	/**
	 * Returns the {@code SingleModelMessageMapper}, if present, that
	 * corresponds to the current request; {@code Optional#empty()} otherwise.
	 *
	 * @param  request the current request
	 * @return the {@code SingleModelMessageMapper}, if present; {@code
	 *         Optional#empty()} otherwise
	 */
	public <T> Optional<SingleModelMessageMapper<T>>
		getSingleModelMessageMapperOptional(Request request) {

		return INSTANCE.getSingleModelMessageMapperOptional(
			request, this::computeMessageMappers);
	}

}