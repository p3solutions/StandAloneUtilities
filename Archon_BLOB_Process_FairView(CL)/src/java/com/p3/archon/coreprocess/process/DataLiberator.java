package com.p3.archon.coreprocess.process;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

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
		String ConnectionUrl = null;
		if (inputArgs.getDatabaseServer().equals("sql")) {
			ConnectionUrl = "jdbc:sqlserver://" + inputArgs.getHost() + ":" + inputArgs.getPort() + ";database="
					+ inputArgs.getDatabase();
		} else if (inputArgs.getDatabaseServer().equals("postgresql")) {
			ConnectionUrl = "jdbc:postgresql://" + inputArgs.getHost() + ":" + inputArgs.getPort() + "/"
					+ inputArgs.getDatabase();
		} else if (inputArgs.getDatabaseServer().equalsIgnoreCase("mysql")) {
			ConnectionUrl = "jdbc:mysql://" + inputArgs.getHost() + ":" + inputArgs.getPort() + "/"
					+ inputArgs.getDatabase();
		} else {
			ConnectionUrl = "jdbc:db2://" + inputArgs.getHost() + ":" + inputArgs.getPort() + "/"
					+ inputArgs.getLocation();
		}
		connection = DriverManager.getConnection(ConnectionUrl, inputArgs.getUser(), inputArgs.getPass());
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
