package com.p3.archon.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.p3.archon.beans.ArchonInputBean;

public class StartProcess {

	private ArchonInputBean ipargs;

	public StartProcess(ArchonInputBean ipargs) {
		this.ipargs = ipargs;
	}

	public void start() {

		fileProcessor(ipargs.getInputPath());
	}

	private void fileProcessor(String string) {

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
		FileInputStream fs = new FileInputStream(file.getAbsolutePath());
		InputStreamReader isr = new InputStreamReader(fs);
		BufferedReader br = new BufferedReader(isr);
		String line;
		StringBuffer sfb = new StringBuffer();

		System.out.println("Processing " + file.getAbsolutePath());
		sfb.append(
				"<html><head><style> @page {  size: 18in 12in; } body {font-family:\"Courier\"; font-size:1em }p { white-space: pre;}</style></head><body><p>");

		while ((line = br.readLine()) != null) {
			line = line.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
			sfb.append(line).append("\n");
		}
		sfb.append("</p></body></html>");

		fs.close();
		isr.close();
		br.close(); // closes the stream and release the resources
		System.out.println("Processed " + file.getAbsolutePath());
		createReport(sfb, ipargs.getOutputPath(), file.getName());

	}

	protected void createReport(StringBuffer sb, String path, String fileName) {
		try {
			String inputfile = path + File.separator + "out.html";
			String tempfile = path + File.separator + "temp.pdf";
			String finalfile = path + File.separator + FilenameUtils.removeExtension(fileName) + ".pdf";
			PrintWriter writer = new PrintWriter(inputfile, "UTF-8");
			writer.println(sb.toString());
			writer.close();

			String url = new File(inputfile).toURI().toURL().toString();
			OutputStream os = new FileOutputStream(new File(tempfile));
			ITextRenderer renderer = new ITextRenderer();
			renderer.getFontResolver().addFont("/Courier.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			renderer.setDocument(url);
			renderer.layout();
			renderer.createPDF(os);
			os.close();

			PdfReader pdfReader = new PdfReader(tempfile);
			PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(new File(finalfile)));
			pdfStamper.close();
			pdfReader.close();

			FileUtils.forceDeleteOnExit(new File(inputfile));
			FileUtils.forceDeleteOnExit(new File(tempfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
