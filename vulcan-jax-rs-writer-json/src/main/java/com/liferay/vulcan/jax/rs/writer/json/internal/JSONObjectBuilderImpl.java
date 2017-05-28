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

package com.liferay.vulcan.jax.rs.writer.json.internal;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.vulcan.message.json.JSONObjectBuilder;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
public class JSONObjectBuilderImpl implements JSONObjectBuilder {

	@Override
	public JSONObject build() {
		return _jsonObject;
	}

	@Override
	public FirstStep field(String name) {
		return new FirstStepImpl(name, _jsonObject);
	}

	@Override
	public FirstStep nestedField(String parentName, String... nestedNames) {
		FirstStep firstStep = field(parentName);

		for (String string : nestedNames) {
			firstStep = firstStep.field(string);
		}

		return firstStep;
	}

	private final JSONObject _jsonObject = JSONFactoryUtil.createJSONObject();

	private static class FirstStepImpl implements FirstStep {

		public FirstStepImpl(String name, JSONObject jsonObject) {
			_name = name;
			_stepJSONObject = jsonObject;
		}

		@Override
		public FirstStep field(String name) {
			JSONObject previousJSONObject = _stepJSONObject.getJSONObject(
				_name);

			if (previousJSONObject == null) {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

				_stepJSONObject.put(_name, jsonObject);

				return new FirstStepImpl(name, jsonObject);
			}

			return new FirstStepImpl(name, previousJSONObject);
		}

		@Override
		public FirstStep nestedField(String parentName, String... nestedNames) {
			FirstStep firstStep = field(parentName);

			for (String string : nestedNames) {
				firstStep = firstStep.field(string);
			}

			return firstStep;
		}

		@Override
		public void value(Object value) {
			if (value != null) {
				_stepJSONObject.put(_name, value);
			}
		}

		private final String _name;
		private final JSONObject _stepJSONObject;

	}

}