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

package org.onap.dcae.ci.utilities;

import com.aventstack.extentreports.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.report.Report;
import org.onap.sdc.dcae.composition.model.Requirement;
import org.onap.sdc.dcae.composition.model.Value;
import org.onap.sdc.dcae.composition.model.deserializer.RequirementDeserializer;
import org.onap.sdc.dcae.composition.model.deserializer.ValueDeserializer;
import org.onap.sdc.dcae.composition.util.DcaeBeConstants;
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
		Report.log(Status.DEBUG, "Create VFCMT Response: " + StringUtils.truncate(response));
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
		return createVfcmt(DcaeRestClient.getDefaultUserId());
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Vfcmt[] getAllVfcmts() throws Exception {
		Report.log(Status.INFO, "Fetching all vfcmt resources");
		RestResponse response = DcaeRestClient.getAllVfcmts();
		return convertResponseToVfcmt(response);
	}

	public Vfcmt[] getAllBaseVfcmts() throws Exception {
		Report.log(Status.INFO, "Fetching all vfcmt resources");
		RestResponse response = DcaeRestClient.getAllMonitoringTemplatesVfcmts();
		return convertResponseToVfcmt(response);
	}

	private Vfcmt[] convertResponseToVfcmt(RestResponse response) throws Exception {
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
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
		Report.log(Status.INFO, "Creating vfcmt...");
		Vfcmt vfcmt = createVfcmt(userId);
		if (vfcmt.getLifecycleState().equals(DcaeBeConstants.LifecycleStateEnum.NOT_CERTIFIED_CHECKOUT.name()) == false) {
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
		String userId = DcaeRestClient.getDefaultUserId();
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
		Report.log(Status.INFO, "Creating vfcmt");
		Vfcmt vfcmt = createVfcmt(userId);
		Report.log(Status.INFO, "Checkin the vfcmt");
		RestResponse response = DcaeRestClient.checkinVfcmt(vfcmt.getUuid(), vfcmt.getLastUpdaterUserId());
		Report.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
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
		String userId = DcaeRestClient.getDefaultUserId();
		return createCheckedinVfcmt(userId);
	}
}
