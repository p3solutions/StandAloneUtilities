package com.p3.archon.beans;

import org.kohsuke.args4j.Option;

public class ArchonInputBean {

	@Option(name = "-ip", aliases = { "--inputPath" }, usage = "inputPath", required = true)
	private String inputPath;

	@Option(name = "-cp", aliases = { "--csvPath" }, usage = "csvPath", required = true)
	private String csvPath;

	@Option(name = "-ifn", aliases = { "--idFileName" }, usage = "idFileName", required = true)
	private int idFileName;

	@Option(name = "-ofn", aliases = { "--originalFileName" }, usage = "originalFileName", required = true)
	private int originalFileName;

	@Option(name = "-ofe", aliases = { "--originalFileExtention" }, usage = "originalFileExtention", required = false)
	private int originalFileExtention;

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getCsvPath() {
		return csvPath;
	}

	public void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}

	public int getIdFileName() {
		return idFileName;
	}

	public void setIdFileName(int idFileName) {
		this.idFileName = idFileName;
	}

	public int getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(int originalFileName) {
		this.originalFileName = originalFileName;
	}

	public int getOriginalFileExtention() {
		return originalFileExtention;
	}

	public void setOriginalFileExtention(int originalFileExtention) {
		this.originalFileExtention = originalFileExtention;
	}

}
