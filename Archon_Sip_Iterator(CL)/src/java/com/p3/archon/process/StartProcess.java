package com.p3.archon.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.p3.archon.bean.ArchonInputBean;

public class StartProcess {
	private ArchonInputBean inputBean;
	private boolean TABLE_JDFS2HS81_ABBOOKADRS_flag = false;

	private boolean JDFS2HS81_F4301_ROW_flag = false;
	private boolean PHTRDJ_flag = false;
	StringBuffer PHTRDJ = new StringBuffer();
	private boolean PHCNDJ_flag = false;
	StringBuffer PHCNDJ = new StringBuffer();
	private boolean PHOTOT_flag = false;
	StringBuffer PHOTOT = new StringBuffer();
	private boolean PHUPMJ_flag = false;
	StringBuffer PHUPMJ = new StringBuffer();
	private boolean PHCRR_flag = false;
	StringBuffer PHCRR = new StringBuffer();

	private boolean JDFS2HS81_F4311_ROW_flag = false;
	private boolean PDTRDJ_flag = false;
	StringBuffer PDTRDJ = new StringBuffer();
	private boolean PDCNDJ_flag = false;
	StringBuffer PDCNDJ = new StringBuffer();
	private boolean PDDGL_flag = false;
	StringBuffer PDDGL = new StringBuffer();
	private boolean PDAOPN_flag = false;
	StringBuffer PDAOPN = new StringBuffer();
	private boolean PDAREC_flag = false;
	StringBuffer PDAREC = new StringBuffer();
	private boolean PDFAP_flag = false;
	StringBuffer PDFAP = new StringBuffer();
	private boolean PDFREC_flag = false;
	StringBuffer PDFREC = new StringBuffer();
	private boolean PDUPMJ_flag = false;
	StringBuffer PDUPMJ = new StringBuffer();
	private boolean PDCRR_flag = false;
	StringBuffer PDCRR = new StringBuffer();

	private boolean JDFS2HS81_F43121_ROW_flag = false;
	private boolean PRTRDJ_flag = false;
	StringBuffer PRTRDJ = new StringBuffer();
	private boolean PRRCDJ_flag = false;
	StringBuffer PRRCDJ = new StringBuffer();
	private boolean PRDGL_flag = false;
	StringBuffer PRDGL = new StringBuffer();
	private boolean PRAPTD_flag = false;
	StringBuffer PRAPTD = new StringBuffer();
	private boolean PRAOPN_flag = false;
	StringBuffer PRAOPN = new StringBuffer();
	private boolean PRAREC_flag = false;
	StringBuffer PRAREC = new StringBuffer();
	private boolean PRACLO_flag = false;
	StringBuffer PRACLO = new StringBuffer();
	private boolean PRFAP_flag = false;
	StringBuffer PRFAP = new StringBuffer();
	private boolean PRFAPT_flag = false;
	StringBuffer PRFAPT = new StringBuffer();
	private boolean PRFREC_flag = false;
	StringBuffer PRFREC = new StringBuffer();
	private boolean PRFCLO_flag = false;
	StringBuffer PRFCLO = new StringBuffer();
	private boolean PRUPMJ_flag = false;
	StringBuffer PRUPMJ = new StringBuffer();
	private boolean PRCRR_flag = false;
	StringBuffer PRCRR = new StringBuffer();

	private boolean JDFS2HS81_F43199_ROW_flag = false;
	private boolean OLTRDJ_flag = false;
	StringBuffer OLTRDJ = new StringBuffer();
	private boolean OLCNDJ_flag = false;
	StringBuffer OLCNDJ = new StringBuffer();
	private boolean OLDGL_flag = false;
	StringBuffer OLDGL = new StringBuffer();
	private boolean OLAOPN_flag = false;
	StringBuffer OLAOPN = new StringBuffer();
	private boolean OLAREC_flag = false;
	StringBuffer OLAREC = new StringBuffer();
	private boolean OLFAP_flag = false;
	StringBuffer OLFAP = new StringBuffer();
	private boolean OLFREC_flag = false;
	StringBuffer OLFREC = new StringBuffer();
	private boolean OLUPMJ_flag = false;
	StringBuffer OLUPMJ = new StringBuffer();
	private boolean OLCRR_flag = false;
	StringBuffer OLCRR = new StringBuffer();

	private boolean JDFS2HS81_F4209_ROW_flag = false;
	private boolean HOTRDJ_flag = false;
	StringBuffer HOTRDJ = new StringBuffer();
	private boolean HORDJ_flag = false;
	StringBuffer HORDJ = new StringBuffer();
	private boolean HOUPMJ_flag = false;
	StringBuffer HOUPMJ = new StringBuffer();

	public StartProcess(ArchonInputBean inputBean) {
		this.inputBean = inputBean;
	}

	public void start() {
		try {
			unzip(inputBean.getInput(), inputBean.getOutput());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		xmlReader(inputBean.getOutput());
	}

	private void xmlReader(String file) {
		Writer fileWriter = null;
		XMLEventReader eventReader = null;
		File metaBackup = null;
		try {
			File metaOut = new File(file + File.separator + "eas_pdi.xml");
			metaBackup = new File(file + File.separator + "eas_pdi_backup.xml");
			metaOut.renameTo(metaBackup);
			fileWriter = new FileWriter(metaOut);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			eventReader = factory.createXMLEventReader(new FileReader(metaBackup));
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					String startValue = startElement.getName().getLocalPart();
//					fileWriter.write("<" + startValue + ">");
					switch (startValue) {
					case "TABLE_JDFS2HS81.F4301":
						fileWriter.write("<" + startValue + " xmlns=\"urn:x-emc:ia:schema:jde_jdfs2hs81_f4301:1.0\">");
						break;
					case "JDFS2HS81.F4301_ROW":
						fileWriter.write("<" + startValue + ">");
						JDFS2HS81_F4301_ROW_flag = true;
						break;
					case "TABLE_JDFS2HS81.ABBOOKADRS":
						fileWriter.write(PHTRDJ.toString() + PHOTOT.toString() + PHCNDJ.toString() + PHUPMJ.toString()
								+ PHCRR.toString());
						PHTRDJ = new StringBuffer();
						PHOTOT = new StringBuffer();
						PHCNDJ = new StringBuffer();
						PHUPMJ = new StringBuffer();
						PHCRR = new StringBuffer();
						fileWriter.write("<" + startValue + ">");
						TABLE_JDFS2HS81_ABBOOKADRS_flag = true;
						break;
					case "PHTRDJ":
						fileWriter.write("<" + startValue + ">");
						PHTRDJ_flag = true;
						PHTRDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PHCNDJ":
						fileWriter.write("<" + startValue + ">");
						PHCNDJ_flag = true;
						PHCNDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PHOTOT":
						fileWriter.write("<" + startValue + ">");
						PHOTOT_flag = true;
						PHOTOT.append("<" + startValue + "_DE" + ">");
						break;
					case "PHUPMJ":
						fileWriter.write("<" + startValue + ">");
						PHUPMJ_flag = true;
						PHUPMJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PHCRR":
						fileWriter.write("<" + startValue + ">");
						PHCRR_flag = true;
						PHCRR.append("<" + startValue + "_MD" + ">");
						break;

					case "JDFS2HS81.F4311_ROW":
						fileWriter.write("<" + startValue + ">");
						JDFS2HS81_F4311_ROW_flag = true;
						break;
					case "PDTRDJ":
						fileWriter.write("<" + startValue + ">");
						PDTRDJ_flag = true;
						PDTRDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PDCNDJ":
						fileWriter.write("<" + startValue + ">");
						PDCNDJ_flag = true;
						PDCNDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PDDGL":
						fileWriter.write("<" + startValue + ">");
						PDDGL_flag = true;
						PDDGL.append("<" + startValue + "_DT" + ">");
						break;
					case "PDAOPN":
						fileWriter.write("<" + startValue + ">");
						PDAOPN_flag = true;
						PDAOPN.append("<" + startValue + "_DE" + ">");
						break;
					case "PDAREC":
						fileWriter.write("<" + startValue + ">");
						PDAREC_flag = true;
						PDAREC.append("<" + startValue + "_DE" + ">");
						break;
					case "PDFAP":
						fileWriter.write("<" + startValue + ">");
						PDFAP_flag = true;
						PDFAP.append("<" + startValue + "_DE" + ">");
						break;
					case "PDFREC":
						fileWriter.write("<" + startValue + ">");
						PDFREC_flag = true;
						PDFREC.append("<" + startValue + "_DE" + ">");
						break;
					case "PDUPMJ":
						fileWriter.write("<" + startValue + ">");
						PDUPMJ_flag = true;
						PDUPMJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PDCRR":
						fileWriter.write("<" + startValue + ">");
						PDCRR_flag = true;
						PDCRR.append("<" + startValue + "_MD" + ">");
						break;

					case "JDFS2HS81.F43121_ROW":
						fileWriter.write("<" + startValue + ">");
						JDFS2HS81_F43121_ROW_flag = true;
						break;
					case "PRTRDJ":
						fileWriter.write("<" + startValue + ">");
						PRTRDJ_flag = true;
						PRTRDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PRRCDJ":
						fileWriter.write("<" + startValue + ">");
						PRRCDJ_flag = true;
						PRRCDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PRDGL":
						fileWriter.write("<" + startValue + ">");
						PRDGL_flag = true;
						PRDGL.append("<" + startValue + "_DT" + ">");
						break;
					case "PRAPTD":
						fileWriter.write("<" + startValue + ">");
						PRAPTD_flag = true;
						PRAPTD.append("<" + startValue + "_DE" + ">");
						break;
					case "PRAOPN":
						fileWriter.write("<" + startValue + ">");
						PRAOPN_flag = true;
						PRAOPN.append("<" + startValue + "_DE" + ">");
						break;
					case "PRAREC":
						fileWriter.write("<" + startValue + ">");
						PRAREC_flag = true;
						PRAREC.append("<" + startValue + "_DE" + ">");
						break;
					case "PRACLO":
						fileWriter.write("<" + startValue + ">");
						PRACLO_flag = true;
						PRACLO.append("<" + startValue + "_DE" + ">");
						break;
					case "PRFAP":
						fileWriter.write("<" + startValue + ">");
						PRFAP_flag = true;
						PRFAP.append("<" + startValue + "_DE" + ">");
						break;
					case "PRFAPT":
						fileWriter.write("<" + startValue + ">");
						PRFAPT_flag = true;
						PRFAPT.append("<" + startValue + "_DE" + ">");
						break;
					case "PRFREC":
						fileWriter.write("<" + startValue + ">");
						PRFREC_flag = true;
						PRFREC.append("<" + startValue + "_DE" + ">");
						break;
					case "PRFCLO":
						fileWriter.write("<" + startValue + ">");
						PRFCLO_flag = true;
						PRFCLO.append("<" + startValue + "_DE" + ">");
						break;
					case "PRUPMJ":
						fileWriter.write("<" + startValue + ">");
						PRUPMJ_flag = true;
						PRUPMJ.append("<" + startValue + "_DT" + ">");
						break;
					case "PRCRR":
						fileWriter.write("<" + startValue + ">");
						PRCRR_flag = true;
						PRCRR.append("<" + startValue + "_MD" + ">");
						break;

					case "JDFS2HS81.F43199_ROW":
						fileWriter.write("<" + startValue + ">");
						JDFS2HS81_F43199_ROW_flag = true;
						break;
					case "OLTRDJ":
						fileWriter.write("<" + startValue + ">");
						OLTRDJ_flag = true;
						OLTRDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "OLCNDJ":
						fileWriter.write("<" + startValue + ">");
						OLCNDJ_flag = true;
						OLCNDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "OLDGL":
						fileWriter.write("<" + startValue + ">");
						OLDGL_flag = true;
						OLDGL.append("<" + startValue + "_DT" + ">");
						break;
					case "OLAOPN":
						fileWriter.write("<" + startValue + ">");
						OLAOPN_flag = true;
						OLAOPN.append("<" + startValue + "_DE" + ">");
						break;
					case "OLAREC":
						fileWriter.write("<" + startValue + ">");
						OLAREC_flag = true;
						OLAREC.append("<" + startValue + "_DE" + ">");
						break;
					case "OLFAP":
						fileWriter.write("<" + startValue + ">");
						OLFAP_flag = true;
						OLFAP.append("<" + startValue + "_DE" + ">");
						break;
					case "OLFREC":
						fileWriter.write("<" + startValue + ">");
						OLFREC_flag = true;
						OLFREC.append("<" + startValue + "_DE" + ">");
						break;
					case "OLUPMJ":
						fileWriter.write("<" + startValue + ">");
						OLUPMJ_flag = true;
						OLUPMJ.append("<" + startValue + "_DT" + ">");
						break;
					case "OLCRR":
						fileWriter.write("<" + startValue + ">");
						OLCRR_flag = true;
						OLCRR.append("<" + startValue + "_MD" + ">");
						break;

					case "JDFS2HS81.F4209_ROW":
						fileWriter.write("<" + startValue + ">");
						JDFS2HS81_F4209_ROW_flag = true;
						break;
					case "HOTRDJ":
						fileWriter.write("<" + startValue + ">");
						HOTRDJ_flag = true;
						HOTRDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "HORDJ":
						fileWriter.write("<" + startValue + ">");
						HORDJ_flag = true;
						HORDJ.append("<" + startValue + "_DT" + ">");
						break;
					case "HOUPMJ":
						fileWriter.write("<" + startValue + ">");
						HOUPMJ_flag = true;
						HOUPMJ.append("<" + startValue + "_DT" + ">");
						break;
					default:
						fileWriter.write("<" + startValue + ">");

					}
					break;
				case XMLStreamConstants.CHARACTERS:
					Characters characters = event.asCharacters();
					if (PHTRDJ_flag) {
						julianDateConversion(characters.getData(), PHTRDJ);
					}
					if (PHCNDJ_flag) {
						julianDateConversion(characters.getData(), PHCNDJ);
					}
					if (PHOTOT_flag) {
						deConversion(characters.getData(), PHOTOT);
					}
					if (PHUPMJ_flag) {
						julianDateConversion(characters.getData(), PHUPMJ);
					}
					if (PHCRR_flag) {
						mdConversion(characters.getData(), PHCRR);
					}
					if (PDTRDJ_flag) {
						julianDateConversion(characters.getData(), PDTRDJ);
					}

					if (PDCNDJ_flag) {
						julianDateConversion(characters.getData(), PDCNDJ);
					}
					if (PDDGL_flag) {
						julianDateConversion(characters.getData(), PDDGL);
					}
					if (PDAOPN_flag) {
						deConversion(characters.getData(), PDAOPN);
					}
					if (PDAREC_flag) {
						deConversion(characters.getData(), PDAREC);
					}
					if (PDFAP_flag) {
						deConversion(characters.getData(), PDFAP);
					}
					if (PDFREC_flag) {
						deConversion(characters.getData(), PDFREC);
					}
					if (PDUPMJ_flag) {
						julianDateConversion(characters.getData(), PDUPMJ);
					}
					if (PDCRR_flag) {
						mdConversion(characters.getData(), PDCRR);
					}

					if (PRTRDJ_flag) {
						julianDateConversion(characters.getData(), PRTRDJ);
					}
					if (PRRCDJ_flag) {
						julianDateConversion(characters.getData(), PRRCDJ);
					}
					if (PRDGL_flag) {
						julianDateConversion(characters.getData(), PRDGL);
					}
					if (PRAPTD_flag) {
						deConversion(characters.getData(), PRAPTD);
					}
					if (PRAOPN_flag) {
						deConversion(characters.getData(), PRAOPN);
					}
					if (PRAREC_flag) {
						deConversion(characters.getData(), PRAREC);
					}
					if (PRACLO_flag) {
						deConversion(characters.getData(), PRACLO);
					}
					if (PRFAP_flag) {
						deConversion(characters.getData(), PRFAP);
					}
					if (PRFAPT_flag) {
						deConversion(characters.getData(), PRFAPT);
					}
					if (PRFREC_flag) {
						deConversion(characters.getData(), PRFREC);
					}
					if (PRFCLO_flag) {
						deConversion(characters.getData(), PRFCLO);
					}
					if (PRUPMJ_flag) {
						julianDateConversion(characters.getData(), PRUPMJ);
					}
					if (PRCRR_flag) {
						mdConversion(characters.getData(), PRCRR);
					}

					if (OLTRDJ_flag) {
						julianDateConversion(characters.getData(), OLTRDJ);
					}
					if (OLCNDJ_flag) {
						julianDateConversion(characters.getData(), OLCNDJ);
					}
					if (OLDGL_flag) {
						julianDateConversion(characters.getData(), OLDGL);
					}
					if (OLAOPN_flag) {
						deConversion(characters.getData(), OLAOPN);
					}
					if (OLAREC_flag) {
						deConversion(characters.getData(), OLAREC);
					}
					if (OLFAP_flag) {
						deConversion(characters.getData(), OLFAP);
					}
					if (OLFREC_flag) {
						deConversion(characters.getData(), OLFREC);
					}
					if (OLUPMJ_flag) {
						julianDateConversion(characters.getData(), OLUPMJ);
					}
					if (OLCRR_flag) {
						mdConversion(characters.getData(), OLCRR);
					}

					if (HOTRDJ_flag) {
						julianDateConversion(characters.getData(), HOTRDJ);
					}
					if (HORDJ_flag) {
						julianDateConversion(characters.getData(), HORDJ);
					}
					if (HOUPMJ_flag) {
						julianDateConversion(characters.getData(), HOUPMJ);
					}
					fileWriter.write(getXmlValidChar(characters.getData()));
					break;
				case XMLStreamConstants.END_ELEMENT:
					EndElement endElement = event.asEndElement();
					String endValue = endElement.getName().getLocalPart();
					switch (endValue) {

					case "JDFS2HS81.F4301_ROW":
						fileWriter.write("</" + endValue + ">");
						JDFS2HS81_F4301_ROW_flag = false;
						break;
					case "PHTRDJ":
						fileWriter.write("</" + endValue + ">");
						if (PHTRDJ.length() > 0)
							PHTRDJ.append("</" + endValue + "_DT" + ">\n");
						PHTRDJ_flag = false;
						break;
					case "PHCNDJ":
						fileWriter.write("</" + endValue + ">");
						if (PHCNDJ.length() > 0)
							PHCNDJ.append("</" + endValue + "_DT" + ">\n");
						PHCNDJ_flag = false;
						break;
					case "PHOTOT":
						fileWriter.write("</" + endValue + ">");
						if (PHOTOT.length() > 0)
							PHOTOT.append("</" + endValue + "_DE" + ">\n");
						PHOTOT_flag = false;
						break;
					case "PHUPMJ":
						fileWriter.write("</" + endValue + ">");
						if (PHUPMJ.length() > 0)
							PHUPMJ.append("</" + endValue + "_DT" + ">\n");
						PHUPMJ_flag = false;
						break;
					case "PHCRR":
						fileWriter.write("</" + endValue + ">");
						if (PHCRR.length() > 0)
							PHCRR.append("</" + endValue + "_MD" + ">\n");
						PHCRR_flag = false;
						break;

					case "JDFS2HS81.F4311_ROW":
						fileWriter.write(PDDGL.toString() + PDTRDJ.toString() + PDCNDJ.toString() + PDUPMJ.toString()
								+ PDAOPN.toString() + PDAREC.toString() + PDFAP.toString() + PDFREC.toString()
								+ PDCRR.toString());
						PDDGL = new StringBuffer();
						PDTRDJ = new StringBuffer();
						PDCNDJ = new StringBuffer();
						PDUPMJ = new StringBuffer();
						PDAOPN = new StringBuffer();
						PDAREC = new StringBuffer();
						PDFAP = new StringBuffer();
						PDFREC = new StringBuffer();
						PDCRR = new StringBuffer();
						fileWriter.write("</" + endValue + ">");
						JDFS2HS81_F4311_ROW_flag = false;
						break;

					case "PDTRDJ":
						fileWriter.write("</" + endValue + ">");
						PDTRDJ_flag = false;
						if (PDTRDJ.length() > 0)
							PDTRDJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "PDCNDJ":
						fileWriter.write("</" + endValue + ">");
						PDCNDJ_flag = false;
						if (PDCNDJ.length() > 0)
							PDCNDJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "PDDGL":
						fileWriter.write("</" + endValue + ">");
						PDDGL_flag = false;
						if (PDDGL.length() > 0)
							PDDGL.append("</" + endValue + "_DT" + ">\n");
						break;
					case "PDAOPN":
						fileWriter.write("</" + endValue + ">");
						PDAOPN_flag = false;
						if (PDAOPN.length() > 0)
							PDAOPN.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PDAREC":
						fileWriter.write("</" + endValue + ">");
						PDAREC_flag = false;
						if (PDAREC.length() > 0)
							PDAREC.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PDFAP":
						fileWriter.write("</" + endValue + ">");
						PDFAP_flag = false;
						if (PDFAP.length() > 0)
							PDFAP.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PDFREC":
						fileWriter.write("</" + endValue + ">");
						PDFREC_flag = false;
						if (PDFREC.length() > 0)
							PDFREC.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PDUPMJ":
						fileWriter.write("</" + endValue + ">");
						PDUPMJ_flag = false;
						if (PDUPMJ.length() > 0)
							PDUPMJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "PDCRR":
						fileWriter.write("</" + endValue + ">");
						PDCRR_flag = false;
						if (PDCRR.length() > 0)
							PDCRR.append("</" + endValue + "_MD" + ">\n");
						break;

					case "JDFS2HS81.F43121_ROW":
						fileWriter.write(PRDGL.toString() + PRTRDJ.toString() + PRRCDJ.toString() + PRAOPN.toString()
								+ PRAREC.toString() + PRACLO.toString() + PRFAP.toString() + PRFREC.toString()
								+ PRFCLO.toString() + PRAPTD.toString() + PRFAPT.toString() + PRUPMJ.toString()
								+ PRCRR.toString());
						PRDGL = new StringBuffer();
						PRTRDJ = new StringBuffer();
						PRRCDJ = new StringBuffer();
						PRAOPN = new StringBuffer();
						PRAREC = new StringBuffer();
						PRACLO = new StringBuffer();
						PRFAP = new StringBuffer();
						PRFREC = new StringBuffer();
						PRFCLO = new StringBuffer();
						PRAPTD = new StringBuffer();
						PRFAPT = new StringBuffer();
						PRUPMJ = new StringBuffer();
						PRCRR = new StringBuffer();
						fileWriter.write("</" + endValue + ">");
						JDFS2HS81_F43121_ROW_flag = false;
						break;
					case "PRTRDJ":
						fileWriter.write("</" + endValue + ">");
						PRTRDJ_flag = false;
						if (PRTRDJ.length() > 0)
							PRTRDJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "PRRCDJ":
						fileWriter.write("</" + endValue + ">");
						PRRCDJ_flag = false;
						if (PRRCDJ.length() > 0)
							PRRCDJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "PRDGL":
						fileWriter.write("</" + endValue + ">");
						PRDGL_flag = false;
						if (PRDGL.length() > 0)
							PRDGL.append("</" + endValue + "_DT" + ">\n");
						break;
					case "PRAPTD":
						fileWriter.write("</" + endValue + ">");
						PRAPTD_flag = false;
						if (PRAPTD.length() > 0)
							PRAPTD.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PRAOPN":
						fileWriter.write("</" + endValue + ">");
						PRAOPN_flag = false;
						if (PRAOPN.length() > 0)
							PRAOPN.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PRAREC":
						fileWriter.write("</" + endValue + ">");
						PRAREC_flag = false;
						if (PRAREC.length() > 0)
							PRAREC.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PRACLO":
						fileWriter.write("</" + endValue + ">");
						PRACLO_flag = false;
						if (PRACLO.length() > 0)
							PRACLO.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PRFAP":
						fileWriter.write("</" + endValue + ">");
						PRFAP_flag = false;
						if (PRFAP.length() > 0)
							PRFAP.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PRFAPT":
						fileWriter.write("</" + endValue + ">");
						PRFAPT_flag = false;
						if (PRFAPT.length() > 0)
							PRFAPT.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PRFREC":
						fileWriter.write("</" + endValue + ">");
						PRFREC_flag = false;
						if (PRFREC.length() > 0)
							PRFREC.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PRFCLO":
						fileWriter.write("</" + endValue + ">");
						PRFCLO_flag = false;
						if (PRFCLO.length() > 0)
							PRFCLO.append("</" + endValue + "_DE" + ">\n");
						break;
					case "PRUPMJ":
						fileWriter.write("</" + endValue + ">");
						PRUPMJ_flag = false;
						if (PRUPMJ.length() > 0)
							PRUPMJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "PRCRR":
						fileWriter.write("</" + endValue + ">");
						PRCRR_flag = false;
						if (PRCRR.length() > 0)
							PRCRR.append("</" + endValue + "_MD" + ">\n");
						break;

					case "JDFS2HS81.F43199_ROW":
						fileWriter.write(OLTRDJ.toString() + OLCNDJ.toString() + OLDGL.toString() + OLUPMJ.toString()
								+ OLAOPN.toString() + OLAREC.toString() + OLFAP.toString() + OLFREC.toString()
								+ OLCRR.toString());
						OLTRDJ = new StringBuffer();
						OLCNDJ = new StringBuffer();
						OLDGL = new StringBuffer();
						OLUPMJ = new StringBuffer();
						OLAOPN = new StringBuffer();
						OLAREC = new StringBuffer();
						OLFAP = new StringBuffer();
						OLFREC = new StringBuffer();
						OLCRR = new StringBuffer();
						fileWriter.write("</" + endValue + ">");
						JDFS2HS81_F43199_ROW_flag = false;
						break;
					case "OLTRDJ":
						fileWriter.write("</" + endValue + ">");
						OLTRDJ_flag = false;
						if (OLTRDJ.length() > 0)
							OLTRDJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "OLCNDJ":
						fileWriter.write("</" + endValue + ">");
						OLCNDJ_flag = false;
						if (OLCNDJ.length() > 0)
							OLCNDJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "OLDGL":
						fileWriter.write("</" + endValue + ">");
						OLDGL_flag = false;
						if (OLDGL.length() > 0)
							OLDGL.append("</" + endValue + "_DT" + ">\n");
						break;
					case "OLAOPN":
						fileWriter.write("</" + endValue + ">");
						OLAOPN_flag = false;
						if (OLAOPN.length() > 0)
							OLAOPN.append("</" + endValue + "_DE" + ">\n");
						break;
					case "OLAREC":
						fileWriter.write("</" + endValue + ">");
						OLAREC_flag = false;
						if (OLAREC.length() > 0)
							OLAREC.append("</" + endValue + "_DE" + ">\n");
						break;
					case "OLFAP":
						fileWriter.write("</" + endValue + ">");
						OLFAP_flag = false;
						if (OLFAP.length() > 0)
							OLFAP.append("</" + endValue + "_DE" + ">\n");
						break;
					case "OLFREC":
						fileWriter.write("</" + endValue + ">");
						OLFREC_flag = false;
						if (OLFREC.length() > 0)
							OLFREC.append("</" + endValue + "_DE" + ">\n");
						break;
					case "OLUPMJ":
						fileWriter.write("</" + endValue + ">");
						OLUPMJ_flag = false;
						if (OLUPMJ.length() > 0)
							OLUPMJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "OLCRR":
						fileWriter.write("</" + endValue + ">");
						OLCRR_flag = false;
						if (OLCRR.length() > 0)
							OLCRR.append("</" + endValue + "_MD" + ">\n");
						break;

					case "JDFS2HS81.F4209_ROW":
						fileWriter.write(HORDJ.toString() + HOTRDJ.toString() + HOUPMJ.toString());
						HORDJ = new StringBuffer();
						HOTRDJ = new StringBuffer();
						HOUPMJ = new StringBuffer();
						fileWriter.write("</" + endValue + ">");
						JDFS2HS81_F4209_ROW_flag = false;
						break;
					case "HOTRDJ":
						fileWriter.write("</" + endValue + ">");
						HOTRDJ_flag = false;
						if (HOTRDJ.length() > 0)
							HOTRDJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "HORDJ":
						fileWriter.write("</" + endValue + ">");
						HORDJ_flag = false;
						if (HORDJ.length() > 0)
							HORDJ.append("</" + endValue + "_DT" + ">\n");
						break;
					case "HOUPMJ":
						fileWriter.write("</" + endValue + ">");
						HOUPMJ_flag = false;
						if (HOUPMJ.length() > 0)
							HOUPMJ.append("</" + endValue + "_DT" + ">\n");
						break;

					default:
						fileWriter.write("</" + endValue + ">");
					}
					break;
				default:
					break;
				}
			}
		}

		/*
		 * XMLOutputter xmlOutput = new XMLOutputter();
		 * xmlOutput.setFormat(Format.getPrettyFormat()); xmlOutput.output(System.out);
		 */

		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				eventReader.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
			metaBackup.delete();
		}

	}

	public void unzip(String zipFilePath, String destDir) throws InterruptedException {
		File dir = new File(destDir);
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
//				if (getFileExtension(newFile.toString()) == null) {
//					newFile.mkdirs();
//					ze = zis.getNextEntry();
//					continue;
//				}
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				String newFileName = newFile.getPath().substring(newFile.getPath().lastIndexOf(File.separator),
						newFile.getPath().length());
				if (!newFileName.contains(".") && (!(newFile.exists() || !newFile.isDirectory()))) {
					newFile.mkdirs();
					continue;
				} else {
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void julianDateConversion(String filename, StringBuffer sb) {
		if (filename.toString().trim() == null || Integer.valueOf(filename.toString().trim()) == 0
				|| filename.toString().trim().isEmpty()) {
			sb.delete(0, sb.length());
		} else {
			Date date = null;
			try {
				date = new SimpleDateFormat("Myydd").parse(filename.toString().trim());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append(new SimpleDateFormat("yyyy-MM-dd").format(date));
		}
	}

	private void deConversion(String filename, StringBuffer sb) {
		if (filename.toString().trim() == null || filename.toString().trim().isEmpty()) {
			sb.delete(0, sb.length());
		} else if (Long.valueOf(filename.toString().trim()) == 0) {
			sb.append("0.00");
		} else {
			Object decimalValue = Long.valueOf(filename.toString().trim()) / 100.0;
			NumberFormat myFormat = NumberFormat.getInstance();
			myFormat.setGroupingUsed(true);
			String formatedValue = String.format("%.2f", decimalValue);
			String[] splitedValues = formatedValue.split("\\.");
			sb.append(myFormat.format(Double.valueOf(splitedValues[0])) + "." + splitedValues[1]);
		}

	}

	private void mdConversion(String filename, StringBuffer sb) {
		if (filename.toString().trim() == null || filename.toString().trim().isEmpty()) {
			sb.delete(0, sb.length());
		} else if (filename.equalsIgnoreCase("0E-7")) {
			sb.delete(0, sb.length());
		} else {
			sb.append(filename);
		}
	}

	protected String getXmlValidChar(String data) {
		if (data == null)
			return null;
		return data.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
	}

//	public static void main(String[] args) {
//		deConversion("5376960");
//	}
//
//	private static void deConversion(String filename) {
//		Object decimalValue = Long.valueOf(filename.toString().trim()) / 100.0;
//		NumberFormat myFormat = NumberFormat.getInstance();
//		myFormat.setGroupingUsed(true);
//		String formatedValue = String.format("%.2f", decimalValue);
//		String[] splitedValues = formatedValue.split("\\.");
//		System.out.println(myFormat.format(Double.valueOf(splitedValues[0])) + "." + splitedValues[1]);
//	}
}
