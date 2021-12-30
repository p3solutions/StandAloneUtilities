/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package com.p3.archon.coreprocess.executables.tools.operation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.p3.archon.coreprocess.executables.tools.base.BaseXMLFormatter;
import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.XMLFormattingHelper;
import com.p3.archon.utils.FileUtil;

/**
 * Text formatting of data.
 *
 * @author Malik
 */
final class DataFileFormatter1 extends BaseXMLFormatter implements ExecutionHandler {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
	private static final int FIXED_SIZE = 175;
	private final OperationOptions options;
	private final TextOutputFormat format;
	private final Path path;
	private long recordprocessed;
	private String fp;
	private String ext;

	/**
	 * Text formatting of data.
	 *
	 * @param operation     Options for text formatting of data
	 * @param options       Options for text formatting of data
	 * @param ext
	 * @param fp
	 * @param xSLPath
	 * @param outputOptions Options for text formatting of data
	 */
	DataFileFormatter1(final OperationOptions options, final String of, final Path path, TextOutputFormat format,
			String fp, String ext) throws Exception {
		super(null, format);
		this.options = options;
		this.format = format;
		this.path = path;
		this.fp = fp;
		this.ext = ext;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see schemacrawler.tools.traversal.DataTraversalHandler#handleData(schemacrawler.utility.Query,
	 *      java.sql.ResultSet)
	 */

	/**
	 * {@inheritDoc}
	 *
	 * @see schemacrawler.tools.traversal.DataTraversalHandler#handleData(schemacrawler.schema.Table,
	 *      java.sql.ResultSet)
	 */

	private void handleData(final String title, final ResultSet rows) throws Exception {
		System.out.println("Processing " + title);
		if (rows == null) {
			return;
		}
		try {
			final DataResultSet dataRows = new DataResultSet(rows, options.isShowLobs());
			iterateRows(dataRows, options.getBlobTableName());
		} catch (final SQLException | IOException e) {
			throw new Exception(e.getMessage(), e);
		}
	}

	private static CharSequence numberFormatter(int i) {
		return String.format("%05d", i);
	}

	private void iterateRows(DataResultSet dataRows, String tablename) throws SQLException, IOException {
		Date startDate = new Date();
		System.out.println(rightPadding(tablename, 25) + " -- : " + "\t -> ResultSet gathered. XML writing started at "
				+ dateFormat.format(startDate));

		int recordsProcessed = 0;
		int counter = 0;

		final TextOutputFormat outputFormat = format;

		String numberseq = numberFormatter(counter++).toString();
		new File(path.toString().replace("TEMP.xml", "") + "BLOBs" + File.separator
				+ checkValidFolder(options.getBlobTableName().toUpperCase()) + File.separator
				+ checkValidFolder(options.getBlobColumnName().toUpperCase()) + File.separator + "Folder-"
				+ options.getBlobFolderidentifier() + "-" + numberseq).mkdirs();

		String newPath = path.toString().replace("TEMP.xml", "").replace("TEMP.xml", "") + "tables" + File.separator
				+ (options.isVersion4() ? checkValidFile(options.getSchema().toUpperCase()) + "-" : "")
				+ checkValidFile(options.getBlobTableName().toUpperCase()) + "BLOB-"
				+ checkValidFile(options.getBlobFolderidentifier()) + "-"
				+ checkValidFile(options.getBlobColumnName().toUpperCase()) + "-" + numberseq + ".xml";
		Path pathloc = Paths.get(newPath);
		out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(pathloc.toString()), "UTF-8"), true);

		formattingHelper = new XMLFormattingHelper(out, outputFormat);

		formattingHelper.writeDocumentStart();
		if (options.isVersion4())
			formattingHelper.writeRootElementStart(options.getSchema());
		formattingHelper.writeRootElementStart(options.getBlobTableName() + "BLOB");
		formattingHelper.writeAttribute("BLOB_COLUMN", options.getBlobColumnName());
		/*
		 * formattingHelper.writeRootElementEnd(); if (options.isVersion4())
		 * formattingHelper.writeRootElementEnd(); formattingHelper.writeDocumentEnd();
		 * formattingHelper.flush();
		 */

		while (dataRows.next()) {

			if (recordsProcessed % options.getXmlChunkLimit() == 0 && recordsProcessed != 0) {
				numberseq = numberFormatter(counter++).toString();
				new File(path.toString().replace("TEMP.xml", "") + "BLOBs" + File.separator
						+ checkValidFolder(options.getBlobTableName().toUpperCase()) + File.separator
						+ checkValidFolder(options.getBlobColumnName().toUpperCase()) + File.separator + "Folder-"
						+ options.getBlobFolderidentifier() + "-" + numberseq).mkdirs();
				formattingHelper.writeRootElementEnd();
				if (options.isVersion4())
					formattingHelper.writeRootElementEnd();
				formattingHelper.writeDocumentEnd();
				out.flush();
				formattingHelper.flush();
				System.out.println(rightPadding(tablename, 25) + " -- : " + "\t --> " + recordsProcessed
						+ " record(s) processed. (Write Time Elapsed : "
						+ timeDiff(new Date().getTime() - startDate.getTime()) + ")");
				newPath = path.toString().replace("TEMP.xml", "").replace("TEMP.xml", "") + "tables" + File.separator
						+ (options.isVersion4() ? checkValidFile(options.getSchema().toUpperCase()) + "-" : "")
						+ checkValidFile(options.getBlobTableName().toUpperCase()) + "BLOB-"
						+ checkValidFile(options.getBlobFolderidentifier()) + "-"
						+ checkValidFile(options.getBlobColumnName().toUpperCase()) + "-" + numberseq + ".xml";
				out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(newPath), "UTF-8"), true);
				formattingHelper = new XMLFormattingHelper(out, outputFormat);
				formattingHelper.writeDocumentStart();
				if (options.isVersion4())
					formattingHelper.writeRootElementStart(options.getSchema());
				formattingHelper.writeRootElementStart(options.getBlobTableName() + "BLOB");
				formattingHelper.writeAttribute("BLOB_COLUMN", options.getBlobColumnName());
			}

			try {
				if (!options.getFilenameColumn().equalsIgnoreCase(""))
					processOutput(dataRows.getRows().getObject(1), dataRows.getRows().getObject(2),
							dataRows.getRows().getObject(3), dataRows.getRows().getObject(4),
							dataRows.getRows().getObject(5), recordsProcessed, tablename, numberseq, path,
							recordsProcessed % options.getXmlChunkLimit());

				else if (!options.getFilenameOverrideValue().equalsIgnoreCase(""))
					processOutput(options.getFilenameOverrideValue(), dataRows.getRows().getObject(1),
							dataRows.getRows().getObject(2), dataRows.getRows().getObject(3),
							dataRows.getRows().getObject(4), recordsProcessed, tablename, numberseq, path,
							recordsProcessed % options.getXmlChunkLimit());

				else
					processOutput(Integer.toString((options.getSeqStartValue() + recordsProcessed)),
							dataRows.getRows().getObject(1), dataRows.getRows().getObject(2),
							dataRows.getRows().getObject(3), dataRows.getRows().getObject(4), recordsProcessed,
							tablename, numberseq, path, recordsProcessed % options.getXmlChunkLimit());
				recordsProcessed++;
			} catch (Exception e) {
				e.printStackTrace();
				int seq = recordsProcessed;
				String filenamewithext = "unknown" + (++seq);

				if (options.isVersion4())
					formattingHelper.writeRecordStart("ROW", "");
				else
					formattingHelper.writeRecordStart(tablename, "-ROW");

				formattingHelper.writeElementStart("ID");
				formattingHelper.writeValue("REF_" + numberFormatterSeq(seq));
				formattingHelper.writeElementEnd();

				formattingHelper.writeElementStart("FILE");
				formattingHelper.writeValue(filenamewithext);
				formattingHelper.writeElementEnd();

				formattingHelper.writeElementStart("STATUS");
				formattingHelper.writeValue("unknown");
				formattingHelper.writeElementEnd();

				formattingHelper.writeElementStart("MESSAGE");
				formattingHelper.writeValue(e.getMessage());
				formattingHelper.writeElementEnd();

				formattingHelper.writeRecordEnd();
			}
			if (recordsProcessed % options.getXmlChunkLimit() == 0)
				System.out.println(
						rightPadding(tablename, 25) + " -- : " + "\t --> " + recordsProcessed + " File extracted.");
			formattingHelper.flush();
		}
//		if (recordsProcessed % options.getXmlChunkLimit() != 0 || recordsProcessed == 0) {
		formattingHelper.writeRootElementEnd();
		if (options.isVersion4())
			formattingHelper.writeRootElementEnd();
		formattingHelper.writeDocumentEnd();
		out.flush();
		formattingHelper.flush();
//		}

		this.setRecordprocessed(recordsProcessed);
		System.out.println(rightPadding(tablename, 25) + " -- : " + "\t Extraction Completed. Totally "
				+ recordsProcessed + " record(s) processed. (Total Write Time : "
				+ timeDiff(new Date().getTime() - startDate.getTime()) + ")");
	}

	private void processOutput(Object fileNameObj, Object extension, Object idColumn, Object seqColumn,
			Object versionColumn, int seq, String tablename, String numberseq, Path path, int iterationCounter) {

		String filenamewithext = "unknown" + (++seq);

		String fileName = "";
		String extColumn = ext;
		try {

			long startTime = new Date().getTime();

			if (fileNameObj instanceof Clob) {
				Clob aclob = (Clob) fileNameObj;
				InputStream ip = aclob.getAsciiStream();
				for (int c = ip.read(); c != -1; c = ip.read()) {
					fileName += (char) c;
				}
				ip.close();
			} else if (fileNameObj instanceof NClob) {
				NClob aclob = (NClob) fileNameObj;
				InputStream ip = aclob.getAsciiStream();
				for (int c = ip.read(); c != -1; c = ip.read()) {
					fileName += (char) c;
				}
				ip.close();
			} else {
				fileName = fileNameObj == null ? "unknown" : fileNameObj.toString().trim();
			}

			if (extension instanceof Clob) {
				Clob aclob = (Clob) extension;
				InputStream ip = aclob.getAsciiStream();
				for (int c = ip.read(); c != -1; c = ip.read()) {
					extColumn += (char) c;
				}
				ip.close();
			} else if (extension instanceof NClob) {
				NClob aclob = (NClob) extension;
				InputStream ip = aclob.getAsciiStream();
				for (int c = ip.read(); c != -1; c = ip.read()) {
					extColumn += (char) c;
				}
				ip.close();
			} else {
				extColumn = extension == null ? "" : ext;
			}

			while (fileName.toString().endsWith(".")) {
				fileName = fileName.toString().endsWith(".")
						? fileName.toString().substring(0, fileName.toString().length() - 1)
						: fileName;
			}

			while (extColumn.startsWith(".")) {
				extColumn = extColumn.startsWith(".") ? extColumn.substring(1) : extColumn;
			}

			filenamewithext = fileName.toString() + ((!extColumn.equals("")) ? "." : "") + extColumn;
			String validfilename = checkValidFile(filenamewithext);

			String file = path.toString().replace("TEMP.xml", "") + "BLOBs" + File.separator
					+ checkValidFolder(options.getBlobTableName().toUpperCase()) + File.separator
					+ checkValidFolder(options.getBlobColumnName().toUpperCase()) + File.separator + "Folder-"
					+ options.getBlobFolderidentifier() + "-" + numberseq + File.separator + validfilename;

			File f = new File(fp + File.separator + fileName + "." + ext);
			Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(file), StandardCopyOption.REPLACE_EXISTING);

			System.out.println(f.getAbsolutePath());

			boolean sizeexp = false;
			double size = 0;
			try {
				size = Math.ceil(((double) (new File(file).length()) / (double) 1024));
			} catch (Exception e) {
				sizeexp = true;
			}
			long endTime = new Date().getTime();

			if (options.isVersion4())
				formattingHelper.writeRecordStart("ROW", "");
			else
				formattingHelper.writeRecordStart(tablename, "-ROW");

			formattingHelper.writeElementStart("ID");
			formattingHelper.writeValue(fileName);
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("ORIGINAL_FILE");
			formattingHelper.writeValue(filenamewithext);
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("FILE");

			formattingHelper.writeAttribute("ref",
					"../BLOBs/" + checkValidFolder(options.getBlobTableName().toUpperCase()) + "/"
							+ checkValidFolder(options.getBlobColumnName().toUpperCase()) + "/" + "Folder-"
							+ options.getBlobFolderidentifier() + "-" + numberseq + "/" + validfilename);
			formattingHelper.writeValue(validfilename);

			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("STATUS");
			formattingHelper.writeAttribute("SIZE_KB", sizeexp ? "Could not determine" : Double.toString(size));
			formattingHelper.writeAttribute("TIME_MS", Long.toString(endTime - startTime));
			formattingHelper.writeValue("SUCCESS");
			formattingHelper.writeElementEnd();
			formattingHelper.writeRecordEnd();

		} catch (Exception e) {

			File f = new File(
					path.toString().replace("TEMP.xml", "") + File.separator + "Missing_Files_" + ext + "_type.info");
			try {
				f.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				FileUtil.writeFileAppend(f.getAbsolutePath(),
						fileName + "." + ext + " is not fount in the shared original file" + "\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (options.isVersion4())
				formattingHelper.writeRecordStart("ROW", "");
			else
				formattingHelper.writeRecordStart(tablename, "-ROW");

			formattingHelper.writeElementStart("ID");
			formattingHelper.writeValue("REF_" + numberFormatterSeq(seq));
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("ORIGINAL_FILE");
			formattingHelper.writeValue(filenamewithext);
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("FILE");
			formattingHelper.writeValue(filenamewithext);
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("STATUS");
			formattingHelper.writeValue("FAILURE");
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("MESSAGE");
			formattingHelper.writeValue(e.getMessage());
			formattingHelper.writeElementEnd();
			formattingHelper.writeRecordEnd();
		}

	}

	private String truncateName(String text) {
		if (text == null)
			return "unnamed";
		if (text.length() >= FIXED_SIZE)
			return text.substring(0, FIXED_SIZE - 1);
		return text;
	}

	private static CharSequence numberFormatterSeq(int i) {
		return String.format("%010d", i);
	}

	public static String rightPadding(String str, int num) {
		return String.format("%1$-" + num + "s", str);
	}

	public static String timeDiff(long diff) {
		int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
		String dateFormat = "";
		if (diffDays > 0) {
			dateFormat += diffDays + " day ";
		}
		diff -= diffDays * (24 * 60 * 60 * 1000);

		int diffhours = (int) (diff / (60 * 60 * 1000));
		if (diffhours > 0) {
			dateFormat += leftNumPadding(diffhours, 2) + " hour ";
		} else if (dateFormat.length() > 0) {
			dateFormat += "00 hour ";
		}
		diff -= diffhours * (60 * 60 * 1000);

		int diffmin = (int) (diff / (60 * 1000));
		if (diffmin > 0) {
			dateFormat += leftNumPadding(diffmin, 2) + " min ";
		} else if (dateFormat.length() > 0) {
			dateFormat += "00 min ";
		}

		diff -= diffmin * (60 * 1000);

		int diffsec = (int) (diff / (1000));
		if (diffsec > 0) {
			dateFormat += leftNumPadding(diffsec, 2) + " sec ";
		} else if (dateFormat.length() > 0) {
			dateFormat += "00 sec ";
		}

		int diffmsec = (int) (diff % (1000));
		dateFormat += leftNumPadding(diffmsec, 3) + " ms";
		return dateFormat;
	}

	private static String leftNumPadding(int str, int num) {
		return String.format("%0" + num + "d", str);
	}

	public static String checkValidFolder(String name) {
		if (name != null)
			return getFolderFormatted(name);
		return "unnamed";
	}

	public static String checkValidFile(String name) {
		if (name != null)
			return getFileFormatted(name);
		return "unnamed";
	}

	public static String getFolderFormatted(String string) {
		string = string.trim().replaceAll("[^_^\\p{Alnum}.]", "_").replace("^", "_").replaceAll("\\s+", "_")
				.toUpperCase();
		string = ((string.startsWith("_") && string.endsWith("_") && string.length() > 2)
				? string.substring(1).substring(0, string.length() - 2)
				: string);
		return string;
	}

	public static String getFileFormatted(String string) {
		string = string.trim().replaceAll("[^_^\\p{Alnum}.]", "_").replace("^", "_").replaceAll("\\s+", "_");
		string = ((string.startsWith("_") && string.endsWith("_") && string.length() > 2)
				? string.substring(1).substring(0, string.length() - 2)
				: string);
		return string;
	}

	public static boolean hasValue(String s) {
		return (s != null) && (s.trim().length() > 0);
	}

	public static String getFileExtension(String filename) {
		if (!hasValue(filename))
			return null;
		int index = filename.lastIndexOf('.');
		if (index == -1)
			return null;
		return filename.substring(index + 1, filename.length());
	}

	@Override
	public void handleDataMain(String title, ResultSet rows) throws Exception {
		System.out.println("handledata");
		handleData(title, rows);

	}

	@Override
	public void end() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public long getRecordprocessed() {
		return recordprocessed;

	}

	public void setRecordprocessed(long recordprocessed) {
		this.recordprocessed = recordprocessed;
	}
}
