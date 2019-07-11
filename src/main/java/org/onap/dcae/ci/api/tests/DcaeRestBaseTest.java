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

package org.onap.dcae.ci.api.tests;

import com.aventstack.extentreports.Status;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.onap.dcae.ci.config.Configuration;
import org.onap.dcae.ci.entities.RestResponse;
import org.onap.dcae.ci.entities.composition.services.CiService;
import org.onap.dcae.ci.entities.composition.services.Vfi;
import org.onap.dcae.ci.entities.sdc.SdcComponent;
import org.onap.dcae.ci.entities.sdc.SdcResource;
import org.onap.dcae.ci.entities.sdc.SdcService;
import org.onap.dcae.ci.report.Report;
import org.onap.dcae.ci.utilities.*;
import org.onap.sdc.dcae.composition.model.Requirement;
import org.onap.sdc.dcae.composition.model.Value;
import org.onap.sdc.dcae.composition.model.deserializer.RequirementDeserializer;
import org.onap.sdc.dcae.composition.model.deserializer.ValueDeserializer;
import org.onap.sdc.dcae.composition.restmodels.ruleeditor.ActionDeserializer;
import org.onap.sdc.dcae.composition.restmodels.ruleeditor.BaseAction;
import org.onap.sdc.dcae.composition.restmodels.ruleeditor.BaseCondition;
import org.onap.sdc.dcae.composition.restmodels.ruleeditor.ConditionDeserializer;
import org.onap.sdc.dcae.composition.services.Artifact;
import org.onap.sdc.dcae.composition.services.Resource;
import org.onap.sdc.dcae.composition.services.Service;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.testng.annotations.AfterSuite;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class DcaeRestBaseTest extends SetupReport {

	public static Gson gson = null;
    protected DcaeEntityClient client = new DcaeEntityClient();

	public DcaeRestBaseTest() {
		super();
        GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Requirement.class, new RequirementDeserializer());
		gsonBuilder.registerTypeAdapter(Value.class, new ValueDeserializer());
		gsonBuilder.registerTypeAdapter(BaseAction.class, new ActionDeserializer());
		gsonBuilder.registerTypeAdapter(BaseCondition.class, new ConditionDeserializer());
		gson = gsonBuilder.create();
	}

	@Override
	protected Configuration getEnvConfiguration() {
		return ConfigurationReader.getConfiguration();
	}

	protected Service createServiceWithVFiAsSdcDesigner() throws IOException {
		Report.log(Status.INFO, "Create Service with VFi as SDC Designer method started");

		SdcResource vf = SdcInternalApiClient.createVf();
		vf = SdcInternalApiClient.checkinVf(vf);
		SdcService service = SdcInternalApiClient.createService();
		Vfi vfi = SdcInternalApiClient.createVfi(service, vf);
		return convertToDcaeServiceWithVfi(service, vfi.getName());

	}


	// converts from ui-api-ci object to dcae-ci object (partial construction)
	private Service convertToDcaeServiceWithVfi(SdcService service, String vfiName){
		Service newService = new CiService(service);
		Resource newVfi = new Resource();
		newVfi.setResourceInstanceName(vfiName);
		newService.setResources(Arrays.asList(newVfi));
		return newService;
	}

	protected Vfcmt createVfcmt() throws IOException {
		String newName = "CI" + RandomStringUtils.randomAlphanumeric(4);
		Report.log(Status.INFO, "createVfcmt start");
		RestResponse res = DcaeRestClient.createVfcmt(newName, "description");
		Report.log(Status.INFO, "createVfcmt response=%s", res);
		assertThat(res.getStatusCode()).isEqualTo(200);
		return gson.fromJson(res.getResponse(), Vfcmt.class);
	}

	protected Artifact fetchVfcmtArtifactMetadataByName(String vfcmtUid, String artifactName) throws IOException {
		Report.log(Status.INFO, "fetchVfcmtArtifactMetadataByName, vfcmtUid="+vfcmtUid+", artifactName"+artifactName);
		RestResponse res = DcaeRestClient.getResourceDetails(vfcmtUid);
		Resource vfcmt = gson.fromJson(res.getResponse(), Resource.class);
		return vfcmt.getArtifacts().stream()
				.filter(a -> artifactName.equalsIgnoreCase(a.getArtifactName()))
				.findAny()
				.orElse(null);
	}

	//***** delete all assets created by test users - uses internal SDC API ****//

	@AfterSuite(alwaysRun = true)
	public static void cleanup() throws IOException {

		final String resources = "resources";
		final String services = "services";
		List<String> testUsers = Collections.synchronizedList(Arrays.asList(DcaeRestClient.getDefaultUserId(), DcaeRestClient.getDesigner2UserId()));
		Map<String, List<SdcComponent>> testComponents = testUsers.parallelStream()
				.map(DcaeRestBaseTest::getSafeComponentByUser)
				.flatMap(m -> m.entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> {
					v1.addAll(v2);
					return v1;
				}));
		collectAllResourceIds(Collections.synchronizedList(testComponents.get(resources))).parallelStream().forEach(id -> deleteAsset(resources, id));
		collectAllServiceIds(Collections.synchronizedList(testComponents.get(services))).parallelStream().forEach(id -> deleteAsset(services, id));
		SdcInternalApiClient.deleteMarkedResources();
		SdcInternalApiClient.deleteMarkedServices();
	}

	private static Set<String> getUidsToDelete(String context, SdcComponent component) {
		try {
			return Arrays.asList("0.1", "1.0").contains(component.getVersion()) ? Collections.singleton(component.getUniqueId()) :
					new HashSet<>(SdcInternalApiClient.getAssetMetadata(context, component.getUniqueId(), component.getLastUpdaterUserId()).getMetadata().getAllVersions().values());
		} catch (Exception e) {
			Report.log(Status.ERROR, "failed to fetch sdc component: %s, Error: %s", component.getUniqueId(), e);
			return Collections.emptySet();
		}
	}

	private static Set<String> collectAllResourceIds(List<SdcComponent> resources) {
		Predicate<SdcComponent> deleteResource = p -> Arrays.asList("VF", "VFCMT").contains(p.getResourceType()) && !"Base Monitoring Template".equals(p.getCategories().get(0).getSubcategories().get(0).getName());
		Set<String> resourceIds = Collections.synchronizedSet(resources.parallelStream()
					.filter(deleteResource)
					.map(r -> getUidsToDelete("resources", r))
				    .flatMap(Set::stream)
				.collect(Collectors.toSet()));
		Report.log(Status.INFO, "collected %s resourceIds", resourceIds.size());
		return resourceIds;
	}

	private static Set<String> collectAllServiceIds(List<SdcComponent> services) {
		Set<String> serviceIds = Collections.synchronizedSet(services.parallelStream()
				.map(c -> getUidsToDelete("services", c))
				.flatMap(Set::stream)
				.collect(Collectors.toSet()));
		Report.log(Status.INFO, "collected %s serviceIds", serviceIds.size());
		return serviceIds;
	}

	private static Map<String, List<SdcComponent>> getSafeComponentByUser(String userId) {
		try {
			return SdcInternalApiClient.getAssetsByUser(userId);
		} catch (Exception e){
			Report.log(Status.ERROR, "failed to fetch followed components. Error: %s", e);
			return new HashMap<>();
		}
	}

	private static void deleteAsset(String context, String id) {
		try {
			Report.log(Status.INFO, "about to delete sdc asset(%s): %s", context, id);
			SdcInternalApiClient.deleteAssetFromSdc(context, id);
		} catch (Exception e){
			Report.log(Status.ERROR, "failed to delete sdc asset(%s): %s, Error: %s" ,context, id, e);
		}
	}
}
