package com.p3.archon;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.p3.archon.beans.ArchonInputBean;

public class ConnectivityCheckMain {
	public static void main(String[] args) {

		args = new String[] { "-dbs", "oracle", "-h", "localhost", "-l", "1434", "-u", "", "-p", "", "-s", "dbo", "-d",
				"CLAIMS_SYS", "-tInfo" };

		if (args.length == 0) {
			System.err.println("No arguments specified.\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(1);
		}
		final ArchonInputBean ipargs = new ArchonInputBean();
		final CmdLineParser parser = new CmdLineParser((Object) ipargs);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
			System.err.println("Please check arguments specified. \n" + e.getMessage() + "\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(1);
		}
		System.out.println("Host Name =" + ipargs.getHost());
		System.out.println("Port =" + ipargs.getPort());
		System.out.println("Database Name =" + ipargs.getDatabase());
		System.out.println("Schema =" + ipargs.getSchema());
		ResultSet rs = null;
		Connection conn = null;
		try {

//			File jar = new File("C:\\Archon\\Archon\\lib\\jdbcDrivers\\mssql-jdbc-8.2.2.jre8.jar");
//			URL[] cp = new URL[1];
//			cp[0] = jar.toURI().toURL();
//			URLClassLoader ora8loader = new URLClassLoader(cp, ClassLoader.getSystemClassLoader());
//			Class drvClass = ora8loader.loadClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//			Driver ora8driver = (Driver) drvClass.newInstance();
//
//			Properties props = new Properties();
//			// "user" instead of "username"
//			props.setProperty("user", "sa");
//			props.setProperty("password", "secret");
//			Connection ora8conn = ora8driver.connect("jdbc:sqlserver://" + ipargs.getHost() + ":" + ipargs.getPort()
//					+ ";database=" + ipargs.getDatabase(), props);
//			
//			System.out.println("connect");
//			ora8conn.close();
//			String connectionURL = "jdbc:mysql://" + ipargs.getHost() + ":" + ipargs.getPort() + "/"
//					+ ipargs.getDatabase();

//			String connectionURL = "jdbc:sqlserver://" + ipargs.getHost() + ";database=" + ipargs.getDatabase();

			String connectionURL = "jdbc:jtds:sqlserver://" + ipargs.getHost() + ":" + ipargs.getPort() + "/"
					+ ipargs.getDatabase() + ";useNTLMv2=true";
//			Class.forName("oracle.jdbc.xa.client.OracleXADataSource");

//			if (ipargs.getServer().equalsIgnoreCase("oracle")) {
//				connectionURL = "jdbc:oracle:thin:@" + ipargs.getHost() + ":" + ipargs.getPort() + ":"
//						+ ipargs.getDatabase();
//			} else {
//				connectionURL = "jdbc:oracle:thin:@//" + ipargs.getHost() + ":" + ipargs.getPort() + "/"
//						+ ipargs.getDatabase();
//
//			}

			System.out.println(connectionURL);

			System.out.println("EstablishING Connection to the " + ipargs.getDatabase() + " database");
			conn = DriverManager.getConnection(connectionURL, ipargs.getUser(), ipargs.getPass());
			System.out.println("Sucessfully Established Connection with " + ipargs.getDatabase() + " database");
			if (ipargs.istInfo()) {
				System.out.println("Gathering Table Info");
				System.out.println("----- Table Information -----");
				String schema = (ipargs.getSchema() == null || ipargs.getSchema().isEmpty()) ? "%" : ipargs.getSchema();
				String tn = (ipargs.getTableName() == null || ipargs.getTableName().isEmpty()) ? "%"
						: ipargs.getTableName();
				rs = conn.getMetaData().getTables(null, schema, tn, new String[] { "TABLE", "VIEW" });
				while (rs.next()) {
					String catalogName = rs.getString(1);
					String schemaName = rs.getString(2);
					String tableName = rs.getString(3);

					if (ipargs.isRecordCount()) {
						String countQuery = " select count(1) from " + schemaName + "." + tableName;
						Statement statement = conn.createStatement();
						ResultSet qrs = statement.executeQuery(countQuery);
						qrs.next();
						long count = qrs.getLong(1);
						statement.close();
						qrs.close();
						System.out.println(catalogName + " " + schemaName + " " + tableName + " " + count);
					} else
						System.out.println(catalogName + " " + schemaName + " " + tableName);

				}
				rs.close();
			}
			System.out.println();

			if (ipargs.iscInfo()) {
				System.out.println("----- Column Information -----");
				String schema = (ipargs.getSchema() == null || ipargs.getSchema().isEmpty()) ? "%" : ipargs.getSchema();
				String tn = (ipargs.getTableName() == null || ipargs.getTableName().isEmpty()) ? "%"
						: ipargs.getTableName();
				rs = conn.getMetaData().getColumns(null, schema, tn, null);
				while (rs.next()) {

					System.out.println("Column Name " + rs.getString(4));
					System.out.println("Data Type " + rs.getInt(5));
					System.out.println("TypeName " + rs.getString(6));
					System.out.println("Column size " + rs.getInt(7));
					System.out.println("decimal digits " + rs.getInt(9));
					System.out.println("ordinal " + rs.getInt(17));

					System.out.println("......................");

				}

				rs.close();
				System.out.println();
			}

			if (ipargs.isQ()) {
				Statement statement = conn.createStatement();
				String schema = (ipargs.getSchema() == null || ipargs.getSchema().isEmpty()) ? "%" : ipargs.getSchema();
				String tn = (ipargs.getTableName() == null || ipargs.getTableName().isEmpty()) ? "%"
						: ipargs.getTableName();
				ResultSet qrs = statement.executeQuery("select * from " + schema + "." + tn);
				ResultSetMetaData rsmd = qrs.getMetaData();
				int colnum = rsmd.getColumnCount();
				while (rs.next()) {
					for (int i = 1; i <= colnum; i++) {
						System.out.print(qrs.getString(i) + "  ");
					}
					System.out.println();
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e3) {
					e3.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e3) {
					e3.printStackTrace();
				}
			}
		}

	}
}