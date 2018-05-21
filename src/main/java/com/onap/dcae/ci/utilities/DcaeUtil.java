package com.onap.dcae.ci.utilities;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.*;
import org.apache.commons.lang3.RandomStringUtils;

import org.json.simple.parser.ParseException;
import org.onap.sdc.dcae.composition.util.DcaeBeConstants;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.testng.Assert;

import com.onap.dcae.ci.entities.composition.items.DcaeComponents;
import org.onap.sdc.dcae.composition.services.Resource;
import org.onap.sdc.dcae.composition.services.Service;
import org.onap.sdc.dcae.composition.services.ThinService;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.aventstack.extentreports.Status;


public class DcaeUtil {
    public static Gson gson = new Gson();

    public static class CatalogReources {
        public static Vfcmt[] getAllReourcesFromAsdc() throws IOException{
            RestResponse allResources = DcaeRestClient.getAllVfcmts();
            return gson.fromJson(allResources.getResponse(), Vfcmt[].class);
        }

        public static Vfcmt getResourceByName(String resName) throws IOException{
            Vfcmt[] vfcmts = getAllReourcesFromAsdc();
            List<Vfcmt> vfcmtsStr = Arrays.stream(vfcmts).
                                    filter(item -> item.getName().equals(resName)).
                                    collect(Collectors.toList());

            return vfcmtsStr.get(0);
        }

        public static Vfcmt getOneResourceFromList(int index) throws IOException{
            Vfcmt[] vfcmtList = getAllReourcesFromAsdc();
            return vfcmtList[index];
        }

        public static String getComponentID(int index) throws IOException{
            Vfcmt vfcmt = getOneResourceFromList(0);
            return vfcmt.getUuid();
        }

        public static Resource createNewVfcmtObject() throws IOException{
            Resource res = new Resource();
            res.setResourceInstanceName("LiavNewVFCMT" + RandomStringUtils.randomAlphanumeric(20));
            res.setDescription("This is a test VFCMT");
            return res;
        }

        public static Vfcmt notCheckoutVFCMT() throws IOException{ /* TODO: remove this function and use instead in DcaeEntityClient.getCheckedoutVfcmt() */
            Vfcmt[] vfcmtList = getAllReourcesFromAsdc();
            List<Vfcmt> vfcmtsStr = Arrays.stream(vfcmtList).
                    filter(item -> (item.getLifecycleState().equals(DcaeTestConstants.Sdc.State.NOT_CERTIFIED_CHECKOUT) == false)).
                    collect(Collectors.toList());

            return vfcmtsStr.get(0);
        }

        public static Vfcmt vfcmtNotUserOwner(String user) throws IOException{
            Vfcmt[] vfcmtList = getAllReourcesFromAsdc();
            List<Vfcmt> vfcmtsStr = Arrays.stream(vfcmtList)
                    .filter(item -> (item.getLastUpdaterUserId().equals(user) == false))
                    .collect(Collectors.toList());

            return vfcmtsStr.get(0);
        }

    }

    public static class Services{
        public static ThinService[] getServices(String VFCMTId, String userId) throws IOException{
            RestResponse services = null;
            try{
                services = DcaeRestClient.getServices(VFCMTId, userId);
            }catch(Exception e){
                System.err.println("Exception occurred while trying to fetch all resources from SDC: "+e);
                return null;
            }
            Assert.assertTrue(services.getStatusCode().intValue() == 200);
            String response = services.getResponse();
            ThinService[] serviceList = gson.fromJson(response, ThinService[].class);
            return serviceList.length > 0 ? serviceList : null;
        }

        public static ThinService getOneService(String VFCMTId,int index, String userId) throws IOException {
            ThinService[] services = getServices(VFCMTId, userId);
            return services[index];
        }


        public static List<Resource> getVfListInstance(ThinService thinService) throws IOException {
            RestResponse serviceInstancRes = null;
            try{
                serviceInstancRes = DcaeRestClient.getServicesInstance(thinService.getUuid());
            }catch(Exception e){
                System.err.println("Exception occurred while trying to fetch List of VF instances from SDC service ("+thinService+"). Exception: "+e);
                return null;
            }
            Assert.assertTrue(serviceInstancRes.getStatusCode().intValue() == 200);
            String serviceInstancList = serviceInstancRes.getResponse();
            Service service = gson.fromJson(serviceInstancList, Service.class);
            return service.getResources();
        }

        public static Resource getOneVfInstance(ThinService thinService,int index) throws IOException {
            List<Resource> resources = getVfListInstance(thinService);
            return resources.get(index);
        }

    }


    public static class SdcElementsModelType{
        public static RestResponse getMsElements() throws IOException{
            return DcaeRestClient.getItem(DcaeTestConstants.Composition.Microservice);
        }



        public static JsonArray getSNMPModelItemFromSdc() throws Exception {
            RestResponse resMsElements = getMsElements();
            JsonParser jsonParser = new JsonParser();
            JsonObject responseJson = (JsonObject)jsonParser.parse(resMsElements.getResponse());
            JsonArray itemJsonArray = responseJson.get("data").getAsJsonObject().get("element").getAsJsonObject().get("items").getAsJsonArray();
            Report.logDebug("DCAE Components items", itemJsonArray);
            Service[] services = gson.fromJson(itemJsonArray, Service[].class);
            Report.log(Status.DEBUG, "Trying to find a certified VF which its name starts with supplement/map/enrich");
            List<String> collectIds = Arrays.stream(services)
                .filter(x -> DcaeBeConstants.LifecycleStateEnum.CERTIFIED == DcaeBeConstants.LifecycleStateEnum.valueOf(x.getLifecycleState())&& !x.getModels().isEmpty())
				.filter(x -> x.getName().toLowerCase().startsWith("supplement") || x.getName().toLowerCase().startsWith("map") || x.getName().toLowerCase().startsWith("enrich"))
                .map(Service::getUuid)
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
            DcaeComponents dcaeComponents = getDcaeComponents();
            return dcaeComponents.getData().getElement().getItems().get(itemNumber).getUuid();
        }

        public static DcaeComponents getDcaeComponents() throws IOException{
            RestResponse services = getMsElements();
            String response = services.getResponse();
            return gson.fromJson(response, DcaeComponents.class);
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



    /*	private JSONObject getServiceInstance(JSONObject service) throws IOException {
    RestResponse serviceInstancRes = DcaeRestClient.getServicesInstance(service.get("uuid").toString());
    Assert.assertTrue(serviceInstancRes.getStatusCode().intValue() == 200);
    String serviceInstancList = serviceInstancRes.getResponse();
    return (JSONObject) JSONValue.parse(serviceInstancList);
}*/

}
