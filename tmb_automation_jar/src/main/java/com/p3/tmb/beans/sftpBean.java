package com.p3.tmb.beans;

import com.p3.tmb.commonUtils.encryptionUtils;

public class sftpBean {

	private String remoteHost;
	private String userName;
	private String password;
	private String remotePort;
	private String sessionTimeOut;
	private String channelTimeOut;
	private String localPath;
	private String remotePath;
	private String channelName;
	private String identity;
	
	public String getRemoteHost() {
		return remoteHost;
	}
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRemotePort() {
		return remotePort;
	}
	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}
	public String getSessionTimeOut() {
		return sessionTimeOut;
	}
	public void setSessionTimeOut(String sessionTimeOut) {
		this.sessionTimeOut = sessionTimeOut;
	}
	public String getChannelTimeOut() {
		return channelTimeOut;
	}
	public void setChannelTimeOut(String channelTimeOut) {
		this.channelTimeOut = channelTimeOut;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	public String getRemotePath() {
		return remotePath;
	}
	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	/*
	 * public void decryptor(String appName) { if(checkEncryption()) {
	 * getEncryptedPassword(); password = securityModule.perfromDecrypt(appName,
	 * userName, password); }
	 * 
	 * }
	 */
	public void decryptor() {
		if(checkEncryption()) {
			//getEncryptedPassword();
			//pass = securityModule.perfromDecrypt(appName, user, pass);
			password = encryptionUtils.decryptor(password);
		}
	}

	private boolean checkEncryption() {
		try {
			if(password.startsWith("ENC(")&&password.endsWith(")")) {
				return true;
			}				
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	private void getEncryptedPassword() {
		String pass = password;
		password=pass.substring(pass.indexOf('('),pass.lastIndexOf(')'));
	}
	
}
