package com.p3.tmb.ingester.process;

import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.ingester.beans.ingesterInputBean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


public class scriptMaker {

	private ingesterInputBean inputArgs;
	private final String CONNECT_TEMPLATE = "connect --u ::::: USERNAME ::::: --p ::::: PASSWORD :::::\ncd applications/::::: APP :::::\ncd ../../\n";
	private final String SIP_INGEST_TEMPLATE = "ingest --d applications/::::: APP ::::: --from ::::: DATA_PATH :::::/*.zip";
	private final String SCRIPT_TEMPLATE = "::::: IA_PATH ::::: --cmdfile ::::: SCRIPT_PATH ::::: --cmdfile_echo";

																																								// \"metadata.*\\.xml\"

	public scriptMaker(ingesterInputBean inputArgs) {
		this.inputArgs = inputArgs;
	}

	public void createScript(String path) throws IOException {
		Writer out = new FileWriter(path, false);
		out.write(getConnectTemplate());
			out.write(getIngestTemplate());
		out.write("\nquit");
		out.flush();
		out.close();
	}

	private String getConnectTemplate() {
		return CONNECT_TEMPLATE.replace("::::: USERNAME :::::", inputArgs.getUser())
				.replace("::::: PASSWORD :::::", inputArgs.getPass()).replace("::::: APP :::::", inputArgs.appName);
	}

	private String getIngestTemplate() {
			if (CommonSharedConstants.IA_VERSION.equals("16EP6") || CommonSharedConstants.IA_VERSION.equals("16EP7")
					|| CommonSharedConstants.IA_VERSION.equals("20.2")
					|| CommonSharedConstants.IA_VERSION.equals("20.4")
					|| CommonSharedConstants.IA_VERSION.equals("21.2")
					|| CommonSharedConstants.IA_VERSION.equals("21.4")
					|| CommonSharedConstants.IA_VERSION.equals("22.2")) {
				return SIP_INGEST_TEMPLATE.replace("::::: APP :::::", inputArgs.appName)
						.replace("::::: DATA_PATH :::::", inputArgs.getDataPath().replace("\\", "/"))
						+ " --moveOnSuccess --moveOnSuccessTo " + inputArgs.getDataPath() + File.separator
						+ "INGESTION_SUCCESS" + " --moveOnErrorTo " + inputArgs.getDataPath() + File.separator
						+ "INGESTION_FAILED" + "";
			} else
				return SIP_INGEST_TEMPLATE.replace("::::: APP :::::", inputArgs.appName)
						.replace("::::: DATA_PATH :::::", inputArgs.getDataPath().replace("\\", "/"));
	}

	public void createScriptRunner(String path, String scriptPath) throws IOException {
		Writer out = new FileWriter(path, false);
		out.write(getScriptTemplate(scriptPath));
		out.flush();
		out.close();
	}

	private String getScriptTemplate(String scriptPath) {
		return SCRIPT_TEMPLATE
				.replace("::::: IA_PATH :::::",
						(inputArgs.getIaPath() + File.separator + "bin" + File.separator + "iashell"))
				.replace("::::: SCRIPT_PATH :::::", scriptPath);
	}


}
