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

import com.liferay.apio.architect.alias.IdentifierFunction;
import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.internal.annotation.form.FormTransformer;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedType;
import com.liferay.apio.architect.internal.annotation.representor.processor.ParsedTypeManager;

import java.util.Optional;
import java.util.function.Function;

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
	 * Computes the form for a className.
	 *
	 * @param  className the className of the type
	 * @param  pathToIdentifierFunction function that extract the identifier
	 *         from a path given
	 * @param  nameFunction function that returns the name of the resource given
	 *         the className of the identifier
	 * @return the resulting optional form
	 * @review
	 */
	public <T> Optional<Form<T>> getFormOptional(
		String className, IdentifierFunction<?> pathToIdentifierFunction,
		Function<String, Optional<String>> nameFunction) {

		Optional<ParsedType> parsedTypeOptional =
			_parsedTypeManager.getParsedType(className);

		return parsedTypeOptional.map(
			parsedType -> FormTransformer.toForm(
				parsedType, pathToIdentifierFunction, nameFunction)
		);
	}

	@Reference
	private ParsedTypeManager _parsedTypeManager;

}