package com.p3.archon.coreprocess.executables.tools.base;

import java.io.PrintWriter;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;

public abstract class BaseMetadataXMLFormatter extends BaseFormatter {

	protected BaseMetadataXMLFormatter(PrintWriter out, TextOutputFormat outputFormat) throws Exception {
		super(out, outputFormat, true);
	}
}
