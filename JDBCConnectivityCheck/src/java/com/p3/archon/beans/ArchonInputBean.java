package com.p3.archon.beans;

import org.kohsuke.args4j.Option;

public class ArchonInputBean {

	@Option(name = "-dbs", aliases = { "--server" }, usage = "server", required = true)
	private String server;

	@Option(name = "-h", aliases = { "--host" }, usage = "host Server IP", required = true)
	private String host;

	@Option(name = "-l", aliases = { "--port" }, usage = "database listening port", required = false)
	private String port;

	@Option(name = "-d", aliases = { "--database" }, usage = "database name", required = false)
	private String database;

	@Option(name = "-s", aliases = { "--schema" }, usage = "schema name", required = false)
	private String schema;

	@Option(name = "-u", aliases = { "--user" }, usage = "database username", required = true)
	private String user;

	@Option(name = "-p", aliases = { "--pass" }, usage = "database password", required = true)
	private String pass;

	@Option(name = "-t", aliases = { "--tn" }, usage = "table Name", required = false)
	private String tableName;

	@Option(name = "-tInfo", aliases = { "--tInfo " }, usage = "tInfo")
	private boolean tInfo;

	@Option(name = "-cInfo", aliases = { "--cInfo " }, usage = "cInfo")
	private boolean cInfo;

	@Option(name = "-q", aliases = { "--isQ " }, usage = "isQ")
	private boolean isQ;
	
	@Option(name = "-rc", aliases = { "--rc " }, usage = "rc")
	private boolean recordCount;

	public boolean isRecordCount() {
		return recordCount;
	}

	public void setRecordCount(boolean recordCount) {
		this.recordCount = recordCount;
	}

	public boolean isQ() {
		return isQ;
	}

	public void setQ(boolean isQ) {
		this.isQ = isQ;
	}

	public boolean istInfo() {
		return tInfo;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void settInfo(boolean tInfo) {
		this.tInfo = tInfo;
	}

	public boolean iscInfo() {
		return cInfo;
	}

	public void setcInfo(boolean cInfo) {
		this.cInfo = cInfo;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

}
