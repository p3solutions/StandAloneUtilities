package com.p3solutions.jdbc_parallel_connection_check.connection;

import java.sql.Connection;

public class ConnectionPool {

	private int id;
	private Connection connection;
	private boolean inUse = false;

	public boolean isInUse() {
		return inUse;
	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
