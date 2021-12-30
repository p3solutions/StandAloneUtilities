package com.p3.archon.process;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;

import com.p3.archon.beans.ArchonInputBean;

public class StartProcess {

	private ArchonInputBean ipargs;
	File schemafile = null;

	public StartProcess(ArchonInputBean ipargs) {
		this.ipargs = ipargs;
	}

	public void start() {

		try {
			fileProcessor(ipargs.getInputPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void fileProcessor(String string) throws IOException {

		schemafile = new File(ipargs.getOutputPath() + File.separator + "dbo" + File.separator);

		try {
			Files.createDirectory(Paths.get(schemafile.getAbsolutePath()));
		} catch (FileAlreadyExistsException e) {
			// TODO: handle exception
		}

		File[] los = new File(string).listFiles();
		for (File file : los) {

			if (file.isDirectory()) {
				fileProcessor(file.getAbsolutePath());
			} else {
				try {
					processFile(file.getAbsoluteFile());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	private void processFile(File file) throws IOException {

		System.out.println("Processing -> " + file.getName());
		File tableFile = new File(schemafile.getAbsolutePath() + File.separator
				+ FilenameUtils.removeExtension(file.getName()) + File.separator);
		try {
			Files.createDirectory(Paths.get(tableFile.getAbsolutePath()));
		} catch (FileAlreadyExistsException e) {
			// TODO: handle exception
		}
		Files.copy(Paths.get(file.getAbsolutePath()),
				Paths.get(tableFile.getAbsolutePath() + File.separator + file.getName()),
				StandardCopyOption.REPLACE_EXISTING);

		System.out.println("Processing Completed -> " + file.getName());

	}

}
