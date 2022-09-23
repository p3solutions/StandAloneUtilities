package com.p3.tmb.directoryListener;

import com.jcraft.jsch.JSchException;
import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.DateUtil;
import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.sftp.sftpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class directoryListener {

	private sftpBean sftpBean = null;
	private propertyBean propBean = null;

	final Logger log = LogManager.getLogger(directoryListener.class.getName());
	private directoryListenerThread dtt;

	public directoryListener(propertyBean propBean, sftpBean sftpBean) {
		this.sftpBean = sftpBean;
		this.propBean = propBean;
	}
	
	public void startListening() throws ParseException, InterruptedException {
//		boolean isJobAvail = false;
//		while(!isJobAvail) {
//		LocalDate currentdate = LocalDate.now();
//		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	    Date date = dateFormatter .parse(currentdate.toString()+" 18:00:00");
//
//	    //Now create the time and schedule it
//	    Timer timer = new Timer();
//
//	    //Use this if you want to execute it once
//	    timer.schedule(new directoryListenerThread(propBean, sftpBean), date);		
//		}
			//LocalDate currentdate = LocalDate.now();
		int extendCount = 0;
		String currentDate = DateUtil.getNextScheduledDateTime(propBean.getJobScheduleTime());
		directoryListenerThread dtt = new directoryListenerThread(propBean, sftpBean);
		CommonSharedConstants.jobStatus = false;
		CommonSharedConstants.jobScheduledDate = currentDate;
		Timer timer =  new Timer();
		String scheduleTime = propBean.getJobScheduleTime();
	   sftpUtils sftp = new sftpUtils(sftpBean, propBean);
	   String content = CommonSharedConstants.readyForJobTrue;
		try {
			sftp.createPropertyFilesinRemoteDir(propBean.getTextFilePath(),propBean.getFolderName1(),CommonSharedConstants.folderProp,content);
		} catch (JSchException e) {
			log.error("SFTP Connection Error while creating  "+propBean.getFolderName1()+ " with content "+content);
		}
		try {
			sftp.createPropertyFilesinRemoteDir(propBean.getTextFilePath(),propBean.getFolderName2(),CommonSharedConstants.folderProp,content);
		} catch (JSchException e) {
			log.error("SFTP Connection Error while creating  "+propBean.getFolderName2()+ " with content "+content);
		}
		try {
			sftp.createPropertyFilesinRemoteDir(propBean.getTextFilePath(),CommonSharedConstants.FIXED_FOLDER,CommonSharedConstants.folderProp,content);
		} catch (JSchException e) {
			log.error("SFTP Connection Error while creating  "+CommonSharedConstants.FIXED_FOLDER+ " with content "+content);
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	   CommonSharedConstants.extendDate = dateFormatter.parse(currentDate);
	   
		while(true){
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			//log.info("Time Stamp : "+timestamp);
			
			Date date = dateFormatter.parse(currentDate);
			
			if(!CommonSharedConstants.jobStatus && (!CommonSharedConstants.flag25Cycle && !CommonSharedConstants.flagMonthend)) {	
				
				try {
					
					//if(timer != null)
						timer.cancel();
					timer = new Timer();
					dtt.scheduledDateList = currentDate;
					timer.schedule(dtt,date);
					CommonSharedConstants.jobStatus = true;
//					log.info("Job Status in TRY : " + CommonSharedConstants.jobStatus );
					CommonSharedConstants.extractionDate = currentDate.toString();
					log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Job Scheduled Datetime : " + currentDate);
				}
				catch(IllegalStateException e) {
					if(CommonSharedConstants.jobScheduledDate == null) {
					
					dtt = new directoryListenerThread(propBean, sftpBean);
					}
//					log.info("IllegalStateExceptions");
//				    e.printStackTrace();
//					CommonSharedConstants.logContent.append("Exception in startListening : " + commonUtils.exceptionMsgToString(e));
				}
			}
			if(!CommonSharedConstants.jobStatus  && (!CommonSharedConstants.flag25Cycle && !CommonSharedConstants.flagMonthend)) {
		    currentDate = DateUtil.getNextScheduledDateTime(scheduleTime);
		    Date date1 = dateFormatter.parse(currentDate);
//		    scheduleTime = DateUtil.getDayFormat(date1.getHours()) + ":" + DateUtil.getDayFormat(date1.getMinutes()) + ":" + DateUtil.getDayFormat(date1.getSeconds());
		    CommonSharedConstants.jobScheduledDate = currentDate;
		   // log.info("Scheduled Date updated to : " + CommonSharedConstants.jobScheduledDate );
			}
			if((CommonSharedConstants.flag25Cycle || CommonSharedConstants.flagMonthend) && CommonSharedConstants.extendHoursFlag && CommonSharedConstants.extendHoursCount != 12) {
				
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(CommonSharedConstants.extendDate.getTime());
				calendar.add(calendar.HOUR, 1);
//				calendar.add(calendar.MINUTE, 4);
				dtt = new directoryListenerThread(propBean, sftpBean);
				CommonSharedConstants.extendDate = calendar.getTime();
				timer.cancel();
				timer = new Timer();
				timer.schedule(dtt, CommonSharedConstants.extendDate);
				CommonSharedConstants.jobStatus = true;
				CommonSharedConstants.extendHoursFlag = false;
				log.info("ExtendHours Scheduled at : " + CommonSharedConstants.extendDate);
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  ExtendHours Scheduled at : " +  CommonSharedConstants.extendDate + CommonSharedConstants.newLine);
				CommonSharedConstants.extendHoursCount++;
				//				extendCount++;
//				log.info("Extend Count in INC : " +extendCount);
			}
			if(CommonSharedConstants.extendHoursCount == 12) {
				CommonSharedConstants.flag25Cycle = false;
				CommonSharedConstants.flagMonthend = false;
				CommonSharedConstants.extendHoursFlag = false;
				CommonSharedConstants.jobStatus = false;
				CommonSharedConstants.extendHoursCount = 0;
//				log.info("False1");
				log.info("Maximum job exceeded....");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Maximum job exceeded...." + CommonSharedConstants.newLine);
			}
//			else if((CommonSharedConstants.flag25Cycle || CommonSharedConstants.flagMonthend) && (extendCount%2 == 0) ) {
//				extendCount = 0;
//				directoryListenerThread dirListenerObj = new directoryListenerThread(propBean, sftpBean);
//				log.info(CommonSharedConstants.extendHoursCount + " Hour ");
//				dirListenerObj.startRunning();
//				CommonSharedConstants.extendHoursCount++;
//				log.info("ExtendHours Count : " + CommonSharedConstants.extendHoursCount);
//				log.info("Extend Count : " +extendCount);
//			}
//			else if(CommonSharedConstants.flag25Cycle || CommonSharedConstants.flagMonthend) {
//				extendCount++;
//				log.info("Extend Count : " +extendCount);
//			}

			log.info("Thread Sleeping for "+1000*60*15 +"ms");
//			Thread.sleep(5000);
    		Thread.sleep(1000*60*15);
			log.info("Thread Sleep Completed");
//			Thread.sleep(1000*60*2);

		}	
	}
}
