package org.onap.dcae.ci.utilities;

import com.aventstack.extentreports.Status;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.report.Report;
import org.onap.sdc.dcae.composition.restmodels.canvas.DcaeComponentCatalog;
import org.onap.sdc.dcae.composition.restmodels.sdc.Resource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class DcaeUtil {
    public static Gson gson = new Gson();

    public static class SdcElementsModelType{
        public static DcaeComponentCatalog.SubCategoryFolder getMsElements() throws IOException{
			DcaeComponentCatalog getCatalogResponse = gson.fromJson(DcaeRestClient.getCatalog().getResponse(), DcaeComponentCatalog.class);
			return getCatalogResponse.getElements().stream().filter(p -> DcaeTestConstants.Composition.Microservice.equals(p.getItemId())).findAny().get();
        }

        public static JsonArray getSNMPModelItemFromSdc() throws Exception {

            List<Resource> itemList = getMsElements().getItems();
            Report.logDebug("DCAE Components items", itemList);
            Report.log(Status.DEBUG, "Trying to find a certified VF which its name starts with supplement/map/enrich");
            List<String> collectIds = itemList.stream()
				.filter(x -> x.getName().toLowerCase().startsWith("supplement") || x.getName().toLowerCase().startsWith("map") || x.getName().toLowerCase().startsWith("enrich"))
                .map(Resource::getUuid)
                .collect(Collectors.toList());
            if(collectIds.isEmpty()) {
                Report.log(Status.WARNING, "Could not find any SNMP DCAE Component");
            }
            JsonArray models = new JsonArray();
            for (String id : collectIds) {
                RestResponse res = DcaeRestClient.getItemModel(id);
                models.addAll(parseNodesFromDcaeModelAndAssignUiNid(res.getResponse()));
            }

            return models;
        }

        public static RestResponse getItemModelFromSdc(int itemNumber) throws IOException{
            String uuid = getItemUuid(itemNumber);
            return DcaeRestClient.getItemModel(uuid);
        }

        public static String getItemUuid(int itemNumber) throws IOException{
            DcaeComponentCatalog.SubCategoryFolder dcaeComponents = getMsElements();
            return dcaeComponents.getItems().get(itemNumber).getUuid();
        }
    }

    public static JsonArray parseNodesFromDcaeModelAndAssignUiNid(String response) throws ParseException {
        JsonParser parser = new JsonParser();
        JsonObject data = parser.parse(response).getAsJsonObject().get("data").getAsJsonObject();
        JsonObject model = data.get("model").getAsJsonObject();
        JsonArray nodes = model.get("nodes").getAsJsonArray();
        nodes.forEach((n) ->
            n.getAsJsonObject().add("nid", n.getAsJsonObject().get("name"))
        );
        Report.logDebug("nodes after adding ui nid", nodes);
        return nodes;
    }

    public static String generateCdumpFromSnmpModels(String vfcmtId) throws Exception {
        JsonArray snmpModelItemFromSdc = SdcElementsModelType.getSNMPModelItemFromSdc();
        JsonObject cdump = DcaeRestClient.generateCdumpInput(vfcmtId);
        cdump.add("nodes", snmpModelItemFromSdc);
        Report.logDebug("cdump", cdump);
        return cdump.toString();
    }

	public static String getValueFromJsonResponse(String response, String fieldName) {
		try {
			JSONObject jsonResp = (JSONObject) JSONValue.parse(response);
			Object fieldValue = jsonResp.get(fieldName);
			return fieldValue.toString();

		} catch (Exception e) {
			return null;
		}

	}
}
