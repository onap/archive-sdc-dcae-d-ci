package com.att.ecomp.dcae.ci.api.tests.vfcmt;

import static org.assertj.core.api.Assertions.*;

import org.openecomp.d2.ci.datatypes.http.RestResponse;
import org.openecomp.d2.ci.report.ExtentTestActions;
import org.testng.annotations.*;

import com.att.ecomp.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import com.att.ecomp.dcae.ci.utilities.DcaeRestClient;
import com.att.ecomp.dcae.ci.utilities.StringUtils;
import com.aventstack.extentreports.Status;

public class GetAllVfcmts extends DcaeRestBaseTest {

    private RestResponse response;

    @BeforeClass
    public void executeApiCall() {
        try {
            // act
            ExtentTestActions.log(Status.INFO, "Fetching all vfcmt resources");
            response = DcaeRestClient.getAllVfcmts();
            ExtentTestActions.log(Status.DEBUG, "Response: " + StringUtils.truncate(response));
        } catch (Exception err) {
            ExtentTestActions.log(Status.ERROR, err);
            err.printStackTrace();
        }
    }

    /* Positive tests */

    @Test
    public void testResponseStatusOk() {
        // assert
        ExtentTestActions.log(Status.INFO, "Verifing response status is 200");
        assertThat(response.getStatusCode()).as("response status").isEqualTo(200);
    }

    @Test
    public void testResponseBodyIsListOfVfcmtsOnly() {
        // assert
        ExtentTestActions.log(Status.INFO, "Parsing response to a list of VFCMTs");
        Vfcmt[] vfcmtList = gson.fromJson(response.getResponse(), Vfcmt[].class);
        ExtentTestActions.log(Status.INFO, "Verifing all items of the parsed response are VFCMTs");
        assertThat(vfcmtList)
            .as("response data")
            .extracting("resourceType")
            .containsOnly("VFCMT");
    }

    /* Negative tests */
}
