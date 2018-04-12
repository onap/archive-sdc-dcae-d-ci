package com.att.ecomp.dcae.ci.utilities;

import org.openecomp.d2.ci.datatypes.devObjects.LifecycleStateEnum;
import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;

import com.aventstack.extentreports.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.onap.sdc.dcae.composition.model.Requirement;
import org.onap.sdc.dcae.composition.model.Value;
import org.onap.sdc.dcae.composition.model.deserializer.RequirementDeserializer;
import org.onap.sdc.dcae.composition.model.deserializer.ValueDeserializer;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;

public class DcaeEntityClient {
	
	protected Gson gson;
	
	public DcaeEntityClient() {
		super();
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Requirement.class, new RequirementDeserializer());
		gsonBuilder.registerTypeAdapter(Value.class, new ValueDeserializer());
		gson = gsonBuilder.create();
	}
	
	/* VFCMT */
	
	/**
	 * 
	 * @param name
	 * @param description
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Vfcmt createVfcmt(String name, String description, String userId) throws Exception {
		RestResponse response = DcaeRestClient.createVfcmt(name, description, userId);
		ExtentTestActions.log(Status.DEBUG, "Create VFCMT Response: " + StringUtils.truncate(response));
		if (response.getStatusCode() != 200) {
			throw new Exception("Response: " + StringUtils.truncate(response));
		}
		return gson.fromJson(response.getResponse(), Vfcmt.class);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Vfcmt createVfcmt(String userId) throws Exception {
		String name = StringUtils.randomString("CI-", 20);
		String description = "This vfcmt was created by automated ci tests";
		Vfcmt createVfcmt = createVfcmt(name, description, userId);
		Report.log(Status.INFO, "createVfcmt result="+createVfcmt);
		return createVfcmt;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Vfcmt createVfcmt() throws Exception {
		return createVfcmt(DcaeRestClient.getDefaultUser().getUserId());
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Vfcmt[] getAllVfcmts() throws Exception {
		ExtentTestActions.log(Status.INFO, "Fetching all vfcmt resources");
		RestResponse response = DcaeRestClient.getAllVfcmts();
		ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		if (response.getStatusCode() != 200) {
			throw new Exception("Response: " + StringUtils.truncate(response));
		}
		return gson.fromJson(response.getResponse(), Vfcmt[].class);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Vfcmt createCheckedoutVfcmt(String userId) throws Exception {
		ExtentTestActions.log(Status.INFO, "Creating vfcmt...");
		Vfcmt vfcmt = createVfcmt(userId);
		if (vfcmt.getLifecycleState().equals(LifecycleStateEnum.NOT_CERTIFIED_CHECKOUT.name()) == false) {
			throw new Exception("created vfcmt is not in checkout state!");
		}
		return vfcmt;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Vfcmt createCheckedoutVfcmt() throws Exception {
		String userId = DcaeRestClient.getDefaultUser().getUserId();
		Report.log(Status.INFO, "Going to create checked out VFCMT with user="+userId);
		return createCheckedoutVfcmt(userId);
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Vfcmt createCheckedinVfcmt(String userId) throws Exception {
		ExtentTestActions.log(Status.INFO, "Creating vfcmt");
		Vfcmt vfcmt = createVfcmt(userId);
		ExtentTestActions.log(Status.INFO, "Checkin the vfcmt");
		RestResponse response = DcaeRestClient.checkinVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
		Vfcmt vfcmtAfterCheckin = gson.fromJson(response.getResponse(), Vfcmt.class);
		if (response.getStatusCode() != 200) {
			throw new Exception("Unable to checkin newly created vfcmt");
		}
		return vfcmtAfterCheckin;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Vfcmt createCheckedinVfcmt() throws Exception {
		String userId = DcaeRestClient.getDefaultUser().getUserId();
		return createCheckedinVfcmt(userId);
	}
}
