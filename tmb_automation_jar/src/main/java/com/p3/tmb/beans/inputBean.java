package com.p3.tmb.beans;

import org.kohsuke.args4j.Option;

public class inputBean {

	@Option(name ="-p", aliases="--property file path", usage="contains the property file path",required =true)
	private String propertyFilePath;
	

	public String getPropertyFilePath() {
		return propertyFilePath;
	}

	public void setPropertyFilePath(String propertyFilePath) {
		this.propertyFilePath = propertyFilePath;
	}
}
