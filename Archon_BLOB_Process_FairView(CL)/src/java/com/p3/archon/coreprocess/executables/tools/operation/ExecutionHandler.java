package com.p3.archon.coreprocess.executables.tools.operation;

import java.sql.ResultSet;

public interface ExecutionHandler {

	void handleDataMain(String title, ResultSet rows) throws Exception;

	void end() throws Exception;
	
	long getRecordprocessed();
}
