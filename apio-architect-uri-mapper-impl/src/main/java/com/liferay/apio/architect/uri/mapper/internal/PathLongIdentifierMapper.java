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

package com.liferay.apio.architect.uri.mapper.internal;

import com.liferay.apio.architect.uri.Path;
import com.liferay.apio.architect.uri.mapper.PathIdentifierMapper;

import io.vavr.control.Try;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;

/**
 * Maps a {@link Path} to a {@link Long}, and vice versa.
 *
 * <p>
 * {@code Long} can then be used as the identifier of a resource model.
 * </p>
 *
 * @author Alejandro Hernández
 */
@Component(service = PathIdentifierMapper.class)
public class PathLongIdentifierMapper implements PathIdentifierMapper<Long> {

	@Override
	public Long map(Path path) {
		return Try.of(
			() -> Long.parseLong(path.getId())
		).getOrElseThrow(
			() -> new BadRequestException(
				path.getId() + " is not a valid long value")
		);
	}

	@Override
	public Path map(String name, Long aLong) {
		return new Path(name, String.valueOf(aLong));
	}

}