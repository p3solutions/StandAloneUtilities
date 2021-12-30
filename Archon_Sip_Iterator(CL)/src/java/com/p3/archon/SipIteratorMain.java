package com.p3.archon;

import java.util.Date;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.p3.archon.bean.ArchonInputBean;
import com.p3.archon.process.StartProcess;

public class SipIteratorMain {
	public static void main(String[] args) {
		ArchonInputBean bean = new ArchonInputBean();
		CmdLineParser parser = new CmdLineParser(bean);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			e.printStackTrace();
			System.err.println("Please check arguments specified. \n" + e.getMessage() + "\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(1);
		}

		StartProcess sp = new StartProcess(bean);
		sp.start();

	}

}
