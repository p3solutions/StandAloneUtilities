package com.p3.tmb.commonUtils;

import com.p3.tmb.constant.CommonSharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class zipUtils {

	final Logger log = LogManager.getLogger(zipUtils.class.getName());

	String inputPath;
	String outputFileName;
	String outputPath;
	public zipUtils(String inputPath) {
		this.inputPath = inputPath + File.separator + CommonSharedConstants.INPUT_BACKUP_DIRECTORY;
		this.outputFileName = new File(inputPath).getName();
		this.outputPath = inputPath+File.separator+outputFileName+".zip";
	}
	    private static final int BUFFER_SIZE = 4096;
	    public void zip(List<File> listFiles, String destZipFile) throws FileNotFoundException,
	            IOException {
	        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
	        log.info("Started Fetching files");
	        for (File file : listFiles) {
	            if (file.isDirectory()) {
	            	log.info("Processing Directory: "+file.getName());
	                zipDirectory(file, file.getName(), zos);
	            } else {
	            	log.info("Processing File: "+file.getName());
	                zipFile(file, zos);
	            }
	        }
	        zos.flush();
	        zos.close();
	    }
	   
	    public String startZip() throws FileNotFoundException, IOException {
	        List<File> listFiles = new ArrayList<File>();
	       File[] inputFiles = new File(inputPath).listFiles(); 
	        for (File file: inputFiles) {
	            listFiles.add(file);
	        }
	        
	        zip(listFiles, outputPath);
	        return outputPath;
	    }
	    private void zipDirectory(File folder, String parentFolder,
	            ZipOutputStream zos) throws FileNotFoundException, IOException {
	    	BufferedInputStream bis = null;
	        for (File file : folder.listFiles()) {
	            if (file.isDirectory()) {
	                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
	                continue;
	            }
	            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
	            bis = new BufferedInputStream(
	                    new FileInputStream(file));
	            long bytesRead = 0;
	            byte[] bytesIn = new byte[BUFFER_SIZE];
	            int read = 0;
	            while ((read = bis.read(bytesIn)) != -1) {
	                zos.write(bytesIn, 0, read);
	                bytesRead += read;
	            }
	            zos.closeEntry();
	        }
	        bis.close();
	        
	        log.info(folder.getName()+" directory Processed.");

	        
	    }
	    private void zipFile(File file, ZipOutputStream zos)
	            throws FileNotFoundException, IOException {
	    	BufferedInputStream bis = null;
	        zos.putNextEntry(new ZipEntry(file.getName()));
	        bis = new BufferedInputStream(new FileInputStream(
	                file));
	        long bytesRead = 0;
	        byte[] bytesIn = new byte[BUFFER_SIZE];
	        int read = 0;
	        while ((read = bis.read(bytesIn)) != -1) {
	            zos.write(bytesIn, 0, read);
	            bytesRead += read;
	        }
	        zos.closeEntry();
	        bis.close();
	        log.info(file.getName()+" File Processed.");
	    }
	    }
