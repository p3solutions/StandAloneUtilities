package com.p3.archon.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvValidationException;
import com.p3.archon.beans.ArchonInputBean;

public class StartProcess {

	private ArchonInputBean ipargs;
	StringBuffer sfb = new StringBuffer();

	String reportId = UUID.randomUUID().toString().substring(0, 8);

	public StartProcess(ArchonInputBean ipargs) {
		this.ipargs = ipargs;
	}

	public void start() {

		fileProcessor(ipargs.getInputPath());
	}

	private void fileProcessor(String string) {

		sfb.append(
				"<html><head><style> @page {  size: 18in 12in; } table {  background-color: transparent;}caption {  padding-top: 8px;  padding-bottom: 8px;  color: #777;  text-align: left;}th {  text-align: left;}.table {  width: 100%;  max-width: 100%;  margin-bottom: 20px;}.table > thead > tr > th,.table > tbody > tr > th,.table > tfoot > tr > th,.table > thead > tr > td,.table > tbody > tr > td,.table > tfoot > tr > td {  padding: 8px;  line-height: 1.42857143;  vertical-align: top;  border-top: 1px solid #ddd;}.table > thead > tr > th {  vertical-align: bottom;  border-bottom: 2px solid #ddd;}.table > caption + thead > tr:first-child > th,.table > colgroup + thead > tr:first-child > th,.table > thead:first-child > tr:first-child > th,.table > caption + thead > tr:first-child > td,.table > colgroup + thead > tr:first-child > td,.table > thead:first-child > tr:first-child > td {  border-top: 0;}.table > tbody + tbody {  border-top: 2px solid #ddd;}.table .table {  background-color: #fff;}.table-condensed > thead > tr > th,.table-condensed > tbody > tr > th,.table-condensed > tfoot > tr > th,.table-condensed > thead > tr > td,.table-condensed > tbody > tr > td,.table-condensed > tfoot > tr > td {  padding: 5px;}.table-bordered {  border: 1px solid #ddd;}.table-bordered > thead > tr > th,.table-bordered > tbody > tr > th,.table-bordered > tfoot > tr > th,.table-bordered > thead > tr > td,.table-bordered > tbody > tr > td,.table-bordered > tfoot > tr > td {  border: 1px solid #ddd;}.table-bordered > thead > tr > th,.table-bordered > thead > tr > td {  border-bottom-width: 2px;}.table-striped > tbody > tr:nth-of-type(odd) {  background-color: #f9f9f9;}.table-hover > tbody > tr:hover {  background-color: #f5f5f5;}table col[class*=\"col-\"] {  position: static;  display: table-column;  float: none;}table td[class*=\"col-\"],table th[class*=\"col-\"] {  position: static;  display: table-cell;  float: none;}.table > thead > tr > td.active,.table > tbody > tr > td.active,.table > tfoot > tr > td.active,.table > thead > tr > th.active,.table > tbody > tr > th.active,.table > tfoot > tr > th.active,.table > thead > tr.active > td,.table > tbody > tr.active > td,.table > tfoot > tr.active > td,.table > thead > tr.active > th,.table > tbody > tr.active > th,.table > tfoot > tr.active > th {  background-color: #f5f5f5;}.table-hover > tbody > tr > td.active:hover,.table-hover > tbody > tr > th.active:hover,.table-hover > tbody > tr.active:hover > td,.table-hover > tbody > tr:hover > .active,.table-hover > tbody > tr.active:hover > th {  background-color: #e8e8e8;}.table > thead > tr > td.success,.table > tbody > tr > td.success,.table > tfoot > tr > td.success,.table > thead > tr > th.success,.table > tbody > tr > th.success,.table > tfoot > tr > th.success,.table > thead > tr.success > td,.table > tbody > tr.success > td,.table > tfoot > tr.success > td,.table > thead > tr.success > th,.table > tbody > tr.success > th,.table > tfoot > tr.success > th {  background-color: #dff0d8;}.table-hover > tbody > tr > td.success:hover,.table-hover > tbody > tr > th.success:hover,.table-hover > tbody > tr.success:hover > td,.table-hover > tbody > tr:hover > .success,.table-hover > tbody > tr.success:hover > th {  background-color: #d0e9c6;}.table > thead > tr > td.info,.table > tbody > tr > td.info,.table > tfoot > tr > td.info,.table > thead > tr > th.info,.table > tbody > tr > th.info,.table > tfoot > tr > th.info,.table > thead > tr.info > td,.table > tbody > tr.info > td,.table > tfoot > tr.info > td,.table > thead > tr.info > th,.table > tbody > tr.info > th,.table > tfoot > tr.info > th {  background-color: #d9edf7;}.table-hover > tbody > tr > td.info:hover,.table-hover > tbody > tr > th.info:hover,.table-hover > tbody > tr.info:hover > td,.table-hover > tbody > tr:hover > .info,.table-hover > tbody > tr.info:hover > th {  background-color: #c4e3f3;}.table > thead > tr > td.warning,.table > tbody > tr > td.warning,.table > tfoot > tr > td.warning,.table > thead > tr > th.warning,.table > tbody > tr > th.warning,.table > tfoot > tr > th.warning,.table > thead > tr.warning > td,.table > tbody > tr.warning > td,.table > tfoot > tr.warning > td,.table > thead > tr.warning > th,.table > tbody > tr.warning > th,.table > tfoot > tr.warning > th {  background-color: #fcf8e3;}.table-hover > tbody > tr > td.warning:hover,.table-hover > tbody > tr > th.warning:hover,.table-hover > tbody > tr.warning:hover > td,.table-hover > tbody > tr:hover > .warning,.table-hover > tbody > tr.warning:hover > th {  background-color: #faf2cc;}.table > thead > tr > td.danger,.table > tbody > tr > td.danger,.table > tfoot > tr > td.danger,.table > thead > tr > th.danger,.table > tbody > tr > th.danger,.table > tfoot > tr > th.danger,.table > thead > tr.danger > td,.table > tbody > tr.danger > td,.table > tfoot > tr.danger > td,.table > thead > tr.danger > th,.table > tbody > tr.danger > th,.table > tfoot > tr.danger > th {  background-color: #f2dede;}.table-hover > tbody > tr > td.danger:hover,.table-hover > tbody > tr > th.danger:hover,.table-hover > tbody > tr.danger:hover > td,.table-hover > tbody > tr:hover > .danger,.table-hover > tbody > tr.danger:hover > th {  background-color: #ebcccc;}.table-responsive {  min-height: .01%;  overflow-x: auto;}@media screen and (max-width: 767px) {  .table-responsive {    width: 100%;    margin-bottom: 15px;    overflow-y: hidden;    -ms-overflow-style: -ms-autohiding-scrollbar;    border: 1px solid #ddd;  }  .table-responsive > .table {    margin-bottom: 0;  }  .table-responsive > .table > thead > tr > th,  .table-responsive > .table > tbody > tr > th,  .table-responsive > .table > tfoot > tr > th,  .table-responsive > .table > thead > tr > td,  .table-responsive > .table > tbody > tr > td,  .table-responsive > .table > tfoot > tr > td {    white-space: nowrap;  }  .table-responsive > .table-bordered {    border: 0;  }  .table-responsive > .table-bordered > thead > tr > th:first-child,  .table-responsive > .table-bordered > tbody > tr > th:first-child,  .table-responsive > .table-bordered > tfoot > tr > th:first-child,  .table-responsive > .table-bordered > thead > tr > td:first-child,  .table-responsive > .table-bordered > tbody > tr > td:first-child,  .table-responsive > .table-bordered > tfoot > tr > td:first-child {    border-left: 0;  }  .table-responsive > .table-bordered > thead > tr > th:last-child,  .table-responsive > .table-bordered > tbody > tr > th:last-child,  .table-responsive > .table-bordered > tfoot > tr > th:last-child,  .table-responsive > .table-bordered > thead > tr > td:last-child,  .table-responsive > .table-bordered > tbody > tr > td:last-child,  .table-responsive > .table-bordered > tfoot > tr > td:last-child {    border-right: 0;  }  .table-responsive > .table-bordered > tbody > tr:last-child > th,  .table-responsive > .table-bordered > tfoot > tr:last-child > th,  .table-responsive > .table-bordered > tbody > tr:last-child > td,  .table-responsive > .table-bordered > tfoot > tr:last-child > td {    border-bottom: 0;  }}</style></head><body>");

		sfb.append("<h1>Validation Report</h1>");

		sfb.append("<br></br><table class=\"table table-bordered table-striped\"><thead><tr>");
		sfb.append("<td colspan=\"4\" align=\"center\"><b>" + "File Name" + "</b></td>");
		sfb.append("<td colspan=\"3\" align=\"center\"><b>" + "Date" + "</b></td>");
		sfb.append("<td colspan=\"2\" align=\"center\"><b>" + "Source Count" + "</b></td>");
		sfb.append("<td colspan=\"2\" align=\"center\"><b>" + "Dest Count" + "</b></td>");
		sfb.append("<td colspan=\"2\" align=\"center\"><b>" + "Count Match" + "</b></td>");
		sfb.append("<td colspan=\"2\" align=\"center\"><b>" + "Errors" + "</b></td>");
		sfb.append("</tr></thead>");

		File[] los = new File(string).listFiles();
		for (File file : los) {

			if (file.isDirectory()) {
				fileProcessor(file.getAbsolutePath());
			} else {
				try {
					processFile(file.getAbsoluteFile());
				} catch (IOException | CsvValidationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		sfb.append("</table>");
		sfb.append("<br></br>");
		sfb.append("<br></br>");
		sfb.append("<br></br>");
		sfb.append("<br></br>");
		sfb.append("<p>Job Reference Id : " + reportId + "</p>");
		sfb.append("<p>Generated with Archon</p>");
		sfb.append("<p>Generated Date : " + new Date().toGMTString() + "</p>");
		sfb.append("<p><i><font size=\"8px\">This is a system generated report.</font></i></p>");
		sfb.append("</body></html>");
		createReport(sfb, ipargs.getOutputPath());
	}

	private void processFile(File file) throws IOException, CsvValidationException {
		char delimiter = getChar(ipargs.getDelimeter());

		RFC4180Parser parser = new RFC4180ParserBuilder().withSeparator(delimiter)
				.withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).withQuoteChar('"').build();

//		CSVParser parser = new CSVParserBuilder().withSeparator(delimiter)
//				.withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).withIgnoreLeadingWhiteSpace(true)
//				.withIgnoreQuotations(false).withQuoteChar('"').withStrictQuotes(false).build();
		String[] elements;

		BufferedReader br = Files.newBufferedReader(Paths.get(file.getAbsolutePath()),
				Charset.forName(ipargs.getEncoding()));
//		FileReader filereader = new FileReader(srcFilePath);
		CSVReader csvReader = new CSVReaderBuilder(br).withCSVParser(parser).build();
		String[] record;

		int count = 0;

		String lastline = "";
		String fileName = "";
		String date = "";

		StringBuffer sb = new StringBuffer();

		while ((record = csvReader.readNext()) != null) {
			count++;

			if (Integer.valueOf(ipargs.getNoOfColumns()) != record.length) {
				sb.append("Line No " + count + "" + " have " + record.length + " columns;");
			}

			if (count == 1) {
				fileName = record[1];
				date = record[2];

			}

			lastline = record[1];

		}

		count = count - 2;
		int destCount = Integer.valueOf(lastline);

		sfb.append("<tbody><tr>");
		sfb.append("<td colspan=\"4\" align=\"center\">" + fileName + "</td>");
		sfb.append("<td colspan=\"3\" align=\"center\">" + date + "</td>");
		sfb.append("<td colspan=\"2\" align=\"center\">" + destCount + "</td>");
		sfb.append("<td colspan=\"2\" align=\"center\">" + count + "</td>");
		sfb.append("<td colspan=\"2\" align=\"center\">" + ((count == destCount) ? "yes" : "no") + "</td>");
		sfb.append("<td colspan=\"2\" align=\"center\">");

		String[] errors = sb.toString().split(";");
		for (int i = 0; i < errors.length - 1; i++) {
			if (i == 0) {
				continue;
			}
			sfb.append("<tr>" + errors[i] + "</tr>");

		}
		sfb.append("</td>");
		sfb.append("</tr></tbody>");

		csvReader.close();
		br.close(); // closes the stream and release the resources
		System.out.println("Processed " + file.getAbsolutePath());

	}

	private char getChar(String delimiter) {
		switch (delimiter) {
		case "\\t":
		case "\t":
			return '\t';
		default:
			return delimiter.charAt(0);
		}
	}

	protected void createReport(StringBuffer sb, String path) {
		try {
			String inputfile = path + File.separator + "out.html";
			String tempfile = path + File.separator + "temp.pdf";
			String finalfile = path + File.separator + "Validation Report_" + reportId + ".pdf";
			PrintWriter writer = new PrintWriter(inputfile, "UTF-8");
			writer.println(sb.toString());
			writer.close();

			String url = new File(inputfile).toURI().toURL().toString();
			OutputStream os = new FileOutputStream(new File(tempfile));
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocument(url);
			renderer.layout();
			renderer.createPDF(os);
			os.close();

			PdfReader pdfReader = new PdfReader(tempfile);
			PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(new File(finalfile)));
			Image image = Image.getInstance("archon.png");
			image.scaleToFit(300f, 300f);
			image.setAbsolutePosition(950f, 20f);
			for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
				PdfContentByte content = pdfStamper.getUnderContent(i);
				content.addImage(image);
			}
			pdfStamper.close();
			pdfReader.close();

			FileUtils.forceDeleteOnExit(new File(inputfile));
			FileUtils.forceDeleteOnExit(new File(tempfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
