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

//		args = new String[] { "dbmode", "-jid", "944c45f0-a5a9-42d7-8371-3fe060660898", "-rid", "0", "-ja", "0" };

//		args = new String[] { "dbmode", "-jid", "7fc2c613-a95d-4a38-a8fe-83ccb47b72b6", "-rid", "0", "-ja", "0"};
//
//		args = new String[] { "manual", "-dbs", "MySQL", "-h", "localhost", "-l", "3308", "-d", "sakila", "-s",
//				"sakila", "-u", "root", "-p", "secret", "-c", "unstructured", "-o", "file", "-op", "E:\\op", "-bi", "X",
//				"-rpx", "100", "-utn", "ranitest", "-ubc", "blobcolumnname", "-ufo", "COLUMN", "-ueo", "", "-uec", "", "-uev", "",
//				"-ufc", "file_name", "-uss", "", "-ufp", "", "-v", "4", "-nt", "true", "-uvo", "false", "-uic", "",
//				"-usc", "", "-uvc", "", "-ext", "ai", "-fp", "E:\\test3","-aa","" };

//		args = new String[] { "manual", "-dbs", "SQL", "-h", "DESKTOP-LVKUBH8", "-l", "1433", "-d",
//				"demo", "-s", "dbo", "-loc", "", "-u", "sa", "-p", "secret", "-c",
//				"unstructured", "-o", "xml", "-op",
//				"C:\\Users\\91735\\Desktop\\Archon2BuildGen\\ArchonKeyBackup\\keybackup", "-bi", "X", "-rpx", "100",
//				"-utn", "TBL_CMM_ATTACHMENT_STORAGES", "-ubc", "ATTACHMENT_DATA", "-ufo", "", "-ueo", "", "-uec", "",
//				"-uev", "", "-ufc", "", "-uss", "", "-ufp", "", "-v", "4", "-nt", "true" };

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
