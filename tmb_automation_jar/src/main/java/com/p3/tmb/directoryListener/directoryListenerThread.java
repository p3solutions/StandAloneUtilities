package com.p3.tmb.directoryListener;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.DateUtil;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.extractionProcess.extractionProcess;
import com.p3.tmb.extractionProcess.fileValidationProcess;
import com.p3.tmb.sftp.sftpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class directoryListenerThread extends TimerTask {
	Logger log = LogManager.getLogger(directoryListenerThread.class.getName());
	private sftpBean sftpBean = null;
	private propertyBean propBean = null;
	private fileValidationProcess validation = null;
	private sftpUtils sftpUtils = null;
	
	public boolean jobStatus = false;
	public String scheduledDateList = null;
	private List<String> txtFileList = null;
	private String pdfRemotePath = null;
	
	public directoryListenerThread(propertyBean propBean, sftpBean sftpBean) {
		this.sftpBean = sftpBean;
		this.propBean = propBean;
		this.validation = new fileValidationProcess(propBean, sftpBean);
		this.sftpUtils = new sftpUtils(sftpBean, propBean);
		this.pdfRemotePath = propBean.getPdfLocation();
	}

	@Override
	public void run() {
		startRunning();
	}
	
	public void startRunning() {
//		log.info("Accessed start running");
		jobStatus = true;
		txtFileList = null;
		boolean jobScheduleFlag = false;
		String rootPath =propBean.getTextFilePath();
		String folderName  = null;
		try {
		if(isTextFileAvailInDir(propBean.getFolderName1()) && isJobScheduleDate(propBean.getFolderName1()) || CommonSharedConstants.flag25Cycle)
		{
			folderName =  propBean.getFolderName1();
			propBean.setTextFilePath(propBean.getTextFilePath() + CommonSharedConstants.LINUX_PATH_SEPERATOR + propBean.getFolderName1());
			jobScheduleFlag = true;
		}
		else if(isTextFileAvailInDir(propBean.getFolderName2()) && isJobScheduleDate(propBean.getFolderName2()) || CommonSharedConstants.flagMonthend) {
			folderName =  propBean.getFolderName2();
			propBean.setTextFilePath(propBean.getTextFilePath() + CommonSharedConstants.LINUX_PATH_SEPERATOR + propBean.getFolderName2());
			jobScheduleFlag = true;
		}
		else if(isTextFileAvailInDir(CommonSharedConstants.FIXED_FOLDER)) {
			folderName =  CommonSharedConstants.FIXED_FOLDER;
			propBean.setTextFilePath(propBean.getTextFilePath() + CommonSharedConstants.LINUX_PATH_SEPERATOR + CommonSharedConstants.FIXED_FOLDER);
			jobScheduleFlag = true;
		}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.error(commonUtils.exceptionMsgToString(e));
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		try {
			String folderPath = propBean.getTextFilePath();
		if(txtFileList != null && (txtFileList.size() != 0) && jobScheduleFlag && sftpUtils.isJobReadyToExecute(folderPath+CommonSharedConstants.LINUX_PATH_SEPERATOR+CommonSharedConstants.folderProp)) 
		{
//			log.info("Root Path : " +rootPath);
//			CommonSharedConstants.logContent.append("Root path : " + rootPath + CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
//			log.info("Text file path : "+propBean.getTextFilePath());
//			log.info("Folder path:"+folderPath);
			startProcess();
			try {
				sftpUtils.deleteFile(folderPath + CommonSharedConstants.LINUX_PATH_SEPERATOR + CommonSharedConstants.folderProp);
				sftpUtils.createPropertyFilesinRemoteDir(rootPath,folderName,CommonSharedConstants.folderProp,CommonSharedConstants.readyForJobFalse);
			}catch (JSchException e){
				e.printStackTrace();
				log.error("Delete and Create : Error while updating Folder.properties - Could affect the processing of files in next schedule. PLEASE VERIFY IMMEDIATELY");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "\"Delete and Create : Error while updating Folder.properties - Could affect the processing of files in next schedule. PLEASE VERIFY IMMEDIATELY");

			}

		}
		else {
			
//			CommonSharedConstants.logContent.append("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
			String date = null;
			LocalDateTime currentDateTime = LocalDateTime.now();
			String dateTime = DateUtil.dateConversion(currentDateTime.toString(), "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd HH");
//			String dateTime = CommonSharedConstants.sdf4.format(currentDateTime.toString());
			Date date1 = CommonSharedConstants.sdf2.parse(currentDateTime.toString());
			String currentMonthYear =  CommonSharedConstants.sdf1.format(date1);
			String folder1DateTime = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder1Date() +  " " + propBean.getJobScheduleTime().split(":")[0];
			String folder2DateTime = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder2Date() +  " " + propBean.getJobScheduleTime().split(":")[0];
			String folder1DateTimeExt = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder1Date() +  " " + propBean.getJobScheduleTime();
			String folder2DateTimeExt = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder2Date() +  " " + propBean.getJobScheduleTime();
			
			if(folder1DateTime.equals(dateTime)) {
				CommonSharedConstants.flag25Cycle = true;
				CommonSharedConstants.extendHoursFlag = true;
			    CommonSharedConstants.extendDate = CommonSharedConstants.sdf3.parse(folder1DateTimeExt);
//				log.info("true");
//				log.info("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())));
//				CommonSharedConstants.logContent.append("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
			}
			else if(folder2DateTime.equals(dateTime)) {
				CommonSharedConstants.flagMonthend = true;
				CommonSharedConstants.extendHoursFlag = true;
				CommonSharedConstants.extendDate = CommonSharedConstants.sdf3.parse(folder2DateTimeExt);
//				log.info("true");
//				log.info("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())));
//				CommonSharedConstants.logContent.append("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
			}
			else if(!CommonSharedConstants.extendHoursFlag) {
				CommonSharedConstants.extendHoursFlag = true;
			}
			if(CommonSharedConstants.flag25Cycle || CommonSharedConstants.flagMonthend) {
				log.info("Waiting for mapping text file");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  " + CommonSharedConstants.newLine);
				}
			if(CommonSharedConstants.extendHoursCount == 12) {
				CommonSharedConstants.flag25Cycle = false;
				CommonSharedConstants.flagMonthend = false;
				CommonSharedConstants.extendHoursFlag = false;
				CommonSharedConstants.jobStatus = false;
				CommonSharedConstants.extendHoursCount = 0;
//				log.info("False2");
				log.info("Maximum job exceeded....");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Maximum job exceeded...." + CommonSharedConstants.newLine);
			}
			
		}
		propBean.setTextFilePath(rootPath);
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		jobStatus = false;
		scheduledDateList = null;
		CommonSharedConstants.jobStatus = false;
		CommonSharedConstants.jobScheduledDate = null;
		
	
	}
	
	private boolean isTextFileAvailInDir(String folderName ) {
		txtFileList =sftpUtils.checkTextFileExistence(folderName);
		if(txtFileList!=null) {
		return true;
		}
		
		return false;
	}
	
	public boolean isJobScheduleDate(String folderName) throws ParseException {
		
		String date = null;
		LocalDate currentdate = LocalDate.now(); 
		Date date1 = CommonSharedConstants.sdf2.parse(currentdate.toString());
		String currentMonthYear =  CommonSharedConstants.sdf1.format(date1);
		String folder1Date = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder1Date();
		String folder2Date = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder2Date();
		if(folderName.equals(propBean.getFolderName1()) && folder1Date.equals(currentdate.toString())) {
			return true;
		}
		else if(folderName.equals(propBean.getFolderName2()) && folder2Date.equals(currentdate.toString())) {
			return true;
		}
		else if(folderName.equals(CommonSharedConstants.FIXED_FOLDER)) {
			return true;
		}
		
		return false;
	}
	
//	public static void main(String[] args) throws ParseException {
//		String date = null;
//		LocalDateTime currentDateTime = LocalDateTime.now();
//		String dateTime = DateUtil.dateConversion(currentDateTime.toString(), "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd HH");
//		Date date1 = CommonSharedConstants.sdf2.parse(currentDateTime.toString());
//		String currentMonthYear =  CommonSharedConstants.sdf1.format(date1);
//		log.info("current");
//		String folder1DateTime = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder1Date() +  " " + CommonSharedConstants.sdf5.format(propBean.getJobScheduleTime());
//		String folder2DateTime = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder2Date() +  " " + CommonSharedConstants.sdf5.format(propBean.getJobScheduleTime());
//		if(folder1DateTime.equals(dateTime)) {
//			CommonSharedConstants.flag25Cycle = true;
//			log.info("true");
//		}
//		else if(folder2DateTime.equals(dateTime)) {
//			CommonSharedConstants.flagMonthend = true;
//			log.info("true");
//		}
//		else if(CommonSharedConstants.extendHoursCount == 12) {
//			CommonSharedConstants.flag25Cycle = false;
//			CommonSharedConstants.flagMonthend = false;
//			CommonSharedConstants.extendHoursCount = 0;
//		log.info("False2");
//		}
//	}
	
	private void startProcess() throws Exception {
		try {
			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Start Processing...");
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Start Processing..." + CommonSharedConstants.newLine);
			if(CommonSharedConstants.flag25Cycle) {
				CommonSharedConstants.flag25Cycle = false;
			}
			if(CommonSharedConstants.flagMonthend) {
				CommonSharedConstants.flagMonthend = false;
			}
			if(CommonSharedConstants.extendHoursFlag) {
				CommonSharedConstants.extendHoursFlag = false;
				CommonSharedConstants.extendHoursCount = 0;
			}
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			//if (sftpUtils.checkTextFileExistence()) {
			String outputDirName = DateUtil.getDateForDirectory();
			propBean.setDateTimeForReport(outputDirName);
			String txtFilePath = propBean.getTextFilePath();
			String pdfPath = pdfRemotePath + CommonSharedConstants.LINUX_PATH_SEPERATOR + outputDirName;
			propBean.setOutputLocation(FileUtil.createDir(new File(propBean.getOutputLocation()).getParent()
					+ File.separator + outputDirName).getAbsolutePath());
			
			sftpUtils.createDir(pdfPath);
			propBean.setPdfLocation(pdfPath);
			sftpBean.setLocalPath(propBean.getOutputLocation());
			FileUtil.createDir(propBean.getOutputLocation()+File.separator+CommonSharedConstants.INPUT_BACKUP_DIRECTORY);
			
			for(String txtFile : txtFileList) {
				propBean.setTextFilePath(txtFilePath + CommonSharedConstants.LINUX_PATH_SEPERATOR + txtFile);
				log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis()))+ "  Text file path : " + propBean.getTextFilePath());
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Text file path : "+ propBean.getTextFilePath() + CommonSharedConstants.newLine);
				log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Mapping file detected....");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Mapping file detected..." + CommonSharedConstants.newLine);
				sftpUtils.downloadFile(propBean.getTextFilePath());
				//sftpUtils.deleteFile(propBean.getTextFilePath());
				if (validation.startValidtaion()) {
					log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation succeefull!!!");
					CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation succeefull!!!" + CommonSharedConstants.newLine);
					sftpUtils sftpUtils = new sftpUtils(sftpBean, propBean);
					try {
						sftpUtils.downloadInputDirectory();
					} catch (SftpException e1) {
						e1.printStackTrace();
						CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e1) + CommonSharedConstants.newLine);
					}
//					log.info("Downloaded");
						
					extractionProcess extractionObj = new extractionProcess(propBean, sftpBean);
					extractionObj.startExtraction();
						
//					log.info("File Processed....");
//					CommonSharedConstants.logContent.append("File Processed...."+ CommonSharedConstants.newLine);
				} else {
					String deletePath = (propBean.getOutputLocation() + File.separator + "TEMP");
					FileUtil.deleteDirectory(deletePath);
					//FileUtil.deleteDirectory(propBean.getOutputLocation());
					log.info("File validation failed ....");
					CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation failed ...." + CommonSharedConstants.newLine);
				}
			}
			String logFilepath = propBean.getOutputLocation() + File.separator + "logFile_"+outputDirName +".log";
		    FileUtil.createLogFile(CommonSharedConstants.logContent,logFilepath);
		    sftpUtils.uploadFile(logFilepath, pdfPath);
		    CommonSharedConstants.logContent = new StringBuffer();
			//} else {
				//log.info("Waiting for mapping file.... : " + sdf3.format(timestamp));
			//}
		    propBean.setPdfLocation(FileUtil.removeFileNameFromLinuxPath(pdfPath));
		}

		catch (IOException e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		log.info("Looking for next folder"); 
		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Looking for next folder...." + CommonSharedConstants.newLine);
	}

	
}
