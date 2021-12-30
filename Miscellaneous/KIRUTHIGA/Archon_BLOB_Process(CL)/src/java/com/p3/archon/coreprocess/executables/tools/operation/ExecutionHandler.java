package com.p3.archon.coreprocess.executables.tools.operation;

import java.sql.Connection;
import java.sql.ResultSet;

public interface ExecutionHandler {

	void handleDataMain(String title, ResultSet rows, Connection connection, String database, String schema) throws Exception;

	void end() throws Exception;
	
	long getRecordprocessed();
}
