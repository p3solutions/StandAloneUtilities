package com.p3.archon.coreprocess.executables.tools.base;

import java.io.PrintWriter;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.HtmlFormattingHelper;
import com.p3.archon.coreprocess.executables.tools.utility.JsonFormattingHelper;
import com.p3.archon.coreprocess.executables.tools.utility.MetadataXMLFormattingHelper;
import com.p3.archon.coreprocess.executables.tools.utility.PlainTextFormattingHelper;
import com.p3.archon.coreprocess.executables.tools.utility.TextFormattingHelper;
import com.p3.archon.coreprocess.executables.tools.utility.XMLFormattingHelper;

public class BaseFormatter {

	protected TextFormattingHelper formattingHelper;
	protected PrintWriter out;

	protected BaseFormatter(PrintWriter out, TextOutputFormat outputFormat, boolean isMetadataXml) {
		switch (outputFormat) {
		case json:
			formattingHelper = new JsonFormattingHelper(out, outputFormat);
			break;
		case html:
			formattingHelper = new HtmlFormattingHelper(out, outputFormat);
			break;
		case xml:
			formattingHelper = isMetadataXml?new MetadataXMLFormattingHelper(out, outputFormat):new XMLFormattingHelper(out, outputFormat);
			break;
		case text:
		default:
			formattingHelper = new PlainTextFormattingHelper(out, outputFormat);
			break;
		}
	}
}
