package com.p3.archon.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.p3.archon.bean.ArchonInputBean;
import com.p3.archon.utils.JobLogger;

public class StartProcess {
	private ArchonInputBean ipargs;
	private static final String ENCODING = "UTF-8";
	private int level = 0;
	private int i = 0;
	private String tabSpace = "\n";
	boolean hasblob = false;
	private static final boolean GENERATE_ATTACHEMENTS = true;
	private static final String BLOB_PREFIX = "BLOBS_";

	Map<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();

	public StartProcess(ArchonInputBean ipargs) {
		this.ipargs = ipargs;
	}

	public void startProcessing() throws SQLException, IOException {

		if (ipargs.isSip()) {
			ipargs.checkRpx();

			File filePath = new File(ipargs.getInputPath());

			File[] lof = filePath.listFiles();

			for (File file : lof) {

				if (file.getAbsolutePath().endsWith(".xml")) {
					String schemaName = file.getName().substring(0, file.getName().indexOf("-"));

					String tableName = file.getName().substring(file.getName().indexOf("-") + 1,
							file.getName().lastIndexOf("-"));
					ArrayList<String> schList = new ArrayList<String>();
					if (map.containsKey(schemaName)) {
						schList.addAll(map.get(schemaName));
						schList.add(tableName);
					} else {
						schList.add(tableName);

					}
					map.put(schemaName, schList);

				}

			}
			for (Entry<String, ArrayList<String>> schemaTableList : map.entrySet()) {
				JobLogger.getLogger().info("SipGen", StartProcess.class.getName(), "startProcessing",
						"Processing....  " + "Schema Name -> " + schemaTableList.getKey());

				for (String tn : schemaTableList.getValue()) {

					SipPackager sp = new SipPackager(schemaTableList.getKey(), tn, ipargs);
					try {
						JobLogger.getLogger().info("SipGen", StartProcess.class.getName(), "startProcessing",
								"Processing....  " + "Table Name -> " + tn);
						sp.generateSip();
						JobLogger.getLogger().info("SipGen", StartProcess.class.getName(), "startProcessing",
								"Processed....  " + "Table Name -> " + tn);
					} catch (Exception e) {
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						JobLogger.getLogger().error("SipGen", StartProcess.class.getName(), "startProcessing",
								errors.toString() + "\nTerminating ... ");
					}
				}
				JobLogger.getLogger().info("SipGen", StartProcess.class.getName(), "startProcessing",
						"Processed....  " + "Schema Name -> " + schemaTableList.getKey());

			}

		}

		if (ipargs.isPdiGen()) {

			List<String> tn = new ArrayList<String>();
			String ConnectionUrl = null;
			switch (ipargs.getServer().toLowerCase()) {
			case "mainframe-db2":
				ConnectionUrl = "jdbc:db2://" + ipargs.getHost() + ":" + ipargs.getPort() + "/" + ipargs.getLocation();
				break;
			case "db2":
				ConnectionUrl = "jdbc:db2://" + ipargs.getHost() + ":" + ipargs.getPort() + "/" + ipargs.getDatabase()
						+ ";retrieveMessagesFromServerOnGetMessage=true;";
				break;
			case "mysql":
				ConnectionUrl = "jdbc:mysql://" + ipargs.getHost() + ":" + ipargs.getPort() + "/" + ipargs.getDatabase()
						+ "";
				break;

			default:
				break;
			}

			Connection connection = DriverManager.getConnection(ConnectionUrl, ipargs.getUsername(),
					ipargs.getPassword());

			String[] tableNames = null;
			if (ipargs.getTableName() != null) {
				tableNames = ipargs.getTableName().split(",");
			}
			if (tableNames != null && tableNames.length != 0) {
				for (String string : tableNames) {
					tn.add(string);
				}
			} else {

				switch (ipargs.getServer().toLowerCase()) {
				case "mainframe-db2":
					Statement statement = null;
					ResultSet resultSet = null;
					statement = connection.createStatement();
					resultSet = statement.executeQuery("SELECT Name FROM SYSIBM.SYSTABLES WHERE DBNAME='"
							+ ipargs.getDatabase() + "' and CREATOR='" + ipargs.getSchema() + "' AND type ='T';");
					while (resultSet.next()) {

						tn.add(resultSet.getString(1));
					}

					statement.close();
					resultSet.close();
					break;
				case "db2":
				case "mysql":

					ResultSet rs = connection.getMetaData().getTables(null, ipargs.getSchema(), "%",
							new String[] { "TABLE", "VIEW" });
					while (rs.next()) {
						tn.add(rs.getString("TABLE_NAME"));

					}
					rs.close();

					break;

				default:
					break;
				}

			}

			for (String string : tn) {
				String formattedTable = getTextFormatted(string);
				String formattedSchema = getTextFormatted(ipargs.getSchema());
				String holding = ipargs.getHolding().replace(" ", "") + "_" + formattedSchema + "_" + formattedTable;
				String namespace = generateNameSpace(holding);

				String pdipath = ipargs.getOutputPath() + File.separator + "PDIs";
				File path = new File(pdipath);
				if (!path.exists())
					path.mkdirs();

				Writer out = null;
				out = new OutputStreamWriter(new FileOutputStream(
						path.getAbsolutePath() + File.separator + "pdi-schema_" + holding + ".xsd"), ENCODING);

				out.write("<?xml version=\"1.0\" encoding=\"");
				out.write(ENCODING);
				out.write("\"?>\n");

				out.write(getTabSpace(level++)
						+ "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" ");
				out.write("targetNamespace=\"");
				out.write(namespace + "\" ");
				out.write("xmlns=\"");
				out.write(namespace + "\" ");
				out.write("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");
				out.write(">");
				out.write(getTabSpace(level++) + "<xs:element name=\"" + formattedTable + "\">");
				out.write(getTabSpace(level++) + "<xs:complexType>");
				out.write(getTabSpace(level++) + "<xs:sequence>");
				out.write(getTabSpace(level++) + "<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\""
						+ "ROW\">");
				out.write(getTabSpace(level++) + "<xs:complexType>");
				out.write(getTabSpace(level++) + "<xs:sequence>");

				Statement columnStatement = connection.createStatement();
				ResultSet columnsTable = columnStatement
						.executeQuery("select * from " + ipargs.getSchema() + "." + string);
				ResultSetMetaData rsmd = columnsTable.getMetaData();
				int colCount = rsmd.getColumnCount();

				for (int i = 1; i <= colCount; i++) {
					String columnName = rsmd.getColumnLabel(i);

					String columnType = "VARCHAR";
					try {
						columnType = getColumnType(rsmd.getColumnTypeName(i));
					} catch (Exception e) {
						columnType = "VARCHAR";
					}

					int columnTypeCategory = 0;

					if (ipargs.isShowlobs()) {
						if (rsmd.getColumnType(i) == Types.CLOB || rsmd.getColumnType(i) == Types.NCLOB
								|| rsmd.getColumnType(i) == Types.LONGVARCHAR
								|| rsmd.getColumnType(i) == Types.LONGNVARCHAR) {
							columnTypeCategory = 1;
						} else if (rsmd.getColumnType(i) == Types.BLOB || rsmd.getColumnType(i) == Types.LONGVARBINARY
								|| rsmd.getColumnType(i) == Types.VARBINARY) {
							if (rsmd.getColumnTypeName(i).equalsIgnoreCase("LONG VARCHAR FOR BIT DATA")) {
								columnTypeCategory = 1;
							} else
								columnTypeCategory = 2;
						} else if (rsmd.getColumnType(i) == Types.TIME) {
							columnTypeCategory = 3;
						} else {
							columnTypeCategory = 0;
						}
					} else if (rsmd.getColumnType(i) == Types.TIME) {
						columnTypeCategory = 3;
					} else {
						columnTypeCategory = 0;
					}

					out.write(getTabSpace(level++) + "<xs:element name=\"" + columnName + "\" minOccurs=\"0\">");
					out.write(getTabSpace(level++) + "<xs:complexType>");
					out.write(getTabSpace(level++) + "<xs:simpleContent>");
					out.write(getTabSpace(level++) + "<xs:extension base=\"" + columnType + "\">");
					if (columnTypeCategory == 0 || columnTypeCategory == 3) {
						out.write(getTabSpace(level)
								+ "<xs:attribute name=\"null\" type=\"xs:boolean\" use=\"optional\" />");
					}
					if (columnTypeCategory == 1) {
						out.write(getTabSpace(level)
								+ "<xs:attribute name=\"null\" type=\"xs:boolean\" use=\"optional\" />");
						out.write(getTabSpace(level)
								+ "<xs:attribute name=\"type\" type=\"xs:string\" use=\"optional\" />");
					}
					if (columnTypeCategory == 2) {
						hasblob = true;
						out.write(getTabSpace(level)
								+ "<xs:attribute name=\"null\" type=\"xs:boolean\" use=\"optional\" />");
						out.write(getTabSpace(level)
								+ "<xs:attribute name=\"ref\" type=\"xs:string\" use=\"optional\" />");
						out.write(getTabSpace(level)
								+ "<xs:attribute name=\"status\" type=\"xs:string\" use=\"optional\" />");
						out.write(getTabSpace(level)
								+ "<xs:attribute name=\"size\" type=\"xs:string\" use=\"optional\" />");
						out.write(getTabSpace(level)
								+ "<xs:attribute name=\"type\" type=\"xs:string\" use=\"optional\" />");
					}
					level--;
					out.write(getTabSpace(level--) + "</xs:extension>");
					out.write(getTabSpace(level--) + "</xs:simpleContent>");
					out.write(getTabSpace(level--) + "</xs:complexType>");
					out.write(getTabSpace(level) + "</xs:element>");
				}

				if (GENERATE_ATTACHEMENTS && hasblob) {
					out.write(getTabSpace(level++) + "<xs:element name=\"" + BLOB_PREFIX + formattedTable
							+ "_ATTACHEMENTS\">");
					out.write(getTabSpace(level++) + "<xs:complexType>");
					out.write(getTabSpace(level++) + "<xs:sequence>");

					out.write(getTabSpace(level++) + "<xs:element maxOccurs=\"unbounded\" minOccurs=\"0\" name=\""
							+ "ATTACHEMENT" + "\">");
					out.write(getTabSpace(level++) + "<xs:complexType>");
					out.write(getTabSpace(level++) + "<xs:simpleContent>");
					out.write(getTabSpace(level++) + "<xs:extension base=\"" + "xs:string" + "\">");
					out.write(getTabSpace(level)
							+ "<xs:attribute name=\"column\" type=\"xs:string\" use=\"optional\" />");
					level--;
					out.write(getTabSpace(level--) + "</xs:extension>");
					out.write(getTabSpace(level--) + "</xs:simpleContent>");
					out.write(getTabSpace(level--) + "</xs:complexType>");
					out.write(getTabSpace(level--) + "</xs:element>");
					out.write(getTabSpace(level--) + "</xs:sequence>");
					out.write(getTabSpace(level--) + "</xs:complexType>");
					out.write(getTabSpace(level) + "</xs:element>");
				}
				level--;
				out.write(getTabSpace(level) + "</xs:sequence>");
				out.write(
						getTabSpace(level--) + "<xs:attribute type=\"xs:int\" name=\"EXT_ROW_ID\" use=\"optional\"/>");
				out.write(getTabSpace(level--) + "</xs:complexType>");
				out.write(getTabSpace(level--) + "</xs:element>");
				out.write(getTabSpace(level--) + "</xs:sequence>");
				out.write(getTabSpace(level--) + "</xs:complexType>");
				out.write(getTabSpace(level--) + "</xs:element>");
				out.write("\n</xs:schema>");
				out.flush();
				out.close();

			}
			if (connection != null) {
				connection.close();

			}
		}
	}

	private String getTabSpace(int tabSize) {
		if (tabSize < 0)
			return "";
		i = 0;
		tabSpace = "\n";
		while (i++ != tabSize) {
			tabSpace += "\t";
		}
		return tabSpace;
	}

	private String getColumnType(String colType) {
		switch (colType.toUpperCase()) {
		case "CHAR":
		case "VARCHAR":
		case "TEXT":
		case "TINYTEXT":
		case "MEDIUMTEXT":
		case "LONGTEXT":
		case "USERDEFINED":
		case "SMALLINT":
		case "BIGINT":
		case "TINYINT":
			return "xs:string";
		case "INTEGER":
		case "INT":
		case "AUTONUMBER":
		case "NUMERIC":
			return "xs:int";
		case "LONG":
		case "LONGVARCHAR":
			return "xs:long";
		case "DOUBLE":
		case "DECIMAL":
		case "MONEY":
		case "DEC":
		case "SMALLMONEY":
		case "BIGMONEY":
		case "CURRENCY":
			return "xs:double";
		case "FLOAT":
		case "REAL":
			return "xs:float";
		case "DATE":
			return "xs:date";
		case "DATETIME":
		case "TIMESTAMP":
		case "TIMESTAMP_WITH_TIMEZONE":
		case "TIMESTAMP WITH LOCAL TIME ZONE":
		case "SMALLDATETIME":
			return "xs:string";
		default:
			return "xs:string";
		}
	}

	public static String getTextFormatted(String string) {
		string = string.trim().replaceAll("[^_^\\p{Alnum}.]", "_").replace("^", "_").replaceAll("\\s+", "_");
		string = ((string.startsWith("_") && string.endsWith("_") && string.length() > 2)
				? string.substring(1).substring(0, string.length() - 2)
				: string);
		return string.length() > 0 ? ((string.charAt(0) >= '0' && string.charAt(0) <= '9') ? "_" : "") + string
				: string;
	}

	public String generateNameSpace(String holding) {
		return URI.create("urn:x-emc:ia:schema:" + holding.toLowerCase() + ":1.0").toString();
	}

}
