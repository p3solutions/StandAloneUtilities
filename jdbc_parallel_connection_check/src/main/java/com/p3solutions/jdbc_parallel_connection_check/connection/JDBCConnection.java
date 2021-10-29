package com.p3solutions.jdbc_parallel_connection_check.connection;

import com.p3solutions.jdbc_parallel_connection_check.beans.InputBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


public class JDBCConnection {

	private static Logger logger = LogManager.getLogger(JDBCConnection.class);

	protected ArrayList<ConnectionPool> cps;
	protected JDBCConnector as400c;
	protected Connection connection = null;
	protected int maxThread;

	private static String getClassName() {
		return JDBCConnection.class.getName();
	}

	public JDBCConnection(InputBean inputArgs) {
		maxThread=inputArgs.getMpp();
		as400c = new JDBCConnector(inputArgs);
		connection = as400c.getConnection();
	}

	int counter = 0;

	protected void openConnections(InputBean inputArgs) {
		cps = new ArrayList<ConnectionPool>();

		logger.info("setting up db connection in connection pool");
		for (int i = 0; i < maxThread; i++) {
			counter++;
			final int val = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					ConnectionPool cp = new ConnectionPool();
					JDBCConnector as400conn = new JDBCConnector(inputArgs);
					Connection con = as400conn.getConnection();
					cp.setId(val + 1);
					cp.setConnection(con);
					cp.setInUse(false);
					if (cp.getConnection() != null) {
						cps.add(cp);
						logger.info("Created connection '" + cp.getId() + "' in connection pool");
					}
					counter--;
				}
			}).start();
		}

		sleep();

		if (cps.size() == 0) {
			logger.info("No connection available in connection pool. Terminating...");
			System.exit(1);
		} else {
			logger.info("Connection available in connection pool : " + cps.size());
			logger.info("Max parallel processing possible : " + cps.size());
		}
		maxThread = cps.size();
	}

	protected void closeAllConnections() {
		logger.info("trying to close the db connection in connection pool");
		counter = maxThread;
		for (ConnectionPool cp : cps) {
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						if (cp.getConnection() != null)
							cp.getConnection().close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					logger.info("Closed connection '" + cp.getId() + "' from connection pool");
					counter--;
				}
			}).start();
		}

		sleep();

		try {
			logger.info("trying to close the main db connection");
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.info("Closed all open db connections");
	}

	private void sleep() {
		while (counter != 0) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static ConnectionPool getFreeConnection(ArrayList<ConnectionPool> cps) {
		for (ConnectionPool cp : cps) {
			if (!cp.isInUse()) {
				cp.setInUse(true);
				return cp;
			}
		}
		return null;
	}
}
