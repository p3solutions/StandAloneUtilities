package com.p3.archon.dboperations.dbmodel.enums;

public enum CaaAppType {
	TEST("TEST"), SAP("SAP"), PS("PS"), JDE("JDE"), INVALID("NA"), OEBS("OEBS"), SF("SF"),INFOR("INFOR");

	private String value;

	CaaAppType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static CaaAppType getAppType(String apptype) {
		switch (apptype.toLowerCase()) {
		case "sap":
			return SAP;
		case "ps":
			return PS;
		case "jde":
			return JDE;
		case "oebs":
			return OEBS;
		case "sf":
			return SF;
		case "test":
			return TEST;
		case "infor":
			return INFOR;	
		default:
			return INVALID;
		}
	}
}
