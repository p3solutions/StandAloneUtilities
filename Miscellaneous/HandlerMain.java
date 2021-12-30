package com.p3.archon.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import com.p3.archon.commonutilities.PropReader;
import com.p3.archon.constants.ConfigFileConstants;
import com.p3.archon.constants.JobMemoryProps;
import com.p3.archon.constants.SharedConstants;
import com.p3.archon.constants.ToolConstants;
import com.p3.archon.dboperations.dbmodel.enums.CreationOption;
import com.p3.archon.handler.jobhandler.JobHandler;
import com.p3.archon.handler.jobkiller.Killer;
import com.p3.archon.handler.jobscheduler.JobScheduler;
import com.p3.archon.handler.purgehandler.PurgeHandler;
import com.p3.archon.handler.serverhandler.ServerHandler;
import com.p3.archon.securitymodule.SecurityModule;
import com.p3.archon.utilities.FileUtil;
import com.p3.archon.utilities.JobLogger;

public class HandlerMain {

//	public static void main(String[] args) {
//		HandlerMain handlerMain = new HandlerMain();
//		handlerMain.start();
//	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		String text = IOUtils.toString(new FileReader("C:\\Users\\91735\\Downloads\\config\\config.JobHandler.acf"));
		String[] line = SecurityModule
				.perfromDecrypt(CreationOption.JOB_HANDLER.toString(), ToolConstants.SEC_DEFAULT_KEY, text)
				.split(ToolConstants.NEW_LINE);
		ConfigFileConstants.setServer(line[0]);
		ConfigFileConstants.setPort(line[1]);
		ConfigFileConstants.setDatabase(line[2]);
		ConfigFileConstants.setUserName(line[3]);
		ConfigFileConstants.setPassword(line[4]);
		ConfigFileConstants.setUseSSL(line[5]);
		ConfigFileConstants.setMaxThread(Integer.valueOf(line[6]));
		ConfigFileConstants.setThreadSleepTime(Integer.valueOf(line[7]) * 1000);
		ConfigFileConstants.setJobRunType(line[8]);
		ConfigFileConstants.setOutputPath(line[9]);
		if (SharedConstants.OUTPUT_BASE_PATH.equals(""))
			SharedConstants.OUTPUT_BASE_PATH = ConfigFileConstants.getOutputPath();

		System.out.println(line[0]);
		System.out.println(line[1]);
		System.out.println(line[2]);
		System.out.println(line[3]);
		System.out.println(line[4]);
		System.out.println(line[5]);
		System.out.println(line[6]);
		System.out.println(line[7]);
		System.out.println(line[8]);
		System.out.println(line[9]);


//		Writer propWriter = new OutputStreamWriter(
//				new FileOutputStream("C:\\Users\\91735\\Downloads\\config\\config.JobHandler.acf"));
//		String text1 = line[0] + ToolConstants.NEW_LINE + line[1] + ToolConstants.NEW_LINE + line[2]
//				+ ToolConstants.NEW_LINE + line[3] + ToolConstants.NEW_LINE
//				+ line[4] + ToolConstants.NEW_LINE + line[5]
//				+ ToolConstants.NEW_LINE + 11 + ToolConstants.NEW_LINE + line[7] + ToolConstants.NEW_LINE + line[8]
//				+ ToolConstants.NEW_LINE + line[9];
//		String enc = SecurityModule.perfromEncrypt(CreationOption.JOB_HANDLER.toString(), ToolConstants.SEC_DEFAULT_KEY,
//				text1);
//		propWriter.write(enc);
//		propWriter.flush();
//		propWriter.close();

	}

	public void start() {
		try {
			PropReader p = new PropReader(ToolConstants.PROP_FILE);
			SharedConstants.DOT_CMD = p.getStringValue("DOT_PATH", "dot");
			SharedConstants.ARCHON_INSTALLATION_PATH = p.getStringValue("ARCHON_PATH", "");
			SharedConstants.INFOARCHIVE_INSTALLATION_PATH = p.getStringValue("INFOARCHIVE_PATH", "");
			SharedConstants.IA_VERSION = p.getStringValue("IA_VERSION", "");
			SharedConstants.IA_VERSION = SharedConstants.IA_VERSION.trim();
			SharedConstants.AUDITOR_PATH = p.getStringValue("AUDITOR_PATH", "");
			SharedConstants.OUTPUT_BASE_PATH = p.getStringValue("OUTPUT_BASE_PATH", "");
			SharedConstants.RUN_ORACLE9I_WITH_SC = p.getBooleanValue("RUN_ORACLE9I_WITH_SC", false);

			SharedConstants.XFE_RESPONSE_COPY_LOC = p.getStringValue("XFE_RESPONSE_COPY_LOC",
					SharedConstants.OUTPUT_BASE_PATH + File.separator + "XFE_RESPONSE_NOTIFICATIONS");
			SharedConstants.XFE_RESPONSE_FILE_PREFIX = p.getStringValue("XFE_RESPONSE_FILE_PREFIX", "ArchiveResponse_");

			SharedConstants.XFE_RESPONSE_COPY_LOC = SharedConstants.XFE_RESPONSE_COPY_LOC.endsWith(File.separator)
					? SharedConstants.XFE_RESPONSE_COPY_LOC.substring(0,
							SharedConstants.XFE_RESPONSE_COPY_LOC.length() - 1)
					: SharedConstants.XFE_RESPONSE_COPY_LOC;
			try {
				FileUtil.checkCreateDirectory(SharedConstants.XFE_RESPONSE_COPY_LOC);
			} catch (Exception e) {
				if (!new File(SharedConstants.XFE_RESPONSE_COPY_LOC).mkdirs())
					SharedConstants.XFE_RESPONSE_COPY_LOC = "";
				JobLogger.getLogger().error(getClass().getName(), "Start",
						"issue in setting up 'XFE_RESPONSE_COPY_LOC'. Error : " + e.getMessage());
			}

			readUpdateTempPathPropFile();
			readUpdateJavaPathPropFile();

			ToolConstants.update();
			JobLogger.getLogger().info(getClass().getName(), "Start", "Initiating property reader");
			initialize();
			getBaseConfig();
			JobLogger.getLogger().info(getClass().getName(), "Start", "Initialized");
		} catch (NumberFormatException | IOException e) {
			JobLogger.getLogger().error(getClass().getName(), "Start",
					"Initiating property reader error. " + e.getMessage());
			// e.printStackTrace();
		}

		JobMemoryProps.readMemorySettings();
		begin();
	}

	public static void readUpdateJavaPathPropFile() {
		try {
			PropReader p = new PropReader(ToolConstants.PROP_FILE);
			String javaPath = p.getStringValue("JAVA_PATH");
			if (javaPath.equals("")) {
				throw new Exception("JAVA_PATH property is unavailable or undefined.");
			} else
				SharedConstants.JAVA_PATH = "\"" + p.getStringValue("JAVA_PATH") + "\"";
		} catch (Exception e) {
			System.out.println("Setting up default JAVA_HOME location");
			SharedConstants.JAVA_PATH = "java";
			// e.printStackTrace();
		}
	}

	public void readUpdateTempPathPropFile() {
		try {
			PropReader p = new PropReader(ToolConstants.PROP_FILE);
			File file = null;
			SharedConstants.TEMP_PATH = p.getStringValue("TEMP_PATH");
			if (SharedConstants.TEMP_PATH.equals("")) {
				throw new Exception("TEMP_PATH property is unavailable or undefined.");
			}
			file = new File(SharedConstants.TEMP_PATH);
			file.mkdirs();
			if (!file.exists())
				throw new Exception("Unable to create dir in temp path mentioned in variable.properties.");

			File checkfile = new File(file.getAbsolutePath() + File.separator + "tempFile" + new Date().getTime());
			checkfile.createNewFile();
			if (checkfile != null && checkfile.isFile())
				checkfile.delete();
			SharedConstants.TEMP_PATH = file.getAbsolutePath();
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Setting up default TEMP location");
			SharedConstants.TEMP_PATH = System.getProperty("java.io.tmpdir");
		}
		if (SharedConstants.TEMP_PATH == null || SharedConstants.TEMP_PATH.equals("")) {
			SharedConstants.TEMP_PATH = new File("").getAbsolutePath();
		}

		JobLogger.getLogger().info(getClass().getName(), "Start", "TEMP_PATH set up at " + SharedConstants.TEMP_PATH);
	}

	private void begin() {

		JobLogger.getLogger().info(getClass().getName(), "spinning job", "Purge Handler");
		PurgeHandler p = new PurgeHandler();
		p.start();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		JobLogger.getLogger().info(getClass().getName(), "spinning job", "Scheduler");
		JobScheduler l = new JobScheduler();
		l.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		JobLogger.getLogger().info(getClass().getName(), "spinning job", "Canceller");
		Killer k = new Killer();
		k.start();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		JobLogger.getLogger().info(getClass().getName(), "spinning job", "Job Handler");
		JobHandler j = new JobHandler();
		j.start();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		JobLogger.getLogger().info(getClass().getName(), "spinning job", "Server Handler");
		ServerHandler s = new ServerHandler();
		s.start();

		try {
			l.join();
			k.join();
			j.join();
			s.join();
			p.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initialize() throws NumberFormatException, IOException {
		String text = IOUtils.toString(new FileReader(ToolConstants.JOB_HANDLER_ACF_FILE));
		String[] line = SecurityModule
				.perfromDecrypt(CreationOption.JOB_HANDLER.toString(), ToolConstants.SEC_DEFAULT_KEY, text)
				.split(ToolConstants.NEW_LINE);
		ConfigFileConstants.setServer(line[0]);
		ConfigFileConstants.setPort(line[1]);
		ConfigFileConstants.setDatabase(line[2]);
		ConfigFileConstants.setUserName(line[3]);
		ConfigFileConstants.setPassword(line[4]);
		ConfigFileConstants.setUseSSL(line[5]);
		ConfigFileConstants.setMaxThread(Integer.valueOf(line[6]));
		ConfigFileConstants.setThreadSleepTime(Integer.valueOf(line[7]) * 1000);
		ConfigFileConstants.setJobRunType(line[8]);
		ConfigFileConstants.setOutputPath(line[9]);
		if (SharedConstants.OUTPUT_BASE_PATH.equals(""))
			SharedConstants.OUTPUT_BASE_PATH = ConfigFileConstants.getOutputPath();
		JobLogger.getLogger().info(getClass().getName(), "Initialize", "Initialization successfull.");
	}

	private void getBaseConfig() throws IOException {
		String text = IOUtils.toString(new FileReader(ToolConstants.ARCHON_ACF_FILE));
		String[] line = SecurityModule
				.perfromDecrypt(CreationOption.ARCHON.toString(), ToolConstants.SEC_DEFAULT_KEY, text)
				.split(ToolConstants.NEW_LINE);
		ConfigFileConstants.setBaseServer(line[6]);
		JobLogger.getLogger().info(getClass().getName(), "Initialize", "Initialization successfull.");
	}

//	public static void main(String[] args) throws FileNotFoundException, IOException {
//		String text = IOUtils.toString(new FileReader("/Users/malik/Downloads/New Folder With Items 2/config.JobHandler.acf"));
//		System.out.println(SecurityModule
//				.perfromDecrypt(CreationOption.JOB_HANDLER.toString(), ToolConstants.SEC_DEFAULT_KEY, text));
//	}
}
