package com.p3.splitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

import com.p3.splitter.utils.FileUtil;
import com.p3.splitter.utils.ParseXML;

public class SplitXMLFile {

	private static final String XML = ".xml";

	private String filepath;
	private String savelocation;
	private long size;
	private int rowpos;
	private int chunk;

	private String rootElement;
	private String tablenodeElement;
	private String rowElement;
	private String rootStartString;
	private String rootCloseString;
	private String tablenodeStartString;
	private String tablenodeCloseString;

	public SplitXMLFile(String file, String savelocation, Long size, int rowpos) throws Exception {
		this.filepath = file;
		this.savelocation = savelocation;
		this.size = size * 1024 * 1024;
		this.rowpos = rowpos;
		this.chunk = 0;
		new ParseXML().ParseXMLFile(file);
		System.out.println("XML file parsed successfully. File is valid.");
	}

	@SuppressWarnings("unchecked")
	public void startSplit() throws XMLStreamException, Exception {
		assignElements();
		boolean flag = true;
		String XMLFILE = null;
		File xmlfile = null;
		Writer out = null;

		String filename = FileUtil.getFileNameFromPath(filepath);
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader reader = factory.createXMLEventReader(new FileReader(filepath));
		StringBuffer sb = new StringBuffer();
		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				if (flag) {
					chunk++;
					XMLFILE = this.savelocation + File.separator + filename.substring(0, filename.indexOf(XML))
							+ "_chunk_" + (chunk) + XML;
					System.out.println("Saving file " + filename + " chunk " + chunk + " : " + XMLFILE);
					out = new OutputStreamWriter(new FileOutputStream(XMLFILE, true), "UTF-8");
					xmlfile = new File(XMLFILE);
					flag = false;
					out.write(rootStartString);
					if (rowpos == 2)
						out.write(tablenodeStartString);
				}
				String eleName = event.asStartElement().getName().getLocalPart();
				Iterator<Attribute> attributes = event.asStartElement().getAttributes();
				sb = new StringBuffer("");
				while (attributes.hasNext()) {
					Attribute x = attributes.next();
					sb.append(" ").append(x.getName()).append("=\"").append(x.getValue()).append("\"");
				}
				if (rowpos == 2) {
					if (!eleName.equals(rootElement) && !eleName.equals(tablenodeElement))
						out.write("<" + eleName + sb.toString() + ">");
				} else {
					if (!eleName.equals(rootElement))
						out.write("<" + eleName + sb.toString() + ">");
				}
				break;
			case XMLStreamConstants.CHARACTERS:
				Characters characters = event.asCharacters();
				switch (characters.toString()) {
				case "&":
					out.write("&amp;");
					break;
				case ">":
					out.write("&gt;");
					break;
				case "<":
					out.write("&lt;");
					break;
				case "'":
				case "\"":
					out.write("&quot;");
					break;
				default:
					out.write(characters.getData());
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				String endEleName = event.asEndElement().getName().getLocalPart();
				if (!endEleName.equals(rootElement))
					out.write("</" + endEleName + ">");
				if (rowpos == 2) {
					if (endEleName.equals(rowElement))
						if (xmlfile.length() > size) {
							out.write(tablenodeCloseString);
							out.write(rootCloseString);
							flag = true;
							out.flush();
						}
				} else {
					if (endEleName.equals(tablenodeElement))
						if (xmlfile.length() > size) {
							out.write(rootCloseString);
							flag = true;
							out.flush();
						}
				}
				break;
			}
		}
		if (!flag)
			out.write(rootCloseString);
		out.flush();
		out.close();
	}

	@SuppressWarnings("unchecked")
	private void assignElements() throws Exception, XMLStreamException {
		String root = null;
		String table = null;
		String row = null;
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader reader = factory.createXMLEventReader(new FileReader(filepath));
		StringBuffer sb = new StringBuffer();
		if (rowpos == 2) {
			while (reader.hasNext() && (row == null || table == null || root == null)) {
				XMLEvent event = reader.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (root != null && table != null && row == null) {
						row = event.asStartElement().getName().getLocalPart();
					}
					if (root != null && table == null) {
						table = event.asStartElement().getName().getLocalPart();
						Iterator<Attribute> attributes = event.asStartElement().getAttributes();
						sb = new StringBuffer("");
						while (attributes.hasNext()) {
							Attribute x = attributes.next();
							sb.append(" ").append(x.getName()).append("=\"").append(x.getValue()).append("\"");
						}
						tablenodeStartString = "<" + table + sb.toString() + ">";
						tablenodeCloseString = "</" + table + ">";
					}
					if (root == null) {
						root = event.asStartElement().getName().getLocalPart();
						Iterator<Attribute> attributes = event.asStartElement().getAttributes();
						sb = new StringBuffer("");
						while (attributes.hasNext()) {
							Attribute x = attributes.next();
							sb.append(" ").append(x.getName()).append("=\"").append(x.getValue()).append("\"");
						}
						rootStartString = "<" + root + sb.toString() + ">";
						rootCloseString = "</" + root + ">";
					}
					break;
				}
			}
			rootElement = root;
			tablenodeElement = table;
			rowElement = row;
		} else if (rowpos == 1) {
			while (reader.hasNext() && (table == null || root == null)) {
				XMLEvent event = reader.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					if (root != null && table == null) {
						table = event.asStartElement().getName().getLocalPart();
					}
					if (root == null) {
						root = event.asStartElement().getName().getLocalPart();
						Iterator<Attribute> attributes = event.asStartElement().getAttributes();
						sb = new StringBuffer("");
						while (attributes.hasNext()) {
							Attribute x = attributes.next();
							sb.append(" ").append(x.getName()).append("=\"").append(x.getValue()).append("\"");
						}
						rootStartString = "<" + root + sb.toString() + ">";
						rootCloseString = "</" + root + ">";
					}
					break;
				}
			}
			rootElement = root;
			tablenodeElement = table;
			rowElement = row;
		}
	}
}
