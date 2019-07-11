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

package org.onap.dcae.ci.entities.sdc;

import org.onap.dcae.ci.entities.sdc.SdcComponent;


public class SdcService extends SdcComponent {

	private String projectCode = "12345";
    private String serviceRole =  "";// empty string is valid
	private String serviceType = "";// empty string is valid
	private String instantiationType = "Macro";


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
