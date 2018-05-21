package com.onap.dcae.ci.entities.composition.services;

import org.openecomp.d2.ci.datatypes.ServiceReqDetails;

public class Vfi {
    private String name;
    private ServiceReqDetails container;

    public Vfi(String name, ServiceReqDetails container) {
        this.name = name;
        this.container = container;
    }

    public String getName() {
        return name;
    }

    public ServiceReqDetails getContainer() {
        return container;
    }
}