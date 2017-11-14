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

package com.liferay.vulcan.test.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instances of this class represent a mock model that can be written using a
 * {@link com.liferay.vulcan.message.json.SingleModelMessageMapper}.
 *
 * <p>
 * This class has two operation-modes, with {@code null} fields, or without
 * them. This means that when activating {@code nulls}, methods such as {@link
 * #getBooleanFields()} will generate one or more extra fields with {@code null}
 * or {@code empty} values. In order to activate this mode, instantiate this
 * class with the constructor {@link #MockModel(boolean)} with a boolean {@code
 * true}. Instantiating this class with the no-parameters constructor will
 * create a version with the {@code nulls} deactivated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public class MockModel {

	public MockModel() {
		_activateNulls = false;
	}

	public MockModel(boolean activateNulls) {
		_activateNulls = activateNulls;
	}

	/**
	 * Return the boolean fields of this model.
	 *
	 * @return the boolean fields of the model
	 * @review
	 */
	public Map<String, Boolean> getBooleanFields() {
		return new HashMap<String, Boolean>() {
			{
				put("boolean1", true);
				put("boolean2", false);

				if (_activateNulls) {
					put("boolean3", null);
				}
			}
		};
	}

	/**
	 * Return the links of this model.
	 *
	 * @return the links of the model
	 * @review
	 */
	public Map<String, String> getLinks() {
		return new HashMap<String, String>() {
			{
				put("link1", "www.liferay.com");
				put("link2", "community.liferay.com");

				if (_activateNulls) {
					put("link3", null);
					put("link4", "");
				}
			}
		};
	}

	/**
	 * Return the number fields of this model.
	 *
	 * @return the number fields of the model
	 * @review
	 */
	public Map<String, Number> getNumberFields() {
		return new HashMap<String, Number>() {
			{
				put("number1", 2017);
				put("number2", 42);

				if (_activateNulls) {
					put("number3", null);
				}
			}
		};
	}

	/**
	 * Return the string fields of this model.
	 *
	 * @return the string fields of the model
	 * @review
	 */
	public Map<String, String> getStringFields() {
		return new HashMap<String, String>() {
			{
				put("string1", "Live long and prosper");
				put("string2", "Hypermedia");

				if (_activateNulls) {
					put("string3", null);
					put("string4", "");
				}
			}
		};
	}

	/**
	 * Return the types of this model.
	 *
	 * @return the types of the model
	 * @review
	 */
	public List<String> getTypes() {
		return new ArrayList<String>() {
			{
				add("Type 1");
				add("Type 2");
			}
		};
	}

	/**
	 * Return the url of this model.
	 *
	 * @return the url of the model
	 * @review
	 */
	public String getURL() {
		return "localhost:8080";
	}

	private final boolean _activateNulls;

}