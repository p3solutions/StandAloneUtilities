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

//		args = new String[] { "dbmode", "-jid", "58477905-46d1-41be-af47-7e2856783fe8", "-rid", "0", "-ja", "0" };

//		args = new String[] { "dbmode", "-jid", "4ff95381-c264-47bb-8eb8-5d2d66002978", "-rid", "0", "-ja", "0"};

//		args = new String[] { "manual", "-dbs", "sql", "-h", "DESKTOP-LVKUBH8", "-l", "1433", "-d", "blobtest", "-s",
//				"dbo", "-loc", "aaa", "-u", "sa", "-p", "secret", "-c", "unstructured", "-o", "file", "-op", "E:\\test",
//				"-bi", "X", "-rpx", "100", "-utn", "PS_PY_SS_HOWTO_FLE", "-ubc", "FILEDATA", "-ufo", "COLUMN", "-ueo",
//				"", "-uec", "", "-uev", "", "-ufc", "ATTACHSYSFILENAME", "-uss", "", "-ufp", "", "-v", "4", "-nt",
//				"true", "-uvo", "true", "-uic", "ATTACHSYSFILENAME", "-usc", "FILE_SEQ", "-uvc", "VERSION" };

//		args = new String[] { "manual", "-dbs", "mysql", "-h", "localhost", "-l", "3308", "-d", "sakila", "-s",
//				"sakila", "-loc", "aaa", "-u", "root", "-p", "secret", "-c", "unstructured", "-o", "file", "-op",
//				"E:\\test", "-bi", "X", "-rpx", "100", "-utn", "blobtest", "-ubc", "BLOB_cOLUMN", "-ufo", "COLUMN",
//				"-ueo", "", "-uec", "", "-uev", "", "-ufc", "FILE_NAME", "-uss", "", "-ufp", "", "-v", "4", "-nt",
//				"true", "-aa", " ss", "-xsl",
//				"C:\\Users\\91735\\Downloads\\Sample_Xmls\\Sample_Xmls\\SOAPware_DocumentItems_Billing\\SOAPware_DocumentItems_Billing.xsl" };

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
