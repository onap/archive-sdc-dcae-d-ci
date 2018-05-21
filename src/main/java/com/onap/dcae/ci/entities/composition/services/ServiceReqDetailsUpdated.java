package com.onap.dcae.ci.entities.composition.services;

import org.openecomp.d2.ci.datatypes.ServiceReqDetails;

public class ServiceReqDetailsUpdated extends ServiceReqDetails {
    protected String serviceRole =  "";// empty string is valid
    protected String serviceType = "";// empty string is valid
    protected String instantiationType = "Macro";

    public ServiceReqDetailsUpdated(ServiceReqDetails defaultServiceOld) {
        super(defaultServiceOld);
    }

    public void setServiceRole(String serviceRole){
        this.serviceRole = serviceRole;
    }

    public String getServiceRole(){
        return serviceRole;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceType(){
        return serviceType;
    }

    public void setInstantiationType(String instantiationType) {
        this.instantiationType = instantiationType;
    }

    public String getInstantiationType(){
        return instantiationType;
    }
}
