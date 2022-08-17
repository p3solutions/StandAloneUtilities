package com.p3.archon.bean;

import org.kohsuke.args4j.Option;

public class ArchonInputBean {

	@Option(name = "-ip", aliases = { "--inputpath" }, usage = "i/p path", depends = { "-isSip" })
	public String inputPath;

	@Option(name = "-op", aliases = { "--outputPath" }, usage = "o/p path", depends = { "-ispdi" })
	public String outputPath;

	@Option(name = "-an", aliases = { "--applicationName" }, usage = "applicationName", depends = { "-isSip" })
	public String applicationName;

	@Option(name = "-h", aliases = { "--holding" }, usage = "holding", required = true)
	public String holding;

	@Option(name = "-rpx", aliases = { "--rpx" }, usage = "rpx", depends = { "-isSip" })
	public int rpx;

	@Option(name = "-dbs", aliases = { "--database server" }, usage = "Server", depends = { "-ispdi" })
	private String server;

	@Option(name = "-host", aliases = { "--host" }, usage = "Host", depends = { "-ispdi" })
	private String host;

	@Option(name = "-l", aliases = { "--port" }, usage = "Port", depends = { "-ispdi" })
	private String port;

	@Option(name = "-u", aliases = { "--user" }, usage = "database username", depends = { "-ispdi" })
	private String username;

	@Option(name = "-p", aliases = { "--pass" }, usage = "database password", depends = { "-ispdi" })
	private String password;

	@Option(name = "-loc", aliases = { "--location" }, usage = "location", depends = { "-ispdi" })
	private String location;

	@Option(name = "-d", aliases = { "--database" }, usage = "name of the database/schema to connect with", depends = {
			"-ispdi" })
	private String database;

	@Option(name = "-s", aliases = { "--schema" }, usage = "schema", depends = { "-ispdi" })
	private String schema;

	@Option(name = "-tn", aliases = { "--tableName" }, usage = "tableName", depends = { "-ispdi" })
	private String tableName;

	@Option(name = "-ispdi", aliases = { "--isPdiGen " }, usage = "isPdiGen")
	public boolean isPdiGen;

	@Option(name = "-sl", aliases = { "--sl" }, usage = "sl")
	public boolean showlobs;

	@Option(name = "-isSip", aliases = { "--isSip " }, usage = "isSip")
	public boolean isSip;

	public boolean isShowlobs() {
		return showlobs;
	}

	public void setShowlobs(boolean showlobs) {
		this.showlobs = showlobs;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public boolean isPdiGen() {
		return isPdiGen;
	}

	public void setPdiGen(boolean isPdiGen) {
		this.isPdiGen = isPdiGen;
	}

	public boolean isSip() {
		return isSip;
	}

	public void setSip(boolean isSip) {
		this.isSip = isSip;
	}

	public int getRpx() {
		return rpx;
	}

	public void setRpx(int rpx) {
		this.rpx = rpx;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getHolding() {
		return holding;
	}

	public void setHolding(String holding) {
		this.holding = holding;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void checkRpx() {
		if (rpx == 0) {
			rpx = 100;
		}
		rpx = rpx * 1024 * 1024;
	}

}
