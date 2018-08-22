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

package com.liferay.apio.architect.sample.internal.resource;

import static com.liferay.apio.architect.sample.internal.auth.PermissionChecker.hasPermission;

import com.liferay.apio.architect.credentials.Credentials;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.sample.internal.dao.PersonModelService;
import com.liferay.apio.architect.sample.internal.dto.PersonModel;
import com.liferay.apio.architect.sample.internal.form.PersonForm;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides all the information necessary to expose <a
 * href="http://schema.org/Person">Person</a> resources through a web API. The
 * resources are mapped from the internal {@link PersonModel} model.
 *
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class PersonCollectionResource {

	public PersonModel addPerson(
		PersonForm personForm, Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		return _personModelService.create(
			personForm.getPostalAddressModel(), personForm.getImage(),
			personForm.getBirthDate(), personForm.getEmail(),
			personForm.getGivenName(), personForm.getJobTitles(),
			personForm.getFamilyName());
	}

	public void deletePerson(long id, Credentials credentials) {
		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		_personModelService.remove(id);
	}

	public PageItems<PersonModel> getPageItems(Pagination pagination) {
		List<PersonModel> personModels = _personModelService.getPage(
			pagination.getStartPosition(), pagination.getEndPosition());
		int count = _personModelService.getCount();

		return new PageItems<>(personModels, count);
	}

	public PersonModel getPerson(long id) {
		Optional<PersonModel> optional = _personModelService.get(id);

		return optional.orElseThrow(
			() -> new NotFoundException("Unable to get person " + id));
	}

	public PersonModel updatePerson(
		long id, PersonForm personForm, Credentials credentials) {

		if (!hasPermission(credentials)) {
			throw new ForbiddenException();
		}

		Optional<PersonModel> optional = _personModelService.update(
			personForm.getPostalAddressModel(), personForm.getImage(),
			personForm.getBirthDate(), personForm.getEmail(),
			personForm.getGivenName(), personForm.getJobTitles(),
			personForm.getFamilyName(), id);

		return optional.orElseThrow(
			() -> new NotFoundException("Unable to get person " + id));
	}

	@Reference
	private PersonModelService _personModelService;

}