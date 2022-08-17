package com.p3.archon.coreprocess.jobmode;

import java.io.File;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.p3.archon.coreprocess.beans.ArchonInputBean;
import com.p3.archon.coreprocess.common.Constants;
import com.p3.archon.coreprocess.process.DataLiberator;
import com.p3.archon.utils.FileUtil;

public class ManualMode {

	private ArchonInputBean inputArgs;

	public ManualMode(ArchonInputBean inputArgs) {
		this.inputArgs = inputArgs;
	}

	public ManualMode() {
		this.inputArgs = new ArchonInputBean();
	}

	public void startValidateMode() {
		// inputArgs.setCommand("quickdump");
		// inputArgs.setShowLob("true");
		// inputArgs.setOutputFormat("sip");
		// inputArgs.setConstants();
		// try {
		// inputArgs.validateArgs();
		// } catch (Exception e) {
		//
		// }
		DataLiberator dataliberator = new DataLiberator(inputArgs);
		try {
			dataliberator.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (Constants.IS_SIP_EXTRACT) {
			String blobFolder = inputArgs.getOutputPath() + File.separator + "BLOBs";

			try {
				if (FileUtil.checkForDirectory(blobFolder))
					FileUtil.deleteDirectory(blobFolder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void start(String[] args) {
		processInputs(args, inputArgs);
		startValidateMode();
	}

	public static void processInputs(String[] args, ArchonInputBean inputArgs) {
		CmdLineParser parser = new CmdLineParser(inputArgs);
		try {
			inputArgs.setManualMode(true);
			parser.parseArgument(args);
			inputArgs.decryptor();
			inputArgs.setConstants();
			inputArgs.setAuditLevel();
			inputArgs.validateArgs();
		} catch (CmdLineException e) {
			parser.printUsage(System.err);
			System.out.println("Please check arguments specified. \n" + e.getMessage() + "\nTerminating ... ");
			System.exit(1);
		} catch (Exception e) {
			System.out.println("INFO :");
			System.out.println(e.getMessage());
		}

		try {
			inputArgs.checkQueryUde();
		} catch (Exception e) {
			System.out.println("ERROR :");
			System.out.println(e.getMessage());
			System.exit(1);
		}

		if (inputArgs.getDatabaseServer() == null) {
			System.out.println("Database server type currently not supported. Terminating");
			System.exit(1);
		}
	}

}
