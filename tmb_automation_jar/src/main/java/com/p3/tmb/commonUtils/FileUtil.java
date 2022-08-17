package com.p3.tmb.commonUtils;

import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.constant.INGESTER_CONSTANT;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
	
	public static boolean checkForDirectory(String fileDir) throws Exception {
		File f;
		try {
			// check for file existing
			f = new File(fileDir);
			return f.isDirectory();
		} finally {
			f = null;
		}
	}
	
	public static String removeFileNameFromLinuxPath(String path) {
		String resPath=null;
		try {
			if(path.contains("/")) {
				resPath = path.substring(0,path.lastIndexOf("/"));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		return resPath;
	}

	public static boolean checkForFile(String fileDir) throws Exception {
		File f;
		try {
			// check for file existing
			f = new File(fileDir);
			return f.isFile();
		} finally {
			f = null;
		}
	}

	public static String getFileNameFromPath(String fullFileName) {
		File f = new File(fullFileName);
		String fname = f.getName();
		f = null;
		return fname;
	}
	
	public static String getFileNameFromLinuxPath(String path) {
		String filePath = path;
		if(filePath.contains("/")) {
			int lastIndex = filePath.lastIndexOf("/")+1;
			if(lastIndex<=filePath.length()) {
        		return filePath.substring(lastIndex,filePath.length());
			}
		}
		return null;
	}

	public static void checkCreateDirectory(String fileDir) throws Exception {
		System.out.println("CreateDirectory = " + fileDir);
		if (!checkForDirectory(fileDir))
			createDir(fileDir);
	}

	public static File createDir(File dir, String name) throws IOException {
		return createDir(dir.getAbsolutePath() + File.separator + name);
	}

	public static File createDir(String dir, String name) throws IOException {
		return createDir(dir + File.separator + name);
	}

	public static File createDir(String dir) throws IOException {
		File tmpDir = new File(dir);
		if (!tmpDir.exists()) {
			if (!tmpDir.mkdirs()) {
				throw new IOException("Could not create temporary directory: " + tmpDir.getAbsolutePath());
			}
		} else {
			// System.out.println("Not creating directory, " + dir + ", this directory
			// already exists.");
		}
		return tmpDir;
	}

	public static boolean movefile(String filetoMove, String destinationFilePath, boolean haltIfFail) {
		// File (or directory) to be moved
		File file = new File(filetoMove);

		// Destination directory
		File dir = new File(destinationFilePath);

		// Move file to new directory
		boolean success = file.renameTo(new File(dir, file.getName()));
		if (!success) {
			System.err.println("The file " + filetoMove + " was not successfully moved");
			if (haltIfFail)
				System.exit(1);
		}
		return success;
	}

	/**
	 * Delete the target directory and its contents.
	 *
	 * @param strTargetDir Target directory to be deleted.
	 * @return <code>true</code> if all deletions successful, <code>false> otherwise
	 */
	public static boolean deleteDirectory(String strTargetDir) {
		File fTargetDir = new File(strTargetDir);
		if (fTargetDir.exists() && fTargetDir.isDirectory()) {
			return deleteDirectory(fTargetDir);
		} else {
			return false;
		}
	}

	/**
	 * Delete the target directory and its contents.
	 *
	 * @param dir Target directory to be deleted.
	 * @return <code>true</code> if all deletions successful, <code>false> otherwise
	 */
	public static boolean deleteDirectory(File dir) {
		if (dir == null)
			return true;
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String element : children) {
				boolean success = deleteDirectory(new File(dir, element));
				if (!success) {
					System.err.println("Unable to delete file: " + new File(dir, element));
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	/**
	 * deleteFile
	 *
	 * @param filePath the file path
	 * @exception Exception an error has occured
	 */
	public static void deleteFile(String filePath) {
		File f;
		try {
			// check for file existing
			f = new File(filePath);
			if (f.isFile()) {
				f.delete();
			}
		} finally {
			f = null;
		}
	}

	/**
	 * write file
	 *
	 * @param filePath the file path
	 *
	 * @exception Exception an error has occured
	 */
	public static void writeFile(String filePath, String txtToWrite) throws IOException {
		Writer out = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
		try {
			out.write(txtToWrite);
		} finally {
			out.close();
		}
	}

	public static void writeFileAppend(String filePath, String txtToWrite) throws IOException {
		Writer out = new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8");
		try {
			out.write(txtToWrite);
		} finally {
			out.close();
		}
	}
	
	public static String removeExtension(String fileName) {
		String file = "";
		try {
			if (fileName.contains("."))
				file = fileName.substring(0, fileName.lastIndexOf("."));
		} catch (Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		return file;
	}
	
	public static String getExtension(String fileName) {
		String fileExtension = "";
		try {
			if(fileName.contains("."))
			fileExtension += fileName.substring(fileName.lastIndexOf('.'),fileName.length());
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
		}
		return fileExtension;
	}
	
	public static String createTempFolder() {
		String path = "";
		try {
			Path tempDirWithPrefix = Files.createTempDirectory(INGESTER_CONSTANT.INGESTER_TEMP_DIRECTORY_PREFIX);
			path = tempDirWithPrefix.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}
	
	public static boolean createLogFile(StringBuffer inputBuffer, String outputPath ) {
		
		try {
			if(createFile(outputPath)) {
				FileWriter fw = new FileWriter(outputPath);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(inputBuffer.toString());
				bw.close();
				fw.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
		return true;
	}
	
	public static boolean createFile(String inputPath) {
		
		try {
			File file = new File(inputPath);
			if(!file.exists()) {
				file.createNewFile();
			}
			else {
				return false;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			CommonSharedConstants.logContent.append(commonUtils.exceptionMsgToString(e) + CommonSharedConstants.newLine);
			return false;
		}
		return true;
	}

//	public static void main(String[] args) {
//		String deletePath = "/users/EAS_TMB/Fleet card/ReconcileReport/26032022132303";
//		System.out.println(removeFileNameFromLinuxPath(deletePath));
//	}
}
