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

package com.liferay.vulcan.sample.internal.model;

import com.github.javafaker.DateAndTime;
import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import com.github.javafaker.Name;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Instances of this class represents a person. This is a mock class for sample
 * purposes. It contains methods for retrieving/updating/deleting people and a
 * in-memory database with fake data.
 *
 * @author Alejandro Hernández
 * @review
 */
public class Person {

	/**
	 * Adds a new {@code Person} to the database.
	 *
	 * @param  firstName the first name of the person.
	 * @param  lastName the last name of the person.
	 * @param  email the email of the person.
	 * @param  address the address of the person.
	 * @param  jobTitle the job title of the person.
	 * @param  birthDate the birth date of the person.
	 * @param  avatar the avatar of the person.
	 * @return the added {@code Person}.
	 * @review
	 */
	public static Person addPerson(
		String firstName, String lastName, String email, String address,
		String jobTitle, Date birthDate, String avatar) {

		long id = _count.incrementAndGet();

		Person person = new Person(
			id, firstName, lastName, email, address, jobTitle, birthDate,
			avatar);

		_people.put(id, person);

		return person;
	}

	/**
	 * Deletes a {@code Person} with a certain {@code ID} from the database.
	 *
	 * @param  id the ID of the person to delete.
	 * @review
	 */
	public static void deletePerson(long id) {
		_people.remove(id);
	}

	/**
	 * Returns a page of {@code Person} from the database.
	 *
	 * @param  start the start position.
	 * @param  end the end position.
	 * @return the list of people between {@code start} and {@code end}.
	 * @review
	 */
	public static List<Person> getPeople(int start, int end) {
		Collection<Person> people = _people.values();

		Stream<Person> stream = people.stream();

		return stream.skip(
			start
		).limit(
			end
		).collect(
			Collectors.toList()
		);
	}

	/**
	 * Return the total number of people in the database.
	 *
	 * @return the total number of people in the database.
	 * @review
	 */
	public static int getPeopleCount() {
		return _people.size();
	}

	/**
	 * Returns a {@code Person} with a certain {@code ID} from the database if
	 * present. Returns {@code Optional#empty()} otherwise.
	 *
	 * @param  id the ID of the person to retrieve.
	 * @return the {@code Person} for the requested ID if present; {@code
	 *         Optional#empty()} otherwise.
	 * @review
	 */
	public static Optional<Person> getPerson(long id) {
		Person person = _people.get(id);

		return Optional.ofNullable(person);
	}

	/**
	 * Updates a {@code Person} with a certain {@code ID} in the database if
	 * present.
	 *
	 * @param  id the ID of the person to update.
	 * @param  firstName the first name of the person.
	 * @param  lastName the last name of the person.
	 * @param  email the email of the person.
	 * @param  address the address of the person.
	 * @param  jobTitle the job title of the person.
	 * @param  birthDate the birth date of the person.
	 * @param  avatar the avatar of the person.
	 * @return the updated {@code Person} if present; {@code Optional#empty()}
	 *         otherwise.
	 * @review
	 */
	public static Optional<Person> updatePerson(
		long id, String firstName, String lastName, String email,
		String address, String jobTitle, Date birthDate, String avatar) {

		Person person = _people.get(id);

		if (person == null) {
			return Optional.empty();
		}

		person = new Person(
			id, firstName, lastName, email, address, jobTitle, birthDate,
			avatar);

		_people.put(id, person);

		return Optional.of(person);
	}

	/**
	 * The address of this {@code Person}.
	 *
	 * @return the address of the person.
	 * @review
	 */
	public String getAddress() {
		return _address;
	}

	/**
	 * Returns the avatar of this {@code Person}.
	 *
	 * @return the avatar of the person.
	 * @review
	 */
	public String getAvatar() {
		return _avatar;
	}

	/**
	 * The birth date of this {@code Person}.
	 *
	 * @return the birth date of the person.
	 * @review
	 */
	public Date getBirthDate() {
		return _birthDate;
	}

	/**
	 * Returns the email of this {@code Person}.
	 *
	 * @return the email of the person.
	 * @review
	 */
	public String getEmail() {
		return _email;
	}

	/**
	 * Returns the first name of this {@code Person}.
	 *
	 * @return the first name of the person.
	 * @review
	 */
	public String getFirstName() {
		return _firstName;
	}

	/**
	 * Returns the full name of this {@code Person}.
	 *
	 * @return the last name of the person.
	 * @review
	 */
	public String getFullName() {
		return _firstName + " " + _lastName;
	}

	/**
	 * The ID of this {@code Person}.
	 *
	 * @return the ID of the person.
	 * @review
	 */
	public long getId() {
		return _id;
	}

	/**
	 * The job title of this {@code Person}.
	 *
	 * @return the job title of the person.
	 * @review
	 */
	public String getJobTitle() {
		return _jobTitle;
	}

	/**
	 * Returns the last name of this {@code Person}.
	 *
	 * @return the last name of the person.
	 * @review
	 */
	public String getLastName() {
		return _lastName;
	}

	private Person(
		long id, String firstName, String lastName, String email,
		String address, String jobTitle, Date birthDate, String avatar) {

		_id = id;
		_firstName = firstName;
		_lastName = lastName;
		_email = email;
		_address = address;
		_jobTitle = jobTitle;
		_birthDate = birthDate;
		_avatar = avatar;
	}

	private static final AtomicLong _count = new AtomicLong(10);
	private static Map<Long, Person> _people;

	static {
		_people = new HashMap<>();

		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.YEAR, -21);

		Date adultDate = calendar.getTime();

		for (long i = 0; i < 10; i++) {
			Faker faker = new Faker();

			Name name = faker.name();

			Internet internet = faker.internet();

			DateAndTime dateAndTime = faker.date();

			String firstName = name.firstName();
			String lastName = name.lastName();
			String email = internet.safeEmailAddress();
			String address = faker.address().fullAddress();
			String jobTitle = name.title();

			Date birthDate = dateAndTime.past(10000, TimeUnit.DAYS, adultDate);

			String avatar = internet.avatar();

			Person person = new Person(
				i, firstName, lastName, email, address, jobTitle, birthDate,
				avatar);

			_people.put(i, person);
		}
	}

	private final String _address;
	private final String _avatar;
	private final Date _birthDate;
	private final String _email;
	private final String _firstName;
	private final long _id;
	private final String _jobTitle;
	private final String _lastName;

}