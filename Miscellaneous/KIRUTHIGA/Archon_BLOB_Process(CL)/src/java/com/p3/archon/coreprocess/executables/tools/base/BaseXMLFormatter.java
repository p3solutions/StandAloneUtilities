package com.p3.archon.coreprocess.executables.tools.base;

import java.io.PrintWriter;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;

public abstract class BaseXMLFormatter extends BaseFormatter {

	protected BaseXMLFormatter(PrintWriter out, TextOutputFormat outputFormat) throws Exception {
		super(out, outputFormat, false);
	}
}
