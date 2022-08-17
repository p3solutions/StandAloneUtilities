package com.p3.archon.coreprocess;

import java.util.Arrays;
import java.util.Date;

import com.p3.archon.commonfunctions.CommonFunctions;
import com.p3.archon.core.ArchonDBUtils;
import com.p3.archon.coreprocess.jobmode.DBMode;
import com.p3.archon.coreprocess.jobmode.ManualMode;
import com.p3.archon.dboperations.dbmodel.enums.RunMode;
import com.p3.archon.fun.AsciiGen;

public class ArchonCore {

	public static void main(String[] args) {

//		args = new String[] { "dbmode", "-jid", "66347642-1133-4add-825b-938e41be92d9", "-rid", "0", "-ja", "0" };

//		args = new String[] { "dbmode", "-jid", "7fc2c613-a95d-4a38-a8fe-83ccb47b72b6", "-rid", "0", "-ja", "0"};
//
//		args = new String[] { "manual", "-dbs", "SQL", "-h", "dba-archive.fairview.org", "-l", "1433", "-d",
//				"RL6-GICH-Archive", "-s", "dbo", "-u", "InfoArchive", "-p", "7Wuy2!b9", "-c", "unstructured", "-o",
//				"file", "-op", "C:\\ianas\\Applications\\Wave3\\RLSolutions_GICH\\UnStructured\\Test\\", "-bi", "X",
//				"-rpx", "100", "-utn", "TBL_CMM_ATTACHMENT_STORAGES", "-ubc", "ATTACHMENT_DATA", "-ufo", "COLUMN", "-ueo", "",
//				"-uec", "", "-uev", "", "-ufc", "ITEM_ID", "-uss", "", "-ufp", "", "-v", "4", "-nt", "true", "-uvo", "false",
//				"-uic", "", "-usc", "", "-uvc", "" };

//		args = new String[] { "manual", "-dbs", "MySQL", "-h", "localhost", "-l", "3308", "-d", "sakila", "-s",
//				"sakila", "-u", "root", "-p", "secret", "-c", "unstructured", "-o", "file", "-op", "E:\\op\\", "-bi",
//				"X", "-rpx", "100", "-utn", "staff", "-ubc", "picture", "-ufo", "COLUMN", "-ueo", "", "-uec", "",
//				"-uev", "", "-ufc", "staff_id", "-uss", "", "-ufp", "", "-v", "4", "-nt", "true", "-tn",
//				"TOOL_RDBMS_EXTRACTOR_NAME", "-uic", "", "-usc", "", "-uvc", "", "-uvo", "false", "-aa", " " };

		AsciiGen.start();
		if (args.length == 0) {
			System.err.println("No arguments specified.\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(1);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				CommonFunctions.deleteTempDir();
			}
		}));

		try {
			switch (RunMode.getMode(args[0])) {
			case DB:
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					public void run() {
						ArchonDBUtils.closeSessionFactory();
					}
				}));
				DBMode db = new DBMode(args);
				db.startValidateMode();
				break;
			case MANUAL:
				ManualMode man = new ManualMode();
				man.start((String[]) Arrays.copyOfRange(args, 1, args.length));
				break;
			default:
				System.err.println("Run mode was invalid. Possible Value are\n - DBMODE\n - MANUAL\nTerminating ... ");
				System.out.println("Job Terminated = " + new Date());
				System.exit(2);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Exception occured during exceution." + e.getMessage() + "\nTerminating ... ");
			System.out.println("Job Terminated = " + new Date());
			System.exit(3);
		}
		System.exit(0);
	}

}
