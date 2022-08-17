package com.p3.html2pdf.main.process;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

public class directoryListener {

	public static void listenDirectoryForNewHTMLFile(String inputPath) {

		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(inputPath);
			path.register(watchService, ENTRY_CREATE);
			boolean poll = true;
			while (poll) {
				WatchKey key = watchService.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					System.out.println("Event kind : " + event.kind() + " - File : " + event.context());
					if (checkExportFile(event.context().toString()))
						convertHtmltoPdf(event.context().toString(), inputPath);
				}
				poll = key.reset();
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean checkExportFile(String fileName) {

		boolean value = false;

		try {
			String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
			if (extension.equalsIgnoreCase("html") && fileName.startsWith("PDF"))
				value = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	public static void convertHtmltoPdf(String fileName, String filePath) throws IOException {

		PdfDocument pdfDocument = new PdfDocument(
				new PdfWriter(filePath + File.separator + fileName.replaceAll(".html", "") + ".pdf"));
		ConverterProperties properties = new ConverterProperties();
		// pdfDocument.setDefaultPageSize(PageSize.A4.rotate());
		HtmlConverter.convertToPdf(new FileInputStream(filePath + File.separator + fileName), pdfDocument, properties);
		pdfDocument.close();
	}

}
