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

package com.liferay.vulcan.liferay.internal.filter;

import com.liferay.vulcan.liferay.filter.ClassNameClassPKFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alejandro Hernández
 */
public class ClassNameClassPKFilterImpl implements ClassNameClassPKFilter {

	public static final String CLASS_NAME = "className";

	public static final String CLASS_PK = "classPK";

	public ClassNameClassPKFilterImpl(String className, Long classPK) {
		_className = className;
		_classPK = classPK;
	}

	@Override
	public String getClassName() {
		return _className;
	}

	@Override
	public Long getClassPK() {
		return _classPK;
	}

	@Override
	public Map<String, String> getQueryParamMap() {
		return new HashMap<String, String>() {
			{
				put(CLASS_NAME, _className);
				put(CLASS_PK, String.valueOf(_classPK));
			}
		};
	}

	private final String _className;
	private final Long _classPK;

}