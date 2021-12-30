package com.p3.archon;

import java.util.Date;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.p3.archon.beans.ArchonInputBean;
import com.p3.archon.process.StartProcess;

public class FileExtentionAdderMain {

	public static void main(String[] args) {

//		args = new String[] { "-ip", "C:\\Users\\91735\\Downloads\\a", "-op", "E:\\SDR\\",
//				"-e", "UTF-8" };
		if (args.length == 0) {
			System.err.println("No arguments specified.\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(1);
		}

		ArchonInputBean ipargs = new ArchonInputBean();
		CmdLineParser parser = new CmdLineParser(ipargs);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
			System.err.println("Please check arguments specified. \n" + e.getMessage() + "\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(1);
		}

		StartProcess process = new StartProcess(ipargs);
		process.start();

		System.out.println("Job completed at " + new Date());

	}

}
