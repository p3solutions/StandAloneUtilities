package com.p3.archon.beans;

import org.kohsuke.args4j.Option;

public class ArchonInputBean {

	@Option(name = "-ip", aliases = { "--inputPath" }, usage = "inputPath", required = true)
	private String inputPath;

	@Option(name = "-op", aliases = { "--outputPath" }, usage = "outputPath", required = true)
	private String outputPath;

	@Option(name = "-d", aliases = { "--delimeter" }, usage = "delimeter", required = true)
	private String delimeter;

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

}
