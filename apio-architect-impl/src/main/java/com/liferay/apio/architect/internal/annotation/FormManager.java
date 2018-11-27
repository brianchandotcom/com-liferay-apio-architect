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

import static com.liferay.apio.architect.internal.annotation.form.FormTransformer.toForm;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedTypeManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.representable.NameManager;
import com.liferay.apio.architect.internal.wiring.osgi.manager.uri.mapper.PathIdentifierMapperManager;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Computes the form for a className.
 *
 * @author Víctor Galán
 * @review
 */
@Component(service = FormManager.class)
public class FormManager {

	/**
	 * Computes the form for a className. Returns {@code null} if no form could
	 * be found.
	 *
	 * @param  className the className of the type
	 * @return the resulting form, if found; {@code null} otherwise
	 * @review
	 */
	public <T> Form<T> getForm(String className) {
		Optional<ParsedType> parsedTypeOptional =
			_parsedTypeManager.getParsedType(className);

		return parsedTypeOptional.<Form<T>>map(
			parsedType -> toForm(
				parsedType, _pathIdentifierMapperManager::mapToIdentifierOrFail,
				_nameManager::getNameOptional)
		).orElse(
			null
		);
	}

	@Reference
	private NameManager _nameManager;

	@Reference
	private ParsedTypeManager _parsedTypeManager;

	@Reference
	private PathIdentifierMapperManager _pathIdentifierMapperManager;

}