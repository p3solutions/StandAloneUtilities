package com.p3.archon.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xerces.util.XMLChar;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.emc.ia.sdk.sip.assembly.BatchSipAssembler;
import com.emc.ia.sdk.sip.assembly.DefaultPackagingInformationFactory;
import com.emc.ia.sdk.sip.assembly.OneSipPerDssPackagingInformationFactory;
import com.emc.ia.sdk.sip.assembly.PackagingInformation;
import com.emc.ia.sdk.sip.assembly.PackagingInformationFactory;
import com.emc.ia.sdk.sip.assembly.PdiAssembler;
import com.emc.ia.sdk.sip.assembly.SequentialDssIdSupplier;
import com.emc.ia.sdk.sip.assembly.SipAssembler;
import com.emc.ia.sdk.sip.assembly.SipSegmentationStrategy;
import com.emc.ia.sdk.sip.assembly.TemplatePdiAssembler;
import com.emc.ia.sdk.support.io.FileSupplier;
import com.emc.ia.sip.assembly.stringtemplate.StringTemplate;
import com.p3.archon.bean.ArchonInputBean;
import com.p3.archon.bean.FilesToDigitalObjects;
import com.p3.archon.bean.RecordData;
import com.p3.archon.utils.FileUtil;
import com.p3.archon.utils.JobLogger;
import com.p3.archon.utils.Validations;

public class SipPackager {

	private static final boolean GENERATE_ATTACHEMENTS = true;
	private static final String BLOB_PREFIX = "BLOBS_";
	private static final String DEFAULT_PRODUCER = "Archon";

	boolean version = true;
	String title;
	String schema;
	String outputPath;

	boolean hasblob = false;
	BatchSipAssembler<RecordData> batchAssembler;
	private ArchonInputBean ipargs;

	public SipPackager(String schema, String title, ArchonInputBean ipargs) {
		this.schema = schema;
		this.title = title;
		this.ipargs = ipargs;
		outputPath = ipargs.getInputPath().endsWith(File.separator) ? ipargs.getInputPath()
				: ipargs.getInputPath() + File.separator;
		this.outputPath = ipargs.getInputPath().endsWith(File.separator)
				? ipargs.getInputPath().substring(0, ipargs.getInputPath().length() - 1)
				: ipargs.getInputPath();
	}

	public String generateNameSpace(String holding) {
		return URI.create("urn:x-emc:ia:schema:" + holding.toLowerCase() + ":1.0").toString();
	}

	public void generateSip() throws Exception {
		String formattedSchema = getTextFormatted(schema);
		String formattedTable = getTextFormatted(title);
		String appName = ipargs.getApplicationName();
		String holding = ipargs.getHolding().replace(" ", "") + "_" + formattedSchema + "_" + formattedTable;
		String namespace = generateNameSpace(holding);

		PackagingInformation prototype = PackagingInformation.builder().dss().application(appName).holding(holding)
				.producer(DEFAULT_PRODUCER).entity(holding).schema(namespace).end().build();
		PackagingInformationFactory factory = new OneSipPerDssPackagingInformationFactory(
				new DefaultPackagingInformationFactory(prototype), new SequentialDssIdSupplier("ex6dss", 1));
		String sipHeader = "<" + formattedTable + " xmlns=\"" + namespace + "\">\n";
		String sipFooter = "\n" + "</" + formattedTable + ">\n";
		PdiAssembler<RecordData> pdiAssembler = new TemplatePdiAssembler<>(
				new StringTemplate<>(sipHeader, sipFooter, "$model.data$\n"));
		SipAssembler<RecordData> sipAssembler = SipAssembler.forPdiAndContent(factory, pdiAssembler,
				new FilesToDigitalObjects(outputPath));
		batchAssembler = new BatchSipAssembler<>(sipAssembler, SipSegmentationStrategy.byMaxSipSize(ipargs.getRpx()),
				FileSupplier.fromDirectory(new File(outputPath), "SIP-" + holding.toLowerCase() + "-", ".zip"));

		Set<String> fileNamesList = new TreeSet<String>();
		Files.newDirectoryStream(Paths.get(outputPath), path -> path.toString().startsWith(getFilterName()))
				.forEach(filePath -> fileNamesList.add(filePath.toString()));

		for (String file : fileNamesList) {
			covertXmlToSip(file, formattedSchema, formattedTable, version);
			batchAssembler.end();

			JobLogger.getLogger().info("SipGen", SipPackager.class.getName(), "generateSip", "Moving....  " + file);
			FileUtil.checkCreateDirectory(new File(file).getParent() + File.separator + "convertedXMLs");
			FileUtil.movefile(file, new File(file).getParent() + File.separator + "convertedXMLs", false);
			String blobFolder = outputPath + File.separator + "BLOBs" + File.separator
					+ Validations.checkValidFolder(title.toUpperCase());
			if (FileUtil.checkForDirectory(blobFolder)) {
				FileUtil.checkCreateDirectory(
						new File(file).getParent() + File.separator + "convertedXMLs" + File.separator + "BLOBs");
				FileUtil.movefile(blobFolder,
						new File(file).getParent() + File.separator + "convertedXMLs" + File.separator + "BLOBs",
						false);
			}
			JobLogger.getLogger().info("SipGen", SipPackager.class.getName(), "generateSip", "Moved....  " + file);
		}

	}

	protected String dataXmlCompitible(String data) {
		if (data == null)
			return "";
		return data.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;").replace("'", "&apos;").replace("\"",
				"&quot;");
	}

	protected String stripNonValidXMLCharacters(String in) {
		if (in == null || ("".equals(in)))
			return null;
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			if (XMLChar.isValid(c))
				out.append(c);
			else
				out.append("");
		}
		return out.toString();
	}

	private String writeXmlDocumentToXmlFile(Document xmlDocument) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;

		transformer = tf.newTransformer();

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(xmlDocument), new StreamResult(writer));
		String xmlString = writer.getBuffer().toString();
		return xmlString;
	}

	private static Document convertStringToXMLDocument(String xmlString)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
		return doc;

	}

	@SuppressWarnings("unchecked")
	private void covertXmlToSip(String fileItem, String schemaElement, String tableElement, boolean version)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		String ref = null;
		String type = null;
		String status = null;
		boolean cdataflagstart = false;
		boolean cdataflagend = false;
		Map<String, String> attachments = new TreeMap<String, String>();
		String row = version ? "ROW" : tableElement + "-ROW";
		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStream file = new FileInputStream(fileItem);
		InputStreamReader isr = new InputStreamReader(file);
		XMLEventReader eventReader = factory.createXMLEventReader(isr);
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			switch (event.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
				String startElement = event.asStartElement().getName().getLocalPart();
				if (startElement.equals(row)) {
					sb = new StringBuffer();
					attachments.clear();
					sb.append("<ROW");
				} else
					sb.append("<").append(startElement);

				ref = null;
				type = null;
				status = null;
				Iterator<Attribute> attributes = event.asStartElement().getAttributes();
				while (attributes.hasNext()) {
					Attribute x = attributes.next();
					if (x.getName().toString().equals("ref"))
						ref = x.getValue();
					else if (x.getName().toString().equals("status"))
						status = x.getValue();
					else if (x.getName().toString().equals("type"))
						type = x.getValue();

					if (type != null && type.equals("BLOB"))
						hasblob = true;

					if (x.getName().toString().equals("type") && x.getValue().equals("CLOB"))
						cdataflagstart = true;

					sb.append(" ").append(x.getName()).append("=\"").append(dataXmlCompitible(x.getValue()))
							.append("\"");
				}
				if (ref != null && type != null && status != null && !ref.equals("") && type.equals("BLOB")
						&& status.equalsIgnoreCase("SUCCESS"))
					attachments.put(startElement, ref);
				sb.append(">");
				break;
			case XMLStreamConstants.CHARACTERS:
				Characters characters = event.asCharacters();
				if (cdataflagstart) {
					sb.append("<![CDATA[");
					cdataflagstart = false;
					cdataflagend = true;
				}
				if (cdataflagstart || cdataflagend)
					sb.append(characters.getData());
				else
					sb.append(dataXmlCompitible(characters.getData()));
				break;
			case XMLStreamConstants.END_ELEMENT:
				String endElement = event.asEndElement().getName().getLocalPart();
				if (cdataflagend) {
					sb.append("]]>");
				}
				cdataflagstart = false;
				cdataflagend = false;
				if (endElement.equals(row)) {
					if (GENERATE_ATTACHEMENTS && hasblob) {
						sb.append("\n\t\t<").append(BLOB_PREFIX + tableElement + "_ATTACHEMENTS").append(">");
						for (Entry<String, String> blobitem : attachments.entrySet()) {
							sb.append("\n\t\t\t<").append("ATTACHEMENT column=\"").append(blobitem.getKey())
									.append("\">");
							sb.append(getAttachments(blobitem.getValue()));
							sb.append("</ATTACHEMENT>");
						}
						sb.append("\n\t\t</").append(BLOB_PREFIX + tableElement + "_ATTACHEMENTS").append(">");
					}
					sb.append("</ROW>");
					String txt = writeXmlDocumentToXmlFile(
							convertStringToXMLDocument(stripNonValidXMLCharacters(sb.toString())));
					RecordData rec = new RecordData(txt, new ArrayList<String>(attachments.values()));
					batchAssembler.add(rec);
				} else
					sb.append("</").append(endElement).append(">");
				break;
			}
		}
		eventReader.close();
		isr.close();
		file.close();
	}

	private String getAttachments(String item) {
		if (item == null || item.equals(""))
			return "";
		return item.substring(item.lastIndexOf("/") + 1);
	}

	public static String getTextFormatted(String string) {
		string = string.trim().replaceAll("[^_^\\p{Alnum}.]", "_").replace("^", "_").replaceAll("\\s+", "_");
		string = ((string.startsWith("_") && string.endsWith("_") && string.length() > 2)
				? string.substring(1).substring(0, string.length() - 2)
				: string);
		return string.length() > 0 ? ((string.charAt(0) >= '0' && string.charAt(0) <= '9') ? "_" : "") + string
				: string;
	}

	private String getFilterName() {
		return outputPath + File.separator + (version ? Validations.checkValidFile(schema.toUpperCase()) + "-" : "")
				+ Validations.checkValidFile(title.toUpperCase()) + "-";
	}
}
