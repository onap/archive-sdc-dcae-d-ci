package com.onap.dcae.ci.api.tests.composition;

import static org.assertj.core.api.Assertions.*;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.DcaeTestConstants;
import com.onap.dcae.ci.utilities.DcaeUtil;
import com.onap.dcae.ci.utilities.Report;
import org.onap.sdc.dcae.composition.model.ModelDcae;
import org.testng.annotations.*;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openecomp.d2.ci.datatypes.http.RestResponse;

import com.aventstack.extentreports.Status;


public class CompositionMicroServicesApiTests extends DcaeRestBaseTest {
	
	@Test
	public void getAllElementsTest() throws IOException, ParseException{
		Report.log(Status.INFO, "getAllElementsTest start");
		RestResponse services = DcaeRestClient.getElements();
		Report.log(Status.INFO, "getAllElementsTest response=%s", services);
		assertThat(services.getStatusCode().intValue()).isEqualTo(200);
//		System.out.println(services);
		String response = services.getResponse();
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(response);
		JSONObject ele = (JSONObject) o.get("data");
		String arrString = ele.get(("elements")).toString();
//		System.out.println(arrString);
		assertThat(arrString)
			.as("Check that elements not empty")
			.isNotEmpty();
	}
	
	@Test
	public void getMsElementsTest() throws IOException, ParseException{
		Report.log(Status.INFO, "getMsElementsTest start");
		RestResponse services = DcaeUtil.SdcElementsModelType.getMsElements();
		Report.log(Status.INFO, "getMsElementsTest response=%s", services);
		assertThat(services.getStatusCode().intValue()).isEqualTo(200);
//		System.out.println(services);
		String response = services.getResponse();
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(response);
		JSONObject ele = (JSONObject) o.get("data");
		String arrString = ele.get(("element")).toString();
//		System.out.println(arrString);
		assertThat(arrString)
			.as("Check that elements not empty")
			.isNotEmpty();
	}
	
	@Test
	public void getItemModelTest() throws IOException{
		Report.log(Status.INFO, "getItemModelTest start");
		RestResponse itemModelRes = DcaeUtil.SdcElementsModelType.getItemModelFromSdc(0);
		Report.log(Status.INFO, "getItemModelTest response=%s", itemModelRes);
		assertThat(itemModelRes.getStatusCode().intValue()).isEqualTo(200);
		
		String response = itemModelRes.getResponse();
		JSONObject object = (JSONObject) JSONValue.parse(response);
		String errorMsg = object.get("error").toString();
		assertThat(errorMsg).isEqualTo(DcaeTestConstants.Composition.EMPTY_OBJECT);
	}
	
	@Test
	public void getItemTypeTest() throws IOException{
		Report.log(Status.INFO, "getItemModelTest start");
		RestResponse itemModelRes = DcaeUtil.SdcElementsModelType.getItemModelFromSdc(0);
		Report.log(Status.INFO, "itemModelRes response=%s", itemModelRes);
		String response = itemModelRes.getResponse();
		
		String uuid = DcaeUtil.SdcElementsModelType.getItemUuid(0);
		
		ModelDcae model = gson.fromJson(response, ModelDcae.class);
		Report.log(Status.INFO, "model "+model);
		String type = model.getData().getModel().getNodes().get(0).getType();
		Report.log(Status.INFO, "type "+type);

		RestResponse services = DcaeRestClient.getItemType(uuid, type);
		assertThat(services.getStatusCode().intValue()).isEqualTo(200);
	}
	
//	@Test
//	public void saveCompositionCheckoutConflictTest() throws IOException{
//		// pull checkout VFCMT
//		Vfcmt checkoutVFCMT = DcaeUtil.CatalogReources.checkoutVFCMT();
//		// call API
//		RestResponse saveComposition = DcaeRestClient.saveComposition(checkoutVFCMT.getUuid(), checkoutVFCMT.getLastUpdaterUserId());
//		System.out.println(saveComposition);
//		assertThat(saveComposition.getStatusCode())
//			.as("status 500")
//			.isEqualTo(500);
//	}
//
//	@Test
//	public void saveCompositionTest() throws IOException{
//		// Create new VFCMT
//		Resource createNewVfcmtObject = DcaeUtil.CatalogReources.createNewVfcmtObject();
//		// Call SDC API to create the new VFCMT
//		RestResponse resNewVfcmt = DcaeRestClient.createVfcmt(createNewVfcmtObject.getResourceInstanceName(),createNewVfcmtObject.getDescription());
//		
//		Vfcmt newVfcmt = gson.fromJson(resNewVfcmt.getResponse(), Vfcmt.class);
//		
//		// call api
//		RestResponse saveComposition = DcaeRestClient.saveComposition(newVfcmt.getUuid(), newVfcmt.getLastUpdaterUserId());
//		System.out.println(saveComposition);
//		String response = saveComposition.getResponse();
//		
//		String msg = "Artifact created";
//		int errorIndex = response.indexOf(msg);
//		assertThat(errorIndex)
//			.as("Conflict of checkout")
//			.isGreaterThan(-1);
//	}
	
//	@Test
//	public void submitCompositionTest() throws IOException{
//		// pull checkout VFCMT
//		Vfcmt checkoutVFCMT = DcaeUtil.CatalogReources.checkoutVFCMT();
//		String componentId = checkoutVFCMT.getUuid();
//		/* TODO: create new service (requires SDC api support) instead of using a random one that already exists */
//		ThinService oneService = DcaeUtil.Services.getOneService(componentId,0);
//		Resource oneVfInstance = DcaeUtil.Services.getOneVfInstance(oneService, 0);
//		RestResponse submitCompositionRes = DcaeRestClient
//				.submitComposition(componentId, oneService.getUuid(), oneVfInstance.getResourceInstanceName());
//		String response = submitCompositionRes.getResponse();
//		int errorIndex = response.indexOf(DcaeTestConstants.Sdc.State.CONFLICT);
//		assertThat("Conflict of checkout",errorIndex, greaterThan(-1));
//	}

//TODO Block need to get shai service
//	@Test
//	public void submitCompositionTest() throws IOException{
//		String componentId = DcaeUtil.CatalogReources.getComponentID(202);
//		ThinService oneService = DcaeUtil.Services.getOneService(0);
//		Resource oneVfInstance = DcaeUtil.Services.getOneVfInstance(oneService, 0);
//		RestResponse submitCompositionRes = DcaeRestClient
//				.submitComposition(componentId, oneService.getUuid(), oneVfInstance.getResourceInstanceName());
//		String response = submitCompositionRes.getResponse();
//		JSONObject object = (JSONObject) JSONValue.parse(response);
//		assertThat("Success response is not null", object.get("successResponse"), is(notNullValue()));
//	}

}
