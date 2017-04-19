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

package com.liferay.vulcan.wiring.osgi;

import com.liferay.vulcan.contributor.APIContributor;
import com.liferay.vulcan.wiring.osgi.internal.ServiceReferenceServiceTuple;

import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true, service = URIResolver.class)
public class URIResolver {

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void setServiceReference(
		ServiceReference<APIContributor> serviceReference) {

		APIContributor apiContributor = _bundleContext.getService(
			serviceReference);

		_addAPIContributor(serviceReference, apiContributor);
	}

	protected void unsetServiceReference(
		ServiceReference<APIContributor> serviceReference) {
	}

	private void _addAPIContributor(
		ServiceReference<APIContributor> serviceReference,
		APIContributor apiContributor) {

		TreeSet<ServiceReferenceServiceTuple<APIContributor>>
			serviceReferenceServiceTuples = _apiContributors.computeIfAbsent(
				apiContributor.getPath(), name -> new TreeSet<>());

		ServiceReferenceServiceTuple<APIContributor>
			serviceReferenceServiceTuple = new ServiceReferenceServiceTuple<>(
				serviceReference, apiContributor);

		serviceReferenceServiceTuples.add(serviceReferenceServiceTuple);
	}

	private final Map<String,
		TreeSet<ServiceReferenceServiceTuple<APIContributor>>>
			_apiContributors = new ConcurrentHashMap<>();
	private final BundleContext _bundleContext = FrameworkUtil.getBundle(
		URIResolver.class).getBundleContext();

}