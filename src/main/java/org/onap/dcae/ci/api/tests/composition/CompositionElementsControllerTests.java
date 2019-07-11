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

package org.onap.dcae.ci.api.tests.composition;

import com.aventstack.extentreports.Status;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.assertj.core.api.SoftAssertions;
import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.utilities.DcaeTestConstants;
import org.onap.sdc.dcae.composition.model.Model;
import org.onap.sdc.dcae.composition.model.Node;
import org.onap.sdc.dcae.composition.restmodels.canvas.DcaeComponentCatalog;
import org.onap.sdc.dcae.composition.restmodels.sdc.Resource;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CompositionElementsControllerTests extends DcaeRestBaseTest {
	
	JsonParser parser = new JsonParser();

	@Test 
	public void test_getAllElements() throws IOException{
		Report.log(Status.INFO, "test_getAllElements start");
		RestResponse response = DcaeRestClient.getCatalog();
		Report.log(Status.INFO, "getElements response=%s", response);
		DcaeComponentCatalog getCatalogResponse = gson.fromJson(response.getResponse(), DcaeComponentCatalog.class);
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(getCatalogResponse.toString()).isNotEmpty();
			softly.assertThat(Arrays.asList("Policy","Utility","Microservice","Database","Collector","Analytics","Source"))
					.containsAll(getCatalogResponse.getElements().stream().map(DcaeComponentCatalog.SubCategoryFolder::getItemId).collect(Collectors.toList()));
		});
	}
	
	@Test
	public void test_getModelData() throws IOException{
		Report.log(Status.INFO, "test_getModelData start");
		DcaeComponentCatalog getCatalogResponse = gson.fromJson(DcaeRestClient.getCatalog().getResponse(), DcaeComponentCatalog.class);
		List<Resource> itemsData = getCatalogResponse.getElements().stream().filter(p -> DcaeTestConstants.Composition.Microservice.equals(p.getItemId())).findAny().get().getItems();
		String elemId = itemsData.stream().filter(p -> "map".equalsIgnoreCase(p.getName())).findAny().get().getUuid();
		Report.log(Status.INFO, "test_getModelData start");
		
		RestResponse response = DcaeRestClient.getItemModel(elemId);
		Report.log(Status.INFO, "getItemModel response=%s", response);
		JsonElement responseJson = parser.parse(response.getResponse());
		JsonElement itemModelData = responseJson.getAsJsonObject().get("data").getAsJsonObject().get("model");
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(itemModelData.toString()).isNotEmpty();
		});
	}
	
	@Test
	public void test_getTypeData() throws IOException{
		Report.log(Status.INFO, "test_getTypeData start");
		DcaeComponentCatalog getCatalogResponse = gson.fromJson(DcaeRestClient.getCatalog().getResponse(), DcaeComponentCatalog.class);
		List<Resource> itemsData = getCatalogResponse.getElements().stream().filter(p -> DcaeTestConstants.Composition.Microservice.equals(p.getItemId())).findAny().get().getItems();
		String elemId = itemsData.stream().filter(p -> "map".equalsIgnoreCase(p.getName())).findAny().get().getUuid();
		RestResponse responseModel = DcaeRestClient.getItemModel(elemId);
		JsonElement jsonRes = parser.parse(responseModel.getResponse());
		JsonElement jsonModel = jsonRes.getAsJsonObject().get("data").getAsJsonObject().get("model").getAsJsonObject();
		Model model = gson.fromJson(jsonModel, Model.class);
		List<String> nodesDataTypes = model.getNodes().stream()
			.map(Node::getType)
			.collect(Collectors.toList());
		
		nodesDataTypes.forEach(z -> Report.log(Status.INFO, "All types inside model: " + z));
		String nodeType = nodesDataTypes.get(0);
		RestResponse response = DcaeRestClient.getItemType(elemId, nodeType);
		JsonElement elementsById2 = parser.parse(response.getResponse());
		JsonElement data2 = elementsById2.getAsJsonObject().get("data").getAsJsonObject().get("type").getAsJsonObject();
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(data2.toString()).isNotEmpty();
		});
	}
	
	/**************** negative ***************/
	
	@Test
	public void test_getErrorNonExistingModelData() throws IOException{
		Report.log(Status.INFO, "test_getErrorNonExistingModelData start");
		RestResponse response = DcaeRestClient.getItemModel("notExist");
		JsonElement error = parser.parse(response.getResponse());
		JsonElement exception = error.getAsJsonObject().get("requestError");
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(500);
			softly.assertThat(exception.toString()).isNotEmpty();
		});
	}
	
	@Test
	public void test_getErrorNonExistingItemType() throws IOException{
		Report.log(Status.INFO, "test_getErrorNonExistingItemType start");
		RestResponse response = DcaeRestClient.getItemType("notExistId","nonType");
		JsonElement error = parser.parse(response.getResponse());
		JsonElement exception = error.getAsJsonObject().get("requestError");
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(500);
			softly.assertThat(exception.toString()).isNotEmpty();
		});
	}

}
