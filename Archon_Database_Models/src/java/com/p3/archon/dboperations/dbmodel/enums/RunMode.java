package com.p3.archon.dboperations.dbmodel.enums;

public enum RunMode {
	DB("dbmode"), MANUAL("manual"), INVALID("NA");

	private String value;

	RunMode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static RunMode getMode(String mode) {
		switch (mode.toLowerCase()) {
		case "dbmode":
		case "db":
			return DB;
		case "manual":
			return MANUAL;
		default:
			return INVALID;
		}
	}
}
