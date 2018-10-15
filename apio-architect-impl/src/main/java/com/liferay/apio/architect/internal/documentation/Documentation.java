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

package com.liferay.apio.architect.internal.documentation;

import com.liferay.apio.architect.documentation.APIDescription;
import com.liferay.apio.architect.documentation.APITitle;
import com.liferay.apio.architect.documentation.contributor.CustomDocumentation;
import com.liferay.apio.architect.internal.annotation.ActionManager;
import com.liferay.apio.architect.internal.url.ApplicationURL;
import com.liferay.apio.architect.representor.Representor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents the API's auto-documentation.
 *
 * @author Alejandro Hernández
 */
public class Documentation {

	public Documentation(
		Supplier<Optional<APITitle>> apiTitleSupplier,
		Supplier<Optional<APIDescription>> apiDescriptionSupplier,
		Supplier<Optional<ApplicationURL>> entryPointSupplier,
		Supplier<Map<String, Representor>> representorMapSupplier,
		Supplier<ActionManager> actionManagerSupplier,
		Supplier<CustomDocumentation> customDocumentationSupplier) {

		_apiTitleSupplier = apiTitleSupplier;
		_apiDescriptionSupplier = apiDescriptionSupplier;
		_entryPointSupplier = entryPointSupplier;
		_representorMapSupplier = representorMapSupplier;
		_actionManagerSupplier = actionManagerSupplier;
		_customDocumentationSupplier = customDocumentationSupplier;
	}

	public Supplier<ActionManager> getActionManagerSupplier() {
		return _actionManagerSupplier;
	}

	/**
	 * Returns the API's description, if present; otherwise returns {@code
	 * Optional#empty()}.
	 *
	 * @return the API's description, if present; {@code Optional#empty()}
	 *         otherwise
	 */
	public Optional<String> getAPIDescriptionOptional() {
		Optional<APIDescription> optional = _apiDescriptionSupplier.get();

		return optional.map(APIDescription::get);
	}

	/**
	 * Returns the API's title, if present; returns {@code Optional#empty()}
	 * otherwise.
	 *
	 * @return the API's title, if present; {@code Optional#empty()} otherwise
	 */
	public Optional<String> getAPITitleOptional() {
		Optional<APITitle> optional = _apiTitleSupplier.get();

		return optional.map(APITitle::get);
	}

	public CustomDocumentation getCustomDocumentation() {
		return _customDocumentationSupplier.get();
	}

	public Optional<String> getEntryPointOptional() {
		Optional<ApplicationURL> optional = _entryPointSupplier.get();

		return optional.map(ApplicationURL::get);
	}

	/**
	 * Returns a map that contains each resource's name and {@link Representor}
	 * as key-value pairs.
	 *
	 * @return the map
	 */
	public Map<String, Representor> getRepresentors() {
		return _representorMapSupplier.get();
	}

	private final Supplier<ActionManager> _actionManagerSupplier;
	private final Supplier<Optional<APIDescription>> _apiDescriptionSupplier;
	private final Supplier<Optional<APITitle>> _apiTitleSupplier;
	private final Supplier<CustomDocumentation> _customDocumentationSupplier;
	private final Supplier<Optional<ApplicationURL>> _entryPointSupplier;
	private final Supplier<Map<String, Representor>> _representorMapSupplier;

}