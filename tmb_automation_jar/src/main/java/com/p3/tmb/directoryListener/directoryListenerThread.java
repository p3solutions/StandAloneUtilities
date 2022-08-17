package com.p3.tmb.directoryListener;

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
import org.apache.log4j.Logger;

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
	static Logger log = Logger.getLogger(directoryListenerThread.class.getName());
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
//		System.out.println("Accessed start running");
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
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		try {
			String folderPath = propBean.getTextFilePath();
		if(txtFileList != null && (txtFileList.size() != 0) && jobScheduleFlag && sftpUtils.isJobReadyToExecute(folderPath+CommonSharedConstants.LINUX_PATH_SEPERATOR+CommonSharedConstants.folderProp)) 
		{
//			System.out.println("Root Path : " +rootPath);
//			CommonSharedConstants.logContent.append("Root path : " + rootPath + CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
//			System.out.println("Text file path : "+propBean.getTextFilePath());
//			System.out.println("Folder path:"+folderPath);
			startProcess();
			
		    sftpUtils.deleteFile(folderPath + CommonSharedConstants.LINUX_PATH_SEPERATOR + CommonSharedConstants.folderProp);
		    sftpUtils.createPropertyFilesinRemoteDir(rootPath,folderName,CommonSharedConstants.folderProp,CommonSharedConstants.readyForJobFalse);
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
//				System.out.println("true");
//				System.out.println("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())));
//				CommonSharedConstants.logContent.append("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
			}
			else if(folder2DateTime.equals(dateTime)) {
				CommonSharedConstants.flagMonthend = true;
				CommonSharedConstants.extendHoursFlag = true;
				CommonSharedConstants.extendDate = CommonSharedConstants.sdf3.parse(folder2DateTimeExt);
//				System.out.println("true");
//				System.out.println("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())));
//				CommonSharedConstants.logContent.append("Waiting for mapping text file : " +  CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
			}
			else if(!CommonSharedConstants.extendHoursFlag) {
				CommonSharedConstants.extendHoursFlag = true;
			}
			if(CommonSharedConstants.flag25Cycle || CommonSharedConstants.flagMonthend) {
				System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Waiting for mapping text file");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  " + CommonSharedConstants.newLine);
				}
			if(CommonSharedConstants.extendHoursCount == 12) {
				CommonSharedConstants.flag25Cycle = false;
				CommonSharedConstants.flagMonthend = false;
				CommonSharedConstants.extendHoursFlag = false;
				CommonSharedConstants.jobStatus = false;
				CommonSharedConstants.extendHoursCount = 0;
//				System.out.println("False2");
				System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Maximum job exceeded....");
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
//		System.out.println("current");
//		String folder1DateTime = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder1Date() +  " " + CommonSharedConstants.sdf5.format(propBean.getJobScheduleTime());
//		String folder2DateTime = currentMonthYear + CommonSharedConstants.hiphen + propBean.getFolder2Date() +  " " + CommonSharedConstants.sdf5.format(propBean.getJobScheduleTime());
//		if(folder1DateTime.equals(dateTime)) {
//			CommonSharedConstants.flag25Cycle = true;
//			System.out.println("true");
//		}
//		else if(folder2DateTime.equals(dateTime)) {
//			CommonSharedConstants.flagMonthend = true;
//			System.out.println("true");
//		}
//		else if(CommonSharedConstants.extendHoursCount == 12) {
//			CommonSharedConstants.flag25Cycle = false;
//			CommonSharedConstants.flagMonthend = false;
//			CommonSharedConstants.extendHoursCount = 0;
//		System.out.println("False2");
//		}
//	}
	
	private void startProcess() throws Exception {
		try {
			System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Start Processing...");
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
				System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis()))+ "  Text file path : " + propBean.getTextFilePath());
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Text file path : "+ propBean.getTextFilePath() + CommonSharedConstants.newLine);
				System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Mapping file detected....");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Mapping file detected..." + CommonSharedConstants.newLine);
				sftpUtils.downloadFile(propBean.getTextFilePath());
				//sftpUtils.deleteFile(propBean.getTextFilePath());
				if (validation.startValidtaion()) {
					System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation succeefull!!!");
					CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation succeefull!!!" + CommonSharedConstants.newLine);
					sftpUtils sftpUtils = new sftpUtils(sftpBean, propBean);
					try {
						sftpUtils.downloadInputDirectory();
					} catch (SftpException e1) {
						e1.printStackTrace();
						CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e1) + CommonSharedConstants.newLine);
					}
//					System.out.println("Downloaded");
						
					extractionProcess extractionObj = new extractionProcess(propBean, sftpBean);
					extractionObj.startExtraction();
						
//					System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File Processed....");
//					CommonSharedConstants.logContent.append("File Processed...."+ CommonSharedConstants.newLine);
				} else {
					String deletePath = (propBean.getOutputLocation() + File.separator + "TEMP");
					FileUtil.deleteDirectory(deletePath);
					//FileUtil.deleteDirectory(propBean.getOutputLocation());
					System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation failed ....");
					CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation failed ...." + CommonSharedConstants.newLine);
				}
			}
			String logFilepath = propBean.getOutputLocation() + File.separator + "logFile_"+outputDirName +".log";
		    FileUtil.createLogFile(CommonSharedConstants.logContent,logFilepath);
		    sftpUtils.uploadFile(logFilepath, pdfPath);
		    CommonSharedConstants.logContent = new StringBuffer();
			//} else {
				//System.out.println("Waiting for mapping file.... : " + sdf3.format(timestamp));
			//}
		    propBean.setPdfLocation(FileUtil.removeFileNameFromLinuxPath(pdfPath));
		}

		catch (IOException e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Looking for next folder"); 
		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Looking for next folder...." + CommonSharedConstants.newLine);
	}

	
}
