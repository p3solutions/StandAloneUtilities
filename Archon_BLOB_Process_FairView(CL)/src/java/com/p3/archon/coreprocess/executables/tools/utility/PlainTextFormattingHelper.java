package com.p3.archon.coreprocess.executables.tools.utility;

import static com.p3.archon.coreprocess.executables.tools.utility.common.Utility.isBlank;

import java.io.PrintWriter;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.common.Color;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONObject;

public class PlainTextFormattingHelper extends BaseTextFormattingHelper {

	public PlainTextFormattingHelper(final PrintWriter out, final TextOutputFormat outputFormat) {
		super(out, outputFormat);
	}

	@Override
	public String createLeftArrow() {
		return "<--";
	}

	@Override
	public String createRightArrow() {
		return "-->";
	}

	@Override
	public String createWeakLeftArrow() {
		return "<~~";
	}

	@Override
	public String createWeakRightArrow() {
		return "~~>";
	}

	@Override
	public void writeDocumentEnd() {
	}

	@Override
	public void writeDocumentStart() {
	}

	@Override
	public void writeHeader(final DocumentHeaderType type, final String header) {
		if (!isBlank(header)) {
			final String defaultSeparator = separator("=");

			final String prefix;
			final String separator;
			if (type == null) {
				prefix = System.lineSeparator();
				separator = defaultSeparator;
			} else {
				switch (type) {
				case title:
					prefix = System.lineSeparator();
					separator = separator("_");
					break;
				case subTitle:
					prefix = System.lineSeparator();
					separator = defaultSeparator;
					break;
				case section:
					prefix = "";
					separator = separator("-=-");
					break;
				default:
					prefix = System.lineSeparator();
					separator = defaultSeparator;
					break;
				}
			}
			out.println(System.lineSeparator() + prefix + header + System.lineSeparator() + separator + prefix);
		}
	}

	@Override
	public void writeObjectEnd() {
		out.println();
	}

	@Override
	public void writeObjectNameRow(final String id, final String name, final String description,
			final Color backgroundColor) {
		writeNameRow(name, description);
		out.println(DASHED_SEPARATOR);
	}

	@Override
	public void writeObjectStart() {
	}

	@Override
	public void writeValue(String value) {
	}

	@Override
	public void writeElementEnd() {
	}

	@Override
	public void writeElementStart(String elm) {
	}

	@Override
	public void writeRecordStart(String recordElm, String suffix) {
	}

	@Override
	public void writeRecordEnd() {
	}

	@Override
	public void writeRootElementEnd() {
	}

	@Override
	public void writeRootElementStart(String rootElm) {
	}

	@Override
	public void flush() {
	}

	@Override
	public void newlineWriter() {
	}

	@Override
	public void writeAttribute(String name, String value) {
	}

	@Override
	public void writeCData(String object) {
	}

	@Override
	public void flushOnly() {
	}

	@Override
	public void write(JSONObject jsonRoot) throws Exception {
	}

	@Override
	public void flushOutput() {
		// TODO Auto-generated method stub

	}

}
