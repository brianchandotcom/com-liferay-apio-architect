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

package com.liferay.apio.architect.internal.jaxrs.resource;

import static com.liferay.apio.architect.internal.action.Predicates.isActionFor;
import static com.liferay.apio.architect.internal.action.Predicates.isActionNamed;
import static com.liferay.apio.architect.internal.action.Predicates.isCreateAction;
import static com.liferay.apio.architect.internal.action.Predicates.isReplaceAction;

import com.liferay.apio.architect.form.Form;
import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.annotation.ActionManager;
import com.liferay.apio.architect.resource.Resource.GenericParent;
import com.liferay.apio.architect.resource.Resource.Item;
import com.liferay.apio.architect.resource.Resource.Nested;
import com.liferay.apio.architect.resource.Resource.Paged;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Declares the resource that serves form information.
 *
 * @author Alejandro Hernández
 * @review
 */
@Component(
	immediate = true,
	property = {
		"osgi.jaxrs.application.select=(liferay.apio.architect.application=true)",
		"osgi.jaxrs.resource=true"
	},
	service = Object.class
)
@Path("/f")
public class FormResource {

	/**
	 * Returns the create action's {@link Form} for the specified {@link Paged}
	 * resource.
	 *
	 * @review
	 */
	@GET
	@Path("c/{name}")
	public Form creatorForm(@PathParam("name") String name) {
		return _getFormFromActionSemanticsMatching(
			isCreateAction.and(isActionFor(Paged.of(name))));
	}

	/**
	 * Returns the specified action's {@link Form} for the specified {@link
	 * Paged} or {@link Item} resource.
	 *
	 * @review
	 */
	@GET
	@Path("p/{name}/{actionName}")
	public Form customRouteForm(
		@PathParam("name") String name,
		@PathParam("actionName") String actionName) {

		Predicate<ActionSemantics> isCurrentAction = isActionNamed(actionName);
		Predicate<ActionSemantics> isPaged = isActionFor(Paged.of(name));
		Predicate<ActionSemantics> isItem = isActionFor(Item.of(name));

		return _getFormFromActionSemanticsMatching(
			isCurrentAction.and(isPaged.or(isItem)));
	}

	/**
	 * Returns the create action's {@link Form} for the specified {@link Nested}
	 * or {@link GenericParent} resource.
	 *
	 * @review
	 */
	@GET
	@Path("c/{parent}/{name}")
	public Form nestedCreatorForm(
		@PathParam("parent") String parent, @PathParam("name") String name) {

		Predicate<ActionSemantics> isGenericParent = isActionFor(
			GenericParent.of(parent, name));
		Predicate<ActionSemantics> isNested = isActionFor(
			Nested.of(Item.of(parent), name));

		return _getFormFromActionSemanticsMatching(
			isCreateAction.and(isGenericParent.or(isNested)));
	}

	/**
	 * Returns the update action's {@link Form} for the specified {@link Item}
	 * resource.
	 *
	 * @review
	 */
	@GET
	@Path("u/{name}")
	public Form updaterForm(@PathParam("name") String name) {
		return _getFormFromActionSemanticsMatching(
			isReplaceAction.and(isActionFor(Item.of(name))));
	}

	@Reference
	protected ActionManager actionManager;

	private Form _getFormFromActionSemanticsMatching(
		Predicate<ActionSemantics> predicate) {

		Stream<ActionSemantics> stream = actionManager.actionSemantics();

		Optional<ActionSemantics> optional = stream.filter(
			predicate
		).findFirst();

		return optional.flatMap(
			ActionSemantics::getFormOptional
		).orElseThrow(
			NotFoundException::new
		);
	}

}