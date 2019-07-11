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


public class SdcComponentInstance {

	String componentUid;
	String description;
	String posX;
	String posY;
	String name;
	String uniqueId;

	public SdcComponentInstance(SdcResource vf) {
		this.componentUid = vf.getUniqueId();
		this.posX = "20";
		this.posY = "20";
		this.name = vf.getName();
		this.description = vf.getDescription();
	}
}
