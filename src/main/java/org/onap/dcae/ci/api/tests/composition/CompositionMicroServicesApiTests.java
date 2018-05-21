package org.onap.dcae.ci.api.tests.composition;

import static org.assertj.core.api.Assertions.*;

import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.utilities.DcaeRestClient;
import org.onap.dcae.ci.utilities.DcaeTestConstants;
import org.onap.dcae.ci.utilities.DcaeUtil;
import org.onap.dcae.ci.report.Report;
import org.onap.sdc.dcae.composition.model.ModelDcae;
import org.testng.annotations.*;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import com.aventstack.extentreports.Status;


public class CompositionMicroServicesApiTests extends DcaeRestBaseTest {
	
	@Test
	public void getAllElementsTest() throws IOException, ParseException{
		Report.log(Status.INFO, "getAllElementsTest start");
		RestResponse services = DcaeRestClient.getElements();
		Report.log(Status.INFO, "getAllElementsTest response=%s", services);
		assertThat(services.getStatusCode().intValue()).isEqualTo(200);
		String response = services.getResponse();
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(response);
		JSONObject ele = (JSONObject) o.get("data");
		String arrString = ele.get("elements").toString();
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
		String response = services.getResponse();
		JSONParser parser = new JSONParser();
		JSONObject o = (JSONObject) parser.parse(response);
		JSONObject ele = (JSONObject) o.get("data");
		String arrString = ele.get("element").toString();
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

}
