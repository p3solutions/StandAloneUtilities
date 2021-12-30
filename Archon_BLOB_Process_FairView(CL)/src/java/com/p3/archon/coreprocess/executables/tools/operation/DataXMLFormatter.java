package com.p3.archon.coreprocess.executables.tools.operation;

import static com.p3.archon.coreprocess.executables.tools.utility.common.Utility.getTextFormatted;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.p3.archon.coreprocess.executables.tools.base.BaseXMLFormatter;
import com.p3.archon.coreprocess.executables.tools.operation.blobbean.BlobInfo;
import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.BinaryData;
import com.p3.archon.coreprocess.executables.tools.utility.TextFormattingHelper;
import com.p3.archon.coreprocess.executables.tools.utility.XMLFormattingHelper;
import com.p3.archon.utils.FileUtil;

import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;

@SuppressWarnings("unused")
final class DataXMLFormatter extends BaseXMLFormatter implements ExecutionHandler {

	private static final String OPERATION_COUNT = "count";
	private static final String OPERATION_COUNT_EXT = "count-ext";

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
	private static final long THRESHOLD = 1 * 1024 * 1024;
	private final String command;

	private final TextOutputFormat format;
	private final String saveloc;
	private final String encoding;
	private final boolean version;
	private final long xmlChunkLimit;
	private final boolean isShowLobs;
	private final boolean isSplitDate;
	private final String schema;
	private long recordprocessed;
	private boolean isZip = false;
	private String timeformat;
	private TreeMap<String, Long> srcColumnBlobCount;
	private TreeMap<String, Long> destColumnBlobCount;
	private LinkedHashMap<String, Object[]> datatype;
	private long startTimeRec;

	DataXMLFormatter(final String schema, final String command, TextOutputFormat format, PrintWriter out,
			String saveloc, String encoding, boolean version, long xmlChunkLimit, boolean isShowLobs,
			boolean isSplitDate, boolean isZip, String timeformat, TreeMap<String, Long> srcColumnBlobCount,
			TreeMap<String, Long> destColumnBlobCount, LinkedHashMap<String, Object[]> datatype) throws Exception {
		super(out, format);
		this.schema = schema;
		this.saveloc = saveloc;
		this.command = command;
		this.format = format;
		this.encoding = encoding;
		this.version = version;
		this.xmlChunkLimit = xmlChunkLimit;
		this.isShowLobs = isShowLobs;
		this.isSplitDate = isSplitDate;
		this.setRecordprocessed(0);
		this.isZip = isZip;
		this.timeformat = timeformat;
		this.srcColumnBlobCount = srcColumnBlobCount;
		this.destColumnBlobCount = destColumnBlobCount;
		this.datatype = datatype;
	}

	@Override
	public void handleDataMain(final String title, final ResultSet rows) throws Exception {
		handleData(title, rows);
	}

	private long handleAggregateOperationForTable(final String title, final ResultSet results) throws Exception {
		try {
			long aggregate = 0;
			if (results.next()) {
				aggregate = results.getLong(1);
				recordprocessed = aggregate;
			}
			return aggregate;
		} catch (final SQLException e) {
			throw new Exception("Could not obtain aggregate data", e);
		}
	}

	private void handleData(final String title, final ResultSet rows) throws Exception {
		if (rows == null) {
			return;
		}
		if (command.toLowerCase().equals(OPERATION_COUNT_EXT)) {
			handleAggregateOperationForTable(title, rows);
		} else if (command.toLowerCase().equals(OPERATION_COUNT)) {
			final long aggregate = handleAggregateOperationForTable(title, rows);
			formattingHelper.writeRecordStart("COUNT", "-ROW");
			formattingHelper.writeElementStart("ORIG_TABLE_NAME");
			formattingHelper.writeValue(title);
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("TABLE_NAME");
			formattingHelper.writeValue(getTextFormatted(title.toUpperCase()));
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("COUNT");
			formattingHelper.writeValue(Long.toString(aggregate));
			formattingHelper.writeElementEnd();
			formattingHelper.writeRecordEnd();
			formattingHelper.flushOnly();
		} else {
			try {
				final DataResultSet dataRows = new DataResultSet(rows, isShowLobs);
				dataRows.computeColumnInfo(datatype);
				iterateRows(dataRows, dataRows.getColumnName(), dataRows.getColumnType(), dataRows.getColumnTypeJava(),
						title);
			} catch (final Exception e) {
				throw new Exception(e.getMessage(), e);
			}
		}
	}

	private static CharSequence numberFormatter(int i) {
		return String.format("%05d", i);
	}

	private static CharSequence numberFormatterSeq(int i) {
		return String.format("%010d", i);
	}

	private void iterateRows(DataResultSet dataRows, String[] columns, String[] columnsType, int[] columnsTypeJava,
			String tablename) throws SQLException, IOException {
		// int computeCheckLimit = computeCheckLimit(columns.length);
		FileChannel f = null;
		long lastrun = new Date().getTime();
		int recordsProcessed = 0;
		Date startDate = new Date();
		// List<String> queueColumnNameholder = new LinkedList<String>();
		// List<String> queueValueholder = new LinkedList<String>();
		recordsProcessed = 0;
		int counter = 0;
		String processingFile = null;
		try {
			System.out.println(rightPadding(tablename, 25) + " -- : "
					+ "\t -> ResultSet gathered. XML writing started at " + dateFormat.format(startDate));
			final TextOutputFormat outputFormat = TextOutputFormat.xml;
			processingFile = saveloc + (version ? getTextFormatted(schema.toUpperCase()) + "-" : "")
					+ getTextFormatted(tablename.toUpperCase()) + "-" + numberFormatter(counter++) + ".xml";
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(processingFile), encoding), true);
			formattingHelper = new XMLFormattingHelper(out, outputFormat);

			formattingHelper.writeDocumentStart();
			if (version)
				formattingHelper.writeRootElementStart(schema);
			formattingHelper.writeRootElementStart(tablename);
			f = FileChannel.open(Paths.get(processingFile), StandardOpenOption.READ);
			while (dataRows.next()) {
				// if (recordsProcessed % computeCheckLimit != 0) {
				//
				// }
				// else
				out.flush();
				if (f.size() >= (xmlChunkLimit - THRESHOLD)) {
					formattingHelper.writeRootElementEnd();
					if (version)
						formattingHelper.writeRootElementEnd();
					formattingHelper.writeDocumentEnd();
					out.flush();
					formattingHelper.flush();
					formattingHelper.flushOutput();
					if (f != null && f.isOpen())
						f.close();
					if (out != null)
						out.close();
					zip(processingFile);
					System.out.println(rightPadding(tablename, 25) + " -- : " + "\t --> " + recordsProcessed
							+ " record(s) processed. (Write Time Elapsed : "
							+ timeDiff(new Date().getTime() - startDate.getTime()) + "). XML chunk sealed : "
							+ (version ? getTextFormatted(schema.toUpperCase()) + "-" : "")
							+ getTextFormatted(tablename.toUpperCase()) + "-" + numberFormatter(counter) + ".xml");
					processingFile = saveloc + (version ? getTextFormatted(schema.toUpperCase()) + "-" : "")
							+ getTextFormatted(tablename.toUpperCase()) + "-" + numberFormatter(counter++) + ".xml";
					out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(processingFile), encoding), true);
					formattingHelper = new XMLFormattingHelper(out, outputFormat);
					formattingHelper.writeDocumentStart();
					if (version)
						formattingHelper.writeRootElementStart(schema);
					formattingHelper.writeRootElementStart(tablename);
					f = FileChannel.open(Paths.get(processingFile), StandardOpenOption.READ);
				}

				final List<Object> currentRow = dataRows.row();
				final Object[] columnData = currentRow.toArray(new Object[currentRow.size()]);
				if (version)
					formattingHelper.writeRecordStart("ROW", "");
				else
					formattingHelper.writeRecordStart(tablename, "-ROW");
				formattingHelper.writeAttribute("EXT_ROW_ID", Long.toString(recordsProcessed + 1));
				// queueValueholder.clear();
				for (int i = 0; i < columnData.length; i++) {
					// System.out.print(tablename + " <-->" + recordsProcessed + " ---> type : " +
					// columnsTypeJava[i]
					// + " -- " + columnsType[i] + " ->->-> ");
					//
					// if (columnData[i] != null)
					// System.out.println(columnData[i]);
					// else
					// System.out.println("null");
					if (columnData[i] != null) {
						startTimeRec = getCurrentTime();
						formattingHelper.writeElementStart((String) columns[i]);

						if (columnData[i] == null)
							formattingHelper.writeAttribute("null", "true");
						else if (columnsTypeJava[i] == 0) {
							formattingHelper.writeValue(columnData[i] == null ? "" : columnData[i] + "");
						} else if (columnsTypeJava[i] == 3) {
							formattingHelper.writeValue(columnData[i] == null ? "" : columnData[i].toString() + "");
						} else if (isShowLobs && columnsTypeJava[i] > 0) {
							try {
								if (columnsTypeJava[i] == 1) {
									formattingHelper.writeAttribute("type", "CLOB");
									formattingHelper
											.writeCData(columnData[i] == null ? null : columnData[i].toString());
								} else if (columnsTypeJava[i] == 2) {
									formattingHelper.writeAttribute("type", "BLOB");
									if (!srcColumnBlobCount.containsKey(columns[i])) {
										srcColumnBlobCount.put(columns[i], (long) 0);
										destColumnBlobCount.put(columns[i], (long) 0);
									}
									if (columnData[i] != null) {
										srcColumnBlobCount.put(columns[i], srcColumnBlobCount.get(columns[i]) + 1);
										BlobInfo blobinfo = getBlobInfo(tablename, columns[i], columnData[i],
												recordsProcessed, saveloc);
										if (blobinfo.getPath() != null)
											blobinfo.setPath(blobinfo.getPath()
													.replace(saveloc
															+ (saveloc.endsWith(File.separator) ? "" : File.separator),
															"")
													.replace("\\", "/"));
										formattingHelper.writeAttribute("ref", blobinfo.getPath());
										formattingHelper.writeAttribute("size", blobinfo.getSize());
										formattingHelper.writeAttribute("status", blobinfo.getStatus());
										formattingHelper.writeValue(blobinfo.getName());
									} else
										formattingHelper.writeValue("");
								} else
									formattingHelper.writeValue(columnData[i] == null ? "" : columnData[i] + "");
							} catch (NumberFormatException e) {
								formattingHelper.writeValue(columnData[i] == null ? "" : columnData[i] + "");
							}
						}
						formattingHelper.writeElementEnd();

						// if (isSplitDate && columnData[i] != null) {
						// if (isValidDate(columnData[i].toString())) {
						// placeDate(formattingHelper, columns[i], columnData[i].toString());
						// } else if (isValidDateText(columnData[i].toString())) {
						// placeDate(formattingHelper, columns[i], columnData[i].toString());
						// }
						// }
						//
						// if (isSplitDate) {
						// if (columnData[i] == null) {
						// queueValueholder.clear();
						// queueColumnNameholder.clear();
						// } else if (columnData[i].toString().length() > 4) {
						// queueValueholder.clear();
						// queueColumnNameholder.clear();
						// } else {
						// queueValueholder.add(columnData[i].toString());
						// queueColumnNameholder.add(columns[i]);
						// if (queueValueholder.size() == 3) {
						// boolean status = checkforDate(queueValueholder, queueColumnNameholder,
						// formattingHelper);
						// if (status) {
						// queueValueholder.clear();
						// queueColumnNameholder.clear();
						// } else {
						// queueValueholder.remove(0);
						// queueColumnNameholder.remove(0);
						// }
						// }
						// }
						// }
					}
				}
				formattingHelper.writeRecordEnd();
				recordsProcessed++;
				if (new Date().getTime() - lastrun >= 60000) {
					lastrun = new Date().getTime();
					System.out.println(rightPadding(tablename, 25) + " -- : " + "\t --> " + recordsProcessed
							+ " record(s) processed. (Write Time Elapsed : "
							+ timeDiff(new Date().getTime() - startDate.getTime())
							+ ") --> log generated by timed count printer");
				}
			}

			formattingHelper.writeRootElementEnd();
			if (version)
				formattingHelper.writeRootElementEnd();
			formattingHelper.writeDocumentEnd();
			out.flush();
			formattingHelper.flush();
			if (f != null && f.isOpen())
				f.close();
			if (out != null)
				out.close();
			System.out.println(rightPadding(tablename, 25) + " -- : " + "\t Extraction Completed. Totally "
					+ recordsProcessed + " record(s) processed. (Total Write Time : "
					+ timeDiff(new Date().getTime() - startDate.getTime()) + "). XML chunk sealed : "
					+ (version ? getTextFormatted(schema.toUpperCase()) + "-" : "")
					+ getTextFormatted(tablename.toUpperCase()) + "-" + numberFormatter(counter) + ".xml");
			zip(processingFile);
			this.setRecordprocessed(recordsProcessed);
		} catch (SQLException |

				IOException e) {
			System.out.println("count " + recordsProcessed);
			System.out.println(e);
			e.printStackTrace();
			if (e instanceof SQLException) {
				if (version)
					formattingHelper.writeRootElementEnd();
				formattingHelper.writeDocumentEnd();
				out.flush();
				formattingHelper.flush();
				formattingHelper.flushOutput();
				System.out.println(rightPadding(tablename, 25) + " -- : " + "\t Extraction Failed. Totally "
						+ recordsProcessed + " record(s) processed. (Total Write Time : "
						+ timeDiff(new Date().getTime() - startDate.getTime()) + "). XML chunk sealed : "
						+ (version ? getTextFormatted(schema.toUpperCase()) + "-" : "")
						+ getTextFormatted(tablename.toUpperCase()) + "-" + numberFormatter(counter) + ".xml");
			}
			this.setRecordprocessed(recordsProcessed);
			try {
				if (f != null && f.isOpen())
					f.close();
				if (out != null)
					out.close();
			} catch (Exception e1) {

			}
			throw e;
		}
	}

	private int computeCheckLimit(int length) {
		if (length >= 1000)
			return 500;
		if (length >= 750)
			return 1000;
		if (length >= 500)
			return 2000;
		if (length >= 250)
			return 3000;
		if (length >= 200)
			return 5000;
		if (length >= 100)
			return 10000;
		if (length >= 50)
			return 15000;
		return 20000;
	}

	private void zip(String fileName) throws FileNotFoundException, IOException {
		if (isZip) {
			File file = new File(fileName);
			FileOutputStream fos = new FileOutputStream(file.getParent() + File.separator + file.getName() + ".zip");
			ZipOutputStream zos = new ZipOutputStream(fos);
			System.out.println("Writing '" + file.getName() + "' to zip file");
			FileInputStream fis = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
			zos.flush();
			zos.closeEntry();
			fis.close();
			zos.flush();
			zos.close();
			fos.flush();
			fos.close();
			FileUtil.deleteFile(fileName);
		}
	}

	private void placeDate(TextFormattingHelper formattingHelper, String columns, String columnData) {
		if (columnData != null && columnData.length() == 8) {
			String part1 = columnData.substring(0, 4) + "-" + columnData.substring(4, 6) + "-"
					+ columnData.substring(6, 8);
			formattingHelper.writeElementStart(columns + "_DT_SPLIT");
			formattingHelper.writeAttribute("createdBy", "DL");
			formattingHelper.writeValue(part1 == null ? "" : part1 + "");
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart(columns + "_DTM_SPLIT");
			formattingHelper.writeAttribute("createdBy", "DL");
			formattingHelper.writeValue(part1 + "T00:00:00");
			formattingHelper.writeElementEnd();
		} else if (columnData != null && columnData.length() == 10) {
			String part1 = columnData;
			formattingHelper.writeElementStart(columns + "_DT_SPLIT");
			formattingHelper.writeAttribute("createdBy", "DL");
			formattingHelper.writeValue(part1 == null ? "" : part1 + "");
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart(columns + "_DTM_SPLIT");
			formattingHelper.writeAttribute("createdBy", "DL");
			formattingHelper.writeValue(part1 + "T00:00:00");
			formattingHelper.writeElementEnd();
		}
	}

	public static boolean isValidDateText(String inDate) {
		if (inDate == null)
			return false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		if (inDate.startsWith("19") || inDate.startsWith("20"))
			return true;
		else
			return false;
	}

	public static boolean isValidDate(String inDate) {
		if (inDate == null)
			return false;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		if (inDate.startsWith("19") || inDate.startsWith("20"))
			return true;
		else
			return false;
	}

	private long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static String longestCommonSubstrings(String s, String t) {
		int[][] table = new int[s.length()][t.length()];
		int longest = 0;
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < s.length(); i++) {
			for (int j = 0; j < t.length(); j++) {
				if (s.charAt(i) != t.charAt(j)) {
					continue;
				}
				table[i][j] = (i == 0 || j == 0) ? 1 : 1 + table[i - 1][j - 1];
				if (table[i][j] > longest) {
					longest = table[i][j];
					result.clear();
				}
				if (table[i][j] == longest) {
					result.add(s.substring(i - longest + 1, i + 1));
				}
			}
		}
		return result.get(0);
	}

	private boolean checkforDate(List<String> queueValueholder, List<String> queueColumnNameholder,
			TextFormattingHelper formattingHelper) {
		SimilarityStrategy strategy = new JaroWinklerStrategy();
		StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
		double score1 = service.score(queueColumnNameholder.get(0), queueColumnNameholder.get(1));
		double score2 = service.score(queueColumnNameholder.get(0), queueColumnNameholder.get(2));
		if (score1 == score2) {
			double score3 = service.score(queueColumnNameholder.get(1), queueColumnNameholder.get(2));
			if (score2 == score3) {
				if (queueValueholder.get(0).length() == 4
						&& (queueValueholder.get(1).length() == 2 || queueValueholder.get(1).length() == 1)
						&& (queueValueholder.get(2).length() == 2 || queueValueholder.get(2).length() == 1)) {
					String text = queueValueholder.get(0)
							+ (queueValueholder.get(1).length() == 2 ? queueValueholder.get(1)
									: "0" + queueValueholder.get(1))
							+ (queueValueholder.get(2).length() == 2 ? queueValueholder.get(2)
									: "0" + queueValueholder.get(2));
					if (isValidDate(text)) {
						String title = longestCommonSubstrings(queueColumnNameholder.get(0),
								queueColumnNameholder.get(1));
						if (title != null && !title.equals("")) {
							title = title + "_YMD_DT";
							placeDate(formattingHelper, title, text);
						}
					}
				}

				else if (queueValueholder.get(2).length() == 4
						&& (queueValueholder.get(0).length() == 2 || queueValueholder.get(0).length() == 1)
						&& (queueValueholder.get(1).length() == 2 || queueValueholder.get(1).length() == 1)) {
					String text = queueValueholder.get(2)
							+ (queueValueholder.get(0).length() == 2 ? queueValueholder.get(0)
									: "0" + queueValueholder.get(0))
							+ (queueValueholder.get(1).length() == 2 ? queueValueholder.get(1)
									: "0" + queueValueholder.get(1));
					if (isValidDate(text)) {
						String title = longestCommonSubstrings(queueColumnNameholder.get(0),
								queueColumnNameholder.get(1));
						if (title != null && !title.equals("")) {
							title = title + "_YMD_DT";
							placeDate(formattingHelper, title, text);
						}
					}
				}
			}
		}
		return false;
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

	public static String rightPadding(String str, int num) {
		return String.format("%1$-" + num + "s", str);
	}

	@Override
	public void end() throws Exception {
	}

	@Override
	public long getRecordprocessed() {
		return recordprocessed;
	}

	public void setRecordprocessed(long recordprocessed) {
		this.recordprocessed = recordprocessed;
	}

	private BlobInfo getBlobInfo(String tablename, String columnname, Object blob, int recordsProcessed,
			String outputlocation) {
		BlobInfo blobInfo = new BlobInfo();

		String validfilename = UUID.randomUUID().toString().substring(0, 14) + new Date().getTime();
		String folder = outputlocation + (outputlocation.endsWith(File.separator) ? "" : File.separator) + "BLOBs"
				+ File.separator + checkValidFolder(tablename.toUpperCase()) + File.separator
				+ checkValidFolder(columnname.toUpperCase()) + File.separator + "Folder-"
				+ (((recordsProcessed / 250) * 250) + 1) + "-" + (((recordsProcessed / 250) * 250) + 250)
				+ File.separator;

		new File(folder).mkdirs();
		String file = folder + validfilename;

		try {
			BinaryData data = ((BinaryData) blob);

			// InputStream is = new
			// ByteArrayInputStream(data.toString().getBytes(Charset.forName("UTF-8")));
			// MimeTypeDetector detector = new MimeTypeDetector();
			// String mimetype = detector.detectMimeType(file, is);
			// String type = MimeTypeMapping.mimeTypeToExtension(mimetype);
			// file = file + type;

			InputStream in = data.getBlob().getBinaryStream();
			OutputStream out = new FileOutputStream(file);
			byte[] buff = new byte[1024];
			int len = 0;

			while ((len = in.read(buff)) != -1) {
				out.write(buff, 0, len);
			}

			out.flush();
			out.close();
			in.close();

			blobInfo.setPath(file);
			blobInfo.setName(new File(file).getName());
			blobInfo.setStatus("Success");
			try {
				blobInfo.setSize(Math.ceil(((double) (new File(file).length()) / (double) 1024)) + " KB");
			} catch (Exception e) {
				blobInfo.setSize("NA");
			}
			destColumnBlobCount.put(columnname, destColumnBlobCount.get(columnname) + 1);
		} catch (Exception e) {
			blobInfo.setPath("");
			blobInfo.setName(new File(file).getName());
			blobInfo.setStatus("Failure");
			blobInfo.setSize("NA");
			e.printStackTrace();
		}
		return blobInfo;
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
}
