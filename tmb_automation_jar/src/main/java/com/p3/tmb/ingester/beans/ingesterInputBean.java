package com.p3.tmb.ingester.beans;

import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.commonUtils.encryptionUtils;
import com.p3.tmb.constant.CommonSharedConstants;

public class ingesterInputBean {

	public String appName;

	public String user;

	public String pass;

	public String iaPath;

	public String dataPath;

	public String outputPath;

	public String reportId;

	public boolean ingestApp = false;

	public String errorLog;

	public String outputLog;

	public String archonPath;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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

	public String getIaPath() {
		return iaPath;
	}

	public void setIaPath(String iaPath) {
		this.iaPath = iaPath;
	}

	public String getDataPath() {
		return dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public boolean isIngestApp() {
		return ingestApp;
	}

	public void setIngestApp(boolean ingestApp) {
		this.ingestApp = ingestApp;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public String getOutputLog() {
		return outputLog;
	}

	public void setOutputLog(String outputLog) {
		this.outputLog = outputLog;
	}

	public String getArchonPath() {
		return archonPath;
	}

	public void setArchonPath(String archonPath) {
		this.archonPath = archonPath;
	}

	public void decryptor() {
		if(checkEncryption()) {
			//getEncryptedPassword();
			//pass = securityModule.perfromDecrypt(appName, user, pass);
			pass = encryptionUtils.decryptor(pass);
		}
	}

	private boolean checkEncryption() {
		try {
			if(pass.startsWith("ENC(")&&pass.endsWith(")")) {
				return true;
			}				
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		return false;
	}
	private void getEncryptedPassword() {
		String password = pass;
		pass=password.substring(password.indexOf('(')+1,password.lastIndexOf(')'));
	}
}
