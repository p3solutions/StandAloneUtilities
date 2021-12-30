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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.p3.archon.coreprocess.executables.tools.base.BaseXMLFormatter;
import com.p3.archon.coreprocess.executables.tools.options.TextOutputFormat;
import com.p3.archon.coreprocess.executables.tools.utility.XMLFormattingHelper;

/**
 * Text formatting of data.
 *
 * @author Malik
 */
final class DataFileFormatter extends BaseXMLFormatter implements ExecutionHandler {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
	private static final int FIXED_SIZE = 175;
	private final OperationOptions options;
	private final TextOutputFormat format;
	private final Path path;
	private final String xSLPath;
	private long recordprocessed;

	/**
	 * Text formatting of data.
	 *
	 * @param operation     Options for text formatting of data
	 * @param options       Options for text formatting of data
	 * @param xSLPath
	 * @param outputOptions Options for text formatting of data
	 */
	DataFileFormatter(final OperationOptions options, final String of, final Path path, TextOutputFormat format,
			String xSLPath) throws Exception {
		super(null, format);
		this.options = options;
		this.format = format;
		this.path = path;
		this.xSLPath = xSLPath;
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
							dataRows.getRows().getBytes(3), recordsProcessed, tablename, numberseq, path,
							recordsProcessed % options.getXmlChunkLimit());

				else if (!options.getFilenameOverrideValue().equalsIgnoreCase(""))
					processOutput(options.getFilenameOverrideValue(), dataRows.getRows().getObject(1),
							dataRows.getRows().getBytes(2), recordsProcessed, tablename, numberseq, path,
							recordsProcessed % options.getXmlChunkLimit());

				else
					processOutput(Integer.toString((options.getSeqStartValue() + recordsProcessed)),
							dataRows.getRows().getObject(1), dataRows.getRows().getBytes(2), recordsProcessed,
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

	private void processOutput(Object fileNameObj, Object extension, byte[] bs, int seq, String tablename,
			String numberseq, Path path, int iterationCounter) {

		String filenamewithext = "unknown" + (++seq);
		if (bs == null) {

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
			formattingHelper.writeValue("unknown");
			formattingHelper.writeElementEnd();

			formattingHelper.writeElementStart("MESSAGE");
			formattingHelper.writeValue("Content was NULL");
			formattingHelper.writeElementEnd();

			formattingHelper.writeRecordEnd();
			return;
		}
		String fileName = "";
		String extColumn = "";
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
				extColumn = extension == null ? "" : extension.toString().trim();
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

			String getExt = getFileExtension(validfilename);
			if (getExt == null)
				validfilename = validfilename + "_" + String.format("%06d", iterationCounter);
			else
				validfilename = truncateName(validfilename.substring(0, validfilename.lastIndexOf(getExt) - 1)) + "_"
						+ String.format("%06d", iterationCounter) + "." + getExt;

			String file = path.toString().replace("TEMP.xml", "") + "BLOBs" + File.separator
					+ checkValidFolder(options.getBlobTableName().toUpperCase()) + File.separator
					+ checkValidFolder(options.getBlobColumnName().toUpperCase()) + File.separator + "Folder-"
					+ options.getBlobFolderidentifier() + "-" + numberseq + File.separator + validfilename;

			OutputStream out = new FileOutputStream(file);
			out.write(bs);
			out.flush();
			out.close();
			System.out.println(file);

			String convertedFile = "";
			if (xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_MainSummarySmartText")) {

				boolean ReviewCommentsFlag = false;

				StringBuffer reviewComments = new StringBuffer();

				boolean ObjectValueFlag = false;
				boolean TextValueFlag = false;
				boolean CommentValueFlag = false;

				boolean LinkedItemsFlag = false;
				boolean LinkFlag = false;
				boolean DescriptionFlag = false;
				boolean ShortcutFlag = false;
				boolean TypeFlag = false;

				boolean CodeFlag = false;
				boolean CodingSystemFlag = false;
				boolean ValueFlag = false;
				boolean DisplayTextFlag = false;

				boolean CPFlag = false;
				boolean AvailableItemsFlag = false;
				boolean StrengthItemFlag = false;
				boolean FormToCodeMapListFlag = false;
				boolean FormToCodeMapFlag = false;

				boolean ItemNotesFlag = false;

				int hdTitleCount = 0;

				String CodingSystem = "";
				String Value = "";
				String DisplayText = "";

				String Shortcut = "";
				String Description = "";
				String Type = "";

				Writer fileWriter = null;
				XMLEventReader eventReader = null;
				FileReader fr = null;
				try {
					XMLInputFactory factory = XMLInputFactory.newInstance();
					fr = new FileReader(file);
					eventReader = factory.createXMLEventReader(fr);
					convertedFile = file.concat(".txt");
					fileWriter = new FileWriter(new File(convertedFile));
					while (eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();
						switch (event.getEventType()) {
						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String startValue = startElement.getName().getLocalPart();
							switch (startValue) {
							case "ReviewComments":
								ReviewCommentsFlag = true;
								break;
							case "HD":
								hdTitleCount++;
								fileWriter.write("\n");
								fileWriter.write((hdTitleCount > 1 ? "\t" : "")
										+ startElement.getAttributeByName(new QName("Title")).getValue());
								break;
							case "ObjectValue":
								ObjectValueFlag = true;
								break;
							case "TextValue":
								TextValueFlag = true;
								break;
							case "CommentValue":
								CommentValueFlag = true;
								break;
							case "Shortcut":
								ShortcutFlag = true;
								break;
							case "Description":
								DescriptionFlag = true;
								break;
							case "Type":
								TypeFlag = true;
								break;
							case "LinkedItems":
								LinkedItemsFlag = true;
								break;
							case "Link":
								LinkFlag = true;
								break;
							case "Code":
								CodeFlag = true;
								break;
							case "CodingSystem":
								CodingSystemFlag = true;
								break;
							case "Value":
								ValueFlag = true;
								break;
							case "DisplayText":
								DisplayTextFlag = true;
								break;
							case "AvailableItems":
								AvailableItemsFlag = true;
								break;
							case "CP":
								CPFlag = true;
								break;
							case "StrengthItem":
								StrengthItemFlag = true;
								break;
							case "SIG":
								try {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Description "
											+ startElement.getAttributeByName(new QName("Description")).getValue());

								} catch (Exception e) {
									// TODO: handle exception
								}
								break;
							case "FormToCodeMapList":
								FormToCodeMapListFlag = true;
								break;
							case "FormToCodeMap":
								FormToCodeMapFlag = true;
								break;
							case "UnitInfo":
								try {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "UnitInfo "
											+ startElement.getAttributeByName(new QName("UnitID")).getValue() + " "
											+ startElement.getAttributeByName(new QName("Abbr")).getValue());

								} catch (Exception e) {
									// TODO: handle exception
								}
								break;
							case "ItemNotes":
								ItemNotesFlag = true;
								break;

							}
							break;
						case XMLStreamConstants.CHARACTERS:
							Characters characters = event.asCharacters();
							String text = StringUtils.normalizeSpace(characters.getData().trim());
							if (text != null && !text.isEmpty()) {
								if (ReviewCommentsFlag)
									reviewComments.append(text);
								if (ObjectValueFlag && (TextValueFlag || CommentValueFlag)) {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
								}
								if (LinkedItemsFlag && LinkFlag) {
									if (ShortcutFlag)
										Shortcut = text;
									if (DescriptionFlag)
										Description = text;
									if (TypeFlag)
										Type = text;
								}
								if (ObjectValueFlag && CodeFlag) {
									if (CodingSystemFlag)
										CodingSystem = text;
									if (ValueFlag)
										Value = text;
									if (DisplayTextFlag)
										DisplayText = text;
								}
								if (CPFlag) {
									if (AvailableItemsFlag) {
										if (CodeFlag) {
											if (CodingSystemFlag)
												CodingSystem = text;
											if (ValueFlag)
												Value = text;
											if (DisplayTextFlag)
												DisplayText = text;
										}
									}
									if (StrengthItemFlag) {
										if (CodingSystemFlag)
											CodingSystem = text;
										if (ValueFlag)
											Value = text;
										if (DisplayTextFlag)
											DisplayText = text;

									}
									if (FormToCodeMapListFlag && FormToCodeMapFlag) {
										if (CodeFlag) {
											if (CodingSystemFlag)
												CodingSystem = text;
											if (ValueFlag)
												Value = text;
											if (DisplayTextFlag)
												DisplayText = text;
										}
									}
								}
								if (ItemNotesFlag) {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Value " + text);

								}
								break;
							}
							break;
						case XMLStreamConstants.END_ELEMENT:
							EndElement endElement = event.asEndElement();
							String endValue = endElement.getName().getLocalPart();
							switch (endValue) {
							case "ReviewComments":
								if (reviewComments.toString()
										.startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>")) {

									boolean reviewCommentsObjectValueFlag = false;
									boolean reviewCommentsTextValueFlag = false;
									XMLEventReader reviewCommentsEventReader = null;
									try {
										XMLInputFactory reviewCommentsFactory = XMLInputFactory.newInstance();
										reviewCommentsEventReader = reviewCommentsFactory
												.createXMLEventReader(new StringReader(reviewComments.toString()));
										while (reviewCommentsEventReader.hasNext()) {
											XMLEvent reviewCommentsEvent = reviewCommentsEventReader.nextEvent();
											switch (reviewCommentsEvent.getEventType()) {
											case XMLStreamConstants.START_ELEMENT:
												StartElement reviewCommentsEventStartElement = reviewCommentsEvent
														.asStartElement();
												String reviewCommentsEventStartValue = reviewCommentsEventStartElement
														.getName().getLocalPart();
												switch (reviewCommentsEventStartValue) {
												case "ObjectValue":
													reviewCommentsObjectValueFlag = true;
													break;
												case "TextValue":
													reviewCommentsTextValueFlag = true;
													break;

												}
												break;
											case XMLStreamConstants.CHARACTERS:
												Characters reviewCommentsCharacters = reviewCommentsEvent
														.asCharacters();
												String reviewCommentsText = StringUtils
														.normalizeSpace(reviewCommentsCharacters.getData().trim());
												if (reviewCommentsText != null && !reviewCommentsText.isEmpty()) {
													if (reviewCommentsObjectValueFlag && reviewCommentsTextValueFlag) {
														fileWriter.write("Review Comments :" + reviewCommentsText);
														fileWriter.write("\n");
													}

												}

												break;
											case XMLStreamConstants.END_ELEMENT:
												EndElement reviewCommentsCharactersEndElement = reviewCommentsEvent
														.asEndElement();
												String reviewCommentsCharactersEndValue = reviewCommentsCharactersEndElement
														.getName().getLocalPart();
												switch (reviewCommentsCharactersEndValue) {
												case "ObjectValue":
													reviewCommentsObjectValueFlag = false;
													break;
												case "TextValue":
													reviewCommentsTextValueFlag = false;
													break;
												}
												break;

											}
										}
									}

									catch (XMLStreamException e) {
										e.printStackTrace();
									} finally {
										try {
											eventReader.close();
										} catch (Exception e) {
											e.printStackTrace();
											System.err.println(e.getMessage());
										}
									}
								} else {
									fileWriter.write("Review Comments :" + reviewComments.toString());
									fileWriter.write("\n");

								}
								ReviewCommentsFlag = false;
								break;
							case "ObjectValue":
								ObjectValueFlag = false;
								break;
							case "TextValue":
								TextValueFlag = false;
								break;
							case "CommentValue":
								CommentValueFlag = false;
								break;
							case "Shortcut":
								ShortcutFlag = false;
								break;
							case "Description":
								DescriptionFlag = false;
								break;
							case "Type":
								TypeFlag = false;
								break;
							case "LinkedItems":
								LinkedItemsFlag = false;
								break;
							case "Link":
								fileWriter.write("\n");
								fileWriter.write(
										(hdTitleCount > 1 ? "\t\t" : "\t") + Shortcut + " " + Description + " " + Type);
								Shortcut = "";
								Description = "";
								Type = "";
								LinkFlag = false;
								break;
							case "Code":
								fileWriter.write("\n");
								fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + CodingSystem + " " + Value + " "
										+ DisplayText);
								CodingSystem = "";
								Value = "";
								DisplayText = "";
								CodeFlag = false;
								break;
							case "CodingSystem":
								CodingSystemFlag = false;
								break;
							case "Value":
								ValueFlag = false;
								break;
							case "DisplayText":
								DisplayTextFlag = false;
								break;
							case "AvailableItems":
								AvailableItemsFlag = false;
								break;
							case "CP":
								CPFlag = false;
								break;
							case "StrengthItem":
								StrengthItemFlag = false;
								break;
							case "FormToCodeMapList":
								FormToCodeMapListFlag = false;
								break;
							case "FormToCodeMap":
								FormToCodeMapFlag = false;
								break;
							case "ItemNotes":
								ItemNotesFlag = false;
								break;
							case "STI_SummaryActiveProblemsField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryInactiveProblemsField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummarySurgeriesField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryMedicationsField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryAllergiesField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryFamilyHistoryField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryTobaccoField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryAlcoholField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryInterventionsField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummarySocialHistoryField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryROSField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_SummaryPhysicalField":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							}

							break;

						}
					}
				}

				catch (

				FileNotFoundException e) {
					e.printStackTrace();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				} finally {
					try {
						fileWriter.flush();
						fileWriter.close();
						eventReader.close();
						fr.close();
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println(e.getMessage());
					}
				}
				FileUtils.forceDelete(new File(file));

			} else if (xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_SmartText")) {

				boolean ReviewCommentsFlag = false;
				StringBuffer reviewComments = new StringBuffer();

				boolean STI_VitalSignsFlag = false;
				boolean ObjectValueFlag = false;
				boolean TextValueFlag = false;
				boolean CPFlag = false;
				boolean VitalGroupFlag = false;
				boolean ReadingsFlag = false;
				boolean PulseFlag = false;
				boolean RespirationRateFlag = false;
				boolean CentimetersFlag = false;
				boolean KilogramsFlag = false;
				boolean CodeFlag = false;
				boolean CodingSystemFlag = false;
				boolean ValueFlag = false;
				boolean DisplayTextFlag = false;
				boolean AvailableItemsFlag = false;
				boolean StrengthItemFlag = false;
				boolean FormToCodeMapListFlag = false;
				boolean FormToCodeMapFlag = false;
				boolean LinkedItemsFlag = false;
				boolean LinkFlag = false;
				boolean DescriptionFlag = false;
				boolean ShortcutFlag = false;
				boolean TypeFlag = false;

				boolean ItemNotesFlag = false;

				boolean ValuesFlag = false;
				boolean ValueItemFlag = false;

				String Shortcut = "";
				String Description = "";
				String Type = "";

				String CodingSystem = "";
				String Value = "";
				String DisplayText = "";

				int hdTitleCount = 0;

				XMLEventReader eventReader = null;
				Writer fileWriter = null;
				FileReader fr = null;

				try {
					XMLInputFactory factory = XMLInputFactory.newInstance();
					fr = new FileReader(file);
					eventReader = factory.createXMLEventReader(new FileReader(file));
					convertedFile = file.concat(".txt");
					fileWriter = new FileWriter(new File(convertedFile));

					while (eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();
						switch (event.getEventType()) {
						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String startValue = startElement.getName().getLocalPart();
							switch (startValue) {
							case "ReviewComments":
								ReviewCommentsFlag = true;
								break;
							case "HD":
								hdTitleCount++;
								fileWriter.write("\n");
								fileWriter.write((hdTitleCount > 1 ? "\t" : "")
										+ startElement.getAttributeByName(new QName("Title")).getValue());
								break;
							case "STI_VitalSigns":
								STI_VitalSignsFlag = true;
								break;
							case "VitalGroup":
								VitalGroupFlag = true;
								break;
							case "Readings":
								ReadingsFlag = true;
								break;
							case "VitalReading":
								if (startElement.getAttributeByName(new QName("ItemType")).getValue()
										.equalsIgnoreCase("SOAPware_VitalReading_BloodPressure")) {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "BloodPressure");
								}
								if (startElement.getAttributeByName(new QName("ItemType")).getValue()
										.equalsIgnoreCase("SOAPware_VitalReading_BloodPressure_Sitting_Brachial")) {
									fileWriter.write("\n");
									fileWriter.write(
											(hdTitleCount > 1 ? "\t\t" : "\t") + "BloodPressure_Sitting_Brachial");
								}
								if (startElement.getAttributeByName(new QName("ItemType")).getValue()
										.equalsIgnoreCase("SOAPware_VitalReading_BloodPressure_Lying_Brachial")) {
									fileWriter.write("\n");
									fileWriter
											.write((hdTitleCount > 1 ? "\t\t" : "\t") + "BloodPressure_Lying_Brachial");
								}
								if (startElement.getAttributeByName(new QName("ItemType")).getValue()
										.equalsIgnoreCase("SOAPware_VitalReading_BloodPressure_Standing_Radial")) {
									fileWriter.write("\n");
									fileWriter.write(
											(hdTitleCount > 1 ? "\t\t" : "\t") + "BloodPressure_Standing_Radial");
								}
								break;
							case "Values":
								ValuesFlag = true;
								break;
							case "ValueItem":
								ValueItemFlag = true;
								try {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Systolic "
											+ startElement.getAttributeByName(new QName("Systolic")).getValue());
								} catch (Exception e) {
									// TODO: handle exception
								}
								try {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Diastolic "
											+ startElement.getAttributeByName(new QName("Diastolic")).getValue());
								} catch (Exception e) {
									// TODO: handle exception
								}
								break;
							case "Pulse":
								PulseFlag = true;
								break;
							case "RespirationRate":
								RespirationRateFlag = true;
								break;
							case "Centimeters":
								CentimetersFlag = true;
								break;
							case "Kilograms":
								KilogramsFlag = true;
								break;
							case "CP":
								CPFlag = true;
								break;
							case "ObjectValue":
								ObjectValueFlag = true;
								break;
							case "TextValue":
								TextValueFlag = true;
								break;
							case "Code":
								CodeFlag = true;
								break;
							case "CodingSystem":
								CodingSystemFlag = true;
								break;
							case "Value":
								ValueFlag = true;
								break;
							case "DisplayText":
								DisplayTextFlag = true;
								break;
							case "AvailableItems":
								AvailableItemsFlag = true;
								break;
							case "StrengthItem":
								StrengthItemFlag = true;
								break;
							case "FormToCodeMapList":
								FormToCodeMapListFlag = true;
								break;
							case "FormToCodeMap":
								FormToCodeMapFlag = true;
								break;
							case "Shortcut":
								ShortcutFlag = true;
								break;
							case "Description":
								DescriptionFlag = true;
								break;
							case "Type":
								TypeFlag = true;
								break;
							case "UnitInfo":
								try {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "UnitInfo "
											+ startElement.getAttributeByName(new QName("UnitID")).getValue() + " "
											+ startElement.getAttributeByName(new QName("Abbr")).getValue());

								} catch (Exception e) {
									// TODO: handle exception
								}
								break;
							case "LinkedItems":
								LinkedItemsFlag = true;
								break;
							case "Link":
								LinkFlag = true;
								break;
							case "ItemNotes":
								ItemNotesFlag = true;
								break;

							case "SIG":
								try {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Description "
											+ startElement.getAttributeByName(new QName("Description")).getValue());

								} catch (Exception e) {
									// TODO: handle exception
								}
								break;
							default:
								break;

							}
							break;
						case XMLStreamConstants.CHARACTERS:
							Characters characters = event.asCharacters();
							String text = StringUtils.normalizeSpace(characters.getData().trim());
							;

							if (text != null && !text.isEmpty()) {
								if (ReviewCommentsFlag) {
									reviewComments.append(text);
									break;
								}

								if (TextValueFlag && ObjectValueFlag) {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);

								}

								if (ObjectValueFlag && CodeFlag) {
									if (CodingSystemFlag)
										CodingSystem = text;
									if (ValueFlag)
										Value = text;
									if (DisplayTextFlag)
										DisplayText = text;
								}

								if (STI_VitalSignsFlag) {
									if (CPFlag && ReadingsFlag && VitalGroupFlag && ValuesFlag && ValueItemFlag) {
										if (PulseFlag)
											fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Pulse " + text);
										if (RespirationRateFlag)
											fileWriter.write(
													(hdTitleCount > 1 ? "\t\t" : "\t") + "RespirationRate " + text);
										if (CentimetersFlag)
											fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Height " + text);
										if (KilogramsFlag)
											fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Weight " + text);
									}
								}
								if (LinkedItemsFlag && LinkFlag) {
									if (ShortcutFlag)
										Shortcut = text;
									if (DescriptionFlag)
										Description = text;
									if (TypeFlag)
										Type = text;
								}

								if (ItemNotesFlag) {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Value " + text);

								}

								if (CPFlag) {
									if (AvailableItemsFlag) {
										if (CodeFlag) {
											if (CodingSystemFlag)
												CodingSystem = text;
											if (ValueFlag)
												Value = text;
											if (DisplayTextFlag)
												DisplayText = text;
										}
									}
									if (StrengthItemFlag) {
										if (CodingSystemFlag)
											CodingSystem = text;
										if (ValueFlag)
											Value = text;
										if (DisplayTextFlag)
											DisplayText = text;

									}
									if (FormToCodeMapListFlag && FormToCodeMapFlag) {
										if (CodeFlag) {
											if (CodingSystemFlag)
												CodingSystem = text;
											if (ValueFlag)
												Value = text;
											if (DisplayTextFlag)
												DisplayText = text;
										}
									}

								}

								break;

							}
							break;
						case XMLStreamConstants.END_ELEMENT:
							EndElement endElement = event.asEndElement();
							String endValue = endElement.getName().getLocalPart();
							switch (endValue) {
							case "ReviewComments":
								if (reviewComments.toString()
										.startsWith("<?xml version=\"1.0\" encoding=\"utf-8\"?>")) {
									boolean reviewCommentsObjectValueFlag = false;
									boolean reviewCommentsTextValueFlag = false;
									XMLEventReader reviewCommentsEventReader = null;
									try {
										XMLInputFactory reviewCommentsFactory = XMLInputFactory.newInstance();
										reviewCommentsEventReader = reviewCommentsFactory
												.createXMLEventReader(new StringReader(reviewComments.toString()));
										while (reviewCommentsEventReader.hasNext()) {
											XMLEvent reviewCommentsEvent = reviewCommentsEventReader.nextEvent();
											switch (reviewCommentsEvent.getEventType()) {
											case XMLStreamConstants.START_ELEMENT:
												StartElement reviewCommentsEventStartElement = reviewCommentsEvent
														.asStartElement();
												String reviewCommentsEventStartValue = reviewCommentsEventStartElement
														.getName().getLocalPart();
												switch (reviewCommentsEventStartValue) {
												case "ObjectValue":
													reviewCommentsObjectValueFlag = true;
													break;
												case "TextValue":
													reviewCommentsTextValueFlag = true;
													break;

												}
												break;
											case XMLStreamConstants.CHARACTERS:
												Characters reviewCommentsCharacters = reviewCommentsEvent
														.asCharacters();
												String reviewCommentsText = StringUtils
														.normalizeSpace(reviewCommentsCharacters.getData().trim());
												if (reviewCommentsText != null && !reviewCommentsText.isEmpty()) {
													if (reviewCommentsObjectValueFlag && reviewCommentsTextValueFlag) {
														fileWriter.write("Review Comments :" + reviewCommentsText);
														fileWriter.write("\n");
													}

												}

												break;
											case XMLStreamConstants.END_ELEMENT:
												EndElement reviewCommentsCharactersEndElement = reviewCommentsEvent
														.asEndElement();
												String reviewCommentsCharactersEndValue = reviewCommentsCharactersEndElement
														.getName().getLocalPart();
												switch (reviewCommentsCharactersEndValue) {
												case "ObjectValue":
													reviewCommentsObjectValueFlag = false;
													break;
												case "TextValue":
													reviewCommentsTextValueFlag = false;
													break;
												}
												break;

											}
										}
									}

									catch (XMLStreamException e) {
										e.printStackTrace();
									} finally {
										try {
											eventReader.close();
										} catch (Exception e) {
											e.printStackTrace();
											System.err.println(e.getMessage());
										}
									}
								} else {
									fileWriter.write("Review Comments :" + reviewComments.toString());
									fileWriter.write("\n");
								}
								ReviewCommentsFlag = false;
								break;
							case "STI_VitalSigns":
								STI_VitalSignsFlag = false;
								break;
							case "ObjectValue":
								ObjectValueFlag = false;
								break;
							case "TextValue":
								TextValueFlag = false;
								break;
							case "STI_Encounter_Subjective":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_Encounter_Objective":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_Encounter_Assessment":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_Encounter_Plan":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_Encounter_Medication":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "STI_Encounter_FollowUp":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "CP":
								CPFlag = false;
								break;
							case "VitalGroup":
								VitalGroupFlag = false;
								break;
							case "Readings":
								ReadingsFlag = false;
								break;
							case "FormToCodeMapList":
								FormToCodeMapListFlag = false;
								break;
							case "FormToCodeMap":
								FormToCodeMapFlag = false;
								break;
							case "Values":
								ValuesFlag = false;
								break;
							case "ValueItem":
								ValueItemFlag = false;
								break;
							case "Pulse":
								PulseFlag = false;
								break;
							case "RespirationRate":
								RespirationRateFlag = false;
								break;
							case "Centimeters":
								CentimetersFlag = false;
								break;
							case "Kilograms":
								KilogramsFlag = false;
								break;
							case "Code":
								fileWriter.write("\n");
								fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + CodingSystem + " " + Value + " "
										+ DisplayText);
								CodeFlag = false;
								CodingSystem = "";
								Value = "";
								DisplayText = "";
								break;
							case "CodingSystem":
								CodingSystemFlag = false;
								break;
							case "Value":
								ValueFlag = false;
								break;
							case "DisplayText":
								DisplayTextFlag = false;
								break;
							case "AvailableItems":
								AvailableItemsFlag = false;
								break;
							case "StrengthItem":
								StrengthItemFlag = false;
								break;
							case "LinkedItems":
								LinkedItemsFlag = false;
								break;
							case "Link":
								fileWriter.write("\n");
								fileWriter.write(
										(hdTitleCount > 1 ? "\t\t" : "\t") + Shortcut + " " + Description + " " + Type);
								Shortcut = "";
								Description = "";
								Type = "";
								LinkFlag = false;
								break;
							case "ItemNotes":
								ItemNotesFlag = false;
								break;
							case "Shortcut":
								ShortcutFlag = false;
								break;
							case "Description":
								DescriptionFlag = false;
								break;
							case "Type":
								TypeFlag = false;
							default:
								break;
							}
							break;

						}
					}
				}

				catch (

				FileNotFoundException e) {
					e.printStackTrace();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				} finally {
					try {
						fileWriter.flush();
						fileWriter.close();
						fr.close();
						eventReader.close();
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println(e.getMessage());
					}
				}
				FileUtils.forceDelete(new File(file));

			} else if (xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_SuperBill")) {

				boolean NotesFlag = false;

				boolean ObjectValueFlag = false;
				boolean TextValueFlag = false;

				boolean CodeFlag = false;
				boolean CodingSystemFlag = false;
				boolean ValueFlag = false;
				boolean DisplayTextFlag = false;
				boolean CodingFlag = false;

				boolean ItemNotesFlag = false;

				String CodingSystem = "";
				String Value = "";
				String DisplayText = "";

				boolean LinkedItemsFlag = false;
				boolean LinkFlag = false;
				boolean DescriptionFlag = false;
				boolean ShortcutFlag = false;
				boolean TypeFlag = false;

				String Shortcut = "";
				String Description = "";
				String Type = "";

				boolean CPFlag = false;
				boolean RulesFlag = false;
				boolean ItemFlag = false;

				boolean DosageFlag = false;
				boolean ExpirationFlag = false;
				boolean GivenFlag = false;
				boolean DrugFlag = false;
				boolean LotNumberFlag = false;

				int hdTitleCount = 0;

				Writer fileWriter = null;
				XMLEventReader eventReader = null;
				FileReader fr = null;
				try {
					XMLInputFactory factory = XMLInputFactory.newInstance();
					fr = new FileReader(file);
					eventReader = factory.createXMLEventReader(fr);
					convertedFile = file.concat(".txt");
					fileWriter = new FileWriter(new File(convertedFile));
					while (eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();
						switch (event.getEventType()) {
						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String startValue = startElement.getName().getLocalPart();
							switch (startValue) {
							case "Notes":
								NotesFlag = true;
								break;
							case "HD":
								hdTitleCount++;
								fileWriter.write("\n");
								fileWriter.write((hdTitleCount > 1 ? "\t" : "")
										+ startElement.getAttributeByName(new QName("Title")).getValue());
								break;
							case "BillablePulledItems":
								fileWriter.write("\n");
								hdTitleCount = 0;
								fileWriter.write((hdTitleCount > 1 ? "\t" : "") + "BillablePulledItems");
								hdTitleCount++;
								break;
							case "ObjectValue":
								ObjectValueFlag = true;
								break;
							case "TextValue":
								TextValueFlag = true;
								break;
							case "Code":
								CodeFlag = true;
								break;
							case "CodingSystem":
								CodingSystemFlag = true;
								break;
							case "Value":
								ValueFlag = true;
								break;
							case "DisplayText":
								DisplayTextFlag = true;
								break;
							case "ItemNotes":
								ItemNotesFlag = true;
								break;
							case "Shortcut":
								ShortcutFlag = true;
								break;
							case "Description":
								DescriptionFlag = true;
								break;
							case "Type":
								TypeFlag = true;
								break;
							case "LinkedItems":
								LinkedItemsFlag = true;
								break;
							case "Link":
								LinkFlag = true;
								break;
							case "CP":
								CPFlag = true;
								break;
							case "Rules":
								RulesFlag = true;
								break;
							case "Coding":
								CodingFlag = true;
								break;
							case "Item":
								ItemFlag = true;
								try {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t")
											+ startElement.getAttributeByName(new QName("Name")).getValue());
								} catch (Exception e) {
									// TODO: handle exception
								}

								break;
							case "Dosage":
								DosageFlag = true;
								break;
							case "Expiration":
								ExpirationFlag = true;
								break;
							case "Given":
								GivenFlag = true;
								break;
							case "Drug":
								DrugFlag = true;
								break;
							case "LotNumber":
								LotNumberFlag = true;
								break;
							}
							break;
						case XMLStreamConstants.CHARACTERS:
							Characters characters = event.asCharacters();
							String text = StringUtils.normalizeSpace(characters.getData().trim());
							if (text != null && !text.isEmpty()) {
								if (NotesFlag) {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + "Notes: " + text);

									break;
								}
								if (ObjectValueFlag && TextValueFlag) {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
								}

								if (ObjectValueFlag && CodeFlag) {
									if (CodingSystemFlag)
										CodingSystem = text;
									if (ValueFlag)
										Value = text;
									if (DisplayTextFlag)
										DisplayText = text;
								}
								if (LinkedItemsFlag && LinkFlag) {
									if (ShortcutFlag)
										Shortcut = text;
									if (DescriptionFlag)
										Description = text;
									if (TypeFlag)
										Type = text;
								}
								if (CPFlag && RulesFlag && ItemFlag) {
									if (CodingFlag && TextValueFlag) {
										fileWriter.write("\n");
										fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
									}

									if (CodeFlag) {
										if (CodingSystemFlag)
											CodingSystem = text;
										if (ValueFlag)
											Value = text;
										if (DisplayTextFlag)
											DisplayText = text;
									}

									if (DosageFlag) {
										fileWriter.write("\n");
										fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
									}
									if (ExpirationFlag) {
										fileWriter.write("\n");
										fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
									}
									if (GivenFlag) {
										fileWriter.write("\n");
										fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
									}
									if (DrugFlag) {
										fileWriter.write("\n");
										fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
									}
									if (LotNumberFlag) {
										fileWriter.write("\n");
										fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
									}

								}
								if (ItemNotesFlag) {
									fileWriter.write("\n");
									fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + text);
								}

							}
							break;
						case XMLStreamConstants.END_ELEMENT:
							EndElement endElement = event.asEndElement();
							String endValue = endElement.getName().getLocalPart();
							switch (endValue) {
							case "Notes":
								NotesFlag = false;
								break;
							case "Assessment":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "Plan":
								fileWriter.write("\n");
								hdTitleCount = 0;
								break;
							case "ObjectValue":
								ObjectValueFlag = false;
								break;
							case "TextValue":
								TextValueFlag = false;
								break;
							case "Code":
								fileWriter.write("\n");
								fileWriter.write((hdTitleCount > 1 ? "\t\t" : "\t") + CodingSystem + " " + Value + " "
										+ DisplayText);
								CodingSystem = "";
								Value = "";
								DisplayText = "";
								CodeFlag = false;
								break;
							case "CodingSystem":
								CodingSystemFlag = false;
								break;
							case "Value":
								ValueFlag = false;
								break;
							case "DisplayText":
								DisplayTextFlag = false;
								break;
							case "ItemNotes":
								ItemNotesFlag = false;
								break;
							case "Shortcut":
								ShortcutFlag = false;
								break;
							case "Description":
								DescriptionFlag = false;
								break;
							case "Type":
								TypeFlag = false;
								break;
							case "LinkedItems":
								LinkedItemsFlag = false;
								break;
							case "Link":
								fileWriter.write("\n");
								fileWriter.write(
										(hdTitleCount > 1 ? "\t\t" : "\t") + Shortcut + " " + Description + " " + Type);
								Shortcut = "";
								Description = "";
								Type = "";
								LinkFlag = false;
								break;
							case "CP":
								CPFlag = false;
								break;
							case "Rules":
								RulesFlag = false;
								break;
							case "Item":
								ItemFlag = false;
								break;
							case "Coding":
								CodingFlag = false;
								break;
							case "Dosage":
								DosageFlag = false;
								break;
							case "Expiration":
								ExpirationFlag = false;
								break;
							case "Given":
								GivenFlag = false;
								break;
							case "Drug":
								DrugFlag = false;
								break;
							case "LotNumber":
								LotNumberFlag = false;
								break;

							}

							break;

						}
					}
				}

				catch (

				FileNotFoundException e) {
					e.printStackTrace();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				} finally {
					try {
						fr.close();
						fileWriter.flush();
						fileWriter.close();
						eventReader.close();
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println(e.getMessage());
					}
				}
				FileUtils.forceDelete(new File(file));

			} else if (xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_RichText")) {

				boolean rtfDataFlag = false;

				String rtfData = "";

				StringBuffer rtfDataString = new StringBuffer();

				XMLEventReader eventReader = null;
				try {
					XMLInputFactory factory = XMLInputFactory.newInstance();
					eventReader = factory.createXMLEventReader(new FileReader(file));
					while (eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();
						switch (event.getEventType()) {
						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String startValue = startElement.getName().getLocalPart();
							switch (startValue) {
							case "RTFData":
								rtfDataFlag = true;
								break;
							default:
								break;

							}
							break;
						case XMLStreamConstants.CHARACTERS:
							Characters characters = event.asCharacters();
							if (rtfDataFlag) {
								rtfData = characters.getData();
								rtfDataString.append(rtfData);
								RTFEditorKit rtfParser = new RTFEditorKit();
								Document document = rtfParser.createDefaultDocument();
								rtfParser.read(new ByteArrayInputStream(rtfDataString.toString().getBytes()), document,
										0);
								String text = document.getText(0, document.getLength());
								rtfData = text;
								break;
							}
							break;
						case XMLStreamConstants.END_ELEMENT:
							EndElement endElement = event.asEndElement();
							String endValue = endElement.getName().getLocalPart();
							switch (endValue) {
							case "RTFData":
								rtfDataFlag = false;
								break;
							default:
								break;
							}
							break;

						}
					}
				}

				catch (

				FileNotFoundException e) {
					e.printStackTrace();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				} finally {
					try {
						eventReader.close();
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println(e.getMessage());
					}
				}
				convertedFile = file.concat(".txt");
				File txtFile = new File(convertedFile);
				txtFile.createNewFile();
				FileUtils.writeStringToFile(txtFile, rtfData, "UTF-8");
				FileUtils.forceDelete(new File(file));

			} else if (xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_PhoneMessage")) {
				boolean fromFlag = false;
				boolean atFlag = false;
				boolean phoneNumberFlag = false;
				boolean messageFlag = false;
				boolean noteFlag = false;

				String from = "";
				String at = "";
				String phoneNumber = "";
				String message = "";
				String note = "";

				StringBuffer messageString = new StringBuffer();
				StringBuffer noteString = new StringBuffer();

				XMLEventReader eventReader = null;
				try {
					XMLInputFactory factory = XMLInputFactory.newInstance();
					eventReader = factory.createXMLEventReader(new FileReader(file));
					while (eventReader.hasNext()) {
						XMLEvent event = eventReader.nextEvent();
						switch (event.getEventType()) {
						case XMLStreamConstants.START_ELEMENT:
							StartElement startElement = event.asStartElement();
							String startValue = startElement.getName().getLocalPart();
							switch (startValue) {
							case "From":
								fromFlag = true;
								break;
							case "At":
								atFlag = true;
								break;
							case "PhoneNumber":
								phoneNumberFlag = true;
								break;
							case "Message":
								messageFlag = true;
								break;
							case "Note":
								noteFlag = true;
								break;
							default:
								break;

							}
							break;
						case XMLStreamConstants.CHARACTERS:
							Characters characters = event.asCharacters();
							if (fromFlag) {
								from = characters.getData();
								break;
							}
							if (atFlag) {
								at = characters.getData();
								break;
							}
							if (phoneNumberFlag) {
								phoneNumber = characters.getData();
								break;
							}
							if (messageFlag) {
								message = characters.getData();
								messageString.append(message);
								RTFEditorKit rtfParser = new RTFEditorKit();
								Document document = rtfParser.createDefaultDocument();
								rtfParser.read(new ByteArrayInputStream(messageString.toString().getBytes()), document,
										0);
								String text = document.getText(0, document.getLength());
								message = text;
								break;
							}
							if (noteFlag) {
								note = characters.getData();
								noteString.append(note);
								RTFEditorKit rtfParser = new RTFEditorKit();
								Document document = rtfParser.createDefaultDocument();
								rtfParser.read(new ByteArrayInputStream(noteString.toString().getBytes()), document, 0);
								String text = document.getText(0, document.getLength());
								note = text;
								break;
							}
							break;
						case XMLStreamConstants.END_ELEMENT:
							EndElement endElement = event.asEndElement();
							String endValue = endElement.getName().getLocalPart();
							switch (endValue) {
							case "From":
								fromFlag = false;
								break;
							case "At":
								atFlag = false;
								break;
							case "PhoneNumber":
								phoneNumberFlag = false;
								break;
							case "Message":
								messageFlag = false;
								break;
							case "Note":
								noteFlag = false;
								break;
							default:
								break;
							}
							break;

						}
					}
				}

				catch (

				FileNotFoundException e) {
					e.printStackTrace();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				} finally {
					try {
						eventReader.close();
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println(e.getMessage());
					}
				}

				StringBuffer sfb = new StringBuffer();
				sfb.append("<html><head><style> table {\r\n" + "  font-family: arial, sans-serif;\r\n"
						+ "  border-collapse: collapse;\r\n" + "  width: 100%;\r\n" + "}\r\n" + "\r\n" + "td, th {\r\n"
						+ "  border: 1px solid #dddddd;\r\n" + "  text-align: left;\r\n" + "  padding: 8px;\r\n"
						+ "}</style></head><body>");

				sfb.append("<h1>Phone Message</h1>");
				sfb.append("<br></br><table>");
				sfb.append("<tr><th>From</th><td colspan=\"3\">" + from + "</td></tr>");
				sfb.append("<tr><td colspan=\"1\"><b>" + "At" + "</b></td><td colspan=\"3\">" + at + "</td></tr>");
				sfb.append("<tr><td colspan=\"1\"><b>" + "Phone Number" + "</b></td><td colspan=\"3\">" + phoneNumber
						+ "</td></tr>");
				sfb.append("<tr><td colspan=\"1\"><b>" + "Message" + "</b></td><td colspan=\"3\">"
						+ message.replace("&", "and").replace("<", " less then ") + "</td></tr>");
				sfb.append("<tr><td colspan=\"1\"><b>" + "Note" + "</b></td><td colspan=\"3\">"
						+ note.replace("&", "and").replace("<", " less then ") + "</td></tr>");
				sfb.append("</table></body></html>");

				String File_To_Convert = file.concat(".html");

				PrintWriter writer = new PrintWriter(File_To_Convert, "UTF-8");
				writer.println(sfb.toString());
				writer.close();

				String url = new File(File_To_Convert).toURI().toURL().toString();
				convertedFile = file.concat(".pdf");
				OutputStream os = new FileOutputStream(new File(convertedFile));
				ITextRenderer renderer = new ITextRenderer();
				renderer.setDocument(url);
				renderer.layout();
				renderer.createPDF(os);
				os.close();
				FileUtils.forceDelete(new File(File_To_Convert));
				FileUtils.forceDelete(new File(file));
			} else if (new File(xSLPath).getName().contains("SOAPware_DocumentItems_Lab")) {
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer(new StreamSource(xSLPath));
				String File_To_Convert = file.concat(".html");
				transformer.setOutputProperty("omit-xml-declaration", "yes");
				FileOutputStream oss = new FileOutputStream(File_To_Convert);
				transformer.transform(new StreamSource(file), new StreamResult(oss));
				oss.close();
				FileUtils.forceDelete(new File(file));
			} else {
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer transformer = tFactory.newTransformer(new StreamSource(xSLPath));
				String File_To_Convert = file.concat(".html");
				transformer.setOutputProperty("omit-xml-declaration", "yes");
				FileOutputStream oss = new FileOutputStream(File_To_Convert);
				transformer.transform(new StreamSource(file), new StreamResult(oss));
				oss.close();

				String url = new File(File_To_Convert).toURI().toURL().toString();
				convertedFile = file.concat(".pdf");
				FileOutputStream os = new FileOutputStream(new File(convertedFile));
				ITextRenderer renderer = new ITextRenderer();
				renderer.setDocument(url);
				renderer.layout();
				renderer.createPDF(os);
				renderer.finishPDF();
				os.close();
				FileUtils.forceDelete(new File(File_To_Convert));
				FileUtils.forceDelete(new File(file));
			}

			boolean sizeexp = false;
			double size = 0;
			try {
				size = Math.ceil(((double) (new File(convertedFile).length()) / (double) 1024));
			} catch (Exception e) {
				sizeexp = true;
			}
			long endTime = new Date().getTime();

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
			if (xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_RichText")
					|| xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_MainSummarySmartText")
					|| xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_SmartText")
					|| xSLPath.equalsIgnoreCase("SOAPware_DocumentItems_SuperBill")) {

				formattingHelper.writeAttribute("ref",
						"../BLOBs/" + checkValidFolder(options.getBlobTableName().toUpperCase()) + "/"
								+ checkValidFolder(options.getBlobColumnName().toUpperCase()) + "/" + "Folder-"
								+ options.getBlobFolderidentifier() + "-" + numberseq + "/" + validfilename + ".txt");
				formattingHelper.writeValue(validfilename + ".txt");
			} else if (new File(xSLPath).getName().contains("SOAPware_DocumentItems_Lab")) {

				formattingHelper.writeAttribute("ref",
						"../BLOBs/" + checkValidFolder(options.getBlobTableName().toUpperCase()) + "/"
								+ checkValidFolder(options.getBlobColumnName().toUpperCase()) + "/" + "Folder-"
								+ options.getBlobFolderidentifier() + "-" + numberseq + "/" + validfilename + ".html");
				formattingHelper.writeValue(validfilename + ".html");
			} else {

				formattingHelper.writeAttribute("ref",
						"../BLOBs/" + checkValidFolder(options.getBlobTableName().toUpperCase()) + "/"
								+ checkValidFolder(options.getBlobColumnName().toUpperCase()) + "/" + "Folder-"
								+ options.getBlobFolderidentifier() + "-" + numberseq + "/" + validfilename + ".pdf");
				formattingHelper.writeValue(validfilename + ".pdf");

			}
			formattingHelper.writeElementEnd();
			formattingHelper.writeElementStart("STATUS");
			formattingHelper.writeAttribute("SIZE_KB", sizeexp ? "Could not determine" : Double.toString(size));
			formattingHelper.writeAttribute("TIME_MS", Long.toString(endTime - startTime));
			formattingHelper.writeValue("SUCCESS");
			formattingHelper.writeElementEnd();
			formattingHelper.writeRecordEnd();

		} catch (Exception e) {

			e.printStackTrace();

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
