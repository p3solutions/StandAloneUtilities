package com.p3.archon.coreprocess.executables.tools.operation;

import static com.p3.archon.coreprocess.executables.tools.utility.common.Utility.readFully;
import static java.util.Objects.requireNonNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.p3.archon.coreprocess.executables.tools.common.ColumnInfo;
import com.p3.archon.coreprocess.executables.tools.utility.BinaryData;

/**
 * Text formatting of data.
 *
 * @author Sualeh Fatehi
 */
final class DataResultSet {

	private final ResultSet rows;

	public ResultSet getRows() {
		return rows;
	}

	private final List<ColumnInfo> resultsColumns;
	private final boolean showLobs;

	private String[] columnName;
	private String[] columnType;
	private int[] columnTypesJava;
	private int[] columnTypesJavaOrig;
	LinkedHashMap<String, Object[]> dataMap;

	public DataResultSet(final ResultSet rows, final boolean showLobs) throws Exception {
		this.rows = requireNonNull(rows, "Cannot use null results");
		this.showLobs = showLobs;
		resultsColumns = getResultColumns(rows);
	}

	public static List<ColumnInfo> getResultColumns(final ResultSet resultSet) {
		List<ColumnInfo> resultColumns = new ArrayList<ColumnInfo>();
		try {
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int colCount = rsmd.getColumnCount();

			for (int i = 1; i <= colCount; i++) {
				ColumnInfo ci = new ColumnInfo();
				ci.setName(rsmd.getColumnLabel(i));
				ci.setType(rsmd.getColumnTypeName(i));
				ci.setTypeid(rsmd.getColumnType(i));
				resultColumns.add(ci);
			}
		} catch (final SQLException e) {
			e.printStackTrace();
			resultColumns = null;
		}
		return resultColumns;
	}

	public String[] getColumnNames() {
		final int columnCount = resultsColumns.size();
		final String[] columnNames = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			columnNames[i] = resultsColumns.get(i).getName();
		}
		return columnNames;
	}

	public static String getTextFormatted(String string) {
		string = string.trim().replaceAll("[^_^\\p{Alnum}.]", "_").replace("^", "_").replaceAll("\\s+", "_");
		string = ((string.startsWith("_") && string.endsWith("_") && string.length() > 2)
				? string.substring(1).substring(0, string.length() - 2)
				: string);
		return string.length() > 0 ? ((string.charAt(0) >= '0' && string.charAt(0) <= '9') ? "_" : "") + string
				: string;
	}

	public static String getPriliminaryTextFormatted(String string) {
		String modstring = string.trim().replaceAll("[^_^\\p{Alnum}.]", "_").replace("^", "_").replaceAll("\\s+", "_");
		if (string.equals(modstring))
			return string;
		else
			return "_" + string + "_";

	}

	public void computeColumnInfo(LinkedHashMap<String, Object[]> dataMap) {
		final int columnCount = resultsColumns.size();
		columnName = new String[columnCount];
		columnType = new String[columnCount];
		columnTypesJava = new int[columnCount];
		columnTypesJavaOrig = new int[columnCount];
		for (int i = 0; i < columnCount; i++) {
			columnName[i] = getPriliminaryTextFormatted(resultsColumns.get(i).getName());
			columnType[i] = resultsColumns.get(i).getType().toUpperCase();
			columnTypesJavaOrig[i] = resultsColumns.get(i).getTypeid();
			if (showLobs) {
				if (columnTypesJavaOrig[i] == Types.CLOB || columnTypesJavaOrig[i] == Types.NCLOB
						|| columnTypesJavaOrig[i] == Types.LONGVARCHAR
						|| columnTypesJavaOrig[i] == Types.LONGNVARCHAR) {
					columnTypesJava[i] = 1;
				} else if (columnTypesJavaOrig[i] == Types.BLOB || columnTypesJavaOrig[i] == Types.LONGVARBINARY
						|| columnTypesJavaOrig[i] == Types.VARBINARY) {
					columnTypesJava[i] = 2;
				} else if (columnTypesJavaOrig[i] == Types.TIME) {
					columnTypesJava[i] = 3;
				} else {
					columnTypesJava[i] = 0;
				}
			} else if (columnTypesJavaOrig[i] == Types.TIME) {
				columnTypesJava[i] = 3;
			} else {
				columnTypesJava[i] = 0;
			}

			dataMap.put(getTextFormatted(columnName[i].toUpperCase()),
					new Object[] { columnType[i], columnTypesJava[i] });
		}
	}

	public String[] getColumnName() {
		return columnName;
	}

	public String[] getColumnType() {
		return columnType;
	}

	public int[] getColumnTypeJava() {
		return columnTypesJava;
	}

	public int[] getColumnTypeJavaOrig() {
		return columnTypesJavaOrig;
	}

	public boolean next() throws SQLException {
		return rows.next();
	}

	public List<Object> row() throws SQLException {
		final int columnCount = resultsColumns.size();
		final List<Object> currentRow = new ArrayList<>(columnCount);
		long totalTimeRec = System.currentTimeMillis();
		for (int i = 0; i < columnCount; i++)
			currentRow.add(getColumnData(i));
		return currentRow;
	}

	public int width() {
		return resultsColumns.size();
	}

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private final SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");

	DecimalFormat formatter;

	private Object getColumnData(final int i) throws SQLException {
		final int javaSqlType = resultsColumns.get(i).getTypeid();
		Object columnData;
		long startTimeRec;

		if (javaSqlType == Types.CLOB) {
			startTimeRec = getCurrentTime();
			final Clob clob = rows.getClob(i + 1);
			if (rows.wasNull() || clob == null) {
				columnData = null;
			} else {
				columnData = readClob(clob, null);
			}
		} else if (javaSqlType == Types.NCLOB) {
			startTimeRec = getCurrentTime();
			final NClob nClob = rows.getNClob(i + 1);
			if (rows.wasNull() || nClob == null) {
				columnData = null;
			} else {
				columnData = readClob(nClob, null);
			}
		} else if (javaSqlType == Types.BLOB) {
			startTimeRec = getCurrentTime();
			final Blob blob = rows.getBlob(i + 1);
			if (rows.wasNull() || blob == null) {
				columnData = null;
			} else {
				columnData = readBlob(blob);
			}

		} else if (javaSqlType == Types.LONGVARBINARY || javaSqlType == Types.VARBINARY) {
			startTimeRec = getCurrentTime();

			if (resultsColumns.get(i).getType().equalsIgnoreCase("RAW")) {
				try {
					final Blob blob = rows.getBlob(i + 1);
					final InputStream stream = rows.getBinaryStream(i + 1);
					if (rows.wasNull() || stream == null) {
						columnData = null;
					} else {
						columnData = readBlobStream(stream, blob);
					}
				} catch (Exception e) {
					final InputStream stream = rows.getAsciiStream(i + 1);
					if (rows.wasNull() || stream == null) {
						columnData = null;
					} else {
						columnData = readBlobStream(stream, null);
					}
				}

			} else if (resultsColumns.get(i).getType().equalsIgnoreCase("LONG VARCHAR FOR BIT DATA")) {
				System.out.println((resultsColumns.get(i).getType()));
				try {
					final InputStream stream = rows.getBinaryStream(i + 1);
					if (rows.wasNull() || stream == null) {
						columnData = null;
					} else {
						columnData = readBinaryStream(stream);
					}
				} catch (Exception e) {
					columnData = null;
				}

			} else {

				try {
					final Blob blob = rows.getBlob(i + 1);
					final InputStream stream = rows.getBinaryStream(i + 1);
					if (rows.wasNull() || stream == null) {
						columnData = null;
					} else {
						columnData = readBlobStream(stream, blob);
					}
				} catch (Exception e) {
					final InputStream stream = rows.getBinaryStream(i + 1);
					if (rows.wasNull() || stream == null) {
						columnData = null;
					} else {
						columnData = readBlobStream(stream, null);
					}
				}

			}

		} else if (javaSqlType == Types.LONGNVARCHAR || javaSqlType == Types.LONGVARCHAR) {
			startTimeRec = getCurrentTime();
			if (resultsColumns.get(i).getType().equalsIgnoreCase("LONG")) {
				final Object objectValue = rows.getObject(i + 1);
				if (rows.wasNull() || objectValue == null)
					columnData = null;
				else {
					columnData = objectValue;
				}

			} else {
				final InputStream stream = rows.getAsciiStream(i + 1);
				if (rows.wasNull() || stream == null) {
					columnData = null;
				} else {
					columnData = readStream(stream, null);
				}
			}
		} else if (javaSqlType == Types.DATE || resultsColumns.get(i).getType().equalsIgnoreCase("DATE")) {
			startTimeRec = getCurrentTime();
			final Date datevalue = rows.getDate(i + 1);
			if (rows.wasNull() || datevalue == null) {
				columnData = null;
			} else {
				try {
					java.sql.Date ts = rows.getDate(i + 1);
					Date date = new Date();
					date.setTime(ts.getTime());
					String formattedDate = dateOnlyFormat.format(date);
					columnData = formattedDate;
				} catch (Exception e) {
					columnData = rows.getString(i + 1);
				}

			}
		} else if (javaSqlType == Types.TIMESTAMP || javaSqlType == Types.TIMESTAMP_WITH_TIMEZONE) {
			startTimeRec = getCurrentTime();
			final Timestamp timestampValue = rows.getTimestamp(i + 1);
			if (rows.wasNull() || timestampValue == null) {
				columnData = null;
			} else {
				try {
					Timestamp ts = rows.getTimestamp(i + 1);
					Date date = new Date();
					date.setTime(ts.getTime());
					String formattedDate = dateFormat.format(date);
					columnData = formattedDate;
				} catch (Exception e) {
					columnData = rows.getTimestamp(i + 1);
				}
			}
		} else if (javaSqlType == Types.BIT) {
			startTimeRec = getCurrentTime();
			final boolean booleanValue = rows.getBoolean(i + 1);
			final String stringValue = rows.getString(i + 1);
			if (rows.wasNull()) {
				columnData = null;
			} else {
				columnData = stringValue.equalsIgnoreCase(Boolean.toString(booleanValue)) ? booleanValue : stringValue;
			}

		} else if (javaSqlType == Types.FLOAT) {
			startTimeRec = getCurrentTime();
			final float floatValue = rows.getFloat(i + 1);
			if (rows.wasNull()) {
				columnData = null;
			} else {
				float value = (float) floatValue;
				if (Math.abs(value - (int) value) > 0.0)
					formatter = new DecimalFormat(
							"#.##########################################################################################################################################################################################################################");
				else
					formatter = new DecimalFormat("#");
				columnData = formatter.format(value);
			}
		} else if (javaSqlType == Types.DOUBLE) {
			startTimeRec = getCurrentTime();
			final double doubleValue = rows.getDouble(i + 1);
			if (rows.wasNull()) {
				columnData = null;
			} else {
				double value = (double) doubleValue;
				if (Math.abs(value - (int) value) > 0.0)
					formatter = new DecimalFormat(
							"#.##########################################################################################################################################################################################################################");
				else
					formatter = new DecimalFormat("#");
				columnData = formatter.format(value);
			}
		} else {
			startTimeRec = getCurrentTime();
			final Object objectValue = rows.getObject(i + 1);
			if (rows.wasNull() || objectValue == null)
				columnData = null;
			else
				columnData = objectValue;
		}
		return columnData;
	}

	private long getCurrentTime() {
		return System.currentTimeMillis();
	}

	private BinaryData readBlobStream(final InputStream stream, final Blob blob) {
		if (stream == null) {
			return null;
		} else if (showLobs) {
			final BufferedInputStream in = new BufferedInputStream(stream);
			final BinaryData lobData = new BinaryData(readFully(in), blob, null);
			return lobData;
		} else {
			return new BinaryData();
		}
	}

	private BinaryData readBinaryStream(final InputStream stream) {
		if (stream == null) {
			return null;
		} else if (showLobs) {

			byte[] byteArray = null;
			try {
				byteArray = IOUtils.toByteArray(stream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			InputStream input1 = new ByteArrayInputStream(byteArray);
			InputStream input2 = new ByteArrayInputStream(byteArray);

			final BinaryData lobData = new BinaryData(readFully(input1), null, input2);
			if (lobData == null || lobData.toString().isEmpty())
				return null;
			else
				return lobData;
		} else {
			return new BinaryData();
		}
	}

	public BinaryData readBlob(final Blob blob) {
		if (blob == null) {
			return null;
		} else if (showLobs) {
			InputStream in = null;
			BinaryData lobData;
			try {
				try {
					in = blob.getBinaryStream();
				} catch (final SQLFeatureNotSupportedException e) {
					// LOGGER.log(Level.FINEST, "Could not read BLOB data", e);
					in = null;
				}

				if (in != null) {
					lobData = new BinaryData(readFully(in), blob, null);
				} else {
					lobData = new BinaryData();
				}
			} catch (final SQLException e) {
				// LOGGER.log(Level.WARNING, "Could not read BLOB data", e);
				lobData = new BinaryData();
			}
			return lobData;
		} else {
			return new BinaryData();
		}
	}

	public BinaryData readClob(final Clob clob, final Blob blob) {
		if (clob == null) {
			return null;
		} else if (showLobs) {
			Reader rdr = null;
			BinaryData lobData;
			try {
				try {
					rdr = clob.getCharacterStream();
				} catch (final SQLFeatureNotSupportedException e) {
					// LOGGER.log(Level.FINEST, "Could not read CLOB data, as
					// character stream", e);
					rdr = null;
				}
				if (rdr == null) {
					try {
						rdr = new InputStreamReader(clob.getAsciiStream());
					} catch (final SQLFeatureNotSupportedException e) {
						// LOGGER.log(Level.FINEST, "Could not read CLOB data,
						// as ASCII stream", e);
						rdr = null;
					}
				}

				if (rdr != null) {
					String lobDataString = readFully(rdr);
					if (lobDataString.isEmpty()) {
						// Attempt yet another read
						final long clobLength = clob.length();
						lobDataString = clob.getSubString(1, (int) clobLength);
					}
					lobData = new BinaryData(lobDataString, blob, null);
				} else {
					lobData = new BinaryData();
				}
			} catch (final SQLException e) {
				// LOGGER.log(Level.WARNING, "Could not read CLOB data", e);
				lobData = new BinaryData();
			}
			return lobData;
		} else {
			return new BinaryData();
		}
	}

	/**
	 * Reads data from an input stream into a string. Default system encoding is
	 * assumed.
	 *
	 * @param stream Column data object returned by JDBC
	 * @return A string with the contents of the LOB
	 */
	private BinaryData readStream(final InputStream stream, final Blob blob) {
		if (stream == null) {
			return null;
		} else if (showLobs) {
			final BufferedInputStream in = new BufferedInputStream(stream);
			final BinaryData lobData = new BinaryData(readFully(in), blob, null);
			return lobData;
		} else {
			return new BinaryData();
		}
	}

}
