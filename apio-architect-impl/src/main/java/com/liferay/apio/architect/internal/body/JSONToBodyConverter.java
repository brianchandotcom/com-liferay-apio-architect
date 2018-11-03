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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.liferay.apio.architect.form.Body;

import io.vavr.control.Try;

import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.BadRequestException;

/**
 * Reads JSON objects as a {@link Body}.
 *
 * @author Alejandro Hernández
 * @review
 */
public class JSONToBodyConverter {

	/**
	 * Reads a {@code "application/json"} HTTP request body into a {@link Body}
	 * instance or fails with a {@link BadRequestException} if the input is not
	 * a valid JSON.
	 *
	 * @review
	 */
	public static Body jsonToBody(HttpServletRequest request) {
		return Try.withResources(
			() -> new InputStreamReader(request.getInputStream(), UTF_8)
		).of(
			new ObjectMapper()::readTree
		).filter(
			node -> node.isObject() || node.isArray()
		).map(
			JSONBodyImpl::new
		).getOrElseThrow(
			() -> new BadRequestException("Body is not a valid JSON")
		);
	}

	/**
	 * {@link Body} implementation for {@code "application/json"}.
	 *
	 * @review
	 */
	public static class JSONBodyImpl implements Body {

		public JSONBodyImpl(JsonNode jsonNode) {
			_jsonNode = jsonNode;
		}

		@Override
		public Optional<List<Body>> getBodyMembersOptional() {
			return Try.success(
				_jsonNode
			).filter(
				JsonNode::isArray
			).map(
				ArrayNode.class::cast
			).map(
				JSONBodyImpl::_getJsonElements
			).map(
				List::stream
			).map(
				stream -> stream.filter(
					JsonNode::isObject
				).map(
					ObjectNode.class::cast
				).map(
					JSONBodyImpl::new
				).map(
					Body.class::cast
				).collect(
					Collectors.toList()
				)
			).map(
				Optional::ofNullable
			).getOrElseThrow(
				() -> new BadRequestException("Body is not a valid JSON Array")
			);
		}

		@Override
		public Optional<List<Body>> getNestedBodyListOptional(String key) {
			return Optional.ofNullable(
				_jsonNode.get(key)
			).filter(
				JsonNode::isArray
			).map(
				ArrayNode.class::cast
			).map(
				JSONBodyImpl::_getJsonElements
			).map(
				List::stream
			).map(
				stream -> stream.filter(
					JsonNode::isObject
				).map(
					JSONBodyImpl::new
				).collect(
					Collectors.toList()
				)
			);
		}

		@Override
		public Optional<Body> getNestedBodyOptional(String key) {
			return Optional.ofNullable(
				_jsonNode.get(key)
			).filter(
				JsonNode::isObject
			).map(
				ObjectNode.class::cast
			).map(
				JSONBodyImpl::new
			);
		}

		@Override
		public Optional<List<String>> getValueListOptional(String key) {
			return Optional.ofNullable(
				_jsonNode.get(key)
			).filter(
				JsonNode::isArray
			).map(
				ArrayNode.class::cast
			).map(
				JSONBodyImpl::_getJsonElements
			).map(
				List::stream
			).map(
				stream -> stream.filter(
					JsonNode::isValueNode
				).map(
					JsonNode::asText
				).collect(
					Collectors.toList()
				)
			);
		}

		@Override
		public Optional<String> getValueOptional(String key) {
			return Optional.ofNullable(
				_jsonNode.get(key)
			).filter(
				JsonNode::isValueNode
			).map(
				JsonNode::asText
			);
		}

		private static List<JsonNode> _getJsonElements(ArrayNode arrayNode) {
			List<JsonNode> jsonNodes = new ArrayList<>();

			Iterator<JsonNode> iterator = arrayNode.iterator();

			iterator.forEachRemaining(jsonNodes::add);

			return jsonNodes;
		}

		private final JsonNode _jsonNode;

	}

}