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

package com.liferay.apio.architect.internal.body;

import static org.apache.commons.fileupload.servlet.ServletFileUpload.isMultipartContent;

import com.liferay.apio.architect.file.BinaryFile;
import com.liferay.apio.architect.form.Body;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.BadRequestException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 * Reads {@code "multipart/form-data"} as a {@link Body}.
 *
 * @author Alejandro Hernández
 * @review
 */
public class MultipartToBodyConverter {

	/**
	 * Reads a {@code "multipart/form"} HTTP request body into a {@link Body}
	 * instance or fails with a {@link BadRequestException} if the input is not
	 * a valid multipart form.
	 *
	 * @review
	 */
	public static Body multipartToBody(HttpServletRequest request) {
		if (!isMultipartContent(request)) {
			throw new BadRequestException(
				"Request body is not a valid multipart form");
		}

		FileItemFactory fileItemFactory = new DiskFileItemFactory();

		ServletFileUpload servletFileUpload = new ServletFileUpload(
			fileItemFactory);

		try {
			List<FileItem> fileItems = servletFileUpload.parseRequest(request);

			Iterator<FileItem> iterator = fileItems.iterator();

			Map<String, String> values = new HashMap<>();
			Map<String, BinaryFile> binaryFiles = new HashMap<>();
			Map<String, Map<Integer, String>> indexedValueLists =
				new HashMap<>();
			Map<String, Map<Integer, BinaryFile>> indexedFileLists =
				new HashMap<>();

			while (iterator.hasNext()) {
				FileItem fileItem = iterator.next();

				String name = fileItem.getFieldName();

				Matcher matcher = _arrayPattern.matcher(name);

				if (matcher.matches()) {
					int index = Integer.parseInt(matcher.group(2));

					String actualName = matcher.group(1);

					_storeFileItem(
						fileItem,
						value -> {
							Map<Integer, String> indexedMap =
								indexedValueLists.computeIfAbsent(
									actualName, __ -> new HashMap<>());

							indexedMap.put(index, value);
						},
						binaryFile -> {
							Map<Integer, BinaryFile> indexedMap =
								indexedFileLists.computeIfAbsent(
									actualName, __ -> new HashMap<>());

							indexedMap.put(index, binaryFile);
						});
				}
				else {
					_storeFileItem(
						fileItem, value -> values.put(name, value),
						binaryFile -> binaryFiles.put(name, binaryFile));
				}
			}

			Map<String, List<String>> valueLists = _flattenMap(
				indexedValueLists);

			Map<String, List<BinaryFile>> fileLists = _flattenMap(
				indexedFileLists);

			return Body.create(
				key -> Optional.ofNullable(values.get(key)),
				key -> Optional.ofNullable(valueLists.get(key)),
				key -> Optional.ofNullable(fileLists.get(key)),
				key -> Optional.ofNullable(binaryFiles.get(key)));
		}
		catch (FileUploadException | IndexOutOfBoundsException |
			   NumberFormatException e) {

			throw new BadRequestException(
				"Request body is not a valid multipart form", e);
		}
	}

	private static <T> Map<String, List<T>> _flattenMap(
		Map<String, Map<Integer, T>> indexedValueLists) {

		Set<Entry<String, Map<Integer, T>>> entries =
			indexedValueLists.entrySet();

		Stream<Entry<String, Map<Integer, T>>> stream = entries.stream();

		return stream.collect(
			Collectors.toMap(
				Entry::getKey,
				v -> {
					Map<Integer, T> map = v.getValue();

					return new ArrayList<>(map.values());
				}));
	}

	private static void _storeFileItem(
		FileItem fileItem, Consumer<String> valueConsumer,
		Consumer<BinaryFile> fileConsumer) {

		try {
			if (fileItem.isFormField()) {
				InputStream stream = fileItem.getInputStream();

				valueConsumer.accept(Streams.asString(stream));
			}
			else {
				BinaryFile binaryFile = new BinaryFile(
					fileItem.getInputStream(), fileItem.getSize(),
					fileItem.getContentType(), fileItem.getName());

				fileConsumer.accept(binaryFile);
			}
		}
		catch (IOException ioe) {
			throw new BadRequestException("Invalid body", ioe);
		}
	}

	private static final Pattern _arrayPattern = Pattern.compile(
		"([A-Z|a-z]+)\\[([0-9]+)]");

}