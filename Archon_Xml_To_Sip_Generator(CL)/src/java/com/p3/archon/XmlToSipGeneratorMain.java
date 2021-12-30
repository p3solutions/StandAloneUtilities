package com.p3.archon;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Date;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.gson.Gson;
import com.p3.archon.bean.ArchonInputBean;
import com.p3.archon.process.StartProcess;
import com.p3.archon.utils.JobLogger;

public class XmlToSipGeneratorMain {
	public static void main(String[] args) {

//		args = new String[] { "-ip", "E:\\oracle9i", "-an", "test", "-h", "d", "-isSip", "-rpx", "1" };

		JobLogger.getLogger().info("SipGen", XmlToSipGeneratorMain.class.getName(), "main",
				"Job Started = " + new Date());

		ArchonInputBean ipargs = new ArchonInputBean();
		CmdLineParser parser = new CmdLineParser(ipargs);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			JobLogger.getLogger().error("SipGen", XmlToSipGeneratorMain.class.getName(), "main",
					"Please check arguments specified. \n" + errors.toString() + "\nTerminating ... ");
			JobLogger.getLogger().info("SipGen", XmlToSipGeneratorMain.class.getName(), "main",
					"Job Terminated = " + new Date());
			System.exit(1);
		}

		JobLogger.getLogger().info("SipGen", XmlToSipGeneratorMain.class.getName(), "main",
				"Arguments ->" + new Gson().toJson(args));

		StartProcess sp = new StartProcess(ipargs);
		try {
			sp.startProcessing();
		} catch (SQLException | IOException e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			JobLogger.getLogger().error("SipGen", XmlToSipGeneratorMain.class.getName(), "main",
					"Please check arguments specified. \n" + errors.toString() + "\nTerminating ... ");
			JobLogger.getLogger().info("SipGen", XmlToSipGeneratorMain.class.getName(), "main",
					"Job Terminated = " + new Date());
		}

		JobLogger.getLogger().info("SipGen", XmlToSipGeneratorMain.class.getName(), "main",
				"Job Completed = " + new Date());

	}

}
