package com.p3.archon.coreprocess.executables.tools.operation;

import static com.p3.archon.coreprocess.executables.tools.utility.common.Utility.getRowCountMessage;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.TreeMap;

import com.p3.archon.coreprocess.executables.tools.base.BaseFormatter;
import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.TextFormattingHelper.DocumentHeaderType;
import com.p3.archon.coreprocess.executables.tools.utility.common.Color;
import com.p3.archon.coreprocess.executables.tools.utility.html.Alignment;

@SuppressWarnings("unused")
final class DataTextFormatter extends BaseFormatter implements ExecutionHandler {

	private static final String OPERATION_COUNT = "count";
	private static final String OPERATION_COUNT_DESC = "Row Count";
	private int dataBlockCount;
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

	DataTextFormatter(final String schema, final String command, TextOutputFormat format, PrintWriter out,
			String saveloc, String encoding, boolean version, long xmlChunkLimit, boolean isShowLobs,
			boolean isSplitDate, boolean isZip, String timeformat, TreeMap<String, Long> srcColumnBlobCount,
			TreeMap<String, Long> destColumnBlobCount) {
		super(out, format, false);
		this.saveloc = saveloc;
		this.command = command;
		this.format = format;
		this.encoding = encoding;
		this.version = version;
		this.xmlChunkLimit = xmlChunkLimit;
		this.isShowLobs = isShowLobs;
		this.isSplitDate = isSplitDate;
		this.isZip = isZip;
		this.schema = schema;
		this.setRecordprocessed(0);
		this.timeformat = timeformat;
		this.srcColumnBlobCount = srcColumnBlobCount;
		this.destColumnBlobCount = destColumnBlobCount;
	}

	@Override
	public void handleDataMain(final String title, final ResultSet rows) throws Exception {
		handleData(title, rows);
	}

	private String getMessage(final double aggregate) {
		final Number number;
		if (Math.abs(aggregate - (int) aggregate) < 1E-10D) {
			number = Integer.valueOf((int) aggregate);
		} else {
			number = Double.valueOf(aggregate);
		}
		final String message = getRowCountMessage(number);
		return message;
	}

	private void handleAggregateOperationForTable(final String title, final ResultSet results) throws Exception {
		long aggregate = 0;
		try {
			if (results.next()) {
				aggregate = results.getLong(1);
				recordprocessed = aggregate;
			}
		} catch (final SQLException e) {
			throw new Exception("Could not obtain aggregate data", e);
		}
		final String message = getMessage(aggregate);
		formattingHelper.writeNameValueRow(title, message, Alignment.right);
	}

	private static boolean countHeaderflag = true;

	private void handleData(final String title, final ResultSet rows) throws Exception {
		if (rows == null) {
			return;
		}

		if (dataBlockCount == 0 && countHeaderflag) {
			formattingHelper.writeDocumentStart();
			printHeader();
		}

		if (countHeaderflag && command.toLowerCase().equals(OPERATION_COUNT) && (format == TextOutputFormat.text
				|| format == TextOutputFormat.csv || format == TextOutputFormat.tsv)) {
			formattingHelper.println();
			formattingHelper.println();
			formattingHelper.writeNameValueRow("TABLE NAME", "ROWS", Alignment.right);
		}

		if (command.toLowerCase().equals(OPERATION_COUNT)) {
			countHeaderflag = false;
			handleAggregateOperationForTable(title, rows);
		} else {
			formattingHelper.println();
			formattingHelper.println();
			formattingHelper.writeObjectStart();
			formattingHelper.writeObjectNameRow("", title, "", Color.white);
			try {
				final DataResultSet dataRows = new DataResultSet(rows, isShowLobs);
				formattingHelper.writeRowHeader(dataRows.getColumnNames());
				iterateRows(dataRows);
			} catch (final SQLException e) {
				throw new Exception(e.getMessage(), e);
			}
			formattingHelper.writeObjectEnd();
			formattingHelper.writeDocumentEnd();
		}

		dataBlockCount++;

		formattingHelper.writeDocumentEnd();
	}

	private void iterateRows(final DataResultSet dataRows) throws SQLException {
		while (dataRows.next()) {
			final List<Object> currentRow = dataRows.row();
			final Object[] columnData = currentRow.toArray(new Object[currentRow.size()]);
			formattingHelper.writeRow(columnData);
			recordprocessed++;
		}
	}

	private void printHeader() {
		if (command.toLowerCase().equals(OPERATION_COUNT)) {
			formattingHelper.writeObjectStart();
			formattingHelper.writeObjectNameRow("", OPERATION_COUNT_DESC, "", Color.white);
		} else {
			formattingHelper.writeHeader(DocumentHeaderType.subTitle, "Query");
		}
	}

	@Override
	public void end() throws Exception {
	}
}
