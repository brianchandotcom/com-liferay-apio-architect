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

package com.liferay.apio.architect.wiring.osgi.internal.manager.message.json;

import static com.liferay.apio.architect.wiring.osgi.internal.manager.cache.ManagerCache.INSTANCE;

import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import com.liferay.apio.architect.logger.ApioLogger;
import com.liferay.apio.architect.message.json.ErrorMessageMapper;
import com.liferay.apio.architect.wiring.osgi.internal.manager.base.BaseManager;
import com.liferay.apio.architect.wiring.osgi.manager.message.json.ErrorMessageMapperManager;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper.Emitter;

import java.util.Optional;
import java.util.stream.Stream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class ErrorMessageMapperManagerImpl
	extends BaseManager<ErrorMessageMapper, String>
	implements ErrorMessageMapperManager {

	public ErrorMessageMapperManagerImpl() {
		super(ErrorMessageMapper.class);
	}

	@Override
	public Optional<ErrorMessageMapper> getErrorMessageMapperOptional(
		Request request) {

		return INSTANCE.getErrorMessageMapperOptional(
			request, this::_computeErrorMessageMappers);
	}

	@Override
	protected void emit(
		ServiceReference<ErrorMessageMapper> serviceReference,
		Emitter<String> emitter) {

		ErrorMessageMapper errorMessageMapper = bundleContext.getService(
			serviceReference);

		emitter.emit(errorMessageMapper.getMediaType());
	}

	private void _computeErrorMessageMappers() {
		Stream<String> stream = getKeyStream();

		stream.forEach(
			key -> {
				ErrorMessageMapper errorMessageMapper =
					serviceTrackerMap.getService(key);

				try {
					MediaType mediaType = MediaType.valueOf(key);

					INSTANCE.putErrorMessageMapper(
						mediaType, errorMessageMapper);
				}
				catch (IllegalArgumentException iae) {
					if (_apioLogger != null) {
						_apioLogger.warning(
							"Message mapper has invalid media type: " + key);
					}
				}
			});
	}

	@Reference(cardinality = OPTIONAL, policyOption = GREEDY)
	private ApioLogger _apioLogger;

}