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

package com.liferay.apio.architect.documentation.contributor;

import com.liferay.apio.architect.documentation.contributor.CustomDocumentation.Builder;

/**
 * Represents the mapping between properties or operation names and their
 * documentation.
 *
 * @author Víctor Galán
 */
public interface CustomDocumentationContributor {

	/**
	 * Creates a {@link CustomDocumentation} instance by using the provided
	 * {@link CustomDocumentation.Builder}.
	 *
	 * @param builder the builder
	 */
	public CustomDocumentation customDocumentation(Builder builder);

}