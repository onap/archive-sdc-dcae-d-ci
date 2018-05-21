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