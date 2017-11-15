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

package com.liferay.vulcan.test.resource;

import com.liferay.vulcan.alias.BinaryFunction;
import com.liferay.vulcan.resource.RelatedModel;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Instances of this class represent a mock model that can be written using a
 * {@link com.liferay.vulcan.message.json.SingleModelMessageMapper}.
 *
 * <p>
 * This class has two operation-modes, with {@code null} fields, or without
 * them. This means that when activating {@code nulls}, methods such as {@link
 * #getBooleanFields()} will generate one or more extra fields with {@code null}
 * or {@code empty} values. In order to activate this mode, instantiate this
 * class with the constructor {@link #RootModel(boolean)} with a boolean {@code
 * true}. Instantiating this class with the no-parameters constructor will
 * create a version with the {@code nulls} deactivated.
 * </p>
 *
 * @author Alejandro Hernández
 * @review
 */
public class RootModel implements Model {

	public RootModel() {
		_activateNulls = false;
	}

	public RootModel(boolean activateNulls) {
		_activateNulls = activateNulls;
	}

	@Override
	public <T extends Model>
		Map<String, BinaryFunction<T>> getBinaryFunctions() {

		return new LinkedHashMap<String, BinaryFunction<T>>() {
			{
				put("binary1", __ -> null);
				put("binary2", __ -> null);
			}
		};
	}

	@Override
	public Map<String, Boolean> getBooleanFields() {
		return new LinkedHashMap<String, Boolean>() {
			{
				put("boolean1", true);
				put("boolean2", false);

				if (_activateNulls) {
					put("boolean3", null);
				}
			}
		};
	}

	@Override
	public <T extends Model>
		List<RelatedModel<T, ?>> getEmbeddedRelatedModels() {

		return Arrays.asList(
			new RelatedModel<>(
				"embedded1", InnerModel.class,
				__ -> Optional.of(new InnerModel("second"))),
			new RelatedModel<>(
				"embedded2", InnerModel.class,
				__ -> Optional.of(new InnerModel("third"))));
	}

	@Override
	public String getId() {
		return "first";
	}

	@Override
	public <T extends Model> List<RelatedModel<T, ?>> getLinkedRelatedModels() {
		return Arrays.asList(
			new RelatedModel<>(
				"linked1", InnerModel.class,
				__ -> Optional.of(new InnerModel("fourth"))),
			new RelatedModel<>(
				"linked2", InnerModel.class,
				__ -> Optional.of(new InnerModel("sixth"))));
	}

	@Override
	public Map<String, String> getLinks() {
		return new LinkedHashMap<String, String>() {
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

	@Override
	public Map<String, String> getLocalizedStringFunctions() {
		return new LinkedHashMap<String, String>() {
			{
				put("localizedString1", "Translated 1");
				put("localizedString2", "Translated 2");

				if (_activateNulls) {
					put("localizedString3", null);
					put("localizedString4", "");
				}
			}
		};
	}

	@Override
	public Map<String, Number> getNumberFields() {
		return new LinkedHashMap<String, Number>() {
			{
				put("number1", 2017);
				put("number2", 42);

				if (_activateNulls) {
					put("number3", null);
				}
			}
		};
	}

	@Override
	public Map<String, ? extends Model> getRelatedCollections() {
		return new LinkedHashMap<String, Model>() {
			{
				put("relatedCollection1", new InnerModel("seventh"));
				put("relatedCollection2", new InnerModel("eight"));
			}
		};
	}

	@Override
	public Map<String, String> getStringFields() {
		return new LinkedHashMap<String, String>() {
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

	@Override
	public List<String> getTypes() {
		return Arrays.asList("Type 1", "Type 2");
	}

	@Override
	public String getURL() {
		return "localhost:8080";
	}

	private final boolean _activateNulls;

}