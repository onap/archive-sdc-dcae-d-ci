package com.onap.dcae.ci.api.tests.composition;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.entities.composition.rightMenu.element.Item;
import com.onap.dcae.ci.entities.composition.rightMenu.elements.Element;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.Report;
import org.assertj.core.api.SoftAssertions;
import org.onap.sdc.dcae.composition.model.Model;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class CompositionElementsControllerTests extends DcaeRestBaseTest {
	
	JsonParser parser = new JsonParser();

	@Test 
	public void test_getAllElements() throws IOException{
		Report.log(Status.INFO, "test_getAllElements start");
		RestResponse response = DcaeRestClient.getElements();
		Report.log(Status.INFO, "getElements response=%s", response);
		JsonElement getElementJsonResponse = getElementsJson(response);
		Type listType = new TypeToken<List<Element>>(){}.getType();
		List<Element> responseData = gson.fromJson(getElementJsonResponse, listType);
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(getElementJsonResponse.toString()).isNotEmpty();
			softly.assertThat(responseData).extracting("itemId")
				.containsExactlyInAnyOrder("Policy","Utility","Microservice","Database","Collector","Analytics","Source");
		});
	}
	
	
	@DataProvider(name="item")
	public static Object[][] allElementsItems() throws IOException{
		RestResponse response = DcaeRestClient.getElements();
		JsonElement getElementJsonResponse = getElementsJson(response);
		Type listType = new TypeToken<List<Element>>(){}.getType();
		List<Element> responseData = gson.fromJson(getElementJsonResponse, listType);
		return responseData
			.stream()
			.map(x -> new Object[]{ x.getItemId() } )
			.collect(Collectors.toList())
			.toArray(new Object[responseData.size()][1]);
	}
	
	@Test(dataProvider ="item")
	public void test_getAllElementsByItemId(String itemName) throws IOException{
		Report.log(Status.INFO, "test_getAllElementsByItemId start");
		RestResponse response = DcaeRestClient.getItem(itemName);
		Report.log(Status.INFO, "getItem response=%s", response);
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
		});
	}
	
	@Test
	public void test_getModelData() throws IOException{
		Report.log(Status.INFO, "test_getModelData start");
		RestResponse responseGetElements = DcaeRestClient.getElements();
		JsonElement obj = getElementsJson(responseGetElements);	
		String elementItemName = getElementItemName(obj);

		RestResponse responseElementsItem = DcaeRestClient.getItem(elementItemName);
		JsonElement elementsById = parser.parse(responseElementsItem.getResponse());
		JsonElement itemData = elementsById.getAsJsonObject().get("data").getAsJsonObject().get("element").getAsJsonObject().get("items");
		
		String elemId = getElementItemID(itemData);
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
		RestResponse responseGetElements = DcaeRestClient.getElements();
		JsonElement obj = getElementsJson(responseGetElements);	
		String elementItemName = getElementItemName(obj);

		RestResponse responseElementsItem = DcaeRestClient.getItem(elementItemName);
		JsonElement elementsById = parser.parse(responseElementsItem.getResponse());
		JsonElement itemData = elementsById.getAsJsonObject().get("data").getAsJsonObject().get("element").getAsJsonObject().get("items");
		
		String elemId = getElementItemID(itemData);
		RestResponse responseModel = DcaeRestClient.getItemModel(elemId);
		JsonElement jsonRes = parser.parse(responseModel.getResponse());
		JsonElement jsonModel = jsonRes.getAsJsonObject().get("data").getAsJsonObject().get("model").getAsJsonObject();
		Model model = gson.fromJson(jsonModel, Model.class);
		List<String> nodesDataTypes = model.getNodes().stream()
			.map(y -> y.getType())
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
	
	/**************** nagative ***************/ 
	@Test 
	public void test_getAllElementsByNonExistItemId() throws IOException{
		Report.log(Status.INFO, "test_getAllElementsByNonExistItemId start");
		RestResponse response = DcaeRestClient.getItem("notExist");
		JsonElement elementsById = parser.parse(response.getResponse());
		JsonElement exception = elementsById.getAsJsonObject().get("error").getAsJsonObject().get("exception");
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
			softly.assertThat(exception.toString()).isNotEmpty();
		});
	}
	
	@Test
	public void test_getErrorNonExistingModelData() throws IOException{
		Report.log(Status.INFO, "test_getErrorNonExistingModelData start");
		RestResponse response = DcaeRestClient.getItemModel("notExist");
		JsonElement elementsById = parser.parse(response.getResponse());
		JsonElement exception = elementsById.getAsJsonObject().get("error");
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(500);
			softly.assertThat(exception.toString()).isNotEmpty();
		});
	}
	
	@Test
	public void test_getErrorNonExistingItemType() throws IOException{
		Report.log(Status.INFO, "test_getErrorNonExistingItemType start");
		RestResponse response = DcaeRestClient.getItemType("notExistId","nonType");
		JsonElement elementsById = parser.parse(response.getResponse());
		JsonElement exception = elementsById.getAsJsonObject().get("error");
		
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response.getStatusCode()).as("response status").isEqualTo(500);
			softly.assertThat(exception.toString()).isNotEmpty();
		});
	}
	
	/******************** private ********************/
	private static JsonElement getElementsJson(RestResponse response) {
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response.getResponse());
		JsonElement obj = element.getAsJsonObject().get("data").getAsJsonObject().get("elements");
		return obj;
	}
	
	private String getElementItemName(JsonElement obj) {
		Type listType = new TypeToken<List<Element>>(){}.getType();
		List<Element> fromJson = gson.fromJson(obj, listType);
		List<Element> collect = fromJson.stream().filter(x->x.getName().equals("Microservice")).collect(Collectors.toList());
		Element element = collect.get(0);
		String elementItemName = element.getName();
		return elementItemName;
	}
	
	private String getElementItemID(JsonElement data) {
		Type listType = new TypeToken<List<Item>>(){}.getType();
		List<Item> elementsItemFoi = gson.fromJson(data, listType);
		Report.log(Status.INFO, "getElementItemID for map");
		List<Item> foiItemData = elementsItemFoi.stream().filter(x->x.getName().equalsIgnoreCase("map")).collect(Collectors.toList());
		if(foiItemData!=null && foiItemData.size()>0){
			Item item = foiItemData.get(0);
			String elemId = item.getItemId();
			return elemId;
		}else{
			Report.log(Status.ERROR, "getElementItemID for map failed. Does the CI environment has map component in it??");
			return null;
		}
	}
}
