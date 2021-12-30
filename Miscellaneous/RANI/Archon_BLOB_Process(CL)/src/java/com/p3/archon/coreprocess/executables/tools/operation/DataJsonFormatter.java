package com.p3.archon.coreprocess.executables.tools.operation;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

import com.p3.archon.coreprocess.executables.tools.base.BaseJsonFormatter;
import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONArray;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONException;
import com.p3.archon.coreprocess.executables.tools.utility.org.json.JSONObject;

@SuppressWarnings("unused")
final class DataJsonFormatter extends BaseJsonFormatter implements ExecutionHandler {

	private static final String OPERATION_COUNT = "count";

	private final JSONArray jsonDataArray;
	private final String command;

	private final TextOutputFormat format;
	private final String saveloc;
	private final String encoding;
	private final boolean version;
	private final long xmlChunkLimit;
	private final boolean isShowLobs;
	private final boolean isSplitDate;
	private final boolean isZip;
	private final String schema;
	private long recordprocessed;
	private String timeformat;
	private TreeMap<String, Long> srcColumnBlobCount;
	private TreeMap<String, Long> destColumnBlobCount;

	@Override
	public long getRecordprocessed() {
		return recordprocessed;
	}

	public void setRecordprocessed(long recordprocessed) {
		this.recordprocessed = recordprocessed;
	}

	@Override
	public void end() throws Exception {
		super.end();
	}

	DataJsonFormatter(final String schema, final String command, TextOutputFormat format, PrintWriter out,
			String saveloc, String encoding, boolean version, long xmlChunkLimit, boolean isShowLobs,
			boolean isSplitDate, boolean isZip, String timeformat, TreeMap<String, Long> srcColumnBlobCount,
			TreeMap<String, Long> destColumnBlobCount) throws Exception {
		super(out, format);
		this.schema = schema;
		this.saveloc = saveloc;
		this.command = command;
		this.format = format;
		this.encoding = encoding;
		this.version = version;
		this.xmlChunkLimit = xmlChunkLimit;
		this.isShowLobs = isShowLobs;
		this.isSplitDate = isSplitDate;
		this.isZip = isZip;
		jsonDataArray = new JSONArray();
		jsonRoot.put("data", jsonDataArray);
		this.timeformat = timeformat;
		this.srcColumnBlobCount = srcColumnBlobCount;
		this.destColumnBlobCount = destColumnBlobCount;

	}

	@Override
	public void handleDataMain(final String title, final ResultSet rows) throws Exception {
		handleData(title, rows);
	}

	private long handleAggregateOperationForTable(final String title, final ResultSet results) throws Exception {
		try {
			long aggregate = 0;
			if (results.next()) {
				aggregate = results.getLong(1);
				recordprocessed = aggregate;
			}
			return aggregate;
		} catch (final SQLException e) {
			throw new Exception("Could not obtain aggregate data", e);
		}
	}

	private static boolean countHeaderflag = true;

	private void handleData(final String title, final ResultSet rows) throws Exception {
		if (rows == null) {
			return;
		}

		try {
			final JSONObject jsonData = new JSONObject();
			jsonData.put("schema", schema);
			jsonData.put("table", title);

			if (command.toLowerCase().equals(OPERATION_COUNT)) {
				final long aggregate = handleAggregateOperationForTable(title, rows);
				jsonData.put("rows", aggregate);
			} else {
				try {
					final DataResultSet dataRows = new DataResultSet(rows, isShowLobs);

					jsonData.put("columnNames", new JSONArray(dataRows.getColumnNames()));

					final JSONArray jsonRows = iterateRows(dataRows);
					jsonData.put("rows", jsonRows);
				} catch (final SQLException e) {
					throw new Exception(e.getMessage(), e);
				}
			}

			jsonDataArray.put(jsonData);
		} catch (final JSONException e) {
			throw new Exception("Could not convert data to JSON", e);
		}

	}

	private JSONArray iterateRows(final DataResultSet dataRows) throws SQLException {
		final JSONArray jsonRows = new JSONArray();
		// int recordsProcessed = 0;
		while (dataRows.next()) {
			final List<Object> currentRow = dataRows.row();
			jsonRows.put(new JSONArray(currentRow));
			recordprocessed++;
		}
		return jsonRows;
	}
}
