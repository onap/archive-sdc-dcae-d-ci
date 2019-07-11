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

package org.onap.dcae.ci.entities.rule_editor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SaveRuleError {
	private Map<String, Object> requestError;
	private String notes;
	
	@JsonCreator
	public SaveRuleError(
			@JsonProperty("requestError") Map<String, Object> requestError, @JsonProperty("notes") String notes) {
		this.requestError = requestError;
		this.notes = notes;
	}



	public List<String> getFormattedErrors() {
		List<Map<String, String>> errors = (List<Map<String, String>>)requestError.get("serviceExceptions");
		return errors.stream().map(e -> e.get("formattedErrorMessage")).collect(Collectors.toList());
	}
}
