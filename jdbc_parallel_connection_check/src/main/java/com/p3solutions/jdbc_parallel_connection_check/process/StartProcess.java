package com.p3solutions.jdbc_parallel_connection_check.process;

import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.p3solutions.jdbc_parallel_connection_check.beans.InputBean;
import com.p3solutions.jdbc_parallel_connection_check.connection.ConnectionPool;
import com.p3solutions.jdbc_parallel_connection_check.connection.JDBCConnection;
import com.p3solutions.jdbc_parallel_connection_check.connection.JDBCConnector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.BinaryData;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static utils.Utility.readFully;

public class StartProcess extends JDBCConnection {

    private static Logger logger = LogManager.getLogger(StartProcess.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
    DecimalFormat formatter;
    private InputBean inputBean;
    private List<String> tableList = null;
    private Integer counter = 0;


    public StartProcess(InputBean inputBean) {
        super(inputBean);
        this.inputBean = inputBean;
    }

    public void start() throws InterruptedException, SQLException {


        System.out.println("Establishing connection and gathering table information");
        tableList = new ArrayList<>();


        try {

            ResultSet rs = connection.getMetaData().getTables(null, inputBean.getScheamName(), "%", new String[]{"TABLE", "VIEW"});
            while (rs.next()) {
                tableList.add(rs.getString(3));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        openConnections(inputBean);
        for (String tableName : tableList) {
            while (counter == inputBean.getMpp()) {
                Thread.sleep(500);
            }

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    ConnectionPool cp = JDBCConnection.getFreeConnection(cps);
                    logger.info("Connection : '" + cp.getId() + "' - " + tableName
                            + " --> Extracting Data for Table : " + tableName);
                    Statement stmt = null;
                    ResultSet results = null;
                    try {
                        stmt = cp.getConnection().createStatement();
                        System.out.println("Excuting " + "select * from " + inputBean.getDbName() + "." + inputBean.getScheamName() + "." + tableName);
                        results = stmt.executeQuery("select * from " + inputBean.getDbName() + "." + inputBean.getScheamName() + "." + tableName);
                        ResultSetMetaData rsm = results.getMetaData();
                        while (results.next()) {
                            final List<Object> currentRow = new ArrayList<>(rsm.getColumnCount());

                            for (int i = 1; i <= rsm.getColumnCount(); i++) {

                                int type = rsm.getColumnType(i);

                                Object columnData;
                                if (rsm.getColumnTypeName(i).equalsIgnoreCase("geometry")) {

                                    final Object objectValue = ((SQLServerResultSet) results).getGeometry(i);
                                    if (results.wasNull() || objectValue == null)
                                        columnData = null;
                                    else
                                        columnData = objectValue;

                                } else if (rsm.getColumnTypeName(i).equalsIgnoreCase("geography")) {

                                    final Object objectValue = ((SQLServerResultSet) results).getGeography(i);
                                    if (results.wasNull() || objectValue == null)
                                        columnData = null;
                                    else
                                        columnData = objectValue;

                                } else if (type == Types.SQLXML) {
                                    SQLXML xmlVal = results.getSQLXML(i);
                                    String xml = xmlVal.getString();
                                    if (results.wasNull() || xml == null) {
                                        columnData = null;
                                    } else {
                                        columnData = (xml);
                                    }
                                } else if (type == Types.CLOB) {
                                    final Clob clob = results.getClob(i);
                                    if (results.wasNull() || clob == null) {
                                        columnData = null;
                                    } else {
                                        columnData = readClob(clob, null);
                                    }
                                } else if (type == Types.NCLOB) {
                                    final NClob nClob = results.getNClob(i);
                                    if (results.wasNull() || nClob == null) {
                                        columnData = null;
                                    } else {
                                        columnData = readClob(nClob, null);
                                    }
                                } else if (type == Types.BLOB) {
                                    final Blob blob = results.getBlob(i);
                                    if (results.wasNull() || blob == null) {
                                        columnData = null;
                                    } else {
                                        columnData = readBlob(blob);
                                    }
                                } else if (type == Types.LONGVARBINARY || type == Types.VARBINARY) {

                                    if (inputBean.getSl()) {
                                        if (rsm.getColumnTypeName(i).equalsIgnoreCase("RAW")) {

                                            try {
                                                final Blob blob = results.getBlob(i);
                                                final InputStream stream = results.getBinaryStream(i);
                                                if (results.wasNull() || stream == null) {
                                                    columnData = null;
                                                } else {
                                                    columnData = readStream(stream, blob);
                                                }
                                            } catch (Exception e) {
                                                final InputStream stream = results.getAsciiStream(i);
                                                if (results.wasNull() || stream == null) {
                                                    columnData = null;
                                                } else {
                                                    columnData = readStream(stream, null);
                                                }
                                            }

                                        } else if (rsm.getColumnTypeName(i).equalsIgnoreCase("VARCHAR FOR BIT DATA")) {

                                            final Object objectValue = results.getString(i);
                                            if (results.wasNull() || objectValue == null)
                                                columnData = null;
                                            else
                                                columnData = objectValue;

                                        } else {

                                            try {
                                                final Blob blob = results.getBlob(i);
                                                final InputStream stream = results.getBinaryStream(i);
                                                if (results.wasNull() || stream == null) {
                                                    columnData = null;
                                                } else {
                                                    columnData = readStream(stream, blob);
                                                }
                                            } catch (Exception e) {
                                                final InputStream stream = results.getBinaryStream(i);
                                                if (results.wasNull() || stream == null) {
                                                    columnData = null;
                                                } else {
                                                    columnData = readStream(stream, null);
                                                }
                                            }

                                        }

                                    } else {
                                        columnData = new BinaryData();

                                    }

                                } else if (type == Types.LONGNVARCHAR || type == Types.LONGVARCHAR) {
                                    if (rsm.getColumnTypeName(i).equalsIgnoreCase("LONG")) {
                                        final Object objectValue = results.getObject(i);
                                        if (results.wasNull() || objectValue == null)
                                            columnData = null;
                                        else
                                            columnData = objectValue;
                                    } else {

                                        try {
                                            final InputStream stream = results.getAsciiStream(i);
                                            if (results.wasNull() || stream == null) {
                                                columnData = null;
                                            } else {
                                                columnData = readStream(stream, null);
                                            }
                                        } catch (Exception e) {
                                            SQLXML xmlVal = results.getSQLXML(i);
                                            String xml = xmlVal.getString();
                                            if (results.wasNull() || xml == null) {
                                                columnData = null;
                                            } else {
                                                columnData = (xml);
                                            }
                                        }

                                    }
                                } else if (type == Types.DATE || rsm.getColumnTypeName(i).equalsIgnoreCase("DATE")) {
                                    final java.util.Date datevalue = results.getDate(i);
                                    if (results.wasNull() || datevalue == null) {
                                        columnData = null;
                                    } else {
                                        try {
                                            // System.out.println("DATE VALUE : " + rows.getString(i +
                                            // 1));
                                            java.sql.Date ts = results.getDate(i);
                                            java.util.Date date = new java.util.Date();
                                            date.setTime(ts.getTime());
                                            String formattedDate;
                                            if (inputBean.getDt())
                                                formattedDate = dateFormat.format(date);
                                            else
                                                formattedDate = dateOnlyFormat.format(date);
                                            columnData = formattedDate;
                                        } catch (Exception e) {
                                            columnData = results.getString(i);
                                        }
                                    }
                                } else if (type == Types.TIMESTAMP || type == Types.TIMESTAMP_WITH_TIMEZONE
                                        || rsm.getColumnTypeName(i).equalsIgnoreCase("TIMESTAMP WITH TIME ZONE")) {
                                    final Timestamp timestampValue = results.getTimestamp(i);
                                    if (results.wasNull() || timestampValue == null) {
                                        columnData = null;
                                    } else {
                                        try {
                                            Timestamp ts = results.getTimestamp(i);
                                            java.util.Date date = new java.util.Date();
                                            date.setTime(ts.getTime());
                                            String formattedDate = dateFormat.format(date);
                                            columnData = formattedDate;
                                        } catch (Exception e) {
                                            columnData = results.getTimestamp(i);
                                        }
                                    }
                                } else if (type == Types.BIT) {
                                    final boolean booleanValue = results.getBoolean(i);
                                    final String stringValue = results.getString(i);
                                    if (results.wasNull()) {
                                        columnData = null;
                                    } else {
                                        columnData = stringValue.equalsIgnoreCase(Boolean.toString(booleanValue)) ? booleanValue : stringValue;
                                    }

                                } else if (type == Types.FLOAT /* || type == Types.REAL */) {
                                    final float floatValue = results.getFloat(i);
                                    if (results.wasNull()) {
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
                                } else if (type == Types.DOUBLE) {
                                    final double doubleValue = results.getDouble(i);
                                    if (results.wasNull()) {
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


                                    if (rsm.getColumnTypeName(i).equalsIgnoreCase("CHAR FOR BIT DATA")) {

                                        final Object objectValue = results.getString(i);
                                        if (results.wasNull() || objectValue == null)
                                            columnData = null;
                                        else
                                            columnData = objectValue;

                                    } else {
                                        final Object objectValue = results.getObject(i);
                                        if (results.wasNull() || objectValue == null)
                                            columnData = null;
                                        else
                                            columnData = objectValue;
                                    }
                                }
                                currentRow.add(columnData);
                            }

                            currentRow.clear();

                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } finally {
                        try {
                            stmt.close();
                            results.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }

                    logger.info("Connection : '" + cp.getId() + "' - " + tableName
                            + " --> Extraction of Data for Table : " + tableName + " Completed");
                    cp.setInUse(false);
                    counter--;
                }
            });
            counter++;
            t.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sleeper();
        closeAllConnections();
        connection.close();
    }

    public BinaryData readClob(final Clob clob, final Blob blob) {
        if (clob == null) {
            return null;
        } else {
            Reader rdr = null;
            BinaryData lobData;
            try {
                try {
                    rdr = clob.getCharacterStream();
                } catch (final SQLFeatureNotSupportedException e) {
                    rdr = null;
                }
                if (rdr == null) {
                    try {
                        rdr = new InputStreamReader(clob.getAsciiStream());
                    } catch (final SQLFeatureNotSupportedException e) {
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
                    lobData = new BinaryData(lobDataString, blob);
                } else {
                    lobData = new BinaryData();
                }
            } catch (final SQLException e) {
                lobData = new BinaryData();
            }
            return lobData;
        }
    }

    public BinaryData readBlob(final Blob blob) {
        if (blob == null) {
            return null;
        } else {
            InputStream in = null;
            BinaryData lobData;
            try {
                try {
                    in = blob.getBinaryStream();
                } catch (final SQLFeatureNotSupportedException e) {
                    in = null;
                }

                if (in != null) {
                    lobData = new BinaryData(readFully(in), blob);
                } else {
                    lobData = new BinaryData();
                }
            } catch (final SQLException e) {
                lobData = new BinaryData();
            }
            return lobData;
        }
    }

    private BinaryData readStream(final InputStream stream, final Blob blob) {
        if (stream == null) {
            return null;
        } else {
            final BufferedInputStream in = new BufferedInputStream(stream);
            final BinaryData lobData = new BinaryData(readFully(in), blob);
            return lobData;
        }
    }

    private void sleeper() {
        while (counter != 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
