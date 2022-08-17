package com.p3.tmb.commonUtils;

import com.p3.tmb.constant.CommonSharedConstants;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
	public static String dateConversion(String inputDate,String inputPattern,String outputPattern) {
		String outDate = "";
		try {
			SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
			Date dateFormat = inputFormat.parse(inputDate);
			SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
			outDate = outputFormat.format(dateFormat);
			// System.out.println("output date:"+outDate);
		} catch (Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		return outDate;
	}
	
	
	public static String getDateForDirectory() {
		DateFormat dform = new SimpleDateFormat("yyyyMMddHHmmss");
		Date obj = new Date();
//		System.out.println(dform.format(obj)); 
		return dform.format(obj);
	}
	
//	public static String getDateForDirectory1() {
//		DateFormat dform = new SimpleDateFormat("yyyyMMdd");
//		Date obj = new Date();
//		//System.out.println(dform.format(obj)); 
//		return dform.format(obj);
//	}
	
	public static String getCurrentYearMonth() {
		
		LocalDate currentdate = LocalDate.now();
//	      System.out.println("Current date: "+currentdate.toString());
	     
	      return currentdate.toString().substring(0,currentdate.toString().lastIndexOf("-"));
	}
	
	public static String getNextDate(String dateInString) throws ParseException {
		
		String DATE_FORMAT = "yyyy-MM-dd";
		long ONE_DAY_MILLI_SECONDS = 24 * 60 * 60 * 1000;
		
		// Simple date formatter
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		Date date = dateFormat.parse(dateInString);

		// Getting the next day and formatting into 'YYYY-MM-DD'
		long nextDayMilliSeconds = date.getTime() + ONE_DAY_MILLI_SECONDS;
		Date nextDate = new Date(nextDayMilliSeconds);
		String nextDateStr = dateFormat.format(nextDate);
//		System.out.println("Next Date : " + nextDateStr);
		
		return nextDateStr;
	}
	
	public static String getNextDateFromFolderDate(String folderDate) throws ParseException {
		
		String resDate = null;
		String Date = getCurrentYearMonth() + "-" + folderDate;
		int lastDate = getLastDayOfMonth(Date);
		int fdate = Integer.parseInt(folderDate);
		if(fdate <= lastDate && fdate > 0)
			resDate = getNextDate(Date);
		
		return resDate;
	}
	
	public static int getLastDayOfMonth(String dateInString) {
		
		LocalDate convertedDate = LocalDate.parse(dateInString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		convertedDate = convertedDate.withDayOfMonth(
		                                convertedDate.getMonth().length(convertedDate.isLeapYear()));
		
//		System.out.println("Convert Date : " + convertedDate.toString());
		
		return Integer.parseInt(convertedDate.toString().split("-")[2]);
				
	}
	
	private static boolean isTodayScheduleTimeAvailable(int hour,int min) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Date currentDateTime = timestamp;
		int hr = currentDateTime.getHours();
		int  mins = min==0?60:min;
		if(hr<=hour) {
			if(hr == hour-1) {
				if(currentDateTime.getMinutes() < mins-1) {
					return true;
				}
			}
			else if(hr==hour){
				if(currentDateTime.getMinutes() < mins-1)
					return true;
			}
			else {
				return true;
			}
		}
		return false;
	}

	public static String getNextScheduledDateTime(String scheduledTime) throws ParseException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time[] = scheduledTime.split(":");
		if(isTodayScheduleTimeAvailable(Integer.parseInt(time[0]),Integer.parseInt(time[1]))) {
			LocalDate currentdate = LocalDate.now();
			System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Next Scheduled Job Date(Today) : " + currentdate + " " + scheduledTime);
			return currentdate + " " + scheduledTime;
		}
		LocalDate currentdate = LocalDate.now();
		//String nextDate = currentdate.getYear()+"-"+currentdate.getMonth()+"-"+getNextDateFromFolderDate.getDayOfMonth()+1
		String nextDate = getNextDateFromFolderDate(getDayFormat(currentdate.getDayOfMonth()));
		Date date = dateFormatter.parse(nextDate + " " + scheduledTime);
		//Date date = dateFormatter.parse(currentdate + " " + time[0] + ":" + (Integer.parseInt(time[1])+3) + ":" + time[2]);
		System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Next Scheduled Job Date(Tmrw) : "+  dateFormatter.format(date));
		return dateFormatter.format(date);
	}

	public static String getDayFormat(int day) {
		if(day<10) {
			return "0"+day;
		}
		
		return String.valueOf(day);
	}
	
}

