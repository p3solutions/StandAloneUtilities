package com.p3.tmb.constant;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonSharedConstants {

	public static final SimpleDateFormat CCYYMMDD_FORMATTER = new SimpleDateFormat("yyyyMMdd");
	public static final String AOP_CONSTANT = "OUTPUT_BASE_PATH";
	public static final String IA_VERSION_KEY = "IA_VERSION";
	public static final String ARCHON_JOB = ".AJ";
	public static final String ARCHON = "ARCHON";
	public static final String BIN_DIR = "bin";
	public static final String ARCHON_JOBS_FOLDER = "jobs";
	public static final String INPUT_BACKUP_DIRECTORY = "TEMP";

	public static String IA_VERSION = "21.2";
	public static String OUTPUT_BASE_PATH = "";
	public static String PROP_FILE = ".." + File.separator + "config" + File.separator + "variables.properties";
	public static String ARCHON_INSTALLATION_PATH = "";
	public static String INFOARCHIVE_INSTALLATION_PATH = "";
	public static int srcRowCount;
	public static int desRowCount;
	public static int srcBlobCount;
	public static int desBlobCount;
	
	public static final String FIXED_FOLDER = "fixed";
	public static final String LINUX_PATH_SEPERATOR = "/";
	public static final String hiphen = "-";
	public static final String folderProp = "folder.properties";
	public static final String readyForJobFalse = "readyForJob=false";
	public static final String readyForJobTrue = "readyForJob=true";
	public static final String blobColumn = "report";
	public static final String dateColumnFormat = "statement_date:yyyyMMdd";
	public static final String columnNames = "report,account_number,statement_date,printed_flag";
	public static String extractionDate = null;
	public static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM");
	public static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd HH");
	public static final SimpleDateFormat sdf5 = new SimpleDateFormat("HH");
	public static boolean flag25Cycle = false;
	public static boolean flagMonthend = false;
	public static int extendHoursCount;
	public static boolean extendHoursFlag = false;
	public static final int waitingHours = 12;
	public static final int threadSleep = 1000*60*15;
	public static Date extendDate = null;
	
	public static String cycle25Date = "";
	public static String monthEndDate = "";
	
	public static String jobStartDateTime;
	public static String jobEndDateTime;

	public static boolean jobStatus;
	public static String jobScheduledDate = null;

	public static long jobStartTime;
	public static long jobEndTime;
	public static long jobProcessTime;
	public static String FONT_FILE_PATH = "/FreeSans.ttf";
	public static String REPORT_ID = "";
	
	public static StringBuffer logContent = new StringBuffer();
	public static String newLine = System.lineSeparator();

	public static final String PREFIX_SAC = "sac";
	public static final String PREFIX_DOCUMENTUM = "dcm";
	public static final String PREFIX_UDE = "ude";
	public static final String PREFIX_SHAREPOINT = "sp";
	public static final String PREFIX_RDBMS = "rms";
	public static final String PREFIX_METALYZER = "ma";
	public static final String PREFIX_LOTUS_NOTES = "ln";
	public static final String PREFIX_APP_AUTOMATER = "aa";
	public static final String PREFIX_FILESHARE = "fs";
	public static final String PREFIX_ERT_ANALYSIS = "ea";
	public static final String PREFIX_AS400 = "as4";
	public static final String PREFIX_DC = "dc";
	public static final String PREFIX_ERT = "ert";
	public static final String SESSION_ID = "System_gen_";
	public static final String PREFIX_AS400_METALYZER = "as400_ma";
	public static final String PREFIX_AS400DC = "as400_dc";
	public static final String PREFIX_APP_CHAIN_OF_CUSTODY = "coc";
	public static final String PREFIX_XML_FILE_EXTRACTOR = "xf";
	public static final String PREFIX_APP_INGESTER = "ing";
	public static final String PREFIX_PEOPLESOFT = "pa";
	public static final String PREFIX_JDE = "jde";
	public static final String PREFIX_FLAT_FILE_GENERATOR = "ffg";
	public static final String PREFIX_CAA = "caa";
	
	public static final String TEMP_QUERY_FILE = "tempQuery.sql";
	public static String DOT_CMD = "dot"; // s"/opt/local/bin/dot";
	public static boolean TTF_CHECK = false;
	public static String TEMP_PATH = "";
	public static String JAVA_PATH = "java";

}

