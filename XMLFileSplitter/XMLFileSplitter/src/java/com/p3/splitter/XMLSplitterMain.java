package com.p3.splitter;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.p3.splitter.utils.DirectoryWalker;
import com.p3.splitter.utils.FileUtil;
import com.p3.splitter.utils.GeneralUtil;

/**
 * Class: XMLSplitterMain
 *
 * This class is used for main method to run asynchronous report jobs
 * 
 * @author Malik
 * @version 1.0
 *
 */
public class XMLSplitterMain {

	private static final String XML_FILE = "XML";

	public static void main(String[] args) throws Exception {

		// hardcoding
		// args = new String[] {
		// "/Users/malik/Documents/Work/Workspaces/Archon_Workspace/XMLFileSplitter/b",
		// "b1", "4",
		// "2", "false" };

		if (args.length < 4) {
			System.err.println("usage : java com.p3.splitter.XMLSplitterMain"
					+ " <dir to XML file for splitting> <dir to save> <size to split in MB> <row position (1 or 2)> <[optional] delete_source_file true_or_false>");
			System.exit(1);
		}

		String path;
		String savepath;
		long splitsize = 99;
		int rowpos = 1;
		boolean deleteSrc = false;

		path = args[0];
		if (!FileUtil.checkForDirectory(path)) {
			System.err.println("The Directory does not exist");
			System.exit(1);
		}

		savepath = args[1];
		FileUtil.checkCreateDirectory(savepath);

		try {
			splitsize = Long.parseLong(args[2]);
		} catch (Exception e) {
			System.out.println("splitsize value is invalid. Setting default value of 100 MB");
		}

		try {
			rowpos = Integer.parseInt(args[3]);
			if (rowpos != 1 && rowpos != 2) {
				System.err.println("Please check row position. Acceptable values 1 or 2");
				System.exit(1);
			}
		} catch (NumberFormatException e) {
			System.err.println("Please check row position. Acceptable values 1 or 2");
			System.exit(1);
		}

		try {
			deleteSrc = args[4].equalsIgnoreCase("true") ? true : false;
		} catch (ArrayIndexOutOfBoundsException e) {
			deleteSrc = false;
		}

		File fpath = new File(path);
		System.out.println("Importer Directory = " + path);
		DirectoryWalker walker = new DirectoryWalker();
		List<String> fileList = walker.walk(fpath.getAbsolutePath());
		Collections.sort(fileList);
		System.out.println("Sorted List = " + fileList);

		for (String file : fileList) {
			if (!XML_FILE.equalsIgnoreCase(GeneralUtil.getFileExtension(file))) {
				continue;
			}
			System.out.println(
					"---------------------------------------------------------------------------------------------");
			try {
				System.out.println("Splitting XML file : " + FileUtil.getFileNameFromPath(file) + " STARTED");
				SplitXMLFile instance = new SplitXMLFile(file, savepath, splitsize, rowpos);
				instance.startSplit();
				System.out.println("Splitting XML file : " + FileUtil.getFileNameFromPath(file) + " ENDED");
			} catch (Exception e) {
				System.out.println(e.getMessage().replace(",", ":").replace("\n", " -- "));
			}
			System.out.println(
					"---------------------------------------------------------------------------------------------");
			if (deleteSrc)
				try {
					FileUtil.deleteFile(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
	}
}
