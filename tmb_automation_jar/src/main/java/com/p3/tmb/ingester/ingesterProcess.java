package com.p3.tmb.ingester;

import com.p3.tmb.commonUtils.CommandLineProcess;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.commonUtils.OSIdentifier;
import com.p3.tmb.commonUtils.commonUtils;
import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.ingester.beans.ingesterInputBean;
import com.p3.tmb.ingester.process.IngestFileHandler;
import com.p3.tmb.ingester.process.Text2Pdf;
import com.p3.tmb.ingester.process.scriptMaker;
import com.p3.tmb.sftp.sftpUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;


public class ingesterProcess {

//	public static void main(String[] args) {
		
//		args = new String[]{ "-s", "", "-meta", "false", "-db", "", "-a", "FLEET CARD", "-u", "sue@iacustomer.com", 
//				"-dp", "D:\\CodingRelated\\FleetCard\\AutomationJar\\output\\DB", "-p", "opCSoOptzHIaF/HpRn3LlA==", 
//				"-sip", "true","-ol","D:\\CodingRelated\\FleetCard\\AutomationJar\\outputIngReport",
//				"-op","D:\\CodingRelated\\FleetCard\\AutomationJar\\outputIngReport","-tn","Ingest Data",
//				"-uuid","1234567890","-e","true","-el","D:\\CodingRelated\\FleetCard\\AutomationJar\\outputIngReport",
//				"-i","D:\\CodingRelated\\Software\\EP20.4\\infoarchive"};
//		ingesterInputBean ingesterBean = new ingesterInputBean();
//		ingesterBean.setAppName("FLEET CARD");
//		ingesterBean.setUser("sue@iacustomer.com");
//		ingesterBean.setDataPath("D:\\CodingRelated\\FleetCard\\checkingJava\\output\\26022022191432\\DB1");
//		ingesterBean.setPass("password");
//		ingesterBean.setOutputPath("D:\\CodingRelated\\FleetCard\\checkingJava\\output\\26022022191432");
//		ingesterBean.setOutputLog("D:\\CodingRelated\\FleetCard\\checkingJava\\outputIngReport");
//		ingesterBean.setReportId("1234567890");
//		ingesterBean.setErrorLog("D:\\CodingRelated\\FleetCard\\checkingJava\\outputIngReport");
//		ingesterBean.setIaPath("D:\\CodingRelated\\Software\\EP20.4\\infoarchive");
//		
//		ingesterProcess ingester = new ingesterProcess(ingesterBean);
//		ingester.startValidateMode();
//	}
	
	
	
	
	private ingesterInputBean ingesterInputArgs;
	private sftpUtils sftpUtils = null;
	private String pdfPath = null;
	private String reportDateTime = null;
	
	public ingesterProcess(ingesterInputBean inputArgs, sftpUtils sftpUtils, String pdfPath, String reportDateTime) {
		this.ingesterInputArgs = inputArgs;
		ingesterInputArgs.decryptor();
		this.sftpUtils = sftpUtils;
		this.pdfPath = pdfPath;
		this.reportDateTime = reportDateTime;
	}

	public void startValidateMode() {
		String ingestionStatus;
		String destReport = null;
		try {
			String scriptPath = FileUtil.createTempFolder() + "ingest_"
					+ ingesterInputArgs.getReportId() + ".check";
			String scriptRunnerPath = FileUtil.createTempFolder() + "scriptRunner_"
					+ ingesterInputArgs.getReportId() + (OSIdentifier.checkOS() ? ".bat" : ".sh");
			System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  "+ scriptPath);
			System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  "+ scriptRunnerPath);
			scriptMaker sm = new scriptMaker(ingesterInputArgs);
			sm.createScript(scriptPath);
			sm.createScriptRunner(scriptRunnerPath, scriptPath);

			CommandLineProcess clp = new CommandLineProcess(ingesterInputArgs);
			String[] cmdArray = OSIdentifier.checkOS() ? new String[] { scriptRunnerPath }
					: new String[] { "sh", scriptRunnerPath };
			clp.run(cmdArray, ingesterInputArgs.appName);

			Text2Pdf t2p = new Text2Pdf();
			destReport = ingesterInputArgs.outputPath + File.separator + "Ingestion_report(" + ingesterInputArgs.appName + ")_"
					+ ingesterInputArgs.getReportId().substring(0, 8) + "_"+reportDateTime + ".pdf";
			File file = new File(destReport);
			file.getParentFile().mkdirs();
			t2p.createPdf(destReport, clp.outputLogFile, ingesterInputArgs.appName, ingesterInputArgs.reportId);

			// FileUtil.deleteFile(clp.outputLogFile);
			FileUtil.deleteFile(scriptRunnerPath);
			FileUtil.deleteFile(scriptPath);
			ingestionStatus = checkLog(clp.outputlog);
			boolean failStatus = false;
			if (ingestionStatus.startsWith("<p>Ingestion Completed Successfully.</p>")) {
				System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Ingestion report generated at " + destReport);
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Ingestion report generated at " + destReport + CommonSharedConstants.newLine);
				System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Ingestion completed");
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Ingestion completed " + CommonSharedConstants.newLine);
			} else {
				System.out.println( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + ":  Ingestion Failed " + ingestionStatus);
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Ingestion Failed " +ingestionStatus + CommonSharedConstants.newLine);
				System.err.println(ingestionStatus);
		
				failStatus = true;
				// throw new Exception("Ingestion Failed " + ingestionStatus);
			}
			IngestFileHandler ingestHandler = new IngestFileHandler(ingesterInputArgs.getDataPath());

			try {
				//if (ingesterInputArgs.getToolName().equals(ToolName.TOOL_XML_FILE_EXTRACTOR_NAME.getValue())) {
//					ingestHandler.startIngestionArchival();
//					if ((CommonSharedConstants.IA_VERSION.equals("16EP6")
//							|| CommonSharedConstants.IA_VERSION.equals("16EP7")
//							|| CommonSharedConstants.IA_VERSION.equals("20.2")
//							|| CommonSharedConstants.IA_VERSION.equals("20.4")
//							|| CommonSharedConstants.IA_VERSION.equals("21.2")
//							|| CommonSharedConstants.IA_VERSION.equals("21.4")) && ingesterInputArgs.isSip())
//						ingestHandler.IngestedSuccessFileRename();
//				} else {

					if (CommonSharedConstants.IA_VERSION.equals("16EP6")
							|| CommonSharedConstants.IA_VERSION.equals("16EP7")
							|| CommonSharedConstants.IA_VERSION.equals("20.2")
							|| CommonSharedConstants.IA_VERSION.equals("20.4")
							|| CommonSharedConstants.IA_VERSION.equals("21.2")
							|| CommonSharedConstants.IA_VERSION.equals("22.2"))
						
					{
						ingestHandler.IngestedSuccessFileRename();
						ingestHandler.deleteEmptyIngDir();
					}
				//}

			} catch (Exception e) {
				e.printStackTrace();
				CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			}

			if (failStatus) {
				throw new Exception("Ingestion Failed " + ingestionStatus);
			}

		} 
		catch (Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
//			System.exit(1);
		}
	finally{
		if(destReport != null) {
			sftpUtils.uploadFile(destReport, pdfPath);
		}
	}
	}

	public String checkLog(String outputLog) {
		if (outputLog == null || !new File(outputLog).exists())
			return "Unknown Error";
		int count = 0;
		String returnMsg = "";
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(outputLog);
			br = new BufferedReader(fr);
			String line;
			boolean start = false;
			while ((line = br.readLine()) != null) {
				if (!start && line.contains("Ingestion Report Process Log"))
					start = true;
				if (start) {
					if (line.trim().startsWith("Ingested") || line.trim().startsWith("Received")
							|| line.trim().contains("Ingested file")) // ingested with
						// attachement
						count++;
					if (line.contains("Ingest took") && returnMsg.isEmpty())
						returnMsg = "<p>Ingestion Completed Successfully.</p><p>Ingested " + count + " files.</p><p>"
								+ (line.replace("Ingest took", "Total ingestion time : ") + "</p>");
					else if (line.contains("Sorry, password you entered is incorrect, try again."))
						returnMsg = "Credentials are invalid";
					else if (line.contains("Please check application name."))
						returnMsg = "Incorrect Application name, Please check application name.";
					else if (line.contains("Cannot find files to be ingested")
							|| line.contains("There are no SIP files found on provided path"))
						returnMsg = "Either Schema name provide for ingestion is incorrect or no xml/sip file found with provided schema to ingest.";
					else if (line.contains("The filename, directory name, or volume label syntax is incorrect.")
							|| line.contains("You have specified unknown directory"))
						returnMsg = "Incorrect Application name / Schema Name / wrong directory path..(Application Name and Schema Name are case sensitive)";
					else if (line.contains("Connection refused"))
						returnMsg = "Connection to Infoarchive server refused.";
					else if (line.contains("ERROR - Cannot upload the file:"))
						returnMsg = "Invalid Ingestion file";
					else if (line.contains("ERROR - Command failed: connect --u"))
						returnMsg = "Cannot decrypt a value. Either the configuration or the value is wrong";
				}
			}
			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			try {
				br.close();
				fr.close();
				returnMsg = returnMsg + "\n\n" + e.getMessage();
			} catch (IOException e1) {
				e1.printStackTrace();
				CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e1) + CommonSharedConstants.newLine);
				returnMsg = returnMsg + "\n\n" + e1.getMessage();
			}
		}
		returnMsg = returnMsg.isEmpty() ? (count == 0 ? "Unknown error" : "<p>Ingestion Completed Successfully.</p>")
				: returnMsg;
		return returnMsg;
	}
}
