package com.p3.archon.bean;

import org.kohsuke.args4j.Option;

public class ArchonInputBean {
	@Option(name = "-ip", aliases = { "--inputPath" }, usage = "Input Path", required = true)
	public String input;

	@Option(name = "-op", aliases = { "--outPut" }, usage = "Out Put", required = true)
	public String output;

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

}
