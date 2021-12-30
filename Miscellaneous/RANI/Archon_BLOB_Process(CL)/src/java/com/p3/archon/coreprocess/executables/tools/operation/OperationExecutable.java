package com.p3.archon.coreprocess.executables.tools.operation;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;

public final class OperationExecutable {

	private String command;
	private TextOutputFormat format;
	private PrintWriter out;

	private String saveloc;
	private String fp;
	private String ext;
	private String encoding;
	private boolean version;
	private long xmlChunkLimit;
	private boolean showLobs;
	private boolean splitDate;
	private boolean isZip = false;
	private boolean versionInfoOption = false;
	private String schema;
	private String timeformat;
	private TreeMap<String, Long> srcColumnBlobCount;
	private TreeMap<String, Long> destColumnBlobCount;
	private LinkedHashMap<String, Object[]> datatype;

	public LinkedHashMap<String, Object[]> getDatatype() {
		return datatype;
	}

	public void setDatatype(LinkedHashMap<String, Object[]> datatype) {
		this.datatype = datatype;
	}

	private OperationOptions operationOptions;
	private String string;
	private String outputFormat;
	private Path path;

	private ExecutionHandler createdHandler;

	public OperationExecutable(final String schema, final String command, TextOutputFormat format, PrintWriter out,
			String saveloc, String encoding, boolean version, long xmlChunkLimit, boolean showLobs, boolean splitDate,
			boolean isZip, String timeformat) {
		this.command = command;
		this.format = format;
		this.out = out;
		this.saveloc = saveloc;
		this.encoding = encoding;
		this.version = version;
		this.xmlChunkLimit = xmlChunkLimit;
		this.showLobs = showLobs;
		this.splitDate = splitDate;
		this.schema = schema.toUpperCase();
		this.isZip = isZip;
		this.timeformat = timeformat;
		this.srcColumnBlobCount = new TreeMap<String, Long>();
		this.destColumnBlobCount = new TreeMap<String, Long>();
		this.datatype = new LinkedHashMap<String, Object[]>();
	}

	public OperationExecutable(final String schema, final String command) {
		this.schema = schema.toUpperCase();
		this.command = command;
		this.format = TextOutputFormat.xml;
	}

	public OperationExecutable(OperationOptions operationOptions, String string, String outputFormat, Path path,
			TextOutputFormat format, boolean versionInfoOption, String fp, String ext) {
		this.operationOptions = operationOptions;
		this.string = string;
		this.outputFormat = outputFormat;
		this.path = path;
		this.format = format;
		this.versionInfoOption = versionInfoOption;
		this.fp = fp;
		this.ext = ext;
	}

	private ExecutionHandler getExecutionHandler() throws Exception {
		final ExecutionHandler formatter;
		if (format == TextOutputFormat.json) {
			formatter = new DataJsonFormatter(schema, command, format, out, saveloc, encoding, version, xmlChunkLimit,
					showLobs, splitDate, isZip, timeformat, srcColumnBlobCount, destColumnBlobCount);
		} else if (format == TextOutputFormat.xml) {

			formatter = new DataXMLFormatter(schema, command, format, out, saveloc, encoding, version, xmlChunkLimit,
					showLobs, splitDate, isZip, timeformat, srcColumnBlobCount, destColumnBlobCount, datatype);
		} else {
			if (string.equals("file")) {
				if (versionInfoOption) {
					formatter = new DataFileFormatter(operationOptions, outputFormat, path, format);
				} else {
					formatter = new DataFileFormatter1(operationOptions, outputFormat, path, format, fp, ext);

				}
			} else
				formatter = new DataTextFormatter(schema, command, format, out, saveloc, encoding, version,
						xmlChunkLimit, showLobs, splitDate, isZip, timeformat, srcColumnBlobCount, destColumnBlobCount);
		}
		return formatter;
	}

	public ExecutionHandler getHandler() throws Exception {
		final ExecutionHandler handler = getExecutionHandler();
		createdHandler = handler;
		return handler;
	}

	public void executeOn(String title, final Connection connection, ExecutionHandler handler, String query)
			throws Exception {
		final Statement stmt = connection.createStatement();
		final ResultSet results = stmt.executeQuery(query);
		System.out.println("query ex " + query);

		try {
			System.out.println(results);
			handler.handleDataMain(title, results);
		} catch (final Exception e) {
			throw new Exception(e);
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (Exception e) {

				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void end(ExecutionHandler handler) throws Exception {
		handler.end();
	}

	public long getRecordCount() {
		return createdHandler.getRecordprocessed();
	}

	public TreeMap<String, Long> getSrcColumnBlobCount() {
		return srcColumnBlobCount;
	}

	public void setSrcColumnBlobCount(TreeMap<String, Long> srcColumnBlobCount) {
		this.srcColumnBlobCount = srcColumnBlobCount;
	}

	public TreeMap<String, Long> getDestColumnBlobCount() {
		return destColumnBlobCount;
	}

	public void setDestColumnBlobCount(TreeMap<String, Long> destColumnBlobCount) {
		this.destColumnBlobCount = destColumnBlobCount;
	}
}
