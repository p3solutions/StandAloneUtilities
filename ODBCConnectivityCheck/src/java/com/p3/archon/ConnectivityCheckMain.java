package com.p3.archon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.p3.archon.beans.ArchonInputBean;

public class ConnectivityCheckMain {
	public static void main(String[] args) {
//
//		args = new String[] { "-dsn", "testnew", "-u", "sa", "-p", "secret", "-s", "dbo","-tInfo" };

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
		ResultSet rs = null;
		Connection conn = null;
		try {

			System.out.println("EstablishING Connection to the " + ipargs.getDsn() + " data source");
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			conn = DriverManager.getConnection("jdbc:odbc:" + ipargs.getDsn(), ipargs.getUser(), ipargs.getPass());
			System.out.println("Sucessfully Established Connection with " + ipargs.getDsn() + " data source");
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
				qrs.close();
			}
		} catch (Exception e2) {
			e2.printStackTrace();

		} finally {
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