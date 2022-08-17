package com.p3.tmb.extractionProcess;

import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.report.extractionReportGenerator;
import com.p3.tmb.sftp.sftpFileConnection;
import com.p3.tmb.sftp.sftpUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;



public class fileValidationProcess {
	
	private propertyBean propBean;
	private sftpBean sftpBean = null;
	private int sourceRecordCount = 0;
	private int actualSourceRecordCount = 0;
	private int sourceBlobCount = 0;
	private int missingBlobCount = 0;
	private List<String> missingBlobFiles = null; 
	private sftpUtils sftpUtils = null;
	
	public fileValidationProcess(propertyBean propBean, sftpBean sftpBean) {
	this.propBean = propBean;
	this.sftpBean = sftpBean;
	this.sftpUtils = new sftpUtils(sftpBean, propBean);
	}

	private boolean startValidatingFile() throws IOException {
		boolean blobFlag = true;
		actualSourceRecordCount = 0;
		sourceBlobCount = 0;
		missingBlobCount = 0;
		missingBlobFiles = new ArrayList<String>();
		BufferedReader bufferedReader=null;
		FileReader reader = null;
		sftpFileConnection sftp1 = new sftpFileConnection(sftpBean);
		sftp1.getChannelSftp();
		try {
			int reportIndex=0,index=0;
//			sftp = new sftpFileConnection(sftpBean);
//			sftp.getChannelSftp();
			//bufferedReader = sftp.getInputFileBufferReader();	
			reader = new FileReader(new File(propBean.getOutputLocation()+File.separator+CommonSharedConstants.INPUT_BACKUP_DIRECTORY+File.separator+FileUtil.getFileNameFromLinuxPath(propBean.getTextFilePath())));
			bufferedReader = new BufferedReader(reader);

			String line;
			boolean columnFlag = true;
			 
			while ((line = bufferedReader.readLine()) != null) {

				if (columnFlag) {
//					String[] columns = propBean.getColumnNames().split(",");
					String[] columns = CommonSharedConstants.columnNames.split(",");
					for (String column : columns) {
						if (column.toUpperCase().equals("REPORT")) {
							reportIndex = index;						
							} 
						index++;
						
					}
					columnFlag = false;
				} else {
					String[] columnValue = line.split(",");
					 if(index==columnValue.length-1) {
//				     String filePath = propBean.getOutputLocation()+File.separator+"inputBakup" + File.separator+columnValue[reportIndex+1];
//				     File file = new File(filePath);
//				     if(!file.exists()) {
//				    	 fileFlag = false;
//				     }
					
				   
					boolean checkFile = sftp1.checkForFileExistence(FileUtil.removeFileNameFromLinuxPath(propBean.getTextFilePath()) + "/" + columnValue[reportIndex+1]);
						// boolean checkFile = sftpUtils.checkForFileExistence(FileUtil.removeFileNameFromLinuxPath(propBean.getTextFilePath()) + "/" + columnValue[reportIndex+1]);
				     if(!checkFile) {
				    	 blobFlag =  false;
				    	 missingBlobFiles.add(columnValue[reportIndex+1]);
				    	 missingBlobCount++;
				     }
				     else {
				    	 sourceBlobCount++;
				     }
				     actualSourceRecordCount++;
					 }
					}
				}
		} catch (IOException e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);

		} finally {
			if (reader != null)
				reader.close();
			if (bufferedReader != null)
				bufferedReader.close();
			if(sftp1 != null) {
				sftp1.closeConnections();
		}
		}

		return blobFlag;
	}
	
	public boolean startValidtaion() throws IOException {
		System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation started...");
		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  File validation started..." + CommonSharedConstants.newLine);
		boolean status = false;
		getSourceRecordCount();
		if(startValidatingFile() && actualSourceRecordCount==sourceRecordCount) {
			status = true;
			//return true;
		}
		extractionReportGenerator rep = new extractionReportGenerator(propBean, sftpBean);
		rep.generateValidationReport(sourceRecordCount, actualSourceRecordCount, sourceBlobCount, missingBlobCount, missingBlobFiles);
		return status;
		
	}
	private void getSourceRecordCount() {
		sourceRecordCount = 0;
		try {
			 FileReader reader = new FileReader(propBean.getOutputLocation()+File.separator+CommonSharedConstants.INPUT_BACKUP_DIRECTORY+File.separator+FileUtil.getFileNameFromLinuxPath(propBean.getTextFilePath()));
	         BufferedReader bufferedReader = new BufferedReader(reader);
	         String line = null;
	         String nextLine;
	         String[] splitValue = null;
	         while ((nextLine = bufferedReader.readLine()) != null) {
	             line = nextLine;
	          
	         }
	         sourceRecordCount = Integer.parseInt(line.split(",")[1]);
	         System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Total record count in mapping file : " + sourceRecordCount);
	         CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Total record count in mapping file : " + sourceRecordCount + CommonSharedConstants.newLine);
	     } catch (IOException e) {
	         e.printStackTrace();
	         CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
	     
		}
	}

}
