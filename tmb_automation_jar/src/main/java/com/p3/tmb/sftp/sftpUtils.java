package com.p3.tmb.sftp;

import com.jcraft.jsch.SftpException;
import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.constant.CommonSharedConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class sftpUtils {
	sftpFileConnection sftp = null;
	sftpBean sftpBean = null;
	propertyBean propBean = null;
	
	public sftpUtils(sftpBean sftpBean,propertyBean propBean) {
		this.sftpBean = sftpBean;
		this.propBean = propBean;
	}

	public boolean isFolderExist(String path) {
		boolean statusFlag = false;
		try {
			sftp = new sftpFileConnection(sftpBean);
			sftp.getChannelSftp();
			statusFlag = sftp.checkForFileExistence(path);
			sftp.closeConnections();
			
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
		return statusFlag;
	}
	public boolean createPropertyFilesinRemoteDir(String rootPath, String folderName, String fileName, String content) {
		boolean statusFlag = false;
		try {
			String folderPath = rootPath+CommonSharedConstants.LINUX_PATH_SEPERATOR+folderName;

			if(isFolderExist(folderPath)) {
				sftp = new sftpFileConnection(sftpBean);
				sftp.getChannelSftp();
				statusFlag = sftp.createFile(folderPath+CommonSharedConstants.LINUX_PATH_SEPERATOR+fileName,content);
				sftp.closeConnections();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
		
		return statusFlag;
	}
	
    public List<String> checkTextFileExistence(String textFileFolder) {
    	boolean checkFile = false;
    	List<String> files = null;
    	try {
    	System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection started ");
    	CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection started " + CommonSharedConstants.newLine);
    	sftp = new sftpFileConnection(sftpBean);
		sftp.getChannelSftp();
		
		//checkFile = sftp.checkForFileExistence(propBean.getTextFilePath()+"/"+textFileFolder);
		try {
		files = sftp.checkForTextFileExistence(propBean.getTextFilePath()+CommonSharedConstants.LINUX_PATH_SEPERATOR+textFileFolder);
		System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection sucessfull");
		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection sucessfull" + CommonSharedConstants.newLine);
		}
		catch(Exception e) {
			System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection failed");
			e.printStackTrace();
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection failed" + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		//System.out.println("Checking File : " + checkFile);
		if(files.size()>0)
		checkFile = true;
		
		sftp.closeConnections();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e1) + CommonSharedConstants.newLine);
		}
    	return files;
    }
    
    public boolean checkForFileExistence(String path) {
	    
    	boolean status = false;
    	try {
    		sftp = new sftpFileConnection(sftpBean);
    		sftp.getChannelSftp();
    		status = sftp.checkForFileExistence(path);    		
    		sftp.closeConnections();
    		
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
    		return false;
    	}
    	return status;
	}

    
    public boolean downloadFile(String remoteFilePath) {
    	boolean status = false;
    	try {
    		sftp = new sftpFileConnection(sftpBean);
    		sftp.getChannelSftp();
    		status = sftp.downloadFile(remoteFilePath);    		
    		sftp.closeConnections();
    		
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
    		return false;
    	}
    	return status;
    }

    public boolean deleteFile(String remoteFilePath) {
    	boolean status = false;
    	try {
    		if(isFolderExist(remoteFilePath)) {
    		sftp = new sftpFileConnection(sftpBean);
    		sftp.getChannelSftp();
    		status = sftp.deleteFile(remoteFilePath);    		
    		sftp.closeConnections();
    		}
    		
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
    		return false;
    	}
    	return status;
    }
    
    public boolean downloadInputDirectory() throws SftpException, IOException {
		try{
			sftp = new sftpFileConnection(sftpBean);
		sftp.getChannelSftp();
		//String downloadPath = new File(propBean.getTextFilePath()).getParent();
		sftp.downloadFromFolder1(FileUtil.removeFileNameFromLinuxPath(propBean.getTextFilePath()));
		sftp.closeConnections();
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
		return true;
    }
    
    public boolean isJobReadyToExecute(String filePath) throws IOException {
    	BufferedReader bufferedReader = null;
    	boolean statusFlag = false;
    	try {
    		
    		
    		if(isFolderExist(filePath)) {
    		
    		sftp = new sftpFileConnection(sftpBean);
    		sftp.getChannelSftp();
    		bufferedReader = sftp.getFileBufferReader(filePath);
    		String line = null;
    		if ((line = bufferedReader.readLine()) != null) {
    		 if(line.equals(CommonSharedConstants.readyForJobTrue))
    			 statusFlag = true;
    		 else if(line.equals(CommonSharedConstants.readyForJobFalse))
    				 statusFlag =false;
    		}
    		}
    		else
    			statusFlag = true;
		}
    	
    	catch (Exception e) {
    		
    		e.printStackTrace();
    		CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
    		statusFlag = false;
		}
    	finally {
    		if(bufferedReader!=null)
             bufferedReader.close();

    		sftp.closeConnections();

    	}
    	return statusFlag;
    }
    
    public boolean createDir(String path) {
    	try {
	    	sftp = new sftpFileConnection(sftpBean);
			sftp.getChannelSftp();
			sftp.createDir(path);
			sftp.closeConnections();
    	}
    	catch(Exception e) {
    		return false;
    	}
    	return true;
    }
    
    public boolean uploadFile(String inputPath, String destPath) {
    	try {
	    	sftp = new sftpFileConnection(sftpBean);
			sftp.getChannelSftp();
			sftp.uploadFiles(inputPath, destPath);
			sftp.closeConnections();
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File : " + inputPath + " Uploaded to " + destPath + CommonSharedConstants.newLine);
    	}
    	catch(Exception e) {
    		return false;
    	}
    	return true;
    }
}
