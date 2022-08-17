package com.p3.archon.coreprocess.executables.tools.utility;

import com.p3.archon.coreprocess.executables.tools.utility.common.Color;
import com.p3.archon.coreprocess.executables.tools.utility.html.Alignment;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONObject;

public interface TextFormattingHelper {

	enum DocumentHeaderType {
		title {
			@Override
			String getHeaderTag() {
				return "h1";
			}

			@Override
			String getPrefix() {
				return "<p>&#160;</p>";
			}
		},
		subTitle {
			@Override
			String getHeaderTag() {
				return "h2";
			}

			@Override
			String getPrefix() {
				return "<p>&#160;</p>";
			}
		},
		section {
			@Override
			String getHeaderTag() {
				return "h3";
			}

			@Override
			String getPrefix() {
				return "";
			}
		};

		abstract String getHeaderTag();

		abstract String getPrefix();

	}

	TextFormattingHelper append(String text);

	String createAnchor(String text, String link);

	String createLeftArrow();

	String createRightArrow();

	String createWeakLeftArrow();

	String createWeakRightArrow();

	void println();

	void writeDescriptionRow(String description);

	void writeDetailRow(String ordinal, String subName, boolean escapeText, String type, boolean emphasize);

	void writeDetailRow(String ordinal, String subName, String type);

	void writeDocumentEnd();

	void writeDocumentStart();

	void writeEmptyRow();

	void writeHeader(DocumentHeaderType type, String header);

	void writeNameRow(String name, String description);

	void writeObjectEnd();

	void writeObjectStart();

	void writeRow(Object... columnData);

	void writeRowHeader(String... columnNames);

	void writeWideRow(String definition, String style);

	void writeValue(String value);

	public void writeElementEnd();

	public void writeElementStart(String elm);

	public void writeRecordStart(String recordElm, String suffix);

	public void writeRecordEnd();

	public void writeRootElementEnd();

	public void writeRootElementStart(String rootElm);

	public void flush();

	public void newlineWriter();

	void writeAttribute(String name, String value);

	void writeCData(String value);

	void writeNameValueRow(String name, String value, Alignment valueAlignment);

	void writeObjectNameRow(String id, String name, String description, Color backgroundColor);

	void flushOnly();

	void write(JSONObject jsonRoot) throws Exception;

	void flushOutput();

}
