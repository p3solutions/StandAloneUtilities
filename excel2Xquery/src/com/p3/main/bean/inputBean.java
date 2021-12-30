package com.p3.main.bean;

import org.kohsuke.args4j.Option;

public class inputBean {

	@Option(name ="-ip", aliases="--input path", usage="Input path having source file",required =true)
	private String inputPath;
	
	@Option(name ="-op", aliases="--output path", usage="destination path to generate xquery",required=true)
	private String outputPath;

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
