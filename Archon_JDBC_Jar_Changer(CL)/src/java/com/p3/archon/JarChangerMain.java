package com.p3.archon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.p3.archon.constants.SharedConstants;
import com.p3.archon.utils.PropReader;

public class JarChangerMain {
	public static void main(String[] args) {
		PropReader p = new PropReader(SharedConstants.PROP_FILE);
		SharedConstants.OJDBC_VERSION = p.getStringValue("OJDBC_VERSION", "");
		SharedConstants.ARCHON_INSTALLATION_PATH = p.getStringValue("ARCHON_PATH", "");
		String path = SharedConstants.ARCHON_INSTALLATION_PATH + File.separator + "lib" + File.separator
				+ "ojdbcDrivers";
		new File(path + File.separator + "ojdbc6.jar").setWritable(true, false);
		if (SharedConstants.OJDBC_VERSION.equalsIgnoreCase("ojdbc8")) {

			try {
				Files.copy(Paths.get(path + File.separator + "ojdbc" + File.separator + "ojdbc8.jar"),
						Paths.get(path + File.separator + "ojdbc6.jar"), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("ojdbc8 jar copied Sucessfully");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if (SharedConstants.OJDBC_VERSION.equalsIgnoreCase("ojdbc6")) {

			try {
				Files.copy(Paths.get(path + File.separator + "ojdbc" + File.separator + "ojdbc6.jar"),
						Paths.get(path + File.separator + "ojdbc6.jar"), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("ojdbc6 jar copied Sucessfully");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if (SharedConstants.OJDBC_VERSION.equalsIgnoreCase("ojdbc14")) {

			try {
				Files.copy(Paths.get(path + File.separator + "ojdbc" + File.separator + "ojdbc14.jar"),
						Paths.get(path + File.separator + "ojdbc6.jar"), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("ojdbc14 jar copied Sucessfully");
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
