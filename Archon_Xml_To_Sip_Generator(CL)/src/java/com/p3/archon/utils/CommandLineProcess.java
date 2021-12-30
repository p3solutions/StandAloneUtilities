package com.p3.archon.utils;

/*
 * $$HeadURL$$
 * $$Id$$
 *
 * CCopyright (c) 2015, P3Solutions . All Rights Reserved.
 * This code may not be used without the express written permission
 * of the copyright holder, P3Solutions.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

/**
 * @author Malik
 * 
 *         General Utility to run a command line process
 *
 */

public class CommandLineProcess {

	private String jobId;
	private String wf;
	
	public CommandLineProcess() {
		this.jobId = "";
		this.wf = "";
	}
	
	public CommandLineProcess(String jobId, String wf){
		this.jobId = jobId;
		this.wf = wf;
	}
	
	/**
	 * runs the command
	 * 
	 * @param command
	 *            the command
	 * 
	 * @return returns true if the script ran successfully
	 * @throws Exception
	 *             if there is an error
	 */
	public boolean run(String[] command) throws Exception {
		return run(command, null);
	}

	/**
	 * runs the command
	 * 
	 * @param command
	 *            the command
	 * @param fileDirectoryToRunCommand
	 *            the directory on the file system to where the command is to be
	 *            run
	 * 
	 *
	 * @return returns true if the script ran successfully
	 * @throws Exception
	 *             if there is an error
	 */
	public boolean run(String[] command, File fileDirectoryToRunCommand) throws Exception {
		try {
			// do this for debugging to print out the command to be run
			int len = command.length;
			String newCommand = "";
			String sep = "";
			for (int i = 0; i < len; i++) {
				newCommand += sep + command[i];
				sep = " ";
			}
			System.out.println("Running command =  ('" + newCommand + "')");

			// Now run the process
			Process p;
			if (fileDirectoryToRunCommand != null) {
				System.out.println(
						"Running command at File Directory = '" + fileDirectoryToRunCommand.getAbsolutePath() + "'");
				p = Runtime.getRuntime().exec(command, null, fileDirectoryToRunCommand);
			} else {
				p = Runtime.getRuntime().exec(command);
			}
			System.out.println("Process successfully created.");

			long datefile = new Date().getTime();
			String errorlog = "log" + File.separator + jobId + "_" + wf + "_error_" + datefile + ".log";
			String outputlog = "log" + File.separator + jobId + "_" + wf + "_output_" + datefile + ".log";

			// any error or output messages
			CommandLineStream errorGobbler = new CommandLineStream(p.getErrorStream(), "ERROR", errorlog, jobId, wf);
			CommandLineStream outputGobbler = new CommandLineStream(p.getInputStream(), "OUTPUT", outputlog, jobId, wf);
			errorGobbler.start();
			outputGobbler.start();
			int exitVal = p.waitFor();
			if (exitVal != 0) {
				System.out.println("Return value error occurred. Value = " + exitVal);
				throw new Exception(
						"Error occured. Refer file " + new File(errorlog).getAbsolutePath() + " for details");
			}
			System.out.println("Return value of process is " + exitVal);
			return true;

		} catch (Exception e) {
			System.out.println("An Error has occurred = " + e.getMessage());
			throw new Exception("An Error has occurred: " + e.getMessage(), e);
		}

	}

	/**
	 * runs the command
	 * 
	 * @param command
	 *            the command
	 * @param fileDirectoryToRunCommand
	 *            the directory on the file system to where the command is to be
	 *            run
	 * 
	 *
	 * @return returns true if the script ran successfully
	 * @throws Exception
	 *             if there is an error
	 */
	public boolean run(String command, File fileDirectoryToRunCommand) throws Exception {
		try {
			// Now run the process
			Process p;
			if (fileDirectoryToRunCommand != null) {
				System.out.println(
						"Running command at File Directory = '" + fileDirectoryToRunCommand.getAbsolutePath() + "'");
				p = Runtime.getRuntime().exec(command, null, fileDirectoryToRunCommand);
			} else {
				p = Runtime.getRuntime().exec(command);
			}
			System.out.println("Process successfully created.");

			String errorlog = "log" + File.separator + jobId + "_" + wf + "_error_" + new Date().getTime() + ".log";
			String outputlog = "log" + File.separator + jobId + "_" + wf + "_output_" + new Date().getTime() + ".log";
			// any error or output messages
			CommandLineStream errorGobbler = new CommandLineStream(p.getErrorStream(), "ERROR", errorlog, jobId, wf);
			CommandLineStream outputGobbler = new CommandLineStream(p.getInputStream(), "OUTPUT", outputlog, jobId, wf);

			// kick off the threads
			errorGobbler.start();
			outputGobbler.start();
			int exitVal = p.waitFor();
			System.out.println("Exit Value: "+exitVal);
			if (exitVal != 0) {
				System.out.println("Return value error occurred. Value = " + exitVal);
				throw new Exception(
						"Error occured. Refer file " + new File(errorlog).getAbsolutePath() + " for details");
			}
			System.out.println("Return value of process is " + exitVal);
			FileUtil.deleteFile(errorlog);
			return true;

		} catch (Exception e) {
			System.out.println("An Error has occurred = " + e.getMessage());
			e.printStackTrace();
			throw new Exception("An Error has occurred: " + e.getMessage(), e);
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
	InputStream is;
	String type;
	String file;
	Writer out;
	String jobId;
	String wf;
	public final static char CR = (char) 0x0D;
	public final static char LF = (char) 0x0A;

	public final static String CRLF = "" + CR + LF;

	CommandLineStream(InputStream is, String type, String file, String jobId, String wf) {
		this.is = is;
		this.type = type;
		this.file = file;
		this.jobId = jobId;
		this.wf = wf;
		try {
			FileUtil.checkCreateDirectory("log");
			this.out = new OutputStreamWriter(new FileOutputStream(file));
		} catch (Exception e) {
		}
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			int counter = 0;
			if (this.out != null) {
				while (type.equals("OUTPUT") && (line = br.readLine()) != null && ++counter < 10) {

				}
				while ((line = br.readLine()) != null) {
					this.out.write(line);
					this.out.write(CRLF);
				}
				out.flush();
				out.close();
			} else
				while ((line = br.readLine()) != null)
					System.out.println(type + ">" + line);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}