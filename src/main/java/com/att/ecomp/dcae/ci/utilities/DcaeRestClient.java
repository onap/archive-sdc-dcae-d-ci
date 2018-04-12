package com.att.ecomp.dcae.ci.utilities;

import com.att.ecomp.dcae.ci.ui.setup.DcaeConfiguration;
import com.aventstack.extentreports.Status;
import com.google.common.net.UrlEscapers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.simple.JSONObject;
import org.onap.sdc.dcae.composition.restmodels.CreateVFCMTRequest;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.openecomp.d2.ci.api.BaseRestUtils;
import org.openecomp.d2.ci.api.ElementFactory;
import org.openecomp.d2.ci.datatypes.User;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.openecomp.d2.ci.datatypes.http.RestResponse;

import java.io.IOException;

public class DcaeRestClient extends BaseRestUtils {

    private static User defaultUser = ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER);

    public static User getDefaultUser() {
        return defaultUser;
    }

    protected static String getApiUrl(String path) {
        DcaeConfiguration configuration = ConfigurationReader.getConfiguration();

        String dcaeBePort = configuration.getDcaeBePort();
        String dcaeBeHost = configuration.getDcaeBeHost();
        String apiPath = configuration.getApiPath();
        if(System.getProperty("dcaeBeHost")!=null){
            dcaeBeHost = System.getProperty("dcaeBeHost");
            System.out.println("dcaeBeHost was configured via system property: "+dcaeBeHost);
        }
        if(System.getProperty("dcaeBePort")!=null){
            dcaeBePort = System.getProperty("dcaeBePort");
            System.out.println("dcaeBePort was configured via system property: "+dcaeBePort);
        }
        if(System.getProperty("apiPath")!=null){
            apiPath = System.getProperty("apiPath");
            System.out.println("apiPath was configured via system property: "+apiPath);
        }

        return String.format("%s:%s%s%s", dcaeBeHost, dcaeBePort, apiPath, path);
    }

    /* HealthCheck */

    public static RestResponse getHealthcheck() throws IOException {
        return sendGet(getApiUrl("/healthCheck"), null);
    }

    /* VFCMT */

    public static RestResponse getAllVfcmts() throws IOException {
        return sendGet(getApiUrl("/getResourcesByCategory"), defaultUser.getUserId());
    }

    public static RestResponse getAllMonitoringTemplatesVfcmts() throws IOException {
        return sendGet(getApiUrl("/getResourcesByMonitoringTemplateCategory"), defaultUser.getUserId());
    }

    public static RestResponse getVfcmtsForMigration(String contextType,String serviceUuid, String serviceVersion) throws IOException{

        return sendGet(getApiUrl("/" + contextType + "/" + serviceUuid + "/" + serviceVersion + "/getVfcmtsForMigration"), defaultUser.getUserId());
    }

    public static RestResponse createVfcmt(String name, String description, String userId) throws IOException {
        JSONObject newVfcmtJSON = newVfcmtJSON(name, description);
        return sendPost(getApiUrl("/createVFCMT"), newVfcmtJSON.toString(), userId, "application/json");
    }

    public static RestResponse createMc(String request) throws IOException {
        return sendPost(getApiUrl("/createMC"), request, defaultUser.getUserId(), "application/json");
    }

    public static RestResponse createMc(String request,String userId) throws IOException {
        return sendPost(getApiUrl("/createMC"), request, userId, "application/json");
    }

    public static RestResponse createVfcmt(String name, String description) throws IOException{
        return createVfcmt(name, description, defaultUser.getUserId());
    }

    public static RestResponse importMc(String request) throws IOException {
        return sendPost(getApiUrl("/importMC"), request, defaultUser.getUserId(), "application/json");
    }

    public static RestResponse getAttachedService(String vfcmtUuid) throws IOException {
        Report.log(Status.INFO, "getAttachedService for VFCMT uuid="+vfcmtUuid);
        RestResponse res = sendGet(getApiUrl("/" + vfcmtUuid + "/attachment"), defaultUser.getUserId());
        Report.log(Status.INFO, "getAttachedService result=%s", res);
        return res;
    }

    public static RestResponse getServiceExternalReferences(String serviceUuid, String version) throws IOException {
        Report.log(Status.INFO, "getServiceExternalReferences for service uuid="+serviceUuid);
        RestResponse res = sendGet(getApiUrl("/services/" + serviceUuid + "/" + version + "/monitoringComponents"), defaultUser.getUserId());
        Report.log(Status.INFO, "getServiceExternalReferences result=%s", res);
        return res;
    }

    /* VF */

    public static RestResponse getServices(String VFCMTId, String userId) throws IOException{
        return sendGet(getApiUrl("/services/"+VFCMTId), userId);
    }

    public static RestResponse getServicesInstance(String uuid) throws IOException{
        return sendGet(getApiUrl("/service/"+uuid), defaultUser.getUserId());
    }

    public static RestResponse attachVfiRef(String vfcmtUuid, String serviceId, String vfiName) throws IOException{
        Report.log(Status.INFO, "attachVfiRef start");
        JSONObject jsonAttachObj = new JSONObject();
        jsonAttachObj.put("serviceUuid", serviceId);
        jsonAttachObj.put("instanceName", vfiName);

        return sendPost(getApiUrl("/" + vfcmtUuid + "/attachment"), jsonAttachObj.toString(), defaultUser.getUserId(), "application/json");
    }

    public static RestResponse getResourceDetails(String componentId) throws IOException{
        return sendGet(getApiUrl("/resource/"+ componentId), defaultUser.getUserId());
    }


    public static RestResponse getElements() throws IOException{
        return sendGet(getApiUrl("/elements"), defaultUser.getUserId());
    }
    public static RestResponse getItem(String element) throws IOException{
        return sendGet(getApiUrl("/"+ element +"/elements"), defaultUser.getUserId());
    }
    public static RestResponse getItemModel(String elementId) throws IOException{
        return sendGet(getApiUrl("/"+ elementId +"/model"), defaultUser.getUserId());
    }
    public static RestResponse getItemType(String elementId, String type) throws IOException{
        return sendGet(getApiUrl("/"+ elementId +"/type/"+ type +"/"), defaultUser.getUserId());
    }
    public static RestResponse saveComposition(String componentId, String userId) throws IOException{
        JsonObject json = generateCdumpInput(componentId);
        return saveComposition(componentId, userId, json.toString());
    }

    // edit composition new flow - service context
	public static RestResponse saveComposition(String serviceUuid, String vfiName, String vfcmtUuid, String body) throws IOException{
		return sendPost(getApiUrl(String.format("/services/%s/%s/saveComposition/%s", serviceUuid, UrlEscapers.urlFragmentEscaper().escape(vfiName), vfcmtUuid)), body, defaultUser.getUserId(), "application/json");
	}

	// submit composition new flow - service context
	public static RestResponse submitComposition(String serviceUuid, String vfiName, String vfcmtUuid) throws IOException {
		return sendPost(getApiUrl(String.format("/services/createBluePrint/%s/%s/%s", vfcmtUuid, serviceUuid, UrlEscapers.urlFragmentEscaper().escape(vfiName))), "", defaultUser.getUserId(), "application/json");
	}

    public static RestResponse saveComposition(String componentId, String userId, String body) throws IOException{
        return sendPost(getApiUrl("/saveComposition/"+componentId), body, userId, "application/json");
    }

    public static RestResponse getComposition(String componentId) throws IOException{
        return sendGet(getApiUrl("/getComposition/"+ componentId), defaultUser.getUserId());
    }
    public static RestResponse submitComposition(String componentId, String serviceUuid, String vnfiName, String monitoringType) throws IOException{
        return sendPost(getApiUrl("/createBluePrint/"+ componentId +"/"+ serviceUuid +"/"+ UrlEscapers.urlFragmentEscaper().escape(vnfiName) + "/"+  UrlEscapers.urlFragmentEscaper().escape(monitoringType)), "" ,defaultUser.getUserId(), "application/json");
    }

    /* Life Cycle */

    public static RestResponse checkinVfcmt(String vfcmtUuid, String userId) throws IOException {
        return checkinGeneral("vfcmt", vfcmtUuid, userId);
    }

    public static RestResponse checkinGeneral(String assetType, String vfcmtUuid, String userId) throws IOException {
        return sendPut(getApiUrl(String.format("/checkin/%s/%s", assetType, vfcmtUuid)), null, userId, null);
    }

    public static RestResponse checkoutVfcmt(String vfcmtUuid, String userId) throws IOException {
        return checkoutGeneral("vfcmt", vfcmtUuid, userId);
    }

    private static RestResponse checkoutGeneral(String assetType, String vfcmtUuid, String userId) throws IOException {
        return sendPut(getApiUrl(String.format("/checkout/%s/%s", assetType, vfcmtUuid)), null, userId, null);
    }

    public static RestResponse certifyVfcmt(String vfcmtUuid, String userId) throws IOException {
        return sendPut(getApiUrl(String.format("/certify/vfcmt/%s", vfcmtUuid)), null,  userId, null);
    }

    /* Rule Editor */

    public static RestResponse getVesEventTypes() throws IOException {
        return sendGet(getApiUrl("/rule-editor/list-events-by-versions"), defaultUser.getUserId());
    }

    public static RestResponse getVesDataTypes(String vesVersion, String eventType) throws IOException {
        return sendGet(getApiUrl(String.format("/rule-editor/definition/%s/%s", vesVersion, eventType)), defaultUser.getUserId());
    }

    public static RestResponse saveRule(String vfcmtUid, String dcaeCompName, String nid, String configParam, String body) throws IOException {
        return sendPost(getApiUrl(String.format("/rule-editor/rule/%s/%s/%s/%s", vfcmtUid, dcaeCompName, nid, configParam)), body, defaultUser.getUserId(), "application/json" );
    }

    public static RestResponse getRules(String vfcmtUid, String dcaeCompName, String nid, String configParam) throws IOException {
        return sendGet(getApiUrl(String.format("/rule-editor/rule/%s/%s/%s/%s", vfcmtUid, dcaeCompName, nid, configParam)), defaultUser.getUserId());
    }

    public static RestResponse deleteRule(String vfcmtUid, String dcaeCompName, String nid, String configParam, String ruleUid) throws IOException {
        return sendDelete(getApiUrl(String.format("/rule-editor/rule/%s/%s/%s/%s/%s", vfcmtUid, dcaeCompName, nid, configParam, ruleUid)), defaultUser.getUserId());
    }

    public static RestResponse translateRules(String vfcmtUid, String dcaeCompName, String nid, String configParam, String flowType) throws IOException {
        return sendGet(getApiUrl(String.format("/rule-editor/rule/translate/%s/%s/%s/%s?flowType=%s", vfcmtUid, dcaeCompName, nid, configParam, flowType)), defaultUser.getUserId());
    }


	private static JSONObject newVfcmtJSON(String name, String description) {
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("description", description);
		return json;
	}

	public static <T extends CreateVFCMTRequest> void fillCreateMcRequestMandatoryFields(T request) {
		request.setFlowType("flowType_xxx");
		request.setContextType("services");
		request.setName(StringUtils.randomString("CI-", 20));
		request.setDescription("create test vfcmt");
		if(null == request.getVfiName()) {
			request.setVfiName("whatsInAName");
		}
		if(null == request.getServiceUuid()) {
			request.setServiceUuid("service5659860");
		}
	}

	public static JsonObject generateCdumpInput(String componentId) {
		JsonObject json = new JsonObject();
		json.addProperty("cid", componentId);
		json.addProperty("version", 0);
		json.add("nodes", new JsonArray());
		json.add("relations", new JsonArray());
		json.add("inputs", new JsonArray());
		json.add("outputs", new JsonArray());
		return json;

	}
}
