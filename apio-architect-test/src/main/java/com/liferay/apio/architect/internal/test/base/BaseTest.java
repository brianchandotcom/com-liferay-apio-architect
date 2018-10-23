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

package com.liferay.apio.architect.internal.test.base;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.Predicates.is;

import static java.util.function.Function.identity;

import static org.osgi.service.jaxrs.runtime.JaxrsServiceRuntimeConstants.JAX_RS_SERVICE_ENDPOINT;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.junit.After;
import org.junit.Before;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jaxrs.runtime.JaxrsServiceRuntime;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides utility methods for basic whiteboard and Apio operations and checks.
 * This class is the base class for integration tests.
 *
 * @author Alejandro Hernández
 */
public class BaseTest {

	@Before
	public void setUp() {
		_clientBuilderTracker = new ServiceTracker<>(
			_bundleContext, ClientBuilder.class, null);

		_clientBuilderTracker.open();

		_runtimeServiceReference = _bundleContext.getServiceReference(
			JaxrsServiceRuntime.class);
	}

	@After
	public void tearDown() {
		_clientBuilderTracker.close();
		_iterateAndExecute(_registrations, ServiceRegistration::unregister);
	}

	/**
	 * Registers a new service with the provided properties. Returns the service
	 * registration.
	 *
	 * <p>
	 * Warning! This method must be only used in a specific test lifecycle
	 * (inside methods annotated with {@link Before}, {@link After} or {@link
	 * org.junit.Test}.
	 * </p>
	 *
	 * @param  serviceClass the service class
	 * @param  service the service
	 * @param  properties the service properties
	 * @return the service registration
	 * @review
	 */
	protected <T> ServiceRegistration<T> beforeTestRegisterAs(
		Class<T> serviceClass, T service, Map<String, Object> properties) {

		ServiceRegistration<T> serviceRegistration =
			_bundleContext.registerService(
				serviceClass, service,
				properties.toJavaMap(Hashtable::new, identity()));

		_registrations.add(serviceRegistration);

		return serviceRegistration;
	}

	/**
	 * Returns a new {@code ClientBuilder} instance by using the bundle context
	 * to retrieve the registered client builder implementation.
	 */
	protected Client createClient() {
		return Try.of(
			() -> _clientBuilderTracker.waitForService(5000)
		).map(
			ClientBuilder::build
		).getOrElseThrow(
			t -> new AssertionError("Unable to create a valid ClientBuilder", t)
		);
	}

	/**
	 * Creates a {@link WebTarget} containing the path in which the JAX-RS
	 * Whiteboard is listening to requests.
	 *
	 * @review
	 */
	protected WebTarget createDefaultTarget() {
		Client client = createClient();

		return Try.of(
			() -> _runtimeServiceReference.getProperty(JAX_RS_SERVICE_ENDPOINT)
		).map(
			_TO_LIST
		).filter(
			endpoints -> endpoints.size() != 0,
			() -> new IllegalStateException(
				"Unable to find endpoints in \"" + JAX_RS_SERVICE_ENDPOINT +
					"\" property")
		).map(
			Traversable::head
		).map(
			client::target
		).getOrElseThrow(
			t -> new AssertionError("Unable to create a valid WebTarget", t)
		);
	}

	private static <T> void _iterateAndExecute(
		Collection<T> collection, Consumer<T> consumer) {

		Iterator<T> iterator = collection.iterator();

		while (iterator.hasNext()) {
			Try.of(
				iterator::next
			).andThen(
				consumer
			).andFinally(
				iterator::remove
			);
		}
	}

	@SuppressWarnings({"Convert2MethodRef", "unchecked"})
	private static final Function<Object, List<String>> _TO_LIST = v -> Match(
		v
	).of(
		Case($(is(null)), List.empty()),
		Case($(instanceOf(String[].class)), array -> List.of(array)),
		Case($(instanceOf(Iterable.class)), iterable -> List.ofAll(iterable)),
		Case($(), value -> List.of(value.toString()))
	);

	private static final BundleContext _bundleContext = FrameworkUtil.getBundle(
		BaseTest.class).getBundleContext();

	private ServiceTracker<ClientBuilder, ClientBuilder> _clientBuilderTracker;
	private final Collection<ServiceRegistration<?>> _registrations =
		new ArrayList<>();
	private ServiceReference<JaxrsServiceRuntime> _runtimeServiceReference;

}