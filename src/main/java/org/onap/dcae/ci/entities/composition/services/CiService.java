package org.onap.dcae.ci.entities.composition.services;

import org.onap.dcae.ci.entities.sdc.SdcService;
import org.onap.sdc.dcae.composition.services.Service;

public class CiService extends Service {
    // partial construction - converts from internal SDC object to dcae-ci object (external SDC)
    public CiService(SdcService service) {
        super(service.getName(), service.getUuid(), service.getVersion());
    }
}
