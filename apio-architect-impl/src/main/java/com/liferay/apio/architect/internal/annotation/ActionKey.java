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

package com.liferay.apio.architect.internal.annotation;

import static com.liferay.apio.architect.operation.HTTPMethod.GET;

import java.util.Objects;

/**
 * Identifier of an action composed by the several combinations of valid path
 * parameters and the http verb
 *
 * @author Javier Gamarra
 * @review
 */
public class ActionKey {

	public static final String ANY_ROUTE = "__";

	public ActionKey(String httpMethod, String param1) {
		this(httpMethod, param1, null, null, null);
	}

	public ActionKey(String httpMethod, String param1, String param2) {
		this(httpMethod, param1, param2, null, null);
	}

	public ActionKey(
		String httpMethod, String param1, String param2, String param3) {

		this(httpMethod, param1, param2, param3, null);
	}

	public ActionKey(
		String httpMethod, String param1, String param2, String param3,
		String param4) {

		_httpMethodName = httpMethod;
		_param1 = param1;
		_param2 = param2;
		_param3 = param3;
		_param4 = param4;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if ((o == null) || (getClass() != o.getClass())) {
			return false;
		}

		ActionKey actionKey = (ActionKey)o;

		if (Objects.equals(_httpMethodName, actionKey._httpMethodName) &&
			Objects.equals(_param1, actionKey._param1) &&
			Objects.equals(_param2, actionKey._param2) &&
			Objects.equals(_param3, actionKey._param3) &&
			Objects.equals(_param4, actionKey._param4)) {

			return true;
		}

		return false;
	}

	public ActionKey getActionKeyWithHttpMethodName(String httpMethodName) {
		return new ActionKey(
			httpMethodName, _param1, _param2, _param3, _param4);
	}

	public ActionKey getGenericActionKey() {
		return new ActionKey(
			_httpMethodName, _param1, ANY_ROUTE, _param3, _param4);
	}

	public String getHttpMethodName() {
		return _httpMethodName;
	}

	public String getResourceName() {
		if (_param3 == null) {
			return _param1;
		}

		return _param1 + ("/" + _param3);
	}

	public String getResource() {
		return _param1;
	}

	public String getIdOrAction() {
		return _param2;
	}

	public String getNestedResource() {
		return _param3;
	}

	@Override
	public int hashCode() {
		return Objects.hash(
			_httpMethodName, _param1, _param2, _param3, _param4);
	}

	public boolean isCollection() {
		if (_param2 == null) {
			return true;
		}

		return false;
	}

	public boolean isCustom() {
		if ((_param2 != null) && !_param2.equals(ActionKey.ANY_ROUTE)) {
			return true;
		}

		return false;
	}

	public boolean isGetRequest() {
		return _httpMethodName.equals(GET.name());
	}

	public boolean isNested() {
		if (_param3 != null) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return String.join(
			"\'", "_httpMethodName='", _httpMethodName, ", _param1='", _param1,
			", _param2='", _param2, ", _param3='", _param3, ", _param4='",
			_param4);
	}

	private final String _httpMethodName;
	private final String _param1;
	private final String _param2;
	private final String _param3;
	private final String _param4;

}