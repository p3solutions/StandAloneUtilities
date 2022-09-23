package com.p3.tmb.extractionProcess;

import com.jcraft.jsch.SftpException;
import com.p3.tmb.beans.columnBean;
import com.p3.tmb.beans.outputXmlBean;
import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.DateUtil;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.core.sip.PdiSchemaGeneration;
import com.p3.tmb.core.sip.RecordData;
import com.p3.tmb.core.sip.SipCreator;
import com.p3.tmb.ingester.beans.ingesterInputBean;
import com.p3.tmb.ingester.ingesterProcess;
import com.p3.tmb.report.extractionReportGenerator;
import com.p3.tmb.sftp.sftpFileConnection;
import com.p3.tmb.sftp.sftpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xerces.util.XMLChar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class extractionProcess {
	static Logger log = LogManager.getLogger(extractionProcess.class.getName());
	private propertyBean propBean;
	private sftpBean sftpBean;
	private sftpUtils sftpUtils;  
	private LinkedHashMap<String, String> dateColumnDetails = null;
	private ArrayList<ArrayList<outputXmlBean>> xmlBeans = null;
	private ArrayList<ArrayList<outputXmlBean>> xmlReconcileBeans = null;
	private LinkedHashMap<String, Integer> countMap = null;
	private SipCreator sipCreator = null;
	private int srcRowCount;
	private int desRowCount;
	private int srcBlobCount;
	private int desBlobCount;
	sftpFileConnection sftp = null;
	LinkedHashMap<String, String> columnMapReconcile = new LinkedHashMap<String, String>();

	public extractionProcess(propertyBean propBean, sftpBean sftpBean) {
		this.propBean = propBean;
		this.sftpBean = sftpBean;
		this.dateColumnDetails = new LinkedHashMap<String, String>();
		this.xmlBeans = new ArrayList<ArrayList<outputXmlBean>>();
		this.sftpUtils = new sftpUtils(sftpBean, propBean);
	}

	public void startExtraction() throws Exception {
		try {
			log.info("Extraction Started");
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Extraction Started" + CommonSharedConstants.newLine);
		PdiSchemaGeneration pdiObj = new PdiSchemaGeneration(propBean);
		pdiObj.startPdiSchema(propBean.getTableName(), propBean.getHolding(), startProcessingFile());
//		pdiObj.startPdiSchema(propBean.getTableName_reconcile(), propBean.getHolding_reconcile(), columnMapReconcile);
		sipCreator();
//		sipCreatorReconcile();
    	CommonSharedConstants.jobEndDateTime = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss").format(new Date());
		CommonSharedConstants.jobEndTime = new Date().getTime();
		CommonSharedConstants.srcRowCount = srcRowCount-1;
		CommonSharedConstants.desRowCount = desRowCount;
		CommonSharedConstants.srcBlobCount = srcBlobCount;
		CommonSharedConstants.desBlobCount = desBlobCount;
		//CommonSharedConstants.jobProcessTime = TimeUnit.MILLISECONDS.toMillis(new Date(CommonSharedConstants.jobStartDateTime).getTime() - new Date(CommonSharedConstants.jobEndDateTime).getTime());
		extractionReportGenerator report = new extractionReportGenerator(propBean, sftpBean);
		log.info("Extraction Completed");
		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Extraction Completed" + CommonSharedConstants.newLine);
		report.generateDataExtractionReport();
		}
		catch(Exception e) {
			log.info("Extraction Failed");
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Extraction Failed " + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			e.printStackTrace();
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		
		}
		try {
			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Ingestion Started");
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Ingestion Started" + CommonSharedConstants.newLine);
			startIngestionProcess();
//			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + ":  Ingestion Completed");
//			CommonSharedConstants.logContent.append("Ingestion Completed : " + CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
		}
		catch(Exception e) {
			log.error("Ingestion Failed");
//			CommonSharedConstants.logContent.append("Ingestion Failed : " + CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + CommonSharedConstants.newLine);
			e.printStackTrace();
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		//zipUtils zipObj = new zipUtils(propBean.getOutputLocation());
		//zipObj.startZip();
		try {
			sftp = new sftpFileConnection(sftpBean);
			sftp.getChannelSftp();
			//sftp.uploadFiles(zipObj.startZip(), propBean.getServerBackupPath());
			File outputDir = new File(propBean.getOutputLocation());
			File[] fileList = outputDir.listFiles();
			for (File f : fileList) {
				if (FileUtil.getExtension(f.getName()).equalsIgnoreCase(".pdf"))
					sftp.uploadFiles(f.getAbsolutePath(), propBean.getPdfLocation());
			}
		}finally {
			sftp.closeConnections();
		}
		String deletePath = (propBean.getOutputLocation() + File.separator + "TEMP");
		FileUtil.deleteDirectory(deletePath);
		String deleteZipFile = (propBean.getOutputLocation() + File.separator + FileUtil.getFileNameFromPath(propBean.getOutputLocation())+".zip");
		FileUtil.deleteFile(deleteZipFile);
		//Files.deleteIfExists(Paths.get(deletePath));
	}

	private LinkedHashMap<String, String> startProcessingFile() throws IOException, SftpException {
		FileReader reader = null;
		BufferedReader bufferedReader = null;
		ArrayList<columnBean> columnDetailList = null;
		countMap = new LinkedHashMap<String, Integer>();
		boolean reconcileColumnFlag = false;
		xmlReconcileBeans = new ArrayList<ArrayList<outputXmlBean>>();
		LinkedHashMap<String, String> columnMap = new LinkedHashMap<String, String>();
		
		try {

			reader = new FileReader(propBean.getOutputLocation()+File.separator+CommonSharedConstants.INPUT_BACKUP_DIRECTORY+File.separator+FileUtil.getFileNameFromLinuxPath(propBean.getTextFilePath()));
			bufferedReader = new BufferedReader(reader);
			
//			sftp = new sftpFileConnection(sftpBean);
//			sftp.getChannelSftp();
//			bufferedReader = sftp.getInputFileBufferReader();
			columnDetailList = new ArrayList<columnBean>();
			String line;
			boolean columnFlag = true;
			while ((line = bufferedReader.readLine()) != null) {

				if (columnFlag) {
//					String[] columns = propBean.getColumnNames().split(",");
					String[] columns = CommonSharedConstants.columnNames.split(",");
//					log.info("Columns :" + columns);
					columnBean colBean = new columnBean();
					columnDetailList.add(colBean);
					for (String column : columns) {
						columnMap.put(column, "VARCHAR");
						if (column.toUpperCase().equals("REPORT")) {
							columnMap.put("REPORT_NAME", "VARCHAR");
						} else if (column.toUpperCase().equals("STATEMENT_DATE")) {
							columnMap.put("STATEMENT_DATE_DP", "VARCHAR");
							columnMap.put("IMPORT_DATE", "date");
							columnMap.put("IMPORT_DATE_DP", "VARCHAR");
//							columnMap.put("PRINTED_FLAG", "VARCHAR");
							
//							columnMapReconcile.put("RECONCILE_COUNT", "VARCHAR");
//							columnMapReconcile.put("STATEMENT_DATE", "date");
//							columnMapReconcile.put("STATEMENT_DATE_DP", "VARCHAR");
							
						}
					}

					parseDateColumns();

					columnFlag = false;
				} else {
					String[] columnValue = line.split(",");
					int index = 0;
					ArrayList<outputXmlBean> row = new ArrayList<outputXmlBean>();
					ArrayList<outputXmlBean> rowReconcile = new ArrayList<outputXmlBean>();
					String prevValue = "";
					if (columnValue.length + 3 == columnMap.size()) {
						for (Map.Entry<String, String> map : columnMap.entrySet()) {

							String columnName = map.getKey();

							if (columnName.equals("REPORT_NAME")) {
								String value = FileUtil.removeExtension(prevValue);
								outputXmlBean xmlBean = new outputXmlBean();
								xmlBean.setColumnName(columnName);
								xmlBean.setValue(value);
								row.add(xmlBean);
								continue;
							} else if (columnName.equals("STATEMENT_DATE_DP")) {
//								String value = dateConversionForDp(prevValue);
								String value = DateUtil.dateConversion(columnValue[index], "yyyy-MM-dd", "MM/dd/yyyy");
								outputXmlBean xmlBean = new outputXmlBean();
								xmlBean.setColumnName(columnName);
								xmlBean.setValue(value);
								row.add(xmlBean);
								if(reconcileColumnFlag) {
									rowReconcile.add(xmlBean);
									xmlReconcileBeans.add(rowReconcile);
									reconcileColumnFlag = false;
								}
								continue;
							} 
							
							else if(columnName.equals("IMPORT_DATE")||columnName.equals("IMPORT_DATE_DP")) {
								String outputPattern = columnName.equals("IMPORT_DATE")?"yyyy-MM-dd":"MM/dd/yyyy";
								String value = DateUtil.dateConversion(CommonSharedConstants.extractionDate, "yyyy-MM-dd HH:mm:ss", outputPattern);
								outputXmlBean xmlBean = new outputXmlBean();
								xmlBean.setColumnName(columnName);
								xmlBean.setValue(value);
								row.add(xmlBean);
							}
							
							else {
								++index;
								if (dateColumnDetails.containsKey(columnName)) {
//									columnValue[index] = dateConversion(columnValue[index]);
									columnValue[index] = DateUtil.dateConversion(columnValue[index], "yyyyMMdd",
											"yyyy-MM-dd");
									columnMap.put(columnName, "DATE");
								}
//								System.out.print(columnName + " : ");
//								System.out.print(columnValue[index] + " ");
								if (columnName.toUpperCase().equals("REPORT")) {
									prevValue = columnValue[index];
								} else if (columnName.toUpperCase().equals("STATEMENT_DATE")) {
									prevValue = columnValue[index];
									if(!countMap.containsKey(columnValue[index])) {
										countMap.put(columnValue[index], 1);
										reconcileColumnFlag = true;
									outputXmlBean xmlBean = new outputXmlBean();
									xmlBean.setColumnName(columnName);
									xmlBean.setValue(columnValue[index]);
									rowReconcile.add(xmlBean);
									}
									
									else {
										int count = countMap.get(columnValue[index]);
										countMap.put(columnValue[index], ++count);
									}
								}
								else if(columnName.toUpperCase().equals("PRINTED_FLAG")){
									String value = columnValue[index];
									if(value.equalsIgnoreCase("Y")) {
										columnValue[index] = "Yes";
									}
									else if(value.equalsIgnoreCase("N")) {
										columnValue[index] = "No";
									}
								}
								outputXmlBean xmlBean = new outputXmlBean();
								xmlBean.setColumnName(columnName);
								xmlBean.setValue(columnValue[index]);
								row.add(xmlBean);
							}
						}
						xmlBeans.add(row);
						
					}
					srcRowCount++;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append("Exception in startProcessingFile : " + commonUtils.exceptionMsgToString(e));

		} finally {
			if (reader != null)
				reader.close();
			if (bufferedReader != null)
				bufferedReader.close();
		}

		return columnMap;
	}

	private void parseDateColumns() {
		try {
//			String dateColumns[] = propBean.getDateColumns().split(",");
			String dateColumns[] = CommonSharedConstants.dateColumnFormat.split(",");
			for (String dateColumn : dateColumns) {
				String dateProperty[] = dateColumn.split(":");
				String dateColumnName = dateProperty[0];
				String datePattern = dateProperty[1];
				dateColumnDetails.put(dateColumnName, datePattern);
			}
		} catch (Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append("Exception in parseDateColumns: " + commonUtils.exceptionMsgToString(e));
		}

	}

//	private String dateConversion(String inputDate) {
//		String outDate = "";
//		try {
//			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
//			Date dateFormat = inputFormat.parse(inputDate);
//			SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
//			outDate = outputFormat.format(dateFormat);
//			// log.info("output date:"+outDate);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return outDate;
//	}
//
//	private String dateConversionForDp(String inputDate) {
//		String outDate = "";
//		try {
//			if (!inputDate.isEmpty()) {
//				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//				Date dateFormat = inputFormat.parse(inputDate);
//				SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
//				outDate = outputFormat.format(dateFormat);
//			}
//			// log.info("output date:"+outDate);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return outDate;
//	}

	private void sipCreator() throws Exception {
		StringBuffer buffer = new StringBuffer("");
		String tableName = propBean.getTableName();
		sipCreator = new SipCreator(propBean, propBean.getDatabaseName().toUpperCase(), propBean.getTableName(), propBean.getHolding(), 1);
		RecordData recordData = null;
		String fileName = null;
		for (int i = 0; i < xmlBeans.size(); i++) {
			buffer.append("  <" + tableName + "_ROW REC_ID=\"" + (i + 1) + "\">\n");

			for (int j = 0; j < xmlBeans.get(i).size(); j++) {
				outputXmlBean xmlBean = xmlBeans.get(i).get(j);
				String columnName = xmlBean.getColumnName().toUpperCase();
				String value = xmlBean.getValue();
				buffer.append("     <" + columnName + ">" + stripNonValidXMLCharacters(value) + "</" + columnName + ">\n");
//				if (columnName.equals(propBean.getBlobColumn().toUpperCase())) {
//					fileName = value;
//				}
				if (columnName.equals(CommonSharedConstants.blobColumn.toUpperCase())) {
					fileName = value;
				}
			}
			desRowCount++;
			srcBlobCount++;
			buffer.append("  </" + tableName + "_ROW>\n");
			String blobPath = fileName == null ? "" : propBean.getOutputLocation()+File.separator+CommonSharedConstants.INPUT_BACKUP_DIRECTORY + File.separator + fileName;
			File sf = new File(blobPath);
//			log.info("path:" + sf.getParent() + "value:" + fileName);
			ArrayList<String> blobsAttachment = new ArrayList<String>();

			if (sf.exists()) {
				blobsAttachment.add(sf.getAbsolutePath());
				desBlobCount++;
			}
			else {
				buffer = new StringBuffer(buffer.toString().replace("<REPORT>" + sf.getName() + "</REPORT>", ""));
				buffer = new StringBuffer(buffer.toString()
						.replace("<REPORT_NAME>" + FileUtil.removeExtension(sf.getName()) + "</REPORT_NAME>", ""));
			}
			recordData = new RecordData(buffer.toString(), blobsAttachment);
			try {
				sipCreator.getBatchAssembler().add(recordData);
			} catch (Exception e) {
				if (e.getMessage().contains("java.util.zip.ZipException: duplicate entry:")) {
					System.err.print("Skipped this file due to duplicate entry " + e.getMessage() + "\n");
					CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Skipped this file due to duplicate entry " + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
					recordData.getAttachements().clear();
					sipCreator.getBatchAssembler().add(recordData);
				} else {
					throw new Exception(e);	
				}
			}
			buffer.delete(0, buffer.length());
		}

		sipCreator.getBatchAssembler().end();
		log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  SIP package created");
		CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  SIP package created" + CommonSharedConstants.newLine);
	}
	
//	private void sipCreatorReconcile() throws Exception {
//		StringBuffer buffer = new StringBuffer("");
//		ArrayList<String> blobsAttachment = new ArrayList<String>();
//		String tableName = propBean.getTableName_reconcile();
//		sipCreator = new SipCreator(propBean, propBean.getDatabaseName().toUpperCase(), propBean.getTableName_reconcile(), propBean.getHolding_reconcile(), 1);
//		RecordData recordData = null;
//		String fileName = null;
//		for (int i = 0; i < xmlReconcileBeans.size(); i++) {
//			buffer.append("  <" + tableName + "_ROW REC_ID=\"" + (i + 1) + "\">\n");
//
//			for (int j = 0; j < xmlReconcileBeans.get(i).size(); j++) {
//				outputXmlBean xmlBean = xmlReconcileBeans.get(i).get(j);
//				String columnName = xmlBean.getColumnName().toUpperCase();
//				String value = xmlBean.getValue();
//				if(countMap.containsKey(value)) {
//					buffer.append("     <RECONCILE_COUNT>" + countMap.get(value) + "</RECONCILE_COUNT>\n");
//				}
//				buffer.append("     <" + columnName + ">" + stripNonValidXMLCharacters(value) + "</" + columnName + ">\n");
//			}
//			
//			buffer.append("  </" + tableName + "_ROW>\n");
//			
//		    recordData = new RecordData(buffer.toString(), blobsAttachment);
//
//			try {
//				sipCreator.getBatchAssembler().add(recordData);
//			} catch (Exception e) {
//				if (e.getMessage().contains("java.util.zip.ZipException: duplicate entry:")) {
//					System.err.print("Skipped this file due to duplicate entry " + e.getMessage() + "\n");
//					recordData.getAttachements().clear();
//					sipCreator.getBatchAssembler().add(recordData);
//				} else {
//					throw new Exception(e);
//				}
//
//			}
//
//			buffer.delete(0, buffer.length());
//
//		}
//
//		sipCreator.getBatchAssembler().end();
//
//	}

	protected String stripNonValidXMLCharacters(String in) {
		if (in == null || ("".equals(in)))
			return null;
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			if (XMLChar.isValid(c))
				out.append(c);
			else
				out.append(checkReplace(c));
		}
		return out.toString();
	}
	
	private String checkReplace(char key) {
		String res = "";
		switch(key) {
		case '&': 
			res = "&amp;";
			break;
		}
		return res;
	}
	
	private void startIngestionProcess() throws IOException {
		ingesterInputBean ingesterBean = new ingesterInputBean();
		ingesterBean.setAppName(propBean.getApplicationName());
		ingesterBean.setUser(propBean.getIaUser());
		ingesterBean.setDataPath(propBean.getOutputLocation()+File.separator+propBean.getDatabaseName());
		ingesterBean.setPass(propBean.getIaPassword());
		FileUtil.createDir(propBean.getOutputLocation()+File.separator+propBean.getDatabaseName()+File.separator+"INGESTION_SUCCESS");
		FileUtil.createDir(propBean.getOutputLocation()+File.separator+propBean.getDatabaseName()+File.separator+"INGESTION_FAILED");
		ingesterBean.setOutputPath(FileUtil.createDir(propBean.getOutputLocation()).getAbsolutePath());
		ingesterBean.setOutputLog(FileUtil.createDir(propBean.getOutputLocation()+File.separator+"LOG").getAbsolutePath());
		ingesterBean.setReportId(UUID.randomUUID().toString());
//		ingesterBean.setErrorLog(FileUtil.createDir(propBean.getOutputLocation()+File.separator+"ERROR").getAbsolutePath());
		ingesterBean.setIaPath(propBean.getIaPath());
		
		ingesterProcess ingester = new ingesterProcess(ingesterBean, sftpUtils, propBean.getPdfLocation(), propBean.getDateTimeForReport());
		ingester.startValidateMode();
	}

	/*
	 * private void writePdiXmlFile() throws IOException { XmlFileWriter xmlFileObj
	 * = new XmlFileWriter(propBean, "eas_pdi.xml"); xmlFileObj.writeStartTag();
	 * xmlFileObj.writeRowTag(xmlBeans); xmlFileObj.writeEndTag(); }
	 * 
	 * private void writeSipXmlFile(int recordCount) throws IOException {
	 * XmlFileWriter xmlFileObj = new XmlFileWriter(propBean, "eas_sip.xml");
	 * xmlFileObj.writeSipXmlFile(recordCount); }
	 */
	
	
}
