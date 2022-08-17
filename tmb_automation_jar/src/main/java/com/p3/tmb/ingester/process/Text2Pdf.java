package com.p3.tmb.ingester.process;

import com.itextpdf.text.*;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.p3.tmb.commonUtils.FileUtil;
import com.p3.tmb.constant.CommonSharedConstants;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;

public class Text2Pdf {

	private static final Rectangle Rectangle = new Rectangle(900, 1200);

	@SuppressWarnings("deprecation")
	public void createPdf(String yourreport, String TEXT, String appName, String reportId)
			throws DocumentException, IOException {
		float left = 30;
		float right = 30;
		float top = 30;
		float bottom = 30;

		Document document = new Document(Rectangle, left, right, top, bottom);
		String tempReport = yourreport + "temp";
		File dest = new File(tempReport);
		System.out.println(CommonSharedConstants.sdf3.format(new Timestamp(System.currentTimeMillis())) + "  "+ dest);
		dest.createNewFile(); // if file already exists will do nothing

		FileOutputStream oFile = new FileOutputStream(dest, false);
		PdfWriter.getInstance(document, oFile);
		document.open();
		BufferedReader br = new BufferedReader(new FileReader(TEXT));
		String line;
		Paragraph p;
		Font normal = new Font(FontFamily.COURIER, 10);
		Font verynormal = new Font(FontFamily.TIMES_ROMAN, 10);
		Font bold = new Font(FontFamily.COURIER, 10, Font.BOLD);
		Font boldTitle = new Font(FontFamily.COURIER, 14, Font.BOLD);
		boolean title = false;

		p = new Paragraph("Ingestion Report (" + appName + ")", boldTitle);
		p.setAlignment(Element.ALIGN_CENTER);
		document.add(p);

		p = new Paragraph("\n", title ? bold : normal);
		document.add(p);

		boolean flag = true;
		while ((line = br.readLine()) != null) {
			if (flag && !line.startsWith("Ingestion Report Process Log")) {
				continue;
			} else {
				flag = false;
			}
			if (line.startsWith("Ingestion Report Process Log")) {
				continue;
			}
			if (line.contains("Return value of process is ")) {
				break;
			}
			if (line.isEmpty()) {
				p = new Paragraph("\n", title ? bold : normal);
				title = line.isEmpty();
				document.add(p);
			} else {
				p = new Paragraph(line, title ? bold : normal);
				p.setAlignment(Element.ALIGN_LEFT);
				title = line.isEmpty();
				document.add(p);
			}
		}
		br.close();

		p = new Paragraph("\n", title ? bold : normal);
		document.add(p);
		document.add(p);
		document.add(p);

		p = new Paragraph("Job Reference Id : " + reportId, verynormal);
		document.add(p);

		p = new Paragraph("Generated Date : " + new Date().toGMTString(), verynormal);
		document.add(p);

		p = new Paragraph("This is a system generated report.", verynormal);
		document.add(p);

		document.close();

		PdfReader pdfReader = new PdfReader(tempReport);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(new File(yourreport)));
		Image image = Image.getInstance("archon.png");
		image.scaleToFit(300f, 300f);
		image.setAbsolutePosition(600f, 20f);
		for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {
			PdfContentByte content = pdfStamper.getUnderContent(i);
			content.addImage(image);
		}
		pdfStamper.close();
		pdfReader.close();

		FileUtil.deleteFile(tempReport);

	}

}
