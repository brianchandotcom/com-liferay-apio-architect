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

package com.liferay.apio.architect.representor.dummy;

import static java.nio.charset.StandardCharsets.UTF_8;

import static java.util.Arrays.asList;

import com.liferay.apio.architect.language.Language;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Dummy class that can be used to {@link
 * com.liferay.apio.architect.representor.Representor}.
 *
 * @author Alejandro Hernández
 * @review
 */
public class Dummy implements DummyIdentified {

	public Dummy(int id) {
		_id = id;
	}

	public Optional<DummyLinked> getDummyLinked1Optional() {
		return Optional.of(new DummyLinked(3));
	}

	public Optional<DummyLinked> getDummyLinked2Optional() {
		return Optional.of(new DummyLinked(4));
	}

	public Optional<DummyParent> getDummyParent1Optional() {
		return Optional.of(new DummyParent(1));
	}

	public Optional<DummyParent> getDummyParent2Optional() {
		return Optional.of(new DummyParent(2));
	}

	@Override
	public int getId() {
		return _id;
	}

	public String getLocalizedString1(Language language) {
		Locale locale = language.getPreferredLocale();

		return locale.getLanguage() + "1";
	}

	public String getLocalizedString2(Language language) {
		Locale locale = language.getPreferredLocale();

		return locale.getLanguage() + "2";
	}

	public String getString1() {
		return "String 1";
	}

	public String getString2() {
		return "String 2";
	}

	public Boolean boolean1 = true;
	public Boolean boolean2 = false;
	public List<Boolean> booleanList1 = asList(true, false, false, true);
	public List<Boolean> booleanList2 = asList(false, false, true, false);
	public final Date date1 = new Date(1465981200000L);
	public final Date date2 = new Date(1491244560000L);
	public final InputStream inputStream1 = new ByteArrayInputStream(
		"Input Stream 1".getBytes(UTF_8));
	public final InputStream inputStream2 = new ByteArrayInputStream(
		"Input Stream 2".getBytes(UTF_8));
	public final Number number1 = 1L;
	public final Number number2 = 2L;
	public List<Number> numberList1 = asList(1, 2, 3, 4, 5);
	public List<Number> numberList2 = asList(6, 7, 8, 9, 10);
	public List<String> stringList1 = asList("a", "b", "c", "d");
	public List<String> stringList2 = asList("e", "f", "g", "h");

	private final int _id;

}