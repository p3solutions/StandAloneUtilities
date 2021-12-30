package com.p3.archon.coreprocess.executables.tools.utility;

import static com.p3.archon.coreprocess.executables.tools.utility.common.Utility.isBlank;
import static com.p3.archon.coreprocess.executables.tools.utility.common.Utility.readResourceFully;
import static com.p3.archon.coreprocess.executables.tools.utility.html.Entities.escapeForXMLElement;

import java.io.PrintWriter;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.common.Color;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONObject;

public final class HtmlFormattingHelper extends BaseTextFormattingHelper {

	private static String HTML_HEADER = htmlHeader();
	private static String HTML_FOOTER = "</body>" + System.lineSeparator() + "</html>";

	private static String htmlHeader() {
		final StringBuilder styleSheet = new StringBuilder(4096);
		styleSheet.append(System.lineSeparator()).append(readResourceFully("/sc.css")).append(System.lineSeparator())
				.append(readResourceFully("/sc_output.css")).append(System.lineSeparator());

		return "<!DOCTYPE html>" + System.lineSeparator() + "<html lang=\"en\">" + System.lineSeparator() + "<head>"
				+ System.lineSeparator() + "  <title>Archon Output</title>" + System.lineSeparator()
				+ "  <meta charset=\"utf-8\"/>" + System.lineSeparator() + "  <style>" + styleSheet + "  </style>"
				+ System.lineSeparator() + "</head>" + System.lineSeparator() + "<body>" + System.lineSeparator();
	}

	public HtmlFormattingHelper(final PrintWriter out, final TextOutputFormat outputFormat) {
		super(out, outputFormat);
	}

	@Override
	public String createLeftArrow() {
		return "\u2190";
	}

	@Override
	public String createRightArrow() {
		return "\u2192";
	}

	@Override
	public String createWeakLeftArrow() {
		return "\u21dc";
	}

	@Override
	public String createWeakRightArrow() {
		return "\u21dd";
	}

	@Override
	public void writeDocumentEnd() {
		out.println(HTML_FOOTER);
	}

	@Override
	public void writeDocumentStart() {
		out.println(HTML_HEADER);
	}

	@Override
	public void writeHeader(final DocumentHeaderType type, final String header) {
		if (!isBlank(header) && type != null) {
			out.println(String.format("%s%n<%s>%s</%s>%n", type.getPrefix(), type.getHeaderTag(), header,
					type.getHeaderTag()));
		}
	}

	@Override
	public void writeObjectEnd() {
		out.append("</table>").println();
		out.println("<p>&#160;</p>");
		out.println();
	}

	@Override
	public void writeObjectNameRow(final String id, final String name, final String description,
			final Color backgroundColor) {
		final StringBuilder buffer = new StringBuilder(1024);
		buffer.append("  <caption style='background-color: ").append(backgroundColor).append(";'>");
		if (!isBlank(name)) {
			buffer.append("<span");
			if (!isBlank(id)) {
				buffer.append(" id='").append(id).append("'");
			}
			buffer.append(" class='caption_name'>").append(escapeForXMLElement(name)).append("</span>");
		}
		if (!isBlank(description)) {
			buffer.append(" <span class='caption_description'>").append(escapeForXMLElement(description))
					.append("</span>");
		}
		buffer.append("</caption>").append(System.lineSeparator());

		out.println(buffer.toString());
	}

	@Override
	public void writeObjectStart() {
		out.println("<table>");
	}

	@Override
	public void writeValue(String string) {
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

	@Override
	public void writeDescriptionRow(String description) {
		out.append(description).println();
	}

}
