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

package com.liferay.vulcan.sample.rest.internal.users.representor;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.representor.builder.RepresentorBuilder;

import java.text.DateFormat;

import java.util.function.Function;

import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(
	immediate = true,
	service = {ModelRepresentorMapper.class, PersonModelRepresentorMapper.class}
)
public class PersonModelRepresentorMapper
	implements ModelRepresentorMapper<User> {

	@Override
	public void buildRepresentor(RepresentorBuilder<User> representorBuilder) {
		DateFormat iso8601Format = DateUtil.getISO8601Format();

		Function<User, Object> getBirthDate = user -> {
			try {
				return iso8601Format.format(user.getBirthday());
			}
			catch (PortalException pe) {
				throw new ServerErrorException(500, pe);
			}
		};

		RepresentorBuilder.FirstStep<User> firstStep =
			representorBuilder.addIdentifier(user ->
				String.valueOf(user.getUserId()));

		firstStep.addType("Person");

		firstStep.addField("additionalName", User::getMiddleName);
		firstStep.addField("birthDate", getBirthDate);
		firstStep.addField("email", User::getEmailAddress);
		firstStep.addField("familyName", User::getLastName);
		firstStep.addField("givenName", User::getFirstName);
		firstStep.addField("name", User::getFullName);
		firstStep.addField("jobTitle", User::getJobTitle);
	}

}