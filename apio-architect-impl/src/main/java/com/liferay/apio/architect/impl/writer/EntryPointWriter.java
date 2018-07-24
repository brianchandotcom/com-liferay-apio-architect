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

package com.liferay.apio.architect.impl.writer;

import static com.liferay.apio.architect.impl.url.URLCreator.createCollectionURL;

import com.liferay.apio.architect.impl.alias.RepresentorFunction;
import com.liferay.apio.architect.impl.entrypoint.EntryPoint;
import com.liferay.apio.architect.impl.message.json.EntryPointMessageMapper;
import com.liferay.apio.architect.impl.message.json.JSONObjectBuilder;
import com.liferay.apio.architect.impl.request.RequestInfo;
import com.liferay.apio.architect.impl.url.ApplicationURL;
import com.liferay.apio.architect.representor.BaseRepresentor;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * Writes the API entrypoint.
 *
 * @author Alejandro Hernández
 * @review
 */
@Component
public class EntryPointWriter {

	/**
	 * Writes the handled {@link EntryPoint} to a string.
	 *
	 * @return the representation of the {@code EntryPoint}
	 */
	public String write() {
		ApplicationURL applicationURL = _requestInfo.getApplicationURL();

		_entryPointMessageMapper.mapSelfURL(
			_jsonObjectBuilder, applicationURL.get());

		List<String> resourceNames = _entryPoint.getResourceNames();

		for (String resourceName : resourceNames) {
			JSONObjectBuilder itemJsonObjectBuilder = new JSONObjectBuilder();

			String url = createCollectionURL(applicationURL, resourceName);

			_entryPointMessageMapper.mapItemSelfURL(
				_jsonObjectBuilder, itemJsonObjectBuilder, resourceName, url);

			_getCollectionItemType(resourceName, itemJsonObjectBuilder);

			_entryPointMessageMapper.onFinishItem(
				_jsonObjectBuilder, itemJsonObjectBuilder);
		}

		_entryPointMessageMapper.onFinish(_jsonObjectBuilder, _entryPoint);

		return _jsonObjectBuilder.build();
	}

	/**
	 * Creates {@code BatchResultWriter} instances.
	 */
	public interface Builder {

		/**
		 * Adds information about the entry point being written to the builder.
		 *
		 * @param  entryPoint the entry point being written
		 * @return the updated builder
		 */
		public static EntryPointMessageMapperStep entryPoint(
			EntryPoint entryPoint) {

			return entryPointMessageMapper -> representorFunction -> requestInfo
				-> () -> new EntryPointWriter(
					entryPoint, entryPointMessageMapper, representorFunction,
					requestInfo);
		}

		public interface BuildStep {

			/**
			 * Constructs and returns a {@code EntryPointWriter} instance with
			 * the information provided to the builder.
			 *
			 * @return the {@code EntryPointWriter} instance
			 */
			public EntryPointWriter build();

		}

		public interface EntryPointMessageMapperStep {

			/**
			 * Adds information to the builder about the {@link
			 * EntryPointMessageMapper}.
			 *
			 * @param  entryPointMessageMapper the {@code
			 *         EntryPointMessageMapper}
			 * @return the updated builder
			 */
			public RepresentorFunctionStep entryPointMessageMapper(
				EntryPointMessageMapper entryPointMessageMapper);

		}

		public interface RepresentorFunctionStep {

			/**
			 * Adds information to the builder about the function that gets a
			 * class's {@link
			 * com.liferay.apio.architect.representor.Representor}.
			 *
			 * @param  representorFunction the function that gets a class's
			 *         {@code Representor}
			 * @return the updated builder
			 */
			public RequestInfoStep representorFunction(
				RepresentorFunction representorFunction);

		}

		public interface RequestInfoStep {

			/**
			 * Adds information to the builder about the request.
			 *
			 * @param  requestInfo the information obtained from the request.
			 *         This can be created by using a {@link
			 *         RequestInfo.Builder}
			 * @return the updated builder
			 */
			public BuildStep requestInfo(RequestInfo requestInfo);

		}

	}

	private EntryPointWriter(
		EntryPoint entryPoint, EntryPointMessageMapper entryPointMessageMapper,
		RepresentorFunction representorFunction, RequestInfo requestInfo) {

		_entryPoint = entryPoint;
		_entryPointMessageMapper = entryPointMessageMapper;
		_representorFunction = representorFunction;
		_requestInfo = requestInfo;

		_jsonObjectBuilder = new JSONObjectBuilder();
	}

	private void _getCollectionItemType(
		String resourceName, JSONObjectBuilder itemJsonObjectBuilder) {

		_representorFunction.apply(
			resourceName
		).map(
			BaseRepresentor::getTypes
		).map(
			types -> types.get(0)
		).ifPresent(
			type -> _entryPointMessageMapper.mapSemantics(
				itemJsonObjectBuilder, type)
		);
	}

	private final EntryPoint _entryPoint;
	private final EntryPointMessageMapper _entryPointMessageMapper;
	private final JSONObjectBuilder _jsonObjectBuilder;
	private final RepresentorFunction _representorFunction;
	private final RequestInfo _requestInfo;

}