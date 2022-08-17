package com.p3.archon.coreprocess.process;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

import com.p3.archon.commonfunctions.CommonFunctions;
import com.p3.archon.coreprocess.beans.ArchonInputBean;
import com.p3.archon.coreprocess.executables.ExecutionJob;
import com.p3.archon.coreprocess.executables.UnstructuredExecutionJob;

public class DataLiberator {

	private ArchonInputBean inputArgs;

	public DataLiberator(ArchonInputBean inputArgs) {
		this.inputArgs = inputArgs;
	}

	public void start() throws Exception {
		Connection connection = null;

		String host = inputArgs.getHost();
		String port = inputArgs.getPort();
		String uName = inputArgs.getUser();
		String pass = inputArgs.getPass();
		String db = inputArgs.getDatabase();

		String connectionUrl = null;

		switch (inputArgs.getDatabaseServer().toLowerCase()) {
		case "teradata":
			connectionUrl = "jdbc:teradata://" + host + "/" + db + "";
			break;
		case "sqlwinauth":
			connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";database=" + db + "";
			break;
		case "sql":
			connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";database=" + db + "";
			break;
		case "oracle":
			connectionUrl = "jdbc:oracle:thin:@" + host + ":" + port + ":" + db + "";
			break;
		case "oracleservice":
			connectionUrl = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + db + "";
			break;
		case "mysql":
			connectionUrl = "jdbc:mysql://" + host + ":" + port + "/" + db
					+ "?nullNamePatternMatchesAll=true&logger=Jdk14Logger&dumpQueriesOnException=true&dumpMetadataOnColumnNotFound=true&maxQuerySizeToLog=4096&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=true&serverTimezone="
					+ CommonFunctions.getTimeZone() + "&disableMariaDbDriver";
			break;
		case "mariadb":
		case "maria":
			connectionUrl = "jdbc:mariadb://{" + host + "" + port + "/" + db + "";
			break;
		case "db2":
			connectionUrl = "jdbc:db2://" + host + ":" + port + "/" + db
					+ ";retrieveMessagesFromServerOnGetMessage=true;";
			break;
		case "sybase":
			connectionUrl = "jdbc:jtds:sybase://" + host + ":" + port + "/" + db + "";
			break;
		case "postgresql":
			connectionUrl = "jdbc:postgresql://" + host + ":" + port + "/" + db + "?ApplicationName=SchemaCrawler";
			break;
		case "as400":
			connectionUrl = "jdbc:as400://" + host + ":" + port + "/" + db + "";
			break;
		case "as400noport":
			connectionUrl = "jdbc:as400://" + host + "/" + db + "";
			break;
		default:
			connectionUrl = null;
		}
		connection = DriverManager.getConnection(connectionUrl, uName, pass);
		System.out.println("connected");
		perfromActivity(connection);
	}

	private void perfromActivity(Connection connection) throws Exception {
		ExecutionJob exe;
		// inputArgs.printInputs();
		Date startTime = new Date();
		System.out.println("--------------------------------- Operation start ---------------------------------");

		try {
			exe = new UnstructuredExecutionJob(connection, inputArgs);
		} catch (Exception e) {
			System.out.println("ERROR :");
			System.out.println(e.getMessage());
			throw e;
		}
		exe.start();

		Date endTime = new Date();
		System.out.println("Job Execution Time : " + ExecutionJob.timeDiff(endTime.getTime() - startTime.getTime()));

		System.out.println("------------------------------- Operation Compeleted -------------------------------");
	}

}
