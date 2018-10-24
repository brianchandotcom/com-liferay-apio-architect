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

package com.liferay.apio.architect.internal.test.sample.jaxrs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;

import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONObjectAs;

import com.liferay.apio.architect.internal.test.base.BaseTest;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import org.junit.Test;

/**
 * Test suite for {@link
 * com.liferay.apio.architect.sample.internal.jaxrs.AvatarResource} class.
 *
 * @author Alejandro Hernández
 */
public class AvatarResourceTest extends BaseTest {

	@Test
	public void testAvatarResourceReturns404OnNonexistentAvatar() {
		Response response = _requestAvatarForId(9999);

		assertThat(response.getStatus(), is(404));

		JSONObject expected = createJSONObject(
			builder -> builder.put(
				"@type", "not-found"
			).put(
				"description", "Unable to find the image of user with id 9999"
			).put(
				"statusCode", 404
			).put(
				"title", "Not Found"
			));

		assertThat(asJSONObject(response), is(sameJSONObjectAs(expected)));
	}

	@Test
	public void testAvatarResourceReturnsResponseOnExistentAvatar()
		throws IOException {

		Response response = _requestAvatarForId(0);

		InputStream inputStream = (InputStream)response.getEntity();

		assertThat(response.getStatus(), is(200));
		assertThat(response.getMediaType(), is(MediaType.valueOf("image/jpg")));

		assertThat(inputStream.available(), is(greaterThan(0)));
	}

	private Response _requestAvatarForId(int id) {
		WebTarget webTarget = createDefaultTarget();

		return webTarget.path(
			"images/{id}"
		).resolveTemplate(
			"id", id
		).request(
		).get();
	}

}