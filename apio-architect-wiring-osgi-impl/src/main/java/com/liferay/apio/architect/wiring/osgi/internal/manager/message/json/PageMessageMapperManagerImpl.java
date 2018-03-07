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
import com.liferay.apio.architect.message.json.PageMessageMapper;
import com.liferay.apio.architect.wiring.osgi.internal.manager.base.BaseManager;
import com.liferay.apio.architect.wiring.osgi.manager.message.json.PageMessageMapperManager;
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
public class PageMessageMapperManagerImpl
	extends BaseManager<PageMessageMapper, String>
	implements PageMessageMapperManager {

	public PageMessageMapperManagerImpl() {
		super(PageMessageMapper.class);
	}

	@Override
	public <T> Optional<PageMessageMapper<T>> getPageMessageMapperOptional(
		Request request) {

		return INSTANCE.getPageMessageMapperOptional(
			request, this::_computePageMessageMappers);
	}

	@Override
	protected void emit(
		ServiceReference<PageMessageMapper> serviceReference,
		Emitter<String> emitter) {

		PageMessageMapper pageMessageMapper = bundleContext.getService(
			serviceReference);

		emitter.emit(pageMessageMapper.getMediaType());
	}

	private void _computePageMessageMappers() {
		Stream<String> stream = getKeyStream();

		stream.forEach(
			key -> {
				PageMessageMapper pageMessageMapper =
					serviceTrackerMap.getService(key);

				try {
					MediaType mediaType = MediaType.valueOf(key);

					INSTANCE.putPageMessageMapper(mediaType, pageMessageMapper);
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