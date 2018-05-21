package com.onap.dcae.ci.entities.composition.services;

import org.onap.sdc.dcae.composition.services.Service;
import org.openecomp.d2.ci.datatypes.ServiceReqDetails;

public class CiService extends Service {
    // converts from ui-api-ci object to dcae-ci object (partial construction)
    public CiService(ServiceReqDetails service) {
        super(service.getName(), service.getUUID(), service.getVersion());
    }
}
