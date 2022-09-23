package com.p3.tmb.sftp;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.constant.CommonSharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class sftpUtils {
	sftpFileConnection sftp = null;
	sftpBean sftpBean = null;
	propertyBean propBean = null;
	final Logger log = LogManager.getLogger(sftpUtils.class.getName());

	public sftpUtils(sftpBean sftpBean,propertyBean propBean) {
		this.sftpBean = sftpBean;
		this.propBean = propBean;
	}

	public boolean isFolderExist(String path) throws JSchException {
		boolean statusFlag = false;
		try {
			sftp = new sftpFileConnection(sftpBean);
			sftp.getChannelSftp();
			statusFlag = sftp.checkForFileExistence(path);
		}
		finally {
			try {
				sftp.closeConnections();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return statusFlag;
	}
	public boolean createPropertyFilesinRemoteDir(String rootPath, String folderName, String fileName, String content) throws JSchException {
		boolean statusFlag = false;
		try {
			String folderPath = rootPath+CommonSharedConstants.LINUX_PATH_SEPERATOR+folderName;

			if(isFolderExist(folderPath)) {
				sftp = new sftpFileConnection(sftpBean);
				sftp.getChannelSftp();
				statusFlag = sftp.createFile(folderPath+CommonSharedConstants.LINUX_PATH_SEPERATOR+fileName,content);
				log.info("Created the folder.properties file in the "+folderName+ " with content "+content);
			}
		}
		finally {
			try {
				sftp.closeConnections();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return statusFlag;
	}
	
    public List<String> checkTextFileExistence(String textFileFolder){
    	boolean checkFile = false;
    	List<String> files = null;
    	try {
    	log.info("MFT server connection started ");
    	CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection started " + CommonSharedConstants.newLine);
    	sftp = new sftpFileConnection(sftpBean);
		sftp.getChannelSftp();

		//checkFile = sftp.checkForFileExistence(propBean.getTextFilePath()+"/"+textFileFolder);
		try {
		files = sftp.checkForTextFileExistence(propBean.getTextFilePath()+CommonSharedConstants.LINUX_PATH_SEPERATOR+textFileFolder);
		log.info("MFT server connection sucessfull");
		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection sucessfull" + CommonSharedConstants.newLine);
		}
		catch(Exception e) {
			log.info("MFT server connection failed");
			e.printStackTrace();
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  MFT server connection failed" + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		//log.info("Checking File : " + checkFile);
		if(files.size()>0)
			checkFile = true;

		}
		catch(JSchException e) {
			log.error("Connection Error to sftp while checking Text File Existence");
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		} finally {
			try {
				sftp.closeConnections();
			} catch (IOException e1) {
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e1) + CommonSharedConstants.newLine);
			}
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

    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
    		return false;
    	}finally {
			try {
				sftp.closeConnections();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			log.info("Error : Connection Error to sftp while deleting "+remoteFilePath);
    		e.printStackTrace();
    		CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
    		return false;
    	}finally {
			try {
				sftp.closeConnections();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return status;
    }
    
    public boolean downloadInputDirectory() throws SftpException {
		try{
		sftp = new sftpFileConnection(sftpBean);
		sftp.getChannelSftp();
		//String downloadPath = new File(propBean.getTextFilePath()).getParent();
		sftp.downloadFromFolder1(FileUtil.removeFileNameFromLinuxPath(propBean.getTextFilePath()));
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}finally {
			try {
				sftp.closeConnections();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
    	}
    	catch(Exception e) {
    		return false;
    	}finally {
			try {
				sftp.closeConnections();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return true;
    }
    
    public boolean uploadFile(String inputPath, String destPath) {
    	try {
	    	sftp = new sftpFileConnection(sftpBean);
			sftp.getChannelSftp();
			sftp.uploadFiles(inputPath, destPath);
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File : " + inputPath + " Uploaded to " + destPath + CommonSharedConstants.newLine);
    	}
    	catch(Exception e) {
    		return false;
    	}finally {
			try {
				sftp.closeConnections();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return true;
    }
}
