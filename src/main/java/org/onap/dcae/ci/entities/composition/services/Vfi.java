package org.onap.dcae.ci.entities.composition.services;

import org.onap.dcae.ci.entities.sdc.SdcService;

public class Vfi {
    private String name;
    private SdcService container;

    public Vfi(String name, SdcService container) {
        this.name = name;
        this.container = container;
    }

    public String getName() {
        return name;
    }

    public SdcService getContainer() {
        return container;
    }
}