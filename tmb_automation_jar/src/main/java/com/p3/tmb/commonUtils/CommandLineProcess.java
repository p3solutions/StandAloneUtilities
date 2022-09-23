package com.p3.tmb.commonUtils;

/*
 * $$HeadURL$$
 * $$Id$$
 *
 * CCopyright (c) 2015, P3Solutions . All Rights Reserved.
 * This code may not be used without the express written permission
 * of the copyright holder, P3Solutions.
 */

import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.ingester.beans.ingesterInputBean;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.sql.Timestamp;

/**
 * @author Malik
 * 
 *         General Utility to run a command line process
 *
 */
public class CommandLineProcess {

	public String outputLogFile;
	public ingesterInputBean inputargs;
	public String errorlog;
	public String outputlog;
	public String uuid;

	final Logger log = LogManager.getLogger(CommandLineProcess.class.getName());

	public CommandLineProcess(ingesterInputBean inputargs) {
		this.inputargs = inputargs;
		uuid = inputargs.getReportId();

		String identifier = CommonSharedConstants.PREFIX_APP_INGESTER;
		String archonlogpath = File.separator + "log" + File.separator;
		this.errorlog = inputargs.getOutputLog()+File.separator+ "output_" + identifier + "_" + uuid + ".log";
		this.outputlog = inputargs.getOutputLog()+File.separator + "output_" + identifier + "_" + uuid + ".log";
	}

	/**
	 * runs the command
	 * 
	 * @param command the command
	 * 
	 * @return returns true if the script ran successfully
	 * @throws Exception if there is an error
	 */
	public boolean run(String[] command, String appname) throws Exception {
		String checkLine = "cd applications/" + appname;
		return run(command, checkLine, null);
	}

	/**
	 * runs the command
	 * 
	 * @param command                   the command
	 * @param fileDirectoryToRunCommand the directory on the file system to where
	 *                                  the command is to be run
	 * 
	 *
	 * @return returns true if the script ran successfully
	 * @throws Exception if there is an error
	 */
	public boolean run(String[] command, String cmdcheckline, File fileDirectoryToRunCommand) throws Exception {
		try {
			// do this for debugging to print out the command to be run
			int len = command.length;
			String newCommand = "";
			String sep = "";
			for (int i = 0; i < len; i++) {
				newCommand += sep + command[i];
				sep = " ";
			}
			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Running command =  ('" + newCommand + "')");

			// Now run the process
			Process p;
			if (fileDirectoryToRunCommand != null) {
				log.info(
						"Running command at File Directory = '" + fileDirectoryToRunCommand.getAbsolutePath() + "'");
				p = Runtime.getRuntime().exec(command, null, fileDirectoryToRunCommand);
			} else {
				p = Runtime.getRuntime().exec(command);
			}
			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Process has beean successfully completed.");
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Process has beean successfully completed." + CommonSharedConstants.newLine);
			outputLogFile = new File(outputlog).getAbsolutePath();
			// any error or output messages
			CommandLineStream errorGobbler = new CommandLineStream(p.getErrorStream(), "ERROR", errorlog, p,
					cmdcheckline, this.inputargs);
			CommandLineStream outputGobbler = new CommandLineStream(p.getInputStream(), "OUTPUT", outputlog, p,
					cmdcheckline, this.inputargs);
			errorGobbler.start();
			outputGobbler.start();
			int exitVal = p.waitFor();
			if (exitVal != 0) {
				log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Return value error occurred. Value = " + exitVal);
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Return value error occurred. Value = " + exitVal + CommonSharedConstants.newLine);
				return false;
			}
			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Return value of process is " + exitVal);
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Return value of process is " + exitVal + CommonSharedConstants.newLine);
			return true;
		} catch (Exception e) {
			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  An Error has occurred = " + e.getMessage());
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  An Error has occurred = " + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			throw new Exception("An Error has occurred: " + e.getMessage(), e);
		}

	}

	/**
	 * runs the command
	 * 
	 * @param command                   the command
	 * @param fileDirectoryToRunCommand the directory on the file system to where
	 *                                  the command is to be run
	 * 
	 *
	 * @return returns true if the script ran successfully
	 * @throws Exception if there is an error
	 */
	public boolean run(String command, String cmdcheckline, File fileDirectoryToRunCommand) throws Exception {
		try {
			// Now run the process
			Process p;
			if (fileDirectoryToRunCommand != null) {
				log.info(
						"Running command at File Directory = '" + fileDirectoryToRunCommand.getAbsolutePath() + "'");
				p = Runtime.getRuntime().exec(command, null, fileDirectoryToRunCommand);
			} else {
				p = Runtime.getRuntime().exec(command);
			}

			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Process has beean successfully completed.");
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Process has beean successfully completed." + CommonSharedConstants.newLine);

			outputLogFile = new File(outputlog).getAbsolutePath();
			// any error or output messages
			CommandLineStream errorGobbler = new CommandLineStream(p.getErrorStream(), "ERROR", errorlog, p,
					cmdcheckline, this.inputargs);
			CommandLineStream outputGobbler = new CommandLineStream(p.getInputStream(), "OUTPUT", outputlog, p,
					cmdcheckline, this.inputargs);
			// kick off the threads
			errorGobbler.start();
			outputGobbler.start();
			int exitVal = p.waitFor();
			if (exitVal != 0) {
				log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Return value error occurred. Value = " + exitVal);
				CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Return value error occurred. Value = " + exitVal + CommonSharedConstants.newLine);
				return false;
			}
			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Return value of process is " + exitVal);
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  Return value of process is " + exitVal + CommonSharedConstants.newLine);
			return true;
		} catch (Exception e) {
			log.info( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  An Error has occurred = " + e.getMessage());
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  An Error has occurred = " + commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			throw new Exception( CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  An Error has occurred: " + e.getMessage(), e);
		}
	}

}

/**
 * @author Malik
 * 
 *         CommandLineStream - thread class to read the input and error message
 *         streams from the command line
 *
 */
class CommandLineStream extends Thread {
	String path;
	InputStream is;
	String type;
	String file;
	Writer out;
	Process p;
	String checkline;
	String outputPath = "";
	boolean flag = false;
	boolean metadataIngestion = false;
	boolean sipIngestion;
	public static final String RECEIVED_STRING = "Received ";
	public static final String ZIP_STRING = ".zip";

	public final static char CR = (char) 0x0D;
	public final static char LF = (char) 0x0A;

	public final static String CRLF = "" + CR + LF;
	String parentSourcePath;

	final Logger log = LogManager.getLogger(CommandLineProcess.class.getName());

	CommandLineStream(InputStream is, String type, String file, Process p, String checkline, ingesterInputBean inputargs) {
		this.path = inputargs.getDataPath();
		this.parentSourcePath = new File(path).getAbsolutePath();
		this.is = is;
		this.type = type;
		this.file = file;
		this.p = p;
		this.checkline = checkline;
		this.outputPath = inputargs.outputPath;
		try {
			this.out = new OutputStreamWriter(new FileOutputStream(file, true));
			this.out.write("Ingestion Report Process Log");
			this.out.write(CRLF);
		} catch (Exception e) {
		}
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			if (this.out != null) {
				while ((line = br.readLine()) != null) {
					if (line.contains("cd ../../")) {
						continue;
					}
					if (line.contains("command line client (c) OpenText Corporation")
							|| line.contains(
									"Type 'help' for a list of commands and options, type 'exit' to leave the shell.")
							|| line.contains("quit")) {
						continue;
					}
					if (checkline != null && line.contains(checkline)) {
						flag = true;
						continue;
					}
					if (line.startsWith("Connected to")) {
						flag = true;
					}
					if (flag && line.contains("End time:")) {
						this.out.write(CRLF);
						this.out.write(CRLF);
						this.out.write(CRLF);
						log.error("\nUnable to find the specified Application. Please check application name");
						CommonSharedConstants.logContent.append("\nUnable to find the specified Application. Please check application name"+CommonSharedConstants.newLine);
						this.out.write("\nPlease check application name.");
						CommonSharedConstants.logContent.append("\nPlease check application name."+CommonSharedConstants.newLine);
						this.out.write(CRLF);
						this.out.write(CRLF);
						this.out.write(CRLF);
						this.out.write(line);
						this.out.write(CRLF);
						flag = false;
						continue;
					} else if (flag) {
						flag = false;
					}
					if (line.contains("(Permission denied)")
							|| line.contains("Sorry, password you entered is incorrect, try again.")
							|| line.contains("Connection was not established. Try re-entering password.")) {
						this.out.write(CRLF);
						this.out.write(CRLF);
						this.out.write(CRLF);
						this.out.write(line);
						this.out.write(line.contains("(Permission denied)") ? "\nPermission unavailable"
								: "\nCredentials are invalid");
						this.out.write(CRLF);
						log.info(line.contains("(Permission denied)") ? "\nPermission unavailable"
								: "\nCredentials are invalid");
						CommonSharedConstants.logContent.append(line.contains("(Permission denied)") ? "\nPermission unavailable"
								: "\nCredentials are invalid"+CommonSharedConstants.newLine);
						log.error(line.contains("(Permission denied)") ? "\nPermission unavailable"
								: "\nCredentials are invalid");
						out.flush();
						out.close();
						p.destroy();
						return;
					}
					if (line.startsWith("connect") || line.contains("ERROR - Command failed: connect --u")) {
//						this.out.write(line.substring(0, line.indexOf("--p ") + 4));
//						this.out.write(line.substring(line.indexOf("--p ") + 4).replaceAll(".", "*"));
//						this.out.write(CRLF);
					} else {
						this.out.write(line);
						this.out.write(CRLF);
					}

					if (!metadataIngestion) {
						if (line.contains("ERROR - Cannot upload the file: '")) {
							line = line.substring(0, line.indexOf("'. Please, contact ")).replace("'. Please, contact ",
									"");
							if (!((CommonSharedConstants.IA_VERSION.equals("16EP6")
									|| CommonSharedConstants.IA_VERSION.equals("16EP7")
									|| CommonSharedConstants.IA_VERSION.equals("20.2")
									|| CommonSharedConstants.IA_VERSION.equals("20.4")
									|| CommonSharedConstants.IA_VERSION.equals("21.2")
									|| CommonSharedConstants.IA_VERSION.equals("21.4")
									|| CommonSharedConstants.IA_VERSION.equals("22.2")) && sipIngestion)) {
								String file = (line.substring(line.indexOf(path)));
								try {
									FileUtil.checkCreateDirectory(
											parentSourcePath + File.separator + "INGESTION_FAILED");
									if (!sipIngestion)
										moveBlobFile(new File(file), false, parentSourcePath);
									FileUtils.moveFile(new File(file), new File(parentSourcePath + File.separator
											+ "INGESTION_FAILED" + File.separator + new File(file).getName()));
								} catch (Exception e) {
									log.info(
											"Unable to move '" + file + "' to " + "INGESTION_FAILED directory");
									CommonSharedConstants.logContent.append("Unable to move '" + file + "' to " + "INGESTION_FAILED directory" +CommonSharedConstants.newLine);
									e.printStackTrace();
									CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e));
								}
							}
						} else if (line.trim().startsWith("Ingested") || line.trim().startsWith("Received")
								|| line.trim().contains("Ingested file")) {
							line = line.trim();
							if (!((CommonSharedConstants.IA_VERSION.equals("16EP6")
									|| CommonSharedConstants.IA_VERSION.equals("16EP7")
									|| CommonSharedConstants.IA_VERSION.equals("20.2")
									|| CommonSharedConstants.IA_VERSION.equals("20.4")
									|| CommonSharedConstants.IA_VERSION.equals("21.2")
									|| CommonSharedConstants.IA_VERSION.equals("21.4")
									|| CommonSharedConstants.IA_VERSION.equals("22.2")) && sipIngestion)) {
								String file = sipIngestion
										? (path + File.separator
												+ line.substring(
														line.indexOf(RECEIVED_STRING) + RECEIVED_STRING.length(),
														line.indexOf(ZIP_STRING) + ZIP_STRING.length()))
										: (CommonSharedConstants.IA_VERSION.equals("16EP6")
												|| CommonSharedConstants.IA_VERSION.equals("16EP7")
												|| CommonSharedConstants.IA_VERSION.equals("20.2")
												|| CommonSharedConstants.IA_VERSION.equals("20.4")
												|| CommonSharedConstants.IA_VERSION.equals("21.2")
												|| CommonSharedConstants.IA_VERSION.equals("21.4")
												|| CommonSharedConstants.IA_VERSION.equals("22.2")) ? line.substring(
														line.indexOf(path),
														line.contains(". It took ") ? line.lastIndexOf(". It took ")
																: line.lastIndexOf(" took "))
														: line.substring(line.indexOf(path));
								try {
									FileUtil.checkCreateDirectory(
											parentSourcePath + File.separator + "INGESTION_SUCCESS");
									if (line.startsWith("Ingested file with attachments:")) {
										log.info("Starting to move the blob data");
										CommonSharedConstants.logContent.append("Starting to move the blob data" +CommonSharedConstants.newLine);
										if (!sipIngestion)
											moveBlobFile(new File(file), true, parentSourcePath);
									}

									FileUtils.moveFile(new File(file),
											new File(parentSourcePath + File.separator + "INGESTION_SUCCESS"
													+ File.separator + new File(file).getName() + ".ingested"));
								} catch (Exception e) {
									log.info(
											"Unable to move '" + file + "' to " + "INGESTION_SUCCESS directory");
									CommonSharedConstants.logContent.append("Unable to move '" + file + "' to " + "INGESTION_SUCCESS directory"+CommonSharedConstants.newLine);
									e.printStackTrace();
									CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e));
								}
							}
						}
					}

				}
				out.flush();
				out.close();
			} else
				while ((line = br.readLine()) != null)
					log.info(type + ">" + line);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			CommonSharedConstants.logContent.append("Exception in CommandLineProcess run : " + commonUtils.exceptionMsgToString(ioe) + CommonSharedConstants.newLine);
		}
	}

	private void moveBlobFile(File xmlFile, boolean sucess, String parentSourcePath)
			throws IOException, XMLStreamException {
		log.info("Processing xml file " + xmlFile + " with blob reference.");
		String moveDir = "";
		File path = new File(xmlFile.getParent());
		XMLInputFactory factory = null;
		XMLEventReader eventReader = null;
		XMLEvent event = null;
		FileReader fr = null;
		if (sucess)
			moveDir = "INGESTION_SUCCESS";
		else
			moveDir = "INGESTION_FAILED";
		String startValue;
		String ref;
		File srcFilePath = path;
		boolean winOS = OSIdentifier.checkOS();
		try {
			factory = XMLInputFactory.newInstance();
			fr = new FileReader(xmlFile);
			eventReader = factory.createXMLEventReader(fr);
			while (eventReader.hasNext()) {
				event = eventReader.nextEvent();

				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					startValue = startElement.getName().getLocalPart();
					switch (startValue) {
					case "FILE":
						try {
							srcFilePath = path;
							srcFilePath = new File(srcFilePath.getParent());
							ref = startElement.getAttributeByName(new QName("ref")).getValue();
							srcFilePath = new File(
									srcFilePath + (winOS ? ref.substring(2, ref.length()).replace("/", File.separator)
											: ref.substring(2, ref.length())));
						} catch (Exception e) {
							// e.printStackTrace();
							continue;
						}
						try {
							if (srcFilePath.exists()) {
								FileUtil.checkCreateDirectory(srcFilePath.getParent() + File.separator + moveDir);
								FileUtils.moveFile(srcFilePath, new File(srcFilePath.getParent() + File.separator
										+ moveDir + File.separator + srcFilePath.getName()));
								log.info("Moved '" + srcFilePath + "' to " + moveDir + " directory");
							} else {
								log.info("Failed to move as this file is part of source dir");
							}

						} catch (Exception e) {
							System.out
									.println("Unable to move blob '" + srcFilePath + "' to " + moveDir + " directory");
							e.printStackTrace();
							CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
						}
						break;
					case "ARCHIVE_FILE_NAME":
						try {
							srcFilePath = path;
							ref = startElement.getAttributeByName(new QName("ref")).getValue();
							srcFilePath = new File(
									srcFilePath + File.separator + (winOS ? ref.replace("/", File.separator) : ref));
						} catch (Exception e) {
							// e.printStackTrace();
							continue;
						}
						try {
							if (srcFilePath.exists()) {
								FileUtil.checkCreateDirectory(srcFilePath.getParent() + File.separator + moveDir);
								FileUtils.moveFile(srcFilePath, new File(srcFilePath.getParent() + File.separator
										+ moveDir + File.separator + srcFilePath.getName()));
								log.info("Moved '" + srcFilePath + "' to " + moveDir + " directory");
							} else {
								log.info("Failed to move as this file is part of source dir");
							}
						} catch (Exception e) {
							System.out
									.println("Unable to move blob '" + srcFilePath + "' to " + moveDir + " directory");
							e.printStackTrace();
							CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
						}
						break;
					default:
						try {
							srcFilePath = path;
							ref = startElement.getAttributeByName(new QName("ref")).getValue();
							srcFilePath = new File(srcFilePath + File.separator + (ref.replace("/", File.separator)));
						} catch (Exception e) {
							continue;
						}
						log.info("srcFilePath => " + srcFilePath);
						try {
							if (srcFilePath.exists()) {

								FileUtil.checkCreateDirectory(parentSourcePath + File.separator + moveDir);
								FileUtils.moveFile(srcFilePath,
										new File(parentSourcePath + File.separator + moveDir + File.separator + ref));
								log.info("Moved '" + srcFilePath + "' to " + moveDir + " directory");
							} else {
								log.info("Failed to move as this file is part of source dir");
							}
						} catch (Exception e) {
							System.out
									.println("Unable to move blob '" + srcFilePath + "' to " + moveDir + " directory");
							e.printStackTrace();
							CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
						}
					}
				}
			}
			fr.close();
			eventReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			fr.close();
			eventReader.close();
		}
	}

}