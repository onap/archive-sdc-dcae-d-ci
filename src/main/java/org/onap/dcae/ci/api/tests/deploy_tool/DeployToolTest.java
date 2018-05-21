package org.onap.dcae.ci.api.tests.deploy_tool;


import org.onap.dcae.ci.api.tests.DcaeRestBaseTest;
import org.onap.sdc.dcae.composition.vfcmt.Vfcmt;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeployToolTest  extends DcaeRestBaseTest {

    @Test
    public void verifyDeployToolVfcmtExist() throws Exception {
        Vfcmt[] vfcmts = client.getAllBaseVfcmts();
        assertThat(vfcmts).isNotNull();
        assertThat(vfcmts.length).isGreaterThan(0);
    }

}
