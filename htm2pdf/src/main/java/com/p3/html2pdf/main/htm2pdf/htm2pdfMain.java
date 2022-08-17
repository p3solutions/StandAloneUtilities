package com.p3.html2pdf.main.htm2pdf;

import java.io.IOException;

import com.p3.html2pdf.main.process.directoryListener;

/**
 * Hello world!
 *
 */
public class htm2pdfMain {
	public static void main(String[] args) throws IOException {

		String downloadPath = args[0]; // "C:\\Users\\DELL\\Downloads";
		directoryListener.listenDirectoryForNewHTMLFile(downloadPath);
	}
}
