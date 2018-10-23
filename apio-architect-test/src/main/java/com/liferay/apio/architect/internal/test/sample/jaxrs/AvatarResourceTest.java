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

import com.liferay.apio.architect.internal.test.base.BaseTest;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.ClientBuilder;
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

		JSONObject jsonObject = new JSONObject(
			response.readEntity(String.class));

		String expected = "Unable to find the image of user with id 9999";

		assertThat(jsonObject.getString("@type"), is("not-found"));
		assertThat(jsonObject.getString("description"), is(expected));
		assertThat(jsonObject.getNumber("statusCode"), is(404));
		assertThat(jsonObject.getString("title"), is("Not Found"));
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
		ClientBuilder clientBuilder = getClientBuilder();

		return clientBuilder.build(
		).target(
			WHITEBOARD_URI
		).path(
			"images/{id}"
		).resolveTemplate(
			"id", id
		).request(
		).get();
	}

}