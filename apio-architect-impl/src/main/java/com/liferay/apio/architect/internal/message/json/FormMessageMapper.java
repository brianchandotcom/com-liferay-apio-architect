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

package com.liferay.apio.architect.internal.message.json;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.form.FormField;

/**
 * Maps {@code Form} data to its JSON object representation. Instances of this
 * interface work like events. The form's {@code
 * javax.ws.rs.ext.MessageBodyWriter} calls the form message mapper's methods.
 * In each method, developers should only map the provided part of the resource
 * to its JSON object representation. To enable this, each method receives a
 * {@link JSONObjectBuilder}.
 *
 * <p>
 * The {@link #onFinish} method is called when the writer finishes writing the
 * form. Otherwise, the form message mapper's methods aren't called in a
 * particular order.
 * </p>
 *
 * @author Alejandro Hernández
 */
public interface FormMessageMapper extends MessageMapper<Form> {

	/**
	 * Maps the form's description to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param description the form description
	 */
	public default void mapFormDescription(
		JSONObjectBuilder jsonObjectBuilder, String description) {
	}

	/**
	 * Maps the form field to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param formField the form field
	 */
	public default void mapFormField(
		JSONObjectBuilder jsonObjectBuilder, FormField formField) {
	}

	/**
	 * Maps the form field to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param formField the form field
	 * @param nestedJsonObjectBuilder the nested form
	 */
	public default void mapFormField(
		JSONObjectBuilder jsonObjectBuilder, FormField formField,
		JSONObjectBuilder nestedJsonObjectBuilder) {
	}

	/**
	 * Maps the form's title to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param title the form's title
	 */
	public default void mapFormTitle(
		JSONObjectBuilder jsonObjectBuilder, String title) {
	}

	/**
	 * Maps the form URL to its JSON object representation.
	 *
	 * @param jsonObjectBuilder the JSON object builder for the form
	 * @param url the form's URL
	 */
	public default void mapFormURL(
		JSONObjectBuilder jsonObjectBuilder, String url) {
	}

}