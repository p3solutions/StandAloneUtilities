package com.p3.archon.beans;

import org.kohsuke.args4j.Option;

public class ArchonInputBean {

	@Option(name = "-ip", aliases = { "--inputPath" }, usage = "inputPath", required = true)
	private String inputPath;

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

}
