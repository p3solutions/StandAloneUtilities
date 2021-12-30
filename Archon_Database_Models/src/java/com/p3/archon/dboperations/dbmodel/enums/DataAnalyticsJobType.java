package com.p3.archon.dboperations.dbmodel.enums;

public enum DataAnalyticsJobType {
	TEST_CONNECTION("Test Connection"), DATA_ANALYTICS("Data Analytics");

	private String value;

	DataAnalyticsJobType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
