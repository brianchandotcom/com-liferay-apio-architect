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

package com.liferay.apio.architect.internal.message.json.ld;

import static com.liferay.apio.architect.internal.action.Predicates.returnsAnyOf;
import static com.liferay.apio.architect.internal.message.json.ld.JSONLDMessageMapperUtil.getActionTypes;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.BOOLEAN;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.BOOLEAN_LIST;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.DATE;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.DATE_LIST;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.FILE;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.LINKED_MODEL;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.NESTED_MODEL_LIST;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.NUMBER;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.NUMBER_LIST;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.RELATED_COLLECTION;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.STRING;
import static com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType.STRING_LIST;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

import static java.lang.String.join;

import com.liferay.apio.architect.internal.action.ActionSemantics;
import com.liferay.apio.architect.internal.documentation.Documentation;
import com.liferay.apio.architect.internal.message.json.DocumentationMessageMapper;
import com.liferay.apio.architect.internal.message.json.JSONObjectBuilder;
import com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField;
import com.liferay.apio.architect.internal.wiring.osgi.manager.message.json.DocumentationField.FieldType;
import com.liferay.apio.architect.pagination.Page;

import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * Represents documentation in JSON-LD + Hydra format.
 *
 * <p>
 * For more information, see <a href="https://json-ld.org/">JSON-LD </a> and <a
 * href="https://www.hydra-cg.com/">Hydra </a> .
 * </p>
 *
 * @author Alejandro Hernández
 * @author Javier Gamarra
 * @author Zoltán Takács
 */
@Component(service = DocumentationMessageMapper.class)
public class JSONLDDocumentationMessageMapper
	implements DocumentationMessageMapper {

	@Override
	public String getMediaType() {
		return "application/ld+json";
	}

	@Override
	public void mapAction(
		JSONObjectBuilder jsonObjectBuilder, String resourceName, String type,
		ActionSemantics actionSemantics, String description) {

		jsonObjectBuilder.field(
			"@id"
		).stringValue(
			"_:" + join("/", resourceName, actionSemantics.name())
		);

		jsonObjectBuilder.field(
			"@type"
		).arrayValue(
		).addAllStrings(
			getActionTypes(actionSemantics.name())
		);

		jsonObjectBuilder.field(
			"method"
		).stringValue(
			actionSemantics.method()
		);

		jsonObjectBuilder.field(
			"returns"
		).stringValue(
			_getReturnValue(type, actionSemantics)
		);

		_addDescription(jsonObjectBuilder, description);
	}

	@Override
	public void mapDescription(
		JSONObjectBuilder jsonObjectBuilder, String description) {

		jsonObjectBuilder.field(
			"description"
		).stringValue(
			description
		);
	}

	@Override
	public void mapEntryPoint(
		JSONObjectBuilder jsonObjectBuilder, String entryPoint) {

		jsonObjectBuilder.field(
			"entrypoint"
		).stringValue(
			entryPoint
		);
	}

	@Override
	public void mapProperty(
		JSONObjectBuilder jsonObjectBuilder,
		DocumentationField documentationField, String description) {

		jsonObjectBuilder.field(
			"@type"
		).stringValue(
			"SupportedProperty"
		);

		jsonObjectBuilder.field(
			"property"
		).stringValue(
			documentationField.getName()
		);

		_addType(jsonObjectBuilder, documentationField);
		_addDescription(jsonObjectBuilder, description);
	}

	@Override
	public void mapResource(
		JSONObjectBuilder jsonObjectBuilder, String resourceType,
		String description) {

		jsonObjectBuilder.field(
			"@id"
		).stringValue(
			resourceType
		);

		jsonObjectBuilder.field(
			"@type"
		).stringValue(
			"Class"
		);

		jsonObjectBuilder.field(
			"title"
		).stringValue(
			resourceType
		);

		_addDescription(jsonObjectBuilder, description);
	}

	@Override
	public void mapResourceCollection(
		JSONObjectBuilder jsonObjectBuilder, String resourceType,
		String description) {

		jsonObjectBuilder.field(
			"@id"
		).stringValue(
			"vocab:" + resourceType + "Collection"
		);

		jsonObjectBuilder.field(
			"@type"
		).stringValue(
			"Class"
		);

		jsonObjectBuilder.field(
			"subClassOf"
		).stringValue(
			"Collection"
		);

		jsonObjectBuilder.field(
			"description"
		).stringValue(
			"A collection of " + resourceType
		);

		jsonObjectBuilder.field(
			"title"
		).stringValue(
			resourceType + "Collection"
		);

		Stream.of(
			"totalItems", "member", "numberOfItems"
		).forEach(
			fieldName -> {
				JSONObjectBuilder propertyJsonObjectBuilder =
					new JSONObjectBuilder();

				DocumentationField documentationField;

				if (fieldName.equals("member")) {
					documentationField = DocumentationField.of(
						fieldName, NESTED_MODEL_LIST);
				}
				else {
					documentationField = DocumentationField.of(
						fieldName, NUMBER);
				}

				mapProperty(
					propertyJsonObjectBuilder, documentationField, description);

				onFinishProperty(
					jsonObjectBuilder, propertyJsonObjectBuilder, fieldName);
			}
		);

		_addDescription(jsonObjectBuilder, description);
	}

	@Override
	public void mapTitle(JSONObjectBuilder jsonObjectBuilder, String title) {
		jsonObjectBuilder.field(
			"title"
		).stringValue(
			title
		);
	}

	@Override
	public void onFinish(
		JSONObjectBuilder jsonObjectBuilder, Documentation documentation) {

		jsonObjectBuilder.field(
			"@context"
		).arrayValue(
			arrayBuilder -> arrayBuilder.add(
				builder -> builder.field(
					"@vocab"
				).stringValue(
					"http://schema.org/"
				)
			),
			arrayBuilder -> arrayBuilder.addString(
				"https://www.w3.org/ns/hydra/core#"),
			arrayBuilder -> arrayBuilder.add(
				builder -> builder.field(
					"expects"
				).fields(
					nestedBuilder -> nestedBuilder.field(
						"@type"
					).stringValue(
						"@id"
					),
					nestedBuilder -> nestedBuilder.field(
						"@id"
					).stringValue(
						"hydra:expects"
					)
				),
				builder -> builder.field(
					"returns"
				).fields(
					nestedBuilder -> nestedBuilder.field(
						"@id"
					).stringValue(
						"hydra:returns"
					),
					nestedBuilder -> nestedBuilder.field(
						"@type"
					).stringValue(
						"@id"
					)
				)
			)
		);

		jsonObjectBuilder.field(
			"@id"
		).stringValue(
			"/doc"
		);

		jsonObjectBuilder.field(
			"@type"
		).stringValue(
			"ApiDocumentation"
		);
	}

	@Override
	public void onFinishAction(
		JSONObjectBuilder documentationJsonObjectBuilder,
		JSONObjectBuilder operationJsonObjectBuilder,
		ActionSemantics actionSemantics) {

		documentationJsonObjectBuilder.field(
			"supportedOperation"
		).arrayValue(
		).add(
			operationJsonObjectBuilder
		);
	}

	@Override
	public void onFinishProperty(
		JSONObjectBuilder documentationJsonObjectBuilder,
		JSONObjectBuilder propertyJsonObjectBuilder, String formField) {

		documentationJsonObjectBuilder.field(
			"supportedProperty"
		).arrayValue(
		).add(
			propertyJsonObjectBuilder
		);
	}

	@Override
	public void onFinishResource(
		JSONObjectBuilder documentationJsonObjectBuilder,
		JSONObjectBuilder resourceJsonObjectBuilder, String type) {

		documentationJsonObjectBuilder.field(
			"supportedClass"
		).arrayValue(
		).add(
			resourceJsonObjectBuilder
		);
	}

	private void _addDescription(
		JSONObjectBuilder documentationJsonObjectBuilder, String description) {

		if (description != null) {
			documentationJsonObjectBuilder.field(
				"comment"
			).stringValue(
				description
			);
		}
	}

	private void _addExtraType(
		JSONObjectBuilder jsonObjectBuilder, String collectionType) {

		jsonObjectBuilder.nestedField(
			"manages", "property"
		).stringValue(
			"rdf:type"
		);

		jsonObjectBuilder.nestedField(
			"manages", "object"
		).stringValue(
			collectionType
		);
	}

	private void _addType(
		JSONObjectBuilder jsonObjectBuilder,
		DocumentationField documentationField) {

		FieldType type = documentationField.getType();
		String typeString = null;

		if (BOOLEAN.equals(type)) {
			typeString = "boolean";
		}
		else if (BOOLEAN_LIST.equals(type)) {
			typeString = "collection";
			_addExtraType(jsonObjectBuilder, "boolean");
		}
		else if (DATE.equals(type)) {
			typeString = "date";
		}
		else if (DATE_LIST.equals(type)) {
			typeString = "collection";
			_addExtraType(jsonObjectBuilder, "date");
		}
		else if (FILE.equals(type)) {
			typeString = "file";
		}
		else if (LINKED_MODEL.equals(type)) {
			Optional<String> extraType = documentationField.getExtraType();

			typeString = extraType.orElse(null);
		}
		else if (NUMBER.equals(type)) {
			typeString = "number";
		}
		else if (NUMBER_LIST.equals(type)) {
			typeString = "collection";
			_addExtraType(jsonObjectBuilder, "number");
		}
		else if (RELATED_COLLECTION.equals(type)) {
			typeString = "collection";

			Optional<String> extraTypeOptional =
				documentationField.getExtraType();

			extraTypeOptional.ifPresent(
				extraType -> _addExtraType(jsonObjectBuilder, extraType)
			);
		}
		else if (STRING.equals(type)) {
			typeString = "string";
		}
		else if (STRING_LIST.equals(type)) {
			typeString = "collection";
			_addExtraType(jsonObjectBuilder, "string");
		}

		if (typeString != null) {
			jsonObjectBuilder.field(
				"rdf:type"
			).stringValue(
				typeString
			);
		}
	}

	private String _getReturnValue(
		String type, ActionSemantics actionSemantics) {

		return Match(
			actionSemantics
		).of(
			Case($(returnsAnyOf(Void.class)), _NOTHING),
			Case($(returnsAnyOf(Page.class)), "Collection"), Case($(), type)
		);
	}

	private static final String _NOTHING =
		"http://www.w3.org/2002/07/owl#Nothing";

}