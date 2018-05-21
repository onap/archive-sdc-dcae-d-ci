package com.onap.dcae.ci.ui.setup;

import org.openecomp.d2.ci.datatypes.Configuration;

public class DcaeConfiguration extends Configuration {

	private String apiPath;
	private String dcaeBeHost;
	private String dcaeBePort;
	private String ruleEditorUrl;
	
	

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}
	
	public String getDcaeBeHost() {
		return dcaeBeHost;
	}

	public void setDcaeBeHost(String dcaeBeHost) {
		this.dcaeBeHost = dcaeBeHost;
	}

	public String getDcaeBePort() {
		return dcaeBePort;
	}

	public void setDcaeBePort(String dcaeBePort) {
		this.dcaeBePort = dcaeBePort;
	}

	public String getRuleEditorUrl() {
		return ruleEditorUrl;
	}

	public void setRuleEditorUrl(String ruleEditorUrl) {
		this.ruleEditorUrl = ruleEditorUrl;
	}


	
}
