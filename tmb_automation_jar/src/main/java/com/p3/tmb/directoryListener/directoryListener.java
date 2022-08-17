package com.p3.tmb.directoryListener;

import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.DateUtil;
import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.sftp.sftpUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class directoryListener {

	private sftpBean sftpBean = null;
	private propertyBean propBean = null;
	
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
	   sftp.createPropertyFilesinRemoteDir(propBean.getTextFilePath(),propBean.getFolderName1(),CommonSharedConstants.folderProp,content);
	   sftp.createPropertyFilesinRemoteDir(propBean.getTextFilePath(),propBean.getFolderName2(),CommonSharedConstants.folderProp,content);
	   sftp.createPropertyFilesinRemoteDir(propBean.getTextFilePath(),CommonSharedConstants.FIXED_FOLDER,CommonSharedConstants.folderProp,content);
	   SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	   CommonSharedConstants.extendDate = dateFormatter.parse(currentDate);
	   
		while(true){
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			//System.out.println("Time Stamp : "+timestamp);
			
			Date date = dateFormatter.parse(currentDate);
			
			if(!CommonSharedConstants.jobStatus && (!CommonSharedConstants.flag25Cycle && !CommonSharedConstants.flagMonthend)) {	
				
				try {
					
					//if(timer != null)
						timer.cancel();
					timer = new Timer();
					dtt.scheduledDateList = currentDate;
					timer.schedule(dtt,date);
					CommonSharedConstants.jobStatus = true;
//					System.out.println("Job Status in TRY : " + CommonSharedConstants.jobStatus );
					CommonSharedConstants.extractionDate = currentDate.toString();
					System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Job Scheduled Datetime : " + currentDate);
				}
				catch(IllegalStateException e) {
					if(CommonSharedConstants.jobScheduledDate == null) {
					
					dtt = new directoryListenerThread(propBean, sftpBean);
					}
//					System.out.println("IllegalStateExceptions");
//				    e.printStackTrace();
//					CommonSharedConstants.logContent.append("Exception in startListening : " + commonUtils.exceptionMsgToString(e));
				}
			}
			if(!CommonSharedConstants.jobStatus  && (!CommonSharedConstants.flag25Cycle && !CommonSharedConstants.flagMonthend)) {
		    currentDate = DateUtil.getNextScheduledDateTime(scheduleTime);
		    Date date1 = dateFormatter.parse(currentDate);
//		    scheduleTime = DateUtil.getDayFormat(date1.getHours()) + ":" + DateUtil.getDayFormat(date1.getMinutes()) + ":" + DateUtil.getDayFormat(date1.getSeconds());
		    CommonSharedConstants.jobScheduledDate = currentDate;
		   // System.out.println("Scheduled Date updated to : " + CommonSharedConstants.jobScheduledDate );
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
				System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  ExtendHours Scheduled at : " + CommonSharedConstants.extendDate);
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  ExtendHours Scheduled at : " +  CommonSharedConstants.extendDate + CommonSharedConstants.newLine);
				CommonSharedConstants.extendHoursCount++;
				//				extendCount++;
//				System.out.println("Extend Count in INC : " +extendCount);
			}
			if(CommonSharedConstants.extendHoursCount == 12) {
				CommonSharedConstants.flag25Cycle = false;
				CommonSharedConstants.flagMonthend = false;
				CommonSharedConstants.extendHoursFlag = false;
				CommonSharedConstants.jobStatus = false;
				CommonSharedConstants.extendHoursCount = 0;
//				System.out.println("False1");
				System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Maximum job exceeded....");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Maximum job exceeded...." + CommonSharedConstants.newLine);
			}
//			else if((CommonSharedConstants.flag25Cycle || CommonSharedConstants.flagMonthend) && (extendCount%2 == 0) ) {
//				extendCount = 0;
//				directoryListenerThread dirListenerObj = new directoryListenerThread(propBean, sftpBean);
//				System.out.println(CommonSharedConstants.extendHoursCount + " Hour ");
//				dirListenerObj.startRunning();
//				CommonSharedConstants.extendHoursCount++;
//				System.out.println("ExtendHours Count : " + CommonSharedConstants.extendHoursCount);
//				System.out.println("Extend Count : " +extendCount);
//			}
//			else if(CommonSharedConstants.flag25Cycle || CommonSharedConstants.flagMonthend) {
//				extendCount++;
//				System.out.println("Extend Count : " +extendCount);
//			}
			
			
//			Thread.sleep(5000);
    		Thread.sleep(1000*60*15);
//			Thread.sleep(1000*60*2);

		}	
	}
}
