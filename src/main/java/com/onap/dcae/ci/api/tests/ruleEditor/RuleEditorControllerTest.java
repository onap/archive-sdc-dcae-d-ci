package com.onap.dcae.ci.api.tests.ruleEditor;

import com.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import com.onap.dcae.ci.entities.rule_editor.SaveRuleError;
import com.onap.dcae.ci.utilities.DcaeRestClient;
import com.onap.dcae.ci.utilities.Report;
import org.onap.sdc.dcae.composition.services.Artifact;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import org.onap.sdc.dcae.composition.restmodels.ruleeditor.MappingRules;
import org.onap.sdc.dcae.composition.restmodels.ruleeditor.Rule;
import org.openecomp.d2.ci.api.ElementFactory;
import org.openecomp.d2.ci.datatypes.UserRoleEnum;
import org.testng.annotations.Test;
import org.assertj.core.api.SoftAssertions;
import org.openecomp.d2.ci.datatypes.http.RestResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.fail;

import java.util.List;

public class RuleEditorControllerTest extends DcaeRestBaseTest {

	private String ruleRequestBody =
			"{version:4.1,eventType:syslogFields,description:newRule,actions:[{from:{value:fromField,regex:\"\"},target:event.commonEventHeader.target,id:id,actionType:copy},"
					+ "{actionType:concat,from:{values:[{value:concat1},{value:_concat2}]},target:concatTargetField},{actionType:copy,from:{value:extractFromHere,regex:\"([^:]*):.*\"},target:regexTargetField},"
					+ "{actionType:\"Log Text\",logText:{text:\"some text\",level:WARN}},{actionType:CLear,from:{values:[{value:first_input},{value:second_input}]}},"
					+ "{actionType:map,from:{value:fromField},target:mapTargetField,map:{values:[{key:sourceVal1,value:targetVal1},{key:sourceVal2,value:targetVal2}],haveDefault:true,default:'\"\"'}},"
					+ "{actionType:\"Date Formatter\",id:98908,from:{value:\"${dateFormatterFrom}\"},target:dateFormatterTarget,dateFormatter:{toTimezone:UVW,fromTimezone:XYZ,fromFormat:inputFormat,toFormat:outputFormat}},"
					+ "{actionType:\"Log Event\",id:465456,logEvent:{title:\"some log title\"}},{actionType:\"Replace Text\",from:{value:fromField},replaceText:{find:findText,replace:replacement}}],"
					+ "condition:{left:\"${leftOperand}\",operator:contains,right:[rightOperand1,rightOperand2]}}";

	@Test
	public void saveNewMappingRulesArtifactSuccessTest() throws Exception {
		Report.log(Status.INFO, "test start");
		Vfcmt vfcmt = createVfcmt();
		RestResponse res = saveCompositionAndFirstRuleSuccess(vfcmt, "map", "n.1.map", "param1", ruleRequestBody);
		JsonObject jsonRes = gson.fromJson(res.getResponse(), JsonObject.class);
		assertThat(jsonRes.get("version").getAsString()).isEqualTo("4.1");
		assertThat(jsonRes.get("eventType").getAsString()).isEqualTo("syslogFields");
		assertThat(jsonRes.get("uid")).isNotNull();
		String expectedArtifactName = "map_n.1.map_param1_MappingRules.json";
		Artifact savedArtifact = fetchVfcmtArtifactMetadataByName(vfcmt.getUuid(), expectedArtifactName);
		assertThat(savedArtifact).isNotNull();
	}

	@Test
	public void translateSnmpMappingRuleWithAllActionTypesSuccessTest() throws Exception {
		String expectedTranslation = "{\"processing\":[{\"phase\":\"snmp_map\",\"processors\":[{\"array\":\"varbinds\",\"datacolumn\":\"varbind_value\",\"keycolumn\":\"varbind_oid\",\"class\":\"SnmpConvertor\"},"
				+ "{\"phase\":\"%s\",\"class\":\"RunPhase\"}]},{\"phase\":\"%s\",\"filter\":{\"filters\":[{\"string\":\"${leftOperand}\",\"value\":\"rightOperand1\",\"class\":\"Contains\"},"
				+ "{\"string\":\"${leftOperand}\",\"value\":\"rightOperand2\",\"class\":\"Contains\"}],\"class\":\"Or\"},"
				+ "\"processors\":[{\"updates\":{\"event.commonEventHeader.target\":\"fromField\",\"concatTargetField\":\"concat1_concat2\"},\"class\":\"Set\"},"
				+ "{\"regex\":\"([^:]*):.*\",\"field\":\"regexTargetField\",\"value\":\"extractFromHere\",\"class\":\"ExtractText\"},"
				+ "{\"logLevel\":\"WARN\",\"logText\":\"some text\",\"class\":\"LogText\"},{\"fields\":[\"first_input\",\"second_input\"],\"class\":\"Clear\"},"
				+ "{\"map\":{\"sourceVal1\":\"targetVal1\",\"sourceVal2\":\"targetVal2\"},\"field\":\"fromField\",\"toField\":\"mapTargetField\",\"default\":\"\",\"class\":\"MapAlarmValues\"},"
				+ "{\"fromFormat\":\"inputFormat\",\"fromTz\":\"XYZ\",\"toField\":\"dateFormatterTarget\",\"toFormat\":\"outputFormat\",\"toTz\":\"UVW\",\"value\":\"${dateFormatterFrom}\",\"class\":\"DateFormatter\"},"
				+ "{\"title\":\"some log title\",\"class\":\"LogEvent\"},{\"field\":\"fromField\",\"find\":\"findText\",\"replace\":\"replacement\",\"class\":\"ReplaceText\"}]},"
				+ "{\"phase\":\"%s\",\"processors\":[{\"phase\":\"snmp_publish\",\"class\":\"RunPhase\"}]}]}";
		Vfcmt vfcmt = createVfcmt();
		String name = vfcmt.getName();
		saveCompositionAndFirstRuleSuccess(vfcmt, "map", "map", "param1", ruleRequestBody);
		RestResponse res = DcaeRestClient.translateRules(vfcmt.getUuid(),"map", "map", "param1", "SNMP MSE");
		Report.log(Status.INFO, "translateRules response= "+res);
		assertThat(res.getStatusCode()).isEqualTo(200);
		assertThat(res.getResponse()).isEqualTo(String.format(expectedTranslation, name, name, name));
	}

	@Test
	public void addNewRuleToExistingArtifact() throws Exception {
		Vfcmt vfcmt = createVfcmt();
		String nid = "n.565663636.0";
		RestResponse res = saveCompositionAndFirstRuleSuccess(vfcmt, "map", nid, "param1", ruleRequestBody);
		Rule rule1 = gson.fromJson(res.getResponse(), Rule.class);
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), "map", nid, "param1", ruleRequestBody);
		Report.log(Status.INFO, "saveRule response= "+res);
		Rule rule2 = gson.fromJson(res.getResponse(), Rule.class);
		RestResponse responseGetRules = DcaeRestClient.getRules(vfcmt.getUuid(),"map", nid, "param1");
		Report.log(Status.INFO, "getRules response= "+responseGetRules);
		MappingRules rules = gson.fromJson(responseGetRules.getResponse(), MappingRules.class);
		Report.log(Status.INFO, "MappingRules="+responseGetRules.getResponse());
		assertThat(rules.getRules()).hasSize(2);
		assertThat(rules.getRules().keySet()).containsOnly(rule1.getUid(), rule2.getUid());
	}

	@Test
	public void editExistingRuleInArtifact() throws Exception{
		Vfcmt vfcmt = createVfcmt();
		String updated = "This is an update";
		RestResponse res = saveCompositionAndFirstRuleSuccess(vfcmt, "map", "map", "param1", ruleRequestBody);
		Rule rule = gson.fromJson(res.getResponse(), Rule.class);
		Report.log(Status.INFO, "save rule response=%s", res.getResponse());
		res = DcaeRestClient.getRules(vfcmt.getUuid(),"map", "map", "param1");
		Report.log(Status.INFO, "getRules3 response= "+res);
		MappingRules rules = gson.fromJson(res.getResponse(), MappingRules.class);
		Report.log(Status.INFO, "MappingRules1="+res.getResponse());
		assertThat(rules.getRules().get(rule.getUid()).getDescription()).isEqualTo("newRule");
		assertThat(rules.getRules().keySet()).containsOnly(rule.getUid());
		rule.setDescription(updated);
		DcaeRestClient.saveRule(vfcmt.getUuid(), "map", "map", "param1", rule.toJson());
		Report.log(Status.INFO, "saveRule response= "+res);
		res = DcaeRestClient.getRules(vfcmt.getUuid(),"map", "map", "param1");
		Report.log(Status.INFO, "getRules2 response= "+res);
		rules = gson.fromJson(res.getResponse(), MappingRules.class);
		Report.log(Status.INFO, "MappingRules2="+res.getResponse());
		assertThat(rules.getRules()).hasSize(1);
		assertThat(rules.getRules().get(rule.getUid()).getDescription()).isEqualTo(updated);
	}

	@Test
	public void editRuleInArtifactNoSuchIdErrorTest() throws Exception {
		String expectedError = "{\"requestError\":{\"serviceException\":{\"messageId\":\"SVC6036\",\"text\":\"Error - Failed to save rule. Internal persistence error\",\"variables\":[],\"formattedErrorMessage\":\"Error - Failed to save rule. Internal persistence error\"}},\"notes\":\"\"}";
		Vfcmt vfcmt = createVfcmt();
		RestResponse res = saveCompositionAndFirstRuleSuccess(vfcmt, "map", "map", "param1", ruleRequestBody);
		Rule rule = gson.fromJson(res.getResponse(), Rule.class);
		Report.log(Status.INFO, "rule="+rule);
		rule.setUid("NoSuchUid");
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), "map", "map", "param1", rule.toJson());
		Report.log(Status.INFO, "saveRule response= "+res);
		assertThat(res.getStatusCode()).isEqualTo(409);
		assertThat(res.getResponse()).isEqualTo(expectedError);
	}


	@Test
	public void getAllRules() throws Exception{
		Vfcmt vfcmt = createVfcmt();
		RestResponse res = saveCompositionAndFirstRuleSuccess(vfcmt, "map", "map", "param1", ruleRequestBody);
		String uid1 = gson.fromJson(res.getResponse(), JsonObject.class).get("uid").getAsString();
		//save two more rules
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), "map", "map", "param1", ruleRequestBody);
		Report.log(Status.INFO, "saveRule1 response= "+res);
		String uid2 = gson.fromJson(res.getResponse(), JsonObject.class).get("uid").getAsString();
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), "map", "map", "param1", ruleRequestBody);
		Report.log(Status.INFO, "saveRule2 response= "+res);
		String uid3 = gson.fromJson(res.getResponse(), JsonObject.class).get("uid").getAsString();
		res = DcaeRestClient.getRules(vfcmt.getUuid(),"map", "map", "param1");
		Report.log(Status.INFO, "getRules response= "+res);
		JsonObject jsonRes = gson.fromJson(res.getResponse(), JsonObject.class);
		Report.log(Status.INFO, "jsonRes="+jsonRes);
		assertThat(jsonRes.get("schema")).isNotNull();
		MappingRules actualRules = gson.fromJson(res.getResponse(), MappingRules.class);
		Report.log(Status.INFO, "MappingRules="+res.getResponse());
		assertThat(actualRules.getRules()).hasSize(3);
		assertThat(actualRules.getRules().keySet()).containsOnly(uid1, uid2, uid3);
	}


	@Test
	public void deleteRuleSuccessTest() throws Exception{
		Vfcmt vfcmt = createVfcmt();
		//save first rule
		RestResponse res = saveCompositionAndFirstRuleSuccess(vfcmt, "map", "map", "param1", ruleRequestBody);
		String uid1 = gson.fromJson(res.getResponse(), JsonObject.class).get("uid").getAsString();
		// save second rule
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), "map", "map", "param1", ruleRequestBody);
		Report.log(Status.INFO, "saveRule response= "+res);
		String uid2 = gson.fromJson(res.getResponse(), JsonObject.class).get("uid").getAsString();
		res = DcaeRestClient.getRules(vfcmt.getUuid(),"map", "map", "param1");
		Report.log(Status.INFO, "getRules1 response= "+res);
		MappingRules actualRules = gson.fromJson(res.getResponse(), MappingRules.class);
		Report.log(Status.INFO, "MappingRules1="+res.getResponse());
		// get all rules should return both
		assertThat(actualRules.getRules()).hasSize(2);
		assertThat(actualRules.getRules().keySet()).containsOnly(uid1, uid2);
		// delete a rule
		res = DcaeRestClient.deleteRule(vfcmt.getUuid(),"map", "map", "param1", uid2);
		Report.log(Status.INFO, "deleteRule1 response= "+res);
		assertThat(res.getStatusCode()).isEqualTo(200);
		res = DcaeRestClient.getRules(vfcmt.getUuid(),"map", "map", "param1");
		Report.log(Status.INFO, "getRules2 response= "+res);
		actualRules = gson.fromJson(res.getResponse(), MappingRules.class);
		Report.log(Status.INFO, "MappingRules2="+res.getResponse());
		// get rules should return one rule
		assertThat(actualRules.getRules()).hasSize(1);
		assertThat(actualRules.getRules().keySet()).containsOnly(uid1);
		// delete the last rule - artifact should be deleted
		res = DcaeRestClient.deleteRule(vfcmt.getUuid(),"map", "map", "param1", uid1);
		Report.log(Status.INFO, "deleteRule2 response= "+res);
		assertThat(res.getStatusCode()).isEqualTo(200);
		String expectedArtifactName = "map_map_param1_MappingRules.json";
		Artifact savedArtifact = fetchVfcmtArtifactMetadataByName(vfcmt.getUuid(), expectedArtifactName);
		Report.log(Status.INFO, "savedArtifact= "+savedArtifact);
		assertThat(savedArtifact).isNull();
		// get rules should return empty
		res = DcaeRestClient.getRules(vfcmt.getUuid(),"map", "map", "param1");
		Report.log(Status.INFO, "getRules3 response= "+res);
		assertThat(res.getStatusCode()).isEqualTo(200);
		assertThat(res.getResponse()).isEqualTo("{}");

	}

	@Test
	public void invalidJsonRuleFormatTest() throws Exception{
		String expectedError = "{\"requestError\":{\"serviceException\":{\"messageId\":\"SVC6035\",\"text\":\"Error - Rule format is invalid: %1.\",\"variables\":[\"java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $\"],\"formattedErrorMessage\":\"Error - Rule format is invalid: java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $.\"}},\"notes\":\"\"}";
		RestResponse res = DcaeRestClient.saveRule("someId", "someName", "someNid", "someParam", "gibberish" );
		assertThat(res.getStatusCode()).isEqualTo(400);
		assertThat(res.getResponse()).isEqualTo(expectedError);
	}

	@Test
	public void invalidActionTypeTest() throws Exception {
		String expectedError = "{\"requestError\":{\"serviceException\":{\"messageId\":\"SVC6035\",\"text\":\"Error - Rule format is invalid: %1.\",\"variables\":[\"Undefined action type: gibberish\"],\"formattedErrorMessage\":\"Error - Rule format is invalid: Undefined action type: gibberish.\"}},\"notes\":\"\"}";
		RestResponse res = DcaeRestClient.saveRule("someId", "someName", "someNid", "someParam", "{actions:[{actionType:gibberish}]}");
		assertThat(res.getStatusCode()).isEqualTo(400);
		assertThat(res.getResponse()).isEqualTo(expectedError);
	}

	@Test
	public void conflictingUsersErrorTest() throws Exception {
		Vfcmt vfcmt = createVfcmt();
		RestResponse res = DcaeRestClient.saveComposition(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId(), "{\"nid\":\"map\"}");
		assertThat(res.getStatusCode()).isEqualTo(200);
		//check out by other user then try to save rule by current user
		res = DcaeRestClient.checkoutVfcmt(vfcmt.getUuid(), ElementFactory.getDefaultUser(UserRoleEnum.DESIGNER2).getUserId());
		assertThat(res.getStatusCode()).isEqualTo(200);
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), "map", "map", "someParam", ruleRequestBody);
		assertThat(res.getStatusCode()).isEqualTo(403);
	}

	@Test
	public void uploadArtifactSdcErrorTest() throws Exception {
		String expectedError = "{\"requestError\":{\"serviceException\":{\"messageId\":\"SVC6036\",\"text\":\"Error - Failed to save rule. Internal persistence error\",\"variables\":[],\"formattedErrorMessage\":\"Error - Failed to save rule. Internal persistence error\"}},\"notes\":\"Error: Invalid content.\"}";
		Vfcmt vfcmt = createVfcmt();
		RestResponse res = DcaeRestClient.saveComposition(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId(), "{\"nid\":\"map\"}");
		assertThat(res.getStatusCode()).isEqualTo(200);
		// Generated artifact label would be invalid and should fail when submitted to SDC
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), "map", "map", "()someParam", ruleRequestBody);
		assertThat(res.getStatusCode()).isEqualTo(409);
		assertThat(res.getResponse()).isEqualTo(expectedError);
	}

	@Test
	public void saveMappingRuleNoSuchNidErrorTest() throws Exception {
		String expectedError =  "{\"requestError\":{\"serviceException\":{\"messageId\":\"SVC6114\",\"text\":\"DCAE component %1 not found in composition\",\"variables\":[\"noSuchComponent\"],\"formattedErrorMessage\":\"DCAE component noSuchComponent not found in composition\"}},\"notes\":\"\"}";
		Vfcmt vfcmt = createVfcmt();
		RestResponse res = DcaeRestClient.saveComposition(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId(), "{\"nid\":\"map\"}");
		assertThat(res.getStatusCode()).isEqualTo(200);
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), "noSuchComponent", "noSuchComponent", "someParam", ruleRequestBody);
		assertThat(res.getStatusCode()).isEqualTo(400);
		assertThat(res.getResponse()).isEqualTo(expectedError);
	}


	@Test
	public void deleteRuleNoSuchIdTest() throws Exception {
		String expectedError = "{\"requestError\":{\"serviceException\":{\"messageId\":\"SVC6115\",\"text\":\"Delete rule failed. Internal persistence error\",\"variables\":[],\"formattedErrorMessage\":\"Delete rule failed. Internal persistence error\"}},\"notes\":\"\"}";
		Vfcmt vfcmt = createVfcmt();
		//save rule
		saveCompositionAndFirstRuleSuccess(vfcmt, "map", "map", "param1", ruleRequestBody);
		RestResponse res = DcaeRestClient.deleteRule(vfcmt.getUuid(),"map", "map", "param1", "noSuchRuleId");
		Report.log(Status.INFO, "deleteRule response=%s", res);
		assertThat(res.getStatusCode()).isEqualTo(409);
		assertThat(res.getResponse()).isEqualTo(expectedError);
	}
	
	// After first rule is saved the mappingRules.json artifact is available for get/delete/edit tests
	private RestResponse saveCompositionAndFirstRuleSuccess(Vfcmt vfcmt, String dcaeCompName, String nid, String configParam, String body) throws Exception {
		// generate and save a composition.yml
		Report.log(Status.INFO, "saveCompositionAndFirstRuleSuccess start");
		RestResponse res = DcaeRestClient.saveComposition(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId(), String.format("{\"nid\":\"%s\"}", nid));
		Report.log(Status.INFO, "saveComposition response=%s", res);
		assertThat(res.getStatusCode()).isEqualTo(200);
		
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), dcaeCompName, nid, configParam, body);
		Report.log(Status.INFO, "saveRule response=%s", res);
		assertThat(res.getStatusCode()).isEqualTo(200);
		return res;
	}


	@Test
	public void saveMappingRuleNegativeTest_BadResponse() throws Exception {
		// arrange
		Report.log(Status.INFO, "Arrangeing test...");
		ObjectMapper mapper = new ObjectMapper();
		String badRuleRequestBody = "{\"version\":\"5.3\",\"eventType\":\"syslogFields\",\"uid\":\"\",\"description\":\"map rules\","
				+ "\"actions\":[{actionType:\"Date Formatter\",from:{value:whatever}},{\"id\":\"22fdded0-c9eb-11e7-83c1-3592231134a4\",\"actionType\":\"map\",\"from\":{\"value\":\"AAA\",\"regex\":\"\",\"state\":\"closed\",\"values\":[{\"value\":\"\"},{\"value\":\"\"}]},\"target\":\"BBB\",\"map\":{\"values\":[{\"key\":\"foo\",\"value\":\"bar\"}],\"haveDefault\":true,\"default\":\"\"}},{\"id\":\"2d6fab00-c9eb-11e7-83c1-3592231134a4\",\"actionType\":\"map\",\"from\":{\"value\":\"\",\"regex\":\"\",\"state\":\"closed\",\"values\":[{\"value\":\"\"},{\"value\":\"\"}]},\"target\":\"DDD\",\"map\":{\"values\":[{\"key\":\"foo\",\"value\":\"bar\"}],\"haveDefault\":false,\"default\":\"\"}},{\"id\":\"60bff5a0-c9eb-11e7-83c1-3592231134a4\",\"actionType\":\"map\",\"from\":{\"value\":\"EEE\",\"regex\":\"\",\"state\":\"closed\",\"values\":[{\"value\":\"\"},{\"value\":\"\"}]},\"target\":\"\",\"map\":{\"values\":[{\"key\":\"foo\",\"value\":\"bar\"}],\"haveDefault\":false,\"default\":\"\"}},{\"id\":\"75ea0ce0-c9eb-11e7-83c1-3592231134a4\",\"actionType\":\"map\",\"from\":{\"value\":\"FFF\",\"regex\":\"\",\"state\":\"closed\",\"values\":[{\"value\":\"\"},{\"value\":\"\"}]},\"target\":\"GGG\",\"map\":{\"values\":[{\"key\":\"foo\",\"value\":\"bar\"},{\"key\":\"\",\"value\":\"\"}],\"haveDefault\":false,\"default\":\"\"}},{\"id\":\"75ea0ce0-c9eb-11e7-83c1-3592231134a4\",\"actionType\":\"map\",\"from\":{\"value\":\"FFF\",\"regex\":\"\",\"state\":\"closed\",\"values\":[{\"value\":\"\"},{\"value\":\"\"}]},\"target\":\"GGG\",\"map\":{\"values\":[{\"key\":\"foo\",\"value\":\"bar\"},{\"key\":\"foo\",\"value\":\"not bar\"}],\"haveDefault\":false,\"default\":\"\"}}],\"condition\":null}";
		Vfcmt vfcmt = createVfcmt();
		Report.log(Status.INFO, "Saving composition of a fake cdump...");
		RestResponse res = DcaeRestClient.saveComposition(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId(), "{\"nid\":\"map\"}");
		Report.logDebug("saveComposition response", res);
		if (res.getStatusCode() != 200) {
			fail("Unable to arrange test, save composition failed\n" + res.toString());
		}
		// act
		Report.log(Status.INFO, "Executing...");
		Report.logDebug("Request body", badRuleRequestBody);
		RestResponse targetRes = DcaeRestClient.saveRule(vfcmt.getUuid(), "noSuchComponent", "map", "someParam", badRuleRequestBody);
		Report.logDebug("saveRule response", targetRes);
		// assert
		Report.log(Status.INFO, "Asserting...");
		List<String> errors = mapper.readValue(targetRes.getResponse(), SaveRuleError.class).getFormattedErrors(); // parse response
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(targetRes.getStatusCode()).isEqualTo(400);
			softly.assertThat(errors).containsExactlyInAnyOrder(
					"Please fill the default value of map action to BBB",
				    "Please fill the from field of map action to DDD",
				    "Please fill the target field of map action to ",
				    "Please fill all key-value pairs of map action to GGG",
				    "Error: Duplication in map keys exists, please modify rule configuration",
					"Please fill the target field of Date Formatter action to null",
					"Please fill the to timezone field of Date Formatter action to null",
					"Please fill the from timezone field of Date Formatter action to null",
					"Please fill the from format field of Date Formatter action to null",
					"Please fill the to format field of Date Formatter action to null"
					);
		});
	}

	@Test
	public void translateInvalidFlowTypeFailureTest() throws Exception {
		String expectedError = "{\"requestError\":{\"serviceException\":{\"messageId\":\"SVC6116\",\"text\":\"Translation failed. Reason: %s\",\"variables\":[\"%s\"],\"formattedErrorMessage\":\"Translation failed. Reason: %s\"}},\"notes\":\"\"}";
		RestResponse res = DcaeRestClient.translateRules("someId","map", "map", "param1", "noSuchFlowType");
		assertThat(res.getStatusCode()).isEqualTo(400);
		assertThat(res.getResponse()).isEqualTo(String.format(expectedError, "%1", "Flow type noSuchFlowType not found", "Flow type noSuchFlowType not found"));
		res = DcaeRestClient.translateRules("someId","map", "map", "param1", null);
		assertThat(res.getStatusCode()).isEqualTo(400);
		assertThat(res.getResponse()).isEqualTo(String.format(expectedError, "%1", "Flow type null not found", "Flow type null not found"));
	}

	@Test
	public void getExistingRuleTargetsTest() throws Exception {
		String dcaeCompName = "theComponent";
		String nid = "theNid";
		String configParam1 = "ConfigParam1";
		String configParam2 = "ConfigParam2";
		final String UID = "uid";

		Vfcmt vfcmt = createVfcmt();
		RestResponse res = saveCompositionAndFirstRuleSuccess(vfcmt, dcaeCompName, nid, configParam1, ruleRequestBody);
		gson.fromJson(res.getResponse(), JsonObject.class).get(UID).getAsString();
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), dcaeCompName, nid, configParam2, ruleRequestBody);
		Report.log(Status.INFO, "saveRule1 response= "+res);
		gson.fromJson(res.getResponse(), JsonObject.class).get(UID).getAsString();
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), dcaeCompName, nid, configParam1, ruleRequestBody);
		Report.log(Status.INFO, "saveRule2 response= "+res);
		gson.fromJson(res.getResponse(), JsonObject.class).get(UID).getAsString();
		res = DcaeRestClient.saveRule(vfcmt.getUuid(), dcaeCompName, nid, configParam1, ruleRequestBody);
		Report.log(Status.INFO, "saveRule3 response= "+res);

		res = DcaeRestClient.getExistingRuleTargets(vfcmt.getUuid(),dcaeCompName,nid);
		if (res.getStatusCode() != 200) {
			fail("Unable to arrange test, get existing rule targets test failed\n" + res.toString());
		}
		String response = res.getResponse();
		assertThat(response.contains(configParam1));
		assertThat(response.contains(configParam2));
	}
}
