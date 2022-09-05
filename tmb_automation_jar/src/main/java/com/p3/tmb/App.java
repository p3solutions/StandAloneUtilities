package com.p3.tmb;

import com.p3.tmb.beans.inputBean;
import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.DateUtil;
import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.constant.INGESTER_CONSTANT;
import com.p3.tmb.constant.PROPERTY_CONSTANT;
import com.p3.tmb.directoryListener.directoryListener;
import com.p3.tmb.property.parsePropertyFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.kohsuke.args4j.CmdLineParser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

//		System.setProperty("logFilename", "log");
//		System.setProperty("basePath", "logs");

		final Logger log = LogManager.getLogger(App.class.getName());

		log.info("#################################### TMB AUTOMATION START RUNNING ####################################");
    	//System.out.println("Enc : " + securityModule.perfromEncrypt("FLEET CARD SAMPLE", "autoappusr", ""));
    	//System.out.println("ENC : " + encryptionUtils.encryptor("Pass@265"));
    	//System.out.println("ENC : " + encryptionUtils.encryptor("clarchon@123"));
    	CommonSharedConstants.jobStartTime = new Date().getTime();
    	CommonSharedConstants.jobStartDateTime = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss").format(new Date());
	    inputBean inputArgs = new inputBean();
		CmdLineParser parser = new CmdLineParser(inputArgs);
		try {
			String[] trimmedArgs = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				trimmedArgs[i] = args[i].trim();
			}
			parser.parseArgument(trimmedArgs);
			if (validateArguments(args)) {
				parsePropertyFile propertyFile = new parsePropertyFile(inputArgs.getPropertyFilePath());
				// stem.out.println(propertyFile.getValue("TEXT_FILE_PATH"));
				propertyBean propBean = new propertyBean();
				
				propBean.setTextFilePath(propertyFile.getValue(PROPERTY_CONSTANT.TEXT_FILE_PATH));
				propBean.setColumnNames(propertyFile.getValue(PROPERTY_CONSTANT.COLUMN_NAMES));
				propBean.setDateColumns(propertyFile.getValue(PROPERTY_CONSTANT.DATE_COLUMNS));
				propBean.setDatabaseName(propertyFile.getValue(PROPERTY_CONSTANT.DB_NAME));
				propBean.setHolding(propertyFile.getValue(PROPERTY_CONSTANT.HOLDING_NAME));
				propBean.setOutputLocation(propertyFile.getValue(PROPERTY_CONSTANT.OUTPUT_LOCATION)+File.separator+DateUtil.getDateForDirectory());
				propBean.setSchemaName(propertyFile.getValue(PROPERTY_CONSTANT.SCHEMA_NAME));
				propBean.setTableName(propertyFile.getValue(PROPERTY_CONSTANT.TABLE_NAME));
				propBean.setApplicationName(propertyFile.getValue(PROPERTY_CONSTANT.APPLICATION_NAME));
//				propBean.setBlobPath(propertyFile.getValue(PROPERTY_CONSTANT.BLOB_PATH));
				propBean.setBlobColumn(propertyFile.getValue(PROPERTY_CONSTANT.BLOB_COLUMN));
				propBean.setIaUser(propertyFile.getValue(INGESTER_CONSTANT.IA_USER));
				propBean.setIaPassword(propertyFile.getValue(INGESTER_CONSTANT.IA_PASSWORD));
				propBean.setIaPath(propertyFile.getValue(INGESTER_CONSTANT.IA_INSTALLATION_PATH));
//				propBean.setServerBackupPath(propertyFile.getValue(PROPERTY_CONSTANT.SERVER_BACKUP_PATH));
				propBean.setPdfLocation(propertyFile.getValue(PROPERTY_CONSTANT.PDF_LOCATION));
				propBean.setJobScheduleTime(propertyFile.getValue(PROPERTY_CONSTANT.JOB_SCHEDULE_TIME));
				propBean.setFolderName1(propertyFile.getValue(PROPERTY_CONSTANT.FOLDER_NAME1));
				propBean.setFolderName2(propertyFile.getValue(PROPERTY_CONSTANT.FOLDER_NAME2));
				propBean.setFolder1Date(propertyFile.getValue(PROPERTY_CONSTANT.FOLDER1_DATE));
				propBean.setFolder2Date(propertyFile.getValue(PROPERTY_CONSTANT.FOLDER2_DATE));
				propBean.setSipSplitSize(Integer.valueOf(propertyFile.getValue(PROPERTY_CONSTANT.SIP_SPLIT_SIZE)));
//				propBean.setHolding_reconcile(propertyFile.getValue(PROPERTY_CONSTANT.HOLDING_NAME_RECONCILE));
//				propBean.setTableName_reconcile(propertyFile.getValue(PROPERTY_CONSTANT.TABLE_NAME_RECONCILE));
				
				sftpBean sftpBean = new sftpBean();
				sftpBean.setUserName(propertyFile.getValue(PROPERTY_CONSTANT.SFTP_USER));
				sftpBean.setPassword(propertyFile.getValue(PROPERTY_CONSTANT.SFTP_PASSWORD));
				sftpBean.decryptor();
				sftpBean.setRemoteHost(propertyFile.getValue(PROPERTY_CONSTANT.HOST));
				sftpBean.setRemotePort(propertyFile.getValue(PROPERTY_CONSTANT.PORT));
				sftpBean.setRemotePath(propBean.getTextFilePath());
				sftpBean.setLocalPath(propBean.getOutputLocation());
				directoryListener dlObj = new directoryListener(propBean, sftpBean);
				dlObj.startListening();
//				extractionProcess extractionObj = new extractionProcess(propBean, sftpBean);
//				extractionObj.startExtraction();
			
				
			} else {
				System.out.println("Invalid arguments");
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		log.info("#################################### TMB AUTOMATION STOP RUNNING ####################################");
	}

	private static boolean validateArguments(String InputArgs[]) {
		boolean checkValidate = false;
		try {
//			System.out.println("input args" + InputArgs[0]);
			int count = 0;
			if (new File(InputArgs[1]).exists())
				count++;
			if (count == 0) {
				System.out.println("Property file path does not exists...");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		return true;
	}
}
