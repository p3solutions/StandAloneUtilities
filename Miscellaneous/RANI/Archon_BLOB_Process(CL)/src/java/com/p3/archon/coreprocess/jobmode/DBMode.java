package com.p3.archon.coreprocess.jobmode;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.apache.wink.json4j.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.p3.archon.commonfunctions.CommonFunctions;
import com.p3.archon.constants.ExtractionConstants;
import com.p3.archon.coreprocess.beans.ArchonInputBean;
import com.p3.archon.coreprocess.beans.DbListInputBean;
import com.p3.archon.dboperations.dao.ExtractionLocationDAO;
import com.p3.archon.dboperations.dao.ExtractionStatusDAO;
import com.p3.archon.dboperations.dao.JobDetailsDAO;
import com.p3.archon.dboperations.dbmodel.JobDetails;
import com.p3.archon.dboperations.dbmodel.RowId;
import com.p3.archon.dboperations.dbmodel.enums.ToolName;
import com.p3.archon.utils.FileUtil;

public class DBMode {

	private DbListInputBean dbInputArgs;
	private JSONObject inputDetails;
	private String outputPath;
	private JobDetails jd;
	private ArchonInputBean inputArgs;

	public DBMode(String[] args) {
		CommonFunctions.readUpdatePropFile();
		this.inputArgs = new ArchonInputBean();
		dbInputArgs = new DbListInputBean();
		CmdLineParser parser = new CmdLineParser(dbInputArgs);
		try {
			parser.parseArgument((String[]) Arrays.copyOfRange(args, 1, args.length));
		} catch (CmdLineException e) {
			parser.printUsage(System.err);
			System.err.println("Please check arguments specified. \n" + e.getMessage() + "\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(1);
		}
	}

	public void startValidateMode() throws Exception {
		JobDetailsDAO cdao = new JobDetailsDAO();

		RowId rowId = new RowId();
		rowId.setJobAttempt(dbInputArgs.jobAttempt);
		rowId.setJobId(dbInputArgs.jobId);
		rowId.setRunId(dbInputArgs.runId);
		jd = cdao.getJobRecord(rowId);
		if (jd == null) {
			System.err.println("Please check jobid specified. \nNo such record exist in Database.\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(11);
		}

		inputArgs.setRowId(rowId);
		boolean result = new ExtractionStatusDAO().isRecordExists(jd.getRowId());
		if (result) {
			outputPath = new ExtractionLocationDAO().getOutputLoc(jd.getRowId());
			FileUtil.checkCreateDirectory(outputPath);
		} else
			outputPath = CommonFunctions.checkCreateOutputSaveLocation(jd);

		inputDetails = new JSONObject(jd.getInputDetails());

		inputArgs.setDatabaseServer(inputDetails.getString(ExtractionConstants.DATABASESERVER.getValue()));
		inputArgs.setHost(inputDetails.getString(ExtractionConstants.HOST.getValue()));
		inputArgs.setDatabase(inputDetails.getString(ExtractionConstants.DATABASE.getValue()));
		inputArgs.setPort(inputDetails.getString(ExtractionConstants.PORT.getValue()));
		inputArgs.setSchema(inputDetails.getString(ExtractionConstants.SCHEMA.getValue()));
		inputArgs.setUser(inputDetails.getString(ExtractionConstants.USERNAME.getValue()));
		inputArgs.setPass(inputDetails.getString(ExtractionConstants.PASSWORD.getValue()));
		inputArgs.setEnc(true);
		inputArgs.setOutputPath(outputPath);
		inputArgs.setToolName(jd.getToolName());
		inputArgs.setReportId(
				(jd.getRowIdString() == null || jd.getRowIdString().isEmpty()) ? UUID.randomUUID().toString()
						: jd.getRowIdString());

		if (jd.getToolName().equals(ToolName.TOOL_BLOB_EXTRACTOR_NAME)) {
			inputArgs.setBlobIdentifier(inputDetails.getString(ExtractionConstants.BLOB_IDENTIFIER.getValue()));
			inputArgs.setRpx(inputDetails.getString(ExtractionConstants.BPF.getValue()));
			inputArgs.setUtn(inputDetails.getString(ExtractionConstants.EXTRACTION_TABLENAME.getValue()));
			inputArgs.setUbc(inputDetails.getString(ExtractionConstants.BLOB_COLUMN.getValue()));
			inputArgs.setUfo(inputDetails.getString(ExtractionConstants.FILE_NAME_OPTION.getValue()));
			if (inputDetails.containsKey(ExtractionConstants.FILE_NAME_COLUMN.getValue()))
				inputArgs.setUfc(inputDetails.getString(ExtractionConstants.FILE_NAME_COLUMN.getValue()));
			if (inputDetails.containsKey(ExtractionConstants.FILE_NAME_SEQ_START_NUMBER.getValue()))
				inputArgs.setUss(inputDetails.getString(ExtractionConstants.FILE_NAME_SEQ_START_NUMBER.getValue()));
			if (inputDetails.containsKey(ExtractionConstants.FILE_NAME_PREFIX.getValue()))
				inputArgs.setUfp(inputDetails.getString(ExtractionConstants.FILE_NAME_PREFIX.getValue()));
			inputArgs.setUeo(inputDetails.getString(ExtractionConstants.FILE_EXT_OPTION.getValue()));
			inputArgs.setUec(inputDetails.getString(ExtractionConstants.FILE_EXT_COLUMN.getValue()));
			inputArgs.setUev(inputDetails.getString(ExtractionConstants.FILE_EXT_VALUE.getValue()));
			inputArgs.setUvo(inputDetails.getString(ExtractionConstants.VERSION_INFO_OPTION.getValue()));
			inputArgs.setUic(inputDetails.getString(ExtractionConstants.ID_COLUMN.getValue()));
			inputArgs.setUsc(inputDetails.getString(ExtractionConstants.FILE_SEQ_COLUMN.getValue()));
			if (inputDetails.containsKey(ExtractionConstants.VERSION_COLUMN.getValue()))
				inputArgs.setUvc(inputDetails.getString(ExtractionConstants.VERSION_COLUMN.getValue()));
			inputArgs.setNt("true");
			inputArgs.setNv("true");
			inputArgs.setCommand(inputDetails.getString(ExtractionConstants.COMMAND.getValue()));
			inputArgs.setOutputFormat(inputDetails.getString(ExtractionConstants.OUTPUT_FILE_FORMAT.getValue()));

		}

		inputArgs.decryptor();
		inputArgs.setConstants();
		inputArgs.setAuditLevel();
		inputArgs.validateArgs();
		ManualMode manual = new ManualMode(inputArgs);
		manual.startValidateMode();
	}

}
