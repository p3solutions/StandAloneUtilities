package com.p3.tmb.commonUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class commonUtils {

	public static String exceptionMsgToString(Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
