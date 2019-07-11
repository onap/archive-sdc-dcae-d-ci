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

package org.onap.dcae.ci.entities.composition.services;

import org.onap.dcae.ci.entities.sdc.SdcService;
import org.onap.sdc.dcae.composition.services.Service;

public class CiService extends Service {
    // partial construction - converts from internal SDC object to dcae-ci object (external SDC)
    public CiService(SdcService service) {
        super(service.getName(), service.getUuid(), service.getVersion());
    }
}
