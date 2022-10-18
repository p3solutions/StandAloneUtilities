package com.p3.tmb.report;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.p3.tmb.beans.propertyBean;
import com.p3.tmb.beans.sftpBean;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.constant.CommonSharedConstants;
import com.p3.tmb.sftp.sftpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class extractionReportGenerator {

	private StringBuffer sb_DE = new StringBuffer();
	private propertyBean propBean;
	private sftpBean sftpBean = null;
	sftpUtils sftpUtils = null;
	final Logger log = LogManager.getLogger(extractionReportGenerator.class.getName());
	
	public extractionReportGenerator(propertyBean propBean, sftpBean sftpBean) {
	 this.propBean = propBean;
	 this.sftpBean = sftpBean;
	 this.sftpUtils = new sftpUtils(sftpBean, propBean);
	}
	
	public String generateValidationReport(int srcCount, int actSrcCount, int blobCount, int missingBlobCount, List<String> missingBlobFiles) {
		String pdfPath = null;
		
		try {
			setReportID();
			sb_DE = createHeader(sb_DE, "Text File Validation Summary", true);
			sb_DE.append("<h2>File Validation Info:</h2>");
			sb_DE = createValidationTable(sb_DE, new Object[] { "FILE NAME", "SOURCE BLOB COUNT", "MISSING BLOB COUNT","SOURCE COUNT", "ACTUAL COUNT", "COUNT MATCH"}, true);
			sb_DE = createValidationData(sb_DE, new String[]{ 
					FileUtil.getFileNameFromLinuxPath(propBean.getTextFilePath()), 
					String.valueOf(srcCount), 
					String.valueOf(actSrcCount), 
					String.valueOf(blobCount),
					String.valueOf(missingBlobCount),
			});
			sb_DE = createValidationTable(sb_DE, null, false);
			sb_DE.append("<h2>MISSING BLOB FILE INFO:</h2>");
			createValidationMissingBlobFile(sb_DE, missingBlobFiles, FileUtil.getFileNameFromLinuxPath(propBean.getTextFilePath()));
		} catch (Exception e) {
			log.error("Exception while generating validation report.." + e.getMessage());
			e.printStackTrace();
		} finally {
			sb_DE = createFooter(sb_DE, new Object[] { timeDiff(new Date().getTime() - CommonSharedConstants.jobStartTime) },
				CommonSharedConstants.REPORT_ID);
			pdfPath = createReport(sb_DE, propBean.getOutputLocation(), CommonSharedConstants.REPORT_ID, "File_Validation Report_");
		
		}
		return pdfPath;
	}
	
	public void generateDataExtractionReport() {
		try {
			sb_DE = new StringBuffer();
			setReportID();
			sb_DE = createHeader(sb_DE, "Data Extraction Summary", true);
			sb_DE.append("<h2>Files Processed Info:</h2>");
			sb_DE = createTable(sb_DE,
					new Object[] { "FILE COUNT", "SCHEMA", "TABLE", "SOURCE FILE", "SOURCE RECORD COUNT",
							"DESTINATION RECORD COUNT", "COUNTS MATCH", "START TIME", "END TIME",
							"TOTAL PROCESSING TIME", "STATUS" },
					true);
			
			createData(sb_DE, new String[]{
					String.valueOf(1),
					propBean.getSchemaName(),
					propBean.getTableName(),
					new File(propBean.getTextFilePath()).getName(),
					String.valueOf(CommonSharedConstants.srcRowCount),
					String.valueOf(CommonSharedConstants.desRowCount),
	                 CommonSharedConstants.jobStartDateTime,
                              CommonSharedConstants.jobEndDateTime,
					String.valueOf(timeDiff(CommonSharedConstants.jobEndTime-CommonSharedConstants.jobStartTime)),	
			});
			sb_DE=createTable(sb_DE, null, false);
			sb_DE.append("<h2>Blob Extraction Info:</h2>");
			sb_DE = createBlobTable(sb_DE,
					new Object[] { "COLUMN COUNT", "TABLE_NAME", "SOURCE_COUNT", "DESTINATION_COUNT" }, true);
			sb_DE = createBlobData(sb_DE, 1);
			sb_DE=createBlobTable(sb_DE, null, false);
		} catch (Exception e) {
			log.error("Exception while generating extraction report.." + e.getMessage());
			e.printStackTrace();
		} finally {
			sb_DE = createFooter(sb_DE, new Object[] { timeDiff(new Date().getTime() - CommonSharedConstants.jobStartTime) },
				CommonSharedConstants.REPORT_ID);
			createReport(sb_DE, propBean.getOutputLocation(), CommonSharedConstants.REPORT_ID, "Data_Extraction Report_");
		}

	}

	private StringBuffer createHeader(StringBuffer sb, String headerMsg, boolean isDataExtraction) {
		StringBuffer sfb = new StringBuffer();
		sfb.append("<html><head><style> @page {  size: 18in 12in; } table {  background-color: transparent;}capt"
				+ "ion {  padding-top: 8px;  padding-bottom: 8px;  color: #777;  text-align: left;}th {  text-align: left;}.table {  width: 100%;  max-width: 100%;  margin-bottom: 20px;}.table > thead > tr > th,.table > tbody > tr > th,.table > tfoot > tr > th,.table > thead > tr > td,.table > tbody > tr > td,.table > tfoot > tr > td { font-family: "
				+ "\"Arial Unicode MS\", FreeSans"
				+ "; padding: 8px;  line-height: 1.42857143;  vertical-align: top;  border-top: 1px solid #ddd;}.table > thead > tr > th {  vertical-align: bottom;  border-bottom: 2px solid #ddd;}.table > caption + thead > tr:first-child > th,.table > colgroup + thead > tr:first-child > th,.table > thead:first-child > tr:first-child > th,.table > caption + thead > tr:first-child > td,.table > colgroup + thead > tr:first-child > td,.table > thead:first-child > tr:first-child > td {  border-top: 0;}.table > tbody + tbody {  border-top: 2px solid #ddd;}.table .table {  background-color: #fff;}.table-condensed > thead > tr > th,.table-condensed > tbody > tr > th,.table-condensed > tfoot > tr > th,.table-condensed > thead > tr > td,.table-condensed > tbody > tr > td,.table-condensed > tfoot > tr > td {  padding: 5px;}.table-bordered {  border: 1px solid #ddd;}.table-bordered > thead > tr > th,.table-bordered > tbody > tr > th,.table-bordered > tfoot > tr > th,.table-bordered > thead > tr > td,.table-bordered > tbody > tr > td,.table-bordered > tfoot > tr > td {  border: 1px solid #ddd;}.table-bordered > thead > tr > th,.table-bordered > thead > tr > td {  border-bottom-width: 2px;}.table-striped > tbody > tr:nth-of-type(odd) {  background-color: #f9f9f9;}.table-hover > tbody > tr:hover {  background-color: #f5f5f5;}table col[class*=\"col-\"] {  position: static;  display: table-column;  float: none;}table td[class*=\"col-\"],table th[class*=\"col-\"] {  position: static;  display: table-cell;  float: none;}.table > thead > tr > td.active,.table > tbody > tr > td.active,.table > tfoot > tr > td.active,.table > thead > tr > th.active,.table > tbody > tr > th.active,.table > tfoot > tr > th.active,.table > thead > tr.active > td,.table > tbody > tr.active > td,.table > tfoot > tr.active > td,.table > thead > tr.active > th,.table > tbody > tr.active > th,.table > tfoot > tr.active > th {  background-color: #f5f5f5;}.table-hover > tbody > tr > td.active:hover,.table-hover > tbody > tr > th.active:hover,.table-hover > tbody > tr.active:hover > td,.table-hover > tbody > tr:hover > .active,.table-hover > tbody > tr.active:hover > th {  background-color: #e8e8e8;}.table > thead > tr > td.success,.table > tbody > tr > td.success,.table > tfoot > tr > td.success,.table > thead > tr > th.success,.table > tbody > tr > th.success,.table > tfoot > tr > th.success,.table > thead > tr.success > td,.table > tbody > tr.success > td,.table > tfoot > tr.success > td,.table > thead > tr.success > th,.table > tbody > tr.success > th,.table > tfoot > tr.success > th {  background-color: #dff0d8;}.table-hover > tbody > tr > td.success:hover,.table-hover > tbody > tr > th.success:hover,.table-hover > tbody > tr.success:hover > td,.table-hover > tbody > tr:hover > .success,.table-hover > tbody > tr.success:hover > th {  background-color: #d0e9c6;}.table > thead > tr > td.info,.table > tbody > tr > td.info,.table > tfoot > tr > td.info,.table > thead > tr > th.info,.table > tbody > tr > th.info,.table > tfoot > tr > th.info,.table > thead > tr.info > td,.table > tbody > tr.info > td,.table > tfoot > tr.info > td,.table > thead > tr.info > th,.table > tbody > tr.info > th,.table > tfoot > tr.info > th {  background-color: #d9edf7;}.table-hover > tbody > tr > td.info:hover,.table-hover > tbody > tr > th.info:hover,.table-hover > tbody > tr.info:hover > td,.table-hover > tbody > tr:hover > .info,.table-hover > tbody > tr.info:hover > th {  background-color: #c4e3f3;}.table > thead > tr > td.warning,.table > tbody > tr > td.warning,.table > tfoot > tr > td.warning,.table > thead > tr > th.warning,.table > tbody > tr > th.warning,.table > tfoot > tr > th.warning,.table > thead > tr.warning > td,.table > tbody > tr.warning > td,.table > tfoot > tr.warning > td,.table > thead > tr.warning > th,.table > tbody > tr.warning > th,.table > tfoot > tr.warning > th {  background-color: #fcf8e3;}.table-hover > tbody > tr > td.warning:hover,.table-hover > tbody > tr > th.warning:hover,.table-hover > tbody > tr.warning:hover > td,.table-hover > tbody > tr:hover > .warning,.table-hover > tbody > tr.warning:hover > th {  background-color: #faf2cc;}.table > thead > tr > td.danger,.table > tbody > tr > td.danger,.table > tfoot > tr > td.danger,.table > thead > tr > th.danger,.table > tbody > tr > th.danger,.table > tfoot > tr > th.danger,.table > thead > tr.danger > td,.table > tbody > tr.danger > td,.table > tfoot > tr.danger > td,.table > thead > tr.danger > th,.table > tbody > tr.danger > th,.table > tfoot > tr.danger > th {  background-color: #f2dede;}.table-hover > tbody > tr > td.danger:hover,.table-hover > tbody > tr > th.danger:hover,.table-hover > tbody > tr.danger:hover > td,.table-hover > tbody > tr:hover > .danger,.table-hover > tbody > tr.danger:hover > th {  background-color: #ebcccc;}.table-responsive {  min-height: .01%;  overflow-x: auto;}@media screen and (max-width: 767px) {  .table-responsive {    width: 100%;    margin-bottom: 15px;    overflow-y: hidden;    -ms-overflow-style: -ms-autohiding-scrollbar;    border: 1px solid #ddd;  }  .table-responsive > .table {    margin-bottom: 0;  }  .table-responsive > .table > thead > tr > th,  .table-responsive > .table > tbody > tr > th,  .table-responsive > .table > tfoot > tr > th,  .table-responsive > .table > thead > tr > td,  .table-responsive > .table > tbody > tr > td,  .table-responsive > .table > tfoot > tr > td {    white-space: nowrap;  }  .table-responsive > .table-bordered {    border: 0;  }  .table-responsive > .table-bordered > thead > tr > th:first-child,  .table-responsive > .table-bordered > tbody > tr > th:first-child,  .table-responsive > .table-bordered > tfoot > tr > th:first-child,  .table-responsive > .table-bordered > thead > tr > td:first-child,  .table-responsive > .table-bordered > tbody > tr > td:first-child,  .table-responsive > .table-bordered > tfoot > tr > td:first-child {    border-left: 0;  }  .table-responsive > .table-bordered > thead > tr > th:last-child,  .table-responsive > .table-bordered > tbody > tr > th:last-child,  .table-responsive > .table-bordered > tfoot > tr > th:last-child,  .table-responsive > .table-bordered > thead > tr > td:last-child,  .table-responsive > .table-bordered > tbody > tr > td:last-child,  .table-responsive > .table-bordered > tfoot > tr > td:last-child {    border-right: 0;  }  .table-responsive > .table-bordered > tbody > tr:last-child > th,  .table-responsive > .table-bordered > tfoot > tr:last-child > th,  .table-responsive > .table-bordered > tbody > tr:last-child > td,  .table-responsive > .table-bordered > tfoot > tr:last-child > td {    border-bottom: 0;  }}</style></head><body>");

		sfb.append("<center><h1><b>"+ headerMsg +"</b></h1><br/></center>");
		
		return sfb;
	}
	
	private StringBuffer createTable(StringBuffer sb, Object[] strings, boolean flag) {
		if (flag) {
			sb.append("<br></br><table class=\"table table-bordered table-striped\"><thead><tr>");
			sb.append("<td colspan=\"3\" align=\"center\"><b>" + strings[0] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[1] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[2] + "</b></td>");
		    //sb.append("<td colspan=\"1\" align=\"center\"><b>" + strings[3] +
			 //"</b></td>");
			// sb.append("<td colspan=\"1\" align=\"center\"><b>" + strings[4] +
			// "</b></td>");
			sb.append("<td colspan=\"1\" align=\"center\"><b>" + strings[4] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[5] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[6] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[7] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[8] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[9] + "</b></td>");
			sb.append("</tr></thead>");
		} else {
			sb.append("</table>");
		}
		return sb;
	}
	
	private StringBuffer createValidationTable(StringBuffer sb, Object[] strings, boolean flag) {
		if (flag) {
			sb.append("<br></br><table class=\"table table-bordered table-striped\"><thead><tr>");
			sb.append("<td colspan=\"3\" align=\"center\"><b>" + strings[0] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[1] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[2] + "</b></td>");
			
		    //sb.append("<td colspan=\"1\" align=\"center\"><b>" + strings[3] +
			 //"</b></td>");
			// sb.append("<td colspan=\"1\" align=\"center\"><b>" + strings[4] +
			// "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[3] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[4] + "</b></td>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[5] + "</b></td>");
			sb.append("</tr></thead>");
		} else {
			sb.append("</table>");
		}
		return sb;
	}
	
	private StringBuffer createValidationMissingBlobFile(StringBuffer sb, List<String> missingBlobFiles, String textFileName) {
		
			sb.append("<br></br><table class=\"table table-bordered table-striped\"><thead><tr>");
			sb.append("<td colspan=\"1\" align=\"center\"><b> S.NO. </b></td>");
			sb.append("<td colspan=\"3\" align=\"center\"><b> TEXT FILE NAME </b></td>");
			sb.append("<td colspan=\"3\" align=\"center\"><b> MISSING BLOB FILE NAME </b></td>");
			sb.append("</tr></thead>");
			sb.append("<tbody>");
			int count = 1;
			for (String missingBlobFileName : missingBlobFiles) {
				sb.append("<tr>");
				sb.append("<td colspan=\"1\" align=\"center\">" + (count++) + "</td>");
				sb.append("<td colspan=\"3\" align=\"center\">" + textFileName + "</td>");
			sb.append("<td colspan=\"3\" align=\"center\">" + missingBlobFileName + "</td>");
			sb.append("</tr>");
			}
			sb.append("</tbody>");
			sb.append("</table>");
		
		return sb;
	}

	private static StringBuffer createData(StringBuffer sb, String[] strings) {
		sb.append("<tbody><tr>");
		sb.append("<td colspan=\"3\" align=\"center\">" + strings[0] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[1] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[2] + "</td>");
		// sb.append("<td colspan=\"1\" align=\"center\"><b>" + strings[3] +
		// "</td>");
		// sb.append("<td colspan=\"1\" align=\"center\"><b>" + strings[4] +
		// "</td>");
		//sb.append("<td colspan=\"1\" align=\"center\">" + strings[3] + "</td>");
		sb.append("<td colspan=\"1\" align=\"center\">" + strings[4] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[5] + "</td>");

		boolean recordCountMatch = Integer.parseInt(strings[4]) == Integer.parseInt(strings[5]);
		String status = "FAILED";
		if (recordCountMatch) {
			status = "SUCCESS";
			sb.append("<td colspan=\"2\" align=\"center\"><font color=\"green\">YES</font></td>");
		}
		else
			sb.append("<td colspan=\"2\" align=\"center\"><font color=\"red\">NO</font></td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[6] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[7] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[8] + "</td>");
		//sb.append("<td colspan=\"2\" align=\"center\">" + status + "</td>");
		sb.append("</tr></tbody>");
		return sb;
	}
	
	private static StringBuffer createValidationData(StringBuffer sb, String[] strings) {
		sb.append("<tbody><tr>");
		sb.append("<td colspan=\"3\" align=\"center\">" + strings[0] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[3] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[4] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[1] + "</td>");
		sb.append("<td colspan=\"2\" align=\"center\">" + strings[2] + "</td>");
		
		String countMatch = "No";
		if(strings[1] .equals(strings[2])) {
			countMatch = "Yes";
		}
		sb.append("<td colspan=\"2\" align=\"center\">" + countMatch + "</td>");
		sb.append("</tr></tbody>");
		return sb;
	}

	@SuppressWarnings("deprecation")
	private static StringBuffer createFooter(StringBuffer sb, Object[] strings, String reportId) {
		sb.append("<br></br>");
		sb.append("<br></br>");
		sb.append("<br></br>");
		sb.append("<br></br>");
		if (reportId != null)
			sb.append("<p>Job Reference Id : " + reportId + "</p>");
		sb.append("<p>Generated with Archon</p>");
		sb.append("<p>Generated Date : " + new Date() + "</p>");
		sb.append("<p>Job Execution Time : " + strings[0] + "</p>");
		sb.append("<p><i><font size=\"8px\">This is a system generated report.</font></i></p>");
		sb.append("</body></html>");
		return sb;
	}

	private static String getTitle(boolean isSummaryReport) {
		if (isSummaryReport)
			return ("<center><h1><b>Data Extraction Summary</b></h1><br/></center>");
		else
			return ("<center><h1><b>Metadata Extraction Report</b></h1></center>");
	}
	
	private StringBuffer createBlobData(StringBuffer sb, int count) {
		sb.append("<tbody><tr>");
		sb.append("<td colspan=\"2\" align=\"center\">" + count + "</td>");
		sb.append("<td colspan=\"3\" align=\"center\">" +propBean.getTableName() + "</td>");
		sb.append("<td colspan=\"3\" align=\"center\">" + CommonSharedConstants.srcBlobCount + "</td>");
		sb.append("<td colspan=\"3\" align=\"center\">" + CommonSharedConstants.desBlobCount + "</td>");
		sb.append("</tr></tbody>");
		return sb;
	}

	private StringBuffer createBlobTable(StringBuffer sb, Object[] strings, boolean b) {
		if (b) {
			sb.append("<br></br><table class=\"table table-bordered table-striped\"><thead><tr>");
			sb.append("<td colspan=\"2\" align=\"center\"><b>" + strings[0] + "</b></td>");
			sb.append("<td colspan=\"3\" align=\"center\"><b>" + strings[1] + "</b></td>");
			sb.append("<td colspan=\"3\" align=\"center\"><b>" + strings[2] + "</b></td>");
			sb.append("<td colspan=\"3\" align=\"center\"><b>" + strings[3] + "</b></td>");
			sb.append("</tr></thead>");
		} else {
			sb.append("</table>");
		}
		return sb;

	}

//	public static void main(String[] args) throws DocumentException, IOException {
//
//		String FONT_FILE_PATH = "/FreeSans.ttf";
//
//		String tempfile = "C:\\Archon\\Archon_Output\\superadmin\\Flat_File_Generator\\20210408\\Flat_File_Generator-1617771292670-408fb055-2463-4c33-a743-67518861a686_0_0\\Data_Extraction Report.pdf";
//
//		String url = new File(
//				"C:\\Archon\\Archon_Output\\superadmin\\Flat_File_Generator\\20210408\\Flat_File_Generator-1617771292670-408fb055-2463-4c33-a743-67518861a686_0_0\\Data_Extraction Report__out.html")
//						.toURI().toURL().toString();
//		OutputStream os = new FileOutputStream(new File(tempfile));
//		ITextRenderer renderer = new ITextRenderer();
////		renderer.getFontResolver().addFontDirectory("E:\\ttf", BaseFont.EMBEDDED);
//		addFontDirectory("E:\\ttf", BaseFont.EMBEDDED, renderer.getFontResolver());
//
//		renderer.setDocument(url);
//		renderer.layout();
//		renderer.createPDF(os);
//		os.close();
//
//	}
//
//	private static void addFontDirectory(String dir, boolean embedded, ITextFontResolver fontResolver)
//			throws DocumentException, IOException {
//
//		File f = new File(dir);
//		if (f.isDirectory()) {
//			File[] files = f.listFiles();
//			for (int i = 0; i < files.length; i++) {
//				fontResolver.addFont(files[i].getAbsolutePath(), BaseFont.IDENTITY_H, embedded);
//			}
//		}
//
//	}

	private String createReport(StringBuffer sb, String path, String reportId, String fileName) {
		String finalfile = null;
		
		try {
			String inputfile = path + File.separator + fileName + "_out.html";
			String tempfile = path + File.separator + fileName + "_temp.pdf";
			finalfile = path + File.separator + fileName 
					+ (reportId != null ? reportId.substring(0, 8) : UUID.randomUUID().toString().substring(0, 8))
					+ "_" + propBean.getDateTimeForReport()
					+ ".pdf";
			PrintWriter writer = new PrintWriter(inputfile, "UTF-8");
			writer.println(sb.toString());
			writer.close();
			String url = new File(inputfile).toURI().toURL().toString();
			OutputStream os = new FileOutputStream(new File(tempfile));
			ITextRenderer renderer = new ITextRenderer();
			renderer.getFontResolver().addFont(CommonSharedConstants.FONT_FILE_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			renderer.setDocument(url);
			renderer.layout();
			renderer.createPDF(os);
			os.close();
			FileInputStream is = new FileInputStream(tempfile);
			PdfReader pdfReader = new PdfReader(is);
			FileOutputStream fileOutputStream = new FileOutputStream(new File(finalfile));
			PdfStamper pdfStamper = new PdfStamper(pdfReader, fileOutputStream);
			Image image = Image.getInstance(getClass().getResource("/archon.png"));
			image.scaleToFit(300f, 300f);
			image.setAbsolutePosition(950f, 20f);
			for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
				PdfContentByte content = pdfStamper.getUnderContent(i);
				content.addImage(image);
			}
			pdfStamper.close();
			pdfReader.close();
			is.close();
			fileOutputStream.close();

//			FileUtils.forceDeleteOnExit(new File(inputfile));
//			FileUtils.forceDeleteOnExit(new File(tempfile));
			FileUtil.deleteFile(inputfile);
			FileUtil.deleteFile(tempfile);
			log.info("PDF report created at " + finalfile );
			CommonSharedConstants.logContent.append(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  PDF report created at " + finalfile + CommonSharedConstants.newLine );
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(finalfile != null) {
//				log.info("Final file :" + finalfile);
//				log.info("PDF : " + propBean.getPdfLocation());
				sftpUtils.uploadFile(finalfile, propBean.getPdfLocation());
			}
		}
		return finalfile;
	}

	public static String timeDiff(long diff) {
		int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
		String dateFormat = "";
		if (diffDays > 0) {
			dateFormat += diffDays + " day ";
		}
		diff -= diffDays * (24 * 60 * 60 * 1000);

		int diffhours = (int) (diff / (60 * 60 * 1000));
		if (diffhours > 0) {
			dateFormat += leftNumPadding(diffhours, 2) + " hour ";
		} else if (dateFormat.length() > 0) {
			dateFormat += "00 hour ";
		}
		diff -= diffhours * (60 * 60 * 1000);

		int diffmin = (int) (diff / (60 * 1000));
		if (diffmin > 0) {
			dateFormat += leftNumPadding(diffmin, 2) + " min ";
		} else if (dateFormat.length() > 0) {
			dateFormat += "00 min ";
		}

		diff -= diffmin * (60 * 1000);

		int diffsec = (int) (diff / (1000));
		if (diffsec > 0) {
			dateFormat += leftNumPadding(diffsec, 2) + " sec ";
		} else if (dateFormat.length() > 0) {
			dateFormat += "00 sec ";
		}

		int diffmsec = (int) (diff % (1000));
		dateFormat += leftNumPadding(diffmsec, 3) + " ms";
		return dateFormat;
	}

	private static String leftNumPadding(int str, int num) {
		return String.format("%0" + num + "d", str);
	}

	private void setReportID() {
			CommonSharedConstants.REPORT_ID = UUID.randomUUID().toString().substring(0, 8);
	}

	
}
