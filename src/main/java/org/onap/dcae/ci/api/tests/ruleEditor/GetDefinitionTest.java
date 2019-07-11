/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 - 2019 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dcae.ci.api.tests.ruleEditor;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.report.Report;

import org.onap.sdc.dcae.composition.restmodels.ruleeditor.EventTypeDefinitionUI;
import org.onap.sdc.dcae.composition.restmodels.ruleeditor.EventTypesByVersionUI;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GetDefinitionTest extends DcaeRestBaseTest {
	
	@DataProvider(name = "EventTypes")
	public static Object[][] getVesVersions() {
		Gson gson = new GsonBuilder().create();
		try {
			Report.log(Status.INFO, "Requesting VES event types");
			RestResponse response = DcaeRestClient.getVesEventTypes();
			Report.logDebug("Response", response);
			if (response.getStatusCode() != 200) {
				throw new Exception(response.toString());
			}
			EventTypesByVersionUI[] eventsByVersionList = gson.fromJson(response.getResponse(), EventTypesByVersionUI[].class);
			
			return Arrays.stream(eventsByVersionList)
				.flatMap(version -> version.getEventTypes().stream()
						.map( event -> new Object[] {version.getVersion(), event} )
				).toArray(Object[][]::new);
			
		} catch (Exception err) {
			Report.log(Status.ERROR, "Unable to get ves event types\nException: %s", err.toString());
			return new Object[][] {};
		}
	}
	
	
	/* Positive */
	
	@Test(dataProvider = "EventTypes")
	public void testParameterized(String version, String eventType) throws Exception {
		// arrange
		
		Report.log(Status.INFO, "testParameterized. version="+version+", eventType="+eventType);
		
		Gson gsonStrict = new GsonBuilder()
				.registerTypeAdapter(EventTypeDefinitionUI.class, new StrictJsonDeserializer<EventTypeDefinitionUI>())
				.create();
		
		// act
		RestResponse response = DcaeRestClient.getVesDataTypes(version, eventType);
		Report.logDebug("Response", response);
		// assert
		
		EventTypeDefinitionUI[] resBody = gson.fromJson(response.getResponse(), EventTypeDefinitionUI[].class);
		List<EventTypeDefinitionUI> resBodyFlat = flatten(Arrays.asList(resBody));
		Report.logDebug("resBodyFlat", gsonStrict.toJson(resBodyFlat));
		
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			
			softly.assertThat(resBodyFlat).extracting("name").as("name")
				.doesNotContain(StringUtils.EMPTY)
				.doesNotContainNull();
			
			softly.assertThat(resBodyFlat).extracting("id").as("id")
			.doesNotContain(StringUtils.EMPTY)
			.doesNotContainNull();
		});
	}
	
	
	/* Negative */
	
	
	
	
	/* Private Methods */
	
	public class StrictJsonDeserializer<T> implements JsonDeserializer<T> {
		private Gson gson = new Gson();

		@Override
		public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			try {
				Field[] declaredFields = Class.forName(typeOfT.getTypeName()).getDeclaredFields();
				Arrays.asList(declaredFields).stream()
					.map(field -> field.getName())
					.forEach(fieldName -> {
						if (json.getAsJsonObject().has(fieldName) == false) {
							throw new JsonParseException(String.format("Missing field '%s'", fieldName));
						}
					});
			} catch (SecurityException e) {
				throw new JsonParseException("unable to parse", e);
			} catch (ClassNotFoundException e) {
				throw new JsonParseException("unable to parse", e);
			}
			return gson.fromJson(json, typeOfT);
		}
		
	}
	
	
	private List<EventTypeDefinitionUI> flatten(List<EventTypeDefinitionUI> events) {
		List<EventTypeDefinitionUI> result = new ArrayList<EventTypeDefinitionUI>();
		events.stream().forEach(e -> {
			List<EventTypeDefinitionUI> children = e.getChildren();
			result.add(e); // add this node
			if (children != null) {
				result.addAll(flatten(children)); // add child nodes recursively
			}
		});
		return result;
	}
	
	/* Negative */
	
	
}
