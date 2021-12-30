package com.p3.archon.beans;

import org.kohsuke.args4j.Option;

public class ArchonInputBean {

	@Option(name = "-ip", aliases = { "--inputPath" }, usage = "inputPath", required = true)
	private String inputPath;

	@Option(name = "-op", aliases = { "--outputPath" }, usage = "outputPath", required = true)
	private String outputPath;

	@Option(name = "-d", aliases = { "--delimeter" }, usage = "delimeter", required = true)
	private String delimeter;

	@Option(name = "-e", aliases = { "--encoding" }, usage = "encoding", required = true)
	private String encoding;

	@Option(name = "-nc", aliases = { "--noOfColumns" }, usage = "noOfColumns", required = true)
	private String noOfColumns;

	public String getDelimeter() {
		return delimeter;
	}

	public void setDelimeter(String delimeter) {
		this.delimeter = delimeter;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getNoOfColumns() {
		return noOfColumns;
	}

	public void setNoOfColumns(String noOfColumns) {
		this.noOfColumns = noOfColumns;
	}

}
