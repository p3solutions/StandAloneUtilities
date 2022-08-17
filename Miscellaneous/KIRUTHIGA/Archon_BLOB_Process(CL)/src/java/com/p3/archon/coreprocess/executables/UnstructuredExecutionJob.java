package com.p3.archon.coreprocess.executables;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

import com.p3.archon.coreprocess.beans.ArchonInputBean;
import com.p3.archon.coreprocess.beans.FileExtInputBean;
import com.p3.archon.coreprocess.common.Constants;
import com.p3.archon.coreprocess.executables.tools.operation.ExecutionHandler;
import com.p3.archon.coreprocess.executables.tools.operation.OperationExecutable;
import com.p3.archon.coreprocess.executables.tools.operation.OperationOptions;
import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.validations.Validations;

public class UnstructuredExecutionJob extends ExecutionJob implements JobExecutable {

	private FileExtInputBean feib;
	Connection conn = null;

	public UnstructuredExecutionJob(Connection connection, ArchonInputBean inputArgs) throws Exception {
		super(connection, inputArgs);
		conn = connection;
		feib = inputArgs.checkUnstructuredInputs();
	}

	@Override
	public void start() throws Exception {

		printLines(new String[] { "Unstructred Data Extraction", "---------------------------", "" });

		boolean fileNameExtOption = feib.isFileNameExtOption();
		String fileNameOption = feib.getFileNameOption();
		String fileNamePrefixSeq = feib.getFileNamePrefixSeq();
		String fileNameColumn = feib.getFileNameColumn();
		String fileNameExtColumn = feib.getFileNameExtColumn();
		String fileNameExtValue = feib.getFileNameExtValue();
		String blobColumn = feib.getBlobColumn();
		String tableName = feib.getTableName();
		int fileNameStartSeq = feib.getFileNameStartSeq();
		boolean versionInfoOption = feib.isVersionInfoOption();
		String idColumn = feib.getIdColumn();
		String seqColumn = feib.getSeqColumn();
		String versionColumn = feib.getVersionColumn();

		OperationOptions operationOptions = new OperationOptions();

		boolean version = inputArgs.getVersion().trim().equals("4");
		operationOptions.setShowUnqualifiedNames(true);
		operationOptions.setVersion4(version);
		operationOptions.setShowLobs(true);

		if (inputArgs.getBlobIdentifier() != null)
			operationOptions.setBlobFolderidentifier(inputArgs.getBlobIdentifier());
		switch (fileNameOption.toUpperCase()) {
		case "SEQNAME":
			operationOptions.setFilenameOverride(true);
			operationOptions.setFilenameOverrideValue(fileNamePrefixSeq);
			operationOptions.setSeqStartValue(fileNameStartSeq);
			break;
		case "COLUMN":
			operationOptions.setFilename(true);
			operationOptions.setFilenameColumn(fileNameColumn);
			operationOptions.setSeqStartValue(1);
			break;
		default:
			operationOptions.setSeqFilename(true);
			operationOptions.setSeqStartValue(fileNameStartSeq);
			break;
		}

		StringBuffer sb = new StringBuffer();
		sb.append("select ");
		if (fileNameOption.toUpperCase().equalsIgnoreCase("COLUMN"))
			sb.append(operationOptions.getFilenameColumn()).append(" ,");
		if (fileNameExtOption)
			sb.append(fileNameExtColumn).append(" ,");
		else
			sb.append("'").append(fileNameExtValue).append("' ,");
		if (versionInfoOption) {
			sb.append(idColumn).append(" as \"idcolumn\"").append(" ,");
			sb.append(seqColumn).append(" ,");
			if (versionColumn == "")
				sb.append("'").append(versionColumn).append("' ,");
			else {
				sb.append(versionColumn).append(" ,");

			}

		} else {
			sb.append("'").append(idColumn).append("' ,");
			sb.append("'").append(seqColumn).append("' ,");
			sb.append("'").append(versionColumn).append("' ,");

		}
		sb.append(blobColumn);
		sb.append(" from ").append(getTableFullName(tableName));
		if (versionInfoOption) {
			sb.append(" order by ").append(idColumn).append(",").append(seqColumn);
			if (versionColumn != "") {
				sb.append(",").append(versionColumn);
			}
		}

		System.out.println(sb.toString());
		operationOptions.setShowUnqualifiedNames(true);
		operationOptions.setShowLobs(true);
		operationOptions.setXmlChunkLimit(Constants.MAX_RECORD_PER_XML_FILE);

		String outputPath = "Output";
		if (inputArgs.outputPath != null && !inputArgs.outputPath.equalsIgnoreCase(""))
			outputPath = inputArgs.outputPath;

		new File(outputPath).mkdirs();
		new File(outputPath + File.separator + "tables").mkdirs();
		new File(outputPath + File.separator + "BLOBs").mkdirs();
		new File(outputPath + File.separator + "BLOBs" + File.separator
				+ Validations.checkValidFolder(tableName.toUpperCase())).mkdirs();
		new File(outputPath + File.separator + "BLOBs" + File.separator
				+ Validations.checkValidFolder(tableName.toUpperCase()) + File.separator
				+ Validations.checkValidFolder(blobColumn.toUpperCase())).mkdirs();
		Path path = Paths.get(outputPath + File.separator + "TEMP.xml");
		new File(path.toFile().getAbsolutePath()).createNewFile();
		operationOptions.setAppendOutput(false);
		operationOptions.setSchema(Validations.getTextFormatted(inputArgs.getSchema().toUpperCase()));
		operationOptions.setBlobTableName(Validations.getTextFormatted(tableName.toUpperCase()));
		operationOptions.setBlobColumnName(Validations.getTextFormatted(blobColumn.toUpperCase()));

		OperationExecutable opterationExe = new OperationExecutable(operationOptions, "file",
				inputArgs.getOutputFormat(), path, getFormat(inputArgs.getOutputFormat()), versionInfoOption);
		System.out.println("Executing unstructed Date Extraction Job");
		try {
			ExecutionHandler handler1 = opterationExe.getHandler();
			opterationExe.executeOn("file", conn, handler1, sb.toString() + inputArgs.getAdditionalArguments(),
					inputArgs.getDatabase(), inputArgs.getSchema());
		} catch (Exception e) {
		}
		path.toFile().delete();
	}

	private String getTableFullName(String tableName) {
		switch (inputArgs.getDatabaseServer().toLowerCase()) {
		case "sql":
		case "sqlwinauth":
		case "sybase":
			return "[" + inputArgs.database + "]" + "." + "[" + inputArgs.schema + "]" + "." + "[" + tableName + "]";
		case "teradata":
		case "cache":
		case "oracle":
		case "oracleservice":
		case "mysql":
		case "maria":
		case "mariadb":
		case "db2":
		case "postgresql":
		case "mainframe-db2":
			return inputArgs.schema + "." + tableName;
		default:
			return inputArgs.schema + "." + tableName;
		}
	}

	private TextOutputFormat getFormat(String outputFormat) {
		switch (outputFormat.toLowerCase()) {
		case "html":
			return TextOutputFormat.html;
		case "json":
			return TextOutputFormat.json;
		case "csv":
			return TextOutputFormat.csv;
		case "tsv":
			return TextOutputFormat.tsv;
		case "xml":
			return TextOutputFormat.xml;
		default:
			return TextOutputFormat.text;
		}
	}

}
