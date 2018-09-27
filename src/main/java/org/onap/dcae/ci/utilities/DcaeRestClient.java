package org.onap.dcae.ci.utilities;

import com.aventstack.extentreports.Status;
import com.google.common.net.UrlEscapers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.collections4.map.SingletonMap;
import org.json.simple.JSONObject;
import org.onap.dcae.ci.config.Configuration;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.enums.HttpHeaderEnum;
import org.onap.dcae.ci.report.Report;
import org.onap.sdc.dcae.composition.restmodels.CreateVFCMTRequest;

import java.io.IOException;

public class DcaeRestClient extends BaseRestUtils {

	private static String designerDefaultId = "cs0008";
	private static String designer2UserId = "me0009";
	private static String adminDefaultId = "jh0003";
	private static String testerDefaultId = "jm0007";
	private static Configuration configuration = ConfigurationReader.getConfiguration();

    public static String getDefaultUserId() {
        return designerDefaultId;
    }

	public static String getDesigner2UserId() {
		return designer2UserId;
	}

	public static String getDefaultAdminId() {
    	return adminDefaultId;
	}

	public static String getDefaultTesterId() {
    	return testerDefaultId;
	}

    protected static String getApiUrl(String path) {

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
        return sendGet(getApiUrl("/getResourcesByCategory"), designerDefaultId);
    }

    public static RestResponse getAllMonitoringTemplatesVfcmts() throws IOException {
        return sendGet(getApiUrl("/getResourcesByMonitoringTemplateCategory"), designerDefaultId);
    }

    public static RestResponse getVfcmtsForMigration(String contextType,String serviceUuid, String serviceVersion) throws IOException{

        return sendGet(getApiUrl("/" + contextType + "/" + serviceUuid + "/" + serviceVersion + "/getVfcmtsForMigration"), designerDefaultId);
    }

    public static RestResponse createVfcmt(String name, String description, String userId) throws IOException {
        JSONObject newVfcmtJSON = newVfcmtJSON(name, description);
        return sendPost(getApiUrl("/createVFCMT"), newVfcmtJSON.toString(), userId, "application/json");
    }

    public static RestResponse createMc(String request) throws IOException {
        return sendPost(getApiUrl("/createMC"), request, designerDefaultId, "application/json");
    }

    public static RestResponse createMc(String request,String userId) throws IOException {
        return sendPost(getApiUrl("/createMC"), request, userId, "application/json");
    }

    public static RestResponse createVfcmt(String name, String description) throws IOException{
        return createVfcmt(name, description, designerDefaultId);
    }

    public static RestResponse importMc(String request) throws IOException {
        return sendPost(getApiUrl("/importMC"), request, designerDefaultId, "application/json");
    }

    public static RestResponse getAttachedService(String vfcmtUuid) throws IOException {
        Report.log(Status.INFO, "getAttachedService for VFCMT uuid="+vfcmtUuid);
        RestResponse res = sendGet(getApiUrl("/" + vfcmtUuid + "/attachment"), designerDefaultId);
        Report.log(Status.INFO, "getAttachedService result=%s", res);
        return res;
    }

    public static RestResponse getServiceExternalReferences(String serviceUuid, String version) throws IOException {
        Report.log(Status.INFO, "getServiceExternalReferences for service uuid="+serviceUuid);
        RestResponse res = sendGet(getApiUrl("/SERVICE/" + serviceUuid + "/" + version + "/monitoringComponents"), designerDefaultId);
        Report.log(Status.INFO, "getServiceExternalReferences result=%s", res);
        return res;
    }
    /* VF */

    public static RestResponse getServices(String VFCMTId, String userId) throws IOException{
        return sendGet(getApiUrl("/services/"+VFCMTId), userId);
    }

    public static RestResponse getServicesInstance(String uuid) throws IOException{
        return sendGet(getApiUrl("/service/"+uuid), designerDefaultId);
    }

    public static RestResponse attachVfiRef(String vfcmtUuid, String serviceId, String vfiName) throws IOException{
        Report.log(Status.INFO, "attachVfiRef start");
        JSONObject jsonAttachObj = new JSONObject();
        jsonAttachObj.put("serviceUuid", serviceId);
        jsonAttachObj.put("instanceName", vfiName);

        return sendPost(getApiUrl("/" + vfcmtUuid + "/attachment"), jsonAttachObj.toString(), designerDefaultId, "application/json");
    }

    public static RestResponse getResourceDetails(String componentId) throws IOException{
        return sendGet(getApiUrl("/resource/"+ componentId), designerDefaultId);
    }

	public static RestResponse getCatalog() throws IOException{
		return sendGet(getApiUrl("/catalog"), designerDefaultId);
	}

    public static RestResponse getItemModel(String elementId) throws IOException{
        return sendGet(getApiUrl("/"+ elementId +"/model"), designerDefaultId);
    }
    public static RestResponse getItemType(String elementId, String type) throws IOException{
        return sendGet(getApiUrl("/"+ elementId +"/type/"+ type +"/"), designerDefaultId);
    }
    public static RestResponse saveComposition(String componentId, String userId) throws IOException{
        JsonObject json = generateCdumpInput(componentId);
        return saveComposition(componentId, userId, json.toString());
    }

    // edit composition new flow - service context
	public static RestResponse saveComposition(String serviceUuid, String vfiName, String vfcmtUuid, String body) throws IOException{
		return sendPost(getApiUrl(String.format("/service/%s/%s/saveComposition/%s", serviceUuid, UrlEscapers.urlFragmentEscaper().escape(vfiName), vfcmtUuid)), body, designerDefaultId, "application/json");
	}

	// submit composition new flow - service context
	public static RestResponse submitComposition(String serviceUuid, String vfiName, String vfcmtUuid) throws IOException {
		return sendPost(getApiUrl(String.format("/service/createBluePrint/%s/%s/%s", vfcmtUuid, serviceUuid, UrlEscapers.urlFragmentEscaper().escape(vfiName))), "", designerDefaultId, "application/json");
	}

    public static RestResponse saveComposition(String componentId, String userId, String body) throws IOException{
        return sendPost(getApiUrl("/saveComposition/"+componentId), body, userId, "application/json");
    }

    public static RestResponse getComposition(String componentId) throws IOException{
        return sendGet(getApiUrl("/getComposition/"+ componentId), designerDefaultId);
    }
    public static RestResponse submitComposition(String componentId, String serviceUuid, String vnfiName, String monitoringType) throws IOException{
        return sendPost(getApiUrl("/createBluePrint/"+ componentId +"/"+ serviceUuid +"/"+ UrlEscapers.urlFragmentEscaper().escape(vnfiName) + "/"+  UrlEscapers.urlFragmentEscaper().escape(monitoringType)), "" ,designerDefaultId, "application/json");
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
        return sendGet(getApiUrl("/rule-editor/list-events-by-versions"), designerDefaultId);
    }

    public static RestResponse getVesDataTypes(String vesVersion, String eventType) throws IOException {
        return sendGet(getApiUrl(String.format("/rule-editor/definition/%s/%s", vesVersion, eventType)), designerDefaultId);
    }

    public static RestResponse saveRule(String vfcmtUid, String dcaeCompName, String nid, String configParam, String body) throws IOException {
        return sendPost(getApiUrl(String.format("/rule-editor/rule/%s/%s/%s/%s", vfcmtUid, dcaeCompName, nid, configParam)), body, designerDefaultId, "application/json" );
    }

    public static RestResponse getRules(String vfcmtUid, String dcaeCompName, String nid, String configParam) throws IOException {
        return sendGet(getApiUrl(String.format("/rule-editor/rule/%s/%s/%s/%s", vfcmtUid, dcaeCompName, nid, configParam)), designerDefaultId);
    }

    public static RestResponse deleteRule(String vfcmtUid, String dcaeCompName, String nid, String configParam, String ruleUid) throws IOException {
        return sendDelete(getApiUrl(String.format("/rule-editor/rule/%s/%s/%s/%s/%s", vfcmtUid, dcaeCompName, nid, configParam, ruleUid)), designerDefaultId);
    }

	public static RestResponse deleteGroupOfRules(String vfcmtUid, String dcaeCompName, String nid, String configParam, String groupId) throws IOException {
		return sendDelete(getApiUrl(String.format("/rule-editor/group/%s/%s/%s/%s/%s", vfcmtUid, dcaeCompName, nid, configParam, groupId)), designerDefaultId);
	}

	public static RestResponse translateRules(String request) throws IOException {
		return sendPost(getApiUrl("/rule-editor/rule/translate"), request, designerDefaultId, "application/json");
	}

    public static RestResponse getExistingRuleTargets(String vfcmtUuid, String dcaeCompName, String nid) throws IOException {
        String url = getApiUrl(String.format("/rule-editor/getExistingRuleTargets/%s/%s/%s", vfcmtUuid, dcaeCompName, nid));
        return sendGet(url, designerDefaultId);
    }

	public static RestResponse exportRules(String vfcmtUuid, String dcaeCompName, String nid, String configParam) throws IOException {
		String url = getApiUrl(String.format("/rule-editor/export/%s/%s/%s/%s", vfcmtUuid, dcaeCompName, nid, configParam));
		return sendGet(url, designerDefaultId, new SingletonMap<>(HttpHeaderEnum.ACCEPT.getValue(), "application/octet-stream"));
	}

	public static RestResponse importRules(String vfcmtUuid, String dcaeCompName, String nid, String configParam, String request, boolean supportGroups) throws IOException {
		String url = getApiUrl(String.format("/rule-editor/import/%s/%s/%s/%s/%s", vfcmtUuid, dcaeCompName, nid, configParam, supportGroups));
		return sendPost(url, request, designerDefaultId, "application/json");
	}

	public static RestResponse importPhase(String request) throws IOException {
		String url = getApiUrl("/rule-editor/importPhase");
		return sendPost(url, request, designerDefaultId, "application/json");
	}

	public static RestResponse applyFilter(String request) throws IOException {
		String url = getApiUrl("/rule-editor/applyFilter");
		return sendPost(url, request, designerDefaultId, "application/json");
	}

	public static RestResponse deleteFilter(String request) throws IOException {
		String url = getApiUrl("/rule-editor/deleteFilter");
		return sendPost(url, request, designerDefaultId, "application/json");
	}

	public static RestResponse getLatestMcUuid(String contextType, String serviceUuid, String vfiName, String vfcmtUuid) throws IOException {
    	return sendGet(getApiUrl(String.format("/%s/%s/%s/%s/getLatestMcUuid", contextType, serviceUuid, UrlEscapers.urlFragmentEscaper().escape(vfiName), vfcmtUuid)), designerDefaultId);
	}

	// Configuration

	public static RestResponse getConfiguredFlowTypes() throws IOException {
		return sendGet(getApiUrl("/conf/composition"), designerDefaultId);
	}

	public static RestResponse getConfiguredPhasesByFlowType(String flowType) throws IOException {
		return sendGet(getApiUrl(String.format("/conf/getPhases/%s", flowType)), designerDefaultId);
	}

    // TOSCA LAB //

	public static RestResponse getToscaLabHealthCheck() throws IOException {
		return new HttpRequest().httpSendGet(configuration.getToscaLabUrl().concat("/healthcheck"), null);
	}

	public static RestResponse translateModelToBlueprint(String payload) throws IOException {
		return new HttpRequest().httpSendPost(configuration.getToscaLabUrl().concat("/translate"), payload);
	}

	private static JSONObject newVfcmtJSON(String name, String description) {
		JSONObject json = new JSONObject();
		json.put("name", name);
		json.put("description", description);
		return json;
	}

	public static <T extends CreateVFCMTRequest> void fillCreateMcRequestMandatoryFields(T request) {
		request.setFlowType("flowType_xxx");
		request.setContextType("SERVICE");
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
